package com.objects;

import org.json.JSONObject;

public class FlagValue {
	private boolean flag;
	private JSONObject resTC;
	
	public boolean isFlag() {
		return flag;
	}
	public void setFlag(boolean flag) {
		this.flag = flag;
	}
	public JSONObject getResTC() {
		return resTC;
	}
	public void setResTC(JSONObject resTC) {
		this.resTC = resTC;
	}
}