package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.UserAddress;
import com.jy2b.zxxfd.mapper.AddressMapper;
import com.jy2b.zxxfd.service.IAddressService;
import com.jy2b.zxxfd.utils.RegexUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

import static com.jy2b.zxxfd.contants.RedisConstants.USER_ADDRESS_KEY;
import static com.jy2b.zxxfd.contants.RedisConstants.USER_DEFAULT_ADDRESS_KEY;

@Service
public class AddressServiceImpl extends ServiceImpl<AddressMapper, UserAddress> implements IAddressService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultVo queryAddress() {
        // 获取用户id
        Long id = UserHolder.getUser().getId();
        // 从Redis中查询
        String result = stringRedisTemplate.opsForValue().get(USER_ADDRESS_KEY + id);
        if (StrUtil.isNotBlank(result)) {
            List<UserAddress> userAddresses = JSONUtil.toList(result, UserAddress.class);
            if (!userAddresses.isEmpty()) {
                return ResultVo.ok(userAddresses);
            }
        }

        // 获取收货地址
        List<UserAddress> addressList = query().eq("uid", id).list();
        if (!addressList.isEmpty()) {
            // 保存入redis
            saveAddressList(id);
        }
        return ResultVo.ok(addressList);
    }

    @Override
    public ResultVo queryDefaultAddress() {
        // 获取用户id
        Long id = UserHolder.getUser().getId();

        // 查询Redis
        String json = stringRedisTemplate.opsForValue().get(USER_DEFAULT_ADDRESS_KEY + id);
        if (StrUtil.isNotBlank(json)) {
            UserAddress address = JSONUtil.toBean(json, UserAddress.class);
            if (address != null) {
                return ResultVo.ok(address);
            }
        }

        UserAddress result = query().eq("uid", id).eq("isDefault", 1).one();
        if (result != null) {
            // 保存入Redis
            saveDefaultAddress(id);
        }
        return result != null ? ResultVo.ok(result) : ResultVo.fail("未设置默认地址");
    }

    @Override
    public ResultVo queryAddressById(Long id) {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        UserAddress address = query().eq("id", id).eq("uid", userId).one();

        if (address == null) {
            return ResultVo.fail("收货地址不存在");
        }
        return ResultVo.ok(address);
    }

    @Override
    public ResultVo saveAddress(UserAddress address) {
        ResultVo resultVo = checkAddress(address);
        if (!resultVo.getSuccess()) {
            return resultVo;
        }

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
        if (result) {
            saveAddressList(userId);
            if (address.getIsDefault() != null && address.getIsDefault() == 1) {
                saveDefaultAddress(userId);
            }
        }
        return result ? ResultVo.ok(null,"新增收货地址成功") : ResultVo.fail("新增收货地址失败");
    }

    @Override
    public ResultVo updateAddress(UserAddress address) {
        ResultVo resultVo = checkAddress(address);
        if (!resultVo.getSuccess()) {
            return resultVo;
        }

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
        if (result) {
            saveAddressList(userId);
            saveDefaultAddress(userId);
        }
        return result ? ResultVo.ok(null,"修改收货地址成功") : ResultVo.fail("修改收货地址失败");
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
        if (result) {
            saveAddressList(userId);
            saveDefaultAddress(userId);
        }
        return result ? ResultVo.ok(null,"删除收货地址成功") : ResultVo.fail("删除收货地址失败");
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
        if (success > 0) {
            saveAddressList(userId);
            saveDefaultAddress(userId);
        }
        return ResultVo.ok(null,"批量删除收货地址：" + success);
    }

    private void saveAddressList(Long userId) {
        // 获取收货地址
        List<UserAddress> addressList = query().eq("uid", userId).list();
        if (addressList.isEmpty()) {
            stringRedisTemplate.delete(USER_ADDRESS_KEY + userId);
            saveDefaultAddress(userId);
            return;
        }
        String jsonStr = JSONUtil.toJsonStr(addressList);
        // 保存入Redis
        stringRedisTemplate.opsForValue().set(USER_ADDRESS_KEY + userId, jsonStr);
    }

    private void saveDefaultAddress(Long userId) {
        // 查询默认收货地址
        UserAddress address = query().eq("uid", userId).eq("isDefault", 1).one();
        if (address == null) {
            stringRedisTemplate.delete(USER_DEFAULT_ADDRESS_KEY + userId);
            return;
        }

        String jsonStr = JSONUtil.toJsonStr(address);
        // 保存入Redis
        stringRedisTemplate.opsForValue().set(USER_DEFAULT_ADDRESS_KEY + userId, jsonStr);
    }

    private ResultVo checkAddress(UserAddress address) {
        // 获取联系人
        String name = address.getName();
        if (StrUtil.isBlank(name)) {
            return ResultVo.fail("收货人不能为空");
        }

        // 获取联系电话
        String phone = address.getPhone();
        if (StrUtil.isBlank(phone)) {
            return ResultVo.fail("联系电话不能为空");
        }
        if (RegexUtils.isPhoneInvalid(phone)) {
            return ResultVo.fail("联系电话格式错误");
        }

        String addressInfo = address.getAddress();
        if (StrUtil.isBlank(addressInfo)) {
            return ResultVo.fail("收货地址不能为空");
        }
        return ResultVo.ok();
    }
}
