package com.visionit.automation.stepdefs;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.visionit.automation.core.WebDriverFactory;
import com.visionit.automation.pageobjects.CmnPageObjects;
import com.visionit.automation.pageobjects.SearchPageObjects;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class StepDefs {

	private static final Logger logger = LogManager.getLogger(StepDefs.class);

	WebDriver driver;
	String base_url = "https://amazon.in";
	int implicit_wait_timeout_in_sec = 20;
	Scenario scn; // this is set in the @Before method

	CmnPageObjects cmnPageObjects;
	SearchPageObjects searchPageObjects;

	// make sure to use this before import io.cucumber.java.Before;
	// Use @Before to execute steps to be executed before each scnerio
	// one example can be to invoke the browser
	@Before
	public void setUp(Scenario scn) throws Exception{
		this.scn = scn;
		//driver = new ChromeDriver();
		String browserName = WebDriverFactory.getBrowserName();
		driver = WebDriverFactory.getWebDriverForBrowser(browserName);
		logger.info("Browser invoked.");

		cmnPageObjects = new CmnPageObjects(driver, scn);
		searchPageObjects = new SearchPageObjects(driver, scn);
		
	}

	// make sure to use this after import io.cucumber.java.After;
	// Use @After to execute steps to be executed after each scnerio
	// one example can be to close the browser
	@After
	public void cleanUp(){
		driver.quit();
		scn.log("Browser Closed");
		logger.info("Browser Closed");
	}

	//	@Given("User opened browser")
	//	public void user_opened_browser() {
	//		driver = new ChromeDriver();
	//		driver.manage().window().maximize();
	//		driver.manage().timeouts().implicitlyWait(implicit_wait_timeout_in_sec, TimeUnit.SECONDS); 
	//	}

	@Given("User navigated to the home application url")
	public void user_navigated_to_the_home_application_url() {
		driver.get(base_url);
		logger.info("Browser navigated to URL: " + base_url);
		scn.log("Browser navigated to URL: " + base_url);
		cmnPageObjects.validateLandingPageTitle();
	}

	@When("User Search for product {string}")
	public void user_search_for_product(String productName) {
		//Wait and Search for product
		cmnPageObjects.searchProduct(productName);
		cmnPageObjects.clickOnSearchBtn();

	}

	@Then("Search Result page is displayed")
	public void search_result_page_is_displayed() {
		//Wait for titile
		searchPageObjects.validateSearchPageTitle();

	}

	@When("User click on any product")
	public void user_click_on_any_product() {
		//listOfProducts will have all the links displayed in the search box
		searchPageObjects.clickOnFirstProd();
	}

	@Then("Product Description is displayed in new tab")
	public void product_description_is_displayed_in_new_tab() {
		//As product description click will open new tab, we need to switch the driver to the new tab
		//If you do not switch, you can not access the new tab html elements
		//This is how you do it
		logger.info("Switching form Base Window to New Window");
		Set<String> handles = driver.getWindowHandles(); // get all the open windows
		scn.log("List of windows found: "+handles.size());
		logger.info("List of windows found: "+handles.size());
		scn.log("Windows handles: " + handles.toString());
		logger.info("Windows handles: " + handles.toString());
		Iterator<String> it = handles.iterator(); // get the iterator to iterate the elements in set
		String original = it.next();//gives the parent window id
		String prodDescp = it.next();//gives the child window id

		driver.switchTo().window(prodDescp); // switch to product Descp
		scn.log("Switched to the new window/tab");
		logger.info("Switched to the new window/tab with id : " + prodDescp);

		//Now driver can access new driver window, but can not access the orignal tab
		//Check product title is displayed
		WebElement productTitle = driver.findElement(By.id("productTitle"));
		logger.info("Created webelement for productTitle");

		Assert.assertEquals("Product Title",true,productTitle.isDisplayed());
		scn.log("Product Title header is matched and displayed as: " + productTitle.getText() );
		logger.info("Product Title header is matched and displayed as: " + productTitle.getText() );

		WebElement addToCartButton = driver.findElement(By.xpath("//input[@id='add-to-cart-button']"));
		Assert.assertEquals("Product Title",true,addToCartButton.isDisplayed());
		scn.log("Add to cart Button is displayed");
		logger.info("Add to cart Button is displayed");

		//Switch back to the Original Window, however no other operation to be done
		driver.switchTo().window(original);
		scn.log("Switched back to Original tab");
		logger.info("Switched back to Original tab/window with id : " + original);
	}

}
