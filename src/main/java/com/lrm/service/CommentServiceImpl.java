package com.lrm.service;

import com.lrm.Exception.NotFoundException;
import com.lrm.dao.CommentRepository;
import com.lrm.po.Comment;
import com.lrm.po.Question;
import com.lrm.po.User;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 山水夜止
 */
@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private QuestionService questionService;

    /**
     * 存放迭代找出的所有子代的集合
     */
    private List<Comment> tempReplys = new ArrayList<>();

    /**
     * 保存评论 如果不是通过回复的方式 那么前端传回parentCommentId默认设置为1
     *
     * @param comment    前端封装好了的Comment对象
     * @param questionId 问题Id
     * @param postUser   发布评论的人
     */
    @Transactional
    @Override
    public Comment saveComment(Comment comment, Long questionId, User postUser) throws NotFoundException {
        //得到前端封装返回的对象的parentId
        Long parentCommentId = comment.getParentCommentId0();

        //得到当前评论对应的问题
        Question question = questionService.getQuestion(questionId);

        //有父评论 则获得通知的人是父评论的发出者 否则为问题的发出者
        if (parentCommentId != -1) {

            Comment parentComment = getComment(parentCommentId);
            if (parentComment == null) {
                throw new NotFoundException("该父评论不存在");
            }

            //父问题评论数增加
            parentComment.setCommentsNum(parentComment.getCommentsNum() + 1);

            //初始化
            comment.setParentComment(parentComment);
            comment.setReceiveUser(parentComment.getPostUser());
            //父评论是哪种 子评论就是哪种
            comment.setAnswer(parentComment.getAnswer());

        } else {
            //在第一行comment.getParentComment中实际上new了一个parentComment对象(初始化id为-1了) 但id不能为-1 没有将p...持久化所以会报错 要设成null
            comment.setParentComment(null);
            comment.setReceiveUser(questionService.getQuestion(questionId).getUser());
        }

        //初始化
        //所属问题评论数增加 包含评论下的子评论了
        question.setCommentsNum(question.getCommentsNum() + 1);
        comment.setQuestion(question);
        comment.setLooked(false);
        comment.setCreateTime(new Date());
        comment.setLikesNum(0);
        comment.setDisLikesNum(0);
        comment.setCommentsNum(0);
        comment.setHidden(false);
        comment.setPostUserId0(postUser.getId());
        comment.setPostUser(postUser);

        //判断发出评论的人是否是问题所有者
        boolean admin = (postUser == question.getUser());

        //如果是 初始化这两项
        comment.setAdminComment(admin);
        comment.setLooked(admin);

        //如果是有效回答 回答者贡献+3 问题影响力+2 否则仅仅问题影响力+2
        if (comment.getAnswer()) {
            postUser.setDonation(postUser.getDonation() + 4);
        }

        question.setImpact(question.getImpact() + 2);

        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {

        //复原
        Comment comment = getComment(commentId);
        //父评论
        Comment parentComment = comment.getParentComment();
        //评论发出者
        User postUser = comment.getPostUser();
        //评论所在问题
        Question question = comment.getQuestion();

        if (parentComment != null) {
            parentComment.setCommentsNum(parentComment.getCommentsNum() - 1);
        }

        question.setCommentsNum(question.getCommentsNum() - 1);

        if (comment.getAnswer()) {
            postUser.setDonation(postUser.getDonation() - 4);
        }
        question.setImpact(question.getImpact() - 2);

        commentRepository.deleteById(commentId);
    }

    @Override
    public Comment getComment(Long commentId) {
        Optional<Comment> comment = commentRepository.findById(commentId);
        return comment.orElse(null);
    }

    /**
     * 得到问题下分级评论（两级）
     *
     * @param isAnswer 是哪一类评论
     * @return comment集合
     */
    @Override
    public List<Comment> listCommentByQuestionId(Long questionId, Boolean isAnswer) {
        Sort sort = new Sort(Sort.Direction.ASC, "createTime");

        //得到所有第一级评论
        List<Comment> comments = commentRepository.findByQuestionIdAndAnswer(questionId, isAnswer, sort);

        //遍历第一级
        return eachComment(comments);
    }

    /**
     * 遍历所有第一级评论
     *
     * @param comments 第一级评论集合
     * @return 返回评论区的新的评论集合
     */
    private List<Comment> eachComment(List<Comment> comments) {
        //将所有第一级评论保存到commentsView里
        List<Comment> commentsView = new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            BeanUtils.copyProperties(comment, c);
            commentsView.add(c);
        }

        //合并评论的各层子代到第一级子代集合中
        combineChildren(commentsView);
        return commentsView;
    }

    private void combineChildren(List<Comment> comments) {
        //遍历所有第一级评论
        for (Comment comment : comments) {

            //得到回复第一级评论的第二级评论
            List<Comment> replys1 = comment.getReplyComments();

            //遍历第二级评论
            for (Comment reply1 : replys1) {

                //循环迭代，找出子代，存放在tempReplys中
                recursively(reply1);
            }

            //修改顶级节点的reply集合为迭代处理后的集合
            comment.setReceiveComments(tempReplys);

            //清除临时存放区
            tempReplys = new ArrayList<>();
        }
    }

    private void recursively(Comment comment) {
        //第二级评论添加到临时存放集合
        tempReplys.add(comment);

        //如果第二级评论有子评论
        if (comment.getReplyComments().size() > 0) {
            List<Comment> replys = comment.getReplyComments();

            //遍历第三级评论 添加到临时存放集合
            for (Comment reply : replys) {
                    recursively(reply);
            }
        }
    }

    /**
     * 得到某问题下的所有评论 计算赞数
     *
     * @param questionId 问题Id
     * @return 未读评论集合
     */
    @Override
    public List<Comment> listAllCommentByQuestionId(Long questionId) {
        return commentRepository.findByQuestionId(questionId);
    }

    /**
     * 获得未读评论通知
     *
     * @param userId 当前用户Id
     * @return 未读评论集合
     */
    @Override
    public List<Comment> listAllNotReadComment(Long userId) {
        return commentRepository.findByReceiveUserIdAndLooked(userId, false);
    }

}
