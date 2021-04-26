package com.lrm.po;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "t_likes")
public class Likes {
    /**
     * 主键
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 赞的是评论
     */
    private Boolean likeComment;
    /**
     * 赞的是问题
     */
    private Boolean likeQuestion;
    /**
     * 是否已读
     */
    private Boolean isRead;

    /**
     * 返回user对象被json忽略 只能加个这个了
     */
    private Long postUserId0;

    /**
     * 封装成完整的"yyyy-MM-dd HH:mm:ss"的Date类型
     * 点赞时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;


    /**
     * 多likes对一comment
     */
    @ManyToOne
    private Comment comment;

    /**
     * 多likes对一question
     */
    @ManyToOne
    private Question question;

    /**
     * 多likes对一postUser
     */
    @ManyToOne
    private User postUser;

    /**
     * 多likes对一receiveUser
     */
    @ManyToOne
    private User receiveUser;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getLikeComment() {
        return likeComment;
    }

    public void setLikeComment(Boolean likeComment) {
        this.likeComment = likeComment;
    }

    public Boolean getLikeQuestion() {
        return likeQuestion;
    }

    public void setLikeQuestion(Boolean likeQuestion) {
        this.likeQuestion = likeQuestion;
    }

    public Boolean getRead() {
        return isRead;
    }

    public void setRead(Boolean read) {
        isRead = read;
    }

    public Long getPostUserId0() {
        return postUserId0;
    }

    public void setPostUserId0(Long postUserId0) {
        this.postUserId0 = postUserId0;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    @JsonManagedReference
    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    @JsonManagedReference
    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    @JsonBackReference
    public User getPostUser() {
        return postUser;
    }

    public void setPostUser(User postUser) {
        this.postUser = postUser;
    }

    @JsonBackReference
    public User getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(User receiveUser) {
        this.receiveUser = receiveUser;
    }

    @Override
    public String toString() {
        return "Likes{" +
                "id=" + id +
                ", likeComment=" + likeComment +
                ", likeQuestion=" + likeQuestion +
                ", isRead=" + isRead +
                ", postUserId0=" + postUserId0 +
                ", createTime=" + createTime +
                ", comment=" + comment +
                ", question=" + question +
                ", postUser=" + postUser +
                ", receiveUser=" + receiveUser +
                '}';
    }
}
