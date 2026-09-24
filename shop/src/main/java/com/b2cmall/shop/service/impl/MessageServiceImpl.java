package com.b2cmall.shop.service.impl;

import com.b2cmall.shop.dao.mapper.MessageMapper;
import com.b2cmall.shop.dao.po.MessagePO;
import com.b2cmall.shop.service.MessageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MessageServiceImpl implements MessageService {
    // 类型与内容沿用课堂示例，类型1同时对应数据库的欢迎消息唯一索引。
    private static final int WELCOME_TYPE = 1;
    private static final String TITLE = "欢迎注册本系统";
    private static final String CONTENT = "系统规范，系统规约";
    private final MessageMapper mapper;

    public MessageServiceImpl(MessageMapper mapper) { this.mapper = mapper; }

    @Override
    @Transactional
    public Long saveWelcomeMessage(Long shopId) {
        MessagePO message = new MessagePO();
        message.setShopId(shopId);
        message.setTitle(TITLE);
        message.setContent(CONTENT);
        message.setMsgType(WELCOME_TYPE);
        message.setIsRead(0);
        int rows = mapper.saveMessage(message);
        if (rows != 0 && rows != 1) { throw new IllegalStateException("欢迎消息写入行数异常"); }
        Long id = rows == 1 ? message.getId() : mapper.findWelcomeId(shopId);
        if (id == null || id <= 0) { throw new IllegalStateException("欢迎消息未获得有效主键"); }
        return id;
    }
}
