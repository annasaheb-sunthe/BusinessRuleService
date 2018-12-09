
package com.scb.util;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;

import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class XPathConverstion {

	public String getXPathExpressionValue(String xPathVariabeData, String payload) {
		String xpathValue = null;

		try {
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(new InputSource(new StringReader(payload)));
			doc.getDocumentElement().normalize();
			log.info("No. of Nodes in the doc: " + doc.getChildNodes().getLength());
			XPath xPath = XPathFactory.newInstance().newXPath();
			xpathValue = xPath.compile(xPathVariabeData).evaluate(doc);
		} catch (ParserConfigurationException | SAXException | IOException | XPathExpressionException exception) {
			log.info("Exception occured while parsing payload : " + exception.getMessage());
			exception.printStackTrace();
		}

		return xpathValue;
	}

}
