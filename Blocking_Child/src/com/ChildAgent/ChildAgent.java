package com.ChildAgent;

import org.json.JSONException;
import org.json.JSONObject;

import com.Thread.ClassThread;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

public class ChildAgent extends Agent{
	public static JSONObject jsonObj;
	
	protected void setup(){
        super.setup();
        
        addBehaviour(new CyclicBehaviour(this) {
        	//listen for an action like receiving a message
        	public void action() {
            	ACLMessage msg= receive(); 
            	
            	if (msg!=null){
//                	
                    block();
                }
            }
        });
        String sampleJO = "{\"tempagentAdd\":\"http://CL-SAMARAKOON.Virtusa.com:7778/acc\",\"ready\":true,\"keepalive\":\"Block3\",\"tempagent\":\"Child@ChildCont\",\"dependant\":null,\"testCase\":[{\"ms\":\"1000\",\"page\":\"http://www.hsbc.lk/\",\"command\":\"open\"}]}";
        
		try {
			jsonObj = new JSONObject(sampleJO);
			
			ClassThread R1 = new ClassThread( "Thread-1", this);
			R1.start();
			
		} catch (JSONException e){
			e.printStackTrace();
		}
	}
}