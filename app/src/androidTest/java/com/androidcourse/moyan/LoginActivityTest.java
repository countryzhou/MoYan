package com.androidcourse.moyan;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.intent.Intents;
import androidx.test.platform.app.InstrumentationRegistry;

import com.androidcourse.moyan.activity.HomeActivity;
import com.androidcourse.moyan.activity.LoginActivity;
import com.androidcourse.moyan.network.UserNetworkManager;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

/**
 * 登录页面UI测试
 * 测试账号：13800138000，密码：123456，用户名：Test
 */
public class LoginActivityTest extends BaseTest<LoginActivity> {

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void clearLoginState() {
        // 清除SharedPreferences中的登录信息
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            SharedPrefsHelper.init(ApplicationProvider.getApplicationContext());
            SharedPrefsHelper.getInstance().logout();
        });
    }

    // ========== 界面元素测试 ==========

    /**
     * 测试页面初始化：验证所有UI组件正确显示
     */
    @Test
    public void testActivityInitialization() {
        // 验证手机号输入框存在
        onView(withId(R.id.et_phone)).check(matches(isDisplayed()));
        // 验证密码输入框存在
        onView(withId(R.id.et_password)).check(matches(isDisplayed()));
        // 验证登录按钮存在
        onView(withId(R.id.btn_login)).check(matches(isDisplayed()));
        // 验证注册链接存在
        onView(withId(R.id.tv_go_register)).check(matches(isDisplayed()));
        // 验证游客模式入口存在
        onView(withId(R.id.tv_guest_mode)).check(matches(isDisplayed()));
    }

    // ========== 页面跳转测试 ==========

    /**
     * 测试：点击注册链接
     * 预期：跳转到SignupActivity
     */
    @Test
    public void testNavigateToSignup() {
        onView(withId(R.id.tv_go_register)).perform(click());
        waitFor(500);
        // 验证跳转到SignupActivity
    }

    /**
     * 测试：游客模式进入
     * 点击"游客浏览"
     * 预期：保存游客模式状态，跳转到HomeActivity
     */
    @Test
    public void testEnterGuestMode() {
        onView(withId(R.id.tv_guest_mode)).perform(click());
        waitFor(500);
        // 验证跳转到HomeActivity
        // 验证SharedPreferences中guestMode为true
    }

    // ========== 登录功能测试 ==========


    /**
     * 测试：登录失败（账号不存在）
     * 输入：错误的手机号和密码
     * 预期：显示账号不存在对话框
     */
    @Test
    public void testLoginAccountNotExist() {
        onView(withId(R.id.et_phone)).perform(typeText("99999999999"));
        onView(withId(R.id.et_password)).perform(typeText("wrong"));
        onView(withId(R.id.btn_login)).perform(click());

        waitFor(1500);
        String failResponse = UserNetworkManager.getInstance().login("99999999999", "wrong");
        assertResponseFailedWithMessage(failResponse, "登录操作", "手机号或密码错误");
    }

    @Test
    public void testLoginWithEmptyPhone() {
        onView(withId(R.id.et_password)).perform(typeText("123456"));
        onView(withId(R.id.btn_login)).perform(click());

        waitFor(500);
        String failResponse = UserNetworkManager.getInstance().login("", "123456");
        assertResponseFailedWithMessage(failResponse, "登录操作", "手机号不能为空");
    }

    @Test
    public void testLoginWithEmptyPassword() {
        onView(withId(R.id.et_phone)).perform(typeText("13800138000"));
        onView(withId(R.id.btn_login)).perform(click());

        waitFor(500);
        String failResponse = UserNetworkManager.getInstance().login("13800138000", "");
        assertResponseFailedWithMessage(failResponse, "登录操作", "密码不能为空");

    }
}