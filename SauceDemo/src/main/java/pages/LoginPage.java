package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

/**
 * Page Object de la pantalla de Login.
 * Se conserva del proyecto del primer parcial: se usa como prerrequisito
 * para llegar a la pantalla de productos, pero no se automatiza como
 * escenario nuevo (Login/Logout ya se trabajaron en clase).
 */
public class LoginPage {
    WebDriver driver;

    @FindBy(id = "user-name")
    WebElement userNameTextBox;

    @FindBy(id = "password")
    WebElement passwordTextBox;

    @FindBy(id = "login-button")
    WebElement loginButton;

    @FindBy(css = "[data-test='error']")
    WebElement errorMessage;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public void setUserNameTextBox(String userName) {
        userNameTextBox.sendKeys(userName);
    }

    public void setPasswordTextBox(String password) {
        passwordTextBox.sendKeys(password);
    }

    public void clickOnLoginButton() {
        loginButton.click();
    }

    public ProductsPage loginAs(String userName, String password) {
        setUserNameTextBox(userName);
        setPasswordTextBox(password);
        clickOnLoginButton();
        return new ProductsPage(driver);
    }
}
