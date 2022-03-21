package com.erdioran.base;


import com.aventstack.extentreports.ExtentTest;
import com.erdioran.listener.SeleniumListener;
import com.erdioran.utils.DataManager;
import com.erdioran.utils.ExtentTestManager;
import com.erdioran.utils.Helper;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.events.EventFiringWebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.lang.reflect.Method;

public abstract class BaseTest {

    private static final Logger LOGGER = LogManager.getLogger(BaseTest.class);



    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method, ITestResult result, ITestContext context) {
        ThreadContext.put("testName", method.getName());
        LOGGER.info("Executing test method : [{}] in class [{}]", result.getMethod().getMethodName(),
                result.getTestClass().getName());
        String nodeName =
                StringUtils.isNotBlank(result.getMethod().getDescription()) ? result.getMethod().getDescription() : method.getName();
        ExtentTest test = ExtentTestManager.getTest().createNode(nodeName);
        ExtentTestManager.setNode(test);
        test.info("Test Started");
        LOGGER.info("Launching fresh browser");
        launchBrowser(context);
        homePage();
    }

    @AfterMethod(alwaysRun = true)
    public void closeBrowser(ITestResult result, ITestContext context) {
        if (!result.isSuccess()) {
            LOGGER.error("Current test method failed");
            context.setAttribute("previousTestStatus", "failed");
            ExtentTestManager.getNode().fail("Test failed");
            DriverManager.quitDriver();
        } else {
            context.setAttribute("previousTestStatus", "passed");
            DriverManager.quitDriver();
        }
    }

    @AfterTest(alwaysRun = true)
    public void afterTest() {
        DriverManager.quitDriver();
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        Helper.deleteAllFiles(".json", Constant.TARGET_DIR, "eventsExport");
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        DriverManager.quitDriver();
        Helper.deleteAllFiles(".json", Constant.TARGET_DIR, "eventsExport");
    }

    private void launchBrowser(ITestContext context) {
        WebDriverManager.chromedriver().setup();
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--disable-gpu");
        WebDriver driver = new ChromeDriver(chromeOptions);
        driver.manage().window().maximize();
        EventFiringWebDriver eventFiringWebDriver = new EventFiringWebDriver(driver);
        eventFiringWebDriver.register(new SeleniumListener());
        DriverManager.setDriver(eventFiringWebDriver);
    }


    public void homePage() {
        LOGGER.info("Logging into application");
        WebDriver driver = DriverManager.getDriver();
        try {
            driver.get(DataManager.getData("data.url"));
        } catch (Exception e) {
            LOGGER.info("Page failed to load. Trying again");
            driver.get(DataManager.getData("data.url"));
        }

    }


}
