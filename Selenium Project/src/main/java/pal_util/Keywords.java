package pal_util;
import java.util.Hashtable;
import org.testng.Assert;
import org.testng.annotations.Test;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import dataUtility.Xls_Reader;

public class Keywords {
	
	
	ExtentTest TestKeywords;
	App_Keywords App_key;
	Xls_Reader xlsx;
	public Keywords(Xls_Reader x, ExtentTest t) {
		TestKeywords = t;
		xlsx = x;
	}

@Test
	public void executeKeywords(String TestName, Hashtable <String,String> Htable) throws InterruptedException {
		// Create an object of App Keyword class
		TestKeywords.log(LogStatus.INFO, "Executing keywords");
		App_key = new App_Keywords(TestKeywords);

		// Reading an excel file in the project
		String keywordsSheet="Keywords";
		int rownum = xlsx.getRowCount(keywordsSheet); // index of the last row
		int Testnamerow = 1;
		// Reading the Test details start row >>>> code moved to Data utility class
		while(!xlsx.getCellData(keywordsSheet, 0, Testnamerow).equalsIgnoreCase(TestName)){
			Testnamerow ++;
			
		}
		
		// starting for loop for the excel file
		for(int rNum=Testnamerow;rNum<rownum + Testnamerow;rNum++){
			String tcid = xlsx.getCellData(keywordsSheet, "TCID", rNum);
			if(tcid.equalsIgnoreCase(TestName)){
				String keyword = xlsx.getCellData(keywordsSheet, "Keyword", rNum);
				String object = xlsx.getCellData(keywordsSheet, "Object", rNum);
				String data = xlsx.getCellData(keywordsSheet, "Data", rNum);
				TestKeywords.log(LogStatus.INFO, "TEST STEP :-"+"Action ->"+ keyword+ ", Object ->" + object+", Data ->"+ data);

				if (keyword.equals(pal_Constants.OPEN_BROWSER))
					App_key.openbrowser(Htable.get(data));
				else if (keyword.equals(pal_Constants.OPEN_URL))
					App_key.openurl(object);
				else if (keyword.equals(pal_Constants.VERIFY_TEXT_PRESENT))
					App_key.verifyTextPresent(object, Htable.get(data));
				else if (keyword.equals(pal_Constants.INPUT))
					App_key.input(object, Htable.get(data));
				else if (keyword.equals(pal_Constants.Last_Test_Step))
					App_key.lastTestStep();
				else if (keyword.equals(pal_Constants.CLICK_ELEMENT))
					App_key.clickelement(object);
				else if (keyword.equals(pal_Constants.CLOSE_BROWSER))
					App_key.closebrowser();
				else {
					TestKeywords.log(LogStatus.ERROR, "Oops!..Provided keyword is not present in the Keywords.java file.");
					Assert.fail("Oops!..Provided keyword is not present in the Keywords.java file.");		
				}
			}
			
		}
				
	}
	public void FinalTestResult(){
		App_key.FinalTestResult();
		
	}
	
}
