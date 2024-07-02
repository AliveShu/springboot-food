package com.food.mapper;

import com.food.entity.BaseFood;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface BaseFoodMapper {

    @Select("SHOW TABLES like 'base_food%'")
    List<String> existCurrentTable();

    int insert(BaseFood record);

    int updateById(BaseFood record);

    BaseFood existCurrentObj(BaseFood baseFood);

}