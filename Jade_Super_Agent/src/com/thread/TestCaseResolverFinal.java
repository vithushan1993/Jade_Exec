package com.thread;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.objects.FlagValue;
import com.objects.ReceiveTestCase;
import com.objects.ResolvedTestCase;
import com.objects.Status;
import com.objects.TCData;
import com.sender.ResolveFireSelWin;
import com.superAgent.SuperAgent;


public class TestCaseResolverFinal implements Runnable {
	public Thread t;
	private String threadName;
	ReceiveTestCase receviedTC;
	ResolveFireSelWin resolve = new ResolveFireSelWin();
	
	FlagValue falgValue = new FlagValue();
	ResolvedTestCase resTC = new ResolvedTestCase();
	
	public TestCaseResolverFinal(String name, ReceiveTestCase receviedTC){
		threadName = name;
		this.receviedTC = receviedTC;
	}
	
	@Override
	public void run() {
		JSONObject com = null;
		JSONArray finalJsonArr = new JSONArray();
		JSONObject finalJO = new JSONObject();
		ConcurrentLinkedQueue<ResolvedTestCase> queue = new ConcurrentLinkedQueue<>(); // Queue to insert resolved test case
		HashMap<String,TCData> lastHash = new HashMap<>();
		
		if(receviedTC.getConfig().equals("Firefox_Sellenium_Windows")){
			try {
				JSONArray ja = receviedTC.getTestCase();

				boolean flag;
				
				int iterator  = 0;
				do{
					flag = false;
					
					for(int i = 0; i < ja.length(); i++){
//						try {
//							if(ja.getJSONObject(i).getJSONObject("expression").has("opencommand")){
//								
//							}else if(ja.getJSONObject(i).getJSONObject("expression").has("typecommand")){
//								
//							}
//						} catch (JSONException e) {
//							e.printStackTrace();
//						}
						
						try {
							switch (ja.getJSONObject(i).getJSONObject("command").get("command").toString()) {
								case "Open":
									com = resolve.open(ja.getJSONObject(i).getJSONArray("testParameter"));
									break;
								case "Type":
									falgValue = resolve.type(ja.getJSONObject(i).getJSONArray("testParameter"), iterator, flag);
									com = falgValue.getResTC();
									if(falgValue.isFlag()){
										flag = true;
									}
									break;
								case "Click":   /////////////////////////
									com = resolve.click(ja.getJSONObject(i).getJSONArray("testParameter"));
									break;
								case "CheckElementPresent":
									com = resolve.checkElementPresent(ja.getJSONObject(i).getJSONArray("testParameter")); 
									break;
								case "KeyPress":
									com = resolve.keyPress(ja.getJSONObject(i).getJSONArray("testParameter"));
									break;
								case "GetObjectCount":
									com = resolve.getObjectCount(ja.getJSONObject(i).getJSONArray("testParameter"));
									break;
								case "Comment":
									com = resolve.comment(ja.getJSONObject(i).getJSONArray("testParameter"));
									break;
								case "Pause":
									com = resolve.pause(ja.getJSONObject(i).getJSONArray("testParameter"));
									break;
								case "SelectFrame":
									com = resolve.selectFrame(ja.getJSONObject(i).getJSONArray("testParameter"));
									break;
								case "NavigateToURL":
									com = resolve.navigateToURL(ja.getJSONObject(i).getJSONArray("testParameter"));
									break;
								case "DoubleClick":
									com = resolve.doubleClick(ja.getJSONObject(i).getJSONArray("testParameter"));
									break;
								default:
									break;
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						if (com != null){
							finalJsonArr.put(com);
							com = null;
						}
						lastHash = SuperAgent.tcTrackHashMap.get(receviedTC.getId()).get(receviedTC.getConfigID());
						TCData tcObj = new TCData();
						tcObj.setStatus(Status.Not_Executed);
						receviedTC.setTcID(receviedTC.getConfigID() + ":" + (i+1));
						lastHash.put(receviedTC.getTcID(), tcObj);
					}
						
					try {
						finalJO.put(receviedTC.getConfig(), finalJsonArr);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					resTC.setTc(finalJO);
					resTC.setId(receviedTC.getId());
					resTC.setConfigId(receviedTC.getConfigID());
					resTC.setTcId(receviedTC.getTcID());
						
					SuperAgent.resolvedTestCaseHashMap.get(receviedTC.getConfig()).offer(resTC);
					
					queue = new ConcurrentLinkedQueue<>();
					resTC = new ResolvedTestCase();
					finalJsonArr = new JSONArray();
						
					iterator++;
					finalJO = new JSONObject();
				}while(flag == true);
			}finally{
				
			}
//			catch (JSONException e) {
//				e.printStackTrace();
//			}
		}
		TestCaseReceiverFinal.count--;
	}
	
	public void start (){
		if (t == null){
			t = new Thread (this, threadName);
			t.start ();
		}
	}
}
