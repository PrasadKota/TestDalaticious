package com.nexus.scripts;

import java.io.FileWriter;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.opencsv.CSVWriter;

public class TestDataliciousLogs {
	public ChromeDriver driver = null;
	public CSVWriter writeLog = null;
	public boolean dp = false, dt = false, ga = false, oh = false;
	public String dP = "null", dT = "null";

	//variable dp(parameter),dt(parameter),ga for google analytics,oh for optimahub
	//dP,dT string values to store the data if any parameter is present
	
	@BeforeMethod
	public void preData() {
		try {

			writeLog = new CSVWriter(new FileWriter("C:\\Users\\nagap\\Desktop\\log.csv"));
			// setting the properties to run in chrome driver
			System.setProperty("webdriver.chrome.driver", ".//drivers//chromedriver.exe");
			DesiredCapabilities cap = DesiredCapabilities.chrome();
			LoggingPreferences logPrefs = new LoggingPreferences();
			logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
			cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
			// Opening browser
			driver = new ChromeDriver(cap);
			driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS);
			// maximizing the browser for better view
			driver.manage().window().maximize();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testLog() {

		try {
			// task1
			// navigating to url
			driver.get("https://www.google.com");
			WebElement gs = driver.findElement(By.id("lst-ib"));
			// waiting for element to load
			WebDriverWait wait = new WebDriverWait(driver, 10);
			wait.until(ExpectedConditions.visibilityOf(gs));
			// entering data
			gs.sendKeys("Datalicious" + Keys.ENTER);
			List<WebElement> resultSearch = driver.findElements(By.xpath("//div[@class='srg']//h3[@class='r']/a"));
			// to click on first link
			wait.until(ExpectedConditions.elementToBeClickable(resultSearch.get(0)));
			resultSearch.get(0).click();
			// to wait still the page is loaded
			// task2
			Thread.sleep(5000);
			LogEntries logs = driver.manage().logs().get("performance");
			Iterator<LogEntry> log = logs.iterator();
			// Iterating one after another log & verifying google-analytics &
			// optimahub is present or not
			while (log.hasNext()) {
				LogEntry logName = log.next();
				JSONObject name = new JSONObject(logName.getMessage());
				// to get the current log
				String cl1 = name.toString();
				// checking if google analytics & optimahub is present or not
				if (cl1.contains("https://www.google-analytics.com") || cl1.contains("dc.optimahub.com")) {
					if (cl1.contains("https://www.google-analytics.com")) {
						// if google analytics is present making it true
						ga = true;
						// identifying the google-analytics parameters using
						// JSON
						JSONObject message = name.getJSONObject("message");
						String method = message.getString("method");
						if (method != null && "Network.requestWillBeSent".equals(method)) {
							// getting the parameters
							JSONObject params = message.getJSONObject("params");
							// splitting it as URL
							String[] splits = params.toString().split("url");
							for (String spli : splits) {
								String[] spParams = spli.split("&");
								for (String spParam : spParams) {
									if (spParam.contains("dp") || spParam.contains("dt")) {
										if (spParam.contains("dp")) {

											dp = true;
											dP = spParam.substring(spParam.indexOf("=") + 1);
											// storing the value of parameter dp
										}

										if (spParam.contains("dt")) {
											// if optimahub is present setting it as true
											 
											dt = true;
											dT = spParam.substring(spParam.indexOf("=") + 1);
										}
									}
								}
							}
						}
					}
					if (cl1.contains("dc.optimahub.com")) {
						oh = true;
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("google-analytics is present " + ga + "\n" + "optima hub is present " + oh);

	}
	
	public void isParamPresent(boolean param,String paramName,String paramValue)
	{
		if (param) {

			paramValue = paramValue.replace("%20", " ");
			paramValue = paramValue.replace("%7C", "|");
			System.out.println("Value of "+paramName+" is: " + paramValue);
			String[] dPS = paramValue.split(" ");
			writeLog.writeNext(dPS);
		} else {
			System.out.println(paramName +"value not present ");

		}
		
	}

	@AfterMethod
	public void postAction() {
		try {
			String dpParam="dp";
			isParamPresent(dp, dpParam, dP);
			
			String dtParam="dt";
			isParamPresent(dt, dtParam, dT);

			writeLog.close();
		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			driver.close();
		}
	}

}
