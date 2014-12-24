package com.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.objects.ResolvedTestCase;
import com.objects.Status;
import com.objects.TCData;
import com.objects.UnresolvedTestCases;
import com.sender.DataTable;
import com.sender.ResolvingSelFirWin;
import com.superAgent.SuperAgent;

/**
 * Thread to resolve test case and insert into "resolvedTestCaseHashMap" hash map
 * @author nvithushan
 *
 */
public class TestCaseResolveX implements Runnable {
	public Thread t;
	private String threadName;
	ConcurrentLinkedQueue<ResolvedTestCase> queue = new ConcurrentLinkedQueue<>(); // Queue to insert resolved test case
	UnresolvedTestCases unres = new UnresolvedTestCases();
	
	public TestCaseResolveX(String name, UnresolvedTestCases unres){
		threadName = name;
		this.unres = unres;
	}
	
	@Override
	public void run() {
		ResolvedTestCase res = new ResolvedTestCase();
		
		try{	
			JSONArray finalJsonArr = new JSONArray(); //to store 
			JSONObject finalJO = new JSONObject(); //to store 
			
			HashMap<String, HashMap<String, ArrayList<String>>> map1 = new HashMap<>();
			
			JSONObject jo = unres.getTestCase(); //getting testcase from the UnresolvedTestCase object
			String fileName = null;  //store the config name
			
			try {
				fileName = jo.getString("name"); //getting the config of the test case
			}catch(JSONException e1) {
				e1.printStackTrace();
			}

			try {
				File fXmlFile = new File("HSBC//" + fileName + ".xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);
				doc.getDocumentElement().normalize();
				
				Element docEle = doc.getDocumentElement();
				NodeList nl = docEle.getChildNodes();
				
				NodeList lis = doc.getElementsByTagName("SelectedDataTableNames");
				
				DataTable tab = new DataTable();
				
				List<String> myList = new ArrayList<String>();
						
				int count = 0, loopCount = 0;
				String set1 = null, set2 = null;
				HashMap<String, ArrayList<String>> val;
				ArrayList<String> val1;
				
				if (nl != null && nl.getLength() > 0) {
					for (int i = 0; i < nl.getLength(); i++) {
						if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
							Element ele = (Element) nl.item(i);
					                
							if(ele.getNodeName().equals("SelectedDataTableNames")){
								myList.add(ele.getAttributes().getNamedItem("name").getNodeValue());
								count++;
					                	
								if(count == lis.getLength()){
									String[] SelectedDataTableNames = myList.toArray(new String[myList.size()]);
									map1 = tab.joinTables(SelectedDataTableNames);
									
									Set<String> keyset= map1.keySet();
									for(String a : keyset){
										set1 = a;
									}
									val = map1.get(set1);
									
									Set<String> keyset1 = val.keySet();
									for (String b : keyset1) {
										set2 = b;
									}
									val1 = val.get(set2);
									
									loopCount = val1.size();
								}
							}
						}
					}
				}
				
				if(unres.getConfig().equals("Firefox_Sellenium_Windows")){
					if (nl != null && nl.getLength() > 0) {					
						ResolvingSelFirWin c = new ResolvingSelFirWin();
						
						HashMap<String,TCData> lastHash = new HashMap<>();
						
						for(int m = 0; m < loopCount ; m++){
							finalJsonArr = c.createTestCase(nl, map1, m);
							res.setId(unres.getId());
							res.setTcId(unres.getConfigID() + ":" + (m+1));
							res.setConfigId(unres.getConfigID());
							
							finalJO.put(unres.getConfig(), finalJsonArr);
							res.setTc(finalJO);
							
							queue = TestCaseReceiveX.resolvedTestCaseHashMap.get(unres.getConfig());
							queue.offer(res);
							
							//////////////////////////////////////////
							lastHash = SuperAgent.tcTrackHashMap.get(unres.getId()).get(unres.getConfigID());
							TCData tcObj = new TCData();
							tcObj.setStatus(Status.Not_Executed);
							lastHash.put(unres.getConfigID() + ":" + (m+1), tcObj);
							
							queue = new ConcurrentLinkedQueue<>();
							finalJO = new JSONObject();
							res = new ResolvedTestCase();
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			Thread.sleep(1000);
		}catch(InterruptedException e){
			e.printStackTrace();
		}
		TestCaseReceiveX.count--;
	}
	
	public void start (){
		if (t == null){
			t = new Thread (this, threadName);
			t.start ();
		}
	}
}