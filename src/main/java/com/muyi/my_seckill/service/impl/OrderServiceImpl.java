package com.muyi.my_seckill.service.impl;

import com.muyi.my_seckill.dao.OrderInfoMapper;
import com.muyi.my_seckill.po.OrderInfo;
import com.muyi.my_seckill.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author 历川
 * @version 1.0
 * @description TODO
 * @date 2023/4/26 18:30
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    OrderInfoMapper orderInfoMapper;
    @Override
    public long addOrder(OrderInfo orderInfo) {
        return orderInfoMapper.insert(orderInfo);
    }
}
