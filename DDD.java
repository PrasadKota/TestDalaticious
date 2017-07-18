package com.nexus.scripts;

import static io.restassured.RestAssured.given;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;

import com.opencsv.CSVWriter;

import io.restassured.RestAssured;

public class DDD {
	
@Test
public void ddd() throws Exception
{
	System.setProperty("webdriver.gecko.driver",".\\drivers\\geckodriver.exe");
	WebDriver driver= new FirefoxDriver();
	driver.get("https://www.google.com");
	driver.manage().timeouts().implicitlyWait(25, TimeUnit.SECONDS);
	driver.manage().window().maximize();
	//task1
	WebElement gs = driver.findElement(By.id("lst-ib"));
	WebDriverWait wait = new WebDriverWait(driver, 10);
	wait.until(ExpectedConditions.visibilityOf(gs));
	
	gs.sendKeys("Datalicious");
	List<WebElement> resultSearch = driver.findElements(By.xpath("//div[@class='srg']//h3[@class='r']/a"));
	//to click on first link
	wait.until(ExpectedConditions.elementToBeClickable(resultSearch.get(0)));
	resultSearch.get(0).click();
	
	//task2
	
	
	
	RestAssured.baseURI="https://www.google-analytics.com";
	 given().param("v", "1").param("_v", "j56")
	.param("a", "1285449106").param("t", "pageview")
	.param("_s", "1").param("dl", "https://www.datalicious.com/").
	param("dr", "https://www.google.co.in/").param("ul", "en-us").
	param("de", "UTF-8").param("dt", "Marketing Data Specialists | Datalicious").
	param("sd", "24-bit").param("sr", "1366x768").param("vp", "1349x317").
	param("je", "0").param("_u", "yCCAAEADY~").param("jid", "715927920").
	param("gjid", "1815754245").param("cid", "1519353464.1500350820").
	param("tid", "UA-6757849-1").param("_gid", "1674571857.1500350820").
	param("_r", "1").param("cd2", "").param("cd3", "IN").param("cd4","ka").
	param("cd5", "bangalore").param("cd6", "not-detected").param("cd1", "").
	param("cd7", "").param("cd8", "2276763742").param("z", "780714622").log().all().
	when().get("/r/collect").then().statusCode(200);
	
	
	
	System.out.println("-------------");
	
	RestAssured.baseURI="https://dc.optimahub.com/";
	given().then().statusCode(300).log().all();
	
	
	driver.close();
}

}
