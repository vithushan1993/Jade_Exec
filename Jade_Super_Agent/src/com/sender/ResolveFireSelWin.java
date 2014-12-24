package com.sender;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.objects.FlagValue;

public class ResolveFireSelWin {
	public String commonSolver(JSONObject value){
		String ans = null;
		
		try {
			if(!value.get("string").equals(null)){ // :)
				if(!value.getString("string").equals(null)){
					ans = value.getString("string");
				}
			}else if(!value.get("variable").equals(null)){
				if((value.getJSONObject("variable").length()>0)){
					
				}
			}else if(!value.get("object").equals(null)){ // :)
				if((value.getJSONObject("object").length()>0)){
					ans = (String) value.getJSONObject("object").getJSONObject("identifier").getString("identifier");
				}
			}else if(!value.get("column").equals(null)){
				if((value.getJSONObject("column").length()>0)){
					
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ans;
	}
	
	/**
	 * OPEN
	 * @param ja
	 * @return
	 */
	public JSONObject open(JSONArray ja){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		
		for(int i = 0; i < ja.length(); i++){
			try {
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Page")){
					jo.put("page", ja.getJSONObject(i).getJSONObject("value").getJSONObject("object").getJSONObject("identifier").get("identifier"));
					
					
					commonSolver(ja.getJSONObject(i).getJSONObject("value"));
					
					
				}else if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("ms")){
					jo.put("ms", ja.getJSONObject(i).getJSONObject("value").get("string"));
				}
			} catch (JSONException e) { 
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("open", jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJO;
	}
	
	/**
	 * TYPE
	 * @param ja
	 * @param count
	 * @param flag
	 * @return
	 */
	public FlagValue type(JSONArray ja, int count, boolean flag){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		FlagValue flagObj = new FlagValue();
		
		for(int i = 0; i < ja.length(); i++){
			try { 
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Object")){
					jo.put("object", ja.getJSONObject(i).getJSONObject("value").getJSONObject("object").getJSONObject("identifier").get("identifier"));
				}else if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Text")){
					JSONArray rowJA = (ja.getJSONObject(i).getJSONObject("value").getJSONObject("column").getJSONArray("row"));

					if(!(rowJA.isNull(count))){
						jo.put("text", rowJA.getJSONObject(count).get("value"));
					}else{
						jo.put("text", "null");
					}
					
					if(!(rowJA.isNull(count+1))){
						flagObj.setFlag(true);
					}else{
						flagObj.setFlag(false);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("type", jo);
			
			flagObj.setResTC(finalJO);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return flagObj;
	} 
	
	/**
	 * CLICK
	 * @param ja
	 * @return
	 */
	public JSONObject click(JSONArray ja){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		
		for(int i = 0; i < ja.length(); i++){
			try {
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Object")){
					jo.put("object", ja.getJSONObject(i).getJSONObject("value").getJSONObject("object").getJSONObject("identifier").get("identifier"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("click", jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJO;
	}
	
	/**
	 * CHECKELEMENTPRESENT
	 * @param ja
	 * @return
	 */
	public JSONObject checkElementPresent(JSONArray ja){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		
		for(int i = 0; i < ja.length(); i++){
			try {
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Object")){
					jo.put("object", ja.getJSONObject(i).getJSONObject("value").getJSONObject("object").getJSONObject("identifier").get("identifier"));
				}else if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("StopOnFailure")){
					jo.put("stopOnFailure", ja.getJSONObject(i).getJSONObject("value").getJSONObject("object").getJSONObject("identifier").get("identifier"));
				}else if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("CustomErrorMessage")){
					jo.put("customErrorMessage", ja.getJSONObject(i).getJSONObject("value").get("string"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("checkElementPresent", jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJO;
	}
	
	/**
	 * KEYPRESS
	 * @param ja
	 * @return
	 */
	public JSONObject keyPress(JSONArray ja){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		
		for(int i = 0; i < ja.length(); i++){
			try {
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Object")){
					jo.put("object", ja.getJSONObject(i).getJSONObject("value").getJSONObject("object").getJSONObject("identifier").get("identifier"));
				}else if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Value")){
					jo.put("value", ja.getJSONObject(i).getJSONObject("value").get("string"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("keyPress", jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJO;
	}
	
	/**
	 * GETOBJECTCOUNT
	 * @param ja
	 * @return
	 */
	public JSONObject getObjectCount(JSONArray ja){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		
		for(int i = 0; i < ja.length(); i++){
			try {
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Object")){
					jo.put("object", ja.getJSONObject(i).getJSONObject("value").getJSONObject("object").getJSONObject("identifier").get("identifier"));
				}else if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("VarName")){
					jo.put("varName", ja.getJSONObject(i).getJSONObject("value").get("string"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("getObjectCount", jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJO;
	}
	
	/**
	 * COMMENT
	 * @param ja
	 * @return
	 */
	public JSONObject comment(JSONArray ja){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		
		for(int i = 0; i < ja.length(); i++){
			try {
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Comment")){
					jo.put("Comment", ja.getJSONObject(i).getJSONObject("value").get("string"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("comment", jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJO;
	}
	
	/**
	 * PAUSE
	 * @param ja
	 * @return
	 */
	public JSONObject pause(JSONArray ja){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		
		for(int i = 0; i < ja.length(); i++){
			try {
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Ms")){
					jo.put("ms", ja.getJSONObject(i).getJSONObject("value").get("string"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("pause", jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJO;
	}
	
	/**
	 * SELECTFRAME
	 * @param ja
	 * @return
	 */
	public JSONObject selectFrame(JSONArray ja){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		
		for(int i = 0; i < ja.length(); i++){
			try {
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Object")){
					jo.put("object", ja.getJSONObject(i).getJSONObject("value").getJSONObject("object").getJSONObject("identifier").get("identifier"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("selectFrame", jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJO;
	}
	
	/**
	 * NAVIGATETOURL
	 * @param ja
	 * @return
	 */
	public JSONObject navigateToURL(JSONArray ja){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		
		for(int i = 0; i < ja.length(); i++){
			try {
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Url")){
					jo.put("url", ja.getJSONObject(i).getJSONObject("value").get("string"));
				}else if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Ms")){
					jo.put("ms", ja.getJSONObject(i).getJSONObject("value").get("string"));
				}
			}catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("navigateToURL", jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJO;
	}
	
	/**
	 * DOUBLECLICK
	 * @param ja
	 * @return
	 */
	public JSONObject doubleClick(JSONArray ja){
		JSONObject jo = new JSONObject();
		JSONObject finalJO = new JSONObject();
		
		for(int i = 0; i < ja.length(); i++){
			try {
				if(ja.getJSONObject(i).getJSONObject("constraint").get("constraint").equals("Object")){
					jo.put("object", ja.getJSONObject(i).getJSONObject("value").getJSONObject("object").getJSONObject("identifier").get("identifier"));
				}
			}catch (JSONException e) {
				e.printStackTrace();
			}
		}
		try {
			finalJO.put("doubleClick", jo);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return finalJO;
	}
}