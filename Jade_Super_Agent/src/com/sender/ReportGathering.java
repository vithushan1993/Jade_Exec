package com.sender;

import java.util.HashMap;

import org.json.JSONObject;

import com.objects.Status;
import com.objects.TCData;
import com.superAgent.SuperAgent;

/**
 * Class to update data after receiving report from executing agents
 * @author nvithushan
 *
 */
public class ReportGathering {
	
	/**
	 * Updating the "tcTrackHashMap" hashmap with the report sent by the executing node
	 * @param report
	 * @param trackID
	 * @param agentname
	 * @param agentAddress
	 */
	public void updateMapping(JSONObject report, String trackID, String agentname, String agentAddress){
		HashMap<String, TCData> lastHash = new HashMap<>();
		String arr[] = trackID.split("_");
		
		String ID = arr[0].trim();
		String configID = arr[1].trim();
		String tcID = arr[2].trim();
		
//		System.out.println(ID + configID);
		lastHash = SuperAgent.tcTrackHashMap.get(ID).get(configID);
		
		TCData tcObj = lastHash.get(tcID);
		tcObj.setStatus(Status.Completed); //Updating the test case track map that te perticular test case had been executed
		tcObj.setReport(report);
		
		lastHash.put(tcID, tcObj);
		tcObj = new TCData();
		
		removeAgentDetails(agentname, agentAddress); 
	}
	
	/**
	 * Removing agent details from the "agentHearBeatDataArrayList" arraylist who has finshed the execution 
	 * @param agentName
	 * @param agentAddress
	 */
	public void removeAgentDetails(String agentName, String agentAddress){
		for (int i = 0; i < SuperAgent.agentHearBeatDataArrayList.size() ; i++) {
			if((SuperAgent.agentHearBeatDataArrayList.get(i).getAgentName().equals(agentName)) && (SuperAgent.agentHearBeatDataArrayList.get(i).getAgentAddress().equals(agentAddress))){
				SuperAgent.agentHearBeatDataArrayList.remove(i);
			}
		}
	}
}
