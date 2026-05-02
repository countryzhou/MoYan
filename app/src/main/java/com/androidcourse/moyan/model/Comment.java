package com.androidcourse.moyan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 评论实体类
 * 功能：表示帖子下的评论或回复数据模型，支持嵌套回复结构（评论-回复的树形结构）
 *
 * 使用场景：
 *   1. 帖子详情页的评论列表展示
 *   2. 评论的嵌套回复（二级回复）
 *   3. 评论的点赞、删除等交互操作
 */
public class Comment {

    /** 评论的唯一标识ID（主键），用于定位和操作具体评论 */
    private int commentId;

    /** 所属帖子的ID，标识这条评论属于哪个帖子 */
    private int postId;

    /** 评论发布者的用户ID，用于关联用户信息 */
    private int userId;

    /** 评论发布者的昵称，用于UI展示（冗余存储，避免每次查询用户表） */
    private String nickname;

    /** 评论发布者的头像URL，用于UI展示 */
    private String avatarUrl;

    /** 评论的文本内容 */
    private String content;

    /** 点赞数量，显示有多少用户喜欢这条评论 */
    private int likeCount;

    /** 回复总数，表示这条评论下有多少条子回复（用于分页和显示） */
    private int replyCount;

    /** 评论创建时间（时间戳，毫秒值），用于排序和显示时间差 */
    private long createTime;

    // ==================== 交互状态字段 ====================

    /** 当前登录用户是否点赞了这条评论，用于UI实时显示点赞状态（红心/空心） */
    private boolean isLiked;

    // ==================== 嵌套回复相关字段 ====================

    /**
     * 被回复的父评论对象
     * 使用场景：当这条评论是回复别人的回复时，此字段指向被回复的那条评论
     * 例如：用户A回复用户B的评论，那么replyTo就指向用户B的评论对象
     * 用于UI显示“@用户名”或显示回复的上下文
     */
    private Comment replyTo;

    /**
     * 子回复列表
     * 存储这条评论下的所有直接回复（一级子回复）
     * 用于实现嵌套评论的展开/折叠功能
     * 注意：这里只存储直接子回复，不存储孙子回复（避免无限嵌套）
     */
    private List<Comment> replies;

    /**
     * UI状态标志：评论是否处于展开状态
     * true - 显示所有子回复
     * false - 隐藏子回复，只显示回复数量
     * 用于实现评论列表的展开/折叠交互
     */
    private boolean isExpanded;

    /**
     * 功能：无参构造函数
     * 具体实现：初始化replies列表为空、折叠状态为false、回复数为0
     * 使用场景：创建新评论对象时，确保集合字段不为null
     */
    public Comment() {
        this.replies = new ArrayList<>();
        this.isExpanded = false;
        this.replyCount = 0;
    }

    // ==================== Getters and Setters ====================
    // 以下方法用于获取和设置上述私有字段的值，遵循JavaBean规范

    public int getCommentId() {
        return commentId;
    }

    public void setCommentId(int commentId) {
        this.commentId = commentId;
    }

    public int getPostId() {
        return postId;
    }

    public void setPostId(int postId) {
        this.postId = postId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public Comment getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(Comment replyTo) {
        this.replyTo = replyTo;
    }

    public int getReplyCount() {
        return replyCount;
    }

    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }

    /**
     * 功能：获取子回复列表
     * 具体实现：如果replies为null则初始化为空列表，避免空指针异常
     * @return 子回复列表（不为null，但可能为空列表）
     */
    public List<Comment> getReplies() {
        if (replies == null) {
            replies = new ArrayList<>();
        }
        return replies;
    }

    /**
     * 功能：设置子回复列表
     * 具体实现：同时更新replies字段和replyCount字段
     * 注意：replyCount会同步更新为回复列表的大小，保持数据一致性
     *
     * @param replies 子回复列表
     */
    public void setReplies(List<Comment> replies) {
        this.replies = replies;
        this.replyCount = replies.size();
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}