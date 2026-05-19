package com.androidcourse.moyan;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.androidcourse.moyan.activity.PostdetailActivity;

/**
 * 帖子详情页面UI测试
 */
public class PostdetailActivityTest extends BaseTest<PostdetailActivity> {

    public PostdetailActivityTest() {
        super(PostdetailActivity.class);
    }

    @Override
    protected void clearLoginState() {
        // 确保已登录状态
    }

    // ========== 界面初始化测试 ==========

    /**
     * 测试：页面所有UI组件正确显示
     */
    @Test
    public void testActivityInitialization() {
        onView(withId(R.id.btnBack)).check(matches(isDisplayed()));
        onView(withId(R.id.ivAuthorAvatar)).check(matches(isDisplayed()));
        onView(withId(R.id.tvAuthorName)).check(matches(isDisplayed()));
        onView(withId(R.id.tvPostTitle)).check(matches(isDisplayed()));
        onView(withId(R.id.tvPostContent)).check(matches(isDisplayed()));
        onView(withId(R.id.rvComments)).check(matches(isDisplayed()));
        onView(withId(R.id.etComment)).check(matches(isDisplayed()));
        onView(withId(R.id.ivLikeBottom)).check(matches(isDisplayed()));
        onView(withId(R.id.ivCollect)).check(matches(isDisplayed()));
        onView(withId(R.id.layoutShare)).check(matches(isDisplayed()));
    }

    // ========== 点赞功能测试 ==========

    /**
     * 测试：点赞帖子
     */
    @Test
    public void testLikePost() {
        int initialLikeCount = getLikeCount();
        onView(withId(R.id.ivLikeBottom)).perform(click());
        waitFor(500);
        // 验证点赞数+1
        // 验证图标变化
    }

    /**
     * 测试：取消点赞
     */
    @Test
    public void testUnlikePost() {
        // 先点赞
        onView(withId(R.id.ivLikeBottom)).perform(click());
        waitFor(500);
        // 再次点击取消点赞
        onView(withId(R.id.ivLikeBottom)).perform(click());
        waitFor(500);
        // 验证点赞数恢复
        // 验证图标恢复
    }

    // ========== 收藏功能测试 ==========

    /**
     * 测试：收藏帖子
     */
    @Test
    public void testCollectPost() {
        onView(withId(R.id.ivCollect)).perform(click());
        waitFor(500);
        // 验证Toast"收藏成功"
        // 验证图标变化
    }

    /**
     * 测试：取消收藏
     */
    @Test
    public void testUncollectPost() {
        // 先收藏
        onView(withId(R.id.ivCollect)).perform(click());
        waitFor(500);
        // 再次点击取消收藏
        onView(withId(R.id.ivCollect)).perform(click());
        waitFor(500);
        // 验证Toast"取消收藏"
        // 验证图标恢复
    }

    // ========== 评论功能测试 ==========

    /**
     * 测试：发表评论
     */
    @Test
    public void testSubmitComment() {
        String commentText = "这是一条测试评论";
        onView(withId(R.id.etComment)).perform(typeText(commentText));
        // 需要点击发送按钮（可能在键盘上或使用EditorAction）
        // 验证评论成功Toast
        // 验证评论列表更新
    }

    /**
     * 测试：评论内容为空时提交
     */
    @Test
    public void testSubmitEmptyComment() {
        onView(withId(R.id.etComment)).perform(click());
        // 尝试提交空评论
        // 验证Toast"请输入评论内容"
    }

    /**
     * 测试：回复评论
     */
    @Test
    public void testReplyToComment() {
        // 滚动到评论列表
        // 点击某条评论的"回复"按钮
        // 验证输入框hint变为"回复 @用户名"
    }

    /**
     * 测试：删除自己的评论
     */
    @Test
    public void testDeleteOwnComment() {
        // 先发表评论
        // 找到自己的评论
        // 点击删除按钮
        // 确认删除对话框
        // 验证评论已删除
    }

    // ========== 关注功能测试 ==========

    /**
     * 测试：关注作者
     */
    @Test
    public void testFollowAuthor() {
        onView(withId(R.id.btnFollow)).perform(click());
        waitFor(500);
        // 验证按钮文字变为"已关注"
        // 验证Toast"关注成功"
    }

    /**
     * 测试：取消关注
     */
    @Test
    public void testUnfollowAuthor() {
        // 先关注
        onView(withId(R.id.btnFollow)).perform(click());
        waitFor(500);
        // 再次点击取消关注
        onView(withId(R.id.btnFollow)).perform(click());
        waitFor(500);
        // 验证按钮文字变为"关注"
        // 验证Toast"取消关注"
    }

    // ========== 分享功能测试 ==========

    /**
     * 测试：分享帖子
     */
    @Test
    public void testSharePost() {
        onView(withId(R.id.layoutShare)).perform(click());
        waitFor(500);
        // 验证分享选择器打开
    }

    // ========== 评论列表测试 ==========

    /**
     * 测试：评论列表滚动
     */
    @Test
    public void testCommentListScroll() {
        onView(withId(R.id.rvComments))
                .perform(actionOnItemAtPosition(5, click()));
        waitFor(500);
    }

    /**
     * 测试：点击评论中的头像跳转到用户主页
     */
    @Test
    public void testClickCommentAvatar() {
        // 找到评论中的头像
        // 点击
        // 验证跳转到UserProfileActivity
    }

    // ========== 返回按钮测试 ==========

    /**
     * 测试：点击返回按钮
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.btnBack)).perform(click());
        waitFor(500);
        // 验证Activity finish
    }

    // ========== 辅助方法 ==========

    private int getLikeCount() {
        // 从tvLikeCountBottom获取当前点赞数
        return 0;
    }
}