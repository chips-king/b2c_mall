package com.b2cmall.shop.dao.mapper;

import com.b2cmall.shop.dao.po.ShopPO;
import com.b2cmall.shop.dao.po.ShopInitializationTaskPO;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ShopMapper {
    ShopPO findByName(@Param("shopName") String shopName);
    int markEmployeeInitialized(@Param("id") Long id, @Param("attempt") long attempt);
    int welcomeCount(@Param("id") Long id);
    int registerShop(ShopPO shop);
    ShopPO findById(@Param("id") Long id);
    int createInitializationTask(@Param("id") Long id, @Param("deadline") long deadline);
    ShopInitializationTaskPO findInitializationTask(@Param("id") Long id);
    int advanceInitializationTask(@Param("id") Long id, @Param("attempt") long attempt, @Param("deadline") long deadline);
    int changeInitializationStatus(@Param("id") Long id, @Param("expected") String expected, @Param("status") String status);
    int finishInitialization(@Param("id") Long id, @Param("attempt") long attempt, @Param("initStatus") String initStatus);
    int recordInitializationFailure(@Param("id") Long id, @Param("attempt") long attempt, @Param("failureCode") String failureCode);
    List<ShopInitializationTaskPO> findExpiredInitializationTasks(@Param("now") long now);
}
