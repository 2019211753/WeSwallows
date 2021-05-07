package com.lrm.po;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author 山水夜止
 */
@Entity
@Table(name = "t_question")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 懒加载 只有getContent了才加载
     * 前端必填内容
     */
    @Basic(fetch = FetchType.LAZY)
    @Lob
    @NotBlank(message = "请输入内容")
    private String content;


    /**
     * 问题描述 前端必填
     */
    @Lob
    @NotBlank(message = "请输入概述")
    private String description;

    /**
     * 问题标题 前端必填
     **/
    @NotBlank(message = "请输入标题")
    private String title;

    /**
     * 浏览次数
     */
    private Integer view;

    /**
     * 获得点赞数量
     */
    private Integer likesNum;

    /**
     * 获得评论数量
     **/
    private Integer commentsNum;

    /**
     * 被点踩数量
     */
    private Integer disLikesNum;

    /**
     * 是否被隐藏
     */
    private Boolean isHidden;

    /**
     * 占比待定
     * 问题的影响力 推荐 impact=user.donation*4+question.view*2+question.comment.count*2+question.comment.maxLikes*2
     * user.donation=user.comment2.count*3++user.comment2.likes*3+user.question.count*2+user.question.likes*2
     */
    private Integer impact;

    /**
     * 时间会封装成完整的"yyyy-MM-dd HH:mm:ss"的Date类型
     * 首页展示根据发布时间展示
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    /**
     * 最新被评论时间展示
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH-mm-ss")
    @Temporal(TemporalType.TIMESTAMP)
    private Date newCommentedTime;


    /**
     * 节约空间不入库
     * 前端传回多个标签 用,分割的字符组合
     **/
    @Transient
    private String tagIds;

    /**
     * 节约空间不入库
     * 返回前端的评论发布者的头像
     **/
    @Transient
    private String avatar;

    /**
     * 节约空间不入库
     * 返回前端的评论发布者的昵称
     **/
    @Transient
    private String nickname;

    /**
     * 节约空间不入库
     * 返回前端的判断该评论是否被当前用户点过赞
     **/
    @Transient
    private Boolean approved;

    /**
     * 节约空间不入库
     * 返回前端的判断该评论是否被当前用户点过踩
     **/
    @Transient
    private Boolean disapproved;


    /**
     * 一question对应多tag
     */
    @ManyToMany
    @JsonManagedReference
    private List<Tag> tags = new ArrayList<>();

    /**
     * 多question对应一user
     **/
    @JsonBackReference
    @ManyToOne
    private User user;

    /**
     * 多question对应一user
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<Likes> likes;

    /**
     * 多dislikes对应一question
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE)
    private List<DisLikes> dislikes;

    /**
     * 多comments对应一question
     */
    @OneToMany(mappedBy = "question", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Comment> comments = new ArrayList<>();


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getView() {
        return view;
    }

    public void setView(Integer view) {
        this.view = view;
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

    public Date getCreateTime() {
        return createTime;
    }

    public Integer getImpact() {
        return impact;
    }

    public void setImpact(Integer impact) {
        this.impact = impact;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getNewCommentedTime() {
        return newCommentedTime;
    }

    public void setNewCommentedTime(Date newCommentedTime) {
        this.newCommentedTime = newCommentedTime;
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

    public Boolean isApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean isDisapproved() {
        return disapproved;
    }

    public void setDisapproved(boolean disapproved) {
        this.disapproved = disapproved;
    }

    @JsonManagedReference
    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    @JsonBackReference
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonBackReference
    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    @JsonBackReference
    public List<Likes> getLikes() {
        return likes;
    }

    public void setLikes(List<Likes> likes) {
        this.likes = likes;
    }

    @JsonBackReference
    public List<DisLikes> getDislikes() {
        return dislikes;
    }

    public void setDislikes(List<DisLikes> dislikes) {
        this.dislikes = dislikes;
    }

    public String getTagIds() {
        return tagIds;
    }

    public void setTagIds(String tagIds) {
        this.tagIds = tagIds;
    }


    /**
     * 真正起setTagIds作用的是这个方法
     * Tag集合转为String对象
     */
    public void init() {
        this.tagIds = tagsToIds(this.getTags());
    }

    /**
     * 前端Tag对象的格式是以,分割的 tagIds作为一个媒介
     *
     * @param tags 与question关联的tag集合
     * @return , 分割的tagid
     */
    private String tagsToIds(List<Tag> tags) {
        if (!tags.isEmpty()) {
            StringBuilder ids = new StringBuilder();
            boolean flag = false;
            for (Tag tag : tags) {
                if (flag) {
                    ids.append(",");
                } else {
                    flag = true;
                }
                ids.append(tag.getId());
            }
            return ids.toString();
        } else {
            return tagIds;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Question)) {
            return false;
        }
        Question question = (Question) o;
        return isApproved().equals(question.isApproved()) && isDisapproved().equals(question.isDisapproved()) && getId().equals(question.getId()) && getContent().equals(question.getContent()) && getDescription().equals(question.getDescription()) && getTitle().equals(question.getTitle()) && getView().equals(question.getView()) && getLikesNum().equals(question.getLikesNum()) && getCommentsNum().equals(question.getCommentsNum()) && getDisLikesNum().equals(question.getDisLikesNum()) && Objects.equals(isHidden, question.isHidden) && getImpact().equals(question.getImpact()) && Objects.equals(getTagIds(), question.getTagIds()) && Objects.equals(getAvatar(), question.getAvatar()) && Objects.equals(getNickname(), question.getNickname()) && Objects.equals(getCreateTime(), question.getCreateTime()) && getNewCommentedTime().equals(question.getNewCommentedTime()) && Objects.equals(getTags(), question.getTags()) && Objects.equals(getUser(), question.getUser()) && Objects.equals(getLikes(), question.getLikes()) && Objects.equals(getDislikes(), question.getDislikes()) && Objects.equals(getComments(), question.getComments());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getContent(), getDescription(), getTitle(), getView(), getLikesNum(), getCommentsNum(), getDisLikesNum(), isHidden, getImpact(), getTagIds(), getAvatar(), getNickname(), isApproved(), isDisapproved(), getCreateTime(), getNewCommentedTime(), getTags(), getUser(), getLikes(), getDislikes(), getComments());
    }

}
