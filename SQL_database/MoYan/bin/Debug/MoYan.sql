/*
MoYan 的部署脚本

此代码由工具生成。
如果重新生成此代码，则对此文件的更改可能导致
不正确的行为并将丢失。
*/

GO
SET ANSI_NULLS, ANSI_PADDING, ANSI_WARNINGS, ARITHABORT, CONCAT_NULL_YIELDS_NULL, QUOTED_IDENTIFIER ON;

SET NUMERIC_ROUNDABORT OFF;


GO
:setvar DatabaseName "MoYan"
:setvar DefaultFilePrefix "MoYan"
:setvar DefaultDataPath "C:\Users\31003\AppData\Local\Microsoft\VisualStudio\SSDT\MoYan"
:setvar DefaultLogPath "C:\Users\31003\AppData\Local\Microsoft\VisualStudio\SSDT\MoYan"

GO
:on error exit
GO
/*
请检测 SQLCMD 模式，如果不支持 SQLCMD 模式，请禁用脚本执行。
要在启用 SQLCMD 模式后重新启用脚本，请执行:
SET NOEXEC OFF; 
*/
:setvar __IsSqlCmdEnabled "True"
GO
IF N'$(__IsSqlCmdEnabled)' NOT LIKE N'True'
    BEGIN
        PRINT N'要成功执行此脚本，必须启用 SQLCMD 模式。';
        SET NOEXEC ON;
    END


GO
USE [$(DatabaseName)];


GO
IF EXISTS (SELECT 1
           FROM   [master].[dbo].[sysdatabases]
           WHERE  [name] = N'$(DatabaseName)')
    BEGIN
        ALTER DATABASE [$(DatabaseName)]
            SET ARITHABORT ON,
                CONCAT_NULL_YIELDS_NULL ON,
                CURSOR_DEFAULT LOCAL 
            WITH ROLLBACK IMMEDIATE;
    END


GO
IF EXISTS (SELECT 1
           FROM   [master].[dbo].[sysdatabases]
           WHERE  [name] = N'$(DatabaseName)')
    BEGIN
        ALTER DATABASE [$(DatabaseName)]
            SET PAGE_VERIFY NONE,
                DISABLE_BROKER 
            WITH ROLLBACK IMMEDIATE;
    END


GO
ALTER DATABASE [$(DatabaseName)]
    SET TARGET_RECOVERY_TIME = 0 SECONDS 
    WITH ROLLBACK IMMEDIATE;


GO
IF EXISTS (SELECT 1
           FROM   [master].[dbo].[sysdatabases]
           WHERE  [name] = N'$(DatabaseName)')
    BEGIN
        ALTER DATABASE [$(DatabaseName)]
            SET QUERY_STORE (QUERY_CAPTURE_MODE = ALL, CLEANUP_POLICY = (STALE_QUERY_THRESHOLD_DAYS = 367), MAX_STORAGE_SIZE_MB = 100) 
            WITH ROLLBACK IMMEDIATE;
    END


GO
PRINT N'正在创建 表 [dbo].[admins]...';


GO
CREATE TABLE [dbo].[admins] (
    [admin_id]        INT            IDENTITY (1, 1) NOT NULL,
    [phone]           VARCHAR (11)   NOT NULL,
    [nickname]        NVARCHAR (20)  NOT NULL,
    [avatar_url]      NVARCHAR (500) NULL,
    [password_hash]   VARCHAR (128)  NULL,
    [is_vip]          BIT            NULL,
    [vip_expire_date] DATETIME       NULL,
    [warning_count]   INT            NULL,
    [is_banned]       BIT            NULL,
    [register_time]   DATETIME       NULL,
    [last_login_time] DATETIME       NULL,
    PRIMARY KEY CLUSTERED ([admin_id] ASC),
    UNIQUE NONCLUSTERED ([nickname] ASC),
    UNIQUE NONCLUSTERED ([phone] ASC)
);


GO
PRINT N'正在创建 表 [dbo].[anonymous_mapping]...';


GO
CREATE TABLE [dbo].[anonymous_mapping] (
    [mapping_id]    INT IDENTITY (1, 1) NOT NULL,
    [post_id]       INT NOT NULL,
    [user_id]       INT NOT NULL,
    [anonymous_num] INT NOT NULL,
    PRIMARY KEY CLUSTERED ([mapping_id] ASC),
    UNIQUE NONCLUSTERED ([post_id] ASC, [user_id] ASC)
);


GO
PRINT N'正在创建 表 [dbo].[daily_tasks]...';


GO
CREATE TABLE [dbo].[daily_tasks] (
    [task_id]      INT            IDENTITY (1, 1) NOT NULL,
    [task_type]    TINYINT        NULL,
    [title]        NVARCHAR (100) NULL,
    [content]      NVARCHAR (MAX) NOT NULL,
    [publish_date] DATE           NOT NULL,
    [is_active]    BIT            NULL,
    PRIMARY KEY CLUSTERED ([task_id] ASC)
);


GO
PRINT N'正在创建 索引 [dbo].[daily_tasks].[idx_tasks_date]...';


GO
CREATE NONCLUSTERED INDEX [idx_tasks_date]
    ON [dbo].[daily_tasks]([publish_date] ASC);


GO
PRINT N'正在创建 表 [dbo].[posts]...';


GO
CREATE TABLE [dbo].[posts] (
    [post_id]      INT            IDENTITY (1, 1) NOT NULL,
    [user_id]      INT            NOT NULL,
    [is_anonymous] BIT            NULL,
    [title]        NVARCHAR (100) NOT NULL,
    [content]      NVARCHAR (MAX) NOT NULL,
    [tags]         NVARCHAR (200) NULL,
    [post_time]    DATETIME       NULL,
    [is_newbie]    BIT            NULL,
    [status]       TINYINT        NULL,
    [view_count]   INT            NULL,
    PRIMARY KEY CLUSTERED ([post_id] ASC)
);


GO
PRINT N'正在创建 索引 [dbo].[posts].[idx_posts_status]...';


GO
CREATE NONCLUSTERED INDEX [idx_posts_status]
    ON [dbo].[posts]([status] ASC);


GO
PRINT N'正在创建 索引 [dbo].[posts].[idx_posts_time]...';


GO
CREATE NONCLUSTERED INDEX [idx_posts_time]
    ON [dbo].[posts]([post_time] ASC);


GO
PRINT N'正在创建 索引 [dbo].[posts].[idx_posts_user]...';


GO
CREATE NONCLUSTERED INDEX [idx_posts_user]
    ON [dbo].[posts]([user_id] ASC);


GO
PRINT N'正在创建 表 [dbo].[ratings]...';


GO
CREATE TABLE [dbo].[ratings] (
    [rating_id]     INT            IDENTITY (1, 1) NOT NULL,
    [post_id]       INT            NOT NULL,
    [user_id]       INT            NOT NULL,
    [tag_accuracy]  TINYINT        NULL,
    [article_score] TINYINT        NULL,
    [comment]       NVARCHAR (500) NULL,
    [rating_time]   DATETIME       NULL,
    PRIMARY KEY CLUSTERED ([rating_id] ASC),
    UNIQUE NONCLUSTERED ([post_id] ASC, [user_id] ASC)
);


GO
PRINT N'正在创建 索引 [dbo].[ratings].[idx_ratings_post]...';


GO
CREATE NONCLUSTERED INDEX [idx_ratings_post]
    ON [dbo].[ratings]([post_id] ASC);


GO
PRINT N'正在创建 表 [dbo].[replies]...';


GO
CREATE TABLE [dbo].[replies] (
    [reply_id]      INT             IDENTITY (1, 1) NOT NULL,
    [post_id]       INT             NOT NULL,
    [user_id]       INT             NOT NULL,
    [is_anonymous]  BIT             NULL,
    [anonymous_num] INT             NULL,
    [content]       NVARCHAR (1000) NOT NULL,
    [reply_time]    DATETIME        NULL,
    [status]        TINYINT         NULL,
    PRIMARY KEY CLUSTERED ([reply_id] ASC)
);


GO
PRINT N'正在创建 索引 [dbo].[replies].[idx_replies_post]...';


GO
CREATE NONCLUSTERED INDEX [idx_replies_post]
    ON [dbo].[replies]([post_id] ASC);


GO
PRINT N'正在创建 索引 [dbo].[replies].[idx_replies_user]...';


GO
CREATE NONCLUSTERED INDEX [idx_replies_user]
    ON [dbo].[replies]([user_id] ASC);


GO
PRINT N'正在创建 表 [dbo].[reports]...';


GO
CREATE TABLE [dbo].[reports] (
    [report_id]   INT            IDENTITY (1, 1) NOT NULL,
    [reporter_id] INT            NOT NULL,
    [target_type] TINYINT        NULL,
    [target_id]   INT            NOT NULL,
    [reason]      NVARCHAR (200) NULL,
    [report_time] DATETIME       NULL,
    [status]      TINYINT        NULL,
    [handler_id]  INT            NULL,
    [handle_time] DATETIME       NULL,
    [handle_note] NVARCHAR (200) NULL,
    PRIMARY KEY CLUSTERED ([report_id] ASC)
);


GO
PRINT N'正在创建 索引 [dbo].[reports].[idx_reports_status]...';


GO
CREATE NONCLUSTERED INDEX [idx_reports_status]
    ON [dbo].[reports]([status] ASC);


GO
PRINT N'正在创建 表 [dbo].[task_answers]...';


GO
CREATE TABLE [dbo].[task_answers] (
    [answer_id]   INT            IDENTITY (1, 1) NOT NULL,
    [task_id]     INT            NOT NULL,
    [user_id]     INT            NOT NULL,
    [content]     NVARCHAR (MAX) NOT NULL,
    [score]       INT            NULL,
    [submit_time] DATETIME       NULL,
    PRIMARY KEY CLUSTERED ([answer_id] ASC)
);


GO
PRINT N'正在创建 索引 [dbo].[task_answers].[idx_answers_task]...';


GO
CREATE NONCLUSTERED INDEX [idx_answers_task]
    ON [dbo].[task_answers]([task_id] ASC);


GO
PRINT N'正在创建 索引 [dbo].[task_answers].[idx_answers_score]...';


GO
CREATE NONCLUSTERED INDEX [idx_answers_score]
    ON [dbo].[task_answers]([score] ASC);


GO
PRINT N'正在创建 表 [dbo].[tips]...';


GO
CREATE TABLE [dbo].[tips] (
    [tip_id]        INT             IDENTITY (1, 1) NOT NULL,
    [from_user_id]  INT             NOT NULL,
    [to_user_id]    INT             NOT NULL,
    [post_id]       INT             NOT NULL,
    [amount]        DECIMAL (10, 2) NOT NULL,
    [platform_fee]  DECIMAL (10, 2) NOT NULL,
    [author_income] DECIMAL (10, 2) NOT NULL,
    [tip_time]      DATETIME        NULL,
    PRIMARY KEY CLUSTERED ([tip_id] ASC)
);


GO
PRINT N'正在创建 索引 [dbo].[tips].[idx_tips_to_user]...';


GO
CREATE NONCLUSTERED INDEX [idx_tips_to_user]
    ON [dbo].[tips]([to_user_id] ASC);


GO
PRINT N'正在创建 索引 [dbo].[tips].[idx_tips_post]...';


GO
CREATE NONCLUSTERED INDEX [idx_tips_post]
    ON [dbo].[tips]([post_id] ASC);


GO
PRINT N'正在创建 表 [dbo].[users]...';


GO
CREATE TABLE [dbo].[users] (
    [user_id]         INT            IDENTITY (1, 1) NOT NULL,
    [phone]           VARCHAR (11)   NOT NULL,
    [nickname]        NVARCHAR (20)  NOT NULL,
    [avatar_url]      NVARCHAR (500) NULL,
    [password_hash]   VARCHAR (128)  NULL,
    [is_vip]          BIT            NULL,
    [vip_expire_date] DATETIME       NULL,
    [warning_count]   INT            NULL,
    [is_banned]       BIT            NULL,
    [register_time]   DATETIME       NULL,
    [last_login_time] DATETIME       NULL,
    PRIMARY KEY CLUSTERED ([user_id] ASC),
    UNIQUE NONCLUSTERED ([nickname] ASC),
    UNIQUE NONCLUSTERED ([phone] ASC)
);


GO
PRINT N'正在创建 索引 [dbo].[users].[idx_users_phone]...';


GO
CREATE NONCLUSTERED INDEX [idx_users_phone]
    ON [dbo].[users]([phone] ASC);


GO
PRINT N'正在创建 索引 [dbo].[users].[idx_users_nickname]...';


GO
CREATE NONCLUSTERED INDEX [idx_users_nickname]
    ON [dbo].[users]([nickname] ASC);


GO
PRINT N'正在创建 默认约束 [dbo].[admins] 上未命名的约束...';


GO
ALTER TABLE [dbo].[admins]
    ADD DEFAULT 0 FOR [is_vip];


GO
PRINT N'正在创建 默认约束 [dbo].[admins] 上未命名的约束...';


GO
ALTER TABLE [dbo].[admins]
    ADD DEFAULT 0 FOR [warning_count];


GO
PRINT N'正在创建 默认约束 [dbo].[admins] 上未命名的约束...';


GO
ALTER TABLE [dbo].[admins]
    ADD DEFAULT 0 FOR [is_banned];


GO
PRINT N'正在创建 默认约束 [dbo].[admins] 上未命名的约束...';


GO
ALTER TABLE [dbo].[admins]
    ADD DEFAULT GETDATE() FOR [register_time];


GO
PRINT N'正在创建 默认约束 [dbo].[daily_tasks] 上未命名的约束...';


GO
ALTER TABLE [dbo].[daily_tasks]
    ADD DEFAULT 1 FOR [is_active];


GO
PRINT N'正在创建 默认约束 [dbo].[posts] 上未命名的约束...';


GO
ALTER TABLE [dbo].[posts]
    ADD DEFAULT 0 FOR [is_anonymous];


GO
PRINT N'正在创建 默认约束 [dbo].[posts] 上未命名的约束...';


GO
ALTER TABLE [dbo].[posts]
    ADD DEFAULT GETDATE() FOR [post_time];


GO
PRINT N'正在创建 默认约束 [dbo].[posts] 上未命名的约束...';


GO
ALTER TABLE [dbo].[posts]
    ADD DEFAULT 0 FOR [is_newbie];


GO
PRINT N'正在创建 默认约束 [dbo].[posts] 上未命名的约束...';


GO
ALTER TABLE [dbo].[posts]
    ADD DEFAULT 0 FOR [status];


GO
PRINT N'正在创建 默认约束 [dbo].[posts] 上未命名的约束...';


GO
ALTER TABLE [dbo].[posts]
    ADD DEFAULT 0 FOR [view_count];


GO
PRINT N'正在创建 默认约束 [dbo].[ratings] 上未命名的约束...';


GO
ALTER TABLE [dbo].[ratings]
    ADD DEFAULT GETDATE() FOR [rating_time];


GO
PRINT N'正在创建 默认约束 [dbo].[replies] 上未命名的约束...';


GO
ALTER TABLE [dbo].[replies]
    ADD DEFAULT 0 FOR [is_anonymous];


GO
PRINT N'正在创建 默认约束 [dbo].[replies] 上未命名的约束...';


GO
ALTER TABLE [dbo].[replies]
    ADD DEFAULT GETDATE() FOR [reply_time];


GO
PRINT N'正在创建 默认约束 [dbo].[replies] 上未命名的约束...';


GO
ALTER TABLE [dbo].[replies]
    ADD DEFAULT 0 FOR [status];


GO
PRINT N'正在创建 默认约束 [dbo].[reports] 上未命名的约束...';


GO
ALTER TABLE [dbo].[reports]
    ADD DEFAULT GETDATE() FOR [report_time];


GO
PRINT N'正在创建 默认约束 [dbo].[reports] 上未命名的约束...';


GO
ALTER TABLE [dbo].[reports]
    ADD DEFAULT 0 FOR [status];


GO
PRINT N'正在创建 默认约束 [dbo].[task_answers] 上未命名的约束...';


GO
ALTER TABLE [dbo].[task_answers]
    ADD DEFAULT 0 FOR [score];


GO
PRINT N'正在创建 默认约束 [dbo].[task_answers] 上未命名的约束...';


GO
ALTER TABLE [dbo].[task_answers]
    ADD DEFAULT GETDATE() FOR [submit_time];


GO
PRINT N'正在创建 默认约束 [dbo].[tips] 上未命名的约束...';


GO
ALTER TABLE [dbo].[tips]
    ADD DEFAULT GETDATE() FOR [tip_time];


GO
PRINT N'正在创建 默认约束 [dbo].[users] 上未命名的约束...';


GO
ALTER TABLE [dbo].[users]
    ADD DEFAULT 0 FOR [is_vip];


GO
PRINT N'正在创建 默认约束 [dbo].[users] 上未命名的约束...';


GO
ALTER TABLE [dbo].[users]
    ADD DEFAULT 0 FOR [warning_count];


GO
PRINT N'正在创建 默认约束 [dbo].[users] 上未命名的约束...';


GO
ALTER TABLE [dbo].[users]
    ADD DEFAULT 0 FOR [is_banned];


GO
PRINT N'正在创建 默认约束 [dbo].[users] 上未命名的约束...';


GO
ALTER TABLE [dbo].[users]
    ADD DEFAULT GETDATE() FOR [register_time];


GO
PRINT N'正在创建 外键 [dbo].[anonymous_mapping] 上未命名的约束...';


GO
ALTER TABLE [dbo].[anonymous_mapping] WITH NOCHECK
    ADD FOREIGN KEY ([post_id]) REFERENCES [dbo].[posts] ([post_id]);


GO
PRINT N'正在创建 外键 [dbo].[anonymous_mapping] 上未命名的约束...';


GO
ALTER TABLE [dbo].[anonymous_mapping] WITH NOCHECK
    ADD FOREIGN KEY ([user_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'正在创建 外键 [dbo].[posts] 上未命名的约束...';


GO
ALTER TABLE [dbo].[posts] WITH NOCHECK
    ADD FOREIGN KEY ([user_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'正在创建 外键 [dbo].[ratings] 上未命名的约束...';


GO
ALTER TABLE [dbo].[ratings] WITH NOCHECK
    ADD FOREIGN KEY ([post_id]) REFERENCES [dbo].[posts] ([post_id]);


GO
PRINT N'正在创建 外键 [dbo].[ratings] 上未命名的约束...';


GO
ALTER TABLE [dbo].[ratings] WITH NOCHECK
    ADD FOREIGN KEY ([user_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'正在创建 外键 [dbo].[replies] 上未命名的约束...';


GO
ALTER TABLE [dbo].[replies] WITH NOCHECK
    ADD FOREIGN KEY ([post_id]) REFERENCES [dbo].[posts] ([post_id]);


GO
PRINT N'正在创建 外键 [dbo].[replies] 上未命名的约束...';


GO
ALTER TABLE [dbo].[replies] WITH NOCHECK
    ADD FOREIGN KEY ([user_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'正在创建 外键 [dbo].[reports] 上未命名的约束...';


GO
ALTER TABLE [dbo].[reports] WITH NOCHECK
    ADD FOREIGN KEY ([reporter_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'正在创建 外键 [dbo].[task_answers] 上未命名的约束...';


GO
ALTER TABLE [dbo].[task_answers] WITH NOCHECK
    ADD FOREIGN KEY ([task_id]) REFERENCES [dbo].[daily_tasks] ([task_id]);


GO
PRINT N'正在创建 外键 [dbo].[task_answers] 上未命名的约束...';


GO
ALTER TABLE [dbo].[task_answers] WITH NOCHECK
    ADD FOREIGN KEY ([user_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'正在创建 外键 [dbo].[tips] 上未命名的约束...';


GO
ALTER TABLE [dbo].[tips] WITH NOCHECK
    ADD FOREIGN KEY ([from_user_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'正在创建 外键 [dbo].[tips] 上未命名的约束...';


GO
ALTER TABLE [dbo].[tips] WITH NOCHECK
    ADD FOREIGN KEY ([to_user_id]) REFERENCES [dbo].[users] ([user_id]);


GO
PRINT N'正在创建 外键 [dbo].[tips] 上未命名的约束...';


GO
ALTER TABLE [dbo].[tips] WITH NOCHECK
    ADD FOREIGN KEY ([post_id]) REFERENCES [dbo].[posts] ([post_id]);


GO
PRINT N'正在创建 CHECK 约束 [dbo].[ratings] 上未命名的约束...';


GO
ALTER TABLE [dbo].[ratings] WITH NOCHECK
    ADD CHECK (tag_accuracy BETWEEN 1 AND 5);


GO
PRINT N'正在创建 CHECK 约束 [dbo].[ratings] 上未命名的约束...';


GO
ALTER TABLE [dbo].[ratings] WITH NOCHECK
    ADD CHECK (article_score BETWEEN 1 AND 5);


GO
PRINT N'根据新创建的约束检查现有的数据';


GO
USE [$(DatabaseName)];


GO
CREATE TABLE [#__checkStatus] (
    id           INT            IDENTITY (1, 1) PRIMARY KEY CLUSTERED,
    [Schema]     NVARCHAR (256),
    [Table]      NVARCHAR (256),
    [Constraint] NVARCHAR (256)
);

SET NOCOUNT ON;

DECLARE tableconstraintnames CURSOR LOCAL FORWARD_ONLY
    FOR SELECT SCHEMA_NAME([schema_id]),
               OBJECT_NAME([parent_object_id]),
               [name],
               0
        FROM   [sys].[objects]
        WHERE  [parent_object_id] IN (OBJECT_ID(N'dbo.anonymous_mapping'), OBJECT_ID(N'dbo.posts'), OBJECT_ID(N'dbo.ratings'), OBJECT_ID(N'dbo.replies'), OBJECT_ID(N'dbo.reports'), OBJECT_ID(N'dbo.task_answers'), OBJECT_ID(N'dbo.tips'))
               AND [type] IN (N'F', N'C')
                   AND [object_id] IN (SELECT [object_id]
                                       FROM   [sys].[check_constraints]
                                       WHERE  [is_not_trusted] <> 0
                                              AND [is_disabled] = 0
                                       UNION
                                       SELECT [object_id]
                                       FROM   [sys].[foreign_keys]
                                       WHERE  [is_not_trusted] <> 0
                                              AND [is_disabled] = 0);

DECLARE @schemaname AS NVARCHAR (256);

DECLARE @tablename AS NVARCHAR (256);

DECLARE @checkname AS NVARCHAR (256);

DECLARE @is_not_trusted AS INT;

DECLARE @statement AS NVARCHAR (1024);

BEGIN TRY
    OPEN tableconstraintnames;
    FETCH tableconstraintnames INTO @schemaname, @tablename, @checkname, @is_not_trusted;
    WHILE @@fetch_status = 0
        BEGIN
            PRINT N'检查约束:' + @checkname + N' [' + @schemaname + N'].[' + @tablename + N']';
            SET @statement = N'ALTER TABLE [' + @schemaname + N'].[' + @tablename + N'] WITH ' + CASE @is_not_trusted WHEN 0 THEN N'CHECK' ELSE N'NOCHECK' END + N' CHECK CONSTRAINT [' + @checkname + N']';
            BEGIN TRY
                EXECUTE [sp_executesql] @statement;
            END TRY
            BEGIN CATCH
                INSERT  [#__checkStatus] ([Schema], [Table], [Constraint])
                VALUES                  (@schemaname, @tablename, @checkname);
            END CATCH
            FETCH tableconstraintnames INTO @schemaname, @tablename, @checkname, @is_not_trusted;
        END
END TRY
BEGIN CATCH
    PRINT ERROR_MESSAGE();
END CATCH

IF CURSOR_STATUS(N'LOCAL', N'tableconstraintnames') >= 0
    CLOSE tableconstraintnames;

IF CURSOR_STATUS(N'LOCAL', N'tableconstraintnames') = -1
    DEALLOCATE tableconstraintnames;

SELECT N'约束验证失败:' + [Schema] + N'.' + [Table] + N',' + [Constraint]
FROM   [#__checkStatus];

IF @@ROWCOUNT > 0
    BEGIN
        DROP TABLE [#__checkStatus];
        RAISERROR (N'验证约束时出错', 16, 127);
    END

SET NOCOUNT OFF;

DROP TABLE [#__checkStatus];


GO
PRINT N'更新完成。';


GO
