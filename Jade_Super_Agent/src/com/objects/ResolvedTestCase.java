package com.objects;

import org.json.JSONObject;

/**
 * Class to store details of resolved test cases in "resolvedTestCaseHashMap" heash map 
 * @author nvithushan
 *
 */
public class ResolvedTestCase {
	private String id; 			//id of the testcase recieved from cloud
	private String configId;	//id of the testcase after diving into different testcase according to the config
	private String tcId;		//id of the testcase after diving into different testcase according to the data in the data tables
	private JSONObject tc;		//resolved testcase
	
	public String getConfigId() {
		return configId;
	}
	public void setConfigId(String configId) {
		this.configId = configId;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTcId() {
		return tcId;
	}
	public void setTcId(String tcId) {
		this.tcId = tcId;
	}
	public JSONObject getTc() {
		return tc;
	}
	public void setTc(JSONObject tc) {
		this.tc = tc;
	}
}
