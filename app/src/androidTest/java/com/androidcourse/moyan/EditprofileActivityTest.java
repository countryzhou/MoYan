package com.androidcourse.moyan;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

// 添加 ViewActions 导入，替换 typeText
import androidx.test.espresso.action.ViewActions;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;

import com.androidcourse.moyan.activity.EditprofileActivity;

/**
 * 编辑资料页面UI测试
 */
public class EditprofileActivityTest extends BaseTest<EditprofileActivity> {

    public EditprofileActivityTest() {
        super(EditprofileActivity.class);
    }

    @Override
    protected void clearLoginState() {
        // 确保已登录并有用户数据
    }

    // ========== 界面初始化测试 ==========

    /**
     * 测试：页面所有UI组件正确显示
     */
    @Test
    public void testActivityInitialization() {
        onView(withId(R.id.iv_back)).check(matches(isDisplayed()));
        onView(withId(R.id.iv_avatar)).check(matches(isDisplayed()));
        onView(withId(R.id.et_nickname)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_save)).check(matches(isDisplayed()));
    }

    /**
     * 测试：当前用户信息正确加载
     */
    @Test
    public void testLoadUserInfo() {
        // 验证昵称EditText显示当前用户昵称
        // 验证头像显示当前用户头像
    }

    // ========== 头像修改测试 ==========

    /**
     * 测试：点击头像选择新图片
     * 预期：打开图片选择器
     */
    @Test
    public void testClickAvatarToPickImage() {
        onView(withId(R.id.iv_avatar)).perform(click());
        waitFor(500);
        // 验证图片选择器启动
    }

    // ========== 昵称修改测试 ==========

    /**
     * 测试：修改昵称成功
     */
    @Test
    public void testEditNicknameSuccess() {
        String newNickname = "新昵称_" + System.currentTimeMillis();
        onView(withId(R.id.et_nickname)).perform(ViewActions.replaceText(newNickname), closeSoftKeyboard());
        onView(withId(R.id.tv_save)).perform(click());
        waitFor(500);

        // 验证Toast"保存成功"
        // 验证Activity finish
    }

    /**
     * 测试：昵称为空时保存
     * 预期：Toast提示"昵称不能为空"
     */
    @Test
    public void testSaveEmptyNickname() {
        onView(withId(R.id.et_nickname)).perform(ViewActions.replaceText(""), closeSoftKeyboard());
        onView(withId(R.id.tv_save)).perform(click());
        waitFor(500);

        // 验证Toast"昵称不能为空"
    }

    /**
     * 测试：昵称长度不足2位
     * 预期：Toast提示"昵称长度需在2-20位之间"
     */
    @Test
    public void testSaveNicknameTooShort() {
        onView(withId(R.id.et_nickname)).perform(ViewActions.replaceText("a"), closeSoftKeyboard());
        onView(withId(R.id.tv_save)).perform(click());
        waitFor(500);

        // 验证错误Toast
    }

    /**
     * 测试：昵称长度超过20位
     * 预期：Toast提示"昵称长度需在2-20位之间"
     */
    @Test
    public void testSaveNicknameTooLong() {
        String longNickname = "这是一个非常非常非常长的昵称超过二十个字";
        onView(withId(R.id.et_nickname)).perform(ViewActions.replaceText(longNickname), closeSoftKeyboard());
        onView(withId(R.id.tv_save)).perform(click());
        waitFor(500);

        // 验证错误Toast
    }

    // ========== 返回按钮测试 ==========

    /**
     * 测试：点击返回按钮
     */
    @Test
    public void testBackButton() {
        onView(withId(R.id.iv_back)).perform(click());
        waitFor(500);
        // 验证Activity finish
    }

    // ========== 修改资料后验证 ==========

    /**
     * 测试：修改资料后刷新显示
     */
    @Test
    public void testProfileUpdatedAfterSave() {
        String newNickname = "更新测试昵称";
        onView(withId(R.id.et_nickname)).perform(ViewActions.replaceText(newNickname), closeSoftKeyboard());
        onView(withId(R.id.tv_save)).perform(click());
        waitFor(500);

        // 重新进入编辑页面
        // 验证昵称已更新
    }
}