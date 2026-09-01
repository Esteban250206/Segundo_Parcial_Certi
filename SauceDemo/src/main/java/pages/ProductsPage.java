package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object de la pantalla de listado de productos (Products / Inventory).
 * Cubre los casos de TestLink: SL-5, SL-6, SL-7, SL-8, SL-9, SL-10, SL-12, SL-13.
 */
public class ProductsPage {
    WebDriver driver;

    @FindBy(className = "title")
    WebElement pageTitle;

    @FindBy(className = "inventory_item")
    List<WebElement> productItems;

    @FindBy(className = "inventory_item_name")
    List<WebElement> productNames;

    @FindBy(className = "inventory_item_price")
    List<WebElement> productPrices;

    @FindBy(className = "inventory_item_img")
    List<WebElement> productImages;

    @FindBy(className = "product_sort_container")
    WebElement sortDropdown;

    @FindBy(className = "shopping_cart_link")
    WebElement cartIcon;

    @FindBy(className = "shopping_cart_badge")
    List<WebElement> cartBadge; // lista para poder validar cuando no existe (carrito vacio)

    public ProductsPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    public boolean isProductsPageDisplayed() {
        return driver.getCurrentUrl().contains("inventory.html");
    }

    public boolean homeTitleIsDisplayed() {
        return pageTitle.isDisplayed() && pageTitle.getText().equalsIgnoreCase("Products");
    }

    // ---------- SL-5: Verificar visualizacion del listado de productos ----------
    public int getProductCount() {
        return productItems.size();
    }

    public List<String> getProductNamesAsDisplayed() {
        return productNames.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    // ---------- SL-6 / SL-7 / SL-13: Ordenar productos ----------
    public void sortBy(String optionValue) {
        // Valores validos del combo: az, za, lohi, hilo
        new Select(sortDropdown).selectByValue(optionValue);
    }

    public List<String> getCurrentProductNamesOrder() {
        return productNames.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public List<Double> getCurrentProductPricesOrder() {
        return productPrices.stream()
                .map(el -> el.getText().replace("$", "").trim())
                .map(Double::parseDouble)
                .collect(Collectors.toList());
    }

    // ---------- SL-8 / SL-9 / SL-12: Agregar / quitar producto del carrito ----------
    public void addProductToCartByIndex(int index) {
        WebElement item = productItems.get(index);
        item.findElement(By.tagName("button")).click();
    }

    public void addProductToCart() {
        addProductToCartByIndex(0);
    }

    /**
     * Intenta agregar un producto al carrito y devuelve si el boton
     * cambio su estado a "Remove" (indicador de que la accion tuvo exito).
     * Se usa para SL-12 (fallo al agregar producto con error_user).
     */
    public boolean addProductToCartByIndexSucceeded(int index) {
        WebElement item = productItems.get(index);
        WebElement button = item.findElement(By.tagName("button"));
        button.click();
        return button.getText().equalsIgnoreCase("Remove");
    }

    public void removeProductFromCartByIndex(int index) {
        WebElement item = productItems.get(index);
        item.findElement(By.tagName("button")).click();
    }

    // ---------- SL-10: Contador del carrito ----------
    public int getCartBadgeCount() {
        if (cartBadge.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(cartBadge.get(0).getText());
    }

    public void clickCartIcon() {
        cartIcon.click();
    }

    // ---------- SL-11: Falla visual en imagenes de productos ----------
    public List<String> getAllProductImageSources() {
        return productImages.stream()
                .map(el -> el.findElement(By.tagName("img")).getAttribute("src"))
                .collect(Collectors.toList());
    }
}
