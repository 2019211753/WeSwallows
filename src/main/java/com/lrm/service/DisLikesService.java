package com.lrm.service;

import com.lrm.po.Comment;
import com.lrm.po.DisLikes;
import com.lrm.po.Question;
import com.lrm.po.User;

/**
 * @author 山水夜止
 */
public interface DisLikesService {

    DisLikes saveDisLikes(DisLikes DisLikes, User postUser);

    DisLikes saveDisLikes(DisLikes DisLikes);

    void deleteDisLikes(DisLikes DisLikes);

    DisLikes getDisLikes(User postUser, Question question);

    DisLikes getDisLikes(User postUser, Comment comment);

    DisLikes getDisLikes(Long DisLikesId);

//    List<DisLikes> listAllNotReadComment(Long userId);
}
