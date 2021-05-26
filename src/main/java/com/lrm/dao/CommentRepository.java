package com.lrm.dao;

import com.lrm.po.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author 山水夜止
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

    /**
     * 查询问题下的第一级评论
     *
     * @param questionId 问题id
     * @param sort       排序顺序
     * @param answer     哪类回答
     * @return 评论集合
     */
    @Query("select c from Comment c where c.parentComment.id is null and c.question.id = ?1 and c.answer = ?2")
    List<Comment> findByQuestionIdAndAnswer(Long questionId, Boolean answer, Sort sort);

    //原本是这样的 会报错无法识别Answer 不过当时是用的isAnswer 似乎POJO属性不能是isxxx
    //List<Comment> findByQuestionIdAndParentCommentNullAndAnswer(Long questionId, Sort sort, Boolean answer);


    /**
     * 得到问题对应的所有评论
     *
     * @param questionId 问题对应id
     * @return 评论集合
     */
    List<Comment> findByQuestionId(Long questionId);


//    /**
//     * 查询用户未读评论
//     *
//     * @param userId   用户Id
//     * @param isLooked 是否已读
//     * @return 评论集合
//     */
//    List<Comment> findByReceiveUserIdAndLooked(Long receiveUserId, Boolean isLooked);


    /**
     * 查询用户所有被评论
     *
     * @param receiveUserId 问题接受者的用户Id
     * @return 被评论的集合
     */
    List<Comment> findByReceiveUserId(Long receiveUserId);

}
