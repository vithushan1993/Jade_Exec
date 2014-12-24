package com.superAgent;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class SuperAgent extends Agent{
	
	public static JSONObject jsonObj;
	public static HashMap<String, String> resolvedTCHashMap = new HashMap<>();
	
	protected void setup(){
        super.setup();
        
        addBehaviour(new CyclicBehaviour(this) {
        	//listen for an action like receiving a message
        	public void action() {
            	ACLMessage msg= receive(); 
            	
            	if (msg!=null){
//                	String s =  msg.getContent(); // get content of the received message
//                	String fileName = msg.getUserDefinedParameter("fileName");
////            		String agentName, agentAddress;
//            		String[] addArr = msg.getSender().getAddressesArray();
            		
//            		if(fileName == null){
//            			
//            		}
                    block();
                }
            }
        });
        startWork();
	}
	
	public void startWork(){
		String jsonData = "" ;//, config = "Firefox_Sellenium_Windows";
		BufferedReader br = null;
		try {
			String line;
			br = new BufferedReader(new FileReader("Blocking_TC.txt"));
		
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
		try {
			JSONObject JO = new JSONObject(jsonData);
			SuperAgent.jsonObj = JO;
			check();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void check(){
		ClassThread R1 = new ClassThread( "Thread-1", this);
		R1.start();
	      
		try {
			Thread.sleep(2000);
//			R1.suspend();
			R1.resume();
			Thread.sleep(3000);
			R1.resume();
//			Thread.sleep(5000);
//			R1.t.stop();
		}catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void sendTestCase(JSONObject jo, String agentName, String agentAddress){
    	System.out.println(jo);
    	byte[] byteArr = jo.toString().getBytes();
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		AID r = new AID(agentName, AID.ISGUID);
		r.addAddresses(agentAddress);
		
		msg.addReceiver(r);
		msg.setByteSequenceContent(byteArr);
		msg.addUserDefinedParameter("fileName", "testCase");
//		send(msg);
    }
}