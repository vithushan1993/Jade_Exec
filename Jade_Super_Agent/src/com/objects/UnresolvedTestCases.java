package com.objects;

import org.json.JSONObject;

/**
 * Class to store unresolved testcases in "testCaseQueue" concurrentQueue
 * @author nvithushan
 *
 */
public class UnresolvedTestCases {
	private String config;
	private String tcID;
	private JSONObject testCase;
	private String ConfigID;
	
	public String getConfigID() {
		return ConfigID;
	}
	public void setConfigID(String configID) {
		ConfigID = configID;
	}
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	public String getId() {
		return tcID;
	}
	public void setId(String id) {
		this.tcID = id;
	}
	public JSONObject getTestCase() {
		return testCase;
	}
	public void setTestCase(JSONObject testCase) {
		this.testCase = testCase;
	}
}
