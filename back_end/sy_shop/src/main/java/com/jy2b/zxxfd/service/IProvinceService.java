package com.jy2b.zxxfd.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jy2b.zxxfd.domain.Province;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
public interface IProvinceService extends IService<Province> {
    /**
     * 获取全部省市区
     * @return ResultVO
     */
    ResultVO selectProvince();

    /**
     * 获取全部省
     * @return List<Province>
     */
    List<Province> selectAllProvince();

    /**
     * 根据省/直辖市获取全部市
     * @param province 省/直辖市代码
     * @return List<Province>
     */
    List<Province> selectAllCityByProvince(String province);

    /**
     * 根据省份城市获取全部城区
     * @param province 省
     * @param city 市
     * @return List<Province>
     */
    List<Province> selectAllAreaByProvinceAndCity(String province, String city);



}
