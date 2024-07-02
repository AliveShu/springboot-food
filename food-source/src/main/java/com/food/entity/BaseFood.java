package com.food.entity;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Getter
@Setter
public class BaseFood implements Serializable {

    /**
    * 主键
    */
    private Long id;

    /**
    * 食物名称
    */
    private String foodName;

    /**
    * 食物类
    */
    private String foodstuffs;

    /**
    * 亚类
    */
    private String subClass;

    /**
    * 创建时间
    */
    private Date createTime;

    /**
    * 修改时间
    */
    private Date updateTime;

    private boolean exist;

    private static final long serialVersionUID = 1L;


    @Override
    public String toString() {
        return "BaseFood{" +
                "id=" + id +
                ", foodName='" + foodName + '\'' +
                ", foodstuffs='" + foodstuffs + '\'' +
                ", subClass='" + subClass + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}