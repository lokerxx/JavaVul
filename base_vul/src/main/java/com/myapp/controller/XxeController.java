package com.myapp.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import org.xml.sax.InputSource;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;


import org.jdom2.input.SAXBuilder;
import org.jdom2.Document;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;


import org.dom4j.io.SAXReader;
import org.dom4j.DocumentHelper;


import org.apache.commons.digester.Digester;


@RestController
public class XxeController {

    @PostMapping("/xxe_saxparserfactory")
    public String xxe_SAXParserFactory(@RequestBody String xmlData) throws Exception {
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser = factory.newSAXParser();
        MyHandler handler = new MyHandler();
        parser.parse(new InputSource(new StringReader(xmlData)), handler);

        // 获取解析后的字符串
        return handler.getParsedContent();
    }

    @PostMapping("/xxe_xmlreaderfactory")
    public String xxe_XMLReaderFactory(@RequestBody String xmlData) throws Exception {
        XMLReader xmlReader = XMLReaderFactory.createXMLReader();
        MyHandler handler = new MyHandler();
        xmlReader.setContentHandler(handler);
        xmlReader.parse(new InputSource(new StringReader(xmlData)));

        // 返回解析后的内容
        return handler.getParsedContent();
    }


    @PostMapping("/xxe_saxbuilder")
    public String xxe_SAXBuilder(@RequestBody String xmlData) throws Exception {
        SAXBuilder saxBuilder = new SAXBuilder(XMLReaders.NONVALIDATING);
        Document document = saxBuilder.build(new StringReader(xmlData));

        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        String xmlString = outputter.outputString(document);

        return xmlString;
    }

    @PostMapping("/xxe_saxreader")
    public String xxe_SAXReader(@RequestBody String xmlData) throws Exception {
        SAXReader reader = new SAXReader();
        org.dom4j.Document document = reader.read(new StringReader(xmlData));

        String xmlString = document.asXML();

        return xmlString;
    }

    @PostMapping("/xxe_documenthelper")
    public String xxe_DocumentHelper(@RequestBody String xmlData) throws Exception {
        org.dom4j.Document document = DocumentHelper.parseText(xmlData);
        // 对文档进行操作
        String xmlString = document.asXML();

        return xmlString;
    }

    @PostMapping("/xxe_documentbuilderfactory")
    public String xxe_DocumentBuilderFactory(@RequestBody String xmlData) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(xmlData)));
        String xmlString = getStringFromDocument(document);
        return xmlString;
    }

    @PostMapping("/xxe_documentbuilderfactory_xinclude")
    public String xxe_DocumentBuilderFactoryXInclude(@RequestBody String xmlData) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setXIncludeAware(true); //支持XInclude
        factory.setNamespaceAware(true);
        factory.setExpandEntityReferences(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        org.w3c.dom.Document document = builder.parse(new InputSource(new StringReader(xmlData)));
        String xmlString = getStringFromDocument(document);
        return xmlString;
    }




    private static String getStringFromDocument(org.w3c.dom.Document document) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(document), new StreamResult(writer));
        return writer.getBuffer().toString();
    }

}

class MyHandler extends DefaultHandler {
    private StringBuilder builder = new StringBuilder();

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        builder.append(ch, start, length);
    }

    // 提供一个方法来获取解析后的内容
    public String getParsedContent() {
        return builder.toString();
    }
}