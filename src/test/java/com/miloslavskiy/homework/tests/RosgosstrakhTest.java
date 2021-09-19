package com.miloslavskiy.homework.tests;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class RosgosstrakhTest {
    private WebDriver driver;
    private WebDriverWait wait;

    @Before
    public void before() {
        System.setProperty("webdriver.chrome.driver", "src/test/resources/webdriver/chromedriver.exe");

        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(30, TimeUnit.SECONDS);
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        wait = new WebDriverWait(driver, 15, 1000);

        driver.get("https://www.rgs.ru/");
    }


    @Test
    public void exampleTest() {
        // spam killer
        spamDestroy(By.xpath("//iframe[@class='flocktory-widget']"),
                By.xpath("//button[@class='CloseButton']"));

        // Убрать уведомление о куках
        WebElement cookiesBtnClose = driver.findElement(By.xpath("//div[@class='btn btn-default text-uppercase']"));
        cookiesBtnClose.click();

        // Выбрать "Меню"
        WebElement menuBtn = driver.findElement(By.xpath("//a[contains(text(), 'Меню') and @data-toggle='dropdown']"));
        menuBtn.click();
        WebElement parentBaseMenu = menuBtn.findElement(By.xpath("./.."));
        Assert.assertTrue("Клик не был совершен", parentBaseMenu.getAttribute("class").contains("open"));

        // Выбрать вкладку "Компаниям"
        WebElement companiesBtn = driver.findElement(
                By.xpath("//form[@id='rgs-main-menu-insurance-dropdown']//a[contains(text(), 'Компаниям')]"));
        companiesBtn.click();
        waitUtilElementToBeVisible(By.xpath("//h1"), "Корпоративное страхование");

        // Выбрать вкладку "Страхование здоровья"
        WebElement insuranceHealthBtn = driver.findElement(By.xpath("//a[text() = 'Страхование здоровья']"));
        wait.until(ExpectedConditions.elementToBeClickable(insuranceHealthBtn));
        insuranceHealthBtn.click();

        // Выбрать вкладку "Добровольное медицинское страхование"
        switchToTabByText("ДМС для сотрудников - добровольное медицинское страхование от Росгосстраха");
        spamDestroy(By.xpath("//iframe[@class='flocktory-widget']"),
                By.xpath("//div[@class='PushTip-close']"));
        WebElement voluntaryHealthInsuranceBtn = driver.findElement(
                By.xpath("//a[contains(text(), 'Добровольное медицинское страхование')]"));
        voluntaryHealthInsuranceBtn.click();
        waitUtilElementToBeVisible(By.xpath ("//h1"), "Добровольное медицинское страхование");

        // Нажать на кнопку "Отправить заявку"
        spamDestroy(By.xpath("//iframe[@class='flocktory-widget']"),
                By.xpath("//div[@data-fl-track='click-close-1']"));
        WebElement sendRequestBtn = driver.findElement(By.xpath("//a[contains(text(), 'Отправить заявку')]"));
        sendRequestBtn.click();
        waitUtilElementToBeVisible(By.xpath("//b[contains(text(), 'Заявка')]"),
                "Заявка на добровольное медицинское страхование");

        // Заполнить все поля формы
        String fieldXpath = "//*[contains(@data-bind, '%s')]";
        fillInputField(driver.findElement(By.xpath(String.format(fieldXpath, "LastName"))), "Авто");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXpath, "FirstName"))), "Тест");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXpath, "MiddleName"))), "Автотестович");
        WebElement popupSelectBtn = driver.findElement(By.xpath("//select[@class='popupSelect form-control']"));
        popupSelectBtn.click();
        popupSelectBtn.sendKeys(Keys.DOWN);
        popupSelectBtn.sendKeys(Keys.ENTER);
        WebElement phoneNumberBtn = driver.findElement(By.xpath("//input[contains(@data-bind, 'Phone')]"));
        phoneNumberBtn.click();
        phoneNumberBtn.sendKeys("1234567890");
        fillInputField(driver.findElement(By.xpath(String.format(fieldXpath, "Email"))), "qwertty");
        WebElement contactDateBtn = driver.findElement(By.xpath("//input[contains(@data-bind, 'ContactDate')]"));
        contactDateBtn.click();
        contactDateBtn.sendKeys("12139999");
        contactDateBtn.sendKeys(Keys.ENTER);
        fillInputField(driver.findElement(By.xpath(String.format(fieldXpath, "Comment"))), "Лучший в мире сайт");
        WebElement checkboxBtn = driver.findElement(By.xpath("//input[@class='checkbox']"));
        checkboxBtn.click();
        WebElement sendBtn = driver.findElement(By.xpath("//button[@id='button-m']"));
        sendBtn.click();
        waitUtilElementToBeVisible(By.xpath("//span[contains(text(), 'Введите адрес электронной почты')]"),
                "Введите адрес электронной почты");
    }


    @After
    public void after() {
        driver.quit();
    }


    private void fillInputField(WebElement element, String value) {
        wait.until(ExpectedConditions.elementToBeClickable(element));
        element.click();
        element.clear();
        element.sendKeys(value);
        boolean checkFlag = wait.until(ExpectedConditions.attributeContains(element, "value", value));
        Assert.assertTrue("Поле было заполнено некорректно", checkFlag);
    }


    private void waitUtilElementToBeVisible(By locator, String expected) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        WebElement pageMainTitle = driver.findElement(locator);
        Assert.assertEquals("Заголовок отсутствует/не соответствует требуемому", expected, pageMainTitle.getText());
    }


    private void spamDestroy(By byFrameXpath, By byElementXpath) {
        String myTab = driver.getWindowHandle();
        try {
            driver.switchTo().frame(driver.findElement(byFrameXpath));
            driver.findElement(byElementXpath);
            driver.findElement(byElementXpath).click();
        } catch (NoSuchElementException ignore) {

        } finally {
            driver.switchTo().window(myTab);
        }

    }

    // Переключение между страницами
    private void switchToTabByText(String text){
        //имя текущей вкладки или дескриптор
        String myTab = driver.getWindowHandle();
        // дескриптор всех вкладок
        ArrayList<String> newTab = new ArrayList<>(driver.getWindowHandles());
        for (String s : newTab) {
            // если это не наша вкладка
            if(!s.equals(myTab)) {
                // переключаемся на нашу вкладку
                driver.switchTo().window(s);
                //если такой вкладки нет выходим из цикла
                if (driver.getTitle().contains(text)) {
                    return;
                }
            }
        }
        // Фейлим тест
        Assert.fail("Вкладка " + text + " не найдена");
    }
}
