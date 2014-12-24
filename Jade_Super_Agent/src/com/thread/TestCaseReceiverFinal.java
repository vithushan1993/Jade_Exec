package com.thread;

import java.util.HashMap;
import java.util.Map.Entry;

import com.objects.ResolvedTestCase;
import com.objects.TCData;
import com.superAgent.SuperAgent;

public class TestCaseReceiverFinal implements Runnable {
	public Thread t;
	private String threadName;
	
	//to count alive thread at once where limit is checked
	public static int count = 0;
	
	public TestCaseReceiverFinal(String name){
		threadName = name;
	}
	
	@Override
	public void run() {
		for (;;) {
			for(int i = 0; i < SuperAgent.receivedTestCase.size(); i++) {
	             //loop to check if max thred is created and create thread for testcase if not max limit is exceeded
	        	 if(!SuperAgent.receivedTestCase.isEmpty()){
	        		 if(count < 3){
	        			 TestCaseResolverFinal insert = new TestCaseResolverFinal("Insert", SuperAgent.receivedTestCase.remove(i));
	        			 insert.start();
	        			 count++;        			
	        		 }
	        	 }
			}
			
//			for (Entry<String, HashMap<String, HashMap<String, TCData>>> a : SuperAgent.tcTrackHashMap.entrySet()) {
//        		for (Entry<String, HashMap<String, TCData>> b : a.getValue().entrySet()) {
//            		for (Entry<String, TCData> c : b.getValue().entrySet()) {
//            			System.out.print(c.getKey()+':'+c.getValue().getStatus() + "  *  ");
//            		}        			
//        		}
//			}
//			System.out.println();
			
			System.out.println(SuperAgent.resolvedTestCaseHashMap);
			
			try {
				Thread.sleep(3500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

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
		if(!(SuperAgent.resolvedTestCaseHashMap.get(config) == null) && !(SuperAgent.resolvedTestCaseHashMap.get(config).isEmpty()))
			return SuperAgent.resolvedTestCaseHashMap.get(config).poll();
		else
			return null;
	}
}