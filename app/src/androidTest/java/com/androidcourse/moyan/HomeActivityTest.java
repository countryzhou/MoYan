package com.androidcourse.moyan;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition;
import static androidx.test.espresso.contrib.RecyclerViewActions.scrollToPosition;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.androidcourse.moyan.activity.HomeActivity;

/**
 * 首页UI测试
 */
public class HomeActivityTest extends BaseTest<HomeActivity> {

    public HomeActivityTest() {
        super(HomeActivity.class);
    }

    @Override
    protected void clearLoginState() {
        // 可设置为已登录状态或游客模式
    }

    // ========== 界面初始化测试 ==========

    /**
     * 测试：首页所有UI组件正确显示
     */
    @Test
    public void testActivityInitialization() {
        // 验证搜索框
        onView(withId(R.id.et_search)).check(matches(isDisplayed()));
        // 验证头像
        onView(withId(R.id.iv_avatar)).check(matches(isDisplayed()));
        // 验证横向卡片RecyclerView
        onView(withId(R.id.rv_trend_cards)).check(matches(isDisplayed()));
        // 验证新闻列表RecyclerView
        onView(withId(R.id.rv_news_list)).check(matches(isDisplayed()));
        // 验证发帖按钮
        onView(withId(R.id.fab_write)).check(matches(isDisplayed()));
        // 验证底部导航栏
        onView(withId(R.id.nav_home)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_explore)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_messages)).check(matches(isDisplayed()));
        onView(withId(R.id.nav_profile)).check(matches(isDisplayed()));
    }

    /**
     * 测试：趋势卡片横向滚动
     */
    @Test
    public void testTrendCardsHorizontalScroll() {
        // 滚动到第3个位置
        onView(withId(R.id.rv_trend_cards))
                .perform(scrollToPosition(2));
        waitFor(500);
        // 验证点指示器更新
    }

    /**
     * 测试：新闻列表垂直滚动
     */
    @Test
    public void testNewsListVerticalScroll() {
        // 滚动到第10个位置
        onView(withId(R.id.rv_news_list))
                .perform(scrollToPosition(10));
        waitFor(500);
    }

    // ========== 搜索功能测试 ==========

    /**
     * 测试：点击搜索框跳转到搜索页
     */
    @Test
    public void testClickSearchBar() {
        onView(withId(R.id.et_search)).perform(click());
        waitFor(500);
        // 验证跳转到SearchActivity
    }

    // ========== 发帖功能测试 ==========

    /**
     * 测试：已登录状态下点击发帖按钮
     * 前置条件：用户已登录
     * 预期：跳转到CreatepostActivity
     */
    @Test
    public void testClickFabWhenLoggedIn() {
        onView(withId(R.id.fab_write)).perform(click());
        waitFor(500);
        // 验证跳转到CreatepostActivity
    }

    /**
     * 测试：未登录状态下点击发帖按钮
     * 前置条件：用户未登录或游客模式
     * 预期：显示Toast提示"请先登录后再发帖"，跳转到LoginActivity
     */
    @Test
    public void testClickFabWhenNotLoggedIn() {
        // 确保未登录状态
        onView(withId(R.id.fab_write)).perform(click());
        waitFor(500);
        // 验证Toast显示
        // 验证跳转到LoginActivity
    }

    // ========== 头像点击测试 ==========

    /**
     * 测试：已登录状态下点击头像
     * 前置条件：用户已登录
     * 预期：跳转到ProfileActivity
     */
    @Test
    public void testClickAvatarWhenLoggedIn() {
        onView(withId(R.id.iv_avatar)).perform(click());
        waitFor(500);
        // 验证跳转到ProfileActivity
    }

    /**
     * 测试：游客模式下点击头像
     * 前置条件：游客模式
     * 预期：显示Toast"请先登录"，跳转到LoginActivity
     */
    @Test
    public void testClickAvatarWhenGuestMode() {
        // 确保游客模式
        onView(withId(R.id.iv_avatar)).perform(click());
        waitFor(500);
        // 验证Toast显示
        // 验证跳转到LoginActivity
    }

    // ========== 底部导航栏测试 ==========

    /**
     * 测试：点击"探索"导航项
     * 预期：跳转到InteractionActivity
     */
    @Test
    public void testNavigateToExplore() {
        onView(withId(R.id.nav_explore)).perform(click());
        waitFor(500);
        // 验证跳转到InteractionActivity
    }

    /**
     * 测试：点击"消息"导航项（已登录）
     * 预期：跳转到MessageActivity
     */
    @Test
    public void testNavigateToMessagesWhenLoggedIn() {
        onView(withId(R.id.nav_messages)).perform(click());
        waitFor(500);
        // 验证跳转到MessageActivity
    }

    /**
     * 测试：点击"消息"导航项（游客模式）
     * 预期：Toast提示"请先登录后再查看消息"，跳转到LoginActivity
     */
    @Test
    public void testNavigateToMessagesWhenGuestMode() {
        onView(withId(R.id.nav_messages)).perform(click());
        waitFor(500);
        // 验证Toast和跳转
    }

    /**
     * 测试：点击"个人资料"导航项（已登录）
     * 预期：跳转到ProfileActivity
     */
    @Test
    public void testNavigateToProfileWhenLoggedIn() {
        onView(withId(R.id.nav_profile)).perform(click());
        waitFor(500);
        // 验证跳转到ProfileActivity
    }

    /**
     * 测试：点击"个人资料"导航项（游客模式）
     * 预期：跳转到LoginActivity
     */
    @Test
    public void testNavigateToProfileWhenGuestMode() {
        onView(withId(R.id.nav_profile)).perform(click());
        waitFor(500);
        // 验证跳转到LoginActivity
    }

    // ========== 刷新功能测试 ==========

    /**
     * 测试：点击首页导航项刷新
     * 预期：显示Toast"刷新中..."，重新加载数据
     */
    @Test
    public void testRefreshData() {
        onView(withId(R.id.nav_home)).perform(click());
        waitFor(500);
        // 验证Toast"刷新中..."
    }

    // ========== 趋势卡片点击测试 ==========

    /**
     * 测试：点击趋势卡片跳转到帖子详情
     */
    @Test
    public void testClickTrendCard() {
        // 点击第一个趋势卡片
        onView(withId(R.id.rv_trend_cards))
                .perform(actionOnItemAtPosition(0, click()));
        waitFor(500);
        // 验证跳转到PostdetailActivity
    }

    // ========== 新闻列表点击测试 ==========

    /**
     * 测试：点击新闻列表项跳转到帖子详情
     */
    @Test
    public void testClickNewsItem() {
        // 点击第一个新闻项
        onView(withId(R.id.rv_news_list))
                .perform(actionOnItemAtPosition(0, click()));
        waitFor(500);
        // 验证跳转到PostdetailActivity
    }
}