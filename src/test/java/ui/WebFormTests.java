package ui;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ui.Constants.*;

/**
 * Класс для тестирования страницы bonigarcia.dev/selenium-webdriver-java/web-form.html
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class WebFormTests {
    private static final String BASE_URL = "https://bonigarcia.dev/selenium-webdriver-java/web-form.html";
    WebDriver driver;
    Actions actions;
    JavascriptExecutor js;

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
                () -> assertEquals("Web form", actualPageTitle.getText())
        );
    }

    /**
     * Ввод текста в поля и проверка значений
     */
    @Test
    void textInputTest() {
        WebElement textInputForm = driver.findElement(By.id("my-text-id"));

        textInputForm.sendKeys(TEST_TEXT);
        String actualText = textInputForm.getAttribute(GET_ATTRIBUTE_VALUE);

        assertEquals(TEST_TEXT, actualText, "Test must be the same");
    }

    @Test
    void textInputClearTest() {
        WebElement textInputForm = driver.findElement(By.id("my-text-id"));

        textInputForm.sendKeys(TEST_TEXT);
        textInputForm.clear();
        String actualText = textInputForm.getAttribute(GET_ATTRIBUTE_VALUE);

        assertEquals(EMPTY_TEXT, actualText, "The field must be cleared");
    }

    @Test
    void passwordInputTest() {
        WebElement passwordForm = driver.findElement(By.name("my-password"));

        passwordForm.sendKeys(TEST_TEXT);
        String actualText = passwordForm.getDomProperty(GET_ATTRIBUTE_VALUE);

        assertEquals(TEST_TEXT, actualText, "Test must be the same");
    }

    @Test
    void textAreaTest() throws IOException {
        WebElement textareaForm = driver.findElement(By.name("my-textarea"));
        String longText = Files.readString(Path.of(TEST_FILE_PATH));
        String actualText = null;

        textareaForm.sendKeys(longText);
        String textFromTextArea = textareaForm.getDomProperty(GET_ATTRIBUTE_VALUE);
        if (textFromTextArea != null) {
            actualText = textFromTextArea.replaceAll("\r?\n", "\n");
        }

        assertEquals(longText.replaceAll("\r?\n", "\n"), actualText, "Test must be the same");
    }

    /**
     * Проверка disabled поля
     */
    @Test
    void disabledInputTest() {
        WebElement disabledInputForm = driver.findElement(By.name("my-disabled"));

        assertAll(
                () -> assertFalse(disabledInputForm.isEnabled(), "The field must be disabled"),
                () -> assertEquals("Disabled input", disabledInputForm.getDomAttribute("placeholder"))
        );
    }

    /**
     * Попытка ввести тест в disabled поле
     */
    @Test
    void disabledInputTextingTest() {
        WebElement disabledInputForm = driver.findElement(By.name("my-disabled"));
        try {
            disabledInputForm.sendKeys(TEST_TEXT);
        } catch (ElementNotInteractableException e) {
            assertEquals(ElementNotInteractableException.class, e.getClass());
        }
    }

    /**
     * Проверка readonly поля
     */
    @Test
    void readonlyInputTest() {
        WebElement readonlyInputForm = driver.findElement(By.name("my-readonly"));

        assertAll(
                () -> assertTrue(readonlyInputForm.isEnabled(), "The field must not be disabled - it's readonly"),
                () -> assertNotNull(readonlyInputForm.getDomAttribute("readonly")),
                () -> assertEquals("true", readonlyInputForm.getDomAttribute("readonly"))
        );
    }

    /**
     * Попытка ввести тест в readonly поле
     */
    @Test
    void readonlyInputTextingTest() {
        WebElement readonlyInputForm = driver.findElement(By.name("my-readonly"));
        try {
            readonlyInputForm.sendKeys(TEST_TEXT);
        } catch (ElementNotInteractableException e) {
            assertEquals(ElementNotInteractableException.class, e.getClass());
        }
    }

    /**
     * Работа со списками
     */
    @Test
    void dropdownSelectTest() {
        WebElement dropdownSelectForm = driver.findElement(By.name("my-select"));
        Select select = new Select(dropdownSelectForm);

        // Default text check
        select.selectByIndex(0);
        String defaultText = select.getFirstSelectedOption().getText();

        select.selectByValue("2");
        String customText = select.getFirstSelectedOption().getText();

        select.selectByVisibleText("Three");
        String customVisibleText = select.getFirstSelectedOption().getText();

        List<WebElement> options = select.getOptions();
        for (WebElement option : options) {
            System.out.printf("Available Option: %s isSelected = %s%n", option.getText(), option.isSelected());
        }

        int optionsCount = select.getOptions().size();

        assertAll(
                () -> assertEquals("Open this select menu", defaultText),
                () -> assertEquals("Two", customText),
                () -> assertEquals("Three", customVisibleText),
                () -> assertEquals(4, optionsCount)
        );
    }

    @Test
    void dropdownDataList() throws InterruptedException {
        // Проверка возможности ввода
        WebElement dropdownDataListForm = driver.findElement(By.name("my-datalist"));
        dropdownDataListForm.sendKeys(TEST_TEXT);
        Thread.sleep(2000);
        String actualText = dropdownDataListForm.getDomProperty("value"); // getDomAttribute из HTML, getDomProperty - ввод пользователем в поле для ввода текста

        // Проверка первой строки option
        WebElement dropdownDataListOption = driver.findElement(By.xpath("//datalist[@id='my-options']/option[1]"));
        String expectedOptionText = dropdownDataListOption.getDomAttribute("value");
        dropdownDataListForm.clear();
        dropdownDataListForm.sendKeys(expectedOptionText);
        Thread.sleep(2000);
        String actualText2 = dropdownDataListForm.getDomProperty("value");

        // Пример из видео разбора ДЗ
        dropdownDataListForm.clear();
        dropdownDataListForm.sendKeys("New Y");
        Thread.sleep(2000);
        dropdownDataListForm.click();
        Thread.sleep(2000);
        js.executeScript("arguments[0].value = 'New York'; arguments[0].dispatchEvent(new Event('change'));", dropdownDataListForm);
        String actualText3 = dropdownDataListForm.getDomProperty(GET_ATTRIBUTE_VALUE);
        // Этот тест под сомнением. Когда была ошибка ввода (не стояло dropdownDataListForm.clear();), тест не сломался

        assertAll(
                () -> assertEquals(TEST_TEXT, actualText),
                () -> assertEquals(expectedOptionText, actualText2),
                () -> assertEquals("New York", actualText3, "Wrong city selection")
        );
        // Такие списки можно проверять с помощью скриншот-тестирования
        // Или по координатам передвигать курсор мыши, чтобы попытаться кликом попасть на всплывающее окно
    }

    /**
     * Проверка загрузки файла
     */
    @Test
    void fileUploadTest() throws InterruptedException {
        File file = new File(TEST_FILE_PATH);
        long fileSize = file.length(); // получение размера файла в байтах
        System.out.println("File size: " + fileSize + " bytes");

        // Получаем URL ресурса
        URL url = WebFormTests.class.getClassLoader().getResource("test.txt");

        String absolutePath = null;

        if (url != null) {
            // Получаем абсолютный путь к файлу
            absolutePath = new File(url.getPath()).getAbsolutePath();
            System.out.println("Абсолютный путь к файлу: " + absolutePath);
        } else {
            System.out.println("Ресурс не найден.");
        }

        WebElement fileInputForm = driver.findElement(By.name("my-file"));
        fileInputForm.sendKeys(absolutePath);
        driver.findElement(By.cssSelector(".btn.btn-outline-primary.mt-3")).click();
        Thread.sleep(2000);

        WebElement formSubmittedText = driver.findElement(By.className("display-6"));
        WebElement receivedText = driver.findElement(By.className("lead"));

        assertAll(
                () -> assertTrue(driver.getCurrentUrl().contains("submitted-form"), "Wrong url"),
                () -> assertTrue(driver.getCurrentUrl().contains("test.txt"), "Url doesn't contain the file"),
                () -> assertEquals("Form submitted", formSubmittedText.getText()),
                () -> assertEquals("Received!", receivedText.getText())
        );
        // Как узнать размер загруженного файла, чтобы сравнить с fileSize? Как сравнить хэшкод файла?
    }

    // Из разбора ДЗ:
    @Test
    void fileUploadTest2() throws InterruptedException {
        WebElement fileInputForm = driver.findElement(By.name("my-file"));
        String fileName = "test.txt";
        File file = new File(TEST_FILE_PATH);
        fileInputForm.sendKeys(file.getAbsolutePath());
        assertEquals("C:\\fakepath\\" + fileName, fileInputForm.getDomProperty(GET_ATTRIBUTE_VALUE));
        Thread.sleep(1000);
    }

    /**
     * Проверка чек-боксов
     */
    @Test
    void checkedCheckboxTest() {
        WebElement checkedCheckboxForm = driver.findElement(By.id("my-check-1"));

        checkedCheckboxForm.click();
        boolean actualCheckboxStatus = checkedCheckboxForm.isSelected();

        assertFalse(actualCheckboxStatus);
    }

    @Test
    void defaultCheckboxTest() {
        WebElement defaultCheckboxForm = driver.findElement(By.id("my-check-2"));

        defaultCheckboxForm.click();
        boolean actualCheckboxStatus = defaultCheckboxForm.isSelected();

        assertTrue(actualCheckboxStatus);
    }

    @Test
    void checkedRadioTest() {
        WebElement checkedRadioForm = driver.findElement(By.id("my-radio-1"));
        boolean actualRadioStatus = checkedRadioForm.isSelected();

        assertTrue(actualRadioStatus);
    }

    @Test
    void defaultRadioTest() {
        WebElement defaultRadioForm = driver.findElement(By.id("my-radio-2"));
        boolean actualRadioStatus = defaultRadioForm.isSelected();

        assertFalse(actualRadioStatus);
    }

    @Test
    void checkedAndDefaultRadioTest() {
        WebElement checkedRadioForm = driver.findElement(By.id("my-radio-1"));
        WebElement defaultRadioForm = driver.findElement(By.id("my-radio-2"));

        defaultRadioForm.click();
        boolean actualCheckedRadioStatus = checkedRadioForm.isSelected();
        boolean actualDefaultRadioStatus = defaultRadioForm.isSelected();

        assertAll(
                () -> assertFalse(actualCheckedRadioStatus),
                () -> assertTrue(actualDefaultRadioStatus)
        );
    }

    @Test
    void colorPickerTest() {
        WebElement colorPickerForm = driver.findElement(By.name("my-colors"));
        String script = "arguments[0].value = arguments[1]; arguments[0].dispatchEvent(new Event('change'))";

        // .asHex() не находит, заменила на String.format():
        js.executeScript(script, colorPickerForm, String.format("#%02X%02X%02X", COLOR_VIOLET_RGB.getRed(), COLOR_VIOLET_RGB.getGreen(), COLOR_VIOLET_RGB.getBlue()));
        assertEquals(COLOR_VIOLET_HEX, colorPickerForm.getDomProperty(GET_ATTRIBUTE_VALUE));

        js.executeScript(script, colorPickerForm, String.format("#%02X%02X%02X", COLOR_RED_RGB.getRed(), COLOR_RED_RGB.getGreen(), COLOR_RED_RGB.getBlue()));
        assertEquals(COLOR_RED_HEX, colorPickerForm.getDomProperty(GET_ATTRIBUTE_VALUE));

        js.executeScript(script, colorPickerForm, String.format("#%02X%02X%02X", COLOR_BLUE_RGB.getRed(), COLOR_BLUE_RGB.getGreen(), COLOR_BLUE_RGB.getBlue()));
        assertEquals(COLOR_BLUE_HEX, colorPickerForm.getDomProperty(GET_ATTRIBUTE_VALUE));

        js.executeScript(script, colorPickerForm, String.format("#%02X%02X%02X", COLOR_GREEN_RGB.getRed(), COLOR_GREEN_RGB.getGreen(), COLOR_GREEN_RGB.getBlue()));
        assertEquals(COLOR_GREEN_HEX, colorPickerForm.getDomProperty(GET_ATTRIBUTE_VALUE));
    }

    /**
     * Проверка работы с датой
     */
    @Test
    void datePickerTextTest() throws InterruptedException {
        WebElement datePickerForm = driver.findElement(By.name("my-date"));

        datePickerForm.sendKeys("03 03 1988");
        datePickerForm.sendKeys(Keys.ENTER);
        Thread.sleep(2000);

        assertEquals("03/03/1988", datePickerForm.getDomProperty(GET_ATTRIBUTE_VALUE), "Date format not working or else");
    }

    @Test
    void datePickerTextTest2() throws InterruptedException {
        WebElement datePickerForm = driver.findElement(By.name("my-date"));
        assertTrue(datePickerForm.getDomProperty(GET_ATTRIBUTE_VALUE).isEmpty(), "По умолчанию поле должно быть пустым");

        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        System.out.println(date);
        Thread.sleep(2000);
        js.executeScript("document.querySelector('input[name=\"my-date\"]').value='" + date + "';");
        Thread.sleep(2000);
        String selectDate = datePickerForm.getDomProperty(GET_ATTRIBUTE_VALUE);
        assertEquals(date, selectDate, "Dates doesn't match");
    }

    @Test
    void datePickerMouseTest() {
        WebElement datePickerForm = driver.findElement(By.name("my-date"));

        actions.moveToElement(datePickerForm).click().perform();
        WebElement datePickerOptions = driver.findElement(By.cssSelector("div.datepicker-days table.table-condensed td.day:nth-child(2)"));
        actions.moveToElement(datePickerOptions).click().perform();
        String actualText = datePickerForm.getAttribute("value");

        assertEquals("02/24/2025", actualText);
        // TODO Проверять месяц. Текущая дата и месяц меняются. Тест не гибкий, легко сломается в дальнейшем
        // Что стоит сделать:
        // Узнаём сегодняшний месяц,дату
        // Предыдущие дни - "old day" (Но они будут не всегда видны, только если 1 число не понедельник), проверить цвет
        // находим сегодняшнюю дату - проверяем "day" и дата, нажать
        // Кнопка next, кнопка previous
    }

    @Test
    void datePickerMouseTest2() {
        String dateFormat = "MM/dd/yyyy";
        WebElement datePickerForm = driver.findElement(By.name("my-date"));
        if (datePickerForm.getDomProperty(GET_ATTRIBUTE_VALUE) != null) {
            assertTrue(datePickerForm.getDomProperty(GET_ATTRIBUTE_VALUE).isEmpty(), "Some day is selected");
        }

        datePickerForm.click();
        LocalDate newDate = LocalDate.now().plusDays(1);
        // handling the case when next day is in the next month, locator is different
        String xpathClassName = LocalDate.now().getMonth().maxLength() == LocalDate.now().getDayOfMonth() ? "new day" : "day";
        WebElement dateToSelect = driver.findElement(By.xpath(String.format("//td[@class='%s' and text()='%d']", xpathClassName, newDate.getDayOfMonth())));
        dateToSelect.click();

        assertEquals(newDate.format(DateTimeFormatter.ofPattern(dateFormat)), datePickerForm.getDomProperty(GET_ATTRIBUTE_VALUE), "Incorrect date selected");
    }

    @Test
    void rangePickerKeysTest() {
        WebElement exampleRangeForm = driver.findElement(By.name("my-range"));

        exampleRangeForm.sendKeys(Keys.ARROW_RIGHT);

        assertEquals("6", exampleRangeForm.getDomProperty(GET_ATTRIBUTE_VALUE));
    }

    @Test
    void rangePickerMouseTest() throws InterruptedException {
        WebElement exampleRangeForm = driver.findElement(By.name("my-range"));
        exampleRangeForm.click();
        String initialValue = exampleRangeForm.getDomProperty(GET_ATTRIBUTE_VALUE);
//        int size = 600;
        int size = exampleRangeForm.getSize().width;
        int step = size / 10;

        actions.clickAndHold(exampleRangeForm)
                .moveByOffset(step, 0)
                .release()
                .perform();
        Thread.sleep(1000);
        String newValue = exampleRangeForm.getDomProperty(GET_ATTRIBUTE_VALUE);

        assertNotEquals(initialValue, newValue, "Ползунок не сдвинулся");
    }

    // Другой пример такого же теста
    @Test
    void rangePickerMouseTest2() throws InterruptedException {
        WebElement exampleRangeForm = driver.findElement(By.name("my-range"));
        int width = exampleRangeForm.getSize().getWidth();
        int x = exampleRangeForm.getLocation().getX();
        int y = exampleRangeForm.getLocation().getY();
        for (int i = 0; i < 0; i++) {
            actions.moveToElement(exampleRangeForm)
                    .clickAndHold()
                    .moveToLocation(x + width / 10 * i, y)
                    .release()
                    .perform();
            assertEquals(String.valueOf(i), exampleRangeForm.getDomProperty(GET_ATTRIBUTE_VALUE));
        }
    }
}
