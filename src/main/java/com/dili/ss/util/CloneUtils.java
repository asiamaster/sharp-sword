package com.dili.ss.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 深拷贝(字节复制)工具
 * Created by asiamaster on 2017/9/12 0012.
 */
public class CloneUtils {

	@SuppressWarnings("unchecked")
	public static <T> T clone(T obj){

		T clonedObj = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(baos);
			oos.writeObject(obj);
			oos.close();

			ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
			ObjectInputStream ois = new ObjectInputStream(bais);
			clonedObj = (T) ois.readObject();
			ois.close();

		}catch (Exception e){
			e.printStackTrace();
		}

		return clonedObj;
	}

//	public static void main1(String[] args) {
//		List<BaseDomain> list = new ArrayList<>();
//		for(int i=0;i<100;i++){
//			BaseDomain baseDomain = new BaseDomain();
//			baseDomain.setId(Long.parseLong(String.valueOf(i)));
//			list.add(baseDomain);
//		}
//		HashMap<String,Object> map = new HashMap<String,Object>();
//		//放基本类型数据
//		map.put("basic", new Integer(1));
//		//放对象
//		map.put("list", list);
//
//		HashMap<String,Object> mapNew = new HashMap<String,Object>();
//		HashMap<String,Object> mapNew2 = new HashMap<String,Object>();
//		HashMap<String,Object> mapNew3 = new HashMap<String,Object>();
//		mapNew.putAll(map);
//		Long starttime =System.currentTimeMillis();
//		mapNew = clone(map);
//		Long endtime =System.currentTimeMillis();
//		System.out.println("使用字节复制所需时间："+(endtime-starttime));
//		//System.out.println(mapNew);
//
//		System.out.println("---------");
//		starttime =System.currentTimeMillis();
//		mapNew2.putAll(map);
//		endtime =System.currentTimeMillis();
//		//注意putAll也不完全是深拷贝，它只能深拷贝基本类型
//		System.out.println("使用putAll复制所需时间："+(endtime-starttime));
//
//		System.out.println("---------");
//		starttime =System.currentTimeMillis();
//		String  jsonstr = JSON.toJSONString(map);
//		mapNew3 = JSON.parseObject(jsonstr,mapNew3.getClass());
//		endtime =System.currentTimeMillis();
//		System.out.print("使用json复制所需时间："+(endtime-starttime));
//	}
}
