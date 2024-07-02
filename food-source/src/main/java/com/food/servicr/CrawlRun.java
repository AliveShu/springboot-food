package com.food.servicr;

import jakarta.annotation.Resource;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

@Service
public class CrawlRun implements CommandLineRunner {

    @Resource
    FoodCrawling foodCrawling;

    @Resource
    FoodNutritionCrawling foodNutritionCrawling;

    @Override
    public void run(String... args) throws Exception {
        foodNutritionCrawling.crawlingRunning();

        // foodCrawling.running();
    }
}
