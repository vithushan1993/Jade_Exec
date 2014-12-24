package com.sender;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Class to join different data tables of a test case as one
 * @author nvithushan
 */
public class DataTable {
	private static int countRow = 0;

	public HashMap<String, HashMap<String, ArrayList<String>>> joinTables(String... tableName) {
		
		ArrayList<String> al = new ArrayList<String>();
		al.addAll(Arrays.asList(tableName));

		HashMap<String, HashMap<String, ArrayList<String>>> map1 = new HashMap<>();
		HashMap<String, ArrayList<String>> map2 = new HashMap<>();
		
		int countName = 0;

		try {
			File fXmlFile = new File("HSBC//DataTables.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			Element docEle = doc.getDocumentElement();
			NodeList nl = docEle.getChildNodes();

			if (nl != null && nl.getLength() > 0) {
				for (int i = 0; i < nl.getLength(); i++) {
					if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element ele = (Element) nl.item(i);

						switch (ele.getNodeName()) {
						case "Table":
							if (al.contains(ele.getAttributes().getNamedItem("name").getNodeValue())) {

								NodeList nl2 = ele.getChildNodes();

								NodeList rowList = ele.getElementsByTagName("Row");
								if (countRow < rowList.getLength()) {
									countRow = rowList.getLength();
								}

								if (nl2 != null && nl2.getLength() > 0) {
									for (int j = 0; j < nl2.getLength(); j++) {
										if (nl2.item(j).getNodeType() == Node.ELEMENT_NODE) {
											Element ele2 = (Element) nl2.item(j);

											switch (ele2.getNodeName()) {
											case "Header":
												NodeList lis3 = ele2.getChildNodes();
												countName = lis3.getLength();
												break;
											default:
												break;
											}
										}
									}
								}
								ArrayList<String> mapArrayList;
								if (nl2 != null && nl2.getLength() > 0) {
									for (int k = 0; k < countName; k++) {
										String headName = null;
										mapArrayList  = new ArrayList<String>();
										for (int j = 0; j < nl2.getLength(); j++) {
											if (nl2.item(j).getNodeType() == Node.ELEMENT_NODE) {
												Element ele2 = (Element) nl2.item(j);

												NodeList abc = ele2.getChildNodes();

												switch (ele2.getNodeName()) {
												case "Header":
													if (nl2.item(k).getNodeType() == Node.ELEMENT_NODE) {
														Element column = (Element) abc.item(k);
														headName = column.getAttributes().getNamedItem("name").getNodeValue();
													}
													break;
												case "Row":
													abc = ele2.getChildNodes();
													if (nl2.item(k).getNodeType() == Node.ELEMENT_NODE) {
														Element row = (Element) abc.item(k);
														mapArrayList.add(row.getTextContent());
													}
													break;
												default:
													break;
												}
											}
										}
										if (headName != null) {
											if(mapArrayList.size() != countRow && mapArrayList.size() < countRow){
												for (int bread = 0; bread < countRow; bread++) {
													if((bread + 1) > mapArrayList.size()){
														mapArrayList.add(null);
													}
												}
											}
											map2.put(headName, mapArrayList);
										}
									}
								}								
								HashMap<String, ArrayList<String>> cloned = (HashMap <String, ArrayList<String>>)map2.clone();
								
								map1.put(ele.getAttributes().getNamedItem("name").getNodeValue(), cloned);
								map2.clear();
							}
							break;
						default:
							break;
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map1;
	}
}
