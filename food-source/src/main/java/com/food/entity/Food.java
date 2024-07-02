package com.food.entity;

public class Food {
    private Long id;

    private String chineseName;

    private String primaryClassification;

    private String secondaryClassification;

    public Food() {
    }

    public Food(Long id, String chineseName, String primaryClassification, String secondaryClassification) {
        this.id = id;
        this.chineseName = chineseName;
        this.primaryClassification = primaryClassification;
        this.secondaryClassification = secondaryClassification;
    }

    public void setChineseName(String chineseName){
        this.chineseName = chineseName;
    }

    public String getChineseName(){
        return chineseName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setPrimaryClassification(String primaryClassification){
        this.primaryClassification = primaryClassification;
    }

    public String getPrimaryClassification(){
        return primaryClassification;
    }

    public void setSecondaryClassification(String secondaryClassification){
        this.secondaryClassification = secondaryClassification;
    }

    public String getSecondaryClassification(){
        return secondaryClassification;
    }
}
