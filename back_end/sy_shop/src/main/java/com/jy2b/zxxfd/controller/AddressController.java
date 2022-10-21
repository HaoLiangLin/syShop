package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.AddressFromDTO;
import com.jy2b.zxxfd.domain.dto.ResultVo;
import com.jy2b.zxxfd.domain.UserAddress;
import com.jy2b.zxxfd.service.IAddressService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/address")
@CrossOrigin
@Api(tags = "收货地址相关接口")
public class AddressController {
    @Resource
    private IAddressService addressService;

    @GetMapping("/list")
    @ApiOperation(value = "查询收货地址")
    @PreAuthorize("hasAnyAuthority('address:query')")
    public ResultVo queryAddress() {
        return addressService.queryAddress();
    }

    @GetMapping("/query/{id}")
    @ApiOperation(value = "根据id查询收货地址")
    @PreAuthorize("hasAnyAuthority('address:query')")
    public ResultVo queryAddressById(@PathVariable("id") Long id) {
        return addressService.queryAddressById(id);
    }

    @PostMapping("/save")
    @ApiOperation(value = "新增收货地址")
    @PreAuthorize("hasAnyAuthority('address:save')")
    public ResultVo saveAddress(@RequestBody UserAddress userAddress) {
        return addressService.saveAddress(userAddress);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改收货地址")
    @PreAuthorize("hasAnyAuthority('address:update')")
    public ResultVo updateAddress(@RequestBody UserAddress userAddress) {
        return addressService.updateAddress(userAddress);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "根据id删除收货地址")
    @PreAuthorize("hasAnyAuthority('address:delete')")
    public ResultVo deleteAddress(@PathVariable("id") Long id) {
        return addressService.deleteAddress(id);
    }

    @DeleteMapping("/deletes")
    @ApiOperation(value = "根据ids批量删除收货地址")
    @PreAuthorize("hasAnyAuthority('address:delete')")
    public ResultVo bulkDeleteAddress(@RequestBody AddressFromDTO addressFromDTO) {
        return addressService.bulkDeleteAddress(addressFromDTO.getIds());
    }
}
