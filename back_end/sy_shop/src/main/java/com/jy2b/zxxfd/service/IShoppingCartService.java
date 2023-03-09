package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.ShoppingCartSaveDTO;
import com.jy2b.zxxfd.domain.dto.ShoppingCartDTO;
import com.jy2b.zxxfd.domain.ShoppingCart;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 林武泰
 * 用户我的购物车业务接口
 */
@Transactional
public interface IShoppingCartService extends IService<ShoppingCart> {
    /**
     * 新增购物车
     * @param cartDTO 添加购物车信息
     * @return ResultVo
     */
    ResultVO saveCart(ShoppingCartSaveDTO cartDTO);

    /**
     * 删除购物车
     * @param id 购物车id
     * @return ResultVo
     */
    ResultVO delCart(Long id);

    /**
     * 批量删除购物车
     * @param ids 购物车id信息
     * @return ResultVo
     */
    ResultVO bulkDelCart(List<Long> ids);

    /**
     * 清空购物车
     * @return ResultVo
     */
    ResultVO emptyCart();

    /**
     * 修改购物车
     * @param cartDTO 购物车信息
     * @return ResultVo
     */
    ResultVO updateCart(ShoppingCartDTO cartDTO);

    /**
     * 根据id查询购物车
     * @param id
     * @return
     */
    ResultVO queryCartById(Long id);

    /**
     * 查询购物车
     * @return ResultVo
     */
    ResultVO queryCart();
}
