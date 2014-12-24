package com.virtusa.childagent.runtime;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.Wait;

import com.google.common.base.Function;
import com.virtusa.isq.vtaf.report.model.TestCase;
import com.virtusa.isq.vtaf.report.model.TestExecution;
import com.virtusa.isq.vtaf.report.model.TestStep;
import com.virtusa.isq.vtaf.report.reporter.Reporter;


public class SeleniumTestBase {

	WebDriver driver;
	Reporter resultReporter;

	public SeleniumTestBase() {
		driver = new FirefoxDriver();
		resultReporter = new Reporter();
		resultReporter.addNewTestExecution();
		resultReporter.addNewTestSuite("test suite1");
		resultReporter.addNewTestCase("test case1");
	}

	public void endTestCase() {
		resultReporter.endTestReporting();
		driver.quit();
	}
	
	public JSONObject createReportJO(){
		TestCase testCase = resultReporter.getTestCase();
		ArrayList<TestStep> testStepArr  = testCase.getTestSteps();
		TestExecution testExecution = resultReporter.getTextExecution();
		
		String timeTaken = testCase.getDuration();
		String result = testCase.getResult();
		String screenResolution = testExecution.getScreenresolution();
		String startTime = testExecution.getTimestamp();
		
		JSONObject mainJO = new JSONObject();
		JSONArray stepsJA = new JSONArray();
		JSONObject stepJO = new JSONObject();
		
		byte[] byteArr = null;
		BufferedImage img = null;
		
		for (TestStep a : testStepArr) {
			if(a.isPassed()){
				try {
					stepJO.put("Status", a.isPassed());
					stepJO.put("Time_Taken", a.getTime());
					stepJO.put("Log", a.getLoglvl());
					stepJO.put("Stack_trace", a.getStacktrace());
					stepJO.put("Message", a.getMessage());
					stepJO.put("Command", a.getCategory());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}else{
				try {
					img = ImageIO.read(new File(resultReporter.getFodlerLocation() +"\\" + a.getErrimg()));
					
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write( img, "png", baos );
					baos.flush();
					byteArr = baos.toByteArray();
					baos.close();
					
					stepJO.put("Status", a.isPassed());
					stepJO.put("Time_Taken", a.getTime());
					stepJO.put("Log", a.getLoglvl());
					stepJO.put("Stack_trace", a.getStacktrace());
					stepJO.put("Message", a.getMessage());
					stepJO.put("Command", a.getCategory());
					stepJO.put("Screenshot", byteArr); 
				}catch (JSONException e) {
					e.printStackTrace();
				}catch (IOException e) {
					e.printStackTrace();
				}
			}
			stepsJA.put(stepJO);
			stepJO = new JSONObject();
		}
		
		try {
			mainJO.put("Time_Taken", timeTaken);
			mainJO.put("Result", result);
			mainJO.put("Screen_Resolution", screenResolution);
			mainJO.put("Start_Time", startTime);
			mainJO.put("Steps", stepsJA);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return mainJO;
	}

	//=============OPEN=============
	public final void open(final String url, final String objectName, final String waitTime) {
		open(url, objectName, "", waitTime);
	}
	
	public final void open(final String url, final String objectName, final String identifier, final String waitTime) {
		ObjectLocator locator = new ObjectLocator(objectName, identifier, url);
		doOpen(locator, waitTime);
	} 
	
	private void doOpen(final ObjectLocator locator, final String waitTime) {
		String url = "";
		WebDriver driver = getDriver();
		try {
			url = locator.getActualLocator();
			if ("default".equalsIgnoreCase(url)) {
				PropertyHandler propertyHandler = new PropertyHandler("runtime.properties");
				url = propertyHandler.getRuntimeProperty("DEFAULT_URL");
				if ("".equals(url)) {
					throw new WebDriverException("Empty URL : " + url);
				}
			}
//			setCommandStartTime(getCurrentTime());
			driver.get(url);

			try {
				driver.manage()
						.timeouts()
						.implicitlyWait(Integer.parseInt(waitTime),TimeUnit.MILLISECONDS);
			} catch (NumberFormatException e) {
				reportresult(true, "OPEN :", "FAILED", "Invalid argument passed for wait time.");
			} catch (Exception e) {
				e.printStackTrace();
			}

			reportresult(true, "OPEN : " + url + "", "PASSED", url);
		} catch (WebDriverException e) {
			String errorString = e.getMessage();
			reportresult(true, "OPEN : " + url + "", "FAILED", "Cannot access the empty URL. URL : " + url + ". Actual Error : " + errorString);
		} catch (Exception e) {
			String errorString = e.getMessage();
			reportresult(true, "OPEN : " + url + "", "FAILED", "Cannot access the URL. URL : " + url + ". Actual Error : " + errorString);
			}
		}

	//================CLICK==================
	public final void click(final String objectName, final String actualObject, final int counter) {
		click(objectName, "", actualObject, counter);
	}
	
	public final void click(final String objectName, final String identifier, final String actualObject, final int counter){
		ObjectLocator locator = new ObjectLocator(objectName, identifier, actualObject);
		doClick(locator, counter);
	}
	
	private void doClick(final ObjectLocator locator, int counter) {
		String objectID = "";
//		int counter = 12;
		WebDriver driver = getDriver();
		try {
			// Retrieve the correct object locator from the object map
			objectID = locator.getActualLocator();
			// first verify whether the element is present in the current web
			// page
//			checkForNewWindowPopups();
			WebElement element = checkElementPresence(objectID);
			/*
			 * START DESCRIPTION following while loop was added to make the
			 * command more consistent try the command for give amount of time
			 * (can be configured through class variable RETRY) command will be
			 * tried for "RETRY" amount of times untill command works. any
			 * exception thrown within the tries will be handled internally.
			 * 
			 * can be exitted from the loop under 2 conditions 1. if the command
			 * succeeded 2. if the RETRY count is exceeded
			 */
			while (counter > 0) {
				try {
					counter--;
					// call for real selenium command
					/* selenium.click(objectID); */

					element.click();
					// if not exception is called consider and report the result
					// as passed
					reportresult(true, "CLICK :" + locator.getObjectName()
							+ "", "PASSED", "");
					// if the test case passed move out from the loop
					break;
				} catch (StaleElementReferenceException staleElementException) {

					element = checkElementPresence(objectID);
				} catch (ElementNotVisibleException ex) {
					try {
						JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
						jsExecutor.executeScript("arguments[0].click();",
								element);
						reportresult(true, "CLICK :" + locator.getObjectName()
								+ "", "PASSED", "");
						break;
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}

				} catch (Exception e) {
					String objectName = locator.getObjectName();
					if (!(counter > 0)) {
						e.printStackTrace();
						reportresult(true, "CLICK :" + objectName + "", "FAILED", "CLICK command cannot access Element ("	+ objectName + ") [" + objectID + "] ");
					}
				}
			}
			/*
			 * END DESCRIPTION
			 */
		} catch (Exception e) {

			e.printStackTrace();
			String objectName = locator.getObjectName();
			/*
			 * VTAF result reporter call
			 */
			reportresult(true, "CLICK :" + objectName + "", "FAILED",
					"CLICK command  :Element (" + objectName + ") [" + objectID
							+ "] not present");
		}
	}
	
	//===================SELETWINDOW=========================
//	public final void selectWindow(final String windowName, final String actualObject,int counter, int retryInterval) {
//		selectWindow(windowName, "", actualObject, counter, retryInterval);
//	}
//
//	public final void selectWindow(final String windowName, final String identifier, final String actualObject,int counter, int retryInterval) {
//		ObjectLocator locator = new ObjectLocator(windowName, identifier, actualObject);
//		doSelectWindow(locator, counter, retryInterval);
//	}
//	
//	private void doSelectWindow(final ObjectLocator locator, int counter, int retryInterval) {
////		int counter = getRetryCount();
//		String targetWindow = null;
//		WebDriver driver = getDriver();
//
//		// Getting the actual object identification from the object map
//		String window = locator.getActualLocator();
//		try {
////			checkForNewWindowPopups();
//
//			/*
//			 * START DESCRIPTION following for loop was added to make the
//			 * command more consistent try the command for give amount of time
//			 * (can be configured through class variable RETRY) command will be
//			 * tried for "RETRY" amount of times or untill command works. any
//			 * exception thrown within the tries will be handled internally.
//			 * 
//			 * can be exited from the loop under 2 conditions 1. if the command
//			 * succeeded 2. if the RETRY count is exceeded
//			 */
//			while (counter > 0) {
//				try {
//					counter--;
//
//					targetWindow = getMatchingWindowFromCurrentWindowHandles(
//							driver, window);
//
//					if (targetWindow != null) {
//
//						driver.switchTo().window(targetWindow);
//
//						driver.manage().window().maximize();
//						focusToCurrentWindow(driver);
//						reportresult(true,
//								"SELECT WINDOW :" + locator.getObjectName()
//										+ "", "PASSED", "");
//						break;
//					} else {
//						throw new WebDriverException("Window Not Found"
//								+ window);
//					}
//				} catch (WebDriverException ex) {
//					sleep(retryInterval);
//					if (!(counter > 0)) {
//						String errorString = ex.getMessage();
//						String objectName = locator.getObjectName();
//						reportresult(
//								true,
//								"SELECT WINDOW :" + objectName + "",
//								"FAILED",
//								"selectWindow command  :Element ("
//										+ objectName
//										+ ") ["
//										+ window
//										+ "] is not accessible. Actual Error : "
//										+ errorString);
//					}
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			String objectName = locator.getObjectName();
//			// if any exception is raised, report failure
//			reportresult(true, "SELECT WINDOW :" + objectName + "", "FAILED",
//					"selectWindow command  :Element (" + objectName + ") ["
//							+ window + "] not present");
//		}
//
//	}
//	
//	private String getMatchingWindowFromCurrentWindowHandles(
//			final WebDriver driver, final String inputWindowName)
//			throws Exception {
//		String targetWindow = null;
//		Set<String> windowarr = getAllWindows();
//		if (inputWindowName.startsWith("index=")) {
//			int winIndex = Integer
//					.parseInt(inputWindowName.substring(
//							inputWindowName.indexOf('=') + 1,
//							inputWindowName.length()));
//			targetWindow = getOpenWindowHandleIndex().get(winIndex);
//
//		} else {
//			boolean objectFound = false;
//			for (String windowname : windowarr) {
//
//				if (inputWindowName.startsWith("regexp:")
//						|| inputWindowName.startsWith("glob:")) {
//
//					objectFound = isMatchingPattern(inputWindowName.substring(
//							inputWindowName.indexOf(':') + 1,
//							inputWindowName.length()), driver.switchTo()
//							.window(windowname).getTitle());
//
//				} else if (driver.switchTo().window(windowname).getTitle()
//						.equals(inputWindowName)) {
//					objectFound = true;
//				}
//				if (objectFound) {
//					targetWindow = windowname;
//					break;
//				}
//			}
//		}
//		return targetWindow;
//	}
//	
//	private boolean isMatchingPattern(final String patternString,
//			final String matcherString) {
//
//		Pattern pattern = Pattern.compile(patternString);
//		Matcher matcher = pattern.matcher(matcherString);
//		return matcher.matches();
//	}
//	
//	private void focusToCurrentWindow(final WebDriver driver) {
//
//		try {
//			JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
//			if (driver.getPageSource().toLowerCase(Locale.getDefault())
//					.contains("<html")) {
//				jsExecutor.executeScript("window.focus();");
//			}
//		} catch (Exception e) {
//			getLog().error(e);
//
//		}
//	}
	
	//===================SELECT==============================
//	public final void select(final String actualObject, final String objectName, final Object objValue, int counter, int retryInterval) {
//		select(actualObject, objectName, "", objValue, counter, retryInterval);
//	}
//	
//	public final void select(final String actualObject,final String objectName, final String identifier, final Object objValue, int counter, int retryInterval) {
//		ObjectLocator locator = new ObjectLocator(objectName, identifier,actualObject);
//		doSelect(locator, objValue, counter, retryInterval);
//	}
//	
//	private void doSelect(final ObjectLocator locator, final Object objValue, int counter, int retryInterval) {
//
//		String value = checkNullObject(objValue, "SELECT");
////		int counter = getRetryCount();
//		String[] actualOptions = {};
//		boolean multiSelect = false;
//		String objectName = locator.getObjectName();
//
//		String objectID = locator.getActualLocator();
//		try {
//			// Checking whether the list box is available
////			checkForNewWindowPopups();
//			WebElement element = checkElementPresence(objectID);
//			// Checking whether the list option is available
//			Select selectElement = new Select(element);
//
//			List<WebElement> actualElementOptions = selectElement.getOptions();
//
//			actualOptions = new String[actualElementOptions.size()];
//			for (int i = 0; i < actualElementOptions.size(); i++) {
//				actualOptions[i] = actualElementOptions.get(i).getText();
//			}
//			multiSelect = checkSelectOptions(value, actualOptions);
//
//			/*
//			 * START DESCRIPTION following for loop was added to make the
//			 * command more consistent try the command for give amount of time
//			 * (can be configured through class variable RETRY) command will be
//			 * tried for "RETRY" amount of times or until command works. any
//			 * exception thrown within the tries will be handled internally.
//			 * 
//			 * can be exited from the loop under 2 conditions 1. if the command
//			 * succeeded 2. if the RETRY count is exceeded
//			 */
//
//			while (counter > 0) {
//				try {
//					counter--;
//
//					if (!multiSelect) {
//
//						selectSingleOptionFromActualDropdown(selectElement,
//								actualOptions, value);
//
//						/*
//						 * if (!checkSelectedOptionValue(objectID, value)) {
//						 * continue; }
//						 */
//
//						reportresult(true, "SELECT :" + objectName + "",
//								"PASSED", "");
//						break;
//					} else {
//
//						selectMultipleOptionFromActualDropDown(selectElement,
//								value);
//						reportresult(true, "SELECT :" + objectName + "",
//								"PASSED", "");
//					}
//
//				} catch (StaleElementReferenceException staleElementException) {
//
//					element = checkElementPresence(objectID);
//					selectElement = new Select(element);
//				} catch (Exception ex) {
//					sleep(retryInterval);
//
//					if (!(counter > 0)) {
//						ex.printStackTrace();
//
//						reportresult(true, "SELECT :" + objectName + "",
//								"FAILED",
//								"SELECT command cannot access :Element ("
//										+ objectName + ") [" + objectID + "] ");
//					}
//				}
//			}
//			/*
//			 * END DESCRIPTION
//			 */
//		} catch (Exception e) {
//			// waiting for the maximum amount of waiting time before failing the
//			// test case
//			// Several checks were introduced to narrow down to the failure to
//			// the exact cause.
//			ErrorMessageHandler messages = new ErrorMessageHandler();
//			String error = messages.getSelectCommandErrorMessages(e
//					.getMessage());
//			String formattedError = error
//					.replaceAll("<locator>", objectName)
//					.replaceAll("<objectID>", objectID)
//					.replaceAll("<inputValue>", objValue.toString())
//					.replaceAll("<actualOptions>",
//							Arrays.asList(actualOptions).toString());
//
//			e.printStackTrace();
//			reportresult(true, "SELECT :" + objectName + "", "FAILED",
//					formattedError);
//		}
//	}
//	
//	private boolean checkSelectOptions(final String valuetoBeSelect,final String[] actualOptions) throws Exception {
//		String[] optionList = valuetoBeSelect.split("#");
//		boolean optionPresent = false;
//		boolean multiSelect = false;
//		int notFoundCount = 0;
//		StringBuilder notFoundItems = new StringBuilder();
//
//		multiSelect = valuetoBeSelect.contains("#");
//
//		for (String option : optionList) {
//			optionPresent = false;
//			if (option.startsWith("regexp:")) {
//				for (String actualOption : actualOptions) {
//					optionPresent = isMatchingPattern(
//							option.substring(option.indexOf(':') + 1,
//									option.length()), actualOption);
//					if (optionPresent) {
//						break;
//					}
//				}
//				if (!optionPresent) {
//					notFoundCount++;
//					notFoundItems.append(option).append(",");
//				}
//
//			} else if (option.startsWith("index=")) {
//				int indexNo = Integer.parseInt(option.replace("index=", ""));
//				if (actualOptions.length <= indexNo) {
//					notFoundCount++;
//					notFoundItems.append(option).append(",");
//				}
//			} else {
//				optionPresent = Arrays.asList(actualOptions).contains(option);
//				if (!optionPresent) {
//					notFoundCount++;
//					notFoundItems.append(option).append(",");
//				}
//			}
//		}
//		if (notFoundCount > 0) {
//			setErrorMessages(getErrorMessages() + " Options cannot be found |"
//					+ notFoundItems.toString());
//
//			throw new Exception("No_Item " + notFoundItems);
//		}
//
//		return multiSelect;
//
//	}
//	
//	private String selectSingleOptionFromActualDropdown(
//			final Select selectElement, final String[] actualOptions,
//			final String value) {
//		String selectedValue = "";
//		if (value.startsWith("regexp:")) {
//
//			Pattern pattern = Pattern.compile(value.substring(
//					value.indexOf(':') + 1, value.length()));
//			for (String actualOption : actualOptions) {
//				Matcher matcher = pattern.matcher(actualOption);
//				if (matcher.matches()) {
//					selectedValue = actualOption;
//					selectElement.selectByVisibleText(actualOption);
//					break;
//				}
//			}
//
//		} else if (value.startsWith("index=")) {
//
//			int indexNo = Integer.parseInt(value.replace("index=", ""));
//
//			selectElement.selectByIndex(indexNo);
//
//		} else {
//			selectedValue = value;
//			selectElement.selectByVisibleText(value);
//		}
//		return selectedValue;
//	}
//	
//	private void selectMultipleOptionFromActualDropDown(
//			final Select selectElement, final String value) {
//
//		String[] options = value.split("#");
//		for (String option : options) {
//			if (option.startsWith("index=")) {
//
//				int indexNo = Integer.parseInt(option.replace("index=", ""));
//				selectElement.selectByIndex(indexNo);
//
//			} else {
//				selectElement.selectByVisibleText(option);
//			}
//			break;
//		}
//	}
	
	//===================CHECKELEMENTPRESENT=================
	public final void checkElementPresent(final String objectName, final boolean stopExecution, final String actualObject, final Object... customError) {
		checkElementPresent(objectName, "", actualObject, stopExecution, customError);
	}
	
	public final void checkElementPresent(final String objectName, final String identifier,final String actualObject, final boolean stopExecution, final Object... customError) {

		ObjectLocator locator = new ObjectLocator(objectName, identifier, actualObject);
		doCheckElementPresent(locator, stopExecution, customError);
	}
	
	private void doCheckElementPresent(final ObjectLocator locator,
			final boolean stopExecution, final Object[] customError) {
		// Retrieve the actual object id from the OR
		String objectID = locator.getActualLocator();
		String objectName = locator.getObjectName();
		try {
			// Check whether the element is present, the validation
//			checkForNewWindowPopups();
			checkElementPresence(objectID);

			// if reached this point, test case should be passed
			reportresult(true, "CHECK ELEMENT PRESENT :" + objectName + "",
					"PASSED", "");
		} catch (Exception e) {
			// if any exception was thrown the faliure should be reported
			// but the continuation will be decided by stpoExecution
			e.printStackTrace();

			reportresult(
					stopExecution,
					"CHECK ELEMENT PRESENT :" + objectName + "",
					"FAILED",
					""
							+ generateCustomError(customError)
							+ " System generated Error : command checkElementPresent()  :Element ("
							+ objectName + ") [" + objectID + "] not present");

		}
	}
	
	private String generateCustomError(final Object[] customError) {
		String customErrorMessage = "";

		if (customError != null
				&& !(customError[0].equals("null") || customError[0].equals(""))) {

			/* if (customError != null && customError.length > 0) { */

			/*
			 * for (int i = 0; i < customError.length; i++) { customErrorMessage
			 * = customErrorMessage + customError[i].toString() + ". "; }
			 */
			customErrorMessage = " Custom Error : " + customError[0].toString()
					+ ". ";
		}
		return customErrorMessage;
	}
	
	//===================DOUBLECLICK=============
	public final void doubleClick(final String actualObject, final String objectName, int counter, int retryInterval) {
		doubleClick(actualObject, objectName, "", counter, retryInterval);
	}
	
	public final void doubleClick(final String actualObject, final String objectName, final String identifier, int counter, int retryInterval) {
		ObjectLocator locator = new ObjectLocator(objectName, identifier, actualObject);
		doDoubleClick(locator, counter, retryInterval);
	}
	
	private void doDoubleClick(final ObjectLocator locator, int counter, int retryInterval) {
		// Retrieve the actual object name from the object repository

//		int counter = getRetryCount();
		WebDriver driver = getDriver();
		String objectName = locator.getObjectName();
		String objectID = locator.getActualLocator();
		try {
			// First checking whether the element is present
//			checkForNewWindowPopups();
			WebElement element = checkElementPresence(objectID);

			/*
			 * START DESCRIPTION following for loop was added to make the
			 * command more consistent try the command for give amount of time
			 * (can be configured through class variable RETRY) command will be
			 * tried for "RETRY" amount of times or until command works. any
			 * exception thrown within the tries will be handled internally.
			 * 
			 * can be exited from the loop under 2 conditions 1. if the command
			 * succeeded 2. if the RETRY count is exceeded
			 */

			while (counter > 0) {
				try {
					counter--;
					element.click();
					Actions dClick = new Actions(driver);
					dClick.moveToElement(element).doubleClick();
					dClick.build().perform();
					/* selenium.doubleClick(objectID); */
					reportresult(true, "DOUBLE CLICK :" + objectName + "",
							"PASSED", "");
					break;
				} catch (StaleElementReferenceException staleElementException) {

					element = checkElementPresence(objectID);
				} catch (Exception e) {

					sleep(retryInterval);
					if (!(counter > 0)) {

						e.printStackTrace();
						reportresult(true, "DOUBLE CLICK :" + objectName + "",
								"FAILED",
								"DOUBLE CLICK command cannot access Element ("
										+ objectName + ") [" + objectID + "] ");
					}
				}
			}
			/*
			 * END DESCRIPTION
			 */
		} catch (Exception e) {
			// if object not found exception is raised fail the test cases
			e.printStackTrace();
			reportresult(true, "DOUBLE CLICK :" + objectName + "", "FAILED",
					"DOUBLE CLICK command  :Element (" + objectName + ") ["
							+ objectID + "] not present");
		}

	}
	
	//===================TYPE====================
	public final void type(final String objectName,final String actualLocator, final Object value, final int counter) {
		type(objectName, actualLocator, "", value, counter);
	}
	
	public final void type(final String objectName, String actualLocator, final String identifier, final Object value, final int counter) {
		ObjectLocator locator = new ObjectLocator(objectName, identifier, actualLocator);
		doType(locator, value, counter);
	}

	public final void doType(final ObjectLocator locator, final Object objValue, int counter) {
		String value = checkNullObject(objValue, "TYPE");

//		int counter = 12;

		// Getting the actual object identification from the object map

		String objectID = locator.getActualLocator();
		try {
			// Check whether the element present
			WebElement element = checkElementPresence(objectID);
			try {
				element.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}

			while (counter > 0) {
				try {
					counter--;
					// Calling the actual command

					element.sendKeys(value);

					reportresult(true, "TYPE :" + locator.getObjectName() + "", "PASSED", " [Input value = " + value + "]");
					break;
				} catch (StaleElementReferenceException staleElementException) {
					element = checkElementPresence(objectID);
				} catch (Exception e) {
					if (!(counter > 0)) {
						e.printStackTrace();
						String objectName = locator.getObjectName();
						reportresult(true, "TYPE :" + objectName + "",
								"FAILED",
								"TYPE command cannot access :Element ("
										+ objectName + ") [" + objectID + "]"
										+ " [Input value = " + value + "]");
					}
				}
			}
			/*
			 *  END DESCRIPTION
			 */
		} catch (Exception e) {
			// if any exception was raised, report a test failure
			e.printStackTrace();
			String objectName = locator.getObjectName();
			reportresult(true, "TYPE :" + objectName + "", "FAILED", "TYPE command  :Element (" + objectName + ") [" + objectID + "] [Input value = " + value + "] not present");
		}
	}
	
	//===============FAIL=============================
//	public final void fail(final Object message) {
//		reportresult(true, "Fail Command : ", "FAILED", " command Fail("
//				+ message + ")");
//		SeleneseTestBaseVir.failure(message);
//	}
	
	//===============DOUBLECLICKAT====================
	public final void doubleClickAt(final String actualObject,final String objectName,final String coordinateString,int counter, int retryInterval) {
		doubleClickAt(actualObject, objectName, "", coordinateString, counter, retryInterval);
	}
	
	public final void doubleClickAt(final String actualObject, final String objectName, final String identifier, final String coordinateString, int counter, int retryInterval) {
		ObjectLocator locator = new ObjectLocator(objectName, identifier, actualObject);
		doDoubleClickAt(locator, coordinateString, counter, retryInterval);
	}
	
	private void doDoubleClickAt(final ObjectLocator locator, final String coordinateString,int counter, int retryInterval) {
//		int counter = getRetryCount();
		int xOffset = 0;
		int yOffset = 0;
		WebDriver driver = getDriver();
		// Retrieve the actual object identification from the OR
		String objectID = locator.getActualLocator();
		String objectName = locator.getObjectName();
		
		try {

			// Precheck done to check whether the element is available if
			// element is not
			// present, code will move to the catch block and report an error
//			checkForNewWindowPopups();
			WebElement element = checkElementPresence(objectID);

			try {
				xOffset = Integer.parseInt((coordinateString.split(",")[0])
						.trim());
				yOffset = Integer.parseInt((coordinateString.split(",")[1])
						.trim());
			} catch (Exception e) {
				e.printStackTrace();
				reportresult(true, "DOUBLE CLICK AT :" + objectName + "",
						"FAILED", "DOUBLE CLICK AT coordinate string ("
								+ coordinateString + ") for :Element ("
								+ objectName + ") [" + objectID
								+ "] is invalid");
			}

			/*
			 * START DESCRIPTION following for loop was added to make the
			 * command more consistent try the command for give amount of time
			 * (can be configured through class variable RETRY) command will be
			 * tried for "RETRY" amount of times or until command works. any
			 * exception thrown within the tries will be handled internally.
			 * 
			 * can be exited from the loop under 2 conditions 1. if the command
			 * succeeded 2. if the RETRY count is exceeded
			 */
			while (counter > 0) {
				counter--;
				try {

					Actions doubleClickAt = new Actions(driver);
					doubleClickAt.moveToElement(element, xOffset, yOffset)
							.doubleClick();
					doubleClickAt.build().perform();

					reportresult(true, "DOUBLE CLICK AT :" + objectName + "",
							"PASSED", "");
					break;
				} catch (StaleElementReferenceException staleElementException) {

					element = checkElementPresence(objectID);
				} catch (Exception e) {
					sleep(retryInterval);
					// The main possibility of throwing exception at this point
					// should be due to the element was not
					// fully loaded, in this catch block handle the exception
					// untill retry amount of attempts
					if (!(counter > 0)) {
						e.printStackTrace();
						reportresult(true, "DOUBLE CLICK AT :" + objectName
								+ "", "FAILED",
								"DOUBLE CLICK AT command  :Element ("
										+ objectName + ") [" + objectID
										+ "] not present");
					}
				}
			}

			/*
			 * END DESCRIPTION
			 */

		} catch (Exception e) {
			// if any exception was raised report report an error and fail the
			// test case
			e.printStackTrace();
			reportresult(true, "DOUBLE CLICK AT :" + objectName + "", "FAILED",
					"DOUBLE CLICK AT command  :Element (" + objectName + ") ["
							+ objectID + "] not present");
		}

	}
	
	//===============NAVIGATETOURL====================
	public final void navigateToURL(final String url, final String waitTime) {
		navigateToURL(url, "", waitTime);
	}
	
	public final void navigateToURL(final String url, final String identifier, final String waitTime) {
		ObjectLocator locator = new ObjectLocator(url, identifier, url);
		
		doNavigateToURL(locator, waitTime);
	}
	
	private void doNavigateToURL(final ObjectLocator locator, final String waitTime) {
		String url = "";
		WebDriver driver = getDriver();
		try {

			url = locator.getActualLocator();
//			setCommandStartTime(getCurrentTime());
			if (url.toLowerCase(Locale.getDefault()).startsWith("openwindow=")) {

				Set<String> oldWindowHandles = getAllWindows();
				String actualUrl = url.substring(url.indexOf('=') + 1,
						url.length());

				JavascriptExecutor js = (JavascriptExecutor) driver;
				js.executeScript("window.open('" + actualUrl
						+ "', '_newWindow');");
				sleep(Integer.parseInt(waitTime));

				Set<String> newWindowHandles = getAllWindows();
				newWindowHandles.removeAll(oldWindowHandles);
				Object[] newWindowArr = newWindowHandles.toArray();
				driver.switchTo().window(newWindowArr[0].toString());

			} else {
				driver.get(url);
				sleep(Integer.parseInt(waitTime));
			}
			reportresult(true, "NAVIGATE TO URL Command :" + url + "",
					"PASSED", url);
		} catch (Exception e) {
			String errorString = e.getMessage();
			reportresult(true, "NAVIGATE TO URL :" + url + "", "FAILED",
					"NAVIGATE TO URL command : URL " + url
							+ " failed. Actual Error : " + errorString);
		}

	}
	
	public final Set<String> getAllWindows() throws Exception {

		WebDriver driver = getDriver();
		try {
			// allData = new String[ (availableWindows.size() )];
			return driver.getWindowHandles();

		} catch (Exception e) {
			throw new Exception("cannot access the windows ", e);
		}
	}
	
	//===============CLICKAT=================
	public final void clickAt(final String actualObject, final String objectName, final String coordinateString, final int counter, int retryInterval) {
		clickAt(actualObject,objectName, "", coordinateString, counter, retryInterval);
	}
	
	public final void clickAt(final String actualObject, final String objectName, final String identifier, final String coordinateString, int counter, int retryInterval) {
		ObjectLocator locator = new ObjectLocator(objectName, identifier, actualObject);
		doClickAt(locator, coordinateString, counter, retryInterval);
	}
	
	private void doClickAt(final ObjectLocator locator, final String coordinateString, int counter, int retryInterval) {
		String objectID = "";
//		int counter = 12;
		int xOffset = 0;
		int yOffset = 0;
		
		WebDriver driver = getDriver();
		String objectName = locator.getObjectName();
		try {
			// Retrieve the correct object locator from the object map
			objectID = locator.getActualLocator();

			// first verify whether the element is present in the current web
			// page
//			checkForNewWindowPopups();
			WebElement element = checkElementPresence(objectID);

			try {
				xOffset = Integer.parseInt((coordinateString.split(",")[0]).trim());
				yOffset = Integer.parseInt((coordinateString.split(",")[1]).trim());
			} catch (Exception e) {

				e.printStackTrace();
				reportresult(true, "CLICKAT :" + objectName + "", "FAILED",
						"CLICKAT coordinate string (" + coordinateString + ") for :Element (" + objectName + ") [" + objectID + "] is invalid");
			}
			/*
			 * START DESCRIPTION following while loop was added to make the
			 * command more consistent try the command for give amount of time
			 * (can be configured through class variable RETRY) command will be
			 * tried for "RETRY" amount of times until command works. any
			 * exception thrown within the tries will be handled internally.
			 * 
			 * can be exited from the loop under 2 conditions 1. if the command
			 * succeeded 2. if the RETRY count is exceeded
			 */
			while (counter > 0) {
				try {
					counter--;
					// call for real selenium command
					/* selenium.clickAt(objectID, coordinateString); */

					Actions clickAt = new Actions(driver);
					clickAt.moveToElement(element, xOffset, yOffset).click();
					clickAt.build().perform();
					// if not exception is called consider and report the result
					// as passed
					reportresult(true, "CLICKAT :" + locator.getObjectName()
							+ "", "PASSED", "");
					// if the testcase passed move out from the loop
					break;
				} catch (StaleElementReferenceException staleElementException) {

					element = checkElementPresence(objectID);
				} catch (Exception e) {

					sleep(retryInterval);
					if (!(counter > 0)) {

						e.printStackTrace();
						reportresult(true, "CLICKAT :" + objectName + "",
								"FAILED",
								"CLICKAT command cannot access Element ("
										+ locator + ") [" + objectID + "] ");
					}
				}
			}
			/*
			 * END DESCRIPTION
			 */

		} catch (Exception e) {

			e.printStackTrace();
			/*
			 * VTAF result reporter call
			 */
			reportresult(true, "CLICKAT :" + objectName + "", "FAILED",
					"CLICKAT command  :Element (" + objectName + ") ["
							+ objectID + "] not present");
 		}
	}
	
	//===============GETOBJECTCOUNT===========
	public final void getObjectCount(final String objectName, final String identifire, final String actual) {
		
	}
	
	public final void doGetObjectCount(final ObjectLocator locator, final Object objValue) {
		
	}
	
	//===================================
	public final Date getCurrentTime() {
        return Calendar.getInstance().getTime();
    }
	
	 
	private WebElement checkElementPresence(final String searchPath)
			throws Exception {
		WebDriver driver = getDriver();
		WebElement webElement = null;
		String locator = searchPath;

		int count = 12;

		final By searchBy = getLocatorType(locator);
		final Long retryMillis = 1000L;

		try {

			Function<WebDriver, WebElement> findElementFunction = new FindElementFunction<WebDriver, WebElement>(
					searchBy);

			Wait<WebDriver> wait = new FluentWait<WebDriver>(driver)
					.withTimeout((count * retryMillis), TimeUnit.MILLISECONDS)
					.pollingEvery(1000, TimeUnit.MILLISECONDS)
					.ignoring(NoSuchElementException.class)
					.ignoring(WebDriverException.class);

			webElement = wait.until(findElementFunction);

		} catch (Exception e) {
			e.printStackTrace();
		}

		if (webElement != null) {
			try {

				JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
				jsExecutor.executeScript("arguments[0].scrollIntoView(false);",
						webElement);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		} else {
			throw new Exception("Element " + searchPath);
		}

		return webElement;
	}

	public static class FindElementFunction<T, K> implements Function<WebDriver, WebElement> {
		/** The search by locator. */
		private By searchByLocator;

		/**
		 * The constructor for inner class.
		 * 
		 * @param by
		 *            The by locator
		 * */
		public FindElementFunction(final By by) {
			searchByLocator = by;
		}

		/**
		 * The apply method which returns a web element.
		 * 
		 * @param driver
		 *            the webdriver
		 * @return The web element found
		 * */
		@Override
		public final WebElement apply(final WebDriver driver) {
			
			return driver.findElement(searchByLocator);
		}
	}

	private String checkNullObject(final Object obj, final String command) {
		String value = null;
		try {
			value = obj.toString();
		} catch (NullPointerException e) {
			reportresult(true, command + " command:", "FAILED", command
					+ " command: Invalid input. cannot use null as input");
		}
		return value;
	}
	
	public final By getLocatorType(final String objectID) throws Exception {

		String typeString = "";
		String ref = "";
		String objectIDinLowerCase = objectID.toLowerCase(Locale.getDefault());
		boolean isObjectTypeisXpath = objectIDinLowerCase.startsWith("/");
		if (!isObjectTypeisXpath) {
			try {
				typeString = objectIDinLowerCase.substring(0,
						objectID.indexOf('='));
				ref = objectID.substring(objectID.indexOf('=') + 1,
						objectID.length());
			} catch (Exception e) {
				throw new Exception("Invalid Locator Passed " + objectID);
			}
		}
		// String objectIDType = typeString.toLowerCase(Locale.getDefault());
		if (isObjectTypeisXpath) {

			return By.xpath(objectID);
		} else if (typeString.contains("xpath")) {

			return By.xpath(ref);
		} else if (typeString.contains("css")) {

			return By.cssSelector(ref);
		} else if (typeString.contains("id")) {

			return By.id(ref);
		} else if (typeString.contains("link")) {

			return By.linkText(ref);
		} else if (typeString.contains("tagname")) {

			return By.tagName(ref);
		} else if (typeString.contains("name")) {

			return By.name(ref);
		} else if (typeString.contains("classname")) {

			return By.className(ref);
		}

		throw new Exception("Invalid Locator Type Passed " + ref);

	}

	public final void reportresult(final boolean isAssert, final String step,
			final String result, final String messageString) {
		try {
			String message = messageString;
			String testStep = step;
			// replace xml special characters in the message.

			if (result.equals("PASSED")) {
				String testMessage = message;
				if (message.equals("") || message == null) {
					testMessage = "Passed";
				}
				resultReporter.reportStepResults(true, testStep, testMessage,
						"Success", "");
			} else {
				resultReporter.reportStepResults(false, testStep, message,
						"Error",
						getSourceLines(new Throwable(message).getStackTrace()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public final void pause(final String waitingTime) {
		try {
			int waitingMilliSeconds = Integer.parseInt(waitingTime);
			sleep(waitingMilliSeconds);
			reportresult(true, "PAUSE Command: (" + waitingMilliSeconds
					+ " ms)", "PASSED", "Pausing for " + waitingTime
					+ " Milliseconds.");
		} catch (NumberFormatException e) {
			reportresult(true, "PAUSE Command: (" + waitingTime + " ms)",
					"FAILED", "Invalid arugment passed for wait time.");
		}

	}
	
	public final void sleep(final int millisecs) {
        try {
            Thread.sleep(millisecs);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
 }

	public final String getSourceLines(final StackTraceElement[] stackTrace) {
		StringBuilder stacktraceLines = new StringBuilder();
		final int stackTraceIndex = 1;
		for (int elementid = 0; elementid < stackTrace.length; elementid++) {

			if (stackTrace[elementid].toString().indexOf("invoke0") != -1) {

				stacktraceLines.append(stackTrace[elementid - stackTraceIndex])
						.append("|");
				stacktraceLines.append(
						stackTrace[elementid - (stackTraceIndex + 1)]).append(
						"|");
				stacktraceLines.append(stackTrace[elementid
						- (stackTraceIndex + 2)]);
			}
		}
		return stacktraceLines.toString();
	}

	private WebDriver getDriver() {

		return driver;
	}

}
