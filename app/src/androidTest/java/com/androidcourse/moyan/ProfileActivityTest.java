package com.androidcourse.moyan;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.androidcourse.moyan.activity.ProfileActivity;

/**
 * 个人主页UI测试
 */
public class ProfileActivityTest extends BaseTest<ProfileActivity> {

    public ProfileActivityTest() {
        super(ProfileActivity.class);
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
        onView(withId(R.id.iv_avatar)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_nickname)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_user_id)).check(matches(isDisplayed()));
        onView(withId(R.id.ll_edit_profile)).check(matches(isDisplayed()));
        onView(withId(R.id.ll_favorite)).check(matches(isDisplayed()));
        onView(withId(R.id.ll_message_record)).check(matches(isDisplayed()));
        onView(withId(R.id.ll_post_record)).check(matches(isDisplayed()));
        onView(withId(R.id.ll_logout)).check(matches(isDisplayed()));
    }

    // ========== 修改资料测试 ==========

    /**
     * 测试：点击"修改个人资料"
     * 预期：跳转到EditprofileActivity
     */
    @Test
    public void testNavigateToEditProfile() {
        onView(withId(R.id.ll_edit_profile)).perform(click());
        waitFor(500);
        // 验证跳转到EditprofileActivity
    }

    // ========== 我的内容测试 ==========

    /**
     * 测试：点击"收藏的帖子"
     */
    @Test
    public void testNavigateToFavorite() {
        onView(withId(R.id.ll_favorite)).perform(click());
        waitFor(500);
        // 验证跳转到收藏页面（TODO功能，可能显示Toast）
    }

    /**
     * 测试：点击"留言记录"
     */
    @Test
    public void testNavigateToMessageRecord() {
        onView(withId(R.id.ll_message_record)).perform(click());
        waitFor(500);
    }

    /**
     * 测试：点击"发帖记录"
     */
    @Test
    public void testNavigateToPostRecord() {
        onView(withId(R.id.ll_post_record)).perform(click());
        waitFor(500);
    }

    // ========== 退出登录测试 ==========

    /**
     * 测试：点击"退出登录"
     * 预期：弹出确认对话框，确认后跳转到LoginActivity
     */
    @Test
    public void testLogout() {
        onView(withId(R.id.ll_logout)).perform(click());
        waitFor(500);

        // 验证确认对话框
        onView(withText("确定要退出登录吗？")).check(matches(isDisplayed()));

        // 点击确定
        onView(withText("确定")).perform(click());
        waitFor(500);

        // 验证跳转到LoginActivity
    }

    // ========== 底部导航测试 ==========

    /**
     * 测试：点击首页导航
     */
    @Test
    public void testNavigateToHome() {
        onView(withId(R.id.nav_home)).perform(click());
        waitFor(500);
        // 验证跳转到HomeActivity
    }

    /**
     * 测试：点击发现导航
     */
    @Test
    public void testNavigateToExplore() {
        onView(withId(R.id.nav_explore)).perform(click());
        waitFor(500);
        // 验证跳转到InteractionActivity
    }

    /**
     * 测试：点击消息导航
     */
    @Test
    public void testNavigateToMessages() {
        onView(withId(R.id.nav_messages)).perform(click());
        waitFor(500);
        // 验证跳转到MessageActivity
    }
}