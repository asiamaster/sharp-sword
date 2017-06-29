package com.dili.ss.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.ss.util.ExportUtils;
import com.dili.ss.domain.ExportParam;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 导出
 * Created by asiamaster on 2017/5/27 0027.
 */
@Controller
@RequestMapping("/export")
public class ExportController {

    public final static Logger log = Logger.getLogger(ExportController.class);



    /**
     * 服务端导出
     *
     * @param request
     * @param response
     * @param columns
     * @param queryParams
     * @param title
     */
    @RequestMapping("/serverExport")
    public @ResponseBody void serverExport(HttpServletRequest request, HttpServletResponse response,
                      @RequestParam("columns") String columns,
                      @RequestParam("queryParams") String queryParams,
                      @RequestParam("title") String title,
                      @RequestParam("url") String url) {
        try {
            ExportUtils.export(request, response, buildExportParam(columns, queryParams, title, url));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private ExportParam buildExportParam(String columns, String queryParams, String title, String url){
        ExportParam exportParam = new ExportParam();
        exportParam.setTitle(title);
        exportParam.setQueryParams((Map) JSONObject.parseObject(queryParams));
        exportParam.setColumns((List)JSONArray.parseArray(columns).toJavaList(List.class));
        exportParam.setUrl(url);
        return exportParam;
    }


}
