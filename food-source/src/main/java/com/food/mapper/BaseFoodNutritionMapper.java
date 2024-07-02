package com.food.mapper;

import com.food.entity.BaseFoodNutrition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BaseFoodNutritionMapper {

    int batchInsert(@Param("list") List<BaseFoodNutrition> list);

    int updateByPrimaryKey(BaseFoodNutrition record);
}