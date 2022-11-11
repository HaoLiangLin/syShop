package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.UserCollection;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface IUserCollectionService extends IService<UserCollection> {
    /**
     * 新增收藏
     * @param gid 商品id
     * @return ResultVo
     */
    ResultVo saveCollection(Long gid);

    /**
     * 删除收藏
     * @param gid 商品id
     * @return ResultVo
     */
    ResultVo delCollection(Long gid);

    /**
     * 查询收藏
     * @return ResultVo
     */
    ResultVo queryCollection();

    /**
     * 根据商品id查询商品是否收藏
     * @param gid 商品id
     * @return ResultVo
     */
    ResultVo queryCollectionByGid(Long gid);
}
