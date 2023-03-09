package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.GoodsItemFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsItemQueryFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsItemSaveFromDTO;
import com.jy2b.zxxfd.domain.GoodsItem;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 林武泰
 * 商品属性业务接口
 */
@Transactional
public interface IGoodsItemService extends IService<GoodsItem> {
    /**
     * 新增商品属性
     * @param itemFromDTO 商品属性信息
     * @return ResultVo
     */
    ResultVO saveItem(GoodsItemSaveFromDTO itemFromDTO);

    /**
     * 删除商品属性
     * @param id 商品属性id
     * @return ResultVo
     */
    ResultVO deleteItem(Long id);

    /**
     * 修改商品属性
     * @param itemFromDTO 商品属性信息
     * @return ResultVo
     */
    ResultVO updateItem(Long id, GoodsItemSaveFromDTO itemFromDTO);

    /**
     * 上传或修改商品属性图标
     * @param id 商品属性ID
     * @param file 图标文件
     * @return ResultVO
     */
    ResultVO uploadOrUpdateItemIcon(Long id, MultipartFile file);

    /**
     * 查询全部商品属性
     * @param id 商品id
     * @param itemFromDTO 查询信息
     * @return
     */
    ResultVO queryItemList(Long id, GoodsItemQueryFromDTO itemFromDTO);

    /**
     * 分页查询商品属性
     * @param page 页码
     * @param size 每页数量
     * @param itemFromDTO 查询信息
     * @return ResultVo
     */
    ResultVO queryItemListPage(Integer page, Integer size, GoodsItemQueryFromDTO itemFromDTO);

    /**
     * 根据商品id获取商品属性
     * @param gid 商品id
     * @return ResultVO
     */
    ResultVO queryItemByGid(Long gid);

    /**
     * 根据商品属性条件获取商品属性
     * @param itemFromDTO 条件信息
     * @return ResultVo
     */
    ResultVO queryItem(GoodsItemFromDTO itemFromDTO);
}
