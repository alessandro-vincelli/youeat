package it.av.youeat.service.todb;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

public class ImportFromYellowThreadsProvinces {
	
	public int getPages(String urlToImport) throws SAXException, IOException, ParserConfigurationException, TransformerException, XPathExpressionException {
		HttpMethodParams httpMethodParams = new HttpMethodParams();
		httpMethodParams.setUriCharset("UTF8");
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		connectionManager.setMaxConnectionsPerHost(400);
		HttpClient httpclient = new HttpClient(connectionManager);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builderD = factory.newDocumentBuilder();

		HttpMethod method = new GetMethod(urlToImport);
		method.setParams(httpMethodParams);
		httpclient.executeMethod(method);

		String result = convertStreamToString(method.getResponseBodyAsStream());
		method.releaseConnection();
		Document document = builderD.parse(new ByteArrayInputStream(Html2Xml.Html2Xml(result).getBytes()));

		XPathFactory xpf = XPathFactory.newInstance();
		XPath xpathJDK = xpf.newXPath();
		XPathExpression xpathPagination = xpathJDK.compile(".//p[@class='pagination-total']/span[@class='orange' and position() = 2]");
		xpathPagination.evaluate(document);
		Node nodePagination = (Node) xpathPagination.evaluate(document, XPathConstants.NODE);
		//lt
		try{
			Integer pages = Integer.parseInt(nodePagination.getFirstChild().getNodeValue());
			if(pages > 34){
				return 34;
			}
			return pages;
		}
		catch (Exception e) {
			System.out.println("probably only one page");
			return 1;
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
