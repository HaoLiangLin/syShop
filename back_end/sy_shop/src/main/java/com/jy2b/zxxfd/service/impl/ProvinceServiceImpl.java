package com.jy2b.zxxfd.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jy2b.zxxfd.domain.Province;
import com.jy2b.zxxfd.domain.dto.CityDTO;
import com.jy2b.zxxfd.domain.dto.ProvinceDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.mapper.ProvinceMapper;
import com.jy2b.zxxfd.service.IProvinceService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 林武泰
 */
@Service
public class ProvinceServiceImpl extends ServiceImpl<ProvinceMapper, Province> implements IProvinceService {
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public ResultVO selectProvince() {
        // 查询缓存
        String province = stringRedisTemplate.opsForValue().get("province:all");
        List<ProvinceDTO> provinceDTOList = JSONUtil.toList(province, ProvinceDTO.class);
        // 判断是否存在
        if (!provinceDTOList.isEmpty()) {
            return ResultVO.ok(provinceDTOList, "查询成功");
        }
        // 查询全部
        List<ProvinceDTO> provinceDTOS = queryAllProvince();
        // 将结果存入缓存
        stringRedisTemplate.opsForValue().set("province:all", JSONUtil.toJsonStr(provinceDTOS));
        return ResultVO.ok(provinceDTOS, "查询成功");
    }

    @Override
    public List<Province> selectAllProvince() {
        // 查询缓存
        String province = stringRedisTemplate.opsForValue().get("province:province");
        List<Province> provinces = JSONUtil.toList(province, Province.class);
        // 判断是否存在
        if (!provinces.isEmpty()) {
            return provinces;
        }
        // 获取全部省
        List<Province> provinceList = query()
                .select("name, province")
                .eq("city", "0")
                .eq("area", "0")
                .eq("town", "0").list();
        // 将结果存入缓存
        stringRedisTemplate.opsForValue().set("province:province", JSONUtil.toJsonStr(provinceList));
        return provinceList;
    }

    @Override
    public List<Province> selectAllCityByProvince(String province) {
        // 查询缓存
        String strJson = stringRedisTemplate.opsForValue().get("province:province:" + province);
        // 将json字符串转为集合
        List<Province> provinceList = JSONUtil.toList(strJson, Province.class);
        // 判断是否不为空
        if (!provinceList.isEmpty()) {
            return provinceList;
        }
        // 根据省份代码查询省份
        Province one = query().eq("province", province).last("limit 1").one();
        // 判断省份是否存在
        if (one == null) {
            return null; // 省份代码错误
        }

        // 根据省份代码获取城市
        List<Province> city = query()
                    .select("name, city")
                    .eq("province", province)
                    .ne("city", "0")
                    .eq("area", "0")
                    .eq("town", "0").list();
        // 保存入缓存
        stringRedisTemplate.opsForValue().set("province:province:" + province, JSONUtil.toJsonStr(city));
        return city;
    }

    @Override
    public List<Province> selectAllAreaByProvinceAndCity(String province, String city) {
        String cacheKey = "province:province:" + province;
        // 查询缓存
        if (StrUtil.isNotBlank(city)) {
            cacheKey += ":" + city;
        }
        String jsonStr = stringRedisTemplate.opsForValue().get(cacheKey);
        List<Province> provinces = JSONUtil.toList(jsonStr, Province.class);
        if (!provinces.isEmpty()) {
            return provinces;
        }

        List<Province> areaList;
        String key = "province:province:";
        // 判断城市代码是否为空
        if (StrUtil.isBlank(city)) {
            // 根据省代码获取全部区
            areaList = query()
                .select("name, area")
                .eq("province", province)
                .ne("city", "0")
                .ne("area", "0")
                .eq("town", "0").list();
            key += province;
        } else {
            // 根据省代码与市代码获取区
            areaList = query()
                    .select("name, area")
                    .eq("province", province)
                    .eq("city", city)
                    .ne("area", "0")
                    .eq("town", "0").list();
            key += province + ":" + city;
        }

        // 保存入缓存
        stringRedisTemplate.opsForValue().set(key , JSONUtil.toJsonStr(areaList));
        return areaList;
    }

    /**
     * 获取全部省市区
     * @return List<ProvinceDTO>
     */
    private List<ProvinceDTO> queryAllProvince() {
        List<ProvinceDTO> provinceDTOS = new ArrayList<>();
        // 获取全部省
        List<Province> list = query()
                .select("name, province")
                .eq("city", "0")
                .eq("area", "0")
                .eq("town", "0").list();
        // 存放全部省
        for (Province province : list) {
            ProvinceDTO provinceDTO = new ProvinceDTO();
            // 获取每一个省的名称和代码
            String code = province.getProvince();
            String name = province.getName();
            provinceDTO.setProvinceName(name);
            provinceDTO.setProvinceCode(code);
            // 获取每个省的市
            List<CityDTO> cityDTOS = queryAllCityByProvince(code, name);
            provinceDTO.setCity(cityDTOS);
            provinceDTOS.add(provinceDTO);
        }
        return provinceDTOS;
    }

    /**
     * 根据省代码获取全部市，包括每个市的区
     * @param province 省代码
     * @param provinceName 省名称
     * @return List<CityDTO>
     */
    private List<CityDTO> queryAllCityByProvince(String province, String provinceName) {
        List<CityDTO> cityDTOS = new ArrayList<>();
        // 获取每一个省的全部市
        List<Province> city = query()
                .select("name, city")
                .eq("province", province)
                .ne("city", "0")
                .eq("area", "0")
                .eq("town", "0").list();
        // 判断是否为直辖市
        if (city.isEmpty()) {
            CityDTO cityDTO = new CityDTO();
            cityDTO.setCityName(provinceName);
            cityDTO.setCityCode("01");
            // 获取全部区
            List<Province> areaList = query()
                    .select("name, area")
                    .eq("province", province)
                    .ne("city", "0")
                    .ne("area", "0")
                    .eq("town", "0").list();
            List<Map<String, String>> arrayList = new ArrayList<>();
            for (Province area : areaList) {
                Map<String, String> areaMap = new HashMap<>();
                areaMap.put("areaName", area.getName());
                areaMap.put("areaCode", area.getArea());
                arrayList.add(areaMap);
            }
            cityDTO.setArea(arrayList);
            cityDTOS.add(cityDTO);
            return cityDTOS;
        }
        // 获取每一个市的全部区
        for (Province c : city) {
            CityDTO cityDTO = new CityDTO();
            cityDTO.setCityName(c.getName());
            cityDTO.setCityCode(c.getCity());
            // 获取每一个市的全部区
            List<Province> areaList = query()
                    .select("name, area")
                    .eq("province", province)
                    .eq("city", c.getCity())
                    .ne("area", "0")
                    .eq("town", "0").list();
            List<Map<String, String>> arrayList = new ArrayList<>();
            for (Province area : areaList) {
                Map<String, String> areaMap = new HashMap<>();
                areaMap.put("areaName", area.getName());
                areaMap.put("areaCode", area.getArea());
                arrayList.add(areaMap);
            }
            cityDTO.setArea(arrayList);
            cityDTOS.add(cityDTO);
        }
        return cityDTOS;
    }
}
