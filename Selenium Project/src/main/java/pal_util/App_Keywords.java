package pal_util;

/**
 * The App keyword class extends Generic keyword file and contanis application
 * specific action keywords.
 *
 * @author Rajender Pal
 */

import java.io.FileInputStream;
import java.time.Duration;
import java.util.Properties;
import java.io.File;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class App_Keywords {

	static final String CHROME = "chrome";
	static final String IE = "ie";
	static final String MOZILLA = "mozilla";
	static final String ID = "_id";
	static final String XPATH = "_xpath";
	static final String NAME = "_name";
	static final String CSS = "_css";
	static final String CLASS = "_class";
	static final String LINK_TEXT = "_linktext";
	public WebDriver driver;
	public Properties prop = null;
	public ExtentTest ExtTest = null;
	public String UserName = null;
	public int EndResult = 0;

	// Initialize property file object in the constructor of this class

	public App_Keywords(ExtentTest t) {
		ExtTest = t;
		prop = new Properties();
		String path = System.getProperty("user.dir") + "//src//test//repository//" + "project.properties";
		FileInputStream fis;
		try {
			fis = new FileInputStream(path);
			prop.load(fis);
			ExtTest.log(LogStatus.INFO, "Read data from property file.");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public WebDriver openbrowser(String browserkey) {
		ExtTest.log(LogStatus.INFO, "Opening the browser:- " + browserkey);
//		System.out.println("Browser: " + browserkey);
		File fl = null;
		if (browserkey == null) {
			ExtTest.log(LogStatus.ERROR, "Browser field is blank!");
			Assert.fail("Browser field is blank!");
		} else if (browserkey.equalsIgnoreCase(MOZILLA)) {
			ProfilesIni allprof = new ProfilesIni();
			FirefoxProfile profile = allprof.getProfile("default");
			// profile.setEnableNativeEvents(true);
			profile.setAcceptUntrustedCertificates(true);
			profile.setAssumeUntrustedCertificateIssuer(true);
			driver = new FirefoxDriver();

		} else if (browserkey.equalsIgnoreCase(CHROME)) {
			driver = new ChromeDriver();

		} else if (browserkey.equalsIgnoreCase(IE)) {
			fl = new File(System.getProperty("user.dir") + "\\IEDriverServer.exe");
			if (!fl.exists()) {
				ExtTest.log(LogStatus.ERROR, "IE driver executable file does not exist." + " in the project path");
				Assert.fail("IE driver executable file does not exist in the project path.");
			}
			System.setProperty("webdriver.ie.driver", System.getProperty("user.dir") + "\\IEDriverServer.exe");
			driver = new InternetExplorerDriver();
		}
		if (driver == null) {
			ExtTest.log(LogStatus.ERROR, "Oh ho..Specified browser is not configured.");
			Assert.fail("Oh ho..Specified browser is not configured.");
		}

		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(2000));
		return driver;
	}

	public void openurl(String urlkey) {
		waitTillPageLoaded();
		driver.navigate().to(prop.getProperty(urlkey));
		ExtTest.log(LogStatus.INFO, "Opened URL :- " + prop.getProperty(urlkey));
	}

	public void input(String locatorkey, String value) {
		ExtTest.log(LogStatus.INFO, "Entering the required data in element:- " + locatorkey);
		if (locatorkey.contains("username_")) {
			UserName = value;
			System.out.println("User try to log in with:- " + UserName);
		}
		WebElement e = getElement(locatorkey);
		try {
			e.clear();
			e.sendKeys(value);
			if (locatorkey.contains("Search") || locatorkey.contains("search")) {
				e.sendKeys(Keys.ENTER);
				waitTillPageLoaded();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			System.out.println("This is CATCH block.");
		}
		ExtTest.log(LogStatus.INFO, "Entered data in element:- " + locatorkey);
		capturescreenshot();
	}

	public void verifyTextPresent(String locatorkey, String datakey) {
		// Verify text is present on given location.
		waitTillPageLoaded();
		if (isElementPresent(locatorkey)) {
			scrollToElement(locatorkey);
			String s1 = getElement(locatorkey).getText();
			if (s1.equalsIgnoreCase(datakey)) {
				ExtTest.log(LogStatus.PASS,
						"Text is present. Actual text is -> " + s1 + " and Expected text is -> " + datakey);
				capturescreenshot();
			} else {
				reportFailure("Text is not present. Actual text is -> " + s1 + " but Expected text is -> " + datakey);
			}
		} else {
			reportFailure("This element is not present :- " + locatorkey);
		}
	}

	public boolean isElementPresent(String locatorkey) {
		List<WebElement> ele = null;
		if (locatorkey.endsWith(XPATH))
			ele = driver.findElements(By.xpath(prop.getProperty(locatorkey)));
		else if (locatorkey.endsWith(ID))
			ele = driver.findElements(By.id(prop.getProperty(locatorkey)));
		else if (locatorkey.endsWith(NAME))
			ele = driver.findElements(By.name(prop.getProperty(locatorkey)));
		else if (locatorkey.endsWith(CSS))
			ele = driver.findElements(By.cssSelector(prop.getProperty(locatorkey)));
		else if (locatorkey.endsWith(LINK_TEXT))
			ele = driver.findElements(By.linkText(prop.getProperty(locatorkey)));
		else if (locatorkey.endsWith(CLASS))
			ele = driver.findElements(By.className(prop.getProperty(locatorkey)));
//		System.out.println(locatorkey + " elements found: " + ele.size());
		if (ele.size() == 0)
			return false;
		else
			return true;
	}

	public void clickelement(String locatorkey) throws InterruptedException {
		waitTillPageLoaded();
		WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(30));
		wait.until(ExpectedConditions.visibilityOf(getElement(locatorkey)));
		if (isElementPresent(locatorkey)) {
			scrollToElement(locatorkey);
			getElement(locatorkey).click();
			ExtTest.log(LogStatus.INFO, "Clicked on element:- " + locatorkey);
			capturescreenshot();
		} else {
			reportFailure("Required element:- " + locatorkey + " is not visible.");
		}
	}

	public WebElement getElement(String locatorkey) {
		WebElement ele = null;
		try {
			if (locatorkey.endsWith(XPATH)) {
				ele = driver.findElement(By.xpath(prop.getProperty(locatorkey)));
			} else if (locatorkey.endsWith(ID)) {
				ele = driver.findElement(By.id(prop.getProperty(locatorkey)));
			} else if (locatorkey.endsWith(NAME)) {
				ele = driver.findElement(By.name(prop.getProperty(locatorkey)));
			} else if (locatorkey.endsWith(CSS)) {
				ele = driver.findElement(By.cssSelector(prop.getProperty(locatorkey)));
			} else if (locatorkey.endsWith(LINK_TEXT)) {
				ele = driver.findElement(By.linkText(prop.getProperty(locatorkey)));
			} else if (locatorkey.endsWith(CLASS)) {
				ele = driver.findElement(By.className(prop.getProperty(locatorkey)));
			} else {
				reportFailure("Buddy..Wrong Element locator key is provided:- " + locatorkey);
			}
		} catch (Exception e) {
			reportFailure("Error in locating the Webelement ->  " + locatorkey);
		}
		return ele;
	}

	public void capturescreenshot() {
		// fileName of the screenshot
		Date d = new Date();
		String screenshotFilename = d.toString().replace(":", "_").replace(" ", "_") + ".png";
		// store screenshot in that file
		File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String savefileloc = System.getProperty("user.dir") + "//screenshots//" + screenshotFilename;
		try {
			FileUtils.copyFile(scrFile, new File(savefileloc));
		} catch (Exception e) {
			e.printStackTrace();
		} // put screenshot file in reports
		ExtTest.log(LogStatus.INFO, "Attaching Screenshot-> " + ExtTest.addScreenCapture(savefileloc));
	}

	public void waitTillPageLoaded() {
		// Check if page is fully loaded
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		String status = "in-progress";
		while (!status.equals("complete")) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			status = (String) jse.executeScript("return document.readyState");
			if (status.equalsIgnoreCase("complete"))
				break;
		}
		System.out.println("Page load status is:- " + status);
	}

	public void FinalTestResult() {
		if (EndResult == 1) {
			System.out.println("Final Test result is: PASS.");
			ExtTest.log(LogStatus.PASS, "Test case is PASSED.");
		} else if (EndResult == 0) {
			System.out.println("Final Test result is: FAIL.");
			ExtTest.log(LogStatus.FAIL, "Test case is FAILED.");
		}
	}

	public void lastTestStep() {
		ExtTest.log(LogStatus.INFO, "This is the last step for this test case.");
		EndResult = 1;
	}

	public void refreshPage() {
		driver.navigate().refresh();
		ExtTest.log(LogStatus.INFO, "Page refreshed.");

	}

	public void scrollToElement(String locatorkey) {
		WebElement ele = getElement(locatorkey);
		JavascriptExecutor jse = (JavascriptExecutor) driver;
		jse.executeScript("arguments[0].scrollIntoView(false);", ele);
	}

	public void closebrowser() {
		driver.close();
		ExtTest.log(LogStatus.INFO, "Closed driver.");
	}

	public void reportFailure(String failureMsg) {
		ExtTest.log(LogStatus.FAIL, failureMsg);
		capturescreenshot();
		driver.close();
		Assert.fail(failureMsg);
	}

}
