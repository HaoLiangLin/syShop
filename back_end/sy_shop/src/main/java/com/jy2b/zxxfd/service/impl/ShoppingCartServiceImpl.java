package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.dto.ShoppingCartSaveDTO;
import com.jy2b.zxxfd.domain.dto.ShoppingCartDTO;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.domain.ShoppingCart;
import com.jy2b.zxxfd.mapper.GoodsItemMapper;
import com.jy2b.zxxfd.mapper.GoodsMapper;
import com.jy2b.zxxfd.mapper.ShoppingCartMapper;
import com.jy2b.zxxfd.service.IShoppingCartService;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements IShoppingCartService {
    @Resource
    private GoodsItemMapper itemMapper;

    @Resource
    private GoodsMapper goodsMapper;

    @Override
    public ResultVo saveCart(ShoppingCartSaveDTO cartDTO) {
        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();
        // 获取商品属性id
        Long gid = cartDTO.getGid();
        // 判断该商品属性是否存在
        GoodsItem item = itemMapper.selectById(gid);
        if (item == null) {
            return ResultVo.fail("商品属性不存在！");
        }
        // 获取数量
        Integer quantity = cartDTO.getQuantity();
        // 判断数量是否为空
        if (quantity == null || quantity <= 0) {
            return ResultVo.fail("数量不能小于零！");
        }
        // 判断数量是否大于商品属性库存
        if (quantity > item.getStock()) {
            return ResultVo.fail("商品库存不足！");
        }

        // 判断是否重复
        ShoppingCart cart = query().eq("uid", userId).eq("gid", gid).one();
        if (cart != null) {
            update().set("quantity", quantity).eq("id", cart.getId()).update();
            return ResultVo.ok("新增购物车成功！");
        }

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUid(userId);
        shoppingCart.setGid(gid);
        shoppingCart.setQuantity(quantity);

        boolean result = save(shoppingCart);
        return result ? ResultVo.ok("新增购物车成功！") : ResultVo.fail("新增购物车失败！");
    }

    @Override
    public ResultVo delCart(Long id) {
        // 查询购物车
        ShoppingCart shoppingCart = getById(id);
        if (shoppingCart == null) {
            return ResultVo.fail("购物车不存在！");
        }

        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();

        // 判断用户id是否一致
        if (!userId.equals(shoppingCart.getUid())) {
            return ResultVo.fail("购物车不存在！");
        }

        // 删除购物车
        boolean result = removeById(id);

        return result ? ResultVo.ok("删除购物车成功！") : ResultVo.fail("删除购物车失败！");
    }

    @Override
    public ResultVo updateCart(ShoppingCartDTO cartDTO) {
        // 查询购物车
        ShoppingCart shoppingCart = getById(cartDTO.getId());

        // 判断购物车是否存在
        if (shoppingCart == null) {
            return ResultVo.fail("购物车不存在！");
        }

        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();
        // 判断用户id是否一致
        if (!userId.equals(shoppingCart.getUid())) {
            return ResultVo.fail("购物车不存在！");
        }

        // 获取修改信息 商品属性id
        Long gid = cartDTO.getGid();

        // 判断商品属性是否存在
        GoodsItem item = itemMapper.selectById(gid);
        if (item == null) {
            return ResultVo.fail("商品属性已下架或不存在！");
        }
        // 获取修改信息 商品数量
        Integer quantity = cartDTO.getQuantity();
        // 判断数量是否不为空
        if (quantity == null || quantity <= 0) {
            return ResultVo.fail("数量不能等于零！");
        }
        // 判断数量是否大于商品属性库存
        if (quantity > item.getStock()) {
            return ResultVo.fail("商品库存不足！");
        }

        ShoppingCart updateCart = new ShoppingCart();
        updateCart.setId(cartDTO.getId());
        updateCart.setUid(userId);
        updateCart.setGid(gid);
        updateCart.setQuantity(quantity);

        // 修改购物车
        boolean result = updateById(updateCart);

        return result ? ResultVo.ok("修改购物车成功！") : ResultVo.fail("修改购物车失败！");
    }

    @Override
    public ResultVo queryCart() {
        // 获取登录用户id
        Long userId = UserHolder.getUser().getId();
        // 查询购物车
        List<ShoppingCart> shoppingCartList = query().eq("uid", userId).orderByDesc("create_time").list();

        // 判断购物车是否为空
        if (shoppingCartList.isEmpty()) {
            return ResultVo.ok(shoppingCartList);
        }

        ArrayList<ShoppingCartDTO> dtoArrayList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            ShoppingCartDTO cartDTO = new ShoppingCartDTO();
            // 设置购物车id
            cartDTO.setId(shoppingCart.getId());

            // 获取商品属性
            GoodsItem item = itemMapper.selectById(shoppingCart.getGid());
            // 获取商品名称
            Goods goods = goodsMapper.selectById(item.getGid());
            // 设置商品名称
            cartDTO.setName(goods.getName());

            // 设置商品图片
            cartDTO.setIcon(item.getIcon());
            // 设置商品属性
            cartDTO.setColor(item.getColor());
            cartDTO.setSize(item.getSize());
            cartDTO.setCombo(item.getCombo());
            cartDTO.setEdition(item.getEdition());

            // 设置商品单价
            cartDTO.setUnitPrice(item.getPrice());
            // 设置商品运费
            cartDTO.setPostage(goods.getPostage());
            // 设置购物车数量
            cartDTO.setQuantity(shoppingCart.getQuantity());

            // 设置总价格
            Double price = (item.getPrice() * shoppingCart.getQuantity()) + goods.getPostage();
            cartDTO.setPrice(price);

            dtoArrayList.add(cartDTO);
        }

        return ResultVo.ok(dtoArrayList);
    }
}
