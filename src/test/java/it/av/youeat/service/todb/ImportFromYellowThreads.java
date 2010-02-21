package it.av.youeat.service.todb;

import it.av.youeat.ocm.model.DataRistorante;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
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
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class ImportFromYellowThreads {
	List<DataRistorante> ristos = Collections.synchronizedList(new ArrayList<DataRistorante>());
	List<String> httResults = Collections.synchronizedList(new ArrayList<String>());
	HttpClient httpclient;
	String urlToImport;
	HttpMethodParams httpMethodParams = new HttpMethodParams();
	public List<DataRistorante> runImport(int pages, String urlToImport) throws SAXException, IOException, ParserConfigurationException, TransformerException, XPathExpressionException {
		
		
		synchronized (urlToImport) {
			this.urlToImport = urlToImport;
        }
		List<Thread> threads = new ArrayList<Thread>(1902);
		List<Document> documents = Collections.synchronizedList(new ArrayList<Document>());
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.setMaxConnectionsPerHost(400);
		httpclient = new HttpClient(connectionManager);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builderD = factory.newDocumentBuilder();
		//p class="pagination-total"
		//1902
		for (int k = 1; k <= pages; k++) {
			Thread t = new Thread(new HttpLoop(k));
			t.start();
			threads.add(t);
			while(t.isAlive()){			
			}
			System.out.println("First iteration" + k);
        }
		threads.clear();
		
		for (String httResult : httResults) {
			try {
	            Document document = builderD.parse(new ByteArrayInputStream(Html2Xml.Html2Xml(httResult).getBytes()));
	            documents.add(document);
            } catch (Exception e) {
	            e.printStackTrace();
	            System.out.println("fail to import" + urlToImport);
            }
        }
		
		for (Document document : documents) {
			Thread t = new Thread(new MessageLoop(document));
			t.start();
			threads.add(t);
        }
					
		for (Thread thread : threads) {
			while (thread.isAlive()) {

			}
		}
		return ristos;
	}

	private class HttpLoop implements Runnable {
		Integer iteration;
		
		
		public HttpLoop(Integer iteration) {
			this.iteration = iteration;
		}

		public void run() {
			synchronized (this) {
				try {
	                httResults.add(runHttp());
                } catch (Exception e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
                }
			}
		}
		synchronized private String runHttp() throws HttpException, IOException{
		
			HttpMethod method = new GetMethod(urlToImport + "p-" + iteration + "?mr=30");
			httpMethodParams.setUriCharset("UTF8");
			method.setParams(httpMethodParams);
			httpclient.executeMethod(method);
			System.out.println("First iteration" + iteration);
			String result = convertStreamToString(method.getResponseBodyAsStream());
			method.releaseConnection();
			return result;
		}
	}
	private class MessageLoop implements Runnable {
		Document document;

		public MessageLoop(Document document) {
			this.document = document;
		}

		public void run() {
			synchronized (this) {
				try {
					synchronized (ristos) {
						ristos.addAll(getRisto(document));    
                    }
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
			}
		}
		
		synchronized private List<DataRistorante> getRisto(Document document) throws XPathExpressionException, HttpException, IOException{
			List<DataRistorante> ristosInt = Collections.synchronizedList(new ArrayList<DataRistorante>());

            XPathFactory xpf = XPathFactory.newInstance();
    		XPath xpathJDK = xpf.newXPath();
            XPathExpression xpathWWW = xpathJDK.compile(".//a[@class='lnkwww']");
            XPathExpression xpathTitle = xpathJDK.compile(".//h3[@class='org orange']/a");
            XPathExpression xpathTitleNoLink = xpathJDK.compile(".//h3[@class='org']");
        	XPathExpression xpathPostalCode = xpathJDK.compile(".//span[@class='postal-code']");
			XPathExpression xpathTelType = xpathJDK.compile(".//span[@class='type']");
			XPathExpression xpathLocality = xpathJDK.compile(".//span[@class='locality']");
			XPathExpression xpathProvince = xpathJDK.compile(".//span[@class='region']");
			XPathExpression xpathStreet = xpathJDK.compile(".//p[@class='street-address']");
			XPathExpression xpathTels = xpathJDK.compile(".//p[@class='tel']");
			XPathExpression xpathRisto = xpathJDK.compile(".//div[@class='listing-client-line-pg  clearfix']");
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
					//System.out.println("title: " + nodeTitle.getFirstChild().getNodeValue());
                    String name;
                    try {
	                    Node nodeTitle = (Node) xpathTitle.evaluate(nodo, XPathConstants.NODE);
	                    name = nodeTitle.getFirstChild().getNodeValue();
                    } catch (Exception e1) {
                    	Node nodeTitle = (Node) xpathTitleNoLink.evaluate(nodo, XPathConstants.NODE);
                    	name = nodeTitle.getFirstChild().getNodeValue();
                    }

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
		                	httpMethodParams.setUriCharset("UTF8");
		                	methodWWW.setParams(httpMethodParams);
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

					DataRistorante risto = new DataRistorante(name, address, postalCode, "IT", province, city, "CAFFE", www, null, new Timestamp(111111111) , phoneNumber, null, faxNumber );

	                System.out.println("risto added: " + risto.getName());
					
					synchronized (ristos) {
						ristos.add(risto);   
	                }
	                } catch (Exception e) {
		                // TODO Auto-generated catch block
		                e.printStackTrace();
	                }
		}
			return ristosInt;

		}
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
	

