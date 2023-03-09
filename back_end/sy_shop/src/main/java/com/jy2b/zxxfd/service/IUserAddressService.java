package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.UserAddress;
import com.jy2b.zxxfd.domain.dto.UserAddressDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 林武泰
 * 用户收货地址业务接口
 */
@Transactional
public interface IUserAddressService extends IService<UserAddress> {
    /**
     * 查询全部收货地址
     * @return ResultVO
     */
    ResultVO queryAllAddress();

    /**
     * 查询默认收货地址
     * @return ResultVo
     */
    ResultVO queryDefaultAddress();

    /**
     * 根据id查询收货地址
     * @param addressId 收货地址id
     * @return ResultVO
     */
    ResultVO queryAddressById(Long addressId);

    /**
     * 修改收货地址
     * @param addressId 收货地址ID
     * @param userAddressDTO 收货地址修改信息
     * @return ResultVO
     */
    ResultVO updateAddress(Long addressId, UserAddressDTO userAddressDTO);

    /**
     * 新增收货地址
     * @param userAddressDTO 收货地址新增信息
     * @return ResultVO
     */
    ResultVO saveAddress(UserAddressDTO userAddressDTO);

    /**
     * 删除收货地址
     * @param addressId 收货地址id
     * @return ResultVO
     */
    ResultVO delAddressById(Long addressId);

    /**
     * 批量删除收货地址
     * @param ids 收货地址id集合
     * @return ResultVo
     */
    ResultVO bulkDeleteAddress(List<Long> ids);
}
