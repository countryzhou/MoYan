package com.androidcourse.moyan;

import android.app.Activity;
import android.content.Context;
import android.os.IBinder;
import android.view.WindowManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.Root;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.isDialog;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Espresso UI测试基类
 * 提供通用的测试工具方法和设置
 */
@RunWith(AndroidJUnit4.class)
public abstract class BaseTest<T extends Activity> {

    @Rule
    public ActivityScenarioRule<T> activityScenarioRule;

    protected Class<T> activityClass;
    protected Context targetContext;

    public BaseTest(Class<T> activityClass) {
        this.activityClass = activityClass;
        this.activityScenarioRule = new ActivityScenarioRule<>(activityClass);
    }

    @Before
    public void setUp() {
        targetContext = ApplicationProvider.getApplicationContext();
        clearLoginState();
        sleep(800);
    }

    @After
    public void tearDown() {
        sleep(300);
    }

    protected abstract void clearLoginState();

    // ==================== 基础操作方法 ====================

    protected void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    protected void clickView(int viewId) {
        onView(withId(viewId)).perform(click());
    }

    protected void inputText(int viewId, String text) {
        onView(withId(viewId))
                .perform(typeText(text), closeSoftKeyboard());
    }

    protected void inputTextWithoutClose(int viewId, String text) {
        onView(withId(viewId)).perform(typeText(text));
    }

    // ==================== 视图验证方法 ====================

    protected void assertViewDisplayed(int viewId) {
        onView(withId(viewId)).check(matches(isDisplayed()));
    }

    protected void assertViewWithTextDisplayed(String text) {
        onView(withText(text)).check(matches(isDisplayed()));
    }

    protected void assertViewNotExist(int viewId) {
        onView(withId(viewId)).check(doesNotExist());
    }

    protected void waitForView(int viewId) {
        sleep(1000);
        assertViewDisplayed(viewId);
    }

    protected void waitForViewWithText(String text) {
        sleep(1000);
        assertViewWithTextDisplayed(text);
    }

    // ==================== 滚动和复杂操作 ====================

    protected void scrollToAndClick(int viewId) {
        onView(withId(viewId)).perform(scrollTo(), click());
    }

    protected void scrollToView(int viewId) {
        onView(withId(viewId)).perform(scrollTo());
    }

    // ==================== Toast 和弹窗验证 ====================

    /**
     * 验证 Toast 消息显示（推荐使用）
     * @param message Toast 显示的文本内容
     */
    protected void assertToastDisplayed(String message) {
        onView(withText(message))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));
    }

    /**
     * 验证 Toast 消息并确保其消失（用于确认 Toast 显示后自动消失）
     * @param message Toast 显示的文本内容
     * @param waitTimeMs 等待 Toast 消失的时间（毫秒）
     */
    protected void assertToastAndThenGone(String message, long waitTimeMs) {
        // 先验证 Toast 显示
        onView(withText(message))
                .inRoot(new ToastMatcher())
                .check(matches(isDisplayed()));

        // 等待 Toast 自动消失
        sleep(waitTimeMs);

        // 验证 Toast 已消失（可选，Toast 默认会自动消失）
        // onView(withText(message)).inRoot(new ToastMatcher()).check(doesNotExist());
    }

    /**
     * 验证对话框消息显示
     * @param message 对话框显示的文本内容
     */
    protected void assertDialogDisplayed(String message) {
        onView(withText(message))
                .inRoot(isDialog())
                .check(matches(isDisplayed()));
    }

    // ==================== 网络响应验证方法 ====================

    /**
     * 验证网络响应是否成功
     * @param response 服务端返回的JSON字符串
     * @return true表示成功，false表示失败
     */
    protected boolean isResponseSuccess(String response) {
        if (response == null || response.isEmpty()) {
            return false;
        }
        try {
            JSONObject jsonResponse = new JSONObject(response);
            int code = jsonResponse.getInt("code");
            return code == 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取网络响应中的错误消息
     * @param response 服务端返回的JSON字符串
     * @return 错误消息，如果成功则返回null
     */
    protected String getResponseErrorMessage(String response) {
        if (response == null || response.isEmpty()) {
            return "响应为空";
        }
        try {
            JSONObject jsonResponse = new JSONObject(response);
            int code = jsonResponse.getInt("code");
            if (code != 0) {
                return jsonResponse.optString("msg", "未知错误");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return "解析响应失败: " + e.getMessage();
        }
    }

    /**
     * 断言网络响应成功
     * @param response 服务端返回的JSON字符串
     * @param operationName 操作名称，用于错误提示
     */
    protected void assertResponseSuccess(String response, String operationName) {
        if (!isResponseSuccess(response)) {
            String errorMsg = getResponseErrorMessage(response);
            throw new AssertionError(operationName + " 失败: " + errorMsg);
        }
    }

    /**
     * 断言网络响应失败
     * @param response 服务端返回的JSON字符串
     * @param operationName 操作名称，用于错误提示
     */
    protected void assertResponseFailed(String response, String operationName) {
        if (isResponseSuccess(response)) {
            throw new AssertionError(operationName + " 应该失败但成功了");
        }
    }

    /**
     * 断言网络响应失败并包含指定错误消息
     * @param response 服务端返回的JSON字符串
     * @param operationName 操作名称，用于错误提示
     * @param expectedErrorMsg 期望的错误消息
     */
    protected void assertResponseFailedWithMessage(String response, String operationName, String expectedErrorMsg) {
        if (isResponseSuccess(response)) {
            throw new AssertionError(operationName + " 应该失败但成功了");
        }
        String actualErrorMsg = getResponseErrorMessage(response);
        if (!actualErrorMsg.contains(expectedErrorMsg)) {
            throw new AssertionError(operationName + " 错误消息不匹配。期望包含: '" + expectedErrorMsg + "', 实际: '" + actualErrorMsg + "'");
        }
    }

    // ==================== 导航操作 ====================

    protected void pressBack() {
        Espresso.pressBack();
    }

    // ==================== 高级工具方法 ====================

    protected void clearAndInputText(int viewId, String text) {
        onView(withId(viewId))
                .perform(ViewActions.clearText(), typeText(text), closeSoftKeyboard());
    }

    protected void assertTextContains(int viewId, String expectedText) {
        onView(withId(viewId))
                .check(matches(ViewMatchers.withText(org.hamcrest.Matchers.containsString(expectedText))));
    }

    protected void waitForEspressoIdle() {
        sleep(1000);
    }

    protected void waitFor(long millis) {
        sleep(millis);
    }

    // ==================== ToastMatcher 内部类 ====================

    /**
     * 自定义 Root Matcher，用于匹配 Toast 窗口
     */
    protected static class ToastMatcher extends TypeSafeMatcher<Root> {

        @Override
        public void describeTo(Description description) {
            description.appendText("is a toast");
        }

        @Override
        public boolean matchesSafely(Root root) {
            int type = root.getWindowLayoutParams().get().type;
            // Toast 的窗口类型
            if (type == WindowManager.LayoutParams.TYPE_TOAST) {
                IBinder windowToken = root.getDecorView().getWindowToken();
                IBinder appToken = root.getDecorView().getApplicationWindowToken();
                // Toast 的 windowToken 和 appToken 是同一个对象
                return windowToken == appToken;
            }
            return false;
        }
    }
}
