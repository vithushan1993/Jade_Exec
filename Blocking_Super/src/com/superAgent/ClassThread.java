package com.superAgent;

import org.json.JSONException;

public class ClassThread implements Runnable {
	public Thread t;
	   private String threadName;
	   boolean suspended = false;
	   SuperAgent superAgent;

	   ClassThread(String name, SuperAgent sup){
	       threadName = name;
	       superAgent = sup;
	   }
	   
	   public void run() {
		   try {
			   int i = 1;
			   for(;;) {
				   try {
//					   System.out.println(SuperAgent.jsonObj.getJSONArray("Block"+i));
					   superAgent.sendTestCase(SuperAgent.jsonObj.getJSONObject("Block"+i), SuperAgent.jsonObj.getJSONObject("Block"+i).get("tempagent").toString(), SuperAgent.jsonObj.getJSONObject("Block"+i).get("tempagentAdd").toString());
					   
					   int a = i+1;
					   
					   if(SuperAgent.jsonObj.getJSONObject("Block"+a).equals("{}")){
						   System.out.println(SuperAgent.jsonObj.getJSONObject("Block"+a));
					   }
					   
					   if(SuperAgent.jsonObj.getJSONObject("Block"+a).get("dependant").toString() != null){
						   this.suspend();
					   }
				   } catch (JSONException e) {
					   e.printStackTrace();
				   }
				   
				   synchronized(this) {
					   while(suspended) {
						   wait();
					   }
				   }
				   Thread.sleep(1000);
				   i++;
				   
				   if(i > 2){
					   this.suspend();
				   }
			   }
		   } catch (InterruptedException e) {
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
