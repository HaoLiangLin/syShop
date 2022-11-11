package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.UserAddress;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IAddressService extends IService<UserAddress> {
    /**
     * 查询收货地址
     * @return ResultVo
     */
    ResultVo queryAddress();

    /**
     * 查询默认收货地址
     * @return ResultVo
     */
    ResultVo queryDefaultAddress();

    /**
     * 根据id查询收货地址
     * @param id 收货地址id
     * @return ResultVo
     */
    ResultVo queryAddressById(Long id);

    /**
     * 新增收货地址
     * @param address 收货地址信息
     * @return ResultVo
     */
    ResultVo saveAddress(UserAddress address);

    /**
     * 修改收货地址
     * @param address 收货地址信息
     * @return ResultVo
     */
    ResultVo updateAddress(UserAddress address);

    /**
     * 删除收货地址
     * @param id 收货地址id
     * @return ResultVo
     */
    ResultVo deleteAddress(Long id);

    /**
     * 批量删除收货地址
     * @param ids 收货地址id集合
     * @return ResultVo
     */
    ResultVo bulkDeleteAddress(List<Long> ids);
}
