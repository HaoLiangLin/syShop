package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.GoodsItemFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsItemQueryFromDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.dto.GoodsItemSaveFromDTO;
import com.jy2b.zxxfd.domain.GoodsItem;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IGoodsItemService extends IService<GoodsItem> {
    /**
     * 新增商品属性
     * @param itemFromDTO 商品属性信息
     * @return ResultVo
     */
    ResultVo saveItem(GoodsItemSaveFromDTO itemFromDTO);

    /**
     * 删除商品属性
     * @param id 商品属性id
     * @return ResultVo
     */
    ResultVo deleteItem(Long id);

    /**
     * 修改商品属性
     * @param itemFromDTO 商品属性信息
     * @return ResultVo
     */
    ResultVo updateItem(Long id, GoodsItemSaveFromDTO itemFromDTO);

    /**
     * 查询全部商品属性
     * @param id 商品id
     * @param itemFromDTO 查询信息
     * @return
     */
    ResultVo queryItemList(Long id, GoodsItemQueryFromDTO itemFromDTO);

    /**
     * 分页查询商品属性
     * @param page 页码
     * @param size 每页数量
     * @param itemFromDTO 查询信息
     * @return ResultVo
     */
    ResultVo queryItemListPage(Integer page, Integer size, GoodsItemQueryFromDTO itemFromDTO);

    /**
     * 根据商品id获取商品属性
     * @param gid 商品id
     * @return ResultVO
     */
    ResultVo queryItemByGid(Long gid);

    /**
     * 根据商品属性条件获取商品属性
     * @param itemFromDTO 条件信息
     * @return ResultVo
     */
    ResultVo queryItem(GoodsItemFromDTO itemFromDTO);
}
