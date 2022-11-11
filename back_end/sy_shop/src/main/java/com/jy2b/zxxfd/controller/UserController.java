package com.jy2b.zxxfd.controller;

import com.jy2b.zxxfd.domain.User;
import com.jy2b.zxxfd.service.IUserService;
import com.jy2b.zxxfd.utils.UserHolder;
import com.jy2b.zxxfd.domain.dto.*;
import com.jy2b.zxxfd.utils.UploadUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;

@RestController
@CrossOrigin
@RequestMapping("/users")
@Api(tags = "用户相关接口")
public class UserController {
    @Resource
    private IUserService userService;

    @GetMapping("/register/code")
    @ApiOperation(value = "注册：发送验证码")
    public ResultVo registerCode(@RequestParam("phone") String phone) {
        return userService.registerCode(phone);
    }

    @PostMapping("/register")
    @ApiOperation(value = "用户注册")
    public ResultVo register(@RequestBody RegisterFromDTO register) {
        return userService.register(register);
    }

    @GetMapping("/code")
    @ApiOperation(value = "登录：发送验证码")
    public ResultVo sendCode(@RequestParam("phone") String phone) {
        return userService.sendCode(phone);
    }

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", notes = "登录类型（loginType）：0是账号密码登录，1是手机号验证码登录")
    public ResultVo login(@RequestBody LoginFormDTO loginFormDTO) {
        return userService.login(loginFormDTO);
    }

    @GetMapping("/me")
    @ApiOperation(value = "获取登录用户信息")
    @PreAuthorize("hasAnyAuthority('user:query')")
    public ResultVo me() {
        UserDTO user = UserHolder.getUser();
        return ResultVo.ok(user);
    }

    @PutMapping("/setPassword")
    @ApiOperation(
            value = "设置/修改密码",
            notes = "当新用户未设置密码，则新密码(newPassword)作为密码；当用户已经设置密码，则需要旧密码(oldPassword)和新密码(newPassword)才能修改密码"
    )
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVo setPassword(@RequestBody PwdFormDTO pwdFormDTO) {
        return userService.setPassword(pwdFormDTO);
    }

    @PostMapping("/uploadIcon")
    @ApiOperation(value = "上传用户头像", notes = "上传成功，返回头像存放路径")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVo uploadIcon(@RequestPart("file") MultipartFile file) {
        // 保存文件
        return UploadUtils.saveFile(file, "/user/icon");
    }

    @PutMapping("/updateIcon")
    @ApiOperation(value = "修改用户头像")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVo updateIcon(@RequestBody UserDTO user, @RequestHeader("authorization") String jwt) {
        return userService.updateIcon(jwt, user.getIcon());
    }

    @PutMapping("/updateNickName")
    @ApiOperation(value = "修改用户昵称")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVo updateNickName(@RequestBody UserDTO user, @RequestHeader("authorization") String jwt) {
        return userService.updateNickName(jwt, user.getNickname());
    }

    @GetMapping("/isNotUpdateUsername")
    @ApiOperation(value = "查看用户是否未修改过账号")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVo isNotUpdateUsername() {
        return userService.isNotUpdateUsername();
    }

    @PutMapping("/updateUsername")
    @ApiOperation(value = "修改用户账号", notes = "仅未修改过账号的用户可修改一次账号")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVo updateUsername(@RequestBody UserDTO user) {
        return userService.updateUsername(user.getUsername());
    }

    @GetMapping("/codePhone")
    @ApiOperation(value = "修改手机号：发送验证码")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVo codePhone(@RequestParam("phone") String phone) {
        return userService.codePhone(phone);
    }

    @PutMapping("/updatePhone/{phone}/{code}")
    @ApiOperation(value = "修改手机号")
    @PreAuthorize("hasAnyAuthority('user:update')")
    public ResultVo updatePhone(@RequestHeader("authorization") String jwt, @PathVariable("phone") String phone, @PathVariable("code") String code) {
        return userService.updatePhone(jwt, phone, code);
    }

    @DeleteMapping("/logout")
    @ApiOperation(value = "退出登录")
    public ResultVo logOut(@RequestHeader("authorization") String token) {
        return userService.logOut(token);
    }

    @GetMapping("/codePassword")
    @ApiOperation(value = "忘记密码：发送验证码")
    public ResultVo codePassword(@RequestParam("phone") String phone) {
        return userService.codePassword(phone);
    }

    @GetMapping("/check/codePassword")
    @ApiOperation(value = "忘记密码：验证验证码")
    public ResultVo checkCodePassword(@RequestParam("phone") String phone, @RequestParam("code") String code) {
        return userService.checkCodePassword(phone, code);
    }

    @PutMapping("/updatePassword")
    @ApiOperation(value = "忘记密码：修改密码", notes = "需要手机号(phone)与新密码(newPassword)")
    public ResultVo updatePassword(@RequestBody PwdFormDTO pwdFormDTO) {
        return userService.updatePassword(pwdFormDTO);
    }

    @PostMapping("/query")
    @ApiOperation(value = "根据条件查询用户")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResultVo queryUser(@RequestBody UserQueryFromDTO userQueryFromDTO) {
        return userService.queryUser(userQueryFromDTO);
    }

    @GetMapping("/list/{page}/{size}")
    @ApiOperation(value = "根据条件分页查询用户", notes = "page：页码，size：每页数量")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResultVo queryUserList(@PathVariable("page") Integer page, @PathVariable("size") Integer size, @RequestBody(required = false) UserQueryFromDTO userQueryFromDTO) {
        return userService.queryUserList(page, size, userQueryFromDTO);
    }

    @PutMapping("/update")
    @ApiOperation(value = "修改用户")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResultVo updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }

    @DeleteMapping("/delete/{id}")
    @ApiOperation(value = "删除用户", notes = "根据用户id删除用户")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResultVo deleteUser(@PathVariable("id") Long id) {
        return userService.deleteUser(id);
    }

    @DeleteMapping("deletes")
    @ApiOperation(value = "批量删除用户", notes = "根据ids批量删除用户")
    @PreAuthorize("hasAnyAuthority('user:delete')")
    public ResultVo bulkDeleteUser(@RequestBody UserQueryFromDTO userQueryFromDTO) {
        return userService.bulkDeleteUser(userQueryFromDTO.getIds());
    }

    @PostMapping("/signIn")
    @PreAuthorize("hasAnyAuthority('user:query')")
    public ResultVo signIn() {
        return userService.signIn();
    }

    @GetMapping("/signIn/today")
    @PreAuthorize("hasAnyAuthority('user:query')")
    public ResultVo querySignIn() {
        return userService.querySignIn();
    }

    @GetMapping("/signIn/count")
    @PreAuthorize("hasAnyAuthority('user:query')")
    public ResultVo queryContinuityDay() {
        return userService.queryContinuityDay();
    }
}
