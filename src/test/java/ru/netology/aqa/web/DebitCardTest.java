package ru.netology.aqa.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DebitCardTest {
    private WebDriver driver;

    @BeforeAll
    public static void setupAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
        driver = null;
    }

    @Test
    void shouldTestSuccessRequest() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Смирнов Алексей");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79234545454");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();
        String actualResult = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText().trim();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.", actualResult);
    }
    @Test
    void shouldSeekSkipNameField() {
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79447567758");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();
        String actualResult = driver.findElement(By.cssSelector("[data-test-id='name'] .input__sub ")).getText().trim();
        assertEquals("Поле обязательно для заполнения", actualResult);
    }
    @Test
    void shouldSeekWrongPhoneNumberField() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Артем Коновалов");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("TF243&^");
        driver.findElement(By.cssSelector("[data-test-id='agreement']")).click();
        driver.findElement(By.cssSelector("button.button")).click();
        String actualResult = driver.findElement(By.cssSelector("[data-test-id='phone'] .input__sub ")).getText().trim();
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.", actualResult);
    }
    @Test
    void shouldSeekEmptyCheckbox() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Артем Коновалов");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+79234567665");
        driver.findElement(By.cssSelector("button.button")).click();
        String actualResult = driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid")).getText().trim();
        assertEquals("Я соглашаюсь с условиями обработки и использования моих персональных данных и разрешаю сделать запрос в бюро кредитных историй", actualResult);
    }
    @Test
    void shouldSeekEmptyPhoneField() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Андрей Смирнов");
        driver.findElement(By.cssSelector("button.button")).click();
        String actualResult = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText().trim();
        assertEquals("Поле обязательно для заполнения", actualResult);
    }
    @Test
    void shouldSeekInvalidName() {
        driver.findElement(By.cssSelector("[data-test-id='name'] input")).sendKeys("Jim Beam");
        driver.findElement(By.cssSelector("[data-test-id='phone'] input")).sendKeys("+12024561111");
        driver.findElement(By.cssSelector("button.button")).click();
        String actualResult = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText().trim();
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.", actualResult);
    }
}