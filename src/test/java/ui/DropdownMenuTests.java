package ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс для тестирования страницы bonigarcia.dev/selenium-webdriver-java/dropdown-menu.html
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DropdownMenuTests {
    private static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/dropdown-menu.html";
    WebDriver driver;
    Actions actions;
    JavascriptExecutor js;
    List<String> expectedItems = List.of("Action", "Another action", "Something else here", "Separated link");

    @BeforeAll
    void setUpAll() {
        if (System.getProperty("local").equals("true")) {
            System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");
        }
        // У нас здесь достаточно простые тесты, нет необходимости очищать и открывать новый браузер перед каждым из них
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        actions = new Actions(driver);
        js = (JavascriptExecutor) driver;
    }

    @AfterAll
    void tearDown() {
        driver.getPageSource();
        driver.quit();
    }

    @BeforeEach
    void setup() {
        driver.get(BASE_URL);
    }

    /**
     * Тест для проверки заголовка - тот ли сайт мы открыли
     */
    @Test
    void openWebFormPageTest() {
        String actualMainTitle = driver.getTitle();
        WebElement actualPageTitle = driver.findElement(By.className("display-6"));

        assertAll(
                () -> assertEquals("Hands-On Selenium WebDriver with Java", actualMainTitle),
                () -> assertEquals("Dropdown menu", actualPageTitle.getText())
        );
    }

    @Test
    void leftClickDropdownTest() throws InterruptedException {
        WebElement leftClickDropdownMenu = driver.findElement(By.id("my-dropdown-1"));
        actions.click(leftClickDropdownMenu).perform();
        Thread.sleep(1000);
        List<String> actualItems = getDropdownItems(leftClickDropdownMenu);
        assertEquals(expectedItems, actualItems, "Элементы выпадающего меню не совпадают");
    }

    @Test
    void rightClickDropdownTest() throws InterruptedException {
        WebElement rightClickDropdownMenu = driver.findElement(By.id("my-dropdown-2"));
        actions.contextClick(rightClickDropdownMenu).perform();
        Thread.sleep(1000);
        List<String> actualItems = getDropdownItems(rightClickDropdownMenu);
        assertEquals(expectedItems, actualItems, "Элементы выпадающего меню не совпадают");
    }

    @Test
    void doubleClickDropdownTest() throws InterruptedException {
        WebElement doubleClickDropdownMenu = driver.findElement(By.id("my-dropdown-3"));
        actions.doubleClick(doubleClickDropdownMenu).perform();
        Thread.sleep(1000);
        List<String> actualItems = getDropdownItems(doubleClickDropdownMenu);
        assertEquals(expectedItems, actualItems, "Элементы выпадающего меню не совпадают");
    }

    private List<String> getDropdownItems(WebElement element) {
        List<WebElement> dropdownItems = element.findElements(By.xpath("./following-sibling::ul[contains(@class, 'dropdown-menu')]//a[@class='dropdown-item']"));
        return dropdownItems.stream().map(WebElement::getText).toList();
    }
}
