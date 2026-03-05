# 咖啡点单系统

## 项目概述
这是一个基于Spring Boot + Vue.js的咖啡点单系统，提供完整的咖啡订购流程，包括用户注册/登录、咖啡浏览、购物车管理、订单处理等功能。

## 技术栈
- 后端：Spring Boot 4.0.3, MyBatis, MySQL
- 前端：Vue 3, Element Plus, Axios, Vue Router
- 构建工具：Maven, Vite

## 功能特性
### 用户功能
- 用户注册/登录
- 个人信息管理
- 查看历史订单

### 咖啡功能
- 浏览咖啡菜单
- 按类别筛选咖啡
- 查看咖啡详情

### 订单功能
- 添加商品到购物车
- 调整商品规格（甜度、温度）
- 提交订单
- 订单状态跟踪

## 项目结构

### 后端结构
```
Coffee-Order-System/
├── src/main/java/com/lihao/CoffeeOrderSystem/
│   ├── Controller/          # 控制器层
│   ├── Service/             # 业务逻辑层接口
│   ├── Service/impl/        # 业务逻辑层实现
│   ├── mapper/              # 数据访问层
│   ├── entity/              # 实体类
│   ├── utils/               # 工具类
│   └── config/              # 配置类
├── src/main/resources/
│   ├── mapper/              # MyBatis XML映射文件
│   └── application.properties # 配置文件
```

### 前端结构
```
coffee-order-system/
├── src/
│   ├── views/               # 页面组件
│   ├── router/              # 路由配置
│   ├── components/          # 公共组件
│   └── assets/              # 静态资源
```

## 快速开始

### 环境要求
- Node.js >= 20.19.0 或 >= 22.12.0
- Java 25
- Maven
- MySQL

### 后端启动
1. 创建数据库并导入表结构
2. 修改 `application.properties` 中的数据库连接信息
3. 运行以下命令：
```bash
cd back/Coffee-Order-System
mvn spring-boot:run
```

### 前端启动
1. 安装依赖：
```bash
cd front/coffee-order-system
npm install
```
2. 启动开发服务器：
```bash
npm run dev
```

### 访问应用
- 前端地址：http://localhost:5173
- 后端API地址：http://localhost:8080
- API文档：http://localhost:8080/swagger-ui.html (如果集成)

## API 接口

### 用户相关
- POST `/api/user/login` - 用户登录
- POST `/api/user/register` - 用户注册
- GET `/api/user/{id}` - 获取用户信息
- PUT `/api/user/update` - 更新用户信息

### 咖啡相关
- GET `/api/coffee/list` - 获取所有咖啡
- GET `/api/coffee/{id}` - 获取咖啡详情
- GET `/api/coffee/category/{category}` - 按分类获取咖啡
- GET `/api/coffee/recommended` - 获取推荐咖啡

### 购物车相关
- GET `/api/cart/list/{userId}` - 获取用户购物车
- POST `/api/cart/add` - 添加到购物车
- PUT `/api/cart/updateQuantity` - 更新购物车商品数量
- DELETE `/api/cart/delete/{id}` - 删除购物车商品

### 订单相关
- POST `/api/order/create` - 创建订单
- GET `/api/order/list/{userId}` - 获取用户订单列表
- GET `/api/order/{id}` - 获取订单详情
- PUT `/api/order/updateStatus` - 更新订单状态
- GET `/api/order/byOrderNo/{orderNo}` - 根据订单号获取订单

## 数据库设计

### 用户表 (user)
- id: 主键
- username: 用户名
- password: 密码
- phone: 手机号
- gender: 性别
- role: 角色 (1:顾客, 2:店员, 3:管理员)
- register_time: 注册时间
- head_image: 头像

### 咖啡表 (coffee)
- id: 主键
- name: 咖啡名称
- price: 价格
- coffee_image: 图片
- description: 描述
- category: 分类
- stock: 库存
- status: 状态 (1:上架, 2:下架)
- recommend: 是否推荐 (1:推荐, 2:不推荐)
- sales: 销量

### 购物车表 (cart)
- id: 主键
- user_id: 用户ID
- coffee_id: 咖啡ID
- coffee_name: 咖啡名称
- coffee_image: 咖啡图片
- price: 价格
- quantity: 数量
- sweet: 甜度
- temperature: 温度

### 订单表 (order)
- id: 主键
- order_no: 订单号
- user_id: 用户ID
- total_amount: 总金额
- status: 状态 (1:待接单, 2:制作中, 3:已完成, 4:已取消)
- remark: 备注
- take_code: 取餐码
- order_time: 下单时间

### 订单详情表 (order_item)
- id: 主键
- order_id: 订单ID
- coffee_id: 咖啡ID
- coffee_name: 咖啡名称
- coffee_image: 咖啡图片
- price: 单价
- quantity: 数量
- total_price: 小计
- create_time: 创建时间
- sweet: 甜度
- temperature: 温度

## 开发说明
本项目采用前后端分离架构，前端通过Axios与后端API进行数据交互。后端提供RESTful API接口，返回JSON格式数据。

## 注意事项
- 请确保数据库服务已启动
- 确保前后端端口未被占用
- 根据实际环境修改数据库连接配置