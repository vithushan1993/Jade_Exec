package com.objects;

/**
 * Class to store agent details in "agentConfigArrayList" arraylist of agents whom are waiting to receive a test case
 * @author nvithushan
 *
 */
public class AgentDetails {
	private String agentName;
	private String agentAddress;
	private String agentConfig;
	
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
	public String getAgentConfig() {
		return agentConfig;
	}
	public void setAgentConfig(String agentConfig) {
		this.agentConfig = agentConfig;
	}
}
