create table tb_user(
id BIGINT not null primary key AUTO_INCREMENT comment '用户ID',
username varchar(128) not null UNIQUE key comment '用户名',
phone varchar(64) not null unique key COMMENT '注册手机号',
password varchar(255) default null comment '密码',
nickname varchar(32) not null comment '昵称',
icon varchar(255) default null comment '头像',
user_type int(1) not null default 1 comment '用户类型，0：管理员，1：用户',
isUpdate int(1) not null default 0 comment '是否已修改账号，0：否，1：是',
isPassword int(1) not null default 0 comment '是否为默认密码，0：否，1：是',
status int(1) not null default 0 comment '账号状态，0：正常，1：停用',
create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '上次登录时间',
isDel int(1) not null default 0 comment '是否删除，0：否，1：是'
)comment '用户表';
 
create table tb_role(
id BIGINT not null primary key AUTO_INCREMENT comment 'ID',
name varchar(128) not null unique key comment '角色名称',
perms varchar(128) not null unique key comment '角色标识',
status int(1) not null default 1 comment '角色状态，0：禁用，1：可用',
remark varchar(128) default null comment '备注',
create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间'
)comment '角色表';

create table tb_user_role(
user_id BIGINT not null comment '用户ID',
role_id bigint not null comment '角色ID',
primary key (user_id,role_id)
)comment '用户角色表';

create table tb_auth(
id BIGINT not null primary key AUTO_INCREMENT comment 'ID',
name varchar(128) not null unique key comment '权限名称',
perms varchar(128) not null unique key comment '权限标识',
status int(1) not null default 1 comment '权限状态，0：禁用，1：可用',
remark varchar(128) default null comment '备注',
create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间'
)comment '权限表';  

create table tb_role_auth(
role_id BIGINT not null comment '角色ID',
auth_id bigint not null comment '权限ID',
primary key (auth_id,role_id)
)comment '角色权限表';

CREATE TABLE tb_user_info  (
id bigint NOT NULL primary key AUTO_INCREMENT COMMENT '用户id',
full_name varchar(255) COMMENT '姓名',
gender char(2)  NULL DEFAULT '保密' COMMENT '性别',
age int NULL DEFAULT NULL COMMENT '年龄',
email varchar(255)  NULL DEFAULT NULL COMMENT '电子邮箱',
birthday date NULL DEFAULT NULL COMMENT '生日',
qq varchar(52)  NULL DEFAULT NULL COMMENT 'QQ',
level int NOT NULL DEFAULT 0 COMMENT '会员级别，0-9，0代表未开通会员'
)comment '用户信息表';

CREATE TABLE tb_user_account  (
id bigint NOT NULL  primary key AUTO_INCREMENT  COMMENT 'id',
balance double(10, 2) NOT NULL DEFAULT 0.00 COMMENT '账户余额',
points bigint NOT NULL DEFAULT 0 COMMENT '账户积分',
recharge double(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计充值',
spending double(10, 2) NOT NULL DEFAULT 0.00 COMMENT '累计消费'
)comment '用户钱包表';

CREATE TABLE `tb_recharge_combo` (
  id bigint NOT NULL primary key AUTO_INCREMENT COMMENT 'id',
  name varchar(255) NOT NULL COMMENT '套餐名称',
  price double(10,2) NOT NULL DEFAULT '0.00' COMMENT '充值金额',
  points bigint NOT NULL DEFAULT '0' COMMENT '赠送积分',
  discount double(3,2) NOT NULL DEFAULT '1.00' COMMENT '折扣'
)comment '充值套餐表';

CREATE TABLE tb_user_address  (
id bigint NOT NULL primary key AUTO_INCREMENT COMMENT 'id',
uid bigint NOT NULL COMMENT '用户id',
name varchar(255)  NOT NULL COMMENT '收货人姓名',
phone varchar(64)  NOT NULL COMMENT '联系电话',
province varchar(128)  NOT NULL COMMENT '省份',
city varchar(128)  NOT NULL COMMENT '市',
district varchar(255)  NOT NULL COMMENT '区/县',
address varchar(255)  NOT NULL COMMENT '详细收货地址',
isDefault int NOT NULL DEFAULT 0 COMMENT '是否为默认收货地址，0：否，1：是',
FOREIGN KEY (uid) REFERENCES tb_user (id)
)comment '收货地址表';

CREATE TABLE tb_goods_category  (
  id bigint NOT NULL primary key AUTO_INCREMENT COMMENT '商品分类id',
  name varchar(128) NOT NULL COMMENT '分类名称',
  icon varchar(128) NULL DEFAULT NULL COMMENT '分类图标',
  fid bigint NULL DEFAULT NULL COMMENT '父类id',
  remark varchar(255) NULL DEFAULT NULL COMMENT '备注'
)comment '商品分类表';

CREATE TABLE tb_goods  (
id bigint NOT NULL primary key AUTO_INCREMENT COMMENT '商品id',
name varchar(255) NOT NULL COMMENT '商品名称',
cid bigint NOT NULL COMMENT '商品分类',
images varchar(1024) NULL DEFAULT NULL COMMENT '商品宣传封面，多个图片以,隔开，最多三张',
province varchar(128) NOT NULL COMMENT '省份',
city varchar(128) NOT NULL COMMENT '市',
district varchar(255) NOT NULL COMMENT '区/县',
address varchar(255) NOT NULL COMMENT '详细发货地址',
postage double(7, 2) NOT NULL DEFAULT 0.00 COMMENT '商品邮费',
recommend int NOT NULL DEFAULT 0 COMMENT '推荐商品，0：不推荐，1：推荐',
monthSale bigint NOT NULL DEFAULT 0 COMMENT '商品月销量',
warranty_time int NOT NULL DEFAULT 0 COMMENT '保修期，单位：天',
refund_time int NOT NULL DEFAULT 0 COMMENT '包退有效期，单位：天',
changer_time int NOT NULL DEFAULT 0 COMMENT '包换有效期，单位：天',
status int NOT NULL DEFAULT 0 COMMENT '商品状态，0：未上架，1：已上架，2：已下架',
shelves_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
isDel int NOT NULL DEFAULT 0 COMMENT '是否删除，0：否，1：是',
FOREIGN KEY (cid) REFERENCES tb_goods_category (id)
)comment '商品表';

CREATE TABLE tb_goods_item(
  id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '商品属性id',
  gid bigint NOT NULL COMMENT '商品id',
  icon varchar(255) NOT NULL COMMENT '商品属性图标',
  color varchar(255) NOT NULL COMMENT '商品属性颜色',
  combo varchar(255) NULL DEFAULT NULL COMMENT '商品属性套餐',
  size varchar(255) NULL DEFAULT NULL COMMENT '商品属性尺寸',
  edition varchar(255) NULL DEFAULT NULL COMMENT '商品属性版本',
  price double(10, 2) NOT NULL DEFAULT 0.00 COMMENT '商品属性价格',
  discount float(3, 2) NOT NULL DEFAULT 1.00 COMMENT '商品属性折扣',
  stock bigint NOT NULL DEFAULT 1 COMMENT '商品属性库存',
  sales bigint NOT NULL DEFAULT 0 COMMENT '商品属性销量',
  remark varchar(255) DEFAULT NULL COMMENT '商品属性备注',
  status int NOT NULL DEFAULT 0  COMMENT '商品属性状态，0：未上架，1：已上架', 
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
	FOREIGN KEY (gid) REFERENCES tb_goods (id)
)comment '商品属性表';

CREATE TABLE tb_shopping_cart  (
  id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '购物车id',
  uid bigint NOT NULL COMMENT '用户id',
  gid bigint NOT NULL COMMENT '商品属性id',
  quantity int NOT NULL DEFAULT 1 COMMENT '数量',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  FOREIGN KEY (uid) REFERENCES tb_user (id),
  FOREIGN KEY (gid) REFERENCES tb_goods_item (id)
)comment '购物车表';

CREATE TABLE tb_order  (
  id bigint NOT NULL primary key COMMENT '订单号',
  uid bigint NOT NULL COMMENT '下单用户id',
  time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  price double(10, 2) NOT NULL COMMENT '订单金额',
  remarks varchar(255) NULL DEFAULT NULL COMMENT '订单备注',
  name varchar(255) NOT NULL COMMENT '收货人姓名',
  phone varchar(64) NOT NULL COMMENT '联系电话',
  province varchar(128) NOT NULL COMMENT '省份',
  city varchar(128) NOT NULL COMMENT '市',
  district varchar(255) NOT NULL COMMENT '区/县',
  address varchar(255) NOT NULL COMMENT '详细收货地址',
  postage double(7, 2) NOT NULL DEFAULT 0.00 COMMENT '运费',
  isPay int NOT NULL DEFAULT 0 COMMENT '是否付款，0：否，1：是',
  payment_methods int NULL DEFAULT NULL COMMENT '付款方式，0：钱包支付，1：微信，2：支付宝',
  logistics_status int NOT NULL DEFAULT 0 COMMENT '发货状态，0未发货，1：待收货，2：已收货，3：待换货，4：已换货，5：待退货，6：已退货，',
  shipping_time timestamp NULL DEFAULT NULL COMMENT '发货时间',
  return_time timestamp NULL DEFAULT NULL COMMENT '退货时间',
  status int NOT NULL DEFAULT 0 COMMENT '订单状态，0：未完成，1：已完成，2：待退款，3：已退款',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  isDel int NOT NULL DEFAULT 0 COMMENT '是否删除，0：否，1：是',
  FOREIGN KEY (uid) REFERENCES tb_user (id)
)comment '订单表';

CREATE TABLE tb_order_item  (
  id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT 'id',
  order_id bigint NOT NULL COMMENT '订单号',
  gid bigint NOT NULL COMMENT '商品属性id',
  quantity int NOT NULL COMMENT '下单数量',
	unit_price double(10, 2) NOT NULL COMMENT '单价',
  price double(10, 2) NOT NULL COMMENT '总价格',
  FOREIGN KEY (gid) REFERENCES tb_goods_item (id),
  FOREIGN KEY (order_id) REFERENCES tb_order (id)
)comment '订单详情表';

CREATE TABLE tb_goods_evaluation (
  id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '评价id',
  uid bigint not null comment '评价用户id',
  order_id bigint NOT NULL COMMENT '订单号',
  goods_id bigint NOT NULL COMMENT '商品id',
  goodsItem_id bigint NOT NULL COMMENT '商品属性id',
  stars int not null default 1 comment '评价等级',
  images varchar(1024) NULL DEFAULT NULL COMMENT '评论图片，多个图片以,隔开，最多三张',
  content longtext NOT NULL COMMENT '评论内容',
  time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  liked int NOT NULL DEFAULT 0 COMMENT '点赞数量',
  status int NOT NULL DEFAULT 0 COMMENT '评论状态，0：正常，1：违规，2：隐藏/屏蔽',
  FOREIGN KEY (uid) REFERENCES tb_user (id),
  FOREIGN KEY (order_id) REFERENCES tb_order (id),
  FOREIGN KEY (goods_id) REFERENCES tb_goods (id),
  FOREIGN KEY (goodsItem_id) REFERENCES tb_goods_item (id)
)comment '商品评价表';

CREATE TABLE tb_evaluation_comment (
  id bigint NOT NULL PRIMARY KEY AUTO_INCREMENT COMMENT '评论id',
  uid bigint not null comment '评论用户id',
  evaluation_id bigint NOT NULL COMMENT '评价id',
  content longtext NOT NULL COMMENT '评论内容',
  time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  liked int NOT NULL DEFAULT 0 COMMENT '点赞数量',
  status int NOT NULL DEFAULT 0 COMMENT '评论状态，0：正常，1：违规，2：隐藏/屏蔽',
  fid bigint COMMENT '评论父id',
  FOREIGN KEY (evaluation_id) REFERENCES tb_goods_evaluation (id)
)COMMENT '评价评论表';

CREATE TABLE tb_goods_collection  (
  uid bigint NOT NULL COMMENT '用户id',
  gid bigint NOT NULL COMMENT '商品id',
  primary key (uid,gid)
)comment '商品收藏表';

CREATE TABLE tb_events (
  id bigint primary key NOT NULL AUTO_INCREMENT COMMENT '活动id',
  name varchar(255) not null comment '活动名称',
  icon varchar(255) not null comment '活动图片',
  start_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '开始时间',
  deadline int not null default 1 comment '活动期限，单位天数，-1：无限期',
  remark varchar(255) comment '活动备注',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间'
)comment '活动事件表'；

CREATE TABLE tb_events_goods (
  events_id bigint not null comment '活动id',
  goods_id bigint not null comment '活动商品id'，
  primary key (events_id, goods_id)
)comment '活动商品表';

CREATE TABLE tb_notice_category (
  id bigint NOT NULL primary key AUTO_INCREMENT COMMENT '公告类型id',
  name varchar(128) NOT NULL COMMENT '公告类型名称',
  fid bigint NULL DEFAULT NULL COMMENT '公告类型父id',
  remark varchar(255) NULL DEFAULT NULL COMMENT '公告类型备注'
)comment '公告类型表';

CREATE TABLE tb_notice (
  id bigint primary key NOT NULL AUTO_INCREMENT COMMENT '公告id',
  title varchar(255) not null comment '公告标题',
  cid bigint not null comment '公告类型',
  content longtext not null comment '公告内容',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
  FOREIGN KEY (cid) REFERENCES tb_notice_category (id)
)comment '公告表';

# 向权限表添加数据
INSERT INTO `tb_auth` VALUES (1, '查询用户', 'user:query', 1, NULL, '2022-10-04 16:59:56', '2022-10-04 17:00:43');
INSERT INTO `tb_auth` VALUES (2, '新增用户', 'user:save', 1, NULL, '2022-10-04 17:00:35', '2022-10-04 17:00:35');
INSERT INTO `tb_auth` VALUES (3, '删除用户', 'user:delete', 1, NULL, '2022-10-04 17:01:00', '2022-10-04 17:01:00');
INSERT INTO `tb_auth` VALUES (4, '修改用户', 'user:update', 1, NULL, '2022-10-04 17:01:24', '2022-10-04 17:01:24');
INSERT INTO `tb_auth` VALUES (5, '查询用户信息', 'userinfo:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (6, '新增用户信息', 'userinfo:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (7, '删除用户信息', 'userinfo:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (8, '修改用户信息', 'userinfo:update', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (9, '查询用户钱包', 'account:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (10, '新增用户钱包', 'account:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (11, '删除用户钱包', 'account:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (12, '钱包充值', 'account:recharge', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (13, '查询收货地址', 'address:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (14, '新增收货地址', 'address:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (15, '删除收货地址', 'address:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (16, '修改收货地址', 'address:update', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (17, '查询购物车', 'shoppingcart:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (18, '新增购物车', 'shoppingcart:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (19, '删除购物车', 'shoppingcart:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (20, '修改购物车', 'shoppingcart:update', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (21, '查询用户收藏', 'collection:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (22, '新增用户收藏', 'collection:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (23, '删除用户收藏', 'collection:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (24, '修改用户收藏', 'collection:update', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (25, '查询用户推荐', 'commend:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (26, '新增用户推荐', 'commend:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (27, '删除用户推荐', 'commend:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (28, '修改用户推荐', 'commend:update', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (29, '提交订单', 'order:submit', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (30, '新增订单', 'order:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (31, '取消订单', 'order:cancel', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (32, '删除订单', 'order:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (33, '查询订单', 'order:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (34, '修改订单', 'order:update', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (35, '查询商品', 'goods:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (36, '新增商品', 'goods:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (37, '删除商品', 'goods:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (38, '修改商品', 'goods:update', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (39, '查询商品属性', 'goods:item:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (40, '新增商品属性', 'goods:item:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (41, '删除商品属性', 'goods:item:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (42, '修改商品属性', 'goods:item:update', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (43, '查询商品分类', 'goods:category:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (44, '新增商品分类', 'goods:category:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (45, '删除商品分类', 'goods:category:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (46, '修改商品分类', 'goods:category:update', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (47, '查询评论', 'comment:query', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (48, '新增评论', 'comment:save', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (49, '删除评论', 'comment:delete', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (50, '修改评论', 'comment:update', 1, NULL, '2022-10-04 17:43:08', '2022-10-04 17:43:08');
INSERT INTO `tb_auth` VALUES (51, '查询充值套餐', 'recharge:combo:query', 1, NULL, '2022-10-16 23:07:31', '2022-10-16 23:07:31');
INSERT INTO `tb_auth` VALUES (52, '新增充值套餐', 'recharge:combo:save', 1, NULL, '2022-10-16 23:07:31', '2022-10-16 23:07:31');
INSERT INTO `tb_auth` VALUES (53, '删除充值套餐', 'recharge:combo:delete', 1, NULL, '2022-10-16 23:07:31', '2022-10-16 23:07:31');
INSERT INTO `tb_auth` VALUES (54, '修改充值套餐', 'recharge:combo:update', 1, NULL, '2022-10-16 23:07:31', '2022-10-16 23:07:31');
INSERT INTO `tb_auth` VALUES (55, '新增活动', 'events:save', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (56, '删除活动', 'events:delete', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (57, '修改活动', 'events:update', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (58, '新增活动商品', 'events:goods:save', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (59, '删除活动商品', 'events:goods:delete', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (60, '修改活动商品', 'events:goods:update', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (61, '查询权限', 'auth:query', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (62, '新增权限', 'auth:save', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (63, '删除权限', 'auth:delete', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (64, '修改权限', 'auth:update', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (65, '查询角色', 'role:query', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (66, '新增角色', 'role:save', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (67, '删除角色', 'role:delete', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (68, '修改角色', 'role:update', 1, NULL, '2022-10-29 23:00:04', '2022-10-29 23:00:04');
INSERT INTO `tb_auth` VALUES (69, '查询公告类型', 'notice:category:query', 1, NULL, '2022-10-30 15:41:12', '2022-10-30 15:41:12');
INSERT INTO `tb_auth` VALUES (70, '新增公告类型', 'notice:category:save', 1, NULL, '2022-10-30 15:41:12', '2022-10-30 15:41:12');
INSERT INTO `tb_auth` VALUES (71, '删除公告类型', 'notice:category:delete', 1, NULL, '2022-10-30 15:41:12', '2022-10-30 15:41:12');
INSERT INTO `tb_auth` VALUES (72, '修改公告类型', 'notice:category:update', 1, NULL, '2022-10-30 15:41:12', '2022-10-30 15:41:12');
INSERT INTO `tb_auth` VALUES (73, '查询公告', 'notice:query', 1, NULL, '2022-10-30 15:41:12', '2022-10-30 15:41:12');
INSERT INTO `tb_auth` VALUES (74, '新增公告', 'notice:save', 1, NULL, '2022-10-30 15:41:12', '2022-10-30 15:41:12');
INSERT INTO `tb_auth` VALUES (75, '删除公告', 'notice:delete', 1, NULL, '2022-10-30 15:41:12', '2022-10-30 15:41:12');
INSERT INTO `tb_auth` VALUES (76, '修改公告', 'notice:update', 1, NULL, '2022-10-30 15:41:12', '2022-10-30 15:41:12');

# 向角色表添加数据
insert into tb_role(name,perms,remark) values
('最高管理员','ROLE_ADMIN','拥有全部权限'),
('订单管理员','ROLE_ORDER_ADMIN',''),
('商品管理员','ROLE_GOODS_ADMIN',''),
('用户管理员','ROLE_USER_ADMIN',''),
('普通用户','ROLE_USER',''),
('一级违规用户','ROLE_OFFENDER_1','违规发言，禁言'),
('二级违规用户','ROLE_OFFENDER_2','违规发言，刷单，禁言，禁止下单'),
('三级违规用户','ROLE_OFFENDER_3','使用作弊软件，第三方违规行为，账号停用，禁用全部权限');

# 向角色权限表添加数据
insert into tb_role_auth values
INSERT INTO `tb_role_auth` VALUES (8, 0);
INSERT INTO `tb_role_auth` VALUES (1, 1);
INSERT INTO `tb_role_auth` VALUES (2, 1);
INSERT INTO `tb_role_auth` VALUES (4, 1);
INSERT INTO `tb_role_auth` VALUES (5, 1);
INSERT INTO `tb_role_auth` VALUES (6, 1);
INSERT INTO `tb_role_auth` VALUES (7, 1);
INSERT INTO `tb_role_auth` VALUES (1, 2);
INSERT INTO `tb_role_auth` VALUES (2, 2);
INSERT INTO `tb_role_auth` VALUES (4, 2);
INSERT INTO `tb_role_auth` VALUES (1, 3);
INSERT INTO `tb_role_auth` VALUES (2, 3);
INSERT INTO `tb_role_auth` VALUES (4, 3);
INSERT INTO `tb_role_auth` VALUES (1, 4);
INSERT INTO `tb_role_auth` VALUES (2, 4);
INSERT INTO `tb_role_auth` VALUES (4, 4);
INSERT INTO `tb_role_auth` VALUES (5, 4);
INSERT INTO `tb_role_auth` VALUES (6, 4);
INSERT INTO `tb_role_auth` VALUES (7, 4);
INSERT INTO `tb_role_auth` VALUES (1, 5);
INSERT INTO `tb_role_auth` VALUES (2, 5);
INSERT INTO `tb_role_auth` VALUES (4, 5);
INSERT INTO `tb_role_auth` VALUES (5, 5);
INSERT INTO `tb_role_auth` VALUES (6, 5);
INSERT INTO `tb_role_auth` VALUES (7, 5);
INSERT INTO `tb_role_auth` VALUES (1, 6);
INSERT INTO `tb_role_auth` VALUES (2, 6);
INSERT INTO `tb_role_auth` VALUES (4, 6);
INSERT INTO `tb_role_auth` VALUES (1, 7);
INSERT INTO `tb_role_auth` VALUES (2, 7);
INSERT INTO `tb_role_auth` VALUES (4, 7);
INSERT INTO `tb_role_auth` VALUES (1, 8);
INSERT INTO `tb_role_auth` VALUES (2, 8);
INSERT INTO `tb_role_auth` VALUES (4, 8);
INSERT INTO `tb_role_auth` VALUES (5, 8);
INSERT INTO `tb_role_auth` VALUES (6, 8);
INSERT INTO `tb_role_auth` VALUES (7, 8);
INSERT INTO `tb_role_auth` VALUES (1, 9);
INSERT INTO `tb_role_auth` VALUES (4, 9);
INSERT INTO `tb_role_auth` VALUES (5, 9);
INSERT INTO `tb_role_auth` VALUES (6, 9);
INSERT INTO `tb_role_auth` VALUES (7, 9);
INSERT INTO `tb_role_auth` VALUES (1, 10);
INSERT INTO `tb_role_auth` VALUES (4, 10);
INSERT INTO `tb_role_auth` VALUES (1, 11);
INSERT INTO `tb_role_auth` VALUES (4, 11);
INSERT INTO `tb_role_auth` VALUES (1, 12);
INSERT INTO `tb_role_auth` VALUES (4, 12);
INSERT INTO `tb_role_auth` VALUES (5, 12);
INSERT INTO `tb_role_auth` VALUES (6, 12);
INSERT INTO `tb_role_auth` VALUES (7, 12);
INSERT INTO `tb_role_auth` VALUES (1, 13);
INSERT INTO `tb_role_auth` VALUES (4, 13);
INSERT INTO `tb_role_auth` VALUES (5, 13);
INSERT INTO `tb_role_auth` VALUES (6, 13);
INSERT INTO `tb_role_auth` VALUES (7, 13);
INSERT INTO `tb_role_auth` VALUES (1, 14);
INSERT INTO `tb_role_auth` VALUES (4, 14);
INSERT INTO `tb_role_auth` VALUES (5, 14);
INSERT INTO `tb_role_auth` VALUES (6, 14);
INSERT INTO `tb_role_auth` VALUES (7, 14);
INSERT INTO `tb_role_auth` VALUES (1, 15);
INSERT INTO `tb_role_auth` VALUES (4, 15);
INSERT INTO `tb_role_auth` VALUES (5, 15);
INSERT INTO `tb_role_auth` VALUES (6, 15);
INSERT INTO `tb_role_auth` VALUES (7, 15);
INSERT INTO `tb_role_auth` VALUES (1, 16);
INSERT INTO `tb_role_auth` VALUES (4, 16);
INSERT INTO `tb_role_auth` VALUES (5, 16);
INSERT INTO `tb_role_auth` VALUES (6, 16);
INSERT INTO `tb_role_auth` VALUES (7, 16);
INSERT INTO `tb_role_auth` VALUES (1, 17);
INSERT INTO `tb_role_auth` VALUES (5, 17);
INSERT INTO `tb_role_auth` VALUES (6, 17);
INSERT INTO `tb_role_auth` VALUES (7, 17);
INSERT INTO `tb_role_auth` VALUES (1, 18);
INSERT INTO `tb_role_auth` VALUES (5, 18);
INSERT INTO `tb_role_auth` VALUES (6, 18);
INSERT INTO `tb_role_auth` VALUES (7, 18);
INSERT INTO `tb_role_auth` VALUES (1, 19);
INSERT INTO `tb_role_auth` VALUES (5, 19);
INSERT INTO `tb_role_auth` VALUES (6, 19);
INSERT INTO `tb_role_auth` VALUES (7, 19);
INSERT INTO `tb_role_auth` VALUES (1, 20);
INSERT INTO `tb_role_auth` VALUES (5, 20);
INSERT INTO `tb_role_auth` VALUES (6, 20);
INSERT INTO `tb_role_auth` VALUES (7, 20);
INSERT INTO `tb_role_auth` VALUES (1, 21);
INSERT INTO `tb_role_auth` VALUES (5, 21);
INSERT INTO `tb_role_auth` VALUES (6, 21);
INSERT INTO `tb_role_auth` VALUES (7, 21);
INSERT INTO `tb_role_auth` VALUES (1, 22);
INSERT INTO `tb_role_auth` VALUES (5, 22);
INSERT INTO `tb_role_auth` VALUES (6, 22);
INSERT INTO `tb_role_auth` VALUES (7, 22);
INSERT INTO `tb_role_auth` VALUES (1, 23);
INSERT INTO `tb_role_auth` VALUES (5, 23);
INSERT INTO `tb_role_auth` VALUES (6, 23);
INSERT INTO `tb_role_auth` VALUES (7, 23);
INSERT INTO `tb_role_auth` VALUES (1, 24);
INSERT INTO `tb_role_auth` VALUES (5, 24);
INSERT INTO `tb_role_auth` VALUES (6, 24);
INSERT INTO `tb_role_auth` VALUES (7, 24);
INSERT INTO `tb_role_auth` VALUES (1, 25);
INSERT INTO `tb_role_auth` VALUES (5, 25);
INSERT INTO `tb_role_auth` VALUES (6, 25);
INSERT INTO `tb_role_auth` VALUES (7, 25);
INSERT INTO `tb_role_auth` VALUES (1, 26);
INSERT INTO `tb_role_auth` VALUES (5, 26);
INSERT INTO `tb_role_auth` VALUES (6, 26);
INSERT INTO `tb_role_auth` VALUES (7, 26);
INSERT INTO `tb_role_auth` VALUES (1, 27);
INSERT INTO `tb_role_auth` VALUES (5, 27);
INSERT INTO `tb_role_auth` VALUES (6, 27);
INSERT INTO `tb_role_auth` VALUES (7, 27);
INSERT INTO `tb_role_auth` VALUES (1, 28);
INSERT INTO `tb_role_auth` VALUES (5, 28);
INSERT INTO `tb_role_auth` VALUES (6, 28);
INSERT INTO `tb_role_auth` VALUES (7, 28);
INSERT INTO `tb_role_auth` VALUES (1, 29);
INSERT INTO `tb_role_auth` VALUES (2, 29);
INSERT INTO `tb_role_auth` VALUES (5, 29);
INSERT INTO `tb_role_auth` VALUES (6, 29);
INSERT INTO `tb_role_auth` VALUES (1, 30);
INSERT INTO `tb_role_auth` VALUES (2, 30);
INSERT INTO `tb_role_auth` VALUES (1, 31);
INSERT INTO `tb_role_auth` VALUES (2, 31);
INSERT INTO `tb_role_auth` VALUES (5, 31);
INSERT INTO `tb_role_auth` VALUES (6, 31);
INSERT INTO `tb_role_auth` VALUES (7, 31);
INSERT INTO `tb_role_auth` VALUES (1, 32);
INSERT INTO `tb_role_auth` VALUES (2, 32);
INSERT INTO `tb_role_auth` VALUES (5, 32);
INSERT INTO `tb_role_auth` VALUES (6, 32);
INSERT INTO `tb_role_auth` VALUES (7, 32);
INSERT INTO `tb_role_auth` VALUES (1, 33);
INSERT INTO `tb_role_auth` VALUES (2, 33);
INSERT INTO `tb_role_auth` VALUES (5, 33);
INSERT INTO `tb_role_auth` VALUES (6, 33);
INSERT INTO `tb_role_auth` VALUES (7, 33);
INSERT INTO `tb_role_auth` VALUES (1, 34);
INSERT INTO `tb_role_auth` VALUES (2, 34);
INSERT INTO `tb_role_auth` VALUES (1, 35);
INSERT INTO `tb_role_auth` VALUES (2, 35);
INSERT INTO `tb_role_auth` VALUES (3, 35);
INSERT INTO `tb_role_auth` VALUES (5, 35);
INSERT INTO `tb_role_auth` VALUES (6, 35);
INSERT INTO `tb_role_auth` VALUES (7, 35);
INSERT INTO `tb_role_auth` VALUES (1, 36);
INSERT INTO `tb_role_auth` VALUES (2, 36);
INSERT INTO `tb_role_auth` VALUES (3, 36);
INSERT INTO `tb_role_auth` VALUES (1, 37);
INSERT INTO `tb_role_auth` VALUES (2, 37);
INSERT INTO `tb_role_auth` VALUES (3, 37);
INSERT INTO `tb_role_auth` VALUES (1, 38);
INSERT INTO `tb_role_auth` VALUES (2, 38);
INSERT INTO `tb_role_auth` VALUES (3, 38);
INSERT INTO `tb_role_auth` VALUES (1, 39);
INSERT INTO `tb_role_auth` VALUES (2, 39);
INSERT INTO `tb_role_auth` VALUES (3, 39);
INSERT INTO `tb_role_auth` VALUES (5, 39);
INSERT INTO `tb_role_auth` VALUES (6, 39);
INSERT INTO `tb_role_auth` VALUES (7, 39);
INSERT INTO `tb_role_auth` VALUES (1, 40);
INSERT INTO `tb_role_auth` VALUES (2, 40);
INSERT INTO `tb_role_auth` VALUES (3, 40);
INSERT INTO `tb_role_auth` VALUES (1, 41);
INSERT INTO `tb_role_auth` VALUES (2, 41);
INSERT INTO `tb_role_auth` VALUES (3, 41);
INSERT INTO `tb_role_auth` VALUES (1, 42);
INSERT INTO `tb_role_auth` VALUES (2, 42);
INSERT INTO `tb_role_auth` VALUES (3, 42);
INSERT INTO `tb_role_auth` VALUES (1, 43);
INSERT INTO `tb_role_auth` VALUES (2, 43);
INSERT INTO `tb_role_auth` VALUES (3, 43);
INSERT INTO `tb_role_auth` VALUES (5, 43);
INSERT INTO `tb_role_auth` VALUES (6, 43);
INSERT INTO `tb_role_auth` VALUES (7, 43);
INSERT INTO `tb_role_auth` VALUES (1, 44);
INSERT INTO `tb_role_auth` VALUES (2, 44);
INSERT INTO `tb_role_auth` VALUES (3, 44);
INSERT INTO `tb_role_auth` VALUES (1, 45);
INSERT INTO `tb_role_auth` VALUES (2, 45);
INSERT INTO `tb_role_auth` VALUES (3, 45);
INSERT INTO `tb_role_auth` VALUES (1, 46);
INSERT INTO `tb_role_auth` VALUES (2, 46);
INSERT INTO `tb_role_auth` VALUES (3, 46);
INSERT INTO `tb_role_auth` VALUES (1, 47);
INSERT INTO `tb_role_auth` VALUES (2, 47);
INSERT INTO `tb_role_auth` VALUES (5, 47);
INSERT INTO `tb_role_auth` VALUES (6, 47);
INSERT INTO `tb_role_auth` VALUES (7, 47);
INSERT INTO `tb_role_auth` VALUES (1, 48);
INSERT INTO `tb_role_auth` VALUES (2, 48);
INSERT INTO `tb_role_auth` VALUES (5, 48);
INSERT INTO `tb_role_auth` VALUES (1, 49);
INSERT INTO `tb_role_auth` VALUES (2, 49);
INSERT INTO `tb_role_auth` VALUES (5, 49);
INSERT INTO `tb_role_auth` VALUES (6, 49);
INSERT INTO `tb_role_auth` VALUES (7, 49);
INSERT INTO `tb_role_auth` VALUES (1, 50);
INSERT INTO `tb_role_auth` VALUES (2, 50);
INSERT INTO `tb_role_auth` VALUES (1, 51);
INSERT INTO `tb_role_auth` VALUES (5, 51);
INSERT INTO `tb_role_auth` VALUES (6, 51);
INSERT INTO `tb_role_auth` VALUES (7, 51);
INSERT INTO `tb_role_auth` VALUES (1, 52);
INSERT INTO `tb_role_auth` VALUES (1, 53);
INSERT INTO `tb_role_auth` VALUES (1, 54);
INSERT INTO `tb_role_auth` VALUES (1, 55);
INSERT INTO `tb_role_auth` VALUES (1, 56);
INSERT INTO `tb_role_auth` VALUES (1, 57);
INSERT INTO `tb_role_auth` VALUES (1, 58);
INSERT INTO `tb_role_auth` VALUES (1, 59);
INSERT INTO `tb_role_auth` VALUES (1, 60);
INSERT INTO `tb_role_auth` VALUES (1, 61);
INSERT INTO `tb_role_auth` VALUES (1, 62);
INSERT INTO `tb_role_auth` VALUES (1, 63);
INSERT INTO `tb_role_auth` VALUES (1, 64);
INSERT INTO `tb_role_auth` VALUES (1, 65);
INSERT INTO `tb_role_auth` VALUES (1, 66);
INSERT INTO `tb_role_auth` VALUES (1, 67);
INSERT INTO `tb_role_auth` VALUES (1, 68);
INSERT INTO `tb_role_auth` VALUES (1, 69);
INSERT INTO `tb_role_auth` VALUES (1, 70);
INSERT INTO `tb_role_auth` VALUES (1, 71);
INSERT INTO `tb_role_auth` VALUES (1, 72);
INSERT INTO `tb_role_auth` VALUES (1, 73);
INSERT INTO `tb_role_auth` VALUES (1, 74);
INSERT INTO `tb_role_auth` VALUES (1, 75);
INSERT INTO `tb_role_auth` VALUES (1, 76);

# 向用户表添加数据
INSERT INTO `tb_user` VALUES (200114216, 'admin', '13080395207', '$2a$10$hRVKBmeqdrqlv1AsqI45P.ulv2CXlRm6iLJYqx08XSAp.SFxzI4b6', '最高管理员', NULL, 0, 0, 0, 0, '2022-10-04 18:39:25', '2022-10-04 18:42:14', 0);
INSERT INTO `tb_user` VALUES (1577368407370752002, 'zhangsan', '13100001111', '$2a$10$px9uR7jmxHyiCMbRk9sDKeeJrz3OEK0xp4RDY4p4Pb0L9GQOoA1JG', '苏娜', '/user/icon/4/14/60f02a8d-02ef-409a-bd27-17222c58ed1c.jpeg', 1, 0, 0, 0, '2022-10-05 02:41:49', '2022-10-28 14:31:49', 0);
INSERT INTO `tb_user` VALUES (1577421806518743041, 'sy_blog_user13011112222', '13011112222', '$2a$10$RsV9.MaH0fnqU4CSYts7cOhPx0UKYHcG7Veif0w.N9VX3ytloQFfy', 'sy_blog_userea6aydfbryou4', '/user/icon/6/5/6fd3ce10-2225-49e3-aaa7-6055f7a5c59d.jpg', 1, 0, 1, 0, '2022-10-05 06:14:00', '2022-10-08 18:59:54', 0);
INSERT INTO `tb_user` VALUES (1579020199879860225, 'lisansi', '13033334444', '$2a$10$dhfeJn04JATcdKpqVrRKCuYoF1HB8BmxHVQALXZSIrp.Cu6KJuRJW', 'sy_blog_user13033334444', NULL, 1, 0, 0, 0, '2022-10-09 16:05:27', '2022-10-09 16:06:07', 0);
INSERT INTO `tb_user` VALUES (1579021393746489345, 'linwuliu', '13055556666', '$2a$10$9S5tfIEVaWJgrq.lCW1YJeOB/xhr4mBtE7wLACn5xxaF9.7RVdo9S', 'sy_blog_user13055556666', NULL, 1, 0, 0, 0, '2022-10-09 16:10:11', '2022-10-09 16:10:11', 0);
INSERT INTO `tb_user` VALUES (1584336961890496514, 't2_5O25', '13022224444', '$2a$10$55XVhdtSi1NYAUy4fm..0OkbcclpfZlZYMkc6rZlwNImEKbJvbR3C', 'sy_blog_user13022224444', NULL, 1, 0, 0, 0, '2022-10-24 08:12:21', '2022-10-24 08:12:21', 0);

# 向用户信息表添加数据
INSERT INTO `tb_user_info` VALUES (200114216, '林武泰', '男', 21, '2786794141@qq.com', '2001-09-25', '2130933887', 9);
INSERT INTO `tb_user_info` VALUES (1577368407370752002, NULL, '保密', NULL, NULL, NULL, NULL, 0);
INSERT INTO `tb_user_info` VALUES (1577421806518743041, NULL, '保密', NULL, NULL, NULL, NULL, 0);
INSERT INTO `tb_user_info` VALUES (1579020199879860225, NULL, '保密', NULL, NULL, NULL, NULL, 0);
INSERT INTO `tb_user_info` VALUES (1579021393746489345, NULL, '保密', NULL, NULL, NULL, NULL, 0);
INSERT INTO `tb_user_info` VALUES (1584336961890496514, NULL, '保密', NULL, NULL, NULL, NULL, 0);

# 向用户角色表添加数据
INSERT INTO `tb_user_role` VALUES (200114216, 1);
INSERT INTO `tb_user_role` VALUES (1577368407370752002, 5);
INSERT INTO `tb_user_role` VALUES (1577421806518743041, 5);
INSERT INTO `tb_user_role` VALUES (1579020199879860225, 5);
INSERT INTO `tb_user_role` VALUES (1579021393746489345, 5);
INSERT INTO `tb_user_role` VALUES (1584336961890496514, 5);

# 向充值套餐表添加数据
INSERT INTO tb_recharge_combo VALUES (1581677102481133569, '充50送500积分', 50.00, 500, 1.00);
INSERT INTO tb_recharge_combo VALUES (1581677150921150465, '充100送1000积分', 100.00, 1000, 1.00);
INSERT INTO tb_recharge_combo VALUES (1581677201881944066, '充200送2000积分', 200.00, 2000, 1.00);
INSERT INTO tb_recharge_combo VALUES (1581677281716326402, '充500送5000积分', 500.00, 5000, 1.00);
INSERT INTO tb_recharge_combo VALUES (1581677908177571841, '充10送1000积分', 10.00, 1000, 1.00);

# 向用户钱包添加数据
INSERT INTO `tb_user_account` VALUES (1577368407370752002, 899.00, 2550, 5080.00, 4181.00);
INSERT INTO `tb_user_account` VALUES (1579020199879860225, 0.00, 0, 0.00, 0.00);
INSERT INTO `tb_user_account` VALUES (1579021393746489345, 0.00, 0, 0.00, 0.00);
INSERT INTO `tb_user_account` VALUES (1584336961890496514, 0.00, 0, 0.00, 0.00);

# 向收货地址表添加数据
INSERT INTO `tb_user_address` VALUES (1578772810896035842, 1577368407370752002, '崔霞', '18153847848', '辽宁省', '湛江市', '涵江区', '某某街道01号', 0);
INSERT INTO `tb_user_address` VALUES (1578774474776432642, 1577368407370752002, '万霞', '18151481708', '内蒙古自治区', '怀化市', '马关县', '某某街道01号', 1);

# 向商品分类表添加数据
INSERT INTO `tb_goods_category` VALUES (1578983911919300610, '手机', NULL, NULL, '');
INSERT INTO `tb_goods_category` VALUES (1578991220561104898, '家电', NULL, NULL, NULL);
INSERT INTO `tb_goods_category` VALUES (1578993577369833473, '华为', NULL, 1578983911919300610, NULL);
INSERT INTO `tb_goods_category` VALUES (1578995184027340802, 'Apple', NULL, 1578983911919300610, NULL);
INSERT INTO `tb_goods_category` VALUES (1580920911282860033, '男装', NULL, NULL, NULL);
INSERT INTO `tb_goods_category` VALUES (1580921016236929025, '上衣', NULL, 1580920911282860033, NULL);

# 向商品表添加数据
INSERT INTO `tb_goods` VALUES (1579070878421901314, '华为手机01', 1578993577369833473, NULL, '广西壮族自治区', '深圳市', '武陵源区', '云南省梧州市-', 5.00, 0, 0, 7, 7, 7, 1, '2022-10-25 19:26:49', '2022-10-29 12:46:48', 0);
INSERT INTO `tb_goods` VALUES (1580921307300655106, '耐克上衣01', 1580921016236929025, NULL, '广东省', '广州市', '天河区', '某某街道01号', 0.00, 0, 0, 7, 7, 7, 1, '2022-10-14 21:59:46', '2022-10-15 11:27:23', 0);

# 向商品属性表添加数据
INSERT INTO `tb_goods_item` VALUES (1579762804599140353, 1579070878421901314, '测试图片', '红色', NULL, NULL, NULL, 1599.00, 1.00, 100, 1, 'mollit', 1, '2022-10-11 17:16:17', '2022-10-29 09:09:20');
INSERT INTO `tb_goods_item` VALUES (1579815980329091074, 1579070878421901314, '测试图片', '蔚蓝色', NULL, NULL, NULL, 1999.00, 1.00, 100, 0, 'aute', 1, '2022-10-11 20:47:35', '2022-10-18 20:42:58');
INSERT INTO `tb_goods_item` VALUES (1579816176584769538, 1579070878421901314, '测试图片', '天青色', '1.0', NULL, NULL, 1199.00, 1.00, 100, 0, NULL, 1, '2022-10-11 20:48:22', '2022-10-29 11:37:15');
INSERT INTO `tb_goods_item` VALUES (1580911673013739522, 1579070878421901314, '测试图片', '天青色', '2.0', NULL, NULL, 3999.00, 1.00, 100, 0, NULL, 1, '2022-10-14 21:21:29', '2022-10-29 11:37:21');
INSERT INTO `tb_goods_item` VALUES (1580922545769574401, 1580921307300655106, '测试图片', '纯白色', NULL, 'S', NULL, 59.00, 1.00, 0, 0, NULL, 1, '2022-10-14 22:04:41', '2022-10-29 09:09:29');
INSERT INTO `tb_goods_item` VALUES (1580971799166910466, 1580921307300655106, '测试图片', '纯白色', NULL, 'M', NULL, 59.00, 1.00, 998, 0, NULL, 1, '2022-10-15 01:20:24', '2022-10-15 01:22:11');
INSERT INTO `tb_goods_item` VALUES (1580971990926295041, 1580921307300655106, '测试图片', '纯白色', NULL, 'L', NULL, 59.00, 1.00, 998, 0, NULL, 1, '2022-10-15 01:21:10', '2022-10-18 23:15:35');

# 向购物车表添加数据
INSERT INTO `tb_shopping_cart` VALUES (1581085498294837250, 1577368407370752002, 1580971990926295041, 10, '2022-10-15 08:52:12');
INSERT INTO `tb_shopping_cart` VALUES (1581093295912816641, 1577368407370752002, 1580911673013739522, 1, '2022-10-15 09:23:11');

# 向活动表添加数据
INSERT INTO `tb_events` VALUES (1586423520785625089, '双十一热购', '/events/icon/7/15/b0042b47-05fd-4ec0-825a-a0bc67acba6a.jpg', '2022-10-30 02:29:08', -1, NULL, '2022-10-30 02:23:36', '2022-10-30 02:29:11');
INSERT INTO `tb_events` VALUES (1586423894460362754, '双十二年终大促销', '/events/icon/1/8/d8bf796a-e6b8-4a8f-b43b-979f274af1bc.jpg', '2022-10-30 02:29:11', -1, NULL, '2022-10-30 02:25:05', '2022-10-30 02:29:19');

# 向活动商品表添加数据
INSERT INTO `tb_events_goods` VALUES (1586423520785625089, 1579070878421901314);

# 向公告类型表添加数据
INSERT INTO `tb_notice_category` VALUES (1586626249483112450, '系统公告', NULL, NULL);
INSERT INTO `tb_notice_category` VALUES (1586627381370593282, '招聘公告', NULL, NULL);
INSERT INTO `tb_notice_category` VALUES (1586627929155059714, '活动公告', NULL, NULL);

# 向公告表添加数据
INSERT INTO `tb_notice` VALUES (1586646432109051906, '喜迎二十大，永远跟党走', 1586626249483112450, '让我们一起向未来！', '2022-10-30 17:09:22', '2022-10-30 17:09:22');
INSERT INTO `tb_notice` VALUES (1586647321033064450, '系统公告', 1586626249483112450, '该网上购物系统，由广东水利电力职业技术学院，20计算机应用二班“摘星小分队”负责开发。\n小组组长：林武泰\n小组组员：马俊荣 钟泽豪 冯巧儿 黄与蓝', '2022-10-30 17:12:54', '2022-10-30 17:12:54');
INSERT INTO `tb_notice` VALUES (1586647853579649026, '系统公告', 1586626249483112450, '测试，测试，over！', '2022-10-30 17:15:01', '2022-10-30 17:15:01');

# 向订单表添加数据
INSERT INTO `tb_order` VALUES (107983487935447047, 1577368407370752002, '2022-10-18 23:51:05', 4122.00, '修改测试', '万霞', '18151481708', '内蒙古自治区', '怀化市', '马关县', '某某街道01号', 5.00, 1, 0, 0, NULL, NULL, 1, '2022-10-20 17:46:14', 0);
INSERT INTO `tb_order` VALUES (109229290444292097, 1577368407370752002, '2022-10-22 08:25:26', 59.00, '我想买一件质量好一些的衬衫', '崔霞', '18153847848', '辽宁省', '湛江市', '涵江区', '某某街道01号', 0.00, 1, 0, 0, NULL, NULL, 1, '2022-10-22 08:28:17', 0);

# 向订单详细表添加数据
INSERT INTO `tb_order_item` VALUES (1582398874968940545, 107983487935447047, 1580911673013739522, 1, 3999.00, 3999.00);
INSERT INTO `tb_order_item` VALUES (1582398874968940546, 107983487935447047, 1580971990926295041, 2, 59.00, 118.00);
INSERT INTO `tb_order_item` VALUES (1583615475502444545, 109229290444292097, 1580971799166910466, 1, 59.00, 59.00);

# 向用户收藏表添加数据
INSERT INTO `tb_goods_collection` VALUES (1577368407370752002, 1579070878421901314);
INSERT INTO `tb_goods_collection` VALUES (1577368407370752002, 1580921307300655106);

# 向商品评价表添加数据
INSERT INTO `tb_goods_evaluation` VALUES (1583621456974266369, 1577368407370752002, 107983487935447047, 1580921307300655106, 1580971990926295041, 5, '/goods/comment/image/9/4/7a5d2ad9-6463-4e3f-a0b9-780166e11392.jpg', '质量可以哦，值得推荐！', '2022-10-21 08:49:12', 1, 0);
INSERT INTO `tb_goods_evaluation` VALUES (1583621655973019649, 1577368407370752002, 109229290444292097, 1580921307300655106, 1580971799166910466, 4, '/goods/comment/image/0/2/701a6a61-fb1a-4e57-a9c0-aa95e1f65bf0.jpg', '还可以吧', '2022-10-22 08:49:59', 1, 0);

# 向评价回复表添加数据
INSERT INTO `tb_evaluation_comment` VALUES (1583729451055796226, 1579020199879860225, 1583621655973019649, '一分钱一分货，已经很不错啦！', '2022-10-22 15:58:20', 1, 0, NULL, 0);
INSERT INTO `tb_evaluation_comment` VALUES (1583729635722612738, 1579021393746489345, 1583621655973019649, '确实，不错啦！哈哈哈~', '2022-10-22 15:59:04', 1, 0, NULL, 0);
INSERT INTO `tb_evaluation_comment` VALUES (1583729835438592001, 1579021393746489345, 1583621655973019649, '确实，哈哈哈~', '2022-10-22 15:59:51', 0, 0, 1583729451055796226, 0);
