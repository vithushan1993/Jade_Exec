package com.sender;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class to resolve test cases sent by cloud
 * @author nvithushan
 *
 */
public class ResolvingSelFirWin {	
	public JSONObject finalObj = null;
	
	/*
	 * Resolve checkElementPresent
	 * 
	 * format: checkElementPresent(final String objectName,final String identifier, final boolean stopExecution, final Object... customError)
	 */
	public void checkElementPresent(String getObj, String identifire){
		String[] splitArr = getObj.split("\\.");
		String value = null;
		
		try{
			Element docEle = openFile("HSBC//" + splitArr[0] + ".xml");
			NodeList nl = docEle.getChildNodes();
			String[] identifireSplit = identifire.split("@");
			
			for (int i = 0; i < identifireSplit.length; i++) {
				System.out.println(identifireSplit[i]);
			}
			
			System.out.println(getPageElement(nl, splitArr[1], "seleniumwebdriver") + "    " + identifire);
			value = getResolvedSearchPath(getPageElement(nl, splitArr[1], "seleniumwebdriver"), identifire);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	/**
	 * Resolve type
	 * 
	 * @param map1
	 * @param object
	 * @param text
	 * @param m
	 * @return
	 */
	public JSONObject type(HashMap<String, HashMap<String, ArrayList<String>>> map1, String object, String text, int m){
		String[] elements = object.split("\\.");
		HashMap<String, ArrayList<String>> map2 = new HashMap<>();
		ArrayList<String> arrLis = new ArrayList<String>();
		
		try{
			JSONObject jo = new JSONObject();
			
			Element docEle = openFile("HSBC//" + elements[0] + ".xml");
			NodeList nl = docEle.getChildNodes();
			jo.put("object", getPageElement(nl, elements[1], "seleniumwebdriver"));

			if(text.charAt(0) == '@'){
				text = text.substring(1);
				
				String[] textSep = text.split("_");
				map2 = map1.get(textSep[0]);
				arrLis = map2.get(textSep[1]);
				
				jo.put("text",arrLis.get(m));
			}else{
				jo.put("text", text);
			}
			finalObj = new JSONObject();
			finalObj.put("type", jo);
		}catch(Exception e){
			e.printStackTrace();
		}
		return finalObj;
	}
	
	/**
	 * Resolve open
	 * format: open(final String objectName, final String waitTime);
	 * @param fileName
	 * @param time
	 * @return JSONObject
	 */
	public JSONObject open(String fileName, String time){
		String value = null;
		try{
			Element docEle = openFile("HSBC//" + fileName + ".xml");
			
			//value = "open(\"" + docEle.getAttributes().getNamedItem("url").getNodeValue() + "\",\"" + time + "\");";
			
			JSONObject jo = new JSONObject();
			jo.put("page", docEle.getAttributes().getNamedItem("url").getNodeValue());
			jo.put("ms", time);

			finalObj = new JSONObject();
			finalObj.put("open", jo);
		}catch(Exception e){
			e.printStackTrace();
		}
		return finalObj;
	}
	
	/**
	 * Resolve click
	 * format: click(final String objectName);
	 * @param path
	 * @returnJSONObject
	 */
	public JSONObject click(String path){
		String[] elements = path.split("\\.");
		
		try{
			Element docEle = openFile("HSBC//" + elements[0] + ".xml");
			NodeList nl = docEle.getChildNodes();
			
			JSONObject jo = new JSONObject();
			jo.put("object", getPageElement(nl, elements[1], "seleniumwebdriver"));

			finalObj = new JSONObject();
			finalObj.put("click", jo);
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return finalObj;
	}
	
	/**
	 * Opens a xml file
	 * @param filePath
	 * @return Element
	 */
	private Element openFile(String filePath){
		Element docEle = null;
		try{
			File fXmlFile = new File(filePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			docEle = doc.getDocumentElement();
		}catch(Exception e){
			e.printStackTrace();
		}
		return docEle;
	}
	
	/**
     * Gets the parameter values.
     * 
     * @param parameters
     *            the parameters
     * @return the parameter values
     */
    public static List<String> getParameterValues(final String parameters) {

        List<String> parameterValues = new ArrayList<String>();
        String[] st = parameters.split("_PARAM,");

        for (int i = 0; i < st.length; i++) {

            parameterValues.add(st[i]);

        }
        return parameterValues;

    }
    
    /**
     * Gets the resolved search path.
     * 
     * @param searchPath
     *            the search path
     * @param identifire
     *            the identifire
     * @return the resolved search path
     */
    public static String getResolvedSearchPath(final String searchPath,
            final String identifire) {
        String resolvedSearchPath = searchPath;
        List<String> parameterValues = getParameterValues(identifire);
        for (String param :  parameterValues) {
            if (!"".equals(param)) {
                resolvedSearchPath =
                        resolvedSearchPath.replace("<"
                                + param.split("_PARAM:")[0]
                                + ">",
                                param.split("_PARAM:")[1]);
            }
        }
        return resolvedSearchPath;
    }
    
    /**
	 * Read the attribute in the page XML file
	 * @param nl
	 * @param compareVal
	 * @param toolVal
	 * @return String
	 */
	public String getPageElement(NodeList nl, String compareVal, String toolVal){
		String value = null;
		
		if (nl != null && nl.getLength() > 0) {
	        for (int i = 0; i < nl.getLength(); i++) {
	            if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
	                Element ele = (Element) nl.item(i);
	                
	                switch(ele.getNodeName()){
                		case "Object":
                			if(ele.getAttributes().getNamedItem("name").getNodeValue().equals(compareVal)){
                				value = ele.getAttributes().getNamedItem(toolVal).getNodeValue();
                			}
                			break;
                		default:
                			break;
	                }
	            }
	        }
		}
		return value;
	}
	
	public JSONArray createTestCase(NodeList nl, HashMap<String, HashMap<String, ArrayList<String>>> map1, int m){
		JSONArray finalJsonArr = new JSONArray();
		JSONObject com = null;
		
		for (int i = 0; i < nl.getLength(); i++) {
			if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
				Element ele = (Element) nl.item(i);
	    
				if(!(ele.getNodeName().equals("SelectedDataTableNames"))){
					switch(ele.getNodeName()){
						case "Click":
							break;
						case "Open":
							com = open(ele.getAttributes().getNamedItem("page").getNodeValue(), ele.getAttributes().getNamedItem("ms").getNodeValue());
							break;
						case "CheckElementPresent":
							//Paused
//							c.checkElementPresent(ele.getAttributes().getNamedItem("object").getNodeValue(),ele.getAttributes().getNamedItem("Identifire").getNodeValue());
							break;
						case "Type":
							com = type(map1, ele.getAttributes().getNamedItem("object").getNodeValue(), ele.getAttributes().getNamedItem("text").getNodeValue(), m);
							break;
						default:
							break;
					}
				}
				if (com != null){
					finalJsonArr.put(com);
					com = null;
				}
			}
		}
		return finalJsonArr;
	}
}
