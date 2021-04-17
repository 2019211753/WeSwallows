# WeSwallows
2020冬季雏燕项目——基于springboot的校内信息交互系统

## 项目设计

### 总体设计

- **本项目用到的技术和框架**<br>

|  工具 | 名称 
| ------------ | ------------
| 开发工具  | IDEA、vscode
| 项目构建 | Maven
| UI框架 | layui、semantic UI
|  语言 | JDK1.8、HTML、css、js
| 内容展示 | wangEditor.md
| 数据库  | Mysql5.6
| 项目框架  | SSH
| ORM  | JPA
| 安全框架  | JWT
| 运行环境  | 腾讯云Centos7

## API

### 使用前

#### 注册

POST http://localhost:8080/register

#### 登录

POST http://localhost:8080/login

### 首页

#### 所有问题和推荐问题

GET http://localhost:8080/

#### 查询问题

POST http://localhost:8080/searchQuestions

#### 点赞

GET http://localhost:8080/question/{questionId}/approve

#### 点踩

GET http://localhost:8080/question/{questionId}/disapprove

### 标签页

#### 第一级标签

GET http://localhost:8080/tags

#### 按标签搜索

POST http://localhost:8080/tags/searchQuestions

### 排行榜

#### 获取用户榜单

GET http://localhost:8080/rank

### 问题页

#### 问题内容

GET http://localhost:8080/question/{questionId}

#### 获取评论

GET http://localhost:8080/question/{questionId}/comments

#### 发布评论

POST http://localhost:8080/question/{questionId}/comments

#### 点赞

GET http://localhost:8080/question/{questionId}/comment/{commentId}/approve

#### 点踩

GET http://localhost:8080/question/{questionId}/comment/{commentId}/disapprove

#### 删除评论

GET http://localhost:8080/question/{questionId}/comment/{commentId}/delete

### 个人主页

#### 问题部分

##### 个人发布的问题

GET http://localhost:8080/customer/questions

##### 新增问题

POST http://localhost:8080/customer/questions

##### 查询问题

POST http://localhost:8080/customer/questions/search

##### 删除问题

GET http://localhost:8080/customer/question/{questionId}/delete

##### 问题归档

GET http://localhost:8080/customer/archives

#### 个人信息

##### 个人信息

GET http://localhost:8080/customer/personal

##### 个人资料修改

POST http://localhost:8080/customer/modifyAll

##### 头像修改

POST http://localhost:8080/customer/uploadAvatar

#### 个人消息

##### 评论和点赞

GET http://localhost:8080/customer/messages

##### 已读某评论

GET http://localhost:8080/customer/messages/{commentId}/read

##### 已读某点赞

GET http://localhost:8080/customer/messages/{likesId}/read

##### 已读所有评论

GET http://localhost:8080/customer/messages/readAllComments

##### 已读所有点赞

GET http://localhost:8080/customer/messages/readAllLikes

### 管理员页

#### 搜索用户

POST http://localhost:8080/admin/searchUser

#### 禁言

GET http://localhost:8080/admin/controlSpeak/{userId}

#### 新增标签

POST http://localhost:8080/admin/tags/input

#### 删除标签

GET http://localhost:8080/admin/tags/{tagId}/delete

#### 删除问题

GET http://localhost:8080/admin/searchQuestions

#### 编辑问题

GET http://localhost:8080/admin/question/{questionId}/edit

POST http://localhost:8080/admin/question

#### 删除问题

GET http://localhost:8080/admin/question/{questionId}/delete