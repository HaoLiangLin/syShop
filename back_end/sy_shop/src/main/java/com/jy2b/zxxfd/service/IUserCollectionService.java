package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.UserCollection;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 林武泰
 * 用户收藏业务接口
 */
@Transactional
public interface IUserCollectionService extends IService<UserCollection> {
    /**
     * 新增收藏
     * @param gid 商品id
     * @return ResultVo
     */
    ResultVO saveCollection(Long gid);

    /**
     * 删除收藏
     * @param gid 商品id
     * @return ResultVo
     */
    ResultVO delCollection(Long gid);

    /**
     * 查询收藏
     * @return ResultVo
     */
    ResultVO queryCollection();

    /**
     * 根据商品id查询商品是否收藏
     * @param gid 商品id
     * @return ResultVo
     */
    ResultVO queryCollectionByGid(Long gid);
}
