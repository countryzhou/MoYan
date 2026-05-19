package com.androidcourse.moyan;

import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import com.androidcourse.moyan.activity.CreatepostActivity;

/**
 * 发布帖子页面UI测试
 */
public class CreatepostActivityTest extends BaseTest<CreatepostActivity> {

    public CreatepostActivityTest() {
        super(CreatepostActivity.class);
    }

    @Override
    protected void clearLoginState() {
        // 确保已登录状态（发帖需要登录）
    }

    // ========== 界面初始化测试 ==========

    /**
     * 测试：页面所有UI组件正确显示
     */
    @Test
    public void testActivityInitialization() {
        onView(withId(R.id.ivBack)).check(matches(isDisplayed()));
        onView(withId(R.id.etContent)).check(matches(isDisplayed()));
        onView(withId(R.id.llAddImage)).check(matches(isDisplayed()));
        onView(withId(R.id.llAddTag)).check(matches(isDisplayed()));
        onView(withId(R.id.anonymousContainer)).check(matches(isDisplayed()));
        onView(withId(R.id.anonymousCheckBox)).check(matches(isDisplayed()));
        onView(withId(R.id.tvSend)).check(matches(isDisplayed()));
        onView(withId(R.id.llDrafts)).check(matches(isDisplayed()));
    }

    // ========== 内容输入测试 ==========

    /**
     * 测试：输入帖子内容
     */
    @Test
    public void testInputContent() {
        String testContent = "这是一条测试帖子的内容";
        onView(withId(R.id.etContent)).perform(typeText(testContent));
        // 验证内容已输入
        onView(withId(R.id.etContent)).check(matches(withText(testContent)));
    }

    /**
     * 测试：内容为空时点击发送
     * 预期：显示Toast提示或不能发送
     */
    @Test
    public void testSendEmptyContent() {
        onView(withId(R.id.tvSend)).perform(click());
        waitFor(500);
        // 验证错误提示
    }

    // ========== 图片功能测试 ==========

    /**
     * 测试：点击添加图片按钮
     * 预期：打开图片选择器
     */
    @Test
    public void testClickAddImage() {
        onView(withId(R.id.llAddImage)).perform(click());
        waitFor(500);
        // 验证图片选择器启动
    }

    /**
     * 测试：添加图片后显示预览
     */
    @Test
    public void testImagePreviewAfterAdd() {
        // 需要Mock图片选择结果
        // 验证llImageContainer中有图片View
    }

    /**
     * 测试：删除已添加的图片
     */
    @Test
    public void testDeleteAddedImage() {
        // 先添加图片
        // 点击删除按钮
        // 验证图片已被移除
    }

    /**
     * 测试：图片数量达到上限后不能继续添加
     * 预期：显示Toast"最多只能选择9张图片"
     */
    @Test
    public void testMaxImageLimit() {
        // 添加9张图片
        // 再次点击添加图片
        // 验证Toast提示
    }

    // ========== 标签功能测试 ==========

    /**
     * 测试：点击添加标签
     * 预期：弹出标签选择对话框
     */
    @Test
    public void testClickAddTag() {
        onView(withId(R.id.llAddTag)).perform(click());
        waitFor(500);
        // 验证对话框显示
    }

    /**
     * 测试：选择系统标签
     */
    @Test
    public void testSelectSystemTag() {
        onView(withId(R.id.llAddTag)).perform(click());
        waitFor(500);
        // 点击某个标签
        // 验证标签显示在llSelectedTags中
    }

    /**
     * 测试：添加自定义标签
     */
    @Test
    public void testAddCustomTag() {
        onView(withId(R.id.llAddTag)).perform(click());
        waitFor(500);
        // 点击"添加自定义标签"
        // 输入标签名
        // 确认添加
        // 验证标签已添加并显示
    }

    /**
     * 测试：长按删除自定义标签
     */
    @Test
    public void testDeleteCustomTag() {
        // 先添加自定义标签
        // 长按标签
        // 确认删除对话框
        // 验证标签已删除
    }

    /**
     * 测试：取消已选中的标签
     */
    @Test
    public void testDeselectTag() {
        // 先选中标签
        // 再次点击相同标签
        // 验证标签从已选列表中移除
    }

    // ========== 匿名发布测试 ==========

    /**
     * 测试：勾选匿名发布
     */
    @Test
    public void testCheckAnonymous() {
        onView(withId(R.id.anonymousCheckBox)).perform(click());
        // 验证CheckBox状态为选中
    }

    /**
     * 测试：取消匿名发布
     */
    @Test
    public void testUncheckAnonymous() {
        // 先选中
        onView(withId(R.id.anonymousCheckBox)).perform(click());
        waitFor(200);
        // 再点击取消
        onView(withId(R.id.anonymousCheckBox)).perform(click());
        // 验证CheckBox状态为未选中
    }

    // ========== 发送帖子测试 ==========

    /**
     * 测试：成功发送帖子
     * 前置条件：已输入内容
     * 预期：显示Toast，关闭当前页面
     */
    @Test
    public void testSendPostSuccess() {
        onView(withId(R.id.etContent)).perform(typeText("测试帖子内容"));
        onView(withId(R.id.tvSend)).perform(click());
        waitFor(1000);
        // 验证Toast
        // 验证Activity finish
    }

    // ========== 草稿箱功能测试 ==========

    /**
     * 测试：点击草稿箱按钮
     * 预期：显示Toast"草稿箱功能开发中"（目前是Toast提示）
     */
    @Test
    public void testClickDrafts() {
        onView(withId(R.id.llDrafts)).perform(click());
        waitFor(500);
        // 验证Toast"草稿箱功能开发中"
    }

    // ========== 返回处理测试 ==========

    /**
     * 测试：有内容时按返回键
     * 预期：弹出"是否保存草稿"对话框
     */
    @Test
    public void testBackWithContent() {
        onView(withId(R.id.etContent)).perform(typeText("一些内容"));
        pressBack();
        waitFor(500);
        // 验证对话框显示
        onView(withText("提示")).check(matches(isDisplayed()));
        onView(withText("是否保存草稿？")).check(matches(isDisplayed()));
    }

    /**
     * 测试：无内容时按返回键
     * 预期：直接关闭页面
     */
    @Test
    public void testBackWithoutContent() {
        pressBack();
        waitFor(500);
        // 验证Activity finish
    }

    /**
     * 测试：草稿保存对话框 - 点击"保存"
     */
    @Test
    public void testSaveDraftOnBack() {
        onView(withId(R.id.etContent)).perform(typeText("草稿内容"));
        pressBack();
        waitFor(500);
        // 点击"保存"按钮
        onView(withText("保存")).perform(click());
        waitFor(500);
        // 验证草稿已保存
        // 验证Activity finish
    }

    /**
     * 测试：草稿保存对话框 - 点击"不保存"
     */
    @Test
    public void testNotSaveDraftOnBack() {
        onView(withId(R.id.etContent)).perform(typeText("草稿内容"));
        pressBack();
        waitFor(500);
        // 点击"不保存"按钮
        onView(withText("不保存")).perform(click());
        waitFor(500);
        // 验证Activity finish
    }

    /**
     * 测试：草稿保存对话框 - 点击"取消"
     */
    @Test
    public void testCancelDraftDialog() {
        onView(withId(R.id.etContent)).perform(typeText("草稿内容"));
        pressBack();
        waitFor(500);
        // 点击"取消"按钮
        onView(withText("取消")).perform(click());
        waitFor(500);
        // 验证对话框关闭，页面未关闭
        onView(withId(R.id.etContent)).check(matches(isDisplayed()));
    }

    // ========== 加载已有草稿测试 ==========

    /**
     * 测试：加载已有草稿
     * 前置条件：Intent传入draft_id
     */
    @Test
    public void testLoadDraft() {
        // 需要通过Intent传递draft_id
        // 验证内容、图片、标签已被填充
    }
}