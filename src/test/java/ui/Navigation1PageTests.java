package ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Класс для тестирования страницы bonigarcia.dev/selenium-webdriver-java/navigation1.html
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class Navigation1PageTests {
    private static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/navigation1.html";
    private static final String PAGE_TITLE = "Navigation example";
    private static final String FIRST_PAGE_TEXT = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.";
    private static final String SECOND_PAGE_TEXT = "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur.";
    private static final String THIRD_PAGE_TEXT = "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.";
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

        checkPreviousButtonIsDisabled();

        assertAll(
                () -> assertEquals("Hands-On Selenium WebDriver with Java", actualMainTitle),
                () -> assertEquals(PAGE_TITLE, actualPageTitle.getText())
        );
    }

    /**
     * Проверка текста и навигации
     */
    @Test
    void defaultPageTextTest() {
        WebElement actualPage1Text = driver.findElement(By.className("lead"));
        assertEquals(FIRST_PAGE_TEXT, actualPage1Text.getText());
    }

    @Test
    void numberNavigationTest() {
        WebElement navigatePage3Button = driver.findElement(By.cssSelector("[href$='3.html']"));
        actions.moveToElement(navigatePage3Button).click().perform();
        WebElement actualPage3Text = driver.findElement(By.className("lead"));

        assertEquals(THIRD_PAGE_TEXT, actualPage3Text.getText());
        // TODO Тест не гибкий, если страниц много, в дальнейшем кнопки "3" не будет видно
    }

    @Test
    void previousButtonTest() throws InterruptedException {
        WebElement previousButton = driver.findElement(By.xpath("//a[@class = 'page-link' and text() = 'Previous']"));
        WebElement currentPageText = driver.findElement(By.xpath("//p[@class = 'lead']"));

        assertAll(
                () -> assertTrue(previousButton.isDisplayed()),
                () -> assertTrue(previousButton.isEnabled()),
                () -> assertEquals(FIRST_PAGE_TEXT, currentPageText.getText())
        );
        Thread.sleep(1000);
    }

    // Из разбора ДЗ2:
    @Test
    void nextButtonNavigation() throws InterruptedException {
        checkPreviousButtonIsDisabled();
        checkPageContent(PAGE_TITLE, FIRST_PAGE_TEXT);

        clickNextButton();
        Thread.sleep(1000);
        checkPageContent(PAGE_TITLE, SECOND_PAGE_TEXT);

        clickNextButton();
        Thread.sleep(1000);
        checkPageContent(PAGE_TITLE, THIRD_PAGE_TEXT);
    }

    @Test
    void previousButtonNavigation() throws InterruptedException {
        // Начинать надо с проверки того, что страницы вообще есть
        // Или же в начале класса заполнять базу данных тестовыми значениями, чтобы страницы появились
        checkPreviousButtonIsDisabled();

        clickNextButton();
        clickNextButton();

        clickPreviousButton();
        Thread.sleep(1000);
        checkPageContent(PAGE_TITLE, SECOND_PAGE_TEXT);

        clickPreviousButton();
        Thread.sleep(1000);
        checkPageContent(PAGE_TITLE, FIRST_PAGE_TEXT);

        checkPreviousButtonIsDisabled();
        // Тест не динамически. Если страниц много, то цифры будут меняться
        // Считать, сколько у нас страниц и от этого уже двигаться
    }

    @Test
    void pageNumberButtonNavigationTest() throws InterruptedException {
        checkPreviousButtonIsDisabled();

        clickFirstPageButton();
        Thread.sleep(1000);
        checkPageContent(PAGE_TITLE, FIRST_PAGE_TEXT);

        clickSecondPageButton();
        Thread.sleep(1000);
        checkPageContent(PAGE_TITLE, SECOND_PAGE_TEXT);

        clickThirdPageButton();
        Thread.sleep(1000);
        checkPageContent(PAGE_TITLE, THIRD_PAGE_TEXT);
    }

    private void checkPreviousButtonIsDisabled() {
        WebElement previousButton = driver.findElement(By.xpath("//a[text()='Previous']"));
        WebElement parentLi = previousButton.findElement(By.xpath("./parent::li"));
        assertTrue(parentLi.getAttribute("class").contains("disabled"), "Previous button is not disabled");
    }

    private void clickNextButton() {
        driver.findElement(By.xpath("//a[text()='Next']")).click();
    }

    private void clickPreviousButton() {
        driver.findElement(By.xpath("//a[text()='Previous']")).click();
    }

    private void checkPageContent(String expectedPageTitle, String expectedPageText) {
        WebElement pageTitleElement = driver.findElement(By.cssSelector("h1.display-6"));
        WebElement pageTextElement = driver.findElement(By.cssSelector("p.lead"));
        assertAll(
                () -> assertEquals(expectedPageTitle,pageTitleElement.getText()),
                () -> assertEquals(expectedPageText, pageTextElement.getText())
        );
    }

    private void clickFirstPageButton() {
        driver.findElement(By.xpath("//a[text()='1']")).click();
    }

    private void clickSecondPageButton() {
        driver.findElement(By.xpath("//a[text()='2']")).click();
    }

    private void clickThirdPageButton() {
        driver.findElement(By.xpath("//a[text()='3']")).click();
    }
}
