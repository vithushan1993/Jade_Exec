package com.receiver;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.executingAgent.ChildAgent;
import com.virtusa.childagent.runtime.SeleniumTestBase;

/**
 * Class to execute the resolved test case 
 * @author nvithushan
 *
 */
public class Execution {
	public void childExecute(JSONObject ja, ChildAgent child){
//		SeleniumTestBase testBase = new SeleniumTestBase();
		
		String page, ms, object, text, stopOnFailure, customErrorMessage, value, varName, comment, url;
		
		JSONArray arr = new JSONArray();
		try {
			arr = ja.getJSONArray("Firefox_Sellenium_Windows");
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		
		for(int f = 1; f <= arr.length(); f++ ){
			for(int s = 0; s < arr.length(); s++){
				
			}
		}
		
		for( int i = 0 ; i < arr.length() ; i++ ){			
			try {
				JSONObject jao = arr.getJSONObject(i);
				JSONObject jo = new JSONObject();
				
				switch (jao.names().getString(0)){
				
				case "open":
					jo = jao.getJSONObject("open");
					System.out.println(jo);
					page = StringEscapeUtils.unescapeXml(jo.getString("page"));
					ms = StringEscapeUtils.unescapeXml(jo.getString("ms"));
							
//								testBase.open(page,"objName", ms);
					break;
				case "type":
					jo = jao.getJSONObject("type");
					
					object = StringEscapeUtils.unescapeXml(jo.getString("object"));
					text = StringEscapeUtils.unescapeXml(jo.getString("text"));
					
//								testBase.type("someName", object, text, 12);
					break;
				case "click":
					jo = jao.getJSONObject("click");
					object = jo.getString("object");
					
//								testBase.click(object);
					break;
				case "checkElementPresent":
					jo = jao.getJSONObject("checkElementPresent");
					object = jo.getString("object");
					stopOnFailure = jo.getString("stopOnFailure");
					customErrorMessage = jo.getString("customErrorMessage");
					
//								testBase.checkElementPresent(object, stopOnFailure, customErrorMessage);
					break;
				case "keyPress":
					jo = jao.getJSONObject("keyPress");
					object = jo.getString("object");
					value = jo.getString("value");
					
//								testBase.keyPress(object, value);
					break;
				case "getObjectCount":
					jo = jao.getJSONObject("getObjectCount");
					object = jo.getString("object");
					varName = jo.getString("varName");
					
//								testBase.getObjectCount(object, varName);				
					break;
				case "comment":
					jo = jao.getJSONObject("comment");
					comment = jo.getString("comment");
					
//								testBase.click(comment);
					break;
				case "pause":
					jo = jao.getJSONObject("pause");
					ms = jo.getString("ms");
					
//								testBase.pause(ms);
					break;
				case "selectFrame":
					jo = jao.getJSONObject("selectFrame");
					object = jo.getString("object");
					
//								testBase.selectFrame(object);
					break;
				case "navigateToURL":
					jo = jao.getJSONObject("navigateToURL");
					url = jo.getString("url");
					ms = jo.getString("ms");
					
//								testBase.navigateToURL(url, ms);
					break;
				case "doubleClick":
					jo = jao.getJSONObject("doubleClick");
					object = jo.getString("object");
					
//							testBase.doubleClick(object);
					break;
				default:
					break;
			}	
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
//		testBase.endTestCase();
//		child.sendReport(testBase.createReportJO());
	}
}
