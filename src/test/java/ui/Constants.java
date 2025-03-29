package ui;

import java.awt.*;

/**
 * Класс для хранения констант, необходимых для тестирования и используемых в разных классах
 * (final класс - для предотвращения наследования и модификации класса).
 */
public final class Constants {
    // Приватный конструктор предотвращает создание экземпляров этого класса
    private Constants() {
        throw new AssertionError("Cannot instantiate Constants class");
    }

    public static final String TEST_TEXT = "Test";
    public static final String EMPTY_TEXT = "";
    public static final String GET_ATTRIBUTE_VALUE = "value";
    public static final String TEST_FILE_PATH = "src/test/resources/test.txt";

    // COLORS
    // Violet
    public static final Color COLOR_VIOLET_RGB = new Color(86, 55, 219, 1);
    public static final String COLOR_VIOLET_HEX = "#5637db";
    // Red
    public static final Color COLOR_RED_RGB = new Color(220, 25, 62, 1);
    public static final String COLOR_RED_HEX = "#dc193e";
    // Blue
    public static final Color COLOR_BLUE_RGB = new Color(25, 43, 220, 1);
    public static final String COLOR_BLUE_HEX = "#192bdc";
    // Green
    public static final Color COLOR_GREEN_RGB = new Color(32, 180, 50, 1);
    public static final String COLOR_GREEN_HEX = "#20b432";
}
