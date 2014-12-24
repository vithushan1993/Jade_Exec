package com.Thread;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;

import com.ChildAgent.ChildAgent;
import com.virtusa.childagent.runtime.SeleniumTestBase;

public class ClassThread implements Runnable {
	public Thread t;
	   private String threadName;
	   boolean suspended = false;
	   ChildAgent childAgent;
	   JSONArray ja = new JSONArray();
	   SeleniumTestBase testBase = new SeleniumTestBase();
	   String page, ms, object, text;
	   
	   public ClassThread(String name, ChildAgent sup){
	       threadName = name;
	       childAgent = sup;
	   }
	   
	   public void run() {
		   try {
			   ja = ChildAgent.jsonObj.getJSONArray("testCase");
				
			   for(int i = 0; i < ja.length();i++){
				   switch (ja.getJSONObject(i).getString("command")) {
					   case "open":
						   page = StringEscapeUtils.unescapeXml(ja.getJSONObject(i).getString("page"));
						   ms = StringEscapeUtils.unescapeXml(ja.getJSONObject(i).getString("ms"));
							
						   testBase.open(page,"objName", ms);
						   break;
					   case "type":
						   object = StringEscapeUtils.unescapeXml(ja.getJSONObject(i).getString("object"));
						   text = StringEscapeUtils.unescapeXml(ja.getJSONObject(i).getString("type"));
							
//						   testBase.type("someName", object, text, 12);
						   break;
					   case "pause":
						   break;
					   default:
						   break;
				   }
			   }
			   synchronized(this) {
				   while(suspended) {
					   wait();
				   }
			   }
		   }catch (InterruptedException e) {
			   e.printStackTrace();
		   }catch (JSONException e) {
				e.printStackTrace();
		   }
			
	   }

	   public void start ()
	   {
	      if (t == null){
	         t = new Thread (this, threadName);
	         t.start ();
	      }
	   }
	   
	   void suspend() {
		   suspended = true;
	   }
	   
	   synchronized void resume() {
	      suspended = false;
	      notify();
	   }
}
