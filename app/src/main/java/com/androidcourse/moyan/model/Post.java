package com.androidcourse.moyan.model;

/**
 * 帖子实体类（Post）
 * 功能：表示用户发布的帖子（文章/动态）的完整数据模型
 *
 * 使用场景：
 *   1. 首页新闻列表展示
 *   2. 帖子详情页展示完整内容
 *   3. 个人中心展示用户发布的帖子列表
 *   4. 搜索结果的帖子条目
 *   5. 趋势卡片的数据源
 *
 * 数据来源：从服务器API获取，通过Gson自动解析JSON字段
 */
public class Post {

    // ==================== 帖子核心信息字段 ====================

    /**
     * 帖子唯一标识ID（主键）
     * 用于定位具体帖子、跳转详情页、点赞、评论等操作
     */
    private int postId;

    /**
     * 发布者的用户ID
     * 用于关联用户信息、判断当前用户是否为作者（控制编辑/删除权限）
     */
    private int userId;

    /** 帖子标题，在列表页和详情页顶部展示 */
    private String title;

    /** 帖子正文内容（支持富文本或纯文本） */
    private String content;

    /**
     * 帖子标签，多个标签用逗号分隔存储
     * 例如："科技,数码,评测" 用于分类和搜索过滤
     */
    private String tags;

    /**
     * 是否匿名发布
     * true - 匿名发布，列表中显示“匿名用户”
     * false - 实名发布，显示真实昵称和头像
     */
    private boolean isAnonymous;

    /**
     * 帖子状态
     * 0 - 待审核（刚发布，等待管理员审核）
     * 1 - 已发布（审核通过，正常显示）
     * 2 - 已拒绝（审核不通过，内容违规）
     */
    private int status;

    // ==================== 互动数据统计字段 ====================

    /** 浏览次数（浏览量），每次打开详情页增加 */
    private int viewCount;

    /** 点赞总数，统计所有用户点赞的数量 */
    private int likeCount;

    /** 回复总数（评论数），统计所有评论+回复的数量 */
    private int replyCount;

    /** 评分人数（有多少用户参与了评分） */
    private int ratingCount;

    /**
     * 平均评分（1-5分）
     * 计算公式：所有评分的总和 / 评分人数
     * 用于显示帖子的综合评分星级
     */
    private double avgScore;

    // ==================== 时间戳字段 ====================

    /**
     * 创建时间（时间戳，毫秒值）
     * 用于排序（最新发布）、显示发布时间（如“3分钟前”）
     */
    private long createTime;

    /**
     * 最后更新时间（时间戳）
     * 当帖子内容被编辑时更新，用于显示“已编辑”标识
     */
    private long updateTime;

    // ==================== 关联查询字段（冗余字段） ====================
    // 以下字段不是帖子表本身的字段，而是通过userId关联user表查询得到的
    // 优点：减少客户端额外请求用户信息的次数

    /**
     * 发布者的昵称（从用户表关联查询）
     * 注意：如果isAnonymous为true，此字段应显示为“匿名用户”
     */
    private String nickname;

    /**
     * 发布者的头像URL（从用户表关联查询）
     * 注意：如果isAnonymous为true，此字段应使用默认匿名头像
     */
    private String avatarUrl;

    /**
     * 当前登录用户是否点赞了此帖子
     * 用于UI实时显示点赞按钮的状态（红色/灰色）
     * 每次加载帖子列表/详情时，服务器会基于当前用户ID返回此字段
     */
    private boolean isLiked;

    // ==================== Getters and Setters ====================
    // 以下方法用于获取和设置上述私有字段的值

    public int getPostId() { return postId; }
    public void setPostId(int postId) { this.postId = postId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public boolean isAnonymous() { return isAnonymous; }
    public void setAnonymous(boolean anonymous) { isAnonymous = anonymous; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public int getViewCount() { return viewCount; }
    public void setViewCount(int viewCount) { this.viewCount = viewCount; }

    public int getLikeCount() { return likeCount; }
    public void setLikeCount(int likeCount) { this.likeCount = likeCount; }

    public int getReplyCount() { return replyCount; }
    public void setReplyCount(int replyCount) { this.replyCount = replyCount; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public double getAvgScore() { return avgScore; }
    public void setAvgScore(double avgScore) { this.avgScore = avgScore; }

    public long getCreateTime() { return createTime; }
    public void setCreateTime(long createTime) { this.createTime = createTime; }

    public long getUpdateTime() { return updateTime; }
    public void setUpdateTime(long updateTime) { this.updateTime = updateTime; }

    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public boolean isLiked() { return isLiked; }
    public void setLiked(boolean liked) { isLiked = liked; }
}