import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import pages.LoginPage;
import pages.ProductsPage;

public class BaseTest {
    protected WebDriver driver;

    protected static final String BASE_URL = "https://www.saucedemo.com/v1/";

    // Usuarios de prueba provistos por SauceDemo
    protected static final String STANDARD_USER = "standard_user";
    protected static final String PROBLEM_USER = "problem_user";
    protected static final String ERROR_USER = "error_user";
    protected static final String PASSWORD = "secret_sauce";

    @BeforeEach
    public void setup() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        driver.get(BASE_URL);
    }

    @AfterEach
    public void cleanup() {
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * Metodo de apoyo: realiza el login (prerrequisito) y devuelve
     * la pagina de productos ya cargada, para no repetir el escenario
     * de Login como caso automatizado independiente.
     */
    protected ProductsPage loginAndGoToProducts(String user, String password) {
        LoginPage loginPage = new LoginPage(driver);
        return loginPage.loginAs(user, password);
    }
}
