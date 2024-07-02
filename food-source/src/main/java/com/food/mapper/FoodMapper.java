package com.food.mapper;

import com.food.entity.Food;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodMapper {

    int insert(Food record);

    Food findByFoodUnique(Food record);

    int updateByPrimaryKeySelective(Food record);
}