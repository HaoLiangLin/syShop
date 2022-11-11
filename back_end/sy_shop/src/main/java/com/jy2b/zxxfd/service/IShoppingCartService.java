package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.dto.ShoppingCartSaveDTO;
import com.jy2b.zxxfd.domain.dto.ShoppingCartDTO;
import com.jy2b.zxxfd.domain.ShoppingCart;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IShoppingCartService extends IService<ShoppingCart> {
    /**
     * 新增购物车
     * @param cartDTO 添加购物车信息
     * @return ResultVo
     */
    ResultVo saveCart(ShoppingCartSaveDTO cartDTO);

    /**
     * 删除购物车
     * @param id 购物车id
     * @return ResultVo
     */
    ResultVo delCart(Long id);

    /**
     * 批量删除购物车
     * @param ids 购物车id信息
     * @return ResultVo
     */
    ResultVo bulkDelCart(List<Long> ids);

    /**
     * 清空购物车
     * @return ResultVo
     */
    ResultVo emptyCart();

    /**
     * 修改购物车
     * @param cartDTO 购物车信息
     * @return ResultVo
     */
    ResultVo updateCart(ShoppingCartDTO cartDTO);

    /**
     * 根据id查询购物车
     * @param id
     * @return
     */
    ResultVo queryCartById(Long id);

    /**
     * 查询购物车
     * @return ResultVo
     */
    ResultVo queryCart();
}
