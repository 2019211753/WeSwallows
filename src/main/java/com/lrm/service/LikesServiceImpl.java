package com.lrm.service;

import com.lrm.dao.LikesRepository;
import com.lrm.po.Comment;
import com.lrm.po.Likes;
import com.lrm.po.Question;
import com.lrm.po.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author 山水夜止
 */
@Service
public class LikesServiceImpl implements LikesService{
    @Autowired
    LikesRepository likesRepository;

    @Transactional
    @Override
    public Likes saveLikes(Likes likes, User postUser, User receiveUser) {
        likes.setCreateTime(new Date());
        likes.setPostUser(postUser);
        likes.setReceiveUser(receiveUser);
        likes.setLooked(receiveUser == postUser);
        likes.setPostUserId0(postUser.getId());

        if (likes.getLikeComment()) {
            if (likes.getComment().getPostUser().equals(likes.getPostUser())) {
                likes.setLooked(true);
            }
        }

        if (likes.getLikeQuestion()) {
            if (likes.getQuestion().getUser().equals(likes.getPostUser())) {
                likes.setLooked(true);
            }
        }
        return likesRepository.save(likes);
    }

    @Override
    @Transactional
    public Likes saveLikes(Likes likes) {
        return likesRepository.save(likes);
    }

    @Override
    @Transactional
    public void deleteLikes(Likes likes) {
        likesRepository.delete(likes);
    }

    @Override
    public Likes getLikes(User postUser, Question question) {
        return likesRepository.findByPostUserAndQuestion(postUser, question);
    }

    @Override
    public Likes getLikes(User postUser, Comment comment) {
        return likesRepository.findByPostUserAndComment(postUser, comment);
    }

    @Override
    public Likes getLikes(Long likesId) {
        Optional<Likes> likes = likesRepository.findById(likesId);
        return likes.orElse(null);
    }

    @Override
    public List<Likes> listAllNotReadComment(Long userId) {
        return likesRepository.findByReceiveUserIdAndLooked(userId, false);
    }

}
