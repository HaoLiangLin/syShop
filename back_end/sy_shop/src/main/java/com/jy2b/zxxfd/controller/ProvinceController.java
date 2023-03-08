package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.Province;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IProvinceService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/province")
public class ProvinceController {
    @Resource
    private IProvinceService provinceService;

    @GetMapping("/all")
    public ResultVO selectProvince() {
        return provinceService.selectProvince();
    }

    @GetMapping("/allProvince")
    public ResultVO selectAllProvince() {
        List<Province> provinceList = provinceService.selectAllProvince();
        return ResultVO.ok(provinceList, "查询成功");
    }

    @GetMapping("/city")
    public ResultVO selectAllCityByProvince(@RequestParam("province") String province) {
        List<Province> provinceList = provinceService.selectAllCityByProvince(province);
        if (provinceList == null) {
            return ResultVO.fail("省代码错误");
        }
        return ResultVO.ok(provinceList, "查询成功");
    }

    @GetMapping("/area")
    ResultVO selectAllAreaByProvinceAndCity(@RequestParam("province") String province, @RequestParam(value = "city", required = false) String city) {
        List<Province> provinceList = provinceService.selectAllAreaByProvinceAndCity(province, city);
        return ResultVO.ok(provinceList, "查询成功");
    }
}
