# 陌言 Android 客户端

## 一、项目运行环境

- **开发工具**：Android Studio (Ladybug | 2024.2.1 或更高版本)
- **JDK版本**：11（Android Studio 自带）
- **最低SDK**：API 24 (Android 7.0)
- **目标SDK**：API 36 (Android 15)
- **编译SDK**：API 36

## 二、项目导入步骤

### 步骤1：克隆项目
```bash
git clone [项目仓库地址]
步骤2：用 Android Studio 打开
选择 File → Open
选择项目文件夹 MoYan，点击 OK
步骤3：等待 Gradle 同步
Android Studio 会自动下载依赖
看到左下角提示 Gradle sync finished 表示成功
步骤4：运行项目
连接真机（USB调试）或启动模拟器
点击工具栏绿色三角形按钮运行


三、项目结构说明
text
MoYan/
├── app/
│   ├── src/main/java/com/androidcourse/moyan/
│   │   ├── network/           # 网络通信层（SocketClient）
│   │   ├── model/             # 数据模型（LoginResponse等）
│   │   ├── LoginActivity.java # 登录页面
│   │   ├── SignupActivity.java# 注册页面
│   │   ├── HomeActivity.java  # 首页
│   │   ├── ProfileActivity.java# 个人主页
│   │   └── ...                # 其他Activity
│   ├── src/main/res/          # 资源文件（布局、图片、样式）
│   │   ├── layout/            # 页面布局XML
│   │   ├── drawable/          # 图片资源
│   │   └── values/            # 颜色、字符串定义
│   └── src/main/AndroidManifest.xml  # 应用配置文件
├── build.gradle (Project)     # 项目级构建配置
├── build.gradle (Module: app) # 模块级构建配置
└── README.md                  # 项目说明文档


四、核心功能模块
模块	对应文件	说明
启动页	SplashActivity	App启动欢迎页
登录	LoginActivity	手机号+验证码登录
注册	SignupActivity	新用户注册
首页	HomeActivity	帖子推荐流
发布帖子	CreatepostActivity	发帖页面
帖子详情	PostdetailActivity	查看帖子内容和回复
个人主页	ProfileActivity	查看用户资料和帖子
编辑资料	EditprofileActivity	修改昵称、头像
搜索	SearchActivity	搜索帖子和话题
私信	MessageActivity	用户私信聊天
互动通知	InteractionActivity	点赞、评论、回复通知
话题详情	TopicdetailActivity	话题下的帖子列表


五、网络通信说明
通信方式
协议：Socket TCP
端口：8888
编码：UTF-8
消息格式：JSON（末尾加两个换行符 \n\n）
请求格式
{"action":"操作名称","params":{参数对象}}
响应格式
{"code":0,"msg":"提示信息","data":数据对象}
code = 0 表示成功
code = 1 表示失败


六、API 接口详细说明
【1】用户登录
action：login
请求示例：
{"action":"login","params":{"phone":"13800138000","code":"123456"}}
响应示例：
{"code":0,"msg":"success","data":{"userId":1,"nickname":"张三","phone":"13800138000"}}

【2】用户注册
action：register
请求示例：
{"action":"register","params":{"phone":"13800138000","nickname":"张三"}}
响应示例：
{"code":0,"msg":"success","data":{"userId":1,"nickname":"张三","phone":"13800138000"}}

【3】获取用户信息
action：getUserInfo
请求示例：
{"action":"getUserInfo","params":{"userId":1}}
响应示例：
{"code":0,"msg":"success","data":{"userId":1,"nickname":"张三","phone":"13800138000","avatarUrl":null}}

【4】修改昵称
action：updateNickname
请求示例：
{"action":"updateNickname","params":{"userId":1,"nickname":"新昵称"}}
响应示例：
{"code":0,"msg":"success","data":null}

【5】修改头像
action：updateAvatar
请求示例：
{"action":"updateAvatar","params":{"userId":1,"avatarUrl":"http://图片地址"}}
响应示例：
{"code":0,"msg":"success","data":null}

【6】发布帖子
action：createPost
请求示例：
{"action":"createPost","params":{"userId":1,"isAnonymous":false,"title":"标题","content":"内容","tags":"诗歌,现代诗"}}
响应示例：
{"code":0,"msg":"success","data":123}
data 是帖子ID

【7】获取帖子列表（首页推荐流）
action：getPostList
请求示例：
{"action":"getPostList","params":{"page":1,"size":20}}
响应示例：
{"code":0,"msg":"success","data":[{"postId":1,"title":"标题","content":"内容","userId":1,"nickname":"张三","likeCount":10,"replyCount":5}]}

【8】获取帖子详情
action：getPostDetail
请求示例：
{"action":"getPostDetail","params":{"postId":1,"userId":1}}
响应示例：
{"code":0,"msg":"success","data":{"postId":1,"title":"标题","content":"内容","replies":[{"replyId":1,"content":"回复内容","userId":2,"nickname":"李四"}]}}

【9】搜索帖子
action：searchPosts
请求示例：
{"action":"searchPosts","params":{"keyword":"关键词","tag":"标签","sortBy":"time","page":1}}
响应示例：
{"code":0,"msg":"success","data":[{"postId":1,"title":"标题","content":"内容"}]}

【10】发布回复
action：createReply
请求示例：
{"action":"createReply","params":{"postId":1,"userId":1,"isAnonymous":false,"content":"回复内容"}}
响应示例：
{"code":0,"msg":"success","data":456}
data 是回复ID

【11】获取回复列表
action：getReplies
请求示例：
{"action":"getReplies","params":{"postId":1,"page":1}}
响应示例：
{"code":0,"msg":"success","data":[{"replyId":1,"content":"回复内容","userId":2,"nickname":"李四","likeCount":3}]}

【12】给帖子评分
action：ratePost
请求示例：
{"action":"ratePost","params":{"postId":1,"userId":1,"tagAccuracy":4,"articleScore":5,"comment":"写得很好"}}
说明：tagAccuracy 和 articleScore 都是1-5分
响应示例：
{"code":0,"msg":"success","data":null}

【13】打赏帖子
action：tipPost
请求示例：
{"action":"tipPost","params":{"postId":1,"fromUserId":1,"amount":10}}
说明：金额单位元，平台抽成8%

响应示例：
{"code":0,"msg":"success","data":null}

【14】举报内容
action：report
请求示例：
{"action":"report","params":{"reporterId":1,"targetType":1,"targetId":1,"reason":"举报原因"}}
说明：targetType=1表示帖子，2表示回复
响应示例：
{"code":0,"msg":"success","data":null}

【15】获取今日互动任务
action：getTodayTask
请求示例：
{"action":"getTodayTask","params":{}}
响应示例：
{"code":0,"msg":"success","data":{"taskId":1,"taskType":1,"title":"续写任务","content":"原文内容"}}

【16】提交任务回答
action：submitTaskAnswer
请求示例：
{"action":"submitTaskAnswer","params":{"taskId":1,"userId":1,"content":"回答内容"}}
响应示例：
{"code":0,"msg":"success","data":789}
data 是回答ID

【17】获取任务优质回答
action：getTopAnswers
请求示例：
{"action":"getTopAnswers","params":{"taskId":1,"limit":3}}
响应示例：
{"code":0,"msg":"success","data":[{"answerId":1,"content":"回答内容","score":45}]}

【18】检查是否已提交今日任务
action：hasSubmitted
请求示例：
{"action":"hasSubmitted","params":{"taskId":1,"userId":1}}
响应示例：
{"code":0,"msg":"success","data":true}
data = true 已提交，false 未提交


七、配置文件说明
1. 网络配置（network/SocketClient.java）
private static final String SERVER_IP = "192.168.1.100";  // 修改为服务端电脑IP
private static final int SERVER_PORT = 8888;              // 服务端端口

2. 依赖库（build.gradle (Module: app)）
dependencies {
    // Gson 解析JSON
    implementation 'com.google.code.gson:gson:2.10.1'
}

3. 网络权限（AndroidManifest.xml）
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />


八、测试账号
手机号	验证码	说明
13800138000	123456	测试登录（Mock模式）
其他11位手机号	123456	Mock模式同样生效
⚠️ 测试阶段验证码固定为 123456


九、常见问题
问题1：编译报错 Cannot resolve symbol 'Gson'
解决：在 build.gradle (Module: app) 的 dependencies 中添加：
implementation 'com.google.code.gson:gson:2.10.1'
然后点击 Sync Now
问题2：App启动显示 Hello World
解决：检查 AndroidManifest.xml 中哪个 Activity 有 MAIN 和 LAUNCHER，确保是 LoginActivity
问题3：点击"立即注册"没反应
解决：检查 LoginActivity.java 中 tvGoRegister 的点击事件是否写了跳转代码
问题4：登录/注册按钮点击后无响应
解决：当前使用 Mock 模式，不需要服务端。手机号以 1 开头 + 验证码 123456 即可成功

十、团队协作提醒
【前端同学注意】
当前登录/注册使用 Mock 模式（假数据），不需要服务端
等服务端搭好后，需要在 LoginActivity 和 SignupActivity 中：
删除 Mock 代码块
取消注释网络请求相关代码
取消注释 import 语句
修改 SocketClient.java 中的 SERVER_IP 为实际服务端IP

【Git 提交规范】
建议使用以下前缀：
feat: 新功能
fix: 修复bug
docs: 文档更新
style: 代码格式调整
refactor: 重构
test: 测试相关

示例：
text
feat: 实现登录页面UI和跳转逻辑
fix: 修复注册页面验证码校验问题
docs: 更新README文档

十一、联系方式
如遇问题，按以下顺序排查：
查看 Android Studio 控制台报错信息
检查 AndroidManifest.xml 配置是否正确
检查布局文件 ID 是否与 Activity 中 findViewById 一致
如果是网络问题，检查服务端是否启动、IP是否正确、防火墙是否放行8888端口