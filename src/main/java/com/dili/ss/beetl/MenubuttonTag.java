package com.dili.ss.beetl;

import com.dili.ss.util.AopTargetUtils;
import com.dili.ss.util.POJOUtils;
import com.dili.ss.util.SpringUtil;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.beetl.core.Tag;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * 菜单按钮自定义标签
 * Created by asiamaster on 2017/7/11 0011.
 */
@Component("menubutton")
public class MenubuttonTag extends Tag {
	private final String LINE_SEPARATOR = System.getProperty("line.separator");
	private final String TAB = "    ";
	//标签默认值
	private final String ID_FIELD_DEFAULT = "id";
	private final String TEXT_FIELD_DEFAULT = "text";
	private final String PARENT_ID_FIELD_DEFAULT = "parentId";

	//标签属性
	private final String SERVICE = "service";
	private final String METHOD = "method";
	private final String QUERYPARAMS = "queryParams";
	private final String ID_FIELD = "idField";
	private final String TEXT_FIELD = "textField";
	private final String PARENT_ID_FIELD = "parentIdField";

	//easyui菜单项属性对应，用于在菜单数据列表中获取相应信息
	private final String ICONCLS = "iconCls";
	private final String DISABLED = "disabled";

	@Override
	public void render() {
		try {
//			BodyContent content = getBodyContent(); // 标签体内容，暂存
			Map<String, String> argsMap = (Map)this.args[1];
			String service = argsMap.get(SERVICE);
			String method = argsMap.get(METHOD);
			String queryParams = argsMap.get(QUERYPARAMS);
//			service和method参数必填
			if(StringUtils.isBlank(service) || StringUtils.isBlank(method)) {
				return;
			}
			Object serviceObj = AopTargetUtils.getTarget(SpringUtil.getBean(service));
			List list = (List)serviceObj.getClass().getMethod(method, String.class).invoke(serviceObj, queryParams);
			if(null == list || list.isEmpty()){
				return;
			}
			writeMenu(list, writeMenubutton(list));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 生成菜单按钮
	 * @param list
	 * @return
	 */
	private List writeMenubutton(List list){
		List rootList = null;
		boolean isMap = false;
		Map<String, String> argsMap = (Map)this.args[1];
		//获取对应字段名称，默认为常量值
		String textField = argsMap.get(TEXT_FIELD) == null ? TEXT_FIELD_DEFAULT : argsMap.get(TEXT_FIELD).toString();
		String idField = argsMap.get(ID_FIELD) == null ? ID_FIELD_DEFAULT : argsMap.get(ID_FIELD).toString();
		String parentIdField = argsMap.get(PARENT_ID_FIELD) == null ? PARENT_ID_FIELD_DEFAULT : argsMap.get(PARENT_ID_FIELD).toString();
		if(Map.class.isAssignableFrom(list.get(0).getClass())){
			rootList = getMapRoots(list, parentIdField);
			isMap = true;
		}else{
			rootList = getBeanRoots(list, parentIdField);
		}
		StringBuilder stringBuilder = new StringBuilder("<div class=\"easyui-panel\" style=\"padding:5px;\">"+ LINE_SEPARATOR);
		for(Object root : rootList) {
			//校验text和id必须有
			if(getData(root, textField, isMap) == null || getData(root, idField, isMap) == null) {
				continue;
			}
			String id = getData(root, idField, isMap).toString();
			//没有子的根节点的class为easyui-linkbutton
			if(!hasChild(list, id, isMap, parentIdField)){
				stringBuilder.append("<a id=\"menubutton_" + id + "\" href=\"#\" class=\"easyui-linkbutton\" data-options=\"blankKey:''");
			}else{
				stringBuilder.append("<a id=\"menubutton_" + id + "\" href=\"#\" class=\"easyui-menubutton\" data-options=\"menu:'#menu_" + id + "'");
			}
			//添加menubutton属性
			if (argsMap.containsKey("plain")) {
				stringBuilder.append(", plain:" + argsMap.get("plain"));
			}
			if (argsMap.containsKey("menuAlign")) {
				stringBuilder.append(", menuAlign:'" + argsMap.get("menuAlign") + "'");
			}
			if (argsMap.containsKey("duration")) {
				stringBuilder.append(", duration:" + argsMap.get("duration") );
			}
			if (argsMap.containsKey("hasDownArrow")) {
				stringBuilder.append(", hasDownArrow:" + argsMap.get("hasDownArrow") );
			}
			if (argsMap.containsKey("iconCls")) {
				stringBuilder.append(", iconCls:'" + argsMap.get("iconCls") + "'");
			}
			stringBuilder.append("\">" + getData(root, textField, isMap) + "</a>"+ LINE_SEPARATOR);
		}
		stringBuilder.append("</div>"+ LINE_SEPARATOR);
		try {
			ctx.byteWriter.writeString(stringBuilder.toString() );
		} catch (IOException e) {
			e.printStackTrace();
		}
		//从list中删除rootList，优化性能
		removeListElement(list, rootList);
		return rootList;
	}

	/**
	 * 生成菜单
	 * @param list
	 * @param rootList
	 * @throws IOException
	 */
	private void writeMenu(List list, List rootList) throws IOException {
		Map<String, String> argsMap = (Map)this.args[1];
		//获取对应字段名称，默认为常量值
		String textField = argsMap.get(TEXT_FIELD) == null ? TEXT_FIELD_DEFAULT : argsMap.get(TEXT_FIELD).toString();
		String idField = argsMap.get(ID_FIELD) == null ? ID_FIELD_DEFAULT : argsMap.get(ID_FIELD).toString();
		String parentIdField = argsMap.get(PARENT_ID_FIELD) == null ? PARENT_ID_FIELD_DEFAULT : argsMap.get(PARENT_ID_FIELD).toString();
		StringBuilder stringBuilder = new StringBuilder();
		boolean isMap = false;
		if(Map.class.isAssignableFrom(list.get(0).getClass())){
			isMap = true;
		}
		for(Object root : rootList){
			//如果根节点下面没有子节点则不生成menu
			if(!hasChild(list, root, isMap, parentIdField)){
				continue;
			}
			String rootId = getData(root, idField, isMap).toString();
			//构建菜单, zIndex:110000是默认值，暂且写死
			stringBuilder.append("<div id=\"menu_"+rootId+"\" data-options=\"zIndex:'110000' ");
			//添加所有menu公用属性
			if(argsMap.containsKey("align")) {
				stringBuilder.append(", align:'" + argsMap.get("align") + "'");
			}
			if(argsMap.containsKey("minWidth")){
				stringBuilder.append(", minWidth:"+argsMap.get("minWidth"));
			}
			if(argsMap.containsKey("itemHeight")){
				stringBuilder.append(", itemHeight:"+argsMap.get("itemHeight"));
			}
			if(argsMap.containsKey("duration")){
				stringBuilder.append(", duration:"+argsMap.get("duration"));
			}
			if(argsMap.containsKey("hideOnUnhover")){
				stringBuilder.append(", hideOnUnhover:"+argsMap.get("hideOnUnhover"));
			}
			if(argsMap.containsKey("inline")){
				stringBuilder.append(", inline:"+argsMap.get("inline"));
			}
			if(argsMap.containsKey("fit")){
				stringBuilder.append(", fit:"+argsMap.get("fit"));
			}
			if(argsMap.containsKey("noline")){
				stringBuilder.append(", noline:"+argsMap.get("noline"));
			}
			if(argsMap.containsKey("onclick")){
				stringBuilder.append(", onClick:"+argsMap.get("onclick"));
			}
			stringBuilder.append("\">"+ LINE_SEPARATOR);

			Iterator it =list.listIterator();
			//构建子菜单
			while(it.hasNext()) {
				Object row = it.next();
				//校验text和id必须有，没有就略过，从list中移除掉
				if (getData(row, textField, isMap) == null || getData(row, idField, isMap) == null) {
					it.remove();
					continue;
				}
				Object parentId = getData(row, parentIdField, isMap);
				//parentId有可能为空，所以在右侧
				if (rootId.equals(parentId)) {
					appendMenuItem(stringBuilder, list, parentId, isMap, textField, idField, parentIdField, "");
				}

			}
			stringBuilder.append("</div>"+ LINE_SEPARATOR);
		}
		ctx.byteWriter.writeString(stringBuilder.toString());
	}

	/**
	 * 递归添加子菜单
	 * @param stringBuilder
	 * @param list
	 * @param parentId
	 * @param isMap
	 * @param textField
	 * @param idField
	 * @param parentIdField
	 * @param tab
	 */
	public void appendMenuItem(StringBuilder stringBuilder, List list, Object parentId, boolean isMap, String textField, String idField, String parentIdField, String tab){
		Iterator it =list.listIterator();
		//构建子菜单
		while(it.hasNext()) {
			Object row = it.next();
			Object currentParentId = getData(row, parentIdField, isMap);
			Object id = getData(row, idField, isMap);
			//在叶节点完成后将id设置为空，这里跳过以提升性能(因为即使是Iterator，在递归中也会有报ConcurrentModificationException)
			if(id == null){
				continue;
			}
			String iconCls = getData(row, ICONCLS, isMap);
			String disabled = getData(row, DISABLED, isMap);
			//currentParentId有可能为空，所以在右侧
			if (parentId.equals(currentParentId)) {
				//选拼接<div>开始标签MenuItem属性和内容
				stringBuilder.append(tab).append(TAB).append("<div data-options=\"blankKey:''");
				if (iconCls != null) {
					stringBuilder.append(", iconCls:'" + iconCls + "'");
				}
				if (disabled != null) {
					stringBuilder.append(", disabled:" + disabled);
				}
				stringBuilder.append(" \">").append(LINE_SEPARATOR);
				stringBuilder.append(tab).append(TAB).append(TAB).append(getData(row, textField, isMap)).append(LINE_SEPARATOR);
				//如果有子节点，就拼接子的<div>开始，然后再递归
				if (hasChild(list, id, isMap, parentIdField)) {
					stringBuilder.append(tab).append(TAB).append(TAB).append("<div>").append(LINE_SEPARATOR);
					appendMenuItem(stringBuilder, list, id, isMap, textField, idField, parentIdField, tab + TAB + TAB);
					//递归子节点完了关闭</div>
					stringBuilder.append(tab).append(TAB).append(TAB).append("</div>").append(LINE_SEPARATOR);
					stringBuilder.append(tab).append(TAB).append("</div>").append(LINE_SEPARATOR);
				} else {
					//如果没有子标签就直接关闭</div>
					stringBuilder.append(tab).append(TAB).append("</div>").append(LINE_SEPARATOR);
					//设置id为空，在下次进入时判断为空不处理，以提升性能(因为即使是Iterator，在递归中也会有报ConcurrentModificationException)
					setData(row, idField, null, isMap);
				}
			}
		}
	}

	/**
	 * List深拷贝
	 * @param src
	 * @return
	 */
	private List deepCopyList(List src){
		List<String> dest = new ArrayList<String>(Arrays.asList(new String[src.size()]));
		Collections.copy(dest, src);
		return dest;
	}

	/**
	 * 判断该节点下是否有子节点, id不允许为空，id为空返回false
	 * @param list
	 * @param id
	 * @param isMap
	 * @param parentIdField
	 * @return
	 */
	private boolean hasChild(List list, Object id, boolean isMap, String parentIdField){
		if(id == null){
			return false;
		}
		for(Object row : list){
			if(id.equals(getData(row, parentIdField, isMap))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 根据key从Map或Bean中获取值
	 * @param row
	 * @param key
	 * @param isMap
	 * @return
	 */
	private String getData(Object row, String key, Boolean isMap){
		Object data = isMap ? ((Map)row).get(key) : POJOUtils.getProperty(row, key);
		return data == null ? null : data.toString();
	}

	/**
	 * 从Map或Bean中根据key设置value
	 * @param row
	 * @param key
	 * @param value
	 * @param isMap
	 */
	private void setData(Object row, String key, Object value,  Boolean isMap){
		if(isMap){
			((Map)row).put(key, value);
		}else{
			POJOUtils.setProperty(row, key, value);
		}
	}

	/**
	 * 获取Map类型的根节点
	 * @param list
	 * @param parentIdField
	 * @return
	 */
	private List getMapRoots(List<Map> list, String parentIdField){
		List<Map> rootLists = Lists.newArrayList();
		for(Map map : list){
			Object parentId = getData(map, parentIdField, true);
			if(parentId == null || parentId.equals("") || parentId.equals("-1")){
				rootLists.add(map);
			}
		}
		return rootLists;
	}

	/**
	 * 获取Bean类型的根节点
	 * @param list
	 * @param parentIdField
	 * @return
	 */
	private List getBeanRoots(List list, String parentIdField){
		List rootLists = Lists.newArrayList();
		for(Object bean : list){
			Object parentId = getData(bean, parentIdField, false);
			if(parentId == null || parentId.equals("") || parentId.equals("-1")){
				rootLists.add(bean);
			}
		}
		return rootLists;
	}

	/**
	 * 删除source中的dest集合
	 * @param source
	 * @param dest
	 */
	private static void removeListElement(List source, List dest){
		Iterator i = source.listIterator();
		while(i.hasNext()){
			Object o = i.next();
			for(Object s : dest){
				if(o.equals(s)){
					i.remove();
				}
			}
		}
	}

	/**
	 * 删除source中的dest集合
	 * @param source
	 * @param dest
	 */
	private static void removeListElement(List source, Object ... dest){
		Iterator i = source.listIterator();
		while(i.hasNext()){
			Object o = i.next();
			for(Object s : dest){
				if(o.equals(s)){
					i.remove();
				}
			}
		}
	}
}
