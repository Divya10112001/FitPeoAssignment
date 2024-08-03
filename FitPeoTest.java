package org.example;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

public class FitPeoTest {
    private static final Logger LOGGER = Logger.getLogger(FitPeoTest.class.getName());
    private WebDriver driver;
    private WebDriverWait wait;

    @BeforeClass
    public void setUp() {
        //Initialize Chrome Driver
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @Test
    public void testFitPeo() {
        //Redirection to FitPeo Website
        driver.get("https://www.fitpeo.com");

        //Open Revenue Calculator Link
        WebElement revenueCalculatorLink = wait.until(
                ExpectedConditions.elementToBeClickable(By.linkText("Revenue Calculator"))
        );
        revenueCalculatorLink.click();

        //Adjust Slider to 820
        WebElement sliderSection = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.className("MuiInputBase-input"))
        );
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", sliderSection);

        WebElement slider = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.className("MuiSlider-track"))
        );

        Actions actions = new Actions(driver);
        actions.clickAndHold(slider).moveByOffset(107, 0).release().perform();
        WebElement textField = wait.until(
                ExpectedConditions.presenceOfElementLocated(By.className("MuiInputBase-input"))
        );

        //Verify that Textbox related to Slider shows 820
        wait.until(ExpectedConditions.attributeToBe(textField, "value", "820"));
        Assert.assertEquals(textField.getAttribute("value"), "820","Incorrect Value of Slider is Found");

        //Change Textbox value to 560
        ((JavascriptExecutor) driver).executeScript("arguments[0].select();", textField);
        textField.sendKeys("560");
        wait.until(ExpectedConditions.attributeToBe(textField, "value", "560"));
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");

        //Validate if the value 560 is reflected everywhere as well
        List<WebElement> patientPerMonth = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("css-hocx5c"))
        );
        Assert.assertEquals(patientPerMonth.get(2).getText(), "560","Value of TextBox is not Updated");

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            LOGGER.warning("InterruptedException occurred: " + e.getMessage());
        }

        //Change slider value back to 820
        ((JavascriptExecutor) driver).executeScript("arguments[0].select();", textField);
        textField.sendKeys("820");
        wait.until(ExpectedConditions.attributeToBe(textField, "value", "820"));

        //Validate the CPT codes and mark the required checkboxes
        List<WebElement> cptCodes = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("css-1s3unkt"))
        );
        List<WebElement> cptCodesCheckboxes = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("PrivateSwitchBase-input"))
        );
        for (int i = 0 ;i < cptCodes.size() ; i += 2) {
            if (cptCodes.get(i).getText().equals("CPT-99091") || cptCodes.get(i).getText().equals("CPT-99453")
                    || cptCodes.get(i).getText().equals("CPT-99454") || cptCodes.get(i).getText().equals("CPT-99474"))
                {
                    cptCodesCheckboxes.get(i/2).click();
                }
        }

        //Validate the Total Reimbursement Headers for $110700
        List<WebElement> totalReimbursementHeader = wait.until(
                ExpectedConditions.presenceOfAllElementsLocatedBy(By.className("css-hocx5c"))
        );
        Assert.assertEquals(totalReimbursementHeader.get(3).getText(), "$110700", "Reimbursement is different");
    }

    @AfterClass
    public void tearDown() {
        driver.quit();
    }
}
