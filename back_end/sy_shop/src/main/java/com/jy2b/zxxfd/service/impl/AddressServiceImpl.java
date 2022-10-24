package com.jy2b.zxxfd.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.UserAddress;
import com.jy2b.zxxfd.mapper.AddressMapper;
import com.jy2b.zxxfd.service.IAddressService;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, UserAddress> implements IAddressService {
    @Override
    public ResultVo queryAddress() {
        // 获取用户id
        Long id = UserHolder.getUser().getId();
        // 获取收货地址
        List<UserAddress> addressList = query().eq("uid", id).list();
        return ResultVo.ok(addressList);
    }

    @Override
    public ResultVo queryAddressById(Long id) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        UserAddress address = query().eq("id", id).eq("uid", userId).one();
        return ResultVo.ok(address);
    }

    @Override
    public ResultVo saveAddress(UserAddress address) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 判断新增是否为默认收货地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            // 是，修改用户全部收货地址为非默认
            update().set("isDefault", 0).eq("uid", userId).update();
        }
        // 指定用户id
        address.setUid(userId);
        // 新增收货地址
        boolean result = save(address);
        return result ? ResultVo.ok("新增收货地址成功") : ResultVo.fail("新增收货地址失败");
    }

    @Override
    public ResultVo updateAddress(UserAddress address) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 获取将要修改的收货地址
        UserAddress userAddress = getById(address.getId());
        // 判断是否存在
        if (userAddress == null) {
            return ResultVo.fail("收货地址不存在");
        }
        // 判断是否属于当前用户
        if (!Objects.equals(userAddress.getUid(), userId)) {
            return ResultVo.fail("收货地址不存在");
        }
        // 判断是否为默认收货地址
        if (address.getIsDefault() != null && address.getIsDefault() == 1) {
            // 是，修改用户全部收货地址为非默认
            update().set("isDefault", 0).eq("uid", userId).update();
        }

        UpdateWrapper<UserAddress> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", address.getId()).eq("uid", userId);
        // 修改收货地址
        boolean result = update(address, updateWrapper);
        return result ? ResultVo.ok("修改收货地址成功") : ResultVo.fail("修改收货地址失败");
    }

    @Override
    public ResultVo deleteAddress(Long id) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();

        // 获取将要删除的收货地址
        UserAddress userAddress = getById(id);

        // 判断收货地址是否存在
        if (userAddress == null) {
            return ResultVo.fail("收货地址不存在");
        }

        // 判断收货地址是否为执行删除用户的
        if (!Objects.equals(userId, userAddress.getUid())) {
            return ResultVo.fail("收货地址不存在");
        }

        boolean result = removeById(id);

        return result ? ResultVo.ok("删除收货地址成功") : ResultVo.fail("删除收货地址失败");
    }

    @Override
    public ResultVo bulkDeleteAddress(List<Long> ids) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 删除成功的id集合
        int success = 0;
        for (Long id : ids) {
            // 获取将要删除的收货地址
            UserAddress userAddress = getById(id);

            // 判断收货地址是否存在
            if (userAddress == null) {
                continue;
            }

            // 判断收货地址是否为执行删除用户的
            if (!Objects.equals(userId, userAddress.getUid())) {
                continue;
            }

            boolean result = removeById(id);
            if (result) {
                success++;
            }
        }
        return ResultVo.ok("批量删除收货地址：" + success);
    }
}
