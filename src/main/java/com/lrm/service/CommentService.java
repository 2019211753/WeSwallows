package com.lrm.service;

import com.lrm.po.Comment;
import com.lrm.po.User;

import java.util.List;

/**
 * @author 山水夜止
 */
public interface CommentService {

    Comment saveComment(Comment comment, Long questionId, User user);

    Comment saveComment(Comment comment);

    List<Comment> listCommentByQuestionId(Long questionId, Boolean isAnswer);

    Comment getComment(Long commentId);

    List<Comment> listAllCommentByQuestionId(Long questionId);

    List<Comment> listAllNotReadComment(Long userId);

    void deleteComment(Long commentId);

}
