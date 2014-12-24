package com.executingAgent;

import javaxt.utils.string;

import org.json.JSONException;
import org.json.JSONObject;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.core.AID;
import jade.lang.acl.*;

import com.receiver.ui.*; 
import com.receiver.Execution;

/**
 * Class for receiver (receive message and send rep for the sender)
 * @author nvithushan
 *
 */
public class ChildAgent extends Agent {
	public static String trackID = null; //to store the IDs of test case received
	
	private static String ID = null;		//main id of the test case received
	private static String configID = null;	//config id of the test case received
	private static String tcID = null;		//id of the test case received
	private static String agentName = null;		//storing the super node name 
	private static String agentAddress = null;	//storing the super node address
	private static String config = null;	//storing this agent config

	SwingFrame frame = new SwingFrame(this);
	Execution res = new Execution();
	
    protected void setup(){ 
    	super.setup();
    	
    	//receiving message
    	addBehaviour(new CyclicBehaviour(this) 
		{
    		public void action() {
				ACLMessage msg= receive();
				String fileName = null;
		
				if (msg!=null){
					fileName = msg.getUserDefinedParameter("fileName");
					
					if(fileName == null){
						
					}else{
						//Checking message whether this agent is alive
						if(fileName.equalsIgnoreCase("Check")){
							ACLMessage reply = msg.createReply();
				            reply.setPerformative( ACLMessage.INFORM);
				            reply.addUserDefinedParameter("fileName", "Alive");
				            reply.addReceiver(msg.getSender());
				            send(reply);
						}else{
							//receiving test case to execute
							byte[] fileContent = msg.getByteSequenceContent();
							trackID = fileName;

							String[] addArr = msg.getSender().getAddressesArray();
							
							setAgentName(msg.getSender().getName());
							setAgentAddress(addArr[0]);
							
							String[] arr = trackID.split("_");
							
							setID(arr[0]);
							setConfigID(arr[1]);
							setTcID(arr[2]);
							
							try {
	            				JSONObject n = new JSONObject(new String(fileContent));
	            				
	            				res.childExecute(n, ChildAgent.this);
	            			} catch (JSONException e) {
	            				e.printStackTrace();
	            			}
						}
					}
					block();
				}
    		}
		});
//    	frame.start(this); //Starting the UI
    	
//    	String abc = "{\"Firefox_Sellenium_Windows\":[{\"open\":{\"ms\":\"5000\",\"page\":\"http://www.hsbc.lk/\"}},{\"type\":{\"text\":\"Max and Min\",\"object\":\"css=input[id=&apos;searchString&apos;]\"}}]}";
    	String abc = "{\"Firefox_Sellenium_Windows\":[{\"open\":{\"ms\":\"5000\",\"page\":\"http://www.google.lk/\"},\"seq\":\"1\"},{\"open\":{\"ms\":\"5000\",\"page\":\"http://www.hsbc.lk/\"},\"seq\":\"2\"}]}";
    	
    	JSONObject JO;
		try {
			JO = new JSONObject(abc);
			res.childExecute(JO, this);
		} catch (JSONException e){
			e.printStackTrace();
		}
    }
    
    /**
     * Sending report back to super node after executing the test case 
     * @param report
     */
    public void sendReport(JSONObject report){
    	System.out.println("Sending report " + report);
    	
//    	byte[] byteArr = report.toString().getBytes();
//    	
//    	ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
//    	
//    	AID r = new AID(getAgentName(), AID.ISGUID);
//    	r.addAddresses(getAgentAddress());
//    	
//        msg.addReceiver(r);
//        msg.setContent(trackID);
//    	msg.setByteSequenceContent(byteArr);
//    	msg.addUserDefinedParameter("fileName", trackID);
//    	send(msg);
//    	
//    	frame.start(this);
//    	sendConfig(getConfig());
    }
    
    /**
     * Sending super node the agent's config to receive a test case 
     * @param message
     */
    public void sendConfig(String message){
    	setConfig(message);
        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
        AID r = new AID("Sukhitha@superContainer", AID.ISGUID);
        r.addAddresses("http://CL-SAMARAKOON.Virtusa.com:7778/acc+");
        
        
        msg.setContent(message);
        msg.addReceiver(r);
        msg.addUserDefinedParameter("fileName", "Config");
        send(msg);				
        System.out.println("Config message " + message);
    }
    
    public static String getID() {
    	return ID;
    }
    
    public static void setID(String iD) {
    	ID = iD;
    }
    
    public static String getConfigID() {
    	return configID;
    }
    
    public static void setConfigID(String configID) {
    	ChildAgent.configID = configID;
    }
    
    public static String getTcID() {
    	return tcID;
    }
    
    public static void setTcID(String tcID) {
    	ChildAgent.tcID = tcID;
    }
    
    public static String getAgentName() {
		return agentName;
	}

	public static void setAgentName(String agentName) {
		ChildAgent.agentName = agentName;
	}

	public static String getAgentAddress() {
		return agentAddress;
	}

	public static void setAgentAddress(String agentAddress) {
		ChildAgent.agentAddress = agentAddress;
	}
	
	public static String getConfig() {
		return config;
	}

	public static void setConfig(String config) {
		ChildAgent.config = config;
	}
}