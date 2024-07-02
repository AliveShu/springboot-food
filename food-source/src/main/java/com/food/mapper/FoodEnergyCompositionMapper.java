package com.food.mapper;

import com.food.entity.FoodEnergyComposition;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FoodEnergyCompositionMapper {

    int insert(FoodEnergyComposition record);

    int updateByFoodId(FoodEnergyComposition record);
}