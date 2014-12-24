package com.thread;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.objects.ResolvedTestCase;
import com.objects.Status;
import com.objects.TCData;
import com.objects.UnresolvedTestCases;
import com.superAgent.SuperAgent;

/**
 * Thread to receive test case and send to reslove according to data table values
 * @author nvithushan
 *
 */
public class TestCaseReceiveX implements Runnable{
	public Thread t;
	private String threadName;
	
	//Hashmap to store queue which will have resolved test case object
	public static HashMap<String, ConcurrentLinkedQueue<ResolvedTestCase>> resolvedTestCaseHashMap = new HashMap<>();
	
	//to count alive thread at once where limit is checked
	public static int count = 0;
	
	public TestCaseReceiveX(String name){
		threadName = name;
	}
	
	@Override
	public void run(){
		ConcurrentLinkedQueue<UnresolvedTestCases> testCaseQueue = new ConcurrentLinkedQueue<>();
	    
        UnresolvedTestCases utc = new UnresolvedTestCases();
		
		//---Hardcoded tc file with multiple config
        JSONObject config1  = new JSONObject();
        JSONObject config2  = new JSONObject();
        JSONObject config3  = new JSONObject();
        JSONObject config4  = new JSONObject();
        JSONArray config = new JSONArray();
        JSONObject mainConfig = new JSONObject();
        JSONObject tcName = new JSONObject();
        
        try {
			config1.put("Tool", "Sellenium");
			config1.put("Browser", "Firefox");
			config1.put("OS", "Windows");
			
			config2.put("Tool", "Sellenium");
			config2.put("Browser", "Chrome");
			config2.put("OS", "Mac");

			config3.put("Tool", "QTP");
			config3.put("Browser", "IE");
			config3.put("OS", "Mac");
			
			config4.put("Tool", "QTP");
			config4.put("Browser", "Firefox");
			config4.put("OS", "Windows");
			
			config.put(config1);
			config.put(config4);
			config.put(config3);
			config.put(config4);
			config.put(config3);
			config.put(config2);
			config.put(config1);
			config.put(config1);
			config.put(config4);
			
			tcName.put("name", "SearchPromotions");
			
			mainConfig.put("Config", config);
			mainConfig.put("TestCase", tcName);
			mainConfig.put("ID", "8:1");
		} catch (JSONException e) {
			e.printStackTrace();
		}//mainconfig is the output file
        
        // class fuction 
        JSONArray configArr = new JSONArray(); //to get config of a testcase 
        JSONObject tc = new JSONObject(); //to get test case
        String keyName = null, id = null;
        
        try {
        	configArr = mainConfig.getJSONArray("Config"); 	//extracting config from main test case file
        	id = mainConfig.getString("ID"); //geting test case ID from main test case file
		}catch (JSONException e) {
			e.printStackTrace();
		}
        
        HashMap<String, HashMap<String, TCData>> resTcTackHashMap1 = new HashMap<>();
        SuperAgent.tcTrackHashMap.put(id, resTcTackHashMap1); //creating hashmap for every test cases received
        
        //to loop through the config file to divide the test case into the number of configs available 
        for (int i = 0; i < configArr.length(); i++) {
        	JSONObject ja = null;
        	String browser = null, tool = null, os = null;
        	
        	//dividing the test case and inserting into testcase Queue
        	try {
        		ja = configArr.getJSONObject(i);
        		browser = ja.getString("Browser");
        		tool = ja.getString("Tool");
        		os = ja.getString("OS");
        		
        		keyName = browser+"_"+tool+"_"+os;
        		
        		tc = mainConfig.getJSONObject("TestCase");
        		
        		utc.setId(id);
        		utc.setConfig(keyName);
        		utc.setTestCase(tc);
        		utc.setConfigID(id+":"+(i+1));
        		
        		testCaseQueue.offer(utc);
        		utc = new UnresolvedTestCases();
        		
        		HashMap<String, TCData> resTcTackHashMap2 = new HashMap<>();
        		resTcTackHashMap1.put(id+":"+(i+1), resTcTackHashMap2);
        		
        	} catch (JSONException e) {
        		e.printStackTrace();
        	}

        	//creating hashmap element for each config if that does not already exist 
        	if(!resolvedTestCaseHashMap.containsKey(keyName)){
        		ConcurrentLinkedQueue<ResolvedTestCase> hashQueue = new ConcurrentLinkedQueue<>(); 
        		resolvedTestCaseHashMap.put(keyName, hashQueue);
        	}
		}
        
        UnresolvedTestCases obj;
        
        //infinite for loop to check if max thred is created and create thread for testcase if not max limit is excveeded
        for(;;){
        	if(!testCaseQueue.isEmpty()){
        		if(count < 3){
        			obj = testCaseQueue.poll();
        			TestCaseResolveX insert = new TestCaseResolveX("Insert", obj);
        			insert.start();
        			count++;        			
        		}
        	}
        	
        	for (Entry<String, HashMap<String, HashMap<String, TCData>>> a : SuperAgent.tcTrackHashMap.entrySet()) {
        		for (Entry<String, HashMap<String, TCData>> b : a.getValue().entrySet()) {
            		for (Entry<String, TCData> c : b.getValue().entrySet()) {
            			System.out.print(c.getKey()+':'+c.getValue().getStatus() + "  *  ");
            		}        			
        		}
			}
        	System.out.println();
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
        }
	}
	
	/*
	 * Starting the thread
	 */
	public void start (){
		if (t == null){
			t = new Thread (this, threadName);
			t.start ();
		}
	}
	
	/**
	 * method to remove test case from "resolvedTestCaseHashMap" to send to agent if there is test case with the config
	 * @param config
	 * @return
	 */
	public static ResolvedTestCase popTestCaseObject(String config){
		if(!(resolvedTestCaseHashMap.get(config) == null) && !(resolvedTestCaseHashMap.get(config).isEmpty()))
			return resolvedTestCaseHashMap.get(config).poll();
		else
			return null;
	}
}
