package com.food.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

import static com.food.entity.FoodUnit.*;

@Getter
@Setter
public class FoodEnergyComposition implements Serializable {

    /**
    * 主键
    */
    private Long id;

    /**
    * 外键
    */
    private Long foodId;

    /**
    * 可食用部分的数量
    */
    private BigDecimal edible = default_value;

    /**
    * 可食用部分的单位
    */
    private String edibleUnit = percentile;

    /**
    * 水分含量
    */
    private BigDecimal moistureContent = default_value;

    /**
    * 水分含量单位
    */
    private String moistureContentUnit = g;

    /**
    * 能量值
    */
    private BigDecimal energy = default_value;

    /**
    * 能量值单位
    */
    private String energyUnit = KJ;

    /**
    * 蛋白质含量
    */
    private BigDecimal protein = default_value;

    /**
    * 蛋白质含量单位
    */
    private String proteinUnit = g;

    /**
    * 脂肪含量
    */
    private BigDecimal fat = default_value;

    /**
    * 脂肪含量单位
    */
    private String fatUnit = g;

    /**
    * 胆固醇含量
    */
    private BigDecimal cholesterol = default_value;

    /**
    * 胆固醇含量单位
    */
    private String cholesterolUnit = g;

    /**
    * 灰分含量
    */
    private BigDecimal ashContent = default_value;

    /**
    * 灰分含量单位
    */
    private String ashContentUnit = g;

    /**
    * 碳水化合物含量
    */
    private BigDecimal carbohydrate = default_value;

    /**
    * 碳水化合物含量单位
    */
    private String carbohydrateUnit = g;

    /**
    * 膳食纤维含量
    */
    private BigDecimal dietaryFiber = default_value;

    /**
    * 膳食纤维含量单位
    */
    private String dietaryFiberUnit = g;

    /**
    * 胡萝卜素
    */
    private BigDecimal carotene = default_value;

    /**
    * 胡萝卜素含量单位
    */
    private String caroteneUnit = mg;

    /**
    * 维生素-A
    */
    private BigDecimal vitaminA = default_value;

    /**
    * 维生素-A含量单位
    */
    private String vitaminAUnit = mg;

    /**
    * α-TE
    */
    private BigDecimal aTe = default_value;

    /**
    * α-TE含量单位
    */
    private String aTeUnit = mg;

    /**
    * 硫胺素
    */
    private BigDecimal thiamine = default_value;

    /**
    * 硫胺素含量单位
    */
    private String thiamineUnit = mg;

    /**
    * 核黄素
    */
    private BigDecimal riboflavin = default_value;

    /**
    * 核黄素含量单位
    */
    private String riboflavinUnit = mg;

    /**
    * 烟酸
    */
    private BigDecimal nicotinicAcid = default_value;

    /**
    * 烟酸含量单位
    */
    private String nicotinicAcidUnit = mg;

    /**
    * 维生素-C
    */
    private BigDecimal vitaminC = default_value;

    /**
    * 维生素-C含量单位
    */
    private String vitaminCUnit = mg;

    /**
    * 钙
    */
    private BigDecimal calcium = default_value;

    /**
    * 钙含量单位
    */
    private String calciumUnit = mg;

    /**
    * 磷
    */
    private BigDecimal phosphorus = default_value;

    /**
    * 磷含量单位
    */
    private String phosphorusUnit = mg;

    /**
    * 钾
    */
    private BigDecimal potassium = default_value;

    /**
    * 钾含量单位
    */
    private String potassiumUnit = mg;

    /**
    * 钠
    */
    private BigDecimal sodium = default_value;

    /**
    * 钠含量单位
    */
    private String sodiumUnit = mg;

    /**
    * 镁
    */
    private BigDecimal magnesium = default_value;

    /**
    * 镁含量单位
    */
    private String magnesiumUnit = mg;

    /**
    * 铁
    */
    private BigDecimal iron = default_value;

    /**
    * 铁含量单位
    */
    private String ironUnit = mg;

    /**
    * 锌
    */
    private BigDecimal zinc = default_value;

    /**
    * 锌含量单位
    */
    private String zincUnit = mg;

    /**
    * 硒
    */
    private BigDecimal selenium = default_value;

    /**
    * 硒含量单位
    */
    private String seleniumUnit = microgram;

    /**
    * 铜
    */
    private BigDecimal copper = default_value;

    /**
    * 铜含量单位
    */
    private String copperUnit = mg;

    /**
    * 锰
    */
    private BigDecimal manganese = default_value;

    /**
    * 锰含量单位
    */
    private String manganeseUnit = mg;

    /**
    * 碘
    */
    private BigDecimal iodine = default_value;

    /**
    * 碘含量单位
    */
    private String iodineUnit = phosphorusUnit;

    /**
    * 饱和脂肪酸
    */
    private BigDecimal saturatedFattyAcids = default_value;

    /**
    * 饱和脂肪酸含量单位
    */
    private String saturatedFattyAcidsUnit = g;

    /**
    * 单不饱和脂肪酸
    */
    private BigDecimal monounsaturatedFattyAcids = default_value;

    /**
    * 单不饱和脂肪酸含量单位
    */
    private String monounsaturatedFattyAcidsUnit = g;

    /**
    * 多不饱和脂肪酸
    */
    private BigDecimal polyunsaturatedFattyAcids = default_value;

    /**
    * 多不饱和脂肪酸含量单位
    */
    private String polyunsaturatedFattyAcidsUnit = g;

    /**
    * 总脂肪酸
    */
    private BigDecimal totalFattyAcids = default_value;

    /**
    * 总脂肪酸含量单位
    */
    private String totalFattyAcidsUnit  = g;


    private static final long serialVersionUID = 1L;

}