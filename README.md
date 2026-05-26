# 陌言 Android 客户端

## 一、项目运行环境

- **开发工具**：Android Studio (Ladybug | 2024.2.1 或更高版本)
- **JDK版本**：11（Android Studio 自带）
- **最低SDK**：API 24 (Android 7.0)
- **目标SDK**：API 36 (Android 15)
- **编译SDK**：API 36

## 二、项目导入步骤

### 步骤1：克隆项目
```
git clone [项目仓库地址]
```
### 步骤2：用 Android Studio 打开
选择 File → Open

选择项目文件夹 MoYan，点击 OK

### 步骤3：等待 Gradle 同步
Android Studio 会自动下载依赖
看到左下角提示 `Gradle sync finished` 表示成功

### 步骤4：运行项目
连接真机（USB调试）或启动模拟器
点击工具栏绿色三角形按钮运行

## 三、安装包模式切换（新增！）
项目支持通过 Build Variant 快速切换 Mock 测试模式和真实联调模式，无需修改代码。

### 3.1 两种模式说明
| 模式             | BuildConfig.IS_DEBUG | 适用场景      | 行为说明                                              |
|:---------------|:---------------------|:----------|:--------------------------------------------------|
| **Debug 模式**   | true                 | 单元测试、UI开发 | 使用 Mock 数据，不需要启动服务端。手机号以 1 开头 + 验证码 123456 即可登录成功 |
| **Release 模式** | false                | 与服务端联调    | 使用真实 Socket 连接，需要启动服务端并配置正确的 IP 地址                |

### 3.2 如何切换模式
**初次流程需要:`File->Sync Project with Gradle Files`同步项目**

点击菜单栏的`Build->Build Variants`标签

在 app 模块的下拉菜单中选择对应模式：

 - 选择 debug → Mock 测试模式
 - 选择 release → 真实联调模式

## 四、项目结构说明
```
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
```
下面是想要的理想实现
```
com.androidcourse.moyan/
│
├── model/                              # 模型层
│   ├── entity/                         # 实体类（原有+新增）
│   │   ├── User.java
│   │   ├── Post.java                   # 添加匿名字段
│   │   ├── Reply.java                  # 添加匿名字段（新增）
│   │   ├── Comment.java
│   │   ├── NewsItem.java
│   │   ├── TrendCard.java
│   │   └── AnonymousMapping.java       # 匿名映射实体（新增）
│   │
│   └── dto/                            # 数据传输对象（新增）
│       ├── LoginRequest.java
│       ├── LoginResponse.java
│       ├── RegisterRequest.java
│       └── AnonymousInfo.java          # 匿名信息DTO（新增）
│
├── network/                            # 网络层
│   ├── SocketClient.java               # 已有
│   ├── CommentNetworkManager.java      # 已有
│   ├── PostNetworkManager.java         # 建议新增
│   ├── UserNetworkManager.java         # 建议新增
│   │
│   ├── request/                        # 请求构建器（新增）
│   │   ├── BaseRequest.java
│   │   ├── PostRequestBuilder.java
│   │   ├── ReplyRequestBuilder.java    # 回复请求构建器
│   │   └── UserRequestBuilder.java
│   │
│   └── response/                       # 响应解析器（新增）
│       ├── ResponseHandler.java
│       └── JsonParser.java
│
├── repository/                         # 数据仓库层（新增）
│   ├── PostRepository.java             # 帖子数据仓库（含匿名处理）
│   ├── CommentRepository.java          # 评论数据仓库（含匿名处理）
│   ├── ReplyRepository.java            # 回复数据仓库（新增）
│   ├── UserRepository.java             # 用户数据仓库
│   └── AnonymousRepository.java        # 匿名数据仓库（新增）
│
├── viewmodel/                          # ViewModel层（新增）
│   ├── auth/
│   │   ├── LoginViewModel.java
│   │   └── RegisterViewModel.java
│   │
│   ├── home/
│   │   ├── HomeViewModel.java
│   │   └── RecommendViewModel.java
│   │
│   ├── post/
│   │   ├── CreatePostViewModel.java    # 创建帖子（含匿名选项）
│   │   ├── PostDetailViewModel.java    # 帖子详情（含匿名显示）
│   │   └── PostListViewModel.java
│   │
│   ├── reply/                          # 回复相关（新增）
│   │   ├── CreateReplyViewModel.java   # 创建回复（含匿名选项）
│   │   └── ReplyListViewModel.java
│   │
│   ├── interaction/
│   │   └── InteractionViewModel.java
│   │
│   ├── message/
│   │   └── MessageViewModel.java
│   │
│   ├── profile/
│   │   ├── ProfileViewModel.java
│   │   ├── EditProfileViewModel.java
│   │   └── UserProfileViewModel.java   # 查看他人主页（新增）
│   │
│   └── search/
│       └── SearchViewModel.java
│
├── view/                               # View层
│   ├── activity/
│   │   ├── SplashActivity.java
│   │   ├── LoginActivity.java
│   │   ├── SignupActivity.java
│   │   ├── HomeActivity.java
│   │   ├── CreatePostActivity.java     # 创建帖子（含匿名复选框）
│   │   ├── PostDetailActivity.java     # 帖子详情（含匿名显示）
│   │   ├── InteractionActivity.java
│   │   ├── MessageActivity.java
│   │   ├── ProfileActivity.java
│   │   ├── EditProfileActivity.java
│   │   ├── SearchActivity.java
│   │   ├── UserProfileActivity.java    # 查看他人主页（新增）
│   │   ├── TopicDetailActivity.java
│   │   └── ItemCommentActivity.java
│   │
│   └── adapter/                        # 适配器（改造支持匿名）
│       ├── PostAdapter.java            # 改造：区分匿名/非匿名显示
│       ├── ReplyAdapter.java           # 改造：区分匿名/非匿名显示
│       ├── CommentAdapter.java
│       ├── NewsAdapter.java
│       └── TrendCardAdapter.java
│
├── cache/                              # 缓存层（新增）
│   ├── MemoryCache.java                # 内存缓存
│   ├── CacheManager.java               # 缓存管理器
│   └── CacheKey.java                   # 缓存Key定义
│
├── utils/                              # 工具类（新增）
│   ├── SharedPrefsHelper.java          # SharedPreferences封装
│   ├── TimeUtils.java                  # 时间格式化
│   ├── ImageLoader.java                # 图片加载封装
│   └── AnonymousHelper.java            # 匿名辅助工具（新增）
│
└── manager/                            # 管理器（新增）
    ├── UserManager.java                # 用户信息管理（单例）
    └── AnonymousManager.java           # 匿名状态管理（新增）
```

可实现模式
·合并匿名相关组件
删除 AnonymousMapping.java 实体（让服务端处理）
删除 AnonymousRepository.java（功能合并到PostRepository）
删除 AnonymousManager.java（功能合并到SharedPrefsHelper）
```
com.androidcourse.moyan/
│
├── model/                              # 模型层（保持原样）
│   ├── User.java                       # 扩展匿名字段
│   ├── Post.java                       # 扩展匿名字段
│   ├── Reply.java                      # 扩展匿名字段
│   ├── Comment.java
│   ├── NewsItem.java
│   ├── TrendCard.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   └── RegisterRequest.java
│
├── network/                            # 网络层（基本保持）
│   ├── SocketClient.java               # 已有
│   ├── CommentNetworkManager.java      # 已有
│   ├── PostNetworkManager.java         # 新增
│   └── UserNetworkManager.java         # 新增
│
├── repository/                         # 数据仓库层（新增，核心）
│   ├── PostRepository.java             # 帖子+匿名逻辑
│   ├── UserRepository.java             # 用户相关
│   └── CommentRepository.java          # 评论相关
│
├── viewmodel/                          # ViewModel层（新增，核心）
│   ├── LoginViewModel.java
│   ├── HomeViewModel.java
│   ├── PostDetailViewModel.java
│   ├── CreatePostViewModel.java
│   └── ProfileViewModel.java
│
├── activity/                           # Activity层（原位置，不用移动）
│   ├── LoginActivity.java              # 改造使用ViewModel
│   ├── SignupActivity.java
│   ├── HomeActivity.java               # 改造使用ViewModel
│   ├── CreatePostActivity.java
│   ├── PostDetailActivity.java
│   ├── ProfileActivity.java
│   ├── EditProfileActivity.java
│   ├── SearchActivity.java
│   ├── MessageActivity.java
│   ├── InteractionActivity.java
│   ├── TopicDetailActivity.java
│   └── UserProfileActivity.java        # 新增
│
├── adapter/                            # 适配器（改造支持匿名）
│   ├── PostAdapter.java                # 改造：区分匿名显示
│   ├── CommentAdapter.java
│   ├── NewsAdapter.java
│   └── TrendCardAdapter.java
│
├── utils/                              # 工具类（简化）
│   ├── SharedPrefsHelper.java          # SP管理
│   ├── TimeUtils.java                  # 时间工具
│   └── AnonymousHelper.java            # 匿名辅助（新增）
│
└── manager/                            # 管理器（简化）
    └── UserManager.java                # 用户状态管理（单例）
```

第四版架构
增加Room数据库存储本地草稿箱、标签等数据
```
com.androidcourse.moyan/
│
├── MoYanApplication.java                                   # Application 类（初始化数据库）
│
├── activity/                                  # Activity 层
│   ├── LoginActivity.java
│   ├── SignupActivity.java
│   ├── HomeActivity.java
│   ├── CreatePostActivity.java                # 新增：写帖子
│   ├── PostDetailActivity.java
│   ├── ProfileActivity.java
│   ├── EditProfileActivity.java
│   ├── SearchActivity.java
│   ├── MessageActivity.java
│   ├── InteractionActivity.java
│   ├── TopicDetailActivity.java
│   └── UserProfileActivity.java
│
├── adapter/                                   # 适配器层
│   ├── PostAdapter.java
│   ├── CommentAdapter.java
│   ├── NewsAdapter.java
│   ├── TrendCardAdapter.java
│   ├── ImagePreviewAdapter.java               # 新增：图片预览适配器
│   └── TagSelectAdapter.java                  # 新增：标签选择适配器
│
├── model/                                     # 模型层
│   ├── User.java
│   ├── Post.java
│   ├── Reply.java
│   ├── Comment.java
│   ├── NewsItem.java
│   ├── TrendCard.java
│   ├── LoginRequest.java
│   ├── LoginResponse.java
│   ├── RegisterRequest.java
│   ├── Draft.java                             # 新增：草稿模型
│   └── Tag.java                               # 新增：标签模型
│
├── database/                                  # 新增：本地数据库层
│   ├── AppDatabase.java                       # Room 数据库实例
│   ├── DraftDao.java                          # 草稿 DAO
│   ├── TagDao.java                            # 标签 DAO
│   └── Converters.java                        # 类型转换器
│
├── repository/                                # 仓库层
│   ├── PostRepository.java
│   ├── UserRepository.java
│   ├── CommentRepository.java
│   ├── DraftRepository.java                   # 新增：草稿仓库
│   └── TagRepository.java                     # 新增：标签仓库
│
├── viewmodel/                                 # ViewModel 层
│   ├── LoginViewModel.java
│   ├── HomeViewModel.java
│   ├── PostDetailViewModel.java
│   ├── ProfileViewModel.java
│   └── CreatePostViewModel.java               # 新增：写帖子 ViewModel
│
├── network/                                   # 网络层
│   ├── SocketClient.java
│   ├── CommentNetworkManager.java
│   ├── PostNetworkManager.java
│   ├── UserNetworkManager.java
│   ├── ApiService.java                        # 新增：Retrofit 接口定义
│   └── RetrofitClient.java                    # 新增：Retrofit 客户端
│
├── utils/                                     # 工具类
│   ├── SharedPrefsHelper.java
│   ├── TimeUtils.java
│   ├── AnonymousHelper.java
│   ├── ImagePickerHelper.java                 # 新增：图片选择辅助类
│   └── KeyboardUtils.java                     # 新增：键盘工具类
│
└── manager/                                   # 管理器
    └── UserManager.java
    

res/layout/
│
├── activity_createcomment.xml          # 创建评论界面
├── activity_createpost.xml             # 创建帖子界面
├── activity_editprofile.xml            # 编辑个人资料界面
├── activity_home.xml                   # 主页界面
├── activity_interaction.xml            # 互动界面（点赞/评论/转发）
├── activity_item_comment.xml           # 评论项界面
├── activity_login.xml                  # 登录界面
├── activity_message.xml                # 消息界面
├── activity_postdetail.xml             # 帖子详情界面
├── activity_profile.xml                # 个人资料界面
├── activity_search.xml                 # 搜索界面
├── activity_signup.xml                 # 注册界面
├── activity_splash.xml                 # 启动页界面
├── activity_topicdetail.xml            # 话题详情界面
│
├── dialog_tag_selector.xml             # 标签选择弹窗
│
├── item_image_preview.xml              # 图片预览项（用于RecyclerView）
├── item_news_card.xml                  # 新闻卡片项
├── item_private_message.xml            # 私信项
├── item_reply.xml                      # 回复项
└── item_trend_card.xml                 # 动态卡片项
```

第五版架构
新增了举报界面，全部用reply替代comment
```
MoYan/
├── app/
│   ├── src/main/java/com/androidcourse/moyan/
│   │   │
│   │   ├── activity/                        # Activity 层
│   │   │   ├── SplashActivity.java          # 启动页
│   │   │   ├── LoginActivity.java           # 登录页
│   │   │   ├── SignupActivity.java          # 注册页
│   │   │   ├── HomeActivity.java            # 首页
│   │   │   ├── CreatepostActivity.java      # 发布帖子
│   │   │   ├── CreateReplyActivity.java     # 发布回复
│   │   │   ├── PostdetailActivity.java      # 帖子详情
│   │   │   ├── ProfileActivity.java         # 个人主页
│   │   │   ├── EditprofileActivity.java     # 编辑资料
│   │   │   ├── UserProfileActivity.java     # 他人主页
│   │   │   ├── SearchActivity.java          # 搜索
│   │   │   ├── MessageActivity.java         # 消息
│   │   │   ├── InteractionActivity.java     # 互动通知
│   │   │   ├── TopicdetailActivity.java     # 话题详情
│   │   │   └── ReportActivity.java          # 举报页面
│   │   │
│   │   ├── adapter/                         # 适配器层
│   │   │   ├── PostAdapter.java             # 帖子列表适配器
│   │   │   ├── ReplyAdapter.java            # 回复列表适配器
│   │   │   ├── NewsAdapter.java             # 新闻列表适配器
│   │   │   ├── TrendCardAdapter.java        # 趋势卡片适配器
│   │   │   ├── ImagePreviewAdapter.java     # 图片预览适配器
│   │   │   └── TagSelectAdapter.java        # 标签选择适配器
│   │   │
│   │   ├── model/                           # 模型层
│   │   │   ├── User.java                    # 用户实体
│   │   │   ├── Post.java                    # 帖子实体（含匿名、图片）
│   │   │   ├── Reply.java                   # 回复实体
│   │   │   ├── Comment.java                 # 【已注释】保留备用
│   │   │   ├── Draft.java                   # 草稿实体（Room）
│   │   │   ├── Tag.java                     # 标签实体（Room）
│   │   │   ├── NewsItem.java                # 新闻列表项
│   │   │   ├── TrendCard.java               # 趋势卡片
│   │   │   ├── LoginRequest.java            # 登录请求
│   │   │   ├── LoginResponse.java           # 登录响应
│   │   │   └── RegisterRequest.java         # 注册请求
│   │   │
│   │   ├── database/                        # Room 数据库层
│   │   │   ├── AppDatabase.java             # 数据库实例
│   │   │   ├── DraftDao.java                # 草稿 DAO
│   │   │   ├── TagDao.java                  # 标签 DAO
│   │   │   └── Converters.java              # 类型转换器
│   │   │
│   │   ├── network/                         # 网络层
│   │   │   ├── SocketClient.java            # Socket 客户端（单例）
│   │   │   ├── UserNetworkManager.java      # 用户相关网络请求
│   │   │   ├── PostNetworkManager.java      # 帖子相关网络请求
│   │   │   ├── ReplyNetworkManager.java     # 回复相关网络请求
│   │   │   └── CommentNetworkManager.java   # 【已注释】保留备用
│   │   │
│   │   ├── repository/                      # 仓库层
│   │   │   ├── UserRepository.java          # 用户数据仓库
│   │   │   ├── PostRepository.java          # 帖子数据仓库
│   │   │   ├── ReplyRepository.java         # 回复数据仓库
│   │   │   ├── DraftRepository.java         # 草稿仓库
│   │   │   ├── TagRepository.java           # 标签仓库
│   │   │   └── CommentRepository.java       # 【已注释】保留备用
│   │   │
│   │   ├── viewmodel/                       # ViewModel 层
│   │   │   ├── LoginViewModel.java          # 登录逻辑
│   │   │   ├── SignupViewModel.java         # 注册逻辑
│   │   │   ├── HomeViewModel.java           # 首页逻辑
│   │   │   ├── PostDetailViewModel.java     # 帖子详情逻辑
│   │   │   ├── CreatePostViewModel.java     # 发布帖子逻辑
│   │   │   └── ProfileViewModel.java        # 个人主页逻辑
│   │   │
│   │   ├── utils/                           # 工具类
│   │   │   ├── SharedPrefsHelper.java       # SharedPreferences 封装
│   │   │   ├── AnonymousHelper.java         # 匿名辅助工具
│   │   │   ├── TimeUtils.java               # 时间格式化
│   │   │   ├── ImagePickerHelper.java       # 图片选择辅助
│   │   │   ├── KeyboardUtils.java           # 键盘工具
│   │   │   └── UriUtils.java                # Uri 路径转换
│   │   │
│   │   ├── manager/                         # 管理器层
│   │   │   └── UserManager.java             # 用户状态管理（单例）
│   │   │
│   │   └── MoYanApplication.java            # Application 类（初始化）
│   │
│   └── src/main/res/                        # 资源文件
│       ├── layout/                          # 页面布局
│       ├── drawable/                        # 图片资源
│       ├── values/                          # 颜色、字符串定义
│       └── menu/                            # 菜单资源
│
├── build.gradle (Project)                   # 项目级构建配置
├── build.gradle (Module: app)               # 模块级构建配置
└── README.md                                # 项目说明文档
```
## 五、核心功能模块

| 模块    | 对应文件                | 说明         |
|:------|:--------------------|:-----------|
| 启动页   | SplashActivity      | App启动欢迎页   |
| 登录    | LoginActivity       | 手机号+验证码登录  |
| 注册    | SignupActivity      | 新用户注册      |
| 首页    | HomeActivity        | 帖子推荐流      |
| 发布帖子  | CreatepostActivity  | 发帖页面       |
| 帖子详情  | PostdetailActivity  | 查看帖子内容和回复  |
| 个人主页  | ProfileActivity     | 查看用户资料和帖子  |
| 编辑资料  | EditprofileActivity | 修改昵称、头像    |
| 搜索    | SearchActivity      | 搜索帖子和话题    |
| 私信    | MessageActivity     | 用户私信聊天     |
| 互动通知  | InteractionActivity | 点赞、评论、回复通知 |
| 话题详情  | TopicdetailActivity | 话题下的帖子列表   |

## 六、网络通信说明

### 通信方式

| 配置项     | 值                     |
|:--------|:----------------------|
| 协议      | Socket TCP            |
| 端口      | 8888                  |
| 编码      | UTF-8                 |
| 消息格式    | JSON（末尾加两个换行符 `\n\n`） |


### 七、API 接口详细说明
【1】用户登录\
请求：{"action":"login","params":{"phone":"13800138000","password":"123456"}}\
说明：password为明文密码，长度6-20位\
响应：{"code":0,"msg":"登录成功","data":{"userId":1,"phone":"13800138000","nickname":"张三","avatarUrl":"","warningCount":0,"isBanned":false}}\

【2】用户注册\
请求：{"action":"register","params":{"phone":"13800138000","password":"123456","nickname":"张三"}}\
说明：password为明文密码，长度6-20位；nickname长度2-20位\
响应：{"code":0,"msg":"注册成功","data":{"userId":1,"phone":"13800138000","nickname":"张三","avatarUrl":""}}\

【3】修改密码（新增）\
请求：{"action":"updatePassword","params":{"userId":1,"oldPassword":"123456","newPassword":"654321"}}\
说明：新密码长度6-20位\
响应：{"code":0,"msg":"密码修改成功","data":null}\

【4】获取用户信息\
请求：{"action":"getUserInfo","params":{"userId":1}}\
响应：{"code":0,"msg":"success","data":{"userId":1,"phone":"13800138000","nickname":"张三","avatarUrl":"http://...","warningCount":0,"isBanned":false}}\

【5】修改昵称\
请求：{"action":"updateNickname","params":{"userId":1,"nickname":"新昵称"}}\
响应：{"code":0,"msg":"success","data":null}\

【6】修改头像\
请求：{"action":"updateAvatar","params":{"userId":1,"avatarUrl":"http://图片地址"}}\
响应：{"code":0,"msg":"success","data":null}\

【7】发布帖子\
请求：{"action":"createPost","params":{"userId":1,"isAnonymous":false,"title":"标题","content":"内容","tags":"诗歌,现代诗"}}\
响应：{"code":0,"msg":"success","data":123}  （data是帖子ID）\

【8】获取帖子列表（首页推荐流）\
请求：{"action":"getPostList","params":{"page":1,"size":20,"userId":1}}\
说明：userId可选，用于判断当前用户是否已点赞\
响应：{"code":0,"msg":"success","data":[{"postId":1,"title":"标题",...}]}\

【9】获取帖子详情\
请求：{"action":"getPostDetail","params":{"postId":1,"userId":1}}\
说明：userId可选，用于判断当前用户是否已点赞/评分\
响应：{"code":0,"msg":"success","data":{"postId":1,"title":"标题","content":"内容","replies":[...]}}\

【10】搜索帖子
请求：{"action":"searchPosts","params":{"keyword":"关键词","tag":"标签","sortBy":"time","page":1}}
说明：sortBy可选值：time（最新）、hot（最热）、score（最高分）
响应：{"code":0,"msg":"success","data":[帖子列表]}


【11】获取用户发布的帖子（新增）
请求：{"action":"getPostsByUserId","params":{"userId":1,"page":1,"size":20}}
说明：page和size为可选参数，默认page=1, size=20
响应：{"code":0,"msg":"success","data":[{"postId":1,"title":"标题","contentPreview":"内容预览","tags":"标签","authorName":"作者","isAnonymous":false,"postTime":"2025-05-26T10:00:00","totalScore":8.5,"replyCount":10,"viewCount":100,"isNewbie":true}]}


【12】发布回复
请求：{"action":"createReply","params":{"postId":1,"userId":1,"isAnonymous":false,"content":"回复内容"}}
响应：{"code":0,"msg":"success","data":456}  （data是回复ID）


【13】获取回复列表
请求：{"action":"getReplies","params":{"postId":1,"page":1}}
响应：{"code":0,"msg":"success","data":[{"replyId":1,"content":"回复内容",...}]}


【14】给帖子评分
请求：{"action":"ratePost","params":{"postId":1,"userId":1,"tagAccuracy":4,"articleScore":5,"comment":"评论"}}
说明：tagAccuracy和articleScore都是1-5分
响应：{"code":0,"msg":"success","data":null}


【15】打赏帖子
请求：{"action":"tipPost","params":{"postId":1,"fromUserId":1,"amount":10}}
说明：金额单位元，平台抽成8%
响应：{"code":0,"msg":"success","data":null}


【16】举报内容
请求：{"action":"report","params":{"reporterId":1,"targetType":1,"targetId":1,"reason":"举报原因"}}
说明：targetType=1表示帖子，2表示回复
响应：{"code":0,"msg":"success","data":null}


【17】获取今日互动任务
请求：{"action":"getTodayTask","params":{}}
响应：{"code":0,"msg":"success","data":{"taskId":1,"taskType":1,"title":"续写任务","content":"原文内容"}}


【18】提交任务回答
请求：{"action":"submitTaskAnswer","params":{"taskId":1,"userId":1,"content":"回答内容"}}
响应：{"code":0,"msg":"success","data":789}  （data是回答ID）


【19】获取任务优质回答
请求：{"action":"getTopAnswers","params":{"taskId":1,"limit":3}}
响应：{"code":0,"msg":"success","data":[{"answerId":1,"content":"回答内容","score":45}]}


【20】检查是否已提交今日任务
请求：{"action":"hasSubmitted","params":{"taskId":1,"userId":1}}
响应：{"code":0,"msg":"success","data":true}  （true已提交/false未提交）

### 八、配置文件说明
1. 网络配置（network/SocketClient.java）\
```
private static final String SERVER_IP = "192.168.1.100";  // 修改为服务端电脑IP
private static final int SERVER_PORT = 8888;           // 服务端端口
```

2. 依赖库（build.gradle (Module: app)）\
```
dependencies {
    // Gson 解析JSON
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

3. 网络权限（AndroidManifest.xml）\
```
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

## 九、测试账号
| 手机号         | 验证码    | 说明           |
|:------------|:-------|:-------------|
| 13800138000 | 123456 | 测试登录（Mock模式） |
| 其他11位手机号 | 123456 | Mock模式同样生效   |

⚠️ 测试阶段验证码固定为 123456


## 十、常见问题
**问题1**：编译报错 Cannot resolve symbol 'Gson'\
**解决**：在 build.gradle (Module: app) 的 dependencies 中添加：\
implementation 'com.google.code.gson:gson:2.10.1'\
然后点击 Sync Now

**问题2**：App启动显示 Hello World\
**解决**：检查 AndroidManifest.xml 中哪个 Activity 有 MAIN 和 LAUNCHER，确保是 LoginActivity

**问题3**：点击"立即注册"没反应\
**解决**：检查 LoginActivity.java 中 tvGoRegister 的点击事件是否写了跳转代码

**问题4**：登录/注册按钮点击后无响应\
**解决**：当前使用 Mock 模式，不需要服务端。手机号以 1 开头 + 验证码 123456 即可成功

## 十一、团队协作提醒
**【前端同学注意】**\
编写代码时，功能注意通过BuildConfig.IS_DEBUG来控制选择模式。\
BuildConfig.IS_DEBUG==1 单元测试\
BuildConfig.IS_DEBUG==0 联调

**【Git 提交规范】**
建议使用以下前缀：\
**feat**: 新功能\
**fix**: 修复bug\
**docs**: 文档更新\
**style**: 代码格式调整\
**refactor**: 重构\
**test**: 测试相关\

示例：
```
feat: 实现登录页面UI和跳转逻辑
fix: 修复注册页面验证码校验问题
docs: 更新README文档
```

## 十一、联系方式
如遇问题，按以下顺序排查：\
查看` Android Studio `控制台报错信息\
检查` AndroidManifest.xml `配置是否正确\
检查布局文件 ID 是否与 Activity 中` findViewById `一致\
如果是网络问题，检查服务端是否启动、IP是否正确、防火墙是否放行8888端口