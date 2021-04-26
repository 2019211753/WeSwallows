package com.lrm.po;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.lrm.annotation.AccountInfoFormat;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//应用于懒加载 在Repository层要使用@EntityGrahph配置在查询方法上 缺一不可
@NamedEntityGraph(name = "User",
        attributeNodes = {@NamedAttributeNode("postComments"),
                @NamedAttributeNode("receiveComments")})
@Entity
@Table(name = "t_user")
public class User {
    /**
     * 每个类都要有一个id主键
     */
    @Id
    @GeneratedValue
    private Long id;

    /**
     * 是否为管理员
     */
    private Boolean isAdmin;

    /**
     * 能否发言
     */
    private Boolean canSpeak;

    /**
     * NotBlank需要搭配有@Valid的controller方法使用 且只能用在String上
     * 前端必填 用户昵称
     */
    @NotBlank(message = "请输入昵称")
    private String nickname;

    /**
     * 前端必填 用户名 后端校验
     */
    @AccountInfoFormat(message = "请输入正确账号格式————长度为7至12且不能包含汉字", need = "false")
    private String username;

    /**
     * 返回Json忽略password
     * 前端必填密码
     */
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @AccountInfoFormat
    private String password;


    /**
     * 用户头像 非必填 可以在前端显示默认值
     */
    private String avatar;
    @Transient
    private File avatarFile;
    /**
     * 用户邮箱 非必填 可以在前端显示默认值
     */
    private String email;
    /**
     * 用户QQ 非必填 可以在前端显示默认值
     */
    private String qqId;
    /**
     * 用户微信 非必填 可以在前端显示默认值
     */
    private String wechatId;

    /**
     * 用户头像 非必填 可以在前端显示默认值
     * 男true 女false
     */
    private Boolean sex;

    /**
     * 个性签名 非必填 可以在前端显示默认值
     */
    private String personalSignature;
    /**
     * 院系 非必填 可以在前端显示默认值
     */
    private String academy;
    /**
     * 专业 非必填 可以在前端显示默认值
     */
    private String major;

    /**
     * 贡献值
     */
    private Integer donation;

    /**
     * 注册时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date registerTime;

    /**
     * 没必要设置Remove 因为不打算做注销账号功能
     * 一user对多questions 发布的问题
     */
    @JsonManagedReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();


    /**
     * 一user对多postComments 发布的评论
     */
    @OneToMany(mappedBy = "postUser", fetch = FetchType.LAZY)
    private List<Comment> postComments = new ArrayList<>();

    /**
     * 一user对多receiveComments 获得的评论
     */
    @OneToMany(mappedBy = "receiveUser", fetch = FetchType.LAZY)
    private List<Comment> receiveComments = new ArrayList<>();

    /**
     * 一user对多postLikes 发布的点赞
     */
    @OneToMany(mappedBy = "postUser", fetch = FetchType.LAZY)
    private List<Likes> postLikes = new ArrayList<>();

    /**
     * 一user对多receiveLikes 接受的点赞
     */
    @OneToMany(mappedBy = "receiveUser", fetch = FetchType.LAZY)
    private List<Comment> receiveLikes = new ArrayList<>();

    /**
     * 一user对多postDisLikes 发布的点踩
     */
    @OneToMany(mappedBy = "postUser", fetch = FetchType.LAZY)
    private List<Comment> postDisLikes = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(Boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public File getAvatarFile() {
        return avatarFile;
    }

    public void setAvatarFile(File avatarFile) {
        this.avatarFile = avatarFile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getQQId() {
        return qqId;
    }

    public void setQQId(String qqId) {
        this.qqId = qqId;
    }

    public Boolean getAdmin() {
        return isAdmin;
    }

    public void setAdmin(Boolean admin) {
        isAdmin = admin;
    }

    public String getQqId() {
        return qqId;
    }

    public void setQqId(String qqId) {
        this.qqId = qqId;
    }

    public String getWechatId() {
        return wechatId;
    }

    public void setWechatId(String wechatId) {
        this.wechatId = wechatId;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public String getPersonalSignature() {
        return personalSignature;
    }

    public void setPersonalSignature(String personalSignature) {
        this.personalSignature = personalSignature;
    }

    public String getAcademy() {
        return academy;
    }

    public void setAcademy(String academy) {
        this.academy = academy;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getDonation() {
        return donation;
    }

    public void setDonation(Integer donation) {
        this.donation = donation;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Boolean getCanSpeak() {
        return canSpeak;
    }

    public void setCanSpeak(Boolean canSpeak) {
        this.canSpeak = canSpeak;
    }

    @JsonManagedReference
    public List<Question> getQuestions() {
        return questions;
    }
    @JsonManagedReference
    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }

    @JsonManagedReference
    public List<Comment> getPostComments() {
        return postComments;
    }

    public void setPostComments(List<Comment> postComments) {
        this.postComments = postComments;
    }

    @JsonManagedReference
    public List<Comment> getReceiveComments() {
        return receiveComments;
    }

    public void setReceiveComments(List<Comment> receiveComments) {
        this.receiveComments = receiveComments;
    }

    @JsonManagedReference
    public List<Likes> getPostLikes() {
        return postLikes;
    }

    public void setPostLikes(List<Likes> postLikes) {
        this.postLikes = postLikes;
    }

    @JsonManagedReference
    public List<Comment> getReceiveLikes() {
        return receiveLikes;
    }

    public void setReceiveLikes(List<Comment> receiveLikes) {
        this.receiveLikes = receiveLikes;
    }

    @JsonManagedReference
    public List<Comment> getPostDisLikes() {
        return postDisLikes;
    }

    public void setPostDisLikes(List<Comment> postDisLikes) {
        this.postDisLikes = postDisLikes;
    }

    //    @Override
//    public String toString() {
//        return "User{" +
//                "id=" + id +
//                ", isAdmin=" + isAdmin +
//                ", canSpeak=" + canSpeak +
//                ", nickname='" + nickname + '\'' +
//                ", username='" + username + '\'' +
//                ", password='" + password + '\'' +
//                ", avatar='" + avatar + '\'' +
//                ", email='" + email + '\'' +
//                ", qqId='" + qqId + '\'' +
//                ", wechatId='" + wechatId + '\'' +
//                ", sex=" + sex +
//                ", personalSignature='" + personalSignature + '\'' +
//                ", academy='" + academy + '\'' +
//                ", major='" + major + '\'' +
//                ", donation=" + donation +
//                ", registerTime=" + registerTime +
//                ", questions=" + questions +
//                ", postComments=" + postComments +
//                ", receiveComments=" + receiveComments +
//                ", postLikes=" + postLikes +
//                ", receiveLikes=" + receiveLikes +
//                '}';
//    }
}

