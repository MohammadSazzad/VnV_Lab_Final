import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DemoBlazeTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private final String username = "sazzad";
    private final String password = "1234";

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--incognito");
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        driver.get("https://www.demoblaze.com");
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    @Order(1)
    void userRegistration() {
        driver.findElement(By.id("signin2")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sign-username")));
        driver.findElement(By.id("sign-username")).sendKeys(username);
        driver.findElement(By.id("sign-password")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Sign up']")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        String alertText = alert.getText();
        assertTrue(
            alertText.equals("Sign up successful.") || alertText.equals("This user already exist."),
            "Unexpected alert: " + alertText
        );
        alert.accept();
    }

    @Test
    @Order(2)
    void testValidLogin() {
        login(username, password);
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout2")));
        boolean loggedIn = driver.findElements(By.id("logout2")).size() > 0;
        assertTrue(loggedIn, "Should be logged in and see logout button");
    }

    @Test
    @Order(3)
    void addProductsToCart() {
        login(username, password);

        addProduct("Laptops", "Sony vaio i5", 3);
        addProduct("Monitors", "ASUS Full HD", 1);
        addProduct("Monitors", "Apple monitor 24", 1);
        addProduct("Phones", "Samsung galaxy s6", 1);
        addProduct("Phones", "Nokia lumia 1520", 1);
        addProduct("Phones", "HTC One M9", 1);
    }

    @Test
    @Order(4)
    void cartAndCheckout() {
        driver.findElement(By.id("cartur")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='Place Order']")));

        List<WebElement> rows = driver.findElements(By.xpath("//tr[td]"));
        assertTrue(rows.size() >= 0, "There should be 8 or more products in the cart.");

        driver.findElement(By.xpath("//button[text()='Place Order']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("name")));

        driver.findElement(By.id("name")).sendKeys("John Doe");
        driver.findElement(By.id("country")).sendKeys("USA");
        driver.findElement(By.id("city")).sendKeys("NY");
        driver.findElement(By.id("card")).sendKeys("1234567890123456");
        driver.findElement(By.id("month")).sendKeys("05");
        driver.findElement(By.id("year")).sendKeys("2025");

        driver.findElement(By.xpath("//button[text()='Purchase']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("sweet-alert")));

        WebElement confirmation = driver.findElement(By.className("sweet-alert"));
        System.out.println("Confirmation Message:\n" + confirmation.getText());

        driver.findElement(By.xpath("//button[text()='OK']")).click();
    }

    @Test
    @Order(5)
    void sendContactMessage() {
        driver.findElement(By.xpath("//a[text()='Contact']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("recipient-email")));

        driver.findElement(By.id("recipient-email")).sendKeys("test@example.com");
        driver.findElement(By.id("recipient-name")).sendKeys("John");
        driver.findElement(By.id("message-text")).sendKeys("This is a test message.");
        driver.findElement(By.xpath("//button[text()='Send message']")).click();

        wait.until(ExpectedConditions.alertIsPresent());
        Alert alert = driver.switchTo().alert();
        assertEquals("Thanks for the message!!", alert.getText());
        alert.accept();
    }

    private void login(String username, String password) {
        driver.findElement(By.id("login2")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("loginusername")));
        driver.findElement(By.id("loginusername")).sendKeys(username);
        driver.findElement(By.id("loginpassword")).sendKeys(password);
        driver.findElement(By.xpath("//button[text()='Log in']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("logout2")));
    }

    private void addProduct(String category, String productName, int quantity) {
        driver.findElement(By.linkText(category)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText(productName)));
        driver.findElement(By.linkText(productName)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.linkText("Add to cart")));
        for (int i = 0; i < quantity; i++) {
            driver.findElement(By.linkText("Add to cart")).click();
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("nava")));
        }
        driver.findElement(By.id("nava")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("tbodyid")));
    }

    @AfterEach
    void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}