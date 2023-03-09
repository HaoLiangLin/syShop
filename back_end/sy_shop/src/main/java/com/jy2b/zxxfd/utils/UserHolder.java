package com.jy2b.zxxfd.utils;

import com.jy2b.zxxfd.domain.dto.UserDTO;

/**
 * @author 林武泰
 * 用户线程
 */
public class UserHolder {
    private static final ThreadLocal<UserDTO> threadLocal = new ThreadLocal<>();

    public static void saveUser(UserDTO userDTO) {
        threadLocal.set(userDTO);
    }

    public static UserDTO getUser() {
        return threadLocal.get();
    }

    public static void removeUser() {
        threadLocal.remove();
    }
}
