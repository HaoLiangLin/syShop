package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.GoodsCommentDTO;
import com.jy2b.zxxfd.domain.dto.GoodsCommentSaveDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.*;
import com.jy2b.zxxfd.service.IGoodsCommentService;
import com.jy2b.zxxfd.utils.TimeUtils;
import com.jy2b.zxxfd.utils.UploadUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import com.jy2b.zxxfd.contants.SystemConstants;
import com.jy2b.zxxfd.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

@Service
public class GoodsCommentServiceImpl extends ServiceImpl<GoodsCommentMapper, GoodsComment> implements IGoodsCommentService {
    @Resource
    private OrderItemMapper orderItemMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private UserMapper userMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Resource
    private GoodsItemMapper goodsItemMapper;

    @Override
    public ResultVo uploadImages(MultipartFile[] files, Long orderItemId) {
        // 查询订单属性
        OrderItem orderItem = orderItemMapper.selectById(orderItemId);
        // 判断订单属性是否存在
        if (orderItem == null) {
            return ResultVo.fail("订单不存在！");
        }

        // 获取订单id
        Long orderId = orderItem.getOrderId();
        // 查询订单
        Order order = orderMapper.selectById(orderId);

        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 判断是否本人上传
        if (!userId.equals(order.getUid())) {
            return ResultVo.fail("订单不存在！");
        }

        if (files.length > SystemConstants.COMMENT_IMAGES_LENGTH) {
            return ResultVo.fail("上传图片不能超过" + SystemConstants.COMMENT_IMAGES_LENGTH + "张！");
        }

        MultipartFile[] multipartFiles = new MultipartFile[SystemConstants.COMMENT_IMAGES_LENGTH];

        /*
          System.arraycopy(Object src, int srcPos, Object dest, int destPos, int length)
          作用：把一个数组中某一段字节数据放到另一个数组中
          src: 源数组
          srcPos: 源数组要复制的起始位置
          dest: 目的数组
          destPos: 目的数组放置的起始位置
          length: 复制的长度
         */
        System.arraycopy(files, 0, multipartFiles, 0, files.length);

        return UploadUtils.saveFiles(multipartFiles, "/goods/comment/image");
    }

    @Override
    public ResultVo saveComment(GoodsCommentSaveDTO commentSaveDTO) {
        // 获取评价内容
        String content = commentSaveDTO.getContent();
        if (StrUtil.isBlank(content)) {
            return ResultVo.fail("评价内容不能为空！");
        }

        // 获取订单属性id
        Long orderItemId = commentSaveDTO.getOrderItemId();
        // 判断订单属性id是否为空
        if (orderItemId == null) {
            return ResultVo.fail("订单数据错误！");
        }

        // 查询订单属性
        OrderItem orderItem = orderItemMapper.selectById(orderItemId);
        // 判断订单属性是否存在
        if (orderItem == null) {
            return ResultVo.fail("订单不存在！");
        }

        // 获取订单号
        Long orderId = orderItem.getOrderId();
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        // 判断订单是否属于评价用户
        if (!order.getUid().equals(userId)) {
            return ResultVo.fail("订单不存在！");
        }
        // 判断订单是否已完成
        if (order.getStatus() != 1) {
            return ResultVo.fail("订单未完成！");
        }

        // 获取商品属性id
        Long gid = orderItem.getGid();
        // 查询是否评论
        Integer count = query().eq("order_id", orderId).eq("gid", gid).count();
        if (count > 0) {
            return ResultVo.fail("该订单已完成评价！");
        }

        GoodsComment goodsComment = new GoodsComment();

        // 获取评价星级
        Integer stars = commentSaveDTO.getStars();
        // 设置评价星级
        if (stars == null || stars < 1) {
            goodsComment.setStars(1);
        } else if (stars > 5) {
            goodsComment.setStars(5);
        } else {
            goodsComment.setStars(stars);
        }

        // 获取评价图片
        String images = commentSaveDTO.getImages();
        if (StrUtil.isNotBlank(images)) {
            // 设置评价图片
            goodsComment.setImages(images);
        }

        // 设置评论内容
        goodsComment.setContent(content);

        // 设置评价用户id
        goodsComment.setUid(userId);

        // 设置订单号
        goodsComment.setOrderId(orderId);

        // 设置商品属性id
        goodsComment.setGid(orderItem.getGid());

        // 新增评论
        boolean result = save(goodsComment);
        return result ? ResultVo.ok("评价成功！") : ResultVo.fail("评价失败！");
    }

    @Override
    public ResultVo deleteComment(Long id) {
        // 查询评价是否存在
        GoodsComment comment = getById(id);
        if (comment == null) {
            return ResultVo.fail("评价不存在！");
        }

        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        if (!userId.equals(comment.getUid())) {
            return ResultVo.fail("评价不存在！");
        }

        // 获取评价图片
        String images = comment.getImages();

        // 删除评价
        boolean result = removeById(id);

        if (result) {
            // 评价图片不为空
            if (StrUtil.isNotBlank(images)) {
                String[] split = images.split(",");
                if (split.length == 0) {
                    ResultVo.ok("删除评价成功！");
                }
                for (String s : split) {
                    // 删除评价图片
                    UploadUtils.deleteFile(s);
                }
            }
        }

        return result ? ResultVo.ok("删除评价成功！") : ResultVo.fail("删除评价失败！");
    }

    @Override
    public ResultVo queryCommentById(Long id) {
        // 查询订单评价
        GoodsComment comment = getById(id);

        if (comment == null) {
            return ResultVo.fail("评价不存在！");
        }

        GoodsCommentDTO goodsCommentDTO = new GoodsCommentDTO();

        // 获取用户id
        Long uid = comment.getUid();
        // 查询用户
        User user = userMapper.selectById(uid);
        // 设置用户头像
        goodsCommentDTO.setUserIcon(user.getIcon());
        // 设置用户昵称
        goodsCommentDTO.setNickname(user.getNickname());

        // 获取评论时间
        Date time = comment.getTime();
        // 设置评论时间
        goodsCommentDTO.setTime(TimeUtils.dateToStringTime(time));

        // 设置评价星级
        goodsCommentDTO.setStars(comment.getStars());

        // 获取商品属性id
        Long gid = comment.getGid();
        // 获取商品属性
        GoodsItem goodsItem = goodsItemMapper.selectById(gid);
        String color = goodsItem.getColor();
        String size = goodsItem.getSize();
        String combo = goodsItem.getCombo();
        String edition = goodsItem.getEdition();

        String parameter = "";

        if (StrUtil.isNotBlank(color)) {
            parameter = color;
        }

        if (StrUtil.isNotBlank(size)) {
            parameter = parameter.concat("/" + size);
        }

        if (StrUtil.isNotBlank(combo)) {
            parameter = parameter.concat("/" + combo);
        }

        if (StrUtil.isNotBlank(edition)) {
            parameter = parameter.concat("/" + edition);
        }

        // 设置参数
        goodsCommentDTO.setParameter(parameter);

        // 设置评价内容
        goodsCommentDTO.setContent(comment.getContent());

        // 获取评价图片
        String images = comment.getImages();
        if (StrUtil.isNotBlank(images)) {
            String[] split = images.split(",");

            ArrayList<String> list = new ArrayList<>(Arrays.asList(split));

            // 设置评论图片
            goodsCommentDTO.setImages(list);
        }

        // 设置点赞数
        goodsCommentDTO.setLiked(comment.getLiked());

        return ResultVo.ok(goodsCommentDTO);
    }

    @Override
    public ResultVo queryComment(Long goodsId, Integer sort) {
        return null;
    }
}
