import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pages.ProductsPage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Pruebas automatizadas de SauceDemo que reflejan los casos documentados
 * en TestLink (proyecto SwagLabs, carpetas "Products" y "Shopping cart").
 * <p>
 * No se incluyen los casos de la carpeta "Login" (SL-1 a SL-4, SL-11) porque
 * ese escenario ya fue automatizado en clase, tal como indica el enunciado
 * ("No repetir los escenarios automatizados en clases como ser: Login, Logout y otros").
 * <p>
 * Conceptos usados: selectores (id, className, xpath), assertions y Page Object Model.
 */
public class ProductsTests extends BaseTest {

    // ---------------------------------------------------------------
    // SL-5: Verificar visualizacion del listado de productos
    // ---------------------------------------------------------------
    @Test
    @DisplayName("SL-5: Verificar visualizacion del listado de productos")
    public void verifyProductListIsDisplayed() {
        ProductsPage productsPage = loginAndGoToProducts(STANDARD_USER, PASSWORD);

        Assertions.assertTrue(productsPage.isProductsPageDisplayed());
        Assertions.assertTrue(productsPage.homeTitleIsDisplayed());
        Assertions.assertEquals(6, productsPage.getProductCount(),
                "Se esperan 6 productos en el listado");
        Assertions.assertFalse(productsPage.getProductNamesAsDisplayed().isEmpty());
    }

    // ---------------------------------------------------------------
    // SL-6: Ordenar productos por nombre de A a Z
    // ---------------------------------------------------------------
    @Test
    @DisplayName("SL-6: Ordenar productos por nombre de A a Z")
    public void verifySortProductsByNameAZ() {
        ProductsPage productsPage = loginAndGoToProducts(STANDARD_USER, PASSWORD);

        productsPage.sortBy("az");
        List<String> actualOrder = productsPage.getCurrentProductNamesOrder();

        List<String> expectedOrder = new ArrayList<>(actualOrder);
        Collections.sort(expectedOrder);

        Assertions.assertEquals(expectedOrder, actualOrder,
                "El listado deberia estar ordenado alfabeticamente de A a Z");
    }

    // ---------------------------------------------------------------
    // SL-7: Ordenar productos por precio de menor a mayor
    // ---------------------------------------------------------------
    @Test
    @DisplayName("SL-7: Ordenar productos por precio de menor a mayor")
    public void verifySortProductsByPriceLowToHigh() {
        ProductsPage productsPage = loginAndGoToProducts(STANDARD_USER, PASSWORD);

        productsPage.sortBy("lohi");
        List<Double> actualOrder = productsPage.getCurrentProductPricesOrder();

        List<Double> expectedOrder = new ArrayList<>(actualOrder);
        Collections.sort(expectedOrder);

        Assertions.assertEquals(expectedOrder, actualOrder,
                "El listado deberia estar ordenado por precio de menor a mayor");
    }

    // ---------------------------------------------------------------
    // SL-8: Agregar un producto al carrito
    // ---------------------------------------------------------------
    @Test
    @DisplayName("SL-8: Agregar un producto al carrito")
    public void verifyAddProductToCart() {
        ProductsPage productsPage = loginAndGoToProducts(STANDARD_USER, PASSWORD);

        productsPage.addProductToCartByIndex(0);

        Assertions.assertEquals(1, productsPage.getCartBadgeCount(),
                "El contador del carrito deberia mostrar 1 producto agregado");
    }

    // ---------------------------------------------------------------
    // SL-9: Eliminar un producto del carrito
    // ---------------------------------------------------------------
    @Test
    @DisplayName("SL-9: Eliminar un producto del carrito")
    public void verifyRemoveProductFromCart() {
        ProductsPage productsPage = loginAndGoToProducts(STANDARD_USER, PASSWORD);

        productsPage.addProductToCartByIndex(0);
        Assertions.assertEquals(1, productsPage.getCartBadgeCount());

        productsPage.removeProductFromCartByIndex(0);

        Assertions.assertEquals(0, productsPage.getCartBadgeCount(),
                "El contador del carrito deberia volver a 0 tras eliminar el producto");
    }

    // ---------------------------------------------------------------
    // SL-10: Verificar contador del carrito con multiples productos
    // ---------------------------------------------------------------
    @Test
    @DisplayName("SL-10: Verificar contador del carrito con multiples productos")
    public void verifyCartBadgeWithMultipleProducts() {
        ProductsPage productsPage = loginAndGoToProducts(STANDARD_USER, PASSWORD);

        productsPage.addProductToCartByIndex(0);
        productsPage.addProductToCartByIndex(1);
        productsPage.addProductToCartByIndex(2);

        Assertions.assertEquals(3, productsPage.getCartBadgeCount(),
                "El contador del carrito deberia reflejar los 3 productos agregados");

        productsPage.clickCartIcon();
        pages.CartPage cartPage = new pages.CartPage(driver);
        Assertions.assertEquals(3, cartPage.getCartItemsCount(),
                "La pagina del carrito deberia listar los mismos 3 productos");
    }

    // ---------------------------------------------------------------
    // SL-12: Fallo al agregar Producto (usuario error_user)
    // ---------------------------------------------------------------
    @Test
    @DisplayName("SL-12: Fallo al agregar producto con error_user")
    public void verifyAddToCartFailsWithErrorUser() {
        ProductsPage productsPage = loginAndGoToProducts(ERROR_USER, PASSWORD);

        // NOTA: ajustar esta aseveracion segun el comportamiento real
        // documentado como "Resultado esperado" del caso SL-12 en TestLink,
        // ya que error_user reproduce una falla puntual de la aplicacion.
        boolean addedSuccessfully = productsPage.addProductToCartByIndexSucceeded(0);

        Assertions.assertFalse(addedSuccessfully,
                "Se espera que agregar un producto falle con el usuario error_user");
    }

    // ---------------------------------------------------------------
    // SL-13: Error al ordenar productos con error_user
    // ---------------------------------------------------------------
    @Test
    @DisplayName("SL-13: Error al ordenar productos con error_user")
    public void verifySortProductsFailsWithErrorUser() {
        ProductsPage productsPage = loginAndGoToProducts(ERROR_USER, PASSWORD);

        productsPage.sortBy("lohi");
        List<Double> actualOrder = productsPage.getCurrentProductPricesOrder();

        List<Double> expectedOrder = new ArrayList<>(actualOrder);
        Collections.sort(expectedOrder);

        // NOTA: ajustar esta aseveracion segun el comportamiento real
        // documentado como "Resultado esperado" del caso SL-13 en TestLink.
        Assertions.assertNotEquals(expectedOrder, actualOrder,
                "Se espera que el orden no se aplique correctamente con el usuario error_user");
    }
}
