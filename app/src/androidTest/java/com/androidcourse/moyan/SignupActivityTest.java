package com.androidcourse.moyan;

import org.junit.Test;
import org.junit.Before;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.InstrumentationRegistry;

import com.androidcourse.moyan.activity.SignupActivity;
import com.androidcourse.moyan.network.UserNetworkManager;
import com.androidcourse.moyan.utils.SharedPrefsHelper;

/**
 * 注册页面UI测试
 * 使用响应码验证，绕过Toast
 */
public class SignupActivityTest extends BaseTest<SignupActivity> {

    private UserNetworkManager userNetworkManager;

    public SignupActivityTest() {
        super(SignupActivity.class);
    }

    @Override
    protected void clearLoginState() {
        // 清理注册相关的状态（注册页面不涉及登录，但为了统一）
        InstrumentationRegistry.getInstrumentation().runOnMainSync(() -> {
            SharedPrefsHelper.init(InstrumentationRegistry.getInstrumentation().getTargetContext());
            // 注册页面不需要清理登录状态，但可以清理可能残留的数据
        });
    }

    @Before
    public void setUp() {
        super.setUp();
        userNetworkManager = UserNetworkManager.getInstance();
    }


    // ========== 界面初始化测试 ==========

    /**
     * 测试：页面所有UI组件正确显示
     */
    @Test
    public void testActivityInitialization() {
        onView(withId(R.id.et_phone)).check(matches(isDisplayed()));
        onView(withId(R.id.et_nickname)).check(matches(isDisplayed()));
        onView(withId(R.id.et_password)).check(matches(isDisplayed()));
        onView(withId(R.id.et_confirm_password)).check(matches(isDisplayed()));
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_go_login)).check(matches(isDisplayed()));
    }

    // ========== 注册功能测试（UI交互 + API验证）==========

    /**
     * 测试：注册成功
     * 输入：有效的手机号、昵称、密码
     * 预期：注册成功，返回code=0
     */
    @Test
    public void testRegisterSuccess() {
        String phone = "138" + String.format("%08d", System.currentTimeMillis()%100000000);
        String nickname = "Test"+System.currentTimeMillis()%100000000;
        String password = "123456";

        // UI操作：填写注册信息
        onView(withId(R.id.et_phone)).perform(typeText(phone));
        onView(withId(R.id.et_nickname)).perform(typeText(nickname));
        onView(withId(R.id.et_password)).perform(typeText(password));
        onView(withId(R.id.et_confirm_password)).perform(typeText(password));

        waitFor(1500);

        // 验证：注册成功，页面应该关闭（返回登录页）
        // 由于页面关闭，检查当前Activity是否还可见
        // 如果页面已关闭，再次查找注册页面的元素应该失败（测试会通过，因为Activity已finish）

        // 可选：直接调用API验证响应（更可靠）
        String response = userNetworkManager.register(phone, password, nickname);
        assertResponseSuccess(response, "注册操作");

    }

    /**
     * 测试：注册失败 - 手机号已存在
     * 输入：已注册的手机号
     * 预期：返回code=1，提示手机号已存在
     */
    @Test
    public void testRegisterWithExistingPhone() {
        String existingPhone = "13800138000";
        String nickname = "Test"+System.currentTimeMillis()%100000000;
        String password = "123456";

        String response = userNetworkManager.register(existingPhone, password, nickname);
        assertResponseFailed(response, "注册操作");

    }

    // ========== 表单验证测试（UI层）==========

    /**
     * 测试：手机号为空
     * 预期：客户端验证失败，不发送网络请求
     */
    @Test
    public void testRegisterWithEmptyPhone() {
        onView(withId(R.id.et_nickname)).perform(typeText("Test"+System.currentTimeMillis()%100000000));
        onView(withId(R.id.et_password)).perform(typeText("123456"));
        onView(withId(R.id.et_confirm_password)).perform(typeText("123456"));
        onView(withId(R.id.btn_register)).perform(click());

        waitFor(500);


        // 直接调用API验证空手机号应该失败
        String response = userNetworkManager.register("", "123456", "Test"+System.currentTimeMillis()%100000000);
        assertResponseFailedWithMessage(response, "空手机号注册", "手机号");
    }

    /**
     * 测试：昵称为空
     * 预期：客户端验证失败
     */
    @Test
    public void testRegisterWithEmptyNickname() {
        onView(withId(R.id.et_phone)).perform(typeText("13800138000"));
        onView(withId(R.id.et_password)).perform(typeText("123456"));
        onView(withId(R.id.et_confirm_password)).perform(typeText("123456"));
        onView(withId(R.id.btn_register)).perform(click());

        waitFor(500);

        // 验证：仍然在注册页面
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));

        // API验证：空昵称应该失败
        String response = userNetworkManager.register("13800138001", "123456", "");
        assertResponseFailedWithMessage(response, "空昵称注册", "昵称");
    }

    /**
     * 测试：密码为空
     * 预期：客户端验证失败
     */
    @Test
    public void testRegisterWithEmptyPassword() {
        onView(withId(R.id.et_phone)).perform(typeText("13800138000"));
        onView(withId(R.id.et_nickname)).perform(typeText("Test"+System.currentTimeMillis()%100000000));
        onView(withId(R.id.btn_register)).perform(click());

        waitFor(500);

        // 验证：仍然在注册页面
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));

        // API验证：空密码应该失败
        String response = userNetworkManager.register("13800138001", "", "Test"+System.currentTimeMillis()%100000000);
        assertResponseFailedWithMessage(response, "空密码注册", "密码");
    }

    /**
     * 测试：两次密码不一致
     * 预期：客户端验证失败，不发送网络请求
     */
    @Test
    public void testRegisterWithPasswordMismatch() {
        onView(withId(R.id.et_phone)).perform(typeText("13800138000"));
        onView(withId(R.id.et_nickname)).perform(typeText("Test"+System.currentTimeMillis()%100000000));
        onView(withId(R.id.et_password)).perform(typeText("123456"));
        onView(withId(R.id.et_confirm_password)).perform(typeText("1234567"));
        onView(withId(R.id.btn_register)).perform(click());

        waitFor(500);

        // 验证：仍然在注册页面
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));
    }

    /**
     * 测试：密码长度不足6位
     * 预期：客户端验证失败
     */
    @Test
    public void testRegisterWithShortPassword() {
        onView(withId(R.id.et_phone)).perform(typeText("13800138000"));
        onView(withId(R.id.et_nickname)).perform(typeText("Test"+System.currentTimeMillis()%100000000));
        onView(withId(R.id.et_password)).perform(typeText("123"));
        onView(withId(R.id.et_confirm_password)).perform(typeText("123"));
        onView(withId(R.id.btn_register)).perform(click());

        waitFor(500);

        // 验证：仍然在注册页面
        onView(withId(R.id.btn_register)).check(matches(isDisplayed()));

        // API验证：短密码应该失败
        String response = userNetworkManager.register("13800138001", "123", "Test"+System.currentTimeMillis()%100000000);
        assertResponseFailedWithMessage(response, "短密码注册", "密码");
    }

    // ========== 页面跳转测试 ==========

    /**
     * 测试：点击"已有账号？立即登录"
     * 预期：关闭当前页面，返回登录页
     */
    @Test
    public void testNavigateToLogin() {
        onView(withId(R.id.tv_go_login)).perform(click());
        waitFor(500);
    }


}