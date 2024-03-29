package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.GoodsQueryFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsSaveFromDTO;
import com.jy2b.zxxfd.domain.dto.GoodsUpdateFromDTO;
import com.jy2b.zxxfd.domain.Goods;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 林武泰
 * 商品业务接口
 */
@Transactional
public interface IGoodsService extends IService<Goods> {
    /**
     * 新增商品
     * @param goodsSaveFromDTO 商品信息
     * @return ResultVo
     */
    ResultVO saveGoods(GoodsSaveFromDTO goodsSaveFromDTO);

    /**
     * 删除商品
     * @param id 商品id
     * @return ResultVo
     */
    ResultVO deleteGoods(Long id);

    /**
     * 修改商品
     * @param updateFromDTO 商品修改信息
     * @return ResultVO
     */
    ResultVO updateGoods(GoodsUpdateFromDTO updateFromDTO);

    /**
     * 上传或修改商品封面
     * @param id 商品Id
     * @param files 封面图片文件
     * @return ResultVO
     */
    ResultVO uploadOrUpdateGoodsImages(Long id, MultipartFile[] files);

    /**
     * 删除商品封面
     * @param id
     * @return
     */
    ResultVO removeGoodsImages(Long id);

    /**
     * 管理员：根据id查询商品
     * @param id 商品id
     * @return ResultVo
     */
    ResultVO findGoodsById(Long id);

    /**
     * 管理员：分页查询所有商品
     * @param page 页码
     * @param size 数量
     * @param goods 商品信息
     * @return ResultVO
     */
    ResultVO findGoodsList(Integer page, Integer size, Goods goods);

    /**
     * 用户：根据id查询商品
     * @param id 商品id
     * @return ResultVo
     */
    ResultVO queryGoodsById(Long id);

    /**
     * 用户：分页查询所有商品
     * @param page 页码
     * @param size 每页的数量
     * @param goodsFromDTO 商品条件信息
     * @return ResultVo
     */
    ResultVO queryGoodsList(Integer page, Integer size, GoodsQueryFromDTO goodsFromDTO);
}
