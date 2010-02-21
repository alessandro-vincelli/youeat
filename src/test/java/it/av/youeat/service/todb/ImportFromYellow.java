package it.av.youeat.service.todb;

import it.av.youeat.ocm.model.DataRistorante;
import it.av.youeat.ocm.util.DateUtil;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ImportFromYellow {
	
	
	
	synchronized public List<DataRistorante> runImport(int k) throws SAXException, IOException, ParserConfigurationException, TransformerException, XPathExpressionException, URISyntaxException {
		List<DataRistorante> ristos = Collections.synchronizedList(new ArrayList<DataRistorante>());
			
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.setMaxConnectionsPerHost(400);
		HttpClient httpclient = new HttpClient(connectionManager);
		httpclient.setTimeout(10000);
		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpathJDK = xpf.newXPath();
        XPathExpression xpathWWW = xpathJDK.compile(".//a[@class='lnkwww']");
        XPathExpression xpathTitle = xpathJDK.compile(".//h3[@class='org orange']/a");
    	XPathExpression xpathPostalCode = xpathJDK.compile(".//span[@class='postal-code']");
		XPathExpression xpathTelType = xpathJDK.compile(".//span[@class='type']");
		XPathExpression xpathLocality = xpathJDK.compile(".//span[@class='locality']");
		XPathExpression xpathProvince = xpathJDK.compile(".//span[@class='region']");
		XPathExpression xpathStreet = xpathJDK.compile(".//p[@class='street-address']");
		XPathExpression xpathTels = xpathJDK.compile(".//p[@class='tel']");
		XPathExpression xpathRisto = xpathJDK.compile(".//div[@class='listing-client-line-pg  clearfix']");

		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builderD = factory.newDocumentBuilder();
		//1902
			System.out.println("http://www.paginegialle.it/pgol/1-007585100/4-ristoranti/l-1/p-" + k + "?mr=30");
			HttpMethod method = new GetMethod("http://www.paginegialle.it/pgol/1-007585100/4-ristoranti/l-1/p-" + k + "?mr=30");
			httpclient.executeMethod(method);
			String result = convertStreamToString(method.getResponseBodyAsStream());
			method.releaseConnection();
			Document document = builderD.parse(new ByteArrayInputStream(Html2Xml.Html2Xml(result).getBytes()));
			
			// Use a Transformer for output
			//TransformerFactory tFactory = TransformerFactory.newInstance();
			//Transformer transformer = tFactory.newTransformer();
			//DOMSource source = new DOMSource(document);
			//StreamResult result = new StreamResult(System.out);
			//transformer.transform(source, result);
			NodeList list = (NodeList) xpathRisto.evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < list.getLength(); i++) {
				try {

	            
				Node nodo = list.item(i);
				Node nodeTitle = (Node) xpathTitle.evaluate(nodo, XPathConstants.NODE);
				//System.out.println("title: " + nodeTitle.getFirstChild().getNodeValue());
				String name = nodeTitle.getFirstChild().getNodeValue();

				Node nodeCAP = (Node) xpathPostalCode.evaluate(nodo, XPathConstants.NODE);
				//System.out.println("CAP: " + nodeCAP.getFirstChild().getNodeValue());
				String postalCode = nodeCAP.getFirstChild().getNodeValue();

				Node nodeLocality = (Node) xpathLocality.evaluate(nodo, XPathConstants.NODE);
				//System.out.println("City: " + nodeLocality.getFirstChild().getNodeValue());
				String city = nodeLocality.getFirstChild().getNodeValue();

				Node nodeProvince = (Node) xpathProvince.evaluate(nodo, XPathConstants.NODE);
				//System.out.println("Province: " + nodeProvince.getFirstChild().getNodeValue());
				String province = StringUtils.substring(nodeProvince.getFirstChild().getNodeValue(), 1, 3);

				Node nodeStreet = (Node) xpathStreet.evaluate(nodo, XPathConstants.NODE);
				//System.out.println("Street: " + nodeStreet.getFirstChild().getNodeValue());
				String address = nodeStreet.getFirstChild().getNodeValue();

				NodeList nodeTels = (NodeList) xpathTels.evaluate(nodo, XPathConstants.NODESET);
				String phoneNumber = null;
				String faxNumber = null;
				for (int j = 0; j < nodeTels.getLength(); j++) {
					Node tel = nodeTels.item(j);
					Node nodeTelType = (Node) xpathTelType.evaluate(tel, XPathConstants.NODE);
					//System.out.println("Tel: " + nodeTelType.getFirstChild().getNodeValue() + " " + tel.getLastChild().getNodeValue());
					if (nodeTelType.getFirstChild().getNodeValue().trim().equalsIgnoreCase("tel:")) {
						phoneNumber = tel.getLastChild().getNodeValue();
					}
					if (nodeTelType.getFirstChild().getNodeValue().trim().equalsIgnoreCase("fax:")) {
						faxNumber = tel.getLastChild().getNodeValue();
					}
				}
				
				Node nodeWWW = (Node) xpathWWW.evaluate(nodo, XPathConstants.NODE);
				String www = null;
				try {
	                if (nodeWWW != null) {
	                	HttpMethod methodWWW = new GetMethod(nodeWWW.getAttributes().getNamedItem("href").getNodeValue());
	                	methodWWW.setFollowRedirects(true);
	                	httpclient.executeMethod(methodWWW);
	                	//methodWWW.getResponseBody();
	                	//System.out.println("www: " + methodWWW.getURI());
	                    www = methodWWW.getURI().toString();
	                    methodWWW.releaseConnection();
	                }
                } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }

				DataRistorante risto = new DataRistorante(name, address, postalCode, "IT", province, city, null, www, null, DateUtil.getTimestamp() , phoneNumber, null, faxNumber );

                System.out.println("risto added: " + risto.getName());
				
				//System.out.println("Iteration: " + k);
				synchronized (ristos) {
					ristos.add(risto);   
                }
                } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }
           }
			
        
		return ristos;
	}
	public String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine() method. We iterate until the BufferedReader return null
		 * which means there's no more data to read. Each line will appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}
