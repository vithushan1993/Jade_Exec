package com.objects;

import org.json.JSONArray;

public class ReceiveTestCase {
	private String id;
	private String ConfigID;
	private String tcID;
	private String config;
	private JSONArray testCase;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getConfigID() {
		return ConfigID;
	}
	public void setConfigID(String configID) {
		ConfigID = configID;
	}
	public String getTcID() {
		return tcID;
	}
	public void setTcID(String tcID) {
		this.tcID = tcID;
	}
	public String getConfig() {
		return config;
	}
	public void setConfig(String config) {
		this.config = config;
	}
	public JSONArray getTestCase() {
		return testCase;
	}
	public void setTestCase(JSONArray testCase) {
		this.testCase = testCase;
	}
}
