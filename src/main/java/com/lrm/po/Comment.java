package com.lrm.po;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 山水夜止
 */
@Entity
@Table(name = "t_comment")
public class Comment {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 如果标识为1 即question.user是发布这个comment的人 那么它应该有一个标识
     */
    private Boolean adminComment;

    /**
     * 通知是否已读
     * 不能用isRead 也不能用read关键字 只能这样了。。。
     */
    private Boolean looked;

    /**
     * 是否是第二类回答 即正式回答
     */
    private Boolean answer;

    /**
     * 点赞数
     */
    private Integer likesNum;

    /**
     * 被评论数
     */
    private Integer commentsNum;

    /**
     * 点踩数
     */
    private Integer disLikesNum;

    /**
     * 是否被隐藏
     */
    private Boolean isHidden;

    /**
     * 返回user对象被json忽略 只能加个这个了
     */
    private Long postUserId0;

    /**
     * 前端传回comment 保存parentCommentId的媒介
     */
    private Long parentCommentId0;

    /**
     * 注解@Lob 长文本
     * 前端必填内容
     * 评论内容
     */
    @Lob
    @NotBlank
    private String content;

    /**
     * 封装成完整的"yyyy-MM-dd HH:mm:ss"的Date类型
     * 评论时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;


    /**
     * 节约空间不入库
     * 返回前端的评论发布者的头像
     */
    @Transient
    private String avatar;

    /**
     * 节约空间不入库
     * 返回前端的评论发布者的昵称
     */
    @Transient
    private String nickname;

    /**
     * 节约空间不入库
     * 返回前端的判断该评论是否被当前用户点过赞
     */
    @Transient
    private Boolean approved;

    /**
     * 节约空间不入库
     * 返回前端的判断该评论是否被当前用户点过踩
     */
    @Transient
    private Boolean disapproved;

    /**
     * 节约空间不入库
     * 前端传回comment 保存父级评论者昵称的媒介
     */
    @Transient
    private String parentCommentName;

    /**
     * 节约空间不入库
     * 作为传回前端评论区的Comments集合
     */
    @Transient
    private List<Comment> receiveComments = new ArrayList<>();


    /**
     * 多comment对一question
     */
    @ManyToOne
    private Question question;

    /**
     * 这里不返回给前端 会很乱 用receiveComments代替
     */
    @JsonIgnore
    @OneToMany(mappedBy = "parentComment", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Comment> replyComments = new ArrayList<>();

    /**
     * 跟replyComments的父子关系
     */
    @ManyToOne(fetch = FetchType.LAZY)
    private Comment parentComment;

    /**
     * 一comment对多likes
     */
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Likes> likes;

    /**
     * 一comment对多dislikes
     */
    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DisLikes> dislikes;

    /**
     * 消息的接收者
     */
    @ManyToOne
    private User receiveUser;
    /**
     * 消息的发送者
     */
    @ManyToOne
    private User postUser;

    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getAdminComment() {
        return adminComment;
    }

    public void setAdminComment(Boolean adminComment) {
        this.adminComment = adminComment;
    }

    public Boolean getAnswer() {
        return answer;
    }

    public void setAnswer(Boolean answer) {
        this.answer = answer;
    }

    public Boolean getLooked() {
        return looked;
    }

    public void setLooked(Boolean looked) {
        this.looked = looked;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Integer getCommentsNum() {
        return commentsNum;
    }

    public void setCommentsNum(Integer commentsNum) {
        this.commentsNum = commentsNum;
    }

    public Integer getDisLikesNum() {
        return disLikesNum;
    }

    public void setDisLikesNum(Integer disLikesNum) {
        this.disLikesNum = disLikesNum;
    }

    public Boolean getHidden() {
        return isHidden;
    }

    public void setHidden(Boolean hidden) {
        isHidden = hidden;
    }

    public Integer getLikesNum() {
        return likesNum;
    }

    public void setLikesNum(Integer likesNum) {
        this.likesNum = likesNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getDisapproved() {
        return disapproved;
    }

    public void setDisapproved(Boolean disapproved) {
        this.disapproved = disapproved;
    }

    public Long getPostUserId0() {
        return postUserId0;
    }

    public void setPostUserId0(Long postUserId0) {
        this.postUserId0 = postUserId0;
    }

    public Long getParentCommentId0() {
        return parentCommentId0;
    }

    public void setParentCommentId0(Long parentCommentId0) {
        this.parentCommentId0 = parentCommentId0;
    }

    public String getParentCommentName() {
        return parentCommentName;
    }

    public void setParentCommentName(String parentCommentName) {
        this.parentCommentName = parentCommentName;
    }

    public List<Comment> getReceiveComments() {
        return receiveComments;
    }

    public void setReceiveComments(List<Comment> receiveComments) {
        this.receiveComments = receiveComments;
    }

    /**
     * 令Comment对象的json中不含question
     *
     * @return 关联的question对象 返回的Json里不含它
     */
    @JsonBackReference
    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    /**
     * 令Comment对象的json中不含replyComments
     *
     * @return 关联的replyComments对象 返回的Json里不含它
     */
    @JsonBackReference
    public List<Comment> getReplyComments() {
        return replyComments;
    }

    public void setReplyComments(List<Comment> replyComments) {
        this.replyComments = replyComments;
    }

    /**
     * 令Comment对象的json中不含parentComment
     *
     * @return 关联的parentComment对象 返回的Json里不含它
     */
    @JsonBackReference
    public Comment getParentComment() {
        return parentComment;
    }

    public void setParentComment(Comment parentComment) {
        this.parentComment = parentComment;
    }

    /**
     * 令Comment对象的json中不含receiveUser
     *
     * @return 关联的receiveUser对象 返回的Json里不含它
     */
    @JsonBackReference
    public User getReceiveUser() {
        return receiveUser;
    }

    public void setReceiveUser(User receiveUser) {
        this.receiveUser = receiveUser;
    }

    /**
     * 令Comment对象的json中不含postUser
     *
     * @return 关联的postUser对象 返回的Json里不含它
     */
    @JsonBackReference
    public User getPostUser() {
        return postUser;
    }

    public void setPostUser(User postUser) {
        this.postUser = postUser;
    }

    /**
     * 令Comment对象的json中不含Likes
     *
     * @return 关联的Likes对象 返回的Json里不含它
     */
    @JsonBackReference
    public List<Likes> getLikes() {
        return likes;
    }

    public void setLikes(List<Likes> likes) {
        this.likes = likes;
    }

    /**
     * 只需要likes的json对象中含有question或comment 而后二者的json对象中只需要有数量 不需要有dislikes
     * 所以用这种方向的Json控制
     *
     * @return 关联的dislikes对象 返回的Json里不含它了
     */
    @JsonBackReference
    public List<DisLikes> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<DisLikes> dislikes) {
        this.dislikes = dislikes;
    }

}
