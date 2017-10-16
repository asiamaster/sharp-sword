package com.dili.ss.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * dom4j xml工具
 * Created by asiamaster on 2017/9/27 0027.
 */
public class XmlUtils {
	protected static final Logger log = LoggerFactory.getLogger(XmlUtils.class);

	/**
	 * 根据地址获得一个Document(XML文件)
	 * @param path 地址
	 * @return Docuyment
	 */
	public static Document File2Document(String path) {
		SAXReader reader = new SAXReader();
		Document document = null;
		try {
			document = reader.read(path);
		} catch (DocumentException e) {
			log.error(e.getMessage());
		}
		return document;
	}

	/**
	 * Document转成文件
	 * @param doc
	 * @param path
	 */
	public static void Document2File(Document doc,
	                                 String path) {
		OutputFormat format = new OutputFormat("", true);
		format.setEncoding("UTF-8");//设置编码格式
		try {
			XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(path), format);
			xmlWriter.write(doc);
			xmlWriter.close();
		} catch (IOException e) {
			log.error(e.getMessage());
		}
	}


	/**
	 * doc转string
	 * @param document
	 * @return
	 */
	public static String Document2String(Document document) {
		String s = "";

		// 使用输出流来进行转化
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// 使用UTF-8编码
		OutputFormat format = new OutputFormat("", true, "UTF-8");
		try {
			XMLWriter writer = new XMLWriter(out, format);
			writer.write(document);
			s = out.toString("UTF-8");
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		return s;
	}

	/**
	 * 字符串转Document
	 * @param xml
	 * @return
	 */
	public static Document String2Document(String xml) {
		try {
			return DocumentHelper.parseText(xml);
		} catch (DocumentException e) {
			log.error(e.getMessage());
		}
		return null;
	}

//	public static void main(String[] args) {
//		//创建document
//		Document document = DocumentHelper.createDocument();
//		//创建根元素
//		Element root = document.addElement("root");
//		//添加子节点1和设置属性
//		Element auther1 = root.addElement("auther")
//				.addAttribute("name", "James")
//				.addAttribute("location", "UK")
//				.addText("James SXtrachan");
//		//添加子节点2和设置属性
//		Element auther2 = root.addElement("auther")
//				.addAttribute("name", "Bob")
//				.addAttribute("location", "US")
//				.addText("James McWhirter");
//		Document2File(document, "d:/test.xml");
//			System.out.println(Document2String(document));
//		Document doc1 = File2Document("d:/test.xml");
//		String xml = Document2String(document);
//		Document doc = String2Document(xml);
//		System.out.println(Document2String(doc));
//	}
}
