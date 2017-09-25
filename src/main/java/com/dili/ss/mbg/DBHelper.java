package com.dili.ss.mbg;


import org.mybatis.generator.api.ConnectionFactory;
import org.mybatis.generator.config.Context;
import org.mybatis.generator.internal.JDBCConnectionFactory;
import org.mybatis.generator.internal.ObjectFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by asiamaster on 2017/9/22 0022.
 */
public class DBHelper {

	private static Connection conn = null;

	/**
	 * 获取数据库连接(延迟加载)
	 * @param context
	 * @return
	 */
	public static Connection getConnection(Context context) {
		try {
			if(conn == null) {
				ConnectionFactory connectionFactory;
				if (context.getJdbcConnectionConfiguration() != null) {
					connectionFactory = new JDBCConnectionFactory(context.getJdbcConnectionConfiguration());
				} else {
					connectionFactory = ObjectFactory.createConnectionFactory(context);
				}
				return connectionFactory.getConnection();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}

	public static void close() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
