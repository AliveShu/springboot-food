package com.food.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Getter
@Setter
public class BaseFoodNutrition implements Serializable {

    final String empty = "";
    final String numberRegular = "(%|kJ|mg|g|μg)";
    final String unitRegular = "([0-9]\\d*\\.\\d*|0\\.\\d*[1-9]\\d*$|^[1-9]\\d*|0$)";

    /**
    * id
    */
    private Long id;

    /**
    * 食物id
    */
    private Long foodId;

    /**
    * 营养名称
    */
    private String nutritionName;

    /**
    * 营养含量
    */
    private String nutritionContent;

    private String number;

    private String unit;

    /**
    * 同类排名
    */
    private String peerRanking;

    /**
    * 同类均值
    */
    private String peerMean;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 修改时间
    */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    public void setNutritionContent(String nutritionContent) {
        this.nutritionContent = nutritionContent;
        if (Objects.equals(nutritionContent, empty)) {
            String[] numV = nutritionContent.split(numberRegular);
            this.number = numV[numV.length -1];
            String[] unitV = nutritionContent.split(unitRegular);
            this.unit = unitV[unitV.length -1];
        }
    }

    @Override
    public String toString() {
        return "BaseFoodNutrition{" +
                "id=" + id +
                ", foodId=" + foodId +
                ", nutritionName='" + nutritionName + '\'' +
                ", nutritionContent='" + nutritionContent + '\'' +
                ", peerRanking='" + peerRanking + '\'' +
                ", peerMean='" + peerMean + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}