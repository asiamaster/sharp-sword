package com.dili.ss.activiti.component;

import org.activiti.bpmn.model.BpmnModel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

public interface CustomProcessDiagramGenerator {

    InputStream generateDiagram(BpmnModel bpmnModel, String imageType, List<String> highLightedActivities, List<String> highLightedFlows, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader, double scaleFactor, Color[] colors);

    BufferedImage generatePngImage(BpmnModel bpmnModel, double scaleFactor);

    BufferedImage generatePngImage(BpmnModel bpmnModel);

    InputStream generateDiagram(BpmnModel bpmnModel, String imageType, String activityFontName, String labelFontName, String annotationFontName, ClassLoader customClassLoader);
}
