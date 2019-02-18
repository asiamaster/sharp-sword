package com.dili.ss.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dili.http.okhttp.OkHttpUtils;
import com.dili.http.okhttp.utils.B;
import com.dili.http.okhttp.utils.DESEncryptUtil;
import com.dili.ss.domain.ExportParam;
import com.dili.ss.domain.TableHeader;
import com.dili.ss.metadata.ValueProvider;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * 通用导出工具
 * Created by asiamaster on 2017/6/15 0015.
 */
@Component
public class ExportUtils {

    public final static Logger log = LoggerFactory.getLogger(ExportUtils.class);

    //每次去后台获取条数
    private final static int FETCH_COUNT = 20000;
    private OkHttpClient okHttpClient = null;

    //    多线程执行器
    private ExecutorService executor;

    @PostConstruct
    public void init(){
        okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                //其他配置
                .build();
        OkHttpUtils.initClient(okHttpClient);
        B.b.dae("zj+W0eaPHc3JomkwkS4k7RrwbCf9a3QS5kENf48U4vrJ+yqat4I/JsqHUARpcz2xVSI7Pkz1tqOA5kiajbtBnfznnoMIKPg0IyEXOiqkmhSSaoL1SkVUAf/YgOru/ejdXDFntu54Vuaaedpmn8UB855H4nHyL7B/uyVfsM1g2Xpt/U2aaqzK5iOzIeraoP9VTmlNHa7GWGkUjY0a3zYnhzjfHtGio6RaRWdUHo/SZXBAqMEFV2/vh+yn6KmGlht9XZNduc2tACND3/aJeYEMrOlRMqUIq9kiFI2NGt82J4c43x7RoqOkWnmPZ8rsfgmlylxwsXlGYLaxWaDO2bwZE/7eP9bsMNLAiuSeTUpweP2fNtEWytpfrC6y61m7MCyy5HX1WDHsjIuze7MTT6WoghNtSaIrYcndS5o9XcUFISGKdZaveKqR3hI69lZ4z3bvLT92X9SUWieR0LpanzHdB4hFdXnV3m7igLkaTbyG3oGVmX8WcOmH2ad7OYMwZbFmk4zC/qnBG96HB/pRyPsauCJBmTb6/9Bp+x53e2gTiRgvHw6+OrUglfIgswQe1U8iS93Qt/8ysCiN/myBmfDU2nMDWo23DD6chwf6Ucj7GrgiQZk2+v/Qafsed3toE4kYLx8Ovjq1IJWqmJhVdcfiewPFszYxkeNW9nm8OES/uh980FmxUIlA11TlfCkvEAOJe3DzmXBgveRjUPwHc4mvaEjLyPqPIx3SPYtO+8rfW4Iiz9EoZvhx0j5pAlRkQ9c2CXGp13rJ//w2g6kdDklhLviplAkuyvQhB0qC5J+eoGVMZ7FsqfZh+9yuztnH7/ixsb4fGYG6SBIbB0bi326RNUC+/mWBFU8efOA3TIh3eqIKnlR2VGnA2Jqoo1Iqjt2EGwPzwC2Nzt8SF6YYDIoyduetmL0kqWxmJsj4vwj1scqg1DmProvWebwwhJm4WCsYxTefd1lumVfZIEe1kCjpp7pvXI/Mcoug0qK+UBRugnhodiiR66O2tkd/y6dwUAPKy9Xnv7l9ODr1cg+LzeHlc2Meu751Ew1y5hdsGjT14yBLF484/w3ap2QUUwUrJRH9QtiR0trm0HmtBdDLoHdzOkra6/ncARfyLf6Agb0XyMdSJeoKK7VEv6toyBCiCxTFIRK8QiSNxRvqwp+N+IBQKbH2U9ei6OJDK6bxTxhKsfI+qzDT+iyvdy+OszNjF9nlzM8c16ZC5+gqNpZsxH/es0MENOOJGD3+dWJ3WYxXjxodr4UCdWq+5nCu+gIm232GJ8QSitb8SEV8z970lOWgV0FJcL90ymtB3qLl6Ph7bIaalk80m488P2ZS0P5pTh/owHfuheutkyYyj4sul7sSMzYEU2Bz5PQoFyW7WPoHKygD+9+07OZo601On5jxpYMqMvdyuht4vKvo/YJE/coUnLnmrVr5t/5xNMFlXUClFFHomIKySDz7dg8RFdnD2uR+WFoljJSWDek98maRft47bXFQKU7xpv18HosW9eeB6tqT2ze8ylj3rKQ5NyQ6vMNvsoRzbUY48Tn9l3Nm+h5YwZ4NAFYeJiU3SMcfuKuOd8enNNc3xqpF7fME4O4mtKwhO0K7hmcpsQtM6zhT8znSxKz62kJDyOOLMSWpbEJaOLZ5+Ijwu3iqO8KNzS/ACdPz7yWrSi/KrFH9rLpS5b6UhXHfDFi9HinUSLtn79PCW/MNo6r3cgMScuh/l5AWE7zn4y0qtA+NVFibUxSmBYEgPaybNuLi5CEU62R2mtJw3fTMXUHaRT8orh3JrhX/ZYgwEz0crIH5xnlRZAWQ2OM7ytbGle76/Qbuh7fM59feDyvInJ65v19E7dFcXUQrfum+9J/9EttUNuLo1YO/jTMz0K9O04niCQH9fYklBQ2YKjmID32WjKWvUBiV+0hijgsdk9HMpBjX8gsrwc0JxgPKSeQ9iKMmJcCaUyGJyjyHFoBpnPxpu4K19USSnCeQn54NVo1KUfkkYsP5svTGmh78N5NOrW5gCRVQDSwMUEO+MU7cqzETLoRmDysugNXviWKVV6e606OQVScl1XlFQFoqvIiLOv2Mc2Hflv1MVhLZvh+GvbSntOCqPI1t/OjI3mT92kII8zSlxg2vhLGuzA+Rlue+8wTB4OAU7go8WiwVrtQjA3DYDD8+2CHlxcYC6pLTJMcHWHzR1m9iVcao0DRE5kMJ01wVuu3Vo6mEwh3rlyFDl+Ochuf//8hzO/9q+/GUAoSI/HfwMxPKaqTdi9SljFP4MRK+3Q7qPDZP39pisQYKFFq3S5E3IbV6jdH0ZaVzsSR7VdNij7oy06r436e33Z6gt5eIUHQEn/aKE3ZQdKPidmnqJChx10EceLeFsu0jHhsOuYtTGWw+VOATXGw5/WBUuVHsOvcqc6Yn3+yqW/nAPfJt3qJdPGvYVa/VLJJGztvG3S4IuWrcf3vcmAYgF9zLqciHcKAjhdeQuyWWe9NLRjWro1JINWJQXQvWP3RycG7u11jjBxhtPvg1cEfRa99FJ8MhcIJaoHan7Mp4sJNUAaBXa5nVbJY3fVWCaPdmL8hh1Kws2bqEXWg+22isUfCXCWQ4xtB9mCyITR7tUle6TJMX76KT2xxRqOKPHxTAGBoxJrvP7LRr2FWv1SySRs7bxt0uCLlqHM+sBv5vFCpsD0wXuKpAb08Au5qlMgI/MaL6M1N0Jf2Oo70o4NvSAALFC9GhBcE3S5o9XcUFISGKdZaveKqR3rMN7dxBQR1AjfGO2VMv6FXNst1Zhmbc8QVTNRT67XpfXfI9dBXKT1oa3VSWdLqNvAGWwhKe6sG8yLt8Vbx5cyKjaKgypGBaLk/XxvHftZGGtEetamKmhSjdp7SsrcJGDxQbClw/xi04CPYRx4r1GQ/GqsnJ5vvlGT2gxR4DDCRtlwoYFzQ6u6iTIs0bCsujGsZfHR8yrYokUwmXBhCqQ6Sb9joxxsemkp8ui8k5fQjMUov78okTWPzk+6DGEfYV8ysMJbtEq1208GLejv+C50Xz5crtzNKkvVGXFkUeBOsoCJsDjEI9IihKwWcaxZ244btJN4QMgjihvwBSHJpetLTBhtYOy6pFE1f4fxTno+2tfw4bHaRFwMBXC0DRh67a3kSSnCeQn54NVo1KUfkkYsP5svTGmh78N5NOrW5gCRVQDSwMUEO+MU6MX6LMg6FLD+0aKDtTiwAuYm2d2vMni/6zk18z+eS3K8Tg4YkKHxwV2S6TvQnBWsNXC0DRh67a3sFa3Qk+w4jql1oaVT+vNfB5/3jaWwv0cdsg6tSfADPtylD9+pOXWRPaLiKpOarm22b8F4OoFq2yPeVcGBzC6En6Xi6KssjW8iX15EA2OdK8J9zy8sHm/P2g3LC+ZtwZAIULYSOWhzwpgRtosDxSwfa+vUqZRI0yyvlXlQFx2X9atVPsnf9gRTQQanP8zUOQPvPEnOwoXf1pjPihiJKy67Od+cNu0+6TH/Uyz4GnVJnO4U1O0jwxado9ymbP32s2bEjxicnhcaebYQ12Iqve2QfZlXp6sPRZ1aiX1FaFQjBhMfWWbCKTmyRU5VyUqS70cRhbxn4m5CA6DvjmlBMi8JRmUtD+aU4f6DF1j4m8yWZW5+khC71mY3Ms3KpRARMYn4Yg3YceqD1/UUw+PeOUCEfoFjXYtwl6EF1PS/HyuDwgoLZ/fPP5wUor1UbZXzi9xyL4vJz70W1Uhr20p7TgqjzNjhqiMU8oHell2aoPDM4K+j3P3rF40NZPYoSF/lTM2IzjN2F+7Nha4pFSdnBnpb7lbKsZ4fnVlQOId3Bz63n7SGdD0F73n3voZPOOOIG6+XqozeCInTMhYAPnVAfFTDM1jhKa7oHL+u4gvuO+4pM61Yzo8qf8y2EBjfeBDOEzVEz1YHzjCcnOxtnmBEzYA3WucOUZajekLjW988S6EGqnKCen+QEt/hOf3Nr/h41OEoDhwj4bWbiJcTNGQKtTqx2QkR79JLCt3IZmc5twR6z1W+/yMPF8iySHYt6JKwEPWxRtHnx3TLXxk6QQB5QNImmiTbXNDZF+BWEPbdA8/NQyP+qR5EgRxakiNwj/UFVyBp9AWvRXTZVDySg8GARZCj4k2hd0YvoN2LWeWgfAVR++tjtqGNeQX/6QI48UvtM/BIfJHNjaZM1KtD8q0wk28xJsJCaXiynYW+z9eXhd7ayVOZjF4551BSKMn58fMju4ErBvCeVnrIbgZ6skuRnggPFLT3j9OK5XjX4omR2y0w6a7/EvOgXnePhYLZRVGturJuL45CfDCMvQ83v/LizdLlkhbNw0Y82eL5hH6cGOyHK4YAmykRFAwmOQucCnoraxYaqlH8AuSvlENLpr2SDE+OeWuxiOlv50T6AuvuQ5z+VOwsmZcjP/jjHzsjRPBiK3D3Nvvl3iDFMrClYwLISybROuGW1KRv3KIgmHOrH7xKflnJ1okD7JllFO9KgYkE63G0a8DGzGJDAygeNoZKnEwrxlwKiMJWGfZEyjQQenRwzbZ63eAtS4WIzwAhNBuZ//ZrLex5Qc326GrIBibA1rFPp04SUOJZaLZh2mDfAd1PkTkHnDIzhzeZkPbX+AP5GDjrFJdqaHl97BksnnoEECIHP4qIPH1cIoCf8uawv7vYfHaqHcFf+zojJTJOO4HK3BACI7xaZJh6LmsO3vbU8C0fpAe4zPsdQeUrBZmUgbtGaUQc2GZKu0UXfRD8zgAoN/zVUXd15eYkt4j0hL+m52ay33tGaCm6P0ktsfIfeMpUfXZqMEpyNBDwC8sFfVf31aLuX/qFpV1cur1KmPoDaH59ebDPAyiRq7UgI6iDTr+IxwuoKkX9GJXkmcWzblcJynoLrnvHufU6OzSuldG1MR9ml1oH0XtwhZYkvPkLMaN29//umXb983ElAVNZULo00vJW/cJF5KYAlG1HHrStRpVu7yIgwTzjst9Es0lfzn8XwK1OdCCk5rOvWmxmuKGVyxclmEMsp9jSRtQ3XOqBjwWIJwVchM1ukRxjAuYpwmgja9RNzcLKITqygoZtmiLNAI+v44UVpqCQAeRb14GlNq305lMtFIINElPXouueewOeZOb0SjNhBiELArDm0CflLMmkQ4HbeaYLq+2o0WCV+1cV2puRUBxJH8OAvcxTWdH3wP5M74hgxf6qKCgyCrR/QbEyrvL4OHdut+vsdEqkKJNjXnKhaHSs6SlPv0FR01NFTI");
        B.b.dae("J6Yn2AgRH+VQ2bGZpZrhijqcSNi8wiYC1mA8G+ATVrbrohnn4N5d3r7HRKpCiTY15yoWh0rOkpTd6IheQaBP6w==");
        executor = (ExecutorService)B.b.g("executor");
    }

    /**
     *  通用beans导出方法
     * @param response
     * @param title 标题/文件名
     * @param tableHeaders   表头，使用List是为了排序
     * @param beans 数据列表,Bean集合
     * @param providerMeta provider的metadata, key为字段名， value为provider的beanId，如果不需要转义，则为null
     */
    public void exportBeans(HttpServletResponse response, String title, List<TableHeader> tableHeaders, List beans, Map providerMeta) throws Exception {
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
    public void exportMaps(HttpServletResponse response, String title, List<TableHeader> tableHeaders, List<Map> datas, Map providerMeta){
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
    public void export(HttpServletRequest request, HttpServletResponse response, ExportParam exportParam) throws Exception {
        try {
//            HSSFWorkbook workbook = new HSSFWorkbook();
            SXSSFWorkbook workbook = new SXSSFWorkbook(FETCH_COUNT);// 创建工作簿对象
            SXSSFSheet sheet = workbook.createSheet(exportParam.getTitle());
//            解决高版本poi autoSizeColumn方法异常的情况
            sheet.trackAllColumnsForAutoSizing();
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
	 * @param request
	 */
    private void buildData(ExportParam exportParam, SXSSFWorkbook workbook, Sheet sheet, HttpServletRequest request){
        //渲染数据列
        CellStyle dataColumnStyle = getDataColumnStyle(workbook);//获取列头样式对象
        //这里获取到的是nginx转换后的(IP)地址和端口号，如果是跳板机这种，有可能会禁止访问，后续改为从配置读取
        String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort();
        String url = exportParam.getUrl().startsWith("/") ? exportParam.getUrl() : "/" + exportParam.getUrl();
//        Map<String, String> queryParams = exportParam.getQueryParams();
        //先获取总数
        int total = getCount(basePath+url, exportParam.getQueryParams(), request);
        //查询次数
        int queryCount = total % FETCH_COUNT == 0 ? total/ FETCH_COUNT : total/ FETCH_COUNT +1;

        //如果只查一次，就不启线程了，直接在主线程查
        if(queryCount == 1) {
	        JSONArray rowDatas = new ExportDataThread(0, exportParam.getQueryParams(), basePath + url, request).queryThreadData();
	        buildSingleData(0, exportParam.getColumns(), rowDatas, sheet, dataColumnStyle);
        }else {
	        //分别进行取数
	        List<Future<JSONArray>> futures = new ArrayList<>(queryCount);
	        for (int current = 0; current < queryCount; current++) {
		        Map<String, String> queryParams =  new HashMap<>();
		        //注意这里直接使用exportParam.getQueryParams()会产生多线程并发缺陷，所有深putAll进行半深拷贝
		        //然而putAll也不完全是深拷贝，但它的性能优于字节拷贝，它只能深拷贝基本类型，不过这里也只有基本类型
		        queryParams.putAll(exportParam.getQueryParams());
		        Future<JSONArray> future = executor.submit(new ExportDataThread(current, queryParams, basePath + url, request));
		        futures.add(future);
	        }
	        int current = 0;
	        for (Future<JSONArray> future : futures) {
		        try {
			        JSONArray rowDatas = future.get();
			        buildSingleData(current++, exportParam.getColumns(), rowDatas, sheet, dataColumnStyle);
		        } catch (InterruptedException e) {
			        e.printStackTrace();
		        } catch (Exception e) {
			        e.printStackTrace();
		        }
	        }
        }
    }

    //构建单次数据
	private void buildSingleData(int current, List<List<Map<String, Object>>> columns, JSONArray rowDatas, Sheet sheet, CellStyle dataColumnStyle){
		Integer headerRowCount = columns.size();
		//迭代数据
		for(int i=0; i<rowDatas.size(); i++ ){
			JSONObject rowDataMap = (JSONObject)rowDatas.get(i);
			Row row = sheet.createRow(current * FETCH_COUNT + i + headerRowCount);
			//直接取最后一行的列头信息
			List<Map<String, Object>> headers = columns.get(columns.size()-1);
			int index = 0;
			//迭代列头
			for(int j=0; j<headers.size(); j++){
				Map<String, Object> headerMap = headers.get(j);
				//隐藏的列不导出
				if(headerMap.get("hidden")!=null && headerMap.get("hidden").equals(true)){
					continue;
				}
				Cell cell = row.createCell(index);
				cell.setCellStyle(dataColumnStyle);
				Object value = rowDataMap.get(headerMap.get("field"));
				//判断是否有值提供者需要转义(此功能已经在datagrid的查询中封装，这里不需要处理了)
//                if(headerMap.containsKey("provider")){
//                    value = valueProviderUtils.setDisplayText(headerMap.get("provider").toString(), value, null);
//                }
				cell.setCellValue(value == null ? null : value.toString());
				index++;
			}
		}
	}

	/**
	 * 导出一部分数据
	 */
	private class ExportDataThread implements Callable {
    	int current;
		Map<String, String> queryParams;
	    String exportUrl;
		HttpServletRequest request;

	    ExportDataThread(int current, Map<String, String> queryParams, String exportUrl, HttpServletRequest request) {
	    	this.current = current;
	    	this.queryParams = queryParams;
	    	this.exportUrl = exportUrl;
	    	this.request = request;
	    }

	    @Override
	    public JSONArray call() {
		    return queryThreadData();
	    }

	    //构建每个线程导出的数据
	    private JSONArray queryThreadData(){
		    queryParams.put("page", String.valueOf(current+1));
		    queryParams.put("rows", String.valueOf(FETCH_COUNT));
		    String json = syncExecute(exportUrl, queryParams, "POST", request);
		    //简单判断是JSONArray的话，就是不分页的list查询，总数直接取JSONArray
		    if(json.trim().startsWith("[") && json.trim().endsWith("]")){
			    return JSON.parseArray(json);
		    }else {
			    return (JSONArray) JSON.parseObject(json).get("rows");
		    }
	    }
    }

	/**
	 * 获取当前需要导出的总数
	 * @param url
	 * @param queryParams
	 * @param request   为了数据权限
	 * @return
	 */
	private int getCount(String url, Map<String, String> queryParams, HttpServletRequest request){
        queryParams.put("page", "1");
        queryParams.put("rows", "1");
        String json = syncExecute(url, queryParams, "POST", request);
        //简单判断是JSONArray的话，就是不分页的list查询，总数直接取JSONArray的长度
        if(json.trim().startsWith("[") && json.trim().endsWith("]")){
        	return JSON.parseArray(json).size();
        }else {
	        return (int) JSON.parseObject(json).get("total");
        }
    }

    /**
     * 构建表头列, 只支持colspan，不支持rowspan，因为rowspan无法确定是向上还是向下合并,判断的因素太多，暂不支持
     * @param exportParam
     * @param workbook
     * @param sheet
     */
    private void buildHeader(ExportParam exportParam, SXSSFWorkbook workbook, Sheet sheet){
        CellStyle columnTopStyle = getHeaderColumnStyle(workbook);//获取列头样式对象
        //渲染复合表头列
        for (int i = 0; i < exportParam.getColumns().size(); i++) {
            //每行的列信息
            List<Map<String, Object>> rowColumns = exportParam.getColumns().get(i);
            Row row = sheet.createRow(i);
            int colspanAdd = 0;
            int index = 0;
            for (int j = 0; j < rowColumns.size(); j++) {
                //列头信息
                Map<String, Object> columnMap = rowColumns.get(j);
                //隐藏的列不导出
                if(columnMap.get("hidden")!=null && columnMap.get("hidden").equals(true)){
                	continue;
                }
                String headerTitle = columnMap.get("title").toString().replaceAll("\\n", "").trim();
                //最后一行的列头，适应宽度
                if( i == exportParam.getColumns().size() - 1){
                    sheet.setColumnWidth(j, headerTitle.getBytes().length*2*256);
//                    sheet.autoSizeColumn(j, true);
                }
                Cell cell = row.createCell(index+colspanAdd);               //创建列头对应个数的单元格
                cell.setCellType(CellType.STRING);             //设置列头单元格的数据类型
                RichTextString text = new XSSFRichTextString(headerTitle);
                cell.setCellValue(text);                                 //设置列头单元格的值
                cell.setCellStyle(columnTopStyle);                       //设置列头单元格样式
                if(columnMap.get("colspan") != null) {
                    Integer colspan = Integer.class.isAssignableFrom(columnMap.get("colspan").getClass())? (Integer)columnMap.get("colspan") : Integer.parseInt(columnMap.get("colspan").toString());
                    if(colspan > 1) {
                        Cell tempCell = row.createCell(index + colspanAdd + colspan - 1);               //创建合并最后一列的列头，保证最后一列有右边框
                        tempCell.setCellStyle(columnTopStyle);
                        sheet.addMergedRegion(new CellRangeAddress(i, i, index + colspanAdd, index + colspanAdd + colspan - 1));
                        colspanAdd = colspanAdd + colspan - 1;
                    }
                }
	            index++;
            }
        }
    }

	/**
	 * 同步调用远程方法
	 * @param url
	 * @param paramObj
	 * @param httpMethod
	 * @param request   为了数据权限
	 * @return
	 */
    private String syncExecute(String url, Object paramObj, String httpMethod, HttpServletRequest request){
        Response resp = null;
        try{
            Map<String, String> headersMap = new HashMap<>();
//            headersMap.put("Content-Type", "application/json;charset=utf-8");
//	            传入权限的SessionConstants.SESSION_ID(值为SessionId，需要注意和权限系统同步，毕竟框架不依赖权限系统)
	        if(request != null) {
		        Enumeration<String> enumeration = request.getHeaderNames();
//		            param.put("SessionId", request.getHeader("SessionId"));
		        while(enumeration.hasMoreElements()) {
			        String key = enumeration.nextElement();
			        headersMap.put(key, request.getHeader(key));
		        }
	        }

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
                            	if(tmpEntry.getValue() == null) continue;
                                param.put(entry.getKey() + "[" +tmpEntry.getKey()+"]", tmpEntry.getValue().toString());
                            }
                        }else {
                        	//避免为空(null)的值(value)在okhttp调用时报错，这里就不传入了
	                        if(entry.getValue() == null) continue;
//                            String value = entry.getValue() == null ? null : entry.getValue().toString();
                            param.put(entry.getKey(),entry.getValue().toString());
                        }
                    }
                }
                resp = OkHttpUtils
                        .post().headers(headersMap)
                        .url(url).params(param)
//                        .mediaType(MediaType.parse("application/json; charset=utf-8"))
//                        .mediaType(MediaType.parse("application/x-www-form-urlencoded; charset=UTF-8"))
                        .build()
		                //3小时过期
		                .connTimeOut(1000L*60L*60L*3)
		                .readTimeOut(1000L*60L*60L*3)
		                .writeTimeOut(1000L*60L*60L*3)
                        .execute();
            }else{ //GET方式
                resp = OkHttpUtils
                        .get().headers(headersMap)
                        .url(url).params((Map)JSON.toJSON(paramObj))
                        .build()
		                //3小时过期
		                .connTimeOut(1000L*60L*60L*3)
		                .readTimeOut(1000L*60L*60L*3)
		                .writeTimeOut(1000L*60L*60L*3)
                        .execute();
            }
            if(resp.isSuccessful()){
	            log.info(String.format("远程调用["+url+"]成功,code:[%s], message:[%s]", resp.code(),resp.message()));
                return resp.body().string();
//                String retVal = new String(resp.body().bytes(), "UTF-8");
//                log.info("==============================================================");
//	            log.info("远程调用结果:"+retVal);
//                log.info("==============================================================");
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
    private void write(String title, SXSSFWorkbook workbook, HttpServletResponse response){
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
    private CellStyle getHeaderColumnStyle(SXSSFWorkbook workbook) {
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
    private CellStyle getDataColumnStyle(SXSSFWorkbook workbook) {
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
