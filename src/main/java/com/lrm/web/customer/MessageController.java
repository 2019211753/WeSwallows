package com.lrm.web.customer;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.lrm.po.Comment;
import com.lrm.po.Likes;
import com.lrm.po.User;
import com.lrm.service.CommentService;
import com.lrm.service.LikesService;
import com.lrm.util.GetTokenInfo;
import com.lrm.vo.Result;
import com.lrm.web.IndexController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 山水夜止.
 */
@RequestMapping("/customer/messages")
@RestController
public class MessageController {
    @Autowired
    private CommentService commentService;

    @Autowired
    private LikesService likesService;

    /**
     * 返回所有通知
     *
     * @param request 获取当前用户id
     * @return 未读评论和点赞
     * @throws JWTVerificationException JWT鉴权错误
     */
    @GetMapping("/")
    public Result<Map<String, Object>> messages(HttpServletRequest request) throws JWTVerificationException {
        Map<String, Object> hashMap = new HashMap<>(2);

        Long userId = GetTokenInfo.getCustomUserId(request);

        List<Comment> comments = commentService.listAllNotReadComment(userId);

        for (Comment comment : comments) {
            User postUser = comment.getPostUser();
            comment.setAvatar(postUser.getAvatar());
            comment.setNickname(postUser.getNickname());
        }

        List<Likes> likes = likesService.listAllNotReadComment(userId);

        for (Likes likes1 : likes) {
            User postUser = likes1.getPostUser();
            likes1.setAvatar(postUser.getAvatar());
            likes1.setNickname(postUser.getNickname());
        }

        hashMap.put("Comments", comments);
        hashMap.put("Likes", likes);

        return new Result<>(hashMap, true, "");
    }

    /**
     * 已读单个评论
     *
     * @param commentId 评论id
     */
    @GetMapping("/{commentId}/read")
    public void readComment(@PathVariable Long commentId) {
        Comment comment = commentService.getComment(commentId);
        comment.setLooked(true);
        commentService.saveComment(comment);
    }

    /**
     * 已读单个点赞
     *
     * @param likesId 点赞id
     */
    @GetMapping("/{likesId}/read")
    public void readLikes(@PathVariable Long likesId) {
        Likes likes = likesService.getLikes(likesId);
        likes.setLooked(true);
        likesService.saveLikes(likes);
    }

    /**
     * 已读所有评论
     *
     * @param request 获取当前用户id
     */
    @GetMapping("/readAllComments")
    public void readAllComments(HttpServletRequest request) {
        Long userId = GetTokenInfo.getCustomUserId(request);
        List<Comment> comments = commentService.listAllNotReadComment(userId);
        for (Comment comment : comments) {
            comment.setLooked(true);
            commentService.saveComment(comment);
        }
    }

    /**
     * 已读所有点赞
     *
     * @param request 获取当前用户id
     */
    @GetMapping("/readAllLikes")
    public void readAllLikes(HttpServletRequest request) {
        Long userId = GetTokenInfo.getCustomUserId(request);

        List<Likes> likes = likesService.listAllNotReadComment(userId);
        for (Likes likes1 : likes) {
            likes1.setLooked(true);
            likesService.saveLikes(likes1);
        }
    }
}

