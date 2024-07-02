package com.food.servicr;

import cn.hutool.core.util.IdUtil;
import com.food.entity.BaseFood;
import com.food.entity.BaseFoodNutrition;
import com.food.mapper.BaseFoodMapper;
import com.food.mapper.BaseFoodNutritionMapper;
import jakarta.annotation.Resource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class FoodNutritionCrawling {

    @Value("${chrome.driver}")
    private String chromedriver;

    String noNextPage = "javascript: void(0)";
    String textSplit = "：";

    final static String index = "https://nlc.chinanutri.cn/fq/";

    @Resource
    BaseFoodMapper baseFoodMapper;

    @Resource
    BaseFoodNutritionMapper baseFoodNutritionMapper;

    @Scheduled(cron = "${food.crawling.cron:0 0 3 ? * L}")
    public void crawlingRunning() {
        List<String> tables = baseFoodMapper.existCurrentTable();
        if (tables.isEmpty())
            return;

        RemoteWebDriver driver = createChromedriver();

        List<WebElement> elements = driver.findElements(By.xpath("//div[contains(@class, ' food_box food_bg')]"));
        for (WebElement element : elements) {
            currentTab(driver, element);
        }
        driver.close();
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

    void currentTab(RemoteWebDriver driver, WebElement element) {
        String nextSuffix = "_top food_border";
        String currentDiv = element.getAttribute("class");
        String nextDiv = currentDiv.split(" ")[0].concat(nextSuffix);
        WebElement level2 = element.findElement(By.xpath("./div[@class='" + nextDiv + "']//a"));
        String level2Text = level2.getText();
        System.out.println("level2中的文本为：" + level2Text);
        List<WebElement> level2Element = element.findElements(By.xpath("./ul[@class='food_list']//a"));
        for (WebElement level2_2 : level2Element) {
            String href = level2_2.getAttribute("href");
            System.out.println("当前标签可跳转链接: " + href);
            String level2_2Text = level2_2.getText();
            System.out.println("level2_2的文本为：" + level2_2Text);
            secondTab(driver, href);
        }
        System.out.println("----------------------------- 分割线 -----------------------------");

    }

    void secondTab(RemoteWebDriver driver, String href) {
        createNewTabAndJump(driver, href);
        recursionHandler(driver, null);
        closeCurrentTab(driver);
        System.out.println("----------------------------- 第二标签分割线 -----------------------------");
    }


    void recursionHandler(RemoteWebDriver driver, String nextPage) {
        List<WebElement> elements = driver.findElements(By.xpath("//tbody//td[@class='td_left']"));
        if (elements.isEmpty()) {
            return;
        }
        for (WebElement element : elements) {
            WebElement elementA = element.findElement(By.xpath("./a"));
            String nextTabUrl = elementA.getAttribute("href");
            String labelText = element.getText();
            System.out.println("下一个选项卡访问链接: " + nextTabUrl);
            System.out.println("当前标签内文本为: " + labelText);
            thirdTab(driver, nextTabUrl);
        }

        WebElement element = driver.findElement(By.xpath("//div[@id='pageList']//div[@class='pagination']//a[@class='next']"));
        nextPage = element.getAttribute("href");
        System.out.println("列表页面下一页访问地址: " + nextPage);
        if (Objects.equals(nextPage, noNextPage)) {
            return;
        }
        driver.executeScript(nextPage);
        sleep(100);
        recursionHandler(driver, nextPage);
    }

    void thirdTab(RemoteWebDriver driver, String href) {
        createNewTabAndJump(driver, href);

        BaseFood baseFood = saveFood(driver);
        List<BaseFoodNutrition> nutritionList = new ArrayList<>();
        List<WebElement> elements = driver.findElements(By.xpath("//tbody//tr"));
        for (int i = 1; i < elements.size(); i++) {
            WebElement webElement = elements.get(i);
            List<WebElement> tdElement = webElement.findElements(By.xpath("./td"));
            BaseFoodNutrition nutrition = tdElement.size() == 5 ? td5Handler(tdElement) : td6Handler(tdElement);
            nutrition.setId(IdUtil.getSnowflakeNextId());
            nutrition.setFoodId(baseFood.getId());
            nutrition.setCreateTime(new Date());
            nutrition.setUpdateTime(new Date());
            nutritionList.add(nutrition);
            System.out.println("食材营养详情: " + nutrition);
        }
        if (baseFood.isExist()) {
            nutritionList.forEach(baseFoodNutritionMapper::updateByPrimaryKey);
        } else {
            baseFoodNutritionMapper.batchInsert(nutritionList);
        }

        // 关闭当前标签页，聚焦左侧第一个窗口
        closeCurrentTab(driver);
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
        String[] windowHandles = driver.getWindowHandles().toArray(new String[]{});
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

    BaseFood saveFood(RemoteWebDriver driver) {
        WebElement name = driver.findElement(By.xpath("//div[@class='food_introduce_top']//h1"));
        List<WebElement> elementList = driver.findElements(By.xpath("//div[@class='food_introduce_top']//span[@class='bieming']"));
        WebElement foodstuffs = elementList.get(0);
        WebElement subClass = elementList.get(1);
        int size = elementList.size();
        if (size > 2) {
            foodstuffs = elementList.get(size - 2);
            subClass = elementList.get(size - 1);
        }

        BaseFood baseFood = new BaseFood();
        baseFood.setId(IdUtil.getSnowflakeNextId());
        baseFood.setFoodName(name.getText());
        BaseFood exist = baseFoodMapper.existCurrentObj(baseFood);
        if (Objects.isNull(exist)) {
            baseFood.setFoodstuffs(foodstuffs.getText().split(textSplit)[1]);
            baseFood.setSubClass(subClass.getText().split(textSplit)[1]);
            baseFood.setCreateTime(new Date());
            baseFood.setUpdateTime(new Date());
            baseFoodMapper.insert(baseFood);
            return baseFood;
        }
        baseFood.setFoodstuffs(foodstuffs.getText().split(textSplit)[1]);
        baseFood.setSubClass(subClass.getText().split(textSplit)[1]);
        exist.setUpdateTime(new Date());
        baseFoodMapper.updateById(exist);
        exist.setExist(true);
        System.out.println(baseFood);
        return exist;
    }

    BaseFoodNutrition td5Handler(List<WebElement> tdElement) {
        BaseFoodNutrition nutrition = new BaseFoodNutrition();
        nutrition.setNutritionName(tdElement.get(0).getText());
        nutrition.setNutritionContent(tdElement.get(1).getText());
        nutrition.setPeerRanking(tdElement.get(2).getText());
        nutrition.setPeerMean(tdElement.get(3).getText());
        return nutrition;
    }

    BaseFoodNutrition td6Handler(List<WebElement> tdElement) {
        BaseFoodNutrition nutrition = new BaseFoodNutrition();
        nutrition.setNutritionName(tdElement.get(1).getText());
        nutrition.setNutritionContent(tdElement.get(2).getText());
        nutrition.setPeerRanking(tdElement.get(3).getText());
        nutrition.setPeerMean(tdElement.get(4).getText());
        return nutrition;
    }
}
