package com.sender;

import java.util.HashMap;

import com.objects.AgentDetails;
import com.objects.AgentHeartBeatData;
import com.objects.ResolvedTestCase;
import com.objects.Status;
import com.objects.TCData;
import com.superAgent.SuperAgent;
import com.thread.TestCaseReceiverFinal;

/**
 * Class to send test cases to agents if availabe else put agents on wait
 * @author nvithushan
 */
public class GetTestCase {
	public void getTestCase(String config, String agentName, String agentAddress, SuperAgent superAgent){
		AgentDetails obj = new AgentDetails();
		HashMap<String, TCData> lastHash = new HashMap<>();
		AgentHeartBeatData agentHeartData = new AgentHeartBeatData();
		
		ResolvedTestCase testCase = TestCaseReceiverFinal.popTestCaseObject(config); //calling method to return "ResolvedTestCase" object if there is a test case with the config and null if there is is no test case with the given config
		
		if(testCase != null){
			lastHash = SuperAgent.tcTrackHashMap.get(testCase.getId()).get(testCase.getConfigId());
			TCData tcObj = lastHash.get(testCase.getTcId());
			tcObj.setStatus(Status.Pending_Execution); //updating the test case track map that it has been sent for execution
			lastHash.put(testCase.getTcId(), tcObj);
			
			agentHeartData.setAgentName(agentName);
			agentHeartData.setAgentAddress(agentAddress);
			agentHeartData.setResTC(testCase);
			agentHeartData.setAgentConfig(config);
			
			SuperAgent.agentHearBeatDataArrayList.add(agentHeartData);  //adding details to "agentHearBeatDataArrayList" arraylist to check whether the agent is alive throughout the process

			superAgent.sendTestCase(testCase.getTc(),  agentName, agentAddress, testCase.getId(), testCase.getConfigId(), testCase.getTcId());
		}else{ 
			//Putting the agents on "agentConfigArrayList" which is accessed by "idelThread" thread, waiting for a test with matching config to be resolved
			obj.setAgentName(agentName);
			obj.setAgentAddress(agentAddress);
			obj.setAgentConfig(config);
			
			SuperAgent.agentConfigArrayList.add(obj);
		}
	}
}