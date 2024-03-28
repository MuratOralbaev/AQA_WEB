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
    void testCardRequestSuccess() {
        WebElement form = driver.findElement(By.className("form"));
        form.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Попова Анна-Мария");
        form.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79051234567");
        form.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        form.findElement(By.cssSelector("button")).click();
        String resultMessage = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText();
        assertEquals("Ваша заявка успешно отправлена! Наш менеджер свяжется с вами в ближайшее время.",
                resultMessage.trim());
    }

    @Test
    void testNameRequired() {
        WebElement form = driver.findElement(By.className("form"));
        form.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79051234567");
        form.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        form.findElement(By.cssSelector("button")).click();
        WebElement errorHintElement = form.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        assertEquals("Поле обязательно для заполнения", errorHintElement.getText().trim());
    }

    @Test
    void testLatinNameNotValid() {
        WebElement form = driver.findElement(By.className("form"));
        form.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Smith Mike");
        form.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79051234567");
        form.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        form.findElement(By.cssSelector("button")).click();
        WebElement errorHintElement = form.findElement(By.cssSelector("[data-test-id='name'].input_invalid .input__sub"));
        assertEquals("Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.",
                errorHintElement.getText().trim());
    }

    @Test
    void testPhoneRequired() {
        WebElement form = driver.findElement(By.className("form"));
        form.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Попова Анна-Мария");
        form.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        form.findElement(By.cssSelector("button")).click();
        WebElement errorHintElement = form.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        assertEquals("Поле обязательно для заполнения", errorHintElement.getText().trim());
    }

    @Test
    void testPhoneMustStartWithPlusSign() {
        WebElement form = driver.findElement(By.className("form"));
        form.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Попова Анна-Мария");
        form.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("79051234567");
        form.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        form.findElement(By.cssSelector("button")).click();
        WebElement errorHintElement = form.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
                errorHintElement.getText().trim());
    }

    @Test
    void testPhoneOf10DigitsInvalid() {
        // Verify that phone value of 10 digits is not valid
        WebElement form = driver.findElement(By.className("form"));
        form.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Попова Анна-Мария");
        form.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+7905123456");
        form.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        form.findElement(By.cssSelector("button")).click();
        assertPhoneErrorHint(form);
    }

    @Test
    void testPhoneOf12DigitsInvalid() {
        // Verify that phone value of 12 digits is not valid
        WebElement form = driver.findElement(By.className("form"));
        form.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Попова Анна-Мария");
        form.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+790512345670");
        form.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        form.findElement(By.cssSelector("button")).click();
        assertPhoneErrorHint(form);
    }

    @Test
    void testPhoneWithAlphabeticCharacterInvalid() {
        // Verify that phone value containing non-digit character is not valid
        WebElement form = driver.findElement(By.className("form"));
        form.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Попова Анна-Мария");
        form.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79O51234567");
        form.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        form.findElement(By.cssSelector("button")).click();
        assertPhoneErrorHint(form);
    }

    private static void assertPhoneErrorHint(WebElement form) {
        WebElement errorHintElement = form.findElement(By.cssSelector("[data-test-id='phone'].input_invalid .input__sub"));
        assertEquals("Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.",
                errorHintElement.getText().trim());
    }

    @Test
    void testAgreementRequired() {
        WebElement form = driver.findElement(By.className("form"));
        form.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Попова Анна-Мария");
        form.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79051234567");
        form.findElement(By.cssSelector("button")).click();
        assertTrue(form.findElement(By.cssSelector("[data-test-id='agreement'].input_invalid")).isDisplayed());
    }
}