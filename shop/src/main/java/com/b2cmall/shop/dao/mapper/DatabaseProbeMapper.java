package com.b2cmall.shop.dao.mapper;

import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface DatabaseProbeMapper {
    Map<String, Object> probe();
}
