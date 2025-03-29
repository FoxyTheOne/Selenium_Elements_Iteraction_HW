package ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Класс для тестирования сайта bonigarcia.dev/selenium-webdriver-java/
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class HomePageTests {
    WebDriver driver;
    private static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/";

    @BeforeAll
    void setUpAll() {
        if (System.getProperty("local").equals("true")) {
            System.setProperty("webdriver.chrome.driver", "src\\test\\resources\\chromedriver.exe");
        }
        // У нас здесь достаточно простые тесты, нет необходимости очищать и открывать новый браузер перед каждым из них
        driver = new ChromeDriver();
        driver.manage().window().maximize();
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
    void openHomePageTest() {
        String actualTitle = driver.getTitle();

        assertEquals("Hands-On Selenium WebDriver with Java", actualTitle);
    }

    /**
     * Пример теста на проверку chapters - как можно было прокликать все ссылки по порядку
     * Если не работает driver.navigate().back(); то нам нужно новую ссылку открывать не в том же окне, а в новом. Чтобы проверить новую открытую вкладку и закрыть её, а первое главное окно висело без изменений и мы могли к нему возвращаться, а у элементов не менялись id
     */
    @Test
    void openAllLinks() throws InterruptedException {
        int qtyLinks = 0;
        List<WebElement> chapters = driver.findElements(By.cssSelector("h5.card-title"));

        if (!chapters.isEmpty()) {
            for (WebElement chapter : chapters) {
                System.out.println(chapter.getText());
                List<WebElement> links = chapter.findElements(By.xpath("./../a")); // Поднимаемся наверх и ищем a элемент. Впереди точка, чтобы искал от относительного элемента
                qtyLinks += links.size();

                for (WebElement link : links){
                    System.out.println(link.getText());
                    link.click();
                    Thread.sleep(1000);
                    driver.navigate().back();
                }

            }
            assertEquals(6,chapters.size());
            assertEquals(27,qtyLinks);
        }
    }
}
