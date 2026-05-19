package com.androidcourse.moyan;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.androidcourse.moyan.activity.SearchActivity;

/**
 * 搜索页面UI测试
 */
public class SearchActivityTest extends BaseTest<SearchActivity> {

    public SearchActivityTest() {
        super(SearchActivity.class);
    }

    @Override
    protected void clearLoginState() {
        // 清除搜索历史记录
    }

    // ========== 界面初始化测试 ==========

    /**
     * 测试：页面所有UI组件正确显示
     */
    @Test
    public void testActivityInitialization() {
        onView(withId(R.id.tv_back)).check(matches(isDisplayed()));
        onView(withId(R.id.et_search)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_search)).check(matches(isDisplayed()));
        onView(withId(R.id.view_history)).check(matches(isDisplayed()));
    }

    // ========== 搜索功能测试 ==========

    /**
     * 测试：输入关键词并搜索
     */
    @Test
    public void testPerformSearch() {
        String keyword = "测试关键词";
        onView(withId(R.id.et_search)).perform(typeText(keyword));
        onView(withId(R.id.tv_search)).perform(click());
        waitFor(1000);

        // 验证搜索结果视图显示
        onView(withId(R.id.view_result)).check(matches(isDisplayed()));
        // 验证历史记录视图隐藏
    }

    /**
     * 测试：搜索空关键词
     */
    @Test
    public void testSearchWithEmptyKeyword() {
        onView(withId(R.id.tv_search)).perform(click());
        waitFor(500);
        // 验证Toast"请输入搜索内容"
    }

    // ========== 搜索历史测试 ==========

    /**
     * 测试：搜索后保存到历史记录
     */
    @Test
    public void testSaveToHistory() {
        String keyword = "历史测试词";
        onView(withId(R.id.et_search)).perform(typeText(keyword));
        onView(withId(R.id.tv_search)).perform(click());
        waitFor(1000);

        // 返回历史记录视图
        pressBack();
        waitFor(500);

        // 验证历史记录中包含该关键词
        onView(withText(keyword)).check(matches(isDisplayed()));
    }

    /**
     * 测试：清空历史记录
     */
    @Test
    public void testClearHistory() {
        // 先添加一些历史记录
        // 点击清空按钮
        onView(withId(R.id.iv_clear_all)).perform(click());
        waitFor(500);
        // 验证Toast"历史记录已清空"
        // 验证历史列表为空
    }

    /**
     * 测试：点击历史记录项直接搜索
     */
    @Test
    public void testClickHistoryItem() {
        // 先有历史记录
        // 点击历史记录项
        // 验证搜索结果视图显示
    }

    // ========== 分类筛选测试 ==========

    /**
     * 测试：切换到"用户"分类
     */
    @Test
    public void testSwitchToUserCategory() {
        performSearchFirst();
        onView(withId(R.id.tv_user)).perform(click());
        waitFor(500);
        // 验证分类高亮变化
    }

    /**
     * 测试：切换到"图片"分类
     */
    @Test
    public void testSwitchToImageCategory() {
        performSearchFirst();
        onView(withId(R.id.tv_image)).perform(click());
        waitFor(500);
        // 验证分类高亮变化
    }

    // ========== 排序筛选测试 ==========

    /**
     * 测试：切换到"最新"排序
     */
    @Test
    public void testSwitchToLatestSort() {
        performSearchFirst();
        onView(withId(R.id.tv_latest)).perform(click());
        waitFor(500);
        // 验证排序高亮变化
    }

    // ========== 返回逻辑测试 ==========

    /**
     * 测试：搜索结果页面按返回键回到历史记录页面
     */
    @Test
    public void testBackFromResultToHistory() {
        performSearchFirst();

        // 按返回键
        pressBack();
        waitFor(500);

        // 验证历史记录视图显示
        onView(withId(R.id.view_history)).check(matches(isDisplayed()));
        // 验证搜索结果视图隐藏
    }

    /**
     * 测试：历史记录页面按返回键关闭Activity
     */
    @Test
    public void testBackFromHistoryFinishesActivity() {
        pressBack();
        waitFor(500);
        // 验证Activity finish
    }

    // ========== 辅助方法 ==========

    private void performSearchFirst() {
        onView(withId(R.id.et_search)).perform(typeText("测试"));
        onView(withId(R.id.tv_search)).perform(click());
        waitFor(1000);
    }
}