package com.lrm.dao;

import com.lrm.po.Comment;
import com.lrm.po.Likes;
import com.lrm.po.Question;
import com.lrm.po.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * @author 山水夜止
 */
public interface LikesRepository extends JpaRepository<Likes, Long> {

    /**
     * 查询User有没有对该问题点过赞
     *
     * @param postUser 当前用户
     * @param question 当前问题
     * @return 没点过返回null
     */
    Likes findByPostUserAndQuestion(User postUser, Question question);

    /**
     * 查询User有没有对该评论点过踩
     *
     * @param postUser 当前用户
     * @param comment  当前评论
     * @return 没点过返回null
     */
    Likes findByPostUserAndComment(User postUser, Comment comment);

//    /**
//     * 已/未读的点赞记录
//     *
//     * @param userId   当前用户ID
//     * @param isLooked 是否已读
//     * @return 点赞记录集合
//     */
//    List<Likes> findByReceiveUserIdAndLooked(Long receiveUserId, Boolean isLooked);

    /**
     * 查询用户所有被点赞
     *
     * @param receiveUserId 问题接受者的用户Id
     * @return 被评论的集合
     */
    List<Likes> findByReceiveUserId(Long receiveUserId);
}
