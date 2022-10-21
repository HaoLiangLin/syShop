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
     * @param userCollection 收藏信息
     * @return ResultVo
     */
    ResultVo delCollection(UserCollection userCollection);

    /**
     * 查询收藏
     * @return ResultVo
     */
    ResultVo queryCollection();
}
