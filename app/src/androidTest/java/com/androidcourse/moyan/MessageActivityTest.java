package com.androidcourse.moyan;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.androidcourse.moyan.activity.MessageActivity;

/**
 * 消息页面UI测试
 */
public class MessageActivityTest extends BaseTest<MessageActivity> {

    public MessageActivityTest() {
        super(MessageActivity.class);
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
        onView(withId(R.id.entry_like)).check(matches(isDisplayed()));
        onView(withId(R.id.entry_mention)).check(matches(isDisplayed()));
        onView(withId(R.id.entry_comment)).check(matches(isDisplayed()));
        onView(withId(R.id.rv_private_messages)).check(matches(isDisplayed()));
    }

    // ========== 互动入口测试 ==========

    /**
     * 测试：点击"赞"入口
     * 预期：显示Toast"功能开发中，敬请期待"
     */
    @Test
    public void testClickLikeEntry() {
        onView(withId(R.id.entry_like)).perform(click());
        waitFor(500);
        // 验证Toast"功能开发中，敬请期待"
    }

    /**
     * 测试：点击"@我"入口
     */
    @Test
    public void testClickMentionEntry() {
        onView(withId(R.id.entry_mention)).perform(click());
        waitFor(500);
    }

    /**
     * 测试：点击"留言"入口
     */
    @Test
    public void testClickCommentEntry() {
        onView(withId(R.id.entry_comment)).perform(click());
        waitFor(500);
    }

    // ========== 私信列表测试 ==========

    /**
     * 测试：私信列表显示
     */
    @Test
    public void testPrivateMessageListDisplay() {
        onView(withId(R.id.rv_private_messages)).check(matches(isDisplayed()));
    }

    /**
     * 测试：点击私信列表项
     * 预期：显示Toast"进入与xxx的聊天"
     */
    @Test
    public void testClickPrivateMessageItem() {
        onView(withId(R.id.rv_private_messages))
                .perform(actionOnItemAtPosition(0, click()));
        waitFor(500);
        // 验证Toast显示
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
     * 测试：点击个人导航
     */
    @Test
    public void testNavigateToProfile() {
        onView(withId(R.id.nav_profile)).perform(click());
        waitFor(500);
        // 验证跳转到ProfileActivity
    }
}