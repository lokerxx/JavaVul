package com.myapp.mapper;

import com.myapp.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface UserMapper {
    User findById(@Param("id") String id);

    List<User> findUsersByIds(@Param("ids") String ids);

    List<User> findUsersByNameLike(@Param("name") String name);

    List<User> findUsersOrderBy(@Param("orderByColumn") String orderByColumn, @Param("orderByDirection") String orderByDirection);

    List<User> findUsersByNames(@Param("names") List<String> names);

    List<User> findUsersByName(String name);

    // 存在 SQL 注入风险的 MyBatis 注解方式
    @Select("SELECT * FROM users WHERE name = #{name}")
    List<User> findUsersByUsername(String name);

}