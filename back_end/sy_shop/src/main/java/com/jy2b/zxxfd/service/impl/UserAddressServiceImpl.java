package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Province;
import com.jy2b.zxxfd.domain.UserAddress;
import com.jy2b.zxxfd.domain.dto.UserAddressDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.UserAddressMapper;
import com.jy2b.zxxfd.service.IProvinceService;
import com.jy2b.zxxfd.service.IUserAddressService;
import com.jy2b.zxxfd.utils.RegexUtils;
import com.jy2b.zxxfd.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.jy2b.zxxfd.contants.RedisConstants.USER_ADDRESS_KEY;
import static com.jy2b.zxxfd.contants.RedisConstants.USER_DEFAULT_ADDRESS_KEY;

/**
 * @author 林武泰
 */
@Service
public class UserAddressServiceImpl extends ServiceImpl<UserAddressMapper, UserAddress> implements IUserAddressService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private IProvinceService provinceService;

    @Override
    public ResultVO queryAllAddress() {
        // 获取用户id
        Long userId = UserHolder.getUser().getId();
        // 拼接Key
        String cacheKey = USER_ADDRESS_KEY + userId;

        // 查询缓存
        Map<Object, Object> addressMap = stringRedisTemplate.opsForHash().entries(cacheKey);
        // 判断是否不为空
        if (!addressMap.isEmpty()) {
            List<UserAddress> addressList = addressMap.values().stream().map(val -> JSONUtil.toBean(val.toString(), UserAddress.class)).collect(Collectors.toList());
            return ResultVO.ok(addressList, "查询成功");
        }

        // 根据用户id查询收货地址
        List<UserAddress> addressList = query().eq("uid", userId).orderByDesc("isDefault").list();
        // 判断收货地址是否为空
        if (addressList.isEmpty()) {
            return ResultVO.ok(addressList, "查询成功");
        }

        // 将全部收货地址逐个进行缓存
        for (UserAddress userAddress : addressList) {
            // 获取收货地址ID
            Long addressId = userAddress.getId();
            // 保存进缓存
            stringRedisTemplate.opsForHash().put(cacheKey, addressId.toString(), JSONUtil.toJsonStr(userAddress));
        }

        return ResultVO.ok(addressList, "查询成功");
    }

    @Override
    public ResultVO queryDefaultAddress() {
        // 获取用户id
        Long id = UserHolder.getUser().getId();

        // 查询Redis
        String json = stringRedisTemplate.opsForValue().get(USER_DEFAULT_ADDRESS_KEY + id);
        if (StrUtil.isNotBlank(json)) {
            UserAddress address = JSONUtil.toBean(json, UserAddress.class);
            if (address != null) {
                return ResultVO.ok(address, "查询成功");
            }
        }

        UserAddress result = query().eq("uid", id).eq("isDefault", 1).one();
        if (result != null) {
            // 保存入Redis
            saveDefaultAddress(id);
        }
        return result != null ? ResultVO.ok(result, "查询成功") : ResultVO.fail("未设置默认地址");
    }

    @Override
    public ResultVO queryAddressById(Long addressId) {
        // 获取用户id
        Long userID = UserHolder.getUser().getId();
        // 获取key
        String cacheKey = USER_ADDRESS_KEY + userID;
        // 根据收货地址id查询收货地址
        Object result = stringRedisTemplate.opsForHash().get(cacheKey, addressId.toString());
        // 判断收货地址是否存在
        if (result == null) {
            return ResultVO.fail("收货地址不存在");
        }
        UserAddress address = JSONUtil.toBean(result.toString(), UserAddress.class);

        return ResultVO.ok(address, "查询成功");
    }

    @Override
    public ResultVO updateAddress(Long addressId, UserAddressDTO userAddressDTO) {
        // 获取用户Id
        Long userId = UserHolder.getUser().getId();
        // 验证收货地址
        String errMsg = checkUserAddress(userId, addressId);
        if (StrUtil.isNotBlank(errMsg)) {
            return ResultVO.fail(errMsg);
        }
        // 修改收货地址
        UpdateWrapper<UserAddress> addressUpdateWrapper = new UpdateWrapper<>();
        // 获取联系人
        String name = userAddressDTO.getName();
        // 判断联系人是否不为空
        if (StrUtil.isNotBlank(name)) {
            addressUpdateWrapper.set("name", name);
        }
        // 获取联系电话
        String tel = userAddressDTO.getPhone();
        // 判断联系电话是否不为空
        if (StrUtil.isNotBlank(tel)) {
            // 判断电话格式
            if (!RegexUtils.isPhoneInvalid(tel)) {
                addressUpdateWrapper.set("tel", tel);
            }
        }
        // 获取省份
        String province = userAddressDTO.getProvince();
        // 省份代码
        String provinceCode = null;
        // 判断省份是否存在
        if (StrUtil.isNotBlank(province)) {
            List<Province> provinceList = provinceService.selectAllProvince();
            // 过滤每一个省份，并收集到正确的
            List<Province> filterResult = provinceList.stream().filter(p -> p.getName().equals(province)).collect(Collectors.toList());
            if (filterResult.isEmpty()) {
                return ResultVO.fail("省份不存在");
            }
            provinceCode = filterResult.get(0).getProvince();
            addressUpdateWrapper.set("province", province);
        }

        // 获取城市
        String city = userAddressDTO.getCity();
        // 城市代码
        String cityCode = null;
        // 判断城市是否存在
        if (StrUtil.isNotBlank(city)) {
            List<Province> cityList = provinceService.selectAllCityByProvince(provinceCode);
            // 判断是否不为直辖市
            if (!cityList.isEmpty()) {
                String finalCity = city;
                // 过滤每一个城市，并收集到正确的
                List<Province> filterResult = cityList.stream().filter(c -> c.getName().equals(finalCity)).collect(Collectors.toList());
                if (filterResult.isEmpty()) {
                    return ResultVO.fail("城市不存在");
                }
                addressUpdateWrapper.set("city", city);
                cityCode = filterResult.get(0).getCity();
            } else {
                city = province;
                addressUpdateWrapper.set("city", city);
            }
        }

        // 获取城区
        String area = userAddressDTO.getDistrict();
        // 判断城区是否存在
        if (StrUtil.isNotBlank(area)) {
            List<Province> areaList = provinceService.selectAllAreaByProvinceAndCity(provinceCode, cityCode);
            // 过滤出正确城区
            List<Province> filterResult = areaList.stream().filter(a -> a.getName().equals(area)).collect(Collectors.toList());
            if (filterResult.isEmpty()) {
                return ResultVO.fail("城区不存在");
            }
            addressUpdateWrapper.set("area", area);
        }

        // 获取具体地址
        String location = userAddressDTO.getAddress();
        // 判断是否不为空
        if (StrUtil.isNotBlank(location)) {
            addressUpdateWrapper.set("address", location);
        }

        String cacheKey = USER_ADDRESS_KEY + userId;

        // 判断是否设置默认地址
        Integer isDefault = userAddressDTO.getIsDefault();
        if (isDefault != null) {
            if (isDefault == 0 || isDefault == 1) {
                addressUpdateWrapper.set("isDefault", isDefault);
                if (isDefault == 1) {
                    // 修改该用户全部地址为非默认地址
                    update(new UpdateWrapper<UserAddress>().eq("uid", userId).eq("isDefault", 1).set("isDefault", 0));
                    Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(cacheKey);
                    if (!entries.isEmpty()) {
                        List<UserAddress> collect = entries.values().stream().filter(val -> {
                            UserAddress address = JSONUtil.toBean(val.toString(), UserAddress.class);
                            return address.getIsDefault() == 1;
                        }).map(val -> JSONUtil.toBean(val.toString(), UserAddress.class)).collect(Collectors.toList());
                        for (UserAddress a : collect) {
                            a.setIsDefault(0);
                            stringRedisTemplate.opsForHash().put(cacheKey, a.getId().toString(), JSONUtil.toJsonStr(a));
                        }
                    }
                }
            }
        }

        addressUpdateWrapper.eq("id", addressId);

        boolean updateResult = update(addressUpdateWrapper);
        if (updateResult) {
            // 更新缓存
            UserAddress userAddress = getById(addressId);
            stringRedisTemplate.opsForHash().put(cacheKey, addressId.toString(), JSONUtil.toJsonStr(userAddress));
        } else {
            throw new RuntimeException("修改收货地址失败");
        }
        return ResultVO.ok("修改收货地址成功");
    }

    @Override
    public ResultVO saveAddress(UserAddressDTO userAddressDTO) {
        // 获取联系人
        String name = userAddressDTO.getName();
        // 判断联系人是否为空
        if (StrUtil.isBlank(name)) {
            return ResultVO.fail("联系人不能为空");
        }
        // 获取联系电话
        String tel = userAddressDTO.getPhone();
        // 判断联系电话是否为空
        if (StrUtil.isBlank(tel)) {
            return ResultVO.fail("联系电话不能为空");
        }
        // 判断手机号是否符合规范
        if (RegexUtils.isPhoneInvalid(tel)) {
            return ResultVO.fail("手机号格式错误");
        }
        // 获取省份
        String province = userAddressDTO.getProvince();
        // 判断省份是否为空
        if (StrUtil.isBlank(province)) {
            return ResultVO.fail("省份不能为空");
        }
        // 获取城市
        String city = userAddressDTO.getCity();
        // 判断城市是否为空
        if (StrUtil.isBlank(city)) {
            return ResultVO.fail("城市不能为空");
        }
        // 获取城区
        String area = userAddressDTO.getDistrict();
        // 判断城区是否为空
        if (StrUtil.isBlank(area)) {
            return ResultVO.fail("城区不能为空");
        }
        // 获取具体地址
        String address = userAddressDTO.getAddress();
        // 判断具体地址是否为空
        if (StrUtil.isBlank(address)) {
            return ResultVO.fail("详细地址不能为空");
        }

        // 获取全部省份
        List<Province> provinceList = provinceService.selectAllProvince();
        // 判断省份是否存在
        List<Province> provinceFilter = provinceList.stream().filter(p -> p.getName().equals(province)).collect(Collectors.toList());
        if (provinceFilter.isEmpty()) {
            return ResultVO.fail("省份不存在");
        }
        // 获取省份代码
        String provinceCode = provinceFilter.get(0).getProvince();

        // 根据省份代码获取该省份城市
        List<Province> cityList = provinceService.selectAllCityByProvince(provinceCode);
        String cityCode = null;
        // 判断是否不为直辖市
        if (cityList != null) {
            // 判断城市是否存在
            if (cityList.isEmpty()) {
                return ResultVO.fail("城市不存在");
            }
            List<Province> cityFilter = cityList.stream().filter(c -> c.getName().equals(city)).collect(Collectors.toList());
            if (cityFilter.isEmpty()) {
                return ResultVO.fail("城市不存在");
            }
           cityCode = cityFilter.get(0).getCity();
        }

        // 获取该城市城区
        List<Province> areaList = provinceService.selectAllAreaByProvinceAndCity(provinceCode, cityCode);
        // 判断城区是否存在
        List<Province> areaFilter = areaList.stream().filter(a -> a.getName().equals(area)).collect(Collectors.toList());
        if (areaFilter.isEmpty()) {
            return ResultVO.fail("城区不存在");
        }

        // 获取用户id
        Long userID = UserHolder.getUser().getId();
        // 获取key
        String cacheKey = USER_ADDRESS_KEY + userID;

        // 新增收货地址
        UserAddress userAddress = new UserAddress();
        userAddress.setUid(userID);
        userAddress.setName(name);
        userAddress.setPhone(tel);
        userAddress.setProvince(province);
        userAddress.setCity(city);
        userAddress.setDistrict(area);
        userAddress.setAddress(address);
        Integer isDefault = userAddressDTO.getIsDefault();
        // 判断是否默认收货地址
        if (isDefault != null) {
            if (isDefault == 1 || isDefault == 0) {
                userAddress.setIsDefault(isDefault);
            }
            if (isDefault == 1) {
                // 修改该用户全部地址为非默认地址
                boolean update = update(new UpdateWrapper<UserAddress>().eq("uid", userID).set("isDefault", 0));
                if (!update) {
                    return ResultVO.fail("新增失败");
                }
                Map<Object, Object> entries = stringRedisTemplate.opsForHash().entries(cacheKey);
                if (!entries.isEmpty()) {
                    List<UserAddress> collect = entries.values().stream().filter(val -> {
                        UserAddress add = JSONUtil.toBean(val.toString(), UserAddress.class);
                        return add.getIsDefault() == 1;
                    }).map(val -> JSONUtil.toBean(val.toString(), UserAddress.class)).collect(Collectors.toList());
                    for (UserAddress a : collect) {
                        a.setIsDefault(0);
                        stringRedisTemplate.opsForHash().put(cacheKey, a.getId().toString(), JSONUtil.toJsonStr(a));
                    }

                }
            }
        } else {
            userAddress.setIsDefault(0);
        }

        boolean saveResult = save(userAddress);
        if (!saveResult) {
            throw new RuntimeException("新增失败");
        }
        stringRedisTemplate.opsForHash().put(cacheKey, userAddress.getId().toString(), JSONUtil.toJsonStr(userAddress));
        return ResultVO.ok(userAddress, "新增成功");
    }

    @Override
    public ResultVO delAddressById(Long addressId) {
        // 获取用户Id
        Long userId = UserHolder.getUser().getId();
        // 获取key
        String cacheKey = USER_ADDRESS_KEY + userId;
        // 验证收货地址
        String errMsg = checkUserAddress(userId, addressId);
        if (StrUtil.isNotBlank(errMsg)) {
            return ResultVO.fail(errMsg);
        }

        // 删除收货地址
        boolean delResult = removeById(addressId);
        if (delResult) {
            stringRedisTemplate.opsForHash().delete(cacheKey, addressId.toString());
        }
        return delResult ? ResultVO.ok("删除成功") : ResultVO.fail("删除失败");
    }

    @Override
    public ResultVO bulkDeleteAddress(List<Long> ids) {
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
        return ResultVO.ok(null,"批量删除收货地址：" + success);
    }

    /**
     * 验证收货地址
     * @param userId 用户id
     * @param addressId 收货地址id
     * @return 错误消息，null代表通过
     */
    private String checkUserAddress(Long userId, Long addressId) {
        // 获取key
        String cacheKey = USER_ADDRESS_KEY + userId;

        Object result = stringRedisTemplate.opsForHash().get(cacheKey, addressId.toString());
        // 判断收货地址是否存在
        if (result == null) {
            return "收货地址不存在";
        }

        return null;
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
}
