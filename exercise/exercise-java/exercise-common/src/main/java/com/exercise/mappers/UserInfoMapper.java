package com.exercise.mappers;

import com.exercise.campus.entity.po.UserInfo;
import com.exercise.campus.entity.query.UserInfoQuery;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户 Mapper（基于自定义泛型 Mapper，不是 MyBatis Plus）
 */
@Mapper
public interface UserInfoMapper {

    int insert(UserInfo po);

    int updateById(UserInfo po);

    int updatePassword(@Param("userId") String userId, @Param("password") String password);

    int updateStatus(@Param("userId") String userId, @Param("status") Integer status);

    int updateLastLoginTime(@Param("userId") String userId);

    int deleteById(@Param("userId") String userId);

    UserInfo selectById(@Param("userId") String userId);

    UserInfo selectByAccount(@Param("account") String account);

    UserInfo selectByEmail(@Param("email") String email);

    Integer countByAccount(@Param("account") String account);

    Integer countByEmail(@Param("email") String email);

    List<UserInfo> selectList(UserInfoQuery query);

    Integer countByQuery(UserInfoQuery query);

    /* === 平台总览统计 === */

    java.util.Map<String, Object> aggregatePlatformUsers(@Param("sinceDate") java.util.Date sinceDate);

    /* === 批量：定时任务按状态查用户 === */

    List<UserInfo> selectByStatus(@Param("status") Integer status,
                                @Param("limit") Integer limit);
}