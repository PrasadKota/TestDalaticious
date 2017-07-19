package com.nexus.scripts;

import java.io.FileWriter;
import java.io.IOException;
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
import org.testng.annotations.Test;

import com.opencsv.CSVWriter;

public class TestDataliciousLogs {

	@Test
	public void testLog() {
		boolean dp = false, dt = false,ga=false,oh=false;
		String dP = "", dT = "";
		ChromeDriver driver = null;
		CSVWriter writeLog=null;
		try {
			writeLog=new CSVWriter(new FileWriter("C:\\Users\\nagap\\Desktop\\log.csv"));
			// setting the properties to run in chrome driver
			System.setProperty("webdriver.chrome.driver", ".//drivers//chromedriver.exe");
			DesiredCapabilities cap = DesiredCapabilities.chrome();
			LoggingPreferences logPrefs = new LoggingPreferences();
			logPrefs.enable(LogType.PERFORMANCE, Level.ALL);
			cap.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
			// Opening browser
			driver = new ChromeDriver(cap);
			// task1
			driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS);
			// maximizing the browser for better view
			driver.manage().window().maximize();
			driver.get("https://www.google.com");// navigating to url
			WebElement gs = driver.findElement(By.id("lst-ib"));
			WebDriverWait wait = new WebDriverWait(driver, 10);// waiting for
																// element to
																// load
			wait.until(ExpectedConditions.visibilityOf(gs));
			gs.sendKeys("Datalicious"+Keys.ENTER);// entering data
			List<WebElement> resultSearch = driver.findElements(By.xpath("//div[@class='srg']//h3[@class='r']/a"));
			// to click on first link
			wait.until(ExpectedConditions.elementToBeClickable(resultSearch.get(0)));
			resultSearch.get(0).click();
			// to wait still the page is loaded
			// task2
			Thread.sleep(5000);
			LogEntries logs = driver.manage().logs().get("performance");

			Iterator<LogEntry> log = logs.iterator();

			while (log.hasNext()) {
				LogEntry logName = log.next();
				JSONObject name = new JSONObject(logName.getMessage());
				// to get the current log
				String cl1 = name.toString();
				//checking if google analytics & optimahub is present or not
				if (cl1.contains("https://www.google-analytics.com") || cl1.contains("dc.optimahub.com")) {
					
					if (cl1.contains("https://www.google-analytics.com")) {
						//if google analytics is present making it true
						ga=true;
						//identifying the google-analytics parameters using JSON
						JSONObject message = name.getJSONObject("message");
						String method = message.getString("method");
						if (method != null && "Network.requestWillBeSent".equals(method)) {
							JSONObject params = message.getJSONObject("params");//getting the parameters
							String[] splits = params.toString().split("url");//splitting it as URL
							for (String spli : splits) {
								if (spli.contains("https://www.google-analytics.com")) {
									String[] spParams = spli.split("&");
									for (String spParam : spParams) {
										if (spParam.contains("dp") || spParam.contains("dt")) {
											if (spParam.contains("dp")) {

												dp = true;
												dP = spParam.substring(3);//storing the value of parameter dp
											}

											if (spParam.contains("dt")) {
												//if optimahub is present making true
												dt = true;
												dT = spParam.substring(3);
											}
										}
									}

								}
							}

						}
					}
					if(cl1.contains("dc.optimahub.com"))
					{
						oh=true;
					}
				}

			}
			driver.close();
		}

		catch (Exception e) {
			e.printStackTrace();
			driver.close();
		}
		System.out.println("google-analytics is present "+ga+"\n"+"optima hub is present "+oh);
		if (dp) {
			
			dP=dP.replace("%20", " ");
			System.out.println("Value of dp is: " + dP);
			String[] dPS = dP.split(" ");
			writeLog.writeNext(dPS);
		} else {
			System.out.println("dp value not present" + dP);
			
		}

		if (dt) {
			
			dT=dT.replace("%20", " ");
			System.out.println("value of dt is:" + dT);
			String[] dTS = dT.split(" ");
			writeLog.writeNext(dTS);
		} else {
			System.out.println("dt value not present" + dT);
		}
		try {
			writeLog.close();
		} catch (IOException e) {
			
			e.printStackTrace();
		}

	}

}
