package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.ShoppingCartSaveDTO;
import com.jy2b.zxxfd.domain.dto.ShoppingCartDTO;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.domain.ShoppingCart;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.GoodsItemMapper;
import com.jy2b.zxxfd.mapper.GoodsMapper;
import com.jy2b.zxxfd.mapper.ShoppingCartMapper;
import com.jy2b.zxxfd.service.IShoppingCartService;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author 林武泰
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {
    @Resource
    private GoodsItemMapper itemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public ResultVO saveCart(ShoppingCartSaveDTO cartDTO) {
        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();
        // 获取商品属性id
        Long gid = cartDTO.getGid();
        // 判断该商品属性是否存在
        GoodsItem item = itemMapper.selectById(gid);
        if (item == null) {
            return ResultVO.fail("商品属性不存在");
        }
        // 获取数量
        Integer quantity = cartDTO.getQuantity();
        // 判断数量是否为空
        if (quantity == null || quantity <= 0) {
            return ResultVO.fail("数量不能小于零");
        }
        // 判断数量是否大于商品属性库存
        if (quantity > item.getStock()) {
            return ResultVO.fail("商品库存不足");
        }

        // 判断是否重复
        ShoppingCart cart = query().eq("uid", userId).eq("gid", gid).one();
        if (cart != null) {
            update().set("quantity", quantity).eq("id", cart.getId()).update();
            return ResultVO.ok(null, "新增购物车成功");
        }

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUid(userId);
        shoppingCart.setGid(gid);
        shoppingCart.setQuantity(quantity);

        boolean result = save(shoppingCart);
        return result ? ResultVO.ok(shoppingCart,"新增购物车成功") : ResultVO.fail("新增购物车失败");
    }

    @Override
    public ResultVO delCart(Long id) {
        // 查询购物车
        ShoppingCart shoppingCart = getById(id);
        if (shoppingCart == null) {
            return ResultVO.fail("购物车不存在");
        }

        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();

        // 判断用户id是否一致
        if (!userId.equals(shoppingCart.getUid())) {
            return ResultVO.fail("购物车不存在");
        }

        // 删除购物车
        boolean result = removeById(id);

        return result ? ResultVO.ok(shoppingCart,"删除购物车成功") : ResultVO.fail("删除购物车失败");
    }

    @Override
    public ResultVO bulkDelCart(List<Long> ids) {
        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();

        int success = 0;
        if (!ids.isEmpty()) {
            for (Long id : ids) {
                boolean result = remove(new QueryWrapper<ShoppingCart>().eq("id", id).eq("uid", userId));
                if (result) {
                    success++;
                }
            }
        }
        return ResultVO.ok(null,"批量删除购物车：" + success);
    }

    @Override
    public ResultVO emptyCart() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 查询购物车
        Integer count = query().eq("uid", userId).count();
        if (count < 1) {
            return ResultVO.ok();
        }
        boolean result = remove(new QueryWrapper<ShoppingCart>().eq("uid", userId));
        return result ? ResultVO.ok(null,"购物车已清空") : ResultVO.fail("清空购物车失败");
    }

    @Override
    public ResultVO updateCart(ShoppingCartDTO cartDTO) {
        // 查询购物车
        ShoppingCart shoppingCart = getById(cartDTO.getId());

        // 判断购物车是否存在
        if (shoppingCart == null) {
            return ResultVO.fail("购物车不存在");
        }

        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();
        // 判断用户id是否一致
        if (!userId.equals(shoppingCart.getUid())) {
            return ResultVO.fail("购物车不存在");
        }

        // 获取修改信息 商品属性id
        Long gid = cartDTO.getGoodsItemId();

        // 判断商品属性是否存在
        GoodsItem item = itemMapper.selectById(gid);
        if (item == null) {
            return ResultVO.fail("商品属性已下架或不存在");
        }
        // 获取修改信息 商品数量
        Integer quantity = cartDTO.getQuantity();
        // 判断数量是否不为空
        if (quantity == null || quantity <= 0) {
            return ResultVO.fail("数量不能等于零");
        }
        // 判断数量是否大于商品属性库存
        if (quantity > item.getStock()) {
            return ResultVO.fail("商品库存不足");
        }

        ShoppingCart updateCart = new ShoppingCart();
        updateCart.setId(cartDTO.getId());
        updateCart.setUid(userId);
        updateCart.setGid(gid);
        updateCart.setQuantity(quantity);

        // 修改购物车
        boolean result = updateById(updateCart);

        return result ? ResultVO.ok(null,"修改购物车成功") : ResultVO.fail("修改购物车失败");
    }

    @Override
    public ResultVO queryCartById(Long id) {
        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();
        // 根据id查询购物车
        ShoppingCart shoppingCart = getById(id);
        // 判断购物车是否为空
        if (shoppingCart == null) {
            return ResultVO.fail("购物车不存在");
        }
        // 判断是否用户购物车
        if (!shoppingCart.getUid().equals(userId)) {
            return ResultVO.fail("购物车不存在");
        }
        ShoppingCartDTO shoppingCartDTO = setShoppingCartDTO(shoppingCart);

        return ResultVO.ok(shoppingCartDTO, "查询成功");
    }

    @Override
    public ResultVO queryCart() {
        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();
        // 查询购物车
        List<ShoppingCart> shoppingCartList = query().eq("uid", userId).orderByDesc("create_time").list();

        // 判断购物车是否为空
        if (shoppingCartList.isEmpty()) {
            return ResultVO.ok(shoppingCartList, "查询成功");
        }

        ArrayList<ShoppingCartDTO> dtoArrayList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            ShoppingCartDTO cartDTO = setShoppingCartDTO(shoppingCart);

            dtoArrayList.add(cartDTO);
        }

        return ResultVO.ok(dtoArrayList, "查询成功");
    }

    private ShoppingCartDTO setShoppingCartDTO(ShoppingCart shoppingCart) {
        ShoppingCartDTO cartDTO = new ShoppingCartDTO();
        // 设置购物车id
        cartDTO.setId(shoppingCart.getId());

        // 获取商品属性
        GoodsItem item = itemMapper.selectById(shoppingCart.getGid());
        // 获取商品名称
        Goods goods = goodsMapper.selectById(item.getGid());
        // 设置商品名称
        cartDTO.setName(goods.getName());

        // 设置商品id
        cartDTO.setGoodsId(item.getGid());
        // 设置商品图片
        cartDTO.setIcon(item.getIcon());
        // 设置商品属性id
        cartDTO.setGoodsItemId(item.getId());
        // 设置商品属性
        cartDTO.setColor(item.getColor());
        cartDTO.setSize(item.getSize());
        cartDTO.setCombo(item.getCombo());
        cartDTO.setEdition(item.getEdition());
        cartDTO.setStock(item.getStock());

        // 设置商品单价
        cartDTO.setUnitPrice(item.getPrice());
        // 设置商品运费
        cartDTO.setPostage(goods.getPostage());
        // 设置购物车数量
        cartDTO.setQuantity(shoppingCart.getQuantity());

        // 设置总价格
        Double price = (item.getPrice() * shoppingCart.getQuantity()) + goods.getPostage();
        cartDTO.setPrice(price);

        return cartDTO;
    }
}
