package com.jy2b.zxxfd.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.jy2b.zxxfd.domain.UserAddress;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressMapper extends BaseMapper<UserAddress> {
}
