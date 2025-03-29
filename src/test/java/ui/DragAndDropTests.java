package ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс для тестирования страницы bonigarcia.dev/selenium-webdriver-java/drag-and-drop.html
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DragAndDropTests {
    private static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/drag-and-drop.html";
    WebDriver driver;
    Actions actions;

    @BeforeAll
    void setUpAll() {
        if (System.getProperty("local").equals("true")) {
            System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");
        }
        // У нас здесь достаточно простые тесты, нет необходимости очищать и открывать новый браузер перед каждым из них
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        actions = new Actions(driver);
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
                () -> assertEquals("Drag and drop", actualPageTitle.getText())
        );
    }

    @Test
    void dragAndDropTest () throws InterruptedException {
        WebElement draggable = driver.findElement(By.xpath("//div[@id='draggable']"));
        WebElement target = driver.findElement(By.xpath("//div[@id='target']"));

        actions.dragAndDrop(draggable,target).perform();
        Thread.sleep(2000);

        assertEquals(draggable.getLocation(), target.getLocation(), "Element didn't move to target");
    }
}
