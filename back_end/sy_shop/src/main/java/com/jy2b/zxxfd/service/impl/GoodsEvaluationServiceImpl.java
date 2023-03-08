package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.GoodsEvaluationDTO;
import com.jy2b.zxxfd.domain.dto.GoodsEvaluationSaveDTO;
import com.jy2b.zxxfd.domain.*;
import com.jy2b.zxxfd.domain.dto.UserDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IGoodsEvaluationService;
import com.jy2b.zxxfd.utils.TimeUtils;
import com.jy2b.zxxfd.utils.UploadUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import com.jy2b.zxxfd.contants.SystemConstants;
import com.jy2b.zxxfd.mapper.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.jy2b.zxxfd.contants.RedisConstants.COMMENT_LIKED_KEY;
import static com.jy2b.zxxfd.contants.RedisConstants.EVALUATION_LIKED_KEY;

@Service
public class GoodsEvaluationServiceImpl extends ServiceImpl<GoodsEvaluationMapper, GoodsEvaluation> implements IGoodsEvaluationService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

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

    @Resource
    private EvaluationCommentMapper commentMapper;

    @Override
    public ResultVO uploadImages(MultipartFile[] files, Long orderItemId) {
        // 查询订单属性
        OrderItem orderItem = orderItemMapper.selectById(orderItemId);
        // 判断订单属性是否存在
        if (orderItem == null) {
            return ResultVO.fail("订单不存在");
        }

        // 获取订单id
        Long orderId = orderItem.getOrderId();
        // 查询订单
        Order order = orderMapper.selectById(orderId);

        // 判断订单是否完成
        if (order.getStatus() != 1) {
            return ResultVO.fail("订单未完成");
        }

        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 判断是否本人上传
        if (!userId.equals(order.getUid())) {
            return ResultVO.fail("订单不存在");
        }

        if (files.length > SystemConstants.COMMENT_IMAGES_LENGTH) {
            return ResultVO.fail("上传图片不能超过" + SystemConstants.COMMENT_IMAGES_LENGTH + "张");
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

        return UploadUtils.saveFiles(multipartFiles, "/goods/evaluation/image");
    }

    @Override
    public ResultVO saveEvaluation(GoodsEvaluationSaveDTO commentSaveDTO) {
        // 获取评价内容
        String content = commentSaveDTO.getContent();
        if (StrUtil.isBlank(content)) {
            return ResultVO.fail("评价内容不能为空");
        }

        // 获取订单属性id
        Long orderItemId = commentSaveDTO.getOrderItemId();
        // 判断订单属性id是否为空
        if (orderItemId == null) {
            return ResultVO.fail("订单数据错误");
        }

        // 查询订单属性
        OrderItem orderItem = orderItemMapper.selectById(orderItemId);
        // 判断订单属性是否存在
        if (orderItem == null) {
            return ResultVO.fail("订单不存在");
        }

        // 获取订单号
        Long orderId = orderItem.getOrderId();
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 查询订单
        Order order = orderMapper.selectById(orderId);
        // 判断订单是否属于评价用户
        if (!order.getUid().equals(userId)) {
            return ResultVO.fail("订单不存在");
        }
        // 判断订单是否已完成
        if (order.getStatus() != 1) {
            return ResultVO.fail("订单未完成");
        }

        // 获取商品属性id
        Long gid = orderItem.getGid();
        // 查询是否评论
        Integer count = query().eq("order_id", orderId).eq("goodsItem_id", gid).count();
        if (count > 0) {
            return ResultVO.fail("该订单已完成评价");
        }

        // 查询商品属性
        GoodsItem goodsItem = goodsItemMapper.selectById(gid);

        GoodsEvaluation goodsEvaluation = new GoodsEvaluation();

        // 获取评价星级
        Integer stars = commentSaveDTO.getStars();
        // 设置评价星级
        if (stars == null || stars < 1) {
            goodsEvaluation.setStars(1);
        } else if (stars > 5) {
            goodsEvaluation.setStars(5);
        } else {
            goodsEvaluation.setStars(stars);
        }

        // 获取评价图片
        String images = commentSaveDTO.getImages();
        if (StrUtil.isNotBlank(images)) {
            // 设置评价图片
            goodsEvaluation.setImages(images);
        }

        // 设置评论内容
        goodsEvaluation.setContent(content);

        // 设置评价用户id
        goodsEvaluation.setUid(userId);

        // 设置订单号
        goodsEvaluation.setOrderId(orderId);

        // 设置商品id
        goodsEvaluation.setGoodsId(goodsItem.getGid());

        // 设置商品属性id
        goodsEvaluation.setGoodsItemId(orderItem.getGid());

        // 新增评论
        boolean result = save(goodsEvaluation);
        if (!result) {
            UploadUtils.deleteFiles(images);
        }
        return result ? ResultVO.ok(null,"评价成功") : ResultVO.fail("评价失败");
    }

    @Override
    public ResultVO deleteEvaluation(Long id) {
        // 查询评价是否存在
        GoodsEvaluation comment = getById(id);
        if (comment == null) {
            return ResultVO.fail("评价不存在");
        }

        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        if (!userId.equals(comment.getUid())) {
            return ResultVO.fail("评价不存在");
        }

        QueryWrapper<EvaluationComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("evaluation_id", id);
        List<EvaluationComment> commentList = commentMapper.selectList(queryWrapper);
        if (!commentList.isEmpty()) {
            // 删除评价评论
            int delete = commentMapper.delete(queryWrapper);

            if (delete < 1) {
                return ResultVO.fail("删除评价失败");
            }

            for (EvaluationComment evaluationComment : commentList) {
                if (evaluationComment != null) {
                    Long commentId = evaluationComment.getId();
                    // 删除点赞记录
                    stringRedisTemplate.delete(COMMENT_LIKED_KEY + commentId);
                }
            }
        }

        // 获取评价图片
        String images = comment.getImages();

        // 删除评价
        boolean result = removeById(id);

        if (result) {
            // 删除点赞记录
            stringRedisTemplate.delete(EVALUATION_LIKED_KEY + id);

            // 删除评价图片
            UploadUtils.deleteFiles(images);
        }

        return result ? ResultVO.ok(null,"删除评价成功") : ResultVO.fail("删除评价失败");
    }

    @Override
    public ResultVO queryEvaluationById(Long id) {
        // 查询订单评价
        GoodsEvaluation comment = getById(id);

        if (comment == null) {
            return ResultVO.fail("评价不存在");
        }

        GoodsEvaluationDTO goodsEvaluationDTO = setEvaluationDTO(comment);

        return ResultVO.ok(goodsEvaluationDTO, "查询成功");
    }

    @Override
    public ResultVO queryEvaluation(Long goodsId, Integer sort) {
        // 根据商品id查询商品
        Goods goods = goodsMapper.selectById(goodsId);
        // 判断商品是否为空
        if (goods == null) {
            return ResultVO.fail("商品不存在");
        }

        // 根据商品id查询商品属性
        QueryWrapper<GoodsItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("gid", goodsId);
        List<GoodsItem> goodsItems = goodsItemMapper.selectList(queryWrapper);

        // 判断商品属性是否为空
        if (goodsItems.isEmpty()) {
            return ResultVO.fail("商品不存在");
        }

        // 判断排序是否为空
        if (sort == null) {
            // 设置默认排序
            sort = 0;
        }

        ArrayList<GoodsEvaluationDTO> goodsEvaluationDTOS = new ArrayList<>();

        QueryWrapper<GoodsEvaluation> commentQueryWrapper = new QueryWrapper<>();
        if (sort == 1) {
            commentQueryWrapper.orderByDesc("time");
        } else {
            commentQueryWrapper.orderByDesc( "stars", "liked");
        }
        commentQueryWrapper.eq("goods_id", goodsId);

        // 根据商品属性id查询商品评价
        List<GoodsEvaluation> commentList = list(commentQueryWrapper);

        if (commentList.isEmpty()) {
            return ResultVO.fail("暂无评价");
        }

        for (GoodsEvaluation goodsEvaluation : commentList) {
            GoodsEvaluationDTO goodsEvaluationDTO = setEvaluationDTO(goodsEvaluation);
            goodsEvaluationDTOS.add(goodsEvaluationDTO);
        }

        return ResultVO.ok(goodsEvaluationDTOS, "查询成功");
    }

    @Override
    public ResultVO likedEvaluation(Long id) {
        // 查询订单评价
        GoodsEvaluation comment = getById(id);
        // 判断评价是否不存在
        if (comment == null) {
            return ResultVO.fail("评价不存在");
        }

        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        Double score = stringRedisTemplate.opsForZSet().score(EVALUATION_LIKED_KEY + id, userId.toString());
        if (score != null && score > 0) {
            // 取消点赞
            boolean result = update().set("liked", comment.getLiked() - 1).eq("id", id).update();
            if (result) {
                stringRedisTemplate.opsForZSet().remove(EVALUATION_LIKED_KEY + id, userId.toString());
            }
            return result ? ResultVO.ok("取消点赞成功") : ResultVO.fail("取消点赞失败");
        }

        // 获取现在时间
        long time = new Date().getTime();

        // 新增点赞
        boolean result = update().set("liked", comment.getLiked() + 1).eq("id", id).update();
        if (result) {
            stringRedisTemplate.opsForZSet().add(EVALUATION_LIKED_KEY + id, userId.toString(), Double.parseDouble(String.valueOf(time)));
        }

        return result ? ResultVO.ok(null,"点赞成功") : ResultVO.fail("点赞失败");
    }

    private GoodsEvaluationDTO setEvaluationDTO(GoodsEvaluation comment) {
        GoodsEvaluationDTO goodsEvaluationDTO = new GoodsEvaluationDTO();

        // 设置评价id
        goodsEvaluationDTO.setId(comment.getId());

        // 设置评论条数
        QueryWrapper<EvaluationComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("evaluation_id", comment.getId());
        Integer integer = commentMapper.selectCount(queryWrapper);
        goodsEvaluationDTO.setComment(integer);

        // 获取用户id
        Long uid = comment.getUid();
        // 查询用户
        User user = userMapper.selectById(uid);
        // 设置用户头像
        goodsEvaluationDTO.setUserIcon(user.getIcon());
        // 设置用户昵称
        goodsEvaluationDTO.setNickname(user.getNickname());

        // 获取评论时间
        Date time = comment.getTime();
        // 设置评论时间
        goodsEvaluationDTO.setTime(TimeUtils.dateToStringTime(time));

        // 设置评价星级
        goodsEvaluationDTO.setStars(comment.getStars());

        // 获取商品属性id
        Long gid = comment.getGoodsItemId();
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
        goodsEvaluationDTO.setParameter(parameter);

        // 设置评价内容
        goodsEvaluationDTO.setContent(comment.getContent());

        // 获取评价图片
        String images = comment.getImages();
        if (StrUtil.isNotBlank(images)) {
            String[] split = images.split(",");

            ArrayList<String> list = new ArrayList<>(Arrays.asList(split));

            // 设置评论图片
            goodsEvaluationDTO.setImages(list);
        }

        // 设置点赞数
        goodsEvaluationDTO.setLiked(comment.getLiked());

        // 获取用户
        UserDTO userDTO = UserHolder.getUser();

        // 设置用户是否点赞
        goodsEvaluationDTO.setIsLiked(0);
        goodsEvaluationDTO.setIsMe(0);
        if (userDTO != null) {
            // 设置是否用户评论
            if (comment.getUid().equals(userDTO.getId())) {
                goodsEvaluationDTO.setIsMe(1);
            }

            // 查询用户是否已对该评价进行点赞
            Double score = stringRedisTemplate.opsForZSet().score(EVALUATION_LIKED_KEY + comment.getId(), userDTO.getId().toString());
            if (score != null && score > 0) {
                goodsEvaluationDTO.setIsLiked(1);
            }
        }

        return goodsEvaluationDTO;
    }
}
