package org.aleksandraRaszka.testProject;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.concurrent.TimeUnit;

public class CartTest {

    WebDriver driver;
    WebDriverWait wait;
    By shopButtonOnMainPage = By.cssSelector("#menu-item-198");
    By windsurfingCategoryOnMainPage = By.cssSelector("img[alt=\"Windsurfing\"]");
    By productInProductPage = By.xpath(".//h2[text()='Egipt – El Gouna']");
    By productInCart = By.xpath(".//a[text()='Egipt - El Gouna']");
    By quantityField = By.cssSelector("input[type='number']");
    WebElement quantity;
    String quantityValue;
    String[] productPages = {"/yoga-i-pilates-w-portugalii/", "/wspinaczka-via-ferraty/", "/wspinaczka-island-peak/",
            "/fuerteventura-sotavento/", "/yoga-i-pilates-w-hiszpanii/", "/windsurfing-w-karpathos/",
            "/wyspy-zielonego-przyladka-sal/", "/wakacje-z-yoga-w-kraju-kwitnacej-wisni/",
            "/egipt-el-gouna/", "/wczasy-relaksacyjne-z-yoga-w-toskanii/"};

    By cartButton = By.cssSelector("a[class=\"cart-contents\"]");
    By cartPage = By.cssSelector(".site-main");


    @BeforeAll
    public static void downloadChromedriver() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    public void testSetUp() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.manage().timeouts().pageLoadTimeout(10, TimeUnit.SECONDS);
        driver.navigate().to("https://fakestore.testelka.pl/");
        wait = new WebDriverWait(driver, 15);

        driver.findElement(By.cssSelector(".woocommerce-store-notice__dismiss-link")).click();

    }

    //test case 1
    //As user, I want to add selected trip to the cart from this trip page
    @Test
    public void addProductFromProductPageTest() {
        goToProductPage();
        getQuantityValue();
        addToCartFromProductPage();
        goToCart();
        cartAssertion();

    }

    //test case 2
    // As user, I want to add to the cart selected trip from category page
    @Test
    public void addProductFromCategoryPageTest() {
        goToProductCategoryPage();
        WebElement addToCartFromCategoryPage = driver.findElement(By.cssSelector("a[data-product_id='386']"));
        addToCartFromCategoryPage.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[class='added_to_cart wc-forward']"))).click();
        quantityValue = "1";
        cartAssertion();
    }

    // test case 3
    // As user, I want to add at least 10 trips to the cart (one kind)
    @Test
    public void addTenTripsOneKindToCartTest() {
        goToProductPage();
        changeQuantity("10");
        getQuantityValue();
        addToCartFromProductPage();
        goToCart();
        cartAssertion();
    }

    //test case 4
    // As user I want to add at least 10 different trips to cart

    @Test
    public void addTenTripsDifferentKindToCartTest() {
        for (String productPage : productPages) {
            driver.navigate().to("https://fakestore.testelka.pl/product" + productPage);
            addToCartFromProductPage();
        }

        goToCart();
        int NumberOfElementsInCart = driver.findElements(By.cssSelector(".cart_item")).size();

        Assertions.assertEquals(10, NumberOfElementsInCart, "Number of items in cart is different. Expected 10, but was: "
                + NumberOfElementsInCart);


    }

    // test case 5
    //As User, I want to change amount of product on the cart page

    @Test
    public void changeAmountOfTripsOnCartPageTest() {
        goToProductPage();
        addToCartFromProductPage();
        goToCart();

        quantity = driver.findElement(quantityField);
        changeQuantity("3");
        WebElement updateCart = wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector("button[name='update_cart']")));
        updateCart.click();
        getQuantityValue(); //on the cart page

        Assertions.assertEquals("3", quantityValue, "Amount of product on cart page is different. Expected 3, Was: " + quantityValue);

    }

    //Test case 6
    //As User, I want to delete trip from the cart page
    @Test
    public void deleteElementFromCartTest(){
        goToProductPage();
        addToCartFromProductPage();
        goToCart();

        WebElement deleteButton = driver.findElement(By.cssSelector(".remove"));
        deleteButton.click();
        WebElement alterAfterDelete =  wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".woocommerce-notices-wrapper")));

        Assertions.assertTrue(alterAfterDelete.isDisplayed(), "Alter about deleted product wasn't displayed." +
                " Probably product wasn't removed from the cart");

    }

    @AfterEach
    public void afterEachTest() {
        driver.close();
        driver.quit();
    }

    private void changeQuantity(String keyValue) {
        quantity.clear();
        quantity.sendKeys(keyValue);
    }

    private void getQuantityValue() {
        quantityValue = quantity.getAttribute("value");
    }

    private void goToProductCategoryPage() {
        WebElement goOnShopPage = driver.findElement(shopButtonOnMainPage);
        goOnShopPage.click();
        WebElement goToWindsurfingCategory = driver.findElement(windsurfingCategoryOnMainPage);
        goToWindsurfingCategory.click();
    }

    private void goToProductPage() {
        goToProductCategoryPage();
        WebElement goToProductPage = driver.findElement(productInProductPage);
        goToProductPage.click();
        quantity = driver.findElement(quantityField);
    }

    private void addToCartFromProductPage() {

        WebElement addToCartFromProductPage = driver.findElement(By.cssSelector("button[name='add-to-cart']"));
        addToCartFromProductPage.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("a[class='button wc-forward']")));

    }

    private void goToCart() {
        WebElement clickCartButton = wait.until(ExpectedConditions.elementToBeClickable(cartButton));
        clickCartButton.click();
        wait.until(ExpectedConditions.presenceOfElementLocated(cartPage));


    }

    private void cartAssertion() {
        WebElement goToProductInCart = wait.until(ExpectedConditions.presenceOfElementLocated(productInCart));
        String productInCartUrl = goToProductInCart.getAttribute("href");
        WebElement amountOfProduct = wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input[class='input-text qty text']")));
        String valueOfProduct = amountOfProduct.getAttribute("value");
        goToProductInCart.click();


        Assertions.assertAll("Assertions after adding one product to cart",
                () -> Assertions.assertEquals(quantityValue, valueOfProduct, "In cart is different amount of product than you added"),
                () -> Assertions.assertEquals(driver.getCurrentUrl(), productInCartUrl, "Product wasn't added correctly to the cart"));
    }

}
