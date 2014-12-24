package com.sender;

import org.json.JSONException;
import org.json.JSONObject;

import com.objects.ReceiveTestCase;
import com.objects.ResolvedTestCase;
import com.objects.TCData;
import com.superAgent.SuperAgent;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;


public class SampleInput {
	public static void setTestCase(String config) throws JSONException,FileNotFoundException{
		String jsonData = "" ;//, config = "Firefox_Sellenium_Windows";
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader("DummyTC.txt"));
		
			while ((line = br.readLine()) != null) {
				jsonData += line + "\n";
			}
		}catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if (br != null)
					br.close();
			}catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		JSONObject JO = new JSONObject(jsonData);
		
		ReceiveTestCase obj = new ReceiveTestCase();
		
		
		obj.setConfig(config);
		obj.setTestCase(JO.getJSONArray("expression"));
		obj.setId(JO.get("@rid").toString());
    	
		HashMap<String, HashMap<String, TCData>> resTcTackHashMap1 = new HashMap<>();
    	SuperAgent.tcTrackHashMap.put(obj.getId(), resTcTackHashMap1); //creating hashmap for every test cases received
		
		
		//creating hashmap element for each config if that does not already exist 
    	if(!SuperAgent.resolvedTestCaseHashMap.containsKey(config)){
    		ConcurrentLinkedQueue<ResolvedTestCase> hashQueue = new ConcurrentLinkedQueue<>(); 
    		SuperAgent.resolvedTestCaseHashMap.put(config, hashQueue);
    	}
    	
    	//code to divide the testcase
    		obj.setConfigID(obj.getId() + ":1");
    		HashMap<String, TCData> resTcTackHashMap2 = new HashMap<>();
    		resTcTackHashMap1.put(obj.getConfigID(), resTcTackHashMap2);
    	
    	//
    	
		SuperAgent.receivedTestCase.add(obj);
	}
}