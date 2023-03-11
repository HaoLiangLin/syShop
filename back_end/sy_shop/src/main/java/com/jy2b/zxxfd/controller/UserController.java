package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.vo.ResultVO;
import com.jy2b.zxxfd.service.IUserService;
import com.jy2b.zxxfd.domain.dto.*;
import io.swagger.annotations.Api;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author 林武泰
 * 用户接口
 */
@RestController
@CrossOrigin
@RequestMapping("/users")
@Api(tags = "用户相关接口")
public class UserController {
    @Resource
    private IUserService userService;
    
    @GetMapping("/registerCode/{phone}")
    public ResultVO sendCodeByRegister(@PathVariable("phone") String phone) {
        return userService.sendCodeByRegister(phone);
    }

    @PostMapping("/register")
    public ResultVO register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    @GetMapping("/loginCode/{phone}")
    public ResultVO sendCodeByLogin(@PathVariable("phone") String phone) {
        return userService.sendCodeByLogin(phone);
    }

    @PostMapping("/login")
    public ResultVO login(@RequestHeader("User-Agent") String userAgent, @RequestBody LoginDTO loginDTO) {
        return userService.login(userAgent, loginDTO, null);
    }

    @PostMapping("/loginToken")
    public ResultVO loginToken(@RequestBody LoginKeyDTO loginKeyDTO) {
        return userService.loginToken(loginKeyDTO);
    }

    @PostMapping("/adminLogin/{adminKey}")
    public ResultVO adminLogin(@RequestHeader("User-Agent") String userAgent, @RequestBody LoginDTO loginDTO, @PathVariable("adminKey") Integer userType) {
        return userService.login(userAgent, loginDTO, userType);
    }

    @DeleteMapping("/logout")
    public ResultVO logOut(@RequestHeader("authorization") String token) {
        return userService.logOut(token);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyAuthority('user:query')")
    public ResultVO me(@RequestHeader("authorization") String token) {
        return userService.me(token);
    }

    @GetMapping("/forgetCode/{username}/{phone}")
    public ResultVO sendCodeByForgetPassword(@PathVariable("username") String username, @PathVariable("phone") String phone) {
        return userService.sendCodeByForgetPassword(username, phone);
    }

    @PostMapping("/forgetCode/check")
    public ResultVO checkForgetPasswordCode(@RequestBody ForgetPasswordDTO forgetPasswordDTO) {
        return userService.checkForgetPasswordCode(forgetPasswordDTO);
    }

    @PutMapping("/forgetPassword")
    public ResultVO updatePasswordByForgetPassword(@RequestBody ForgetPasswordDTO forgetPasswordDTO) {
        return  userService.updatePasswordByForgetPassword(forgetPasswordDTO);
    }

    @PutMapping("/updateUsername/{username}")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVO updateUsername(@PathVariable("username") String username) {
        return userService.updateUsername(username);
    }

    @GetMapping("/updatePhoneCode/{phone}")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVO sendCodeByUpdatePhone(@PathVariable("phone") String phone) {
        return userService.sendCodeByUpdatePhone(phone);
    }

    @PutMapping("/updatePhone/{phone}/{code}")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVO updatePhone(@RequestHeader("authorization") String token, @PathVariable("phone") String phone, @PathVariable("code") String code) {
        return userService.updatePhone(token, phone, code);
    }

    @PutMapping("/updatePassword")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVO updatePassword(@RequestHeader("authorization") String token, @RequestBody UpdatePasswordDTO updatePasswordDTO) {
        return userService.updatePassword(token, updatePasswordDTO);
    }

    @PutMapping("/update/icon")
    @PreAuthorize("hasAnyAuthority('user:info:update')")
    public ResultVO updateUserIcon(@RequestHeader("authorization") String token, @RequestPart("file") MultipartFile file) {
        return userService.updateUserIcon(token, file);
    }

    @PutMapping("/updateNickName")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVO updateNickName(@RequestBody UserDTO user, @RequestHeader("authorization") String jwt) {
        return userService.updateNickName(jwt, user.getNickname());
    }

    @PostMapping("/signIn")
    @PreAuthorize("hasAnyAuthority('user:query')")
    public ResultVO signIn() {
        return userService.signIn();
    }

    @GetMapping("/signIn/today")
    @PreAuthorize("hasAnyAuthority('user:query')")
    public ResultVO querySignIn() {
        return userService.querySignIn();
    }

    @GetMapping("/signIn/count")
    @PreAuthorize("hasAnyAuthority('user:query')")
    public ResultVO queryContinuityDay() {
        return userService.queryContinuityDay();
    }

    @PostMapping("/find/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('user:save','user:delete')")
    public ResultVO findUser(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @RequestBody(required = false) UserManagerDTO userManagerDTO) {
        return userService.findUser(page, size, userManagerDTO);
    }

    @PostMapping("/findSystem/{page}/{size}")
    @PreAuthorize("hasAnyAuthority('admin:all')")
    public ResultVO findSystemUser(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @RequestBody(required = false) UserManagerDTO userManagerDTO) {
        return userService.findSystemUser(page, size, userManagerDTO);
    }

    @PutMapping("/update")
    @PreAuthorize("hasAnyAuthority('user:save','user:delete')")
    public ResultVO updateUser(@RequestBody UpdateUserDTO updateUserDTO) {
        return userService.updateUser(updateUserDTO, "user");
    }

    @PutMapping("/updateSystem")
    @PreAuthorize("hasAnyAuthority('admin:all')")
    public ResultVO updateSystemUser( @RequestBody UpdateUserDTO updateUserDTO) {
        return userService.updateUser(updateUserDTO, "admin");
    }

    @PostMapping("/coerceLogout")
    @PreAuthorize("hasAnyAuthority('admin:all')")
    public ResultVO coerceLogout(@RequestHeader("authorization") String mainToken, @RequestBody CoerceLogoutDTO coerceLogoutDTO) {
        return userService.coerceLogout(mainToken, coerceLogoutDTO);
    }

    @PostMapping("/blockUp/{userId}/{blockUpTime}")
    @PreAuthorize("hasAnyAuthority('admin:all')")
    public ResultVO blockUpUser(@PathVariable("userId") Long userId, @PathVariable("blockUpTime") Long blockUpTime) {
        return userService.blockUpUser(userId, blockUpTime);
    }

    @PostMapping("/blockUp/cancel/{userId}")
    @PreAuthorize("hasAnyAuthority('admin:all')")
    public ResultVO cancelBlockUp(@PathVariable("userId") Long userId) {
        return userService.cancelBlockUp(userId);
    }

    @GetMapping("/registerCount/{startDate}/{endDate}")
    @PreAuthorize("hasAnyAuthority('end:query')")
    public ResultVO registerUserCount(@PathVariable("startDate") Long startDate, @PathVariable("endDate") Long endDate) {
        return userService.registerUserCount(startDate, endDate);
    }

    @GetMapping("/pvCount/{startDate}/{endDate}")
    @PreAuthorize("hasAnyAuthority('end:query')")
    public ResultVO userPVCount(@PathVariable("startDate") Long startDate, @PathVariable("endDate") Long endDate) {
        return userService.userPVCount(startDate, endDate);
    }

}
