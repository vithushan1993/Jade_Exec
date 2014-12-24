package com.objects;

import org.json.JSONObject;

/**
 * Class to store status and executed report of test cases in "tcTrackHashMap" hash map 
 * @author nvithushan
 *
 */
public class TCData {
	private Status status;
	private JSONObject report;
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public JSONObject getReport() {
		return report;
	}
	public void setReport(JSONObject report) {
		this.report = report;
	}
}
