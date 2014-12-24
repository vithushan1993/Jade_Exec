package com.objects;

/**
 * Class to store agents details in "agentHearBeatDataArrayList" arraylist who has received test case and used to check where this agent is alive
 * @author nvithushan
 *
 */
public class AgentHeartBeatData {
	private String agentName;
	private String agentAddress;
	private String agentConfig;
	private ResolvedTestCase resTC; // resolved testcase which was sent to an agent to execute
	private int countFailed = 1; //each time agent check fails count increses and when count is 4 agent is disconnected
	
	public String getAgentConfig() {
		return agentConfig;
	}
	public void setAgentConfig(String agentConfig) {
		this.agentConfig = agentConfig;
	}
	public String getAgentName() {
		return agentName;
	}
	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}
	public String getAgentAddress() {
		return agentAddress;
	}
	public void setAgentAddress(String agentAddress) {
		this.agentAddress = agentAddress;
	}
	public ResolvedTestCase getResTC() {
		return resTC;
	}
	public void setResTC(ResolvedTestCase resTC) {
		this.resTC = resTC;
	}
	public int getCountFailed() {
		return countFailed;
	}
	public void setCountFailed() {
		this.countFailed++;
	}
}
