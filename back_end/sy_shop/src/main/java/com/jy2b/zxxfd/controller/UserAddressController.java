package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.dto.UserAddressDTO;
import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IUserAddressService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 林武泰
 * 用户收货地址接口
 */
@RestController
@CrossOrigin
@RequestMapping("/address")
public class UserAddressController {
    @Resource
    private IUserAddressService userAddressService;

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('user:address:query')")
    public ResultVO queryAllAddress() {
        return userAddressService.queryAllAddress();
    }

    @GetMapping("/default")
    @PreAuthorize("hasAnyAuthority('address:query')")
    public ResultVO queryDefaultAddress() {
        return userAddressService.queryDefaultAddress();
    }

    @GetMapping("/query/{addressId}")
    @PreAuthorize("hasAnyAuthority('user:address:query')")
    public ResultVO queryAddressById(@PathVariable("addressId") Long addressId) {
        return userAddressService.queryAddressById(addressId);
    }

    @PutMapping("/update/{addressId}")
    @PreAuthorize("hasAnyAuthority('user:address:update')")
    public ResultVO updateAddress(@PathVariable("addressId") Long addressId, @RequestBody UserAddressDTO userAddressDTO) {
        return userAddressService.updateAddress(addressId, userAddressDTO);
    }

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('user:address:save')")
    public ResultVO saveAddress(@RequestBody UserAddressDTO userAddressDTO) {
        return userAddressService.saveAddress(userAddressDTO);
    }

    @DeleteMapping("/delete/{addressId}")
    @PreAuthorize("hasAnyAuthority('user:address:delete')")
    public ResultVO delAddressById(@PathVariable("addressId") Long addressId) {
        return userAddressService.delAddressById(addressId);
    }

    @DeleteMapping("/deletes")
    @PreAuthorize("hasAnyAuthority('address:delete')")
    public ResultVO bulkDeleteAddress(@RequestParam List<Long> ids) {
        return userAddressService.bulkDeleteAddress(ids);
    }
}
