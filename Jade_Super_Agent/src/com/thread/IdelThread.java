package com.thread;


import java.util.HashMap;

import com.objects.AgentDetails;
import com.objects.AgentHeartBeatData;
import com.objects.ResolvedTestCase;
import com.objects.Status;
import com.objects.TCData;
import com.superAgent.SuperAgent;

/**
 * Thread to check idel agents and send test case with matching config when a testcase is resolved and added to "resolvedTestCaseHashMap" hash map
 * @author nvithushan
 *
 */
public class IdelThread implements Runnable{
	public Thread t;
	private String threadName;
	SuperAgent superAgent;
	
	AgentDetails agntDet = new AgentDetails();
	AgentHeartBeatData agentHeartData = new AgentHeartBeatData();
	
	public IdelThread(String name, SuperAgent superAgent){
		threadName = name;
		this.superAgent = superAgent;
	}
	
	@Override
	public void run() {
		HashMap<String, TCData> lastHash = new HashMap<>();
		
		for(;;){	
			//for loop to loop through each agents in "agentConfigArrayList" arraylist and check whether there is a testcase with that agent's config
			for(int i = 0; i < SuperAgent.agentConfigArrayList.size();i++){
				agntDet = SuperAgent.agentConfigArrayList.get(i); 
				
				ResolvedTestCase testCase = TestCaseReceiverFinal.popTestCaseObject(agntDet.getAgentConfig()); //calling method to return "ResolvedTestCase" object if there is a test case with the config and null if there is is no test case with the given config
				
				
				if(testCase != null){
					lastHash = SuperAgent.tcTrackHashMap.get(testCase.getId()).get(testCase.getConfigId());
					TCData tcObj = lastHash.get(testCase.getTcId());
					
					tcObj.setStatus(Status.Pending_Execution);//updating the test case track map that it has been sent for execution
					lastHash.put(testCase.getTcId(), tcObj);
					
					SuperAgent.agentConfigArrayList.remove(i);
					
					agentHeartData.setAgentName(agntDet.getAgentName());
					agentHeartData.setAgentAddress(agntDet.getAgentAddress());
					agentHeartData.setResTC(testCase);
					agentHeartData.setAgentConfig(agntDet.getAgentConfig());
					
					SuperAgent.agentHearBeatDataArrayList.add(agentHeartData); //adding details to "agentHearBeatDataArrayList" arraylist to check whether the agent is alive throughout the process
					agentHeartData = new AgentHeartBeatData();

					//sending test case to agent for execution
					superAgent.sendTestCase(testCase.getTc(), agntDet.getAgentName(), agntDet.getAgentAddress(), testCase.getId(),testCase.getConfigId(), testCase.getTcId());
				}
			}
			
			try {
				Thread.sleep(1000);
			}catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Start the thread
	 */
	public void start (){
		if (t == null){
			t = new Thread (this, threadName);
			t.start ();
		}
	}
}