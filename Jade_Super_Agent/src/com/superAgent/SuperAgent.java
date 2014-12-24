 package com.superAgent;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONException;
import org.json.JSONObject;

import com.objects.AgentDetails;
import com.objects.AgentHeartBeatData;
import com.objects.ReceiveTestCase;
import com.objects.ResolvedTestCase;
import com.objects.Status;
import com.objects.TCData;
import com.sender.GetTestCase;
import com.sender.ReportGathering;
import com.sender.SampleInput;
import com.thread.AgentHeartBeatTCCehck;
import com.thread.IdelThread;
import com.thread.TestCaseReceiverFinal;

import jade.core.Agent;
import jade.core.behaviours.*;
import jade.core.AID;
import jade.lang.acl.*;
import jade.domain.AMSService;
import jade.domain.FIPAException;

/**
 * Class to create the super node which controls sub node
 * @author nvithushan
 *
 */
public class SuperAgent extends Agent 
{
	public static ArrayList<AgentDetails> agentConfigArrayList = new ArrayList<>(); //Arraylist to store agent details who are waiting for a test case
	public static HashMap<String, HashMap<String, HashMap<String, TCData>>> tcTrackHashMap = new HashMap<>(); //Hashmap to track details of testcase
	public static ArrayList<AgentHeartBeatData> agentHearBeatDataArrayList = new ArrayList<>(); //Arraylist used in sending heartbeat data to check whether an agent is alive
	public static ArrayList<ReceiveTestCase> receivedTestCase = new ArrayList<>(); //ArrayList to receive and store testcase from cloud
	public static HashMap<String, ConcurrentLinkedQueue<ResolvedTestCase>> resolvedTestCaseHashMap = new HashMap<>(); //Hashmap to store queue which will have resolved test case object

	ReportGathering gather = new ReportGathering();
    GetTestCase get = new GetTestCase();
    
    //agent starting method
    protected void setup(){
        super.setup();
        
        addBehaviour(new CyclicBehaviour(this) {
        	//listen for an action like receiving a message
        	public void action() {
            	ACLMessage msg= receive(); 
            	
            	if (msg!=null){
                	String s =  msg.getContent(); // get content of the received message
                	String fileName = msg.getUserDefinedParameter("fileName");
            		String agentName, agentAddress;
            		String[] addArr = msg.getSender().getAddressesArray();
            		
            		if(fileName == null){
            			if(msg.getSender().getLocalName().equalsIgnoreCase("ams")){ //checking whther its ams
            				try {
            					String[] arr = AMSService.getFailedReceiver(SuperAgent.this, msg).getAddressesArray(); //getting the failed receivers address
            					
								sendBackTcToHash(AMSService.getFailedReceiver(SuperAgent.this, msg).getName(), arr[0]); //calling method to send the test case back to the hashmap
							} catch (FIPAException e) {
								e.printStackTrace();
							}
            			}
            		}else{
            			if(fileName.equalsIgnoreCase("Config")){ //for message where agent send its config to get a test case
            				agentName = msg.getSender().getName();
            				agentAddress = addArr[0];
            				
            				get.getTestCase(s, agentName, agentAddress, SuperAgent.this);
            			}else if(fileName.equalsIgnoreCase("Alive")){ //for message  where agents inform that its alive
            			
            			}else{
            				byte[] fileContent = msg.getByteSequenceContent(); //for message which contains report
            				
            				String getTrackID = fileName; 
            				JSONObject report = new JSONObject();
            				
            				agentName = msg.getSender().getName();
            				agentAddress = addArr[0];
            				
            				try {
            					report = new JSONObject(new String(fileContent));
            				} catch (JSONException e) {
            					e.printStackTrace();
            				}
            				gather.updateMapping(report, getTrackID, agentName, agentAddress); //calling method to update the "tcTrackHashMap" as 'Completed' and update the report
            			}
            		}
                    block();
                }
            }
        });
        //Creating thread for waiting agents to get testcase when a test case is available
        IdelThread idelThread = new IdelThread("Idel", this);
        idelThread.start();
        
        //Creating thread to check whether an agent is alive
        AgentHeartBeatTCCehck heartbeatThread = new AgentHeartBeatTCCehck("Heart", this);
        heartbeatThread.start();  
        
        //Creating thread to receive test case from cloud and store in hashmap with config
        TestCaseReceiverFinal finalCall = new TestCaseReceiverFinal("TestCaseReceive");
        finalCall.start();
        
        try {
			SampleInput.setTestCase("Firefox_Sellenium_Windows");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
//        get.getTestCase("Firefox_Sellenium_Windows", "Uvin@uvinCont", "http://CD-UWITHANA.Virtusa.com:7778/acc", this);
    }
    
    /**
     * Method to send test case to an agent for execution
     * @param jo
     * @param agentName
     * @param agentAddress
     * @param id
     * @param configId
     * @param tcID
     */
    public void sendTestCase(JSONObject jo, String agentName, String agentAddress, String id, String configId, String tcID){
    	String trackID = id + "_" + configId + "_" + tcID;
    	
    	byte[] byteArr = jo.toString().getBytes();
		
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		
		AID r = new AID(agentName, AID.ISGUID);
		r.addAddresses(agentAddress);
		
		msg.addReceiver(r);
		msg.setByteSequenceContent(byteArr);
		msg.addUserDefinedParameter("fileName", trackID);
		send(msg);
    }
    
    /**
     * Method to check whether an agent is alive by sending a message
     * @param agentName
     * @param agentAddress
     */
    public void checkConnectivity(String agentName, String agentAddress){
    	ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
    	AID r = new AID(agentName, AID.ISGUID);
    	r.addAddresses(agentAddress);
    	msg.addReceiver(r);
    	msg.setContent("check");
    	msg.addUserDefinedParameter("fileName", "Check");
    	send(msg);
    }

    /**
     * Method to send a test case back to "resolvedTestCaseHashMap" if an agent is dead who received that test case  
     * @param agentName
     * @param agentAddress
     */
    public void sendBackTcToHash(String agentName, String agentAddress){
    	ResolvedTestCase resTCLast = new ResolvedTestCase();
    	HashMap<String, TCData> lastHash = new HashMap<>();
    	ConcurrentLinkedQueue<ResolvedTestCase> res = new ConcurrentLinkedQueue<>();
    	
    	//looping through the arraylist which contains agents to find the agent needed
    	for (int i = 0; i < agentHearBeatDataArrayList.size(); i++) {
    		//Finding the agent who failed to send alive confirmation
			if((agentHearBeatDataArrayList.get(i).getAgentName().equals(agentName)) && (agentHearBeatDataArrayList.get(i).getAgentAddress().equals(agentAddress))){
				if(agentHearBeatDataArrayList.get(i).getCountFailed() < 4){
					agentHearBeatDataArrayList.get(i).setCountFailed();
				}else{
					lastHash = tcTrackHashMap.get(agentHearBeatDataArrayList.get(i).getResTC().getId()).get(agentHearBeatDataArrayList.get(i).getResTC().getConfigId());
					TCData tcObj = lastHash.get(agentHearBeatDataArrayList.get(i).getResTC().getTcId());
					tcObj.setStatus(Status.Not_Executed);
					lastHash.put(agentHearBeatDataArrayList.get(i).getResTC().getTcId(), tcObj);
					
					res = resolvedTestCaseHashMap.get(agentHearBeatDataArrayList.get(i).getAgentConfig());
					
					resTCLast.setConfigId(agentHearBeatDataArrayList.get(i).getResTC().getConfigId());
					resTCLast.setTcId(agentHearBeatDataArrayList.get(i).getResTC().getTcId());
					resTCLast.setTc(agentHearBeatDataArrayList.get(i).getResTC().getTc());
					
					res.offer(resTCLast);
					
					agentHearBeatDataArrayList.remove(i);
				}
			}
			res = new ConcurrentLinkedQueue<>();
		}
//    	get.getTestCase("Firefox_Sellenium_Windows", "Uvin@uvinCont", "http://CD-UWITHANA.Virtusa.com:7778/acc", this);
    }
}