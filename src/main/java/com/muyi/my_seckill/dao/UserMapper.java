package com.muyi.my_seckill.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muyi.my_seckill.po.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @description TODO
 * @author 历川
 * @date 2023/4/18 19:49
 * @version 1.0
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
