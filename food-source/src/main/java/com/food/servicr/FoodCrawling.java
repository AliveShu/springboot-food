package com.food.servicr;

import cn.hutool.core.util.IdUtil;
import com.food.entity.Food;
import com.food.entity.FoodEnergyComposition;
import com.food.mapper.FoodEnergyCompositionMapper;
import com.food.mapper.FoodMapper;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static com.food.entity.FoodUnit.g;

@Slf4j
@Service
public class FoodCrawling {

    final static String index = "http://yycx.yybq.net";

    @Value("${chrome.driver:}")
    private String chromedriver;

    @Resource
    FoodMapper foodMapper;

    @Resource
    FoodEnergyCompositionMapper foodEnergyCompositionMapper;

    @Scheduled(cron = "${food.crawling.cron:0 0 3 ? * L}")
    public void running() {

        // 打开浏览器的按照地址   设置驱动类型和名称
        RemoteWebDriver driver = createChromedriver();

        List<WebElement> indexElements = driver.findElements(By.xpath("//div[@class='index_classes']//a"));
        for (WebElement element : indexElements) {
            String levelN = element.getText();
            System.out.println("一级分类标签为: " + levelN);

            String href = element.getAttribute("href");
            System.out.println(href);

            createNewTabAndJump(driver, href);

            List<WebElement> webElements = driver.findElements(By.xpath("//div[@class='type_lists']//a"));
            if (webElements.isEmpty()) {
                level3(driver, levelN, null);
            } else {
                level2(driver, webElements, levelN);
            }

            closeCurrentTab(driver);
            System.out.println("------------------------------一级标签分割线-----------------------------------");
        }

        driver.quit();
    }


    RemoteWebDriver createChromedriver() {
        System.setProperty("webdriver.chrome.driver", chromedriver);
        RemoteWebDriver driver = new ChromeDriver();
        driver.get(index);
        WebDriver.Options manage = driver.manage();
        WebDriver.Window window = manage.window();
        window.maximize();

        return driver;
    }


    void level2(RemoteWebDriver driver, List<WebElement> webElements, String levelN) {
        for (WebElement element2 : webElements) {
            String text2 = element2.getText();
            System.out.println("二级分类标签为: " + text2);

            String href2 = element2.getAttribute("href");
            System.out.println(href2);

            createNewTabAndJump(driver, href2);
            level3(driver, levelN, text2);
            closeCurrentTab(driver);
        }
    }

    void level3(RemoteWebDriver driver, String levelN, String level2N) {
        for (WebElement element3 : driver.findElements(By.xpath("//div[@class='ysq_div_list']//a"))) {
            WebElement listTitle = element3.findElement(By.className("list_title"));
            String level3N = listTitle.getText();
            System.out.println("食品名称为: " + level3N);

            String href3 = element3.getAttribute("href");
            System.out.println(href3);

            createNewTabAndJump(driver, href3);
            level4(driver, levelN, level2N, level3N);
            closeCurrentTab(driver);
        }
    }

    void level4(WebDriver driver, String levelN, String level2N, String level3N) {
        Food food = new Food();
        food.setId(IdUtil.getSnowflakeNextId());
        food.setChineseName(level3N);
        food.setPrimaryClassification(levelN);
        food.setSecondaryClassification(level2N);
        FoodEnergyComposition foodEnergyComposition = new FoodEnergyComposition();
        foodEnergyComposition.setId(IdUtil.getSnowflakeNextId());
        foodEnergyComposition.setFoodId(food.getId());

        // 获取能量与成分
        energyAndIngredients(driver, foodEnergyComposition);

        // 获取矿物质
        minerals(driver, foodEnergyComposition);

        // 获取脂肪酸
        fattyAcid(driver, foodEnergyComposition);

        Food exist = foodMapper.findByFoodUnique(food);
        if (Objects.isNull(exist)) {
            int result = foodMapper.insert(food);
            if (result > 0)
                foodEnergyCompositionMapper.insert(foodEnergyComposition);
        } else {
            foodEnergyComposition.setFoodId(exist.getId());
            foodEnergyCompositionMapper.updateByFoodId(foodEnergyComposition);
        }
    }

    void createNewTabAndJump(RemoteWebDriver driver, String href) {
        driver.executeScript("window.open('" + href + "')");
        String[] windowHandles = driver.getWindowHandles().toArray(new String[]{});
        driver.switchTo().window(windowHandles[windowHandles.length - 1]);
        sleep(100);
    }

    void closeCurrentTab(RemoteWebDriver driver) {
        // 关闭当前标签页，聚焦左侧第一个窗口
        driver.close();
        String[] windowHandles =  driver.getWindowHandles().toArray(new String[]{});
        driver.switchTo().window(windowHandles[windowHandles.length - 1]);
        // sleep(100);
    }

    void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("线程睡眠异常:" + e.getMessage());
        }
    }
    
    void energyAndIngredients(WebDriver driver, FoodEnergyComposition foodEnergyComposition) {
        List<WebElement> energyCompositionElements = driver.findElements(By.xpath("//div[@class='details_table'][1]//tr"));


        List<WebElement> edibleElements = energyCompositionElements.get(1).findElements(By.xpath("./*"));// 获取食部
        fieldSetValue(foodEnergyComposition::setEdible, edibleElements, "%");

        List<WebElement> moistureElements = energyCompositionElements.get(2).findElements(By.xpath("./*"));// 获取水分
        fieldSetValue(foodEnergyComposition::setMoistureContent, moistureElements, "g");

        List<WebElement> energyElements = energyCompositionElements.get(3).findElements(By.xpath("./*"));// 获取能量
        fieldSetValue(foodEnergyComposition::setEnergy, energyElements, "KJ");

        List<WebElement> proteinElements = energyCompositionElements.get(4).findElements(By.xpath("./*"));// 获取蛋白质
        fieldSetValue(foodEnergyComposition::setProtein, proteinElements, "g");

        List<WebElement> fatElements = energyCompositionElements.get(5).findElements(By.xpath("./*"));// 获取脂肪
        fieldSetValue(foodEnergyComposition::setFat, fatElements, "g");

        List<WebElement> cholesterolElements = energyCompositionElements.get(6).findElements(By.xpath("./*"));// 获取胆固醇
        fieldSetValue(foodEnergyComposition::setCholesterol, cholesterolElements, "g");

        List<WebElement> ashContentElements = energyCompositionElements.get(7).findElements(By.xpath("./*"));// 获取灰分
        fieldSetValue(foodEnergyComposition::setAshContent, ashContentElements, "g");

        List<WebElement> carbohydrateElements = energyCompositionElements.get(8).findElements(By.xpath("./*"));// 获取碳水化合物
        fieldSetValue(foodEnergyComposition::setCarbohydrate, carbohydrateElements, "g");

        List<WebElement> dietaryFiberElements = energyCompositionElements.get(9).findElements(By.xpath("./*"));// 获取总膳食纤维
        fieldSetValue(foodEnergyComposition::setDietaryFiber, dietaryFiberElements, "g");

        // 获取维生素
        energyCompositionElements = driver.findElements(By.xpath("//div[@class='details_table'][2]//tr"));

        List<WebElement> caroteneElements = energyCompositionElements.get(1).findElements(By.xpath("./*"));// 获取胡萝卜素
        fieldSetValue(foodEnergyComposition::setCarotene, caroteneElements, "mg");

        List<WebElement> vitaminAElements = energyCompositionElements.get(2).findElements(By.xpath("./*"));// 获取维生素A
        fieldSetValue(foodEnergyComposition::setVitaminA, vitaminAElements, "mg");

        List<WebElement> aTeElements = energyCompositionElements.get(3).findElements(By.xpath("./*"));// 获取a-Te
        fieldSetValue(foodEnergyComposition::setATe, aTeElements, "mg");

        List<WebElement> thiamineElements = energyCompositionElements.get(4).findElements(By.xpath("./*"));// 获取硫胺素
        fieldSetValue(foodEnergyComposition::setThiamine, thiamineElements, "mg");

        List<WebElement> riboflavinElements = energyCompositionElements.get(5).findElements(By.xpath("./*"));// 获取核黄素
        fieldSetValue(foodEnergyComposition::setRiboflavin, riboflavinElements, "mg");

        List<WebElement> nicotinicAcidElements = energyCompositionElements.get(6).findElements(By.xpath("./*"));// 获取核黄素
        fieldSetValue(foodEnergyComposition::setNicotinicAcid, nicotinicAcidElements, "mg");

        List<WebElement> vitaminCElements = energyCompositionElements.get(7).findElements(By.xpath("./*"));// 获取维生素C
        fieldSetValue(foodEnergyComposition::setVitaminC, vitaminCElements, "mg");

    }

    void minerals(WebDriver driver, FoodEnergyComposition foodEnergyComposition) {
        List<WebElement> energyCompositionElements = driver.findElements(By.xpath("//div[@class='details_table'][3]//tr"));
        List<WebElement> calciumElements = energyCompositionElements.get(1).findElements(By.xpath("./*"));// 获取钙
        fieldSetValue(foodEnergyComposition::setCalcium, calciumElements, "mg");

        List<WebElement> phosphorusElements = energyCompositionElements.get(2).findElements(By.xpath("./*"));// 获取磷
        fieldSetValue(foodEnergyComposition::setPhosphorus, phosphorusElements, "μg");

        List<WebElement> potassiumElements = energyCompositionElements.get(3).findElements(By.xpath("./*"));// 获取钾
        fieldSetValue(foodEnergyComposition::setPotassium, potassiumElements, "mg");

        List<WebElement> sodiumElements = energyCompositionElements.get(4).findElements(By.xpath("./*"));// 获取钠
        fieldSetValue(foodEnergyComposition::setSodium, sodiumElements, "mg");

        List<WebElement> magnesiumElements = energyCompositionElements.get(5).findElements(By.xpath("./*"));// 获取镁
        fieldSetValue(foodEnergyComposition::setMagnesium, magnesiumElements, "mg");

        List<WebElement> ironElements = energyCompositionElements.get(6).findElements(By.xpath("./*"));// 获取铁
        fieldSetValue(foodEnergyComposition::setIron, ironElements, "mg");

        List<WebElement> zincElements = energyCompositionElements.get(7).findElements(By.xpath("./*"));// 获取锌
        fieldSetValue(foodEnergyComposition::setZinc, zincElements, "mg");

        List<WebElement> seleniumElements = energyCompositionElements.get(8).findElements(By.xpath("./*"));// 获取硒
        fieldSetValue(foodEnergyComposition::setSelenium, seleniumElements, "μg");

        List<WebElement> copperElements = energyCompositionElements.get(9).findElements(By.xpath("./*"));// 获取硒
        fieldSetValue(foodEnergyComposition::setCopper, copperElements, "mg");

        List<WebElement> manganeseElements = energyCompositionElements.get(10).findElements(By.xpath("./*"));// 获取锰
        fieldSetValue(foodEnergyComposition::setManganese, manganeseElements, "mg");

        List<WebElement> iodineElements = energyCompositionElements.get(11).findElements(By.xpath("./*"));// 获取碘
        fieldSetValue(foodEnergyComposition::setIodine, iodineElements, "mg");
    }

    void fattyAcid(WebDriver driver, FoodEnergyComposition foodEnergyComposition) {
        List<WebElement> energyCompositionElements = driver.findElements(By.xpath("//div[@class='details_table'][4]//tr"));

        List<WebElement> saturatedFattyAcidsElements = energyCompositionElements.get(1).findElements(By.xpath("./*"));// 获取饱和脂肪酸
        fieldSetValue(foodEnergyComposition::setSaturatedFattyAcids, saturatedFattyAcidsElements, g);

        List<WebElement> monounsaturatedFattyAcidsElements = energyCompositionElements.get(2).findElements(By.xpath("./*"));// 获取单不饱和脂肪酸
        fieldSetValue(foodEnergyComposition::setMonounsaturatedFattyAcids, monounsaturatedFattyAcidsElements, g);

        List<WebElement> polyunsaturatedFattyAcidsElements = energyCompositionElements.get(3).findElements(By.xpath("./*"));// 获取多不饱和脂肪酸
        fieldSetValue(foodEnergyComposition::setPolyunsaturatedFattyAcids, polyunsaturatedFattyAcidsElements, g);

        List<WebElement> totalFattyAcidsElements = energyCompositionElements.get(4).findElements(By.xpath("./*"));// 获取多不饱和脂肪酸
        fieldSetValue(foodEnergyComposition::setTotalFattyAcids, totalFattyAcidsElements, g);

    }

    void fieldSetValue(Consumer<BigDecimal> consumer, List<WebElement> elementList, String regex) {
        try {
            String text = elementList.get(1).getText();
            BigDecimal bigDecimal = new BigDecimal(text.split(regex)[0]);
            consumer.accept(bigDecimal);
        } catch (Exception e) {
            log.error("异常信息: {}", e.getMessage());
        }
    }

}

