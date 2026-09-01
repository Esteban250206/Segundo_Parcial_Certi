package pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CartPage {
    WebDriver driver;

    // XPath apuntando exactamente al texto "Remove" que se ve en la interfaz
    @FindBy(xpath = "//button[text()='Remove']")
    WebElement removeButton;

    @FindBy(className = "checkout_button")
    WebElement checkoutButton;

    @FindBy(className = "cart_item")
    List<WebElement> cartItems;

    public CartPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isCartPageDisplayed() {
        return driver.getCurrentUrl().contains("cart.html");
    }

    public void removeProductFromCart() {
        removeButton.click();
    }

    public void clickCheckoutButton() {
        checkoutButton.click();
    }

    // Usado para verificar consistencia con el contador del carrito (SL-10)
    public int getCartItemsCount() {
        return cartItems.size();
    }
}
