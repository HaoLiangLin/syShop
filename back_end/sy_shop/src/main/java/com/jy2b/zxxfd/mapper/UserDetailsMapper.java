package com.jy2b.zxxfd.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDetailsMapper {
    List<String> queryAuthPermsByUserId(Long userId);
}
