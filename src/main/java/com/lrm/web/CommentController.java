package com.lrm.web;

import com.lrm.Exception.NoPermissionException;
import com.lrm.Exception.NotFoundException;
import com.lrm.po.*;
import com.lrm.service.*;
import com.lrm.util.GetTokenInfo;
import com.lrm.vo.Magic;
import com.lrm.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author 山水夜止.
 */
@RestController
@RequestMapping("question/{questionId}")
public class CommentController
{
    @Autowired
    private CommentService commentService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private LikesService likesService;

    @Autowired
    private DisLikesService disLikesService;

    /**
     * 展示所有评论
     *
     * @param questionId 评论在哪个问题下 对应的问题Id
     * @param request    用于得到当前userId 处理当前用户点没点过赞的
     * @return 第一类评论+对应在线用户是否点过赞、第二类评论+对应在线用户是否点过赞
     */
    @GetMapping("/comments")
    public Result<Map<String, Object>> comments(@PathVariable Long questionId, HttpServletRequest request) {
        Map<String, Object> hashMap = new HashMap<>(4);

        Long userId = GetTokenInfo.getCustomUserId(request);

        //分别返回两类评论和对应点赞
        List<Comment> comments1 = commentService.listCommentByQuestionId(questionId, false);

        hashMap.put("comments1", dealComment(comments1, userId));

        List<Comment> comments2 = commentService.listCommentByQuestionId(questionId, true);

        hashMap.put("comments2", dealComment(comments2, userId));

        return new Result<>(hashMap, true, "");
    }

    /**
     * @param comments 被处理的comment集合 我更改的只是comment的receiveComment[]属性
     *                 并没有更改他们的父级评论属性 所以仍然可以根据他们的parentComment获取nickname
     * @param userId   当前用户对象 用于处理是否点过赞的
     * @return 给前端的comment集合
     */
    List<Comment> dealComment(List<Comment> comments, Long userId) {
        if (comments.size() != 0) {
            for (Comment comment : comments) {
                insertAttribute(comment, userId);
                List<Comment> receiveComments = comment.getReceiveComments();
                if (receiveComments.size() != 0) {
                    for (Comment receiveComment : receiveComments) {
                        receiveComment.setParentCommentName(receiveComment.getParentComment().getPostUser().getNickname());
                        insertAttribute(receiveComment, userId);
                    }
                }
            }
        }
        return comments;
    }

    /**
     * 配合dealComment插入数据
     *
     * @param comment 被插入的评论对象
     * @param userId  当前用户对象 用于处理是否点过赞的
     */
    void insertAttribute(Comment comment, Long userId) {
        //得到发布问题的人
        User postUser = comment.getPostUser();

        if (likesService.getLikes(userService.getUser(userId), comment) != null) {
            comment.setApproved(true);
        } else {
            comment.setApproved(false);
        }

        if (disLikesService.getDisLikes(userService.getUser(userId), comment) != null) {
            comment.setDisapproved(true);
        } else {
            comment.setDisapproved(false);
        }

        //这里到底要不要用计算力代替空间还要考虑
        comment.setAvatar(postUser.getAvatar());
        comment.setNickname(postUser.getNickname());
    }


    /**
     * 新增评论
     * 提交表单后 到这里 然后得到id 然后刷新评论
     *
     * @param comment       前端封装的comment对象
     * @param request       用于得到当前userId 为评论的postUser
     * @param bindingResult 校验异常处理
     * @return 新增的评论或新增失败报错
     */
    @PostMapping("/comment")
    public Result<Map<String, Object>> post(@Valid Comment comment, HttpServletRequest request, BindingResult bindingResult) throws NotFoundException {
        Map<String, Object> hashMap = new HashMap<>(1);

        //得到当前用户
        Long userId = GetTokenInfo.getCustomUserId(request);
        User postUser = userService.getUser(userId);

        Long questionId = comment.getQuestion().getId();

        if (postUser.getCanSpeak()) {
            //如果是空 报错
            if (bindingResult.hasErrors()) {
                hashMap.put("comments", comment);
                return new Result<>(hashMap, false, "内容不能为空");
            }

            //保存
            commentService.saveComment(comment, questionId, postUser);

            //如果有了，更新问题的最新评论时间
            if (commentService.getComment(comment.getId()) != null) {

                Question question = questionService.getQuestion(questionId);
                question.setNewCommentedTime(new Date());
                questionService.saveQuestion(question);

                hashMap.put("comments", comment);

                return new Result<>(hashMap, true, "发布成功");
            } else {
                return new Result<>(null, false, "发布失败");
            }
        } else {
            return new Result<>(null, false, "您无权限发布评论");
        }
    }

    /**
     * 删除评论
     *
     * @param commentId 被删除的评论对应的Id
     * @param request 获取要执行删除操作的用户id
     * @return 报错信息
     */
    @GetMapping("/comment/{commentId}/delete")
    public Result<Map<String, Object>> deleteComment(@PathVariable Long commentId, HttpServletRequest request) {
        Map<String, Object> hashMap = new HashMap<>(1);

        //得到当前用户 以判断权限
        User customUser = userService.getUser(GetTokenInfo.getCustomUserId(request));
        Boolean admin = GetTokenInfo.isAdmin(request);

        Comment comment = commentService.getComment(commentId);

        //如果评论不存在&没权限删除评论报错
        if (comment == null) {
            throw new NotFoundException("该评论不存在");
        }
        if ((comment.getPostUser() != customUser) & (!admin)) {
            throw new NoPermissionException("您无权限删除该评论");
        }


        commentService.deleteComment(commentId);

        //复原
        User postUser = comment.getPostUser();
        postUser.setDonation(postUser.getDonation() - 2);
        userService.saveUser(postUser);

        comment = commentService.getComment(commentId);

        if (comment != null) {
            hashMap.put("comments", comment);
            return new Result<>(hashMap, false, "删除失败");
        } else {
            return new Result<>(null, true, "删除成功");
        }
    }

    /**
     * 给评论点赞
     *
     * @param commentId 被点赞的评论Id
     * @param questionId 被点赞的评论在哪个问题下 问题的Id
     * @param request 获取执行点赞动作的用户Id
     */
    @GetMapping("/comment/{commentId}/approve")
    public void approve(@PathVariable Long questionId, @PathVariable Long commentId, HttpServletRequest request) {
        Comment comment = commentService.getComment(commentId);

        //只能给有效问题点赞
        if (comment.getAnswer()) {

            //当前用户 点赞的人
            Long postUserId = GetTokenInfo.getCustomUserId(request);
            User postUser = userService.getUser(postUserId);
            //被点赞的人
            User receiveUser = comment.getReceiveUser();

            //若点过踩 取消点踩
            DisLikes dislikes = disLikesService.getDisLikes(postUser, comment);
            if (dislikes != null) {
                disLikesService.deleteDisLikes(dislikes);
                //复原
                hideComment(comment, -1);
            }

            Likes likes = likesService.getLikes(postUser, comment);

            //点过赞删除，无则增加
            if (likes != null) {
                //取消点赞前的最高赞数
                Integer maxNum0 = getMaxLikesNum(commentService.listAllCommentByQuestionId(questionId));

                likesService.deleteLikes(likes);

                dealLikes(receiveUser, comment, questionId, maxNum0, -1);
            } else {
                Likes likes1 = new Likes();

                //因为saveLikes是问题点赞和评论点赞公用的 所以要在这里写
                likes1.setLikeQuestion(false);
                likes1.setLikeComment(true);
                likes1.setComment(comment);


                //点赞前的最高赞数
                Integer maxNum0 = getMaxLikesNum(commentService.listAllCommentByQuestionId(questionId));

                //保存
                likesService.saveLikes(likes1, postUser, receiveUser);

                //对impact属性的处理
                dealLikes(receiveUser, comment, questionId, maxNum0, 1);
            }
        }
    }


    /**
     * 点踩 到标准就隐藏
     *
     * @param questionId 被点踩的评论在哪个问题下 问题的Id
     * @param request 获取执行点踩动作的用户Id
     * @param commentId 评论Id
     */
    @GetMapping("/comment/{commentId}/disapprove")
    public void disapprove(@PathVariable Long questionId, @PathVariable Long commentId, HttpServletRequest request) {
        Comment comment = commentService.getComment(commentId);

        Long postUserId = GetTokenInfo.getCustomUserId(request);
        User postUser = userService.getUser(postUserId);
        User receiveUser = comment.getReceiveUser();

        //若点过赞 取消点赞 贡献值复原
        Likes likes = likesService.getLikes(postUser, comment);
        if (likes != null) {
            //取消点赞前的最高赞数
            Integer maxNum0 = getMaxLikesNum(commentService.listAllCommentByQuestionId(questionId));

            likesService.deleteLikes(likes);

            dealLikes(receiveUser, comment, questionId, maxNum0, -1);
        }

        //若点过踩 取消点踩 否则点踩
        DisLikes dislikes = disLikesService.getDisLikes(postUser, comment);
        if (dislikes != null) {
            disLikesService.deleteDisLikes(dislikes);

            hideComment(comment, -1);
        } else {
            DisLikes dislikes1 = new DisLikes();

            dislikes1.setDislikeQuestion(false);
            dislikes1.setDislikeComment(true);
            dislikes1.setComment(comment);
            disLikesService.saveDisLikes(dislikes1, postUser);

            hideComment(comment, 1);
        }
    }

    /**
     * 评论上传的图片
     *
     * @param commentId 评论的Id
     * @param req 获取上传图片的用户Id
     * @param files      多文件上传
     * @param questionId 发布问题的Id
     * @return 多文件在本地的路径
     * @throws IOException 文件大小溢出
     */
    @PostMapping("/uploadPhotos")
    public Result<Map<String, Object>> uploadPhotos(MultipartFile[] files, HttpServletRequest req, @PathVariable Long questionId, @RequestParam Long commentId) throws IOException {
        Map<String, Object> hashMap = new HashMap<>(1);

        //创建存放文件的文件夹的流程
        Long userId = GetTokenInfo.getCustomUserId(req);
        SimpleDateFormat sdf = new SimpleDateFormat("/yyyy-MM-dd/");
        String format = sdf.format(new Date());
        String path = "/upload/" + userId + "/questions/" + questionId + "/comments/" + format;

        //新文件夹目录绝对路径
        String realPath = req.getServletContext().getRealPath(path);
        List<String> pathList = new ArrayList<>();
        for (MultipartFile uploadFile : files) {
            File folder = new File(realPath);
            if (!folder.isDirectory()) {
                folder.mkdirs();
            }

            //保存文件到文件夹中

            //所上传的文件原名
            String oldName = uploadFile.getOriginalFilename();

            //新文件名
            String newName = UUID.randomUUID().toString() + oldName.substring(oldName.lastIndexOf("."));
            uploadFile.transferTo(new File(folder, newName));
            pathList.add(realPath + newName);
        }

        hashMap.put("photos", pathList);
        return new Result<>(hashMap, true, "上传成功");
    }

    /**
     * @param comments 评论集合
     * @return 集合中评论被点赞最多的这个点赞数
     */
    Integer getMaxLikesNum(List<Comment> comments) {
        Integer max = 0;
        for (Comment comment : comments) {
            Integer maxNum = comment.getLikesNum();
            if (maxNum > max) {
                max = maxNum;
            }
        }
        return max;
    }

    void hideComment(Comment comment, int p) {
        comment.setDisLikesNum(comment.getDisLikesNum() + p);
        //被踩到一定程度隐藏评论
        if ((comment.getDisLikesNum() >= Magic.HIDE_STANDARD1) & (comment.getLikesNum() <= Magic.HIDE_STANDARD2 * comment.getDisLikesNum())) {
            comment.setHidden(true);
        } else {
            comment.setHidden(false);
        }
        commentService.saveComment(comment);
    }

    void dealLikes(User receiveUser, Comment comment, Long questionId, int maxNum0, int p) {

        comment.setLikesNum(comment.getLikesNum() + p * 1);
        commentService.saveComment(comment);

        //问题被（取消）点赞 提问者贡献值+-3
        receiveUser.setDonation(receiveUser.getDonation() + p * 3);
        userService.saveUser(receiveUser);

        //提问者贡献值对问题影响力+-12
        //（取消）点赞后的最高赞数
        Integer maxNum1 = getMaxLikesNum(commentService.listAllCommentByQuestionId(questionId));
        Question question = questionService.getQuestion(questionId);
        question.setImpact(question.getImpact() + p * 2 * (maxNum1 - maxNum0) + p * 12);
        questionService.saveQuestion(question);
    }
}
