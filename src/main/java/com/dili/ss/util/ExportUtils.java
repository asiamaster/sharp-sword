package com.dili.ss.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.http.okhttp.OkHttpUtils;
import com.dili.ss.domain.ExportParam;
import com.dili.ss.domain.TableHeader;
import com.dili.ss.metadata.ValueProvider;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 通用导出工具
 * Created by asiamaster on 2017/6/15 0015.
 */
public class ExportUtils {

    public final static Logger log = Logger.getLogger(ExportUtils.class);

    static OkHttpClient okHttpClient = null;
    static{
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     *  通用beans导出方法
     * @param response
     * @param title 标题/文件名
     * @param tableHeaders   表头，使用List是为了排序
     * @param beans 数据列表,Bean集合
     * @param providerMeta provider的metadata, key为字段名， value为provider的beanId，如果不需要转义，则为null
     */
    public static void exportBeans(HttpServletResponse response, String title, List<TableHeader> tableHeaders, List beans, Map providerMeta) throws Exception {
        List<Map> datas = new ArrayList<>(beans.size());
        for(Object bean : beans){
            Map map = BeanConver.transformObjectToMap(bean);
            datas.add(map);
        }
        exportMaps(response, title, tableHeaders, datas, providerMeta);
    }

    /**
     *  通用maps导出方法
     * @param response
     * @param title 标题/文件名
     * @param tableHeaders   表头，使用List是为了排序
     * @param datas 数据列表,Map集合
     * @param providerMeta provider的metadata, key为字段名， value为provider的beanId，如果不需要转义，则为null
     */
    public static void exportMaps(HttpServletResponse response, String title, List<TableHeader> tableHeaders, List<Map> datas, Map providerMeta){
        HSSFWorkbook workbook = new HSSFWorkbook();                     // 创建工作簿对象
        HSSFSheet sheet = workbook.createSheet(title);

        //构建表头
        HSSFCellStyle columnTopStyle = getHeaderColumnStyle(workbook);  //获取列头样式对象
        HSSFRow headerRow = sheet.createRow(0);
        for (int j = 0; j < tableHeaders.size(); j++) {
            //列头信息
            TableHeader tableHeader = tableHeaders.get(j);
            HSSFCell cell = headerRow.createCell(j);               //创建列头对应个数的单元格
            cell.setCellType(CellType.STRING);             //设置列头单元格的数据类型
            HSSFRichTextString text = new HSSFRichTextString(tableHeader.getTitle().replaceAll("\\n", "").trim());
            cell.setCellValue(text);                                 //设置列头单元格的值
            cell.setCellStyle(columnTopStyle);                       //设置列头单元格样式
        }
        //构建数据
        //渲染数据列
        HSSFCellStyle dataColumnStyle = getDataColumnStyle(workbook);//获取列头样式对象
        //用于缓存providerBean
        Map<String, ValueProvider> providerBuffer = new HashMap<>();
        //迭代数据
        for(int i=0; i<datas.size(); i++ ){
            Map rowDataMap = datas.get(i);
            HSSFRow dataRow = sheet.createRow(i+1);
            //迭代列头
            for(int j=0; j<tableHeaders.size(); j++){
                TableHeader tableHeader = tableHeaders.get(j);
                HSSFCell cell = dataRow.createCell(j);
                cell.setCellStyle(dataColumnStyle);
                Object value = rowDataMap.get(tableHeader.getField());
                if(providerMeta != null && providerMeta.containsKey(tableHeader.getField())){
                    ValueProvider valueProvider = null;
                    //value是provider的beanId
                    String providerBeanId = (String)providerMeta.get(tableHeader.getField());
                    if(providerBuffer.containsKey(providerBeanId)){
                        valueProvider = providerBuffer.get(providerBeanId);
                    }else {
                        valueProvider = SpringUtil.getBean(providerBeanId, ValueProvider.class);
                        providerBuffer.put(providerBeanId, valueProvider);
                    }
                    cell.setCellValue(valueProvider.getDisplayText(value, null, null));
                }else {
                    cell.setCellValue(value == null ? null : value.toString());
                }
            }
        }
        //执行导出
        write(title, workbook, response);
    }

    /**
     * 根据ExportParam导出数据<br></>
     * 用于ExportController的导出datagrid表格
     */
    public static void export(HttpServletRequest request, HttpServletResponse response, ExportParam exportParam) throws Exception {
        try {
            HSSFWorkbook workbook = new HSSFWorkbook();                     // 创建工作簿对象
            HSSFSheet sheet = workbook.createSheet(exportParam.getTitle());
            //构建表头
            buildHeader(exportParam, workbook, sheet);
            //构建数据
            buildData(exportParam, workbook, sheet, request);
            //执行导出
            write(exportParam.getTitle(), workbook, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 构建数据列
     * @param exportParam
     * @param workbook
     * @param sheet
     */
    private static void buildData(ExportParam exportParam, HSSFWorkbook workbook, HSSFSheet sheet, HttpServletRequest request){
        //渲染数据列
        HSSFCellStyle dataColumnStyle = getDataColumnStyle(workbook);//获取列头样式对象
        //这里获取到的是nginx转换后的(IP)地址和端口号，如果是跳板机这种，有可能会禁止访问，后续改为从配置读取
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+request.getContextPath();
        String json = syncExecute(basePath+exportParam.getUrl(), exportParam.getQueryParams(), "POST");
        JSONArray rowDatas = (JSONArray) JSON.parseObject(json).get("rows");
        Integer headerRowCount = exportParam.getColumns().size();
        //迭代数据
        for(int i=0; i<rowDatas.size(); i++ ){
            JSONObject rowDataMap = (JSONObject)rowDatas.get(i);
            HSSFRow row = sheet.createRow(i+headerRowCount);
            //直接取最后一行的列头信息
            List<Map<String, Object>> headers = exportParam.getColumns().get(exportParam.getColumns().size()-1);
            //迭代列头
            for(int j=0; j<headers.size(); j++){
                Map<String, Object> headerMap = headers.get(j);
                HSSFCell cell = row.createCell(j);
                cell.setCellStyle(dataColumnStyle);
                Object value = rowDataMap.get(headerMap.get("field"));
                //判断是否有值提供者需要转义(此功能已经在datagrid的查询中封装，这里不需要处理了)
//                if(headerMap.containsKey("provider")){
//                    value = valueProviderUtils.getDisplayText(headerMap.get("provider").toString(), value, null);
//                }
                cell.setCellValue(value == null ? null : value.toString());
            }
        }
    }

    /**
     * 构建表头列, 只支持colspan，不支持rowspan，因为rowspan无法确定是向上还是向下合并,判断的因素太多，暂不支持
     * @param exportParam
     * @param workbook
     * @param sheet
     */
    private static void buildHeader(ExportParam exportParam, HSSFWorkbook workbook, HSSFSheet sheet){
        HSSFCellStyle columnTopStyle = getHeaderColumnStyle(workbook);//获取列头样式对象

        //渲染复合表头列
        for (int i = 0; i < exportParam.getColumns().size(); i++) {
            //每行的列信息
            List<Map<String, Object>> rowColumns = exportParam.getColumns().get(i);
            HSSFRow row = sheet.createRow(i);

            int colspanAdd = 0;
            for (int j = 0; j < rowColumns.size(); j++) {
                //列头信息
                Map<String, Object> columnMap = rowColumns.get(j);
                HSSFCell cell = row.createCell(j+colspanAdd);               //创建列头对应个数的单元格
                cell.setCellType(CellType.STRING);             //设置列头单元格的数据类型
                HSSFRichTextString text = new HSSFRichTextString(columnMap.get("title").toString().replaceAll("\\n", "").trim());
                cell.setCellValue(text);                                 //设置列头单元格的值
                cell.setCellStyle(columnTopStyle);                       //设置列头单元格样式
                if(columnMap.get("colspan") != null) {
                    Integer colspan = Integer.class.isAssignableFrom(columnMap.get("colspan").getClass())? (Integer)columnMap.get("colspan") : Integer.parseInt(columnMap.get("colspan").toString());
                    if(colspan>1) {
                        HSSFCell tempCell = row.createCell(j + colspanAdd + colspan - 1);               //创建合并最后一列的列头，保证最后一列有右边框
                        tempCell.setCellStyle(columnTopStyle);
                        sheet.addMergedRegion(new CellRangeAddress(i, i, j + colspanAdd, j + colspanAdd + colspan - 1));
                        colspanAdd = colspanAdd + colspan - 1;
                    }
                }
            }
        }
    }

    /**
     * 同步调用远程方法
     * @param url
     * @param paramObj
     * @param httpMethod
     */
    private static String syncExecute(String url, Object paramObj, String httpMethod){
        Response resp = null;
        try{
            Map<String, String> headersMap = new HashMap<>();
            headersMap.put("Content-Type", "application/json;charset=utf-8");

            if("POST".equalsIgnoreCase(httpMethod)){
                JSONObject jo = (JSONObject)JSONObject.toJSON(paramObj);
//                FormBody.Builder builder = new FormBody.Builder();
//                if (jo!=null&&!jo.isEmpty()){
//                    for(Map.Entry<String,Object> entry:jo.entrySet()){
//                        String value = entry.getValue() == null ? null : entry.getValue().toString();
//                        builder.add(entry.getKey(),value);
//                    }
//                }
//                RequestBody requestBody = builder.build();
//                String json = paramObj instanceof String ? (String)paramObj : JSON.toJSONString(paramObj);
                Map<String, String> param = new HashMap<>();
                if (jo!=null&&!jo.isEmpty()){
                    for(Map.Entry<String,Object> entry:jo.entrySet()){
                        if(entry.getValue() instanceof JSONObject){
                            JSONObject valueJo = (JSONObject)entry.getValue();
                            //解决spring mvc参数注入@ModelAttribute Domain domain时，metadata作为Map类型的注入问题
                            for(Map.Entry<String,Object> tmpEntry:valueJo.entrySet()) {
                                String value = tmpEntry.getValue() == null ? null : tmpEntry.getValue().toString();
                                param.put(entry.getKey() + "[" +tmpEntry.getKey()+"]", value);
                            }
                        }else {
                            String value = entry.getValue() == null ? null : entry.getValue().toString();
                            param.put(entry.getKey(),value);
                        }
                    }
                }
                resp = OkHttpUtils
                        .post()
                        .url(url).params(param)
//                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                        .mediaType(MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8"))
                        .build()
                        .execute();
            }else{
                resp = OkHttpUtils
                        .get()
                        .url(url).params((Map)JSON.toJSON(paramObj))
                        .build()
                        .execute();
            }
            if(resp.isSuccessful()){
//                log.info(String.format("远程调用["+url+"]成功,code:[%s], message:[%s]", resp.code(),resp.message()));
                return resp.body().string();
            }else{
                log.error(String.format("远程调用["+url+"]发生失败,code:[%s], message:[%s]", resp.code(),resp.message()));
                return resp.body().string();
            }
        } catch (Exception e) {
            log.error(String.format("远程调用["+url+"]发生异常,code:[%s], message:[%s]", resp.code(),resp.message()));
        }
        return null;
    }

    /**
     * 执行导出
     * @param title
     * @param workbook
     * @param response
     */
    private static void write(String title, HSSFWorkbook workbook, HttpServletResponse response){
        if (workbook != null) {
            try {
                String fileNameDownload = title + ".xls";

                response.setHeader("Content-Disposition", "attachment;filename=\"" + URLEncoder.encode(fileNameDownload, "utf-8") + "\"");
                //this.getResponse().setContentType("application/x-xls;charset=UTF-8");
                response.setCharacterEncoding("utf-8");
                response.setContentType("application/vnd.ms-excel;charset=UTF-8");
                OutputStream out = response.getOutputStream();
                workbook.write(out);
                out.close();
//                    OutputStream out = response.getOutputStream();
//                    response.reset();
//                    response.setHeader("Content-disposition", "attachment; filename=details.xls");
//                    response.setContentType("application/msexcel");
//                    workbook.write(out);
//                    out.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * 列头单元格样式
     */
    private static HSSFCellStyle getHeaderColumnStyle(HSSFWorkbook workbook) {
        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 11);
        //字体加粗
        font.setBold(true);
        //字体颜色
        font.setColor(HSSFColor.ROYAL_BLUE.index);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置前景色样式
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置前景色
        style.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        return style;
    }

    /*
     * 列数据信息单元格样式
     */
    private static HSSFCellStyle getDataColumnStyle(HSSFWorkbook workbook) {
        // 设置字体
        HSSFFont font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short)10);
        //字体加粗
        //font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        HSSFCellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(HSSFColor.BLACK.index);
        //设置左边框;
        style.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(HSSFColor.BLACK.index);
        //设置右边框;
        style.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
        style.setRightBorderColor(HSSFColor.BLACK.index);
        //设置顶边框;
        style.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(HSSFColor.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置自动换行;
        style.setWrapText(false);
        //设置水平对齐的样式为居中对齐;
        style.setAlignment(HorizontalAlignment.CENTER);
        //设置垂直对齐的样式为居中对齐;
        style.setVerticalAlignment(VerticalAlignment.CENTER);

        return style;
    }
}
