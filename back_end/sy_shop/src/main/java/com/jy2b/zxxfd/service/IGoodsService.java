package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.GoodsQueryFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsSaveFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsUpdateFromDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.Goods;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
public interface IGoodsService extends IService<Goods> {
    /**
     * 上传商品封面
     * @param files 上传图片
     * @return ResultVo
     */
    ResultVo uploadImage(MultipartFile[] files);

    /**
     * 新增商品
     * @param goodsSaveFromDTO 商品信息
     * @return ResultVo
     */
    ResultVo saveGoods(GoodsSaveFromDTO goodsSaveFromDTO);

    /**
     * 删除商品
     * @param id 商品id
     * @return ResultVo
     */
    ResultVo deleteGoods(Long id);

    /**
     * 修改商品
     * @param updateFromDTO 商品修改信息
     * @return ResultVO
     */
    ResultVo updateGoods(GoodsUpdateFromDTO updateFromDTO);

    /**
     * 管理员：根据id查询商品
     * @param id 商品id
     * @return ResultVo
     */
    ResultVo findGoodsById(Long id);

    /**
     * 管理员：分页查询所有商品
     * @param page
     * @param size
     * @param goods
     * @return
     */
    ResultVo findGoodsList(Integer page, Integer size, Goods goods);

    /**
     * 用户：根据id查询商品
     * @param id 商品id
     * @return ResultVo
     */
    ResultVo queryGoodsById(Long id);

    /**
     * 用户：分页查询所有商品
     * @param page 页码
     * @param size 每页的数量
     * @param goodsFromDTO 商品条件信息
     * @return ResultVo
     */
    ResultVo queryGoodsList(Integer page, Integer size, GoodsQueryFromDTO goodsFromDTO);
}
