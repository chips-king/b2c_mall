package com.b2cmall.shop.dao.mapper;

import com.b2cmall.shop.dao.po.MessagePO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageMapper {
    int saveMessage(MessagePO message);
    Long findWelcomeId(@Param("shopId") Long shopId);
}
