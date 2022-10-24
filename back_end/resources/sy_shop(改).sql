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


# 向权限表添加数据
insert into tb_auth(name, perms) values
('查询用户','user:query'),
('新增用户','user:save'),
('删除用户','user:delete'),
('修改用户','user:update'),
('查询用户信息','userinfo:query'),
('新增用户信息','userinfo:save'),
('删除用户信息','userinfo:delete'),
('修改用户信息','userinfo:update'),
('查询用户钱包','account:query'),
('新增用户钱包','account:save'),
('删除用户钱包','account:delete'),
('钱包充值','account:recharge'),
('查询收货地址','address:query'),
('新增收货地址','address:save'),
('删除收货地址','address:delete'),
('修改收货地址','address:update'),
('查询购物车','shoppingcart:query'),
('新增购物车','shoppingcart:save'),
('删除购物车','shoppingcart:delete'),
('修改购物车','shoppingcart:update'),
('查询用户收藏','collection:query'),
('新增用户收藏','collection:save'),
('删除用户收藏','collection:delete'),
('修改用户收藏','collection:update'),
('查询用户推荐','commend:query'),
('新增用户推荐','commend:save'),
('删除用户推荐','commend:delete'),
('修改用户推荐','commend:update'),
('提交订单','order:submit'),
('新增订单','order:save'),
('取消订单','order:cancel'),
('删除订单','order:delete'),
('查询订单','order:query'),
('修改订单','order:update'),
('查询商品','goods:query'),
('新增商品','goods:save'),
('删除商品','goods:delete'),
('修改商品','goods:update'),
('查询商品属性','goods:item:query'),
('新增商品属性','goods:item:save'),
('删除商品属性','goods:item:delete'),
('修改商品属性','goods:item:update'),
('查询商品分类','goods:category:query'),
('新增商品分类','goods:category:save'),
('删除商品分类','goods:category:delete'),
('修改商品分类','goods:category:update'),
('查询评论','comment:query'),
('新增评论','comment:save'),
('删除评论','comment:delete'),
('修改评论','comment:update');

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
(1,1),
(1,2),
(1,3),
(1,4),
(1,5),
(1,6),
(1,7),
(1,8),
(1,9),
(1,10),
(1,11),
(1,12),
(1,13),
(1,14),
(1,15),
(1,16),
(1,17),
(1,18),
(1,19),
(1,20),
(1,21),
(1,22),
(1,23),
(1,24),
(1,25),
(1,26),
(1,27),
(1,28),
(1,29),
(1,30),
(1,31),
(1,32),
(1,33),
(1,34),
(1,35),
(1,36),
(1,37),
(1,38),
(1,39),
(1,40),
(1,41),
(1,42),
(1,43),
(1,44),
(1,45),
(1,46),
(1,47),
(1,48),
(1,49),
(1,50),
(2,1),
(2,2),
(2,3),
(2,4),
(2,5),
(2,6),
(2,7),
(2,8),
(2,29),
(2,30),
(2,31),
(2,32),
(2,33),
(2,34),
(2,35),
(2,36),
(2,37),
(2,38),
(2,39),
(2,40),
(2,41),
(2,42),
(2,43),
(2,44),
(2,45),
(2,46),
(2,47),
(2,48),
(2,49),
(2,50),
(3,35),
(3,36),
(3,37),
(3,38),
(3,39),
(3,40),
(3,41),
(3,42),
(3,43),
(3,44),
(3,45),
(3,46),
(4,1),
(4,2),
(4,3),
(4,4),
(4,5),
(4,6),
(4,7),
(4,8),
(4,9),
(4,10),
(4,11),
(4,12),
(4,13),
(4,14),
(4,15),
(4,16),
(5,1),
(5,4),
(5,5),
(5,8),
(5,9),
(5,12),
(5,13),
(5,14),
(5,15),
(5,16),
(5,17),
(5,18),
(5,19),
(5,20),
(5,21),
(5,22),
(5,23),
(5,24),
(5,25),
(5,26),
(5,27),
(5,28),
(5,29),
(5,31),
(5,32),
(5,33),
(5,35),
(5,39),
(5,43),
(5,47),
(5,48),
(5,49),
(6,1),
(6,4),
(6,5),
(6,8),
(6,9),
(6,12),
(6,13),
(6,14),
(6,15),
(6,16),
(6,17),
(6,18),
(6,19),
(6,20),
(6,21),
(6,22),
(6,23),
(6,24),
(6,25),
(6,26),
(6,27),
(6,28),
(6,29),
(6,31),
(6,32),
(6,33),
(6,35),
(6,39),
(6,43),
(6,47),
(6,49),
(7,1),
(7,4),
(7,5),
(7,8),
(7,9),
(7,12),
(7,13),
(7,14),
(7,15),
(7,16),
(7,17),
(7,18),
(7,19),
(7,20),
(7,21),
(7,22),
(7,23),
(7,24),
(7,25),
(7,26),
(7,27),
(7,28),
(7,31),
(7,32),
(7,33),
(7,35),
(7,39),
(7,43),
(7,47),
(7,49),
(8,0);
