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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 通用导出工具
 * Created by asiamaster on 2017/6/15 0015.
 */
public class ExportUtils {

    public final static Logger log = Logger.getLogger(ExportUtils.class);

    //每次去后台获取条数
    private final static int FETCH_COUNT = 10000;
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
        SXSSFWorkbook workbook = new SXSSFWorkbook(FETCH_COUNT);                     // 创建工作簿对象
        Sheet sheet = workbook.createSheet(title);

        //构建表头
        CellStyle columnTopStyle = getHeaderColumnStyle(workbook);  //获取列头样式对象
        Row headerRow = sheet.createRow(0);
        for (int j = 0; j < tableHeaders.size(); j++) {
            //列头信息
            TableHeader tableHeader = tableHeaders.get(j);
            Cell cell = headerRow.createCell(j);               //创建列头对应个数的单元格
            cell.setCellType(CellType.STRING);             //设置列头单元格的数据类型
            RichTextString text = new XSSFRichTextString(tableHeader.getTitle().replaceAll("\\n", "").trim());
            cell.setCellValue(text);                                 //设置列头单元格的值
            cell.setCellStyle(columnTopStyle);                       //设置列头单元格样式
        }
        //构建数据
        //渲染数据列
        CellStyle dataColumnStyle = getDataColumnStyle(workbook);//获取列头样式对象
        //用于缓存providerBean
        Map<String, ValueProvider> providerBuffer = new HashMap<>();
        //迭代数据
        for(int i=0; i<datas.size(); i++ ){
            Map rowDataMap = datas.get(i);
            Row dataRow = sheet.createRow(i+1);
            //迭代列头
            for(int j=0; j<tableHeaders.size(); j++){
                TableHeader tableHeader = tableHeaders.get(j);
                Cell cell = dataRow.createCell(j);
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
//            HSSFWorkbook workbook = new HSSFWorkbook();
            SXSSFWorkbook workbook = new SXSSFWorkbook(FETCH_COUNT);// 创建工作簿对象
            Sheet sheet = workbook.createSheet(exportParam.getTitle());
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
    private static void buildData(ExportParam exportParam, SXSSFWorkbook workbook, Sheet sheet, HttpServletRequest request){
        //渲染数据列
        CellStyle dataColumnStyle = getDataColumnStyle(workbook);//获取列头样式对象
        //这里获取到的是nginx转换后的(IP)地址和端口号，如果是跳板机这种，有可能会禁止访问，后续改为从配置读取
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
        String url = exportParam.getUrl().startsWith("/") ? exportParam.getUrl() : "/" + exportParam.getUrl();
        Map<String, String> queryParams = exportParam.getQueryParams();
        //先获取总数
        int total = getCount(basePath+url, exportParam.getQueryParams());
        //查询次数
        int queryCount = total % FETCH_COUNT == 0 ? total/ FETCH_COUNT : total/ FETCH_COUNT +1;
        //分别进行取数
        for(int current=0; current<queryCount; current++){
            int page = current+1;
            queryParams.put("page", String.valueOf(page));
            queryParams.put("rows", String.valueOf(FETCH_COUNT));
            String json = syncExecute(basePath+url, queryParams, "POST");
            JSONArray rowDatas = (JSONArray) JSON.parseObject(json).get("rows");
            Integer headerRowCount = exportParam.getColumns().size();
            //迭代数据
            for(int i=0; i<rowDatas.size(); i++ ){
                JSONObject rowDataMap = (JSONObject)rowDatas.get(i);
                Row row = sheet.createRow(current * FETCH_COUNT + i + headerRowCount);
                //直接取最后一行的列头信息
                List<Map<String, Object>> headers = exportParam.getColumns().get(exportParam.getColumns().size()-1);
                //迭代列头
                for(int j=0; j<headers.size(); j++){
                    Map<String, Object> headerMap = headers.get(j);
                    Cell cell = row.createCell(j);
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

    }

    private static int getCount(String url, Map<String, String> queryParams){
        queryParams.put("page", "1");
        queryParams.put("rows", "1");
        String json = syncExecute(url, queryParams, "POST");
        return (int)JSON.parseObject(json).get("total");
    }

    /**
     * 构建表头列, 只支持colspan，不支持rowspan，因为rowspan无法确定是向上还是向下合并,判断的因素太多，暂不支持
     * @param exportParam
     * @param workbook
     * @param sheet
     */
    private static void buildHeader(ExportParam exportParam, SXSSFWorkbook workbook, Sheet sheet){
        CellStyle columnTopStyle = getHeaderColumnStyle(workbook);//获取列头样式对象

        //渲染复合表头列
        for (int i = 0; i < exportParam.getColumns().size(); i++) {
            //每行的列信息
            List<Map<String, Object>> rowColumns = exportParam.getColumns().get(i);
            Row row = sheet.createRow(i);

            int colspanAdd = 0;
            for (int j = 0; j < rowColumns.size(); j++) {
                //列头信息
                Map<String, Object> columnMap = rowColumns.get(j);
                Cell cell = row.createCell(j+colspanAdd);               //创建列头对应个数的单元格
                cell.setCellType(CellType.STRING);             //设置列头单元格的数据类型
                RichTextString text = new XSSFRichTextString(columnMap.get("title").toString().replaceAll("\\n", "").trim());
                cell.setCellValue(text);                                 //设置列头单元格的值
                cell.setCellStyle(columnTopStyle);                       //设置列头单元格样式
                if(columnMap.get("colspan") != null) {
                    Integer colspan = Integer.class.isAssignableFrom(columnMap.get("colspan").getClass())? (Integer)columnMap.get("colspan") : Integer.parseInt(columnMap.get("colspan").toString());
                    if(colspan>1) {
                        Cell tempCell = row.createCell(j + colspanAdd + colspan - 1);               //创建合并最后一列的列头，保证最后一列有右边框
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
                JSONObject paramJo = (JSONObject)JSONObject.toJSON(paramObj);
//                FormBody.Builder builder = new FormBody.Builder();
//                if (jo!=null&&!jo.isEmpty()){
//                    for(Map.Entry<String,Object> entry:jo.entrySet()){
//                        String value = entry.getValue() == null ? null : entry.getValue().toString();
//                        builder.add(entry.getKey(),value);
//                    }
//                }
//                RequestBody requestBody = builder.build();
//                String json = paramObj instanceof String ? (String)paramObj : JSON.toJSONString(paramObj);
                //构建查询参数，主要是为了处理metadata信息以及其它类型的值转为String
                Map<String, String> param = new HashMap<>();
                if (paramJo!=null&&!paramJo.isEmpty()){
                    for(Map.Entry<String,Object> entry:paramJo.entrySet()){
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
            }else{ //GET方式
                resp = OkHttpUtils
                        .get()
                        .url(url).params((Map)JSON.toJSON(paramObj))
                        .build()
                        .execute();
            }
            if(resp.isSuccessful()){
//                logger.info(String.format("远程调用["+url+"]成功,code:[%s], message:[%s]", resp.code(),resp.message()));
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
    private static void write(String title, SXSSFWorkbook workbook, HttpServletResponse response){
        if (workbook != null) {
            try {
                String fileNameDownload = title + ".xlsx";

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
    private static CellStyle getHeaderColumnStyle(SXSSFWorkbook workbook) {
        // 设置字体
        Font font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short) 11);
        //字体加粗
        font.setBold(true);
        //字体颜色
        font.setColor(IndexedColors.ROYAL_BLUE.index);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        CellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(IndexedColors.BLACK.index);
        //设置左边框;
        style.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(IndexedColors.BLACK.index);
        //设置右边框;
        style.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
        style.setRightBorderColor(IndexedColors.BLACK.index);
        //设置顶边框;
        style.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(IndexedColors.BLACK.index);
        //在样式用应用设置的字体;
        style.setFont(font);
        //设置前景色样式
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置前景色
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
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
    private static CellStyle getDataColumnStyle(SXSSFWorkbook workbook) {
        // 设置字体
        Font font = workbook.createFont();
        //设置字体大小
        font.setFontHeightInPoints((short)10);
        //字体加粗
        //font.setBoldweight(Font.BOLDWEIGHT_BOLD);
        //设置字体名字
        font.setFontName("Courier New");
        //设置样式;
        CellStyle style = workbook.createCellStyle();
        //设置底边框;
        style.setBorderBottom(BorderStyle.THIN);
        //设置底边框颜色;
        style.setBottomBorderColor(IndexedColors.BLACK.index);
        //设置左边框;
        style.setBorderLeft(BorderStyle.THIN);
        //设置左边框颜色;
        style.setLeftBorderColor(IndexedColors.BLACK.index);
        //设置右边框;
        style.setBorderRight(BorderStyle.THIN);
        //设置右边框颜色;
        style.setRightBorderColor(IndexedColors.BLACK.index);
        //设置顶边框;
        style.setBorderTop(BorderStyle.THIN);
        //设置顶边框颜色;
        style.setTopBorderColor(IndexedColors.BLACK.index);
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
