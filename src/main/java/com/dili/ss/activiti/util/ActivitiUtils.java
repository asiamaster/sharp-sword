package com.dili.ss.activiti.util;

import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.validation.ProcessValidator;
import org.activiti.validation.ProcessValidatorFactory;
import org.activiti.validation.ValidationError;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Activiti工具类
 */
public class ActivitiUtils {

    /**
     * 根据InputStream转换为BpmnModel
     * @param is
     * @return
     * @throws XMLStreamException
     */
    public static BpmnModel toBpmnModel(InputStream is) throws XMLStreamException {
        //创建转换对象
        BpmnXMLConverter converter = new BpmnXMLConverter();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(is);//createXmlStreamReader
        //将xml文件转换成BpmnModel
        return converter.convertToBpmnModel(reader);
    }

    /**
     * 根据xml转换为BpmnModel
     * @param bpmnXml
     * @return
     * @throws XMLStreamException
     */
    public static BpmnModel toBpmnModel(String bpmnXml) throws XMLStreamException {
        //创建转换对象
        BpmnXMLConverter converter = new BpmnXMLConverter();
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLStreamReader reader = factory.createXMLStreamReader(new ByteArrayInputStream(bpmnXml.getBytes()));
        //将xml文件转换成BpmnModel
        return converter.convertToBpmnModel(reader);
    }

    /**
     * BpmnModel验证
     *
     * @param bpmnModel
     * @return 返回第一条错误信息, 正确返回null
     */
    public static String validateBpmnModel(BpmnModel bpmnModel) {
        //验证bpmnModel 是否是正确的bpmn xml文件
        ProcessValidatorFactory processValidatorFactory = new ProcessValidatorFactory();
        ProcessValidator defaultProcessValidator = processValidatorFactory.createDefaultProcessValidator();
        //验证失败信息的封装ValidationError
        List<ValidationError> validate = defaultProcessValidator.validate(bpmnModel);
        return validate.isEmpty() ? null : validate.get(0).getProblem();
    }
}
