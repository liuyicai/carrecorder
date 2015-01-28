package org.iotsys.dao;


import java.sql.*;
import java.util.ArrayList;



import org.apache.commons.logging.Log;  
import org.apache.commons.logging.LogFactory;  
import org.iotsys.czpt.spiderme;


/***
 * 
 * @author :alex.liuyicai(marker.liu@foxmail.com)
 * @sine: 2014-12-31
 * @description:
 *     简单JAVA DAO 对象
 *
 */
public class BaseDao {

 //oracle
// private  static final String Dirver="oracle.jdbc.driver.OracleDriver";
// private  static final String URL="jdbc:oracle:thin:@localhost:1521:XE";
// private  static final String name="admin";
// private  static final String pass="123";

 //sqlserver
// private static final String Dirver="com.microsoft.sqlserver.jdbc.SQLServerDriver";
// private static final String URL="jdbc:sqlserver://localhost:1433;DatabaseName=TBLdb";
// private static final String name="sa";
// private static final String pass="sa";

 //mysql
 private static final String Dirver="com.mysql.jdbc.Driver";
 private static final String URL="jdbc:mysql://127.0.0.1:3306/devicedb?useUnicode=true&characterEncoding=utf8";
 private static final String name="ctisdb";
 private static final String pass="ctisdb";

private static Log logger = LogFactory.getLog(BaseDao.class);
	
 public  Connection getCconnection()
 {
  Connection conn=null;
  try {
   Class.forName(Dirver);
   conn=DriverManager.getConnection(URL,name,pass);
  } catch (Exception e) {
   e.printStackTrace();
  }
  return conn;
 }
 
 
 public void closAll(Connection conn,PreparedStatement ps,ResultSet rs)
 {
		try {
			if (null != rs)
				rs.close();
			if (null != ps)
				ps.close();
			if (null != conn)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
 }
 
 /*****
  * 
  */
 
 public void closAll(Connection conn,Statement stmt,ResultSet rs)
 {
		// 释放结果集连接
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		// 释放声明连接
		if (stmt != null) 
		{
			try {
				stmt.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

		// 释放数据库连接
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}

 }
 
 
 /**
  * 
  * @param sql
  * @param parms
  * @return
  */

 public int executeUpdate(String sql,String parms[])
 {
		Connection conn = null;
		PreparedStatement ps = null;
		int result = -1;
		try {
			conn = getCconnection();
			ps = conn.prepareStatement(sql);
			if (null != parms) {
				for (int i = 0; i < parms.length; i++) {
					ps.setString(i + 1, parms[i]);
				}
			}
			result = ps.executeUpdate();
			// ps.execute();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} finally {
			closAll(conn, ps, null);
		}
		return result;
 }
 
 /***
  * @desc：更新语句
  * 
  */
 
 public int executeUpdate(String sql,ArrayList<String> params)
 {
		Connection conn = null;
		PreparedStatement ps = null;
		int result = -1;
		try {
			conn = getCconnection();
			ps = conn.prepareStatement(sql);
			if (false == params.isEmpty())
			{
				int i =0;
			    for (String s:params)
			    {
			    	ps.setString(i + 1, s);
			    	i++;
			    }
			}
			result = ps.executeUpdate();
			// ps.execute();

		} catch (Exception e) {
			System.out.println(e.getMessage());
			logger.info("SQL="+sql);
		} finally {
			closAll(conn, ps, null);
		}
		return result;
 }
 
 /** 
  * insert update delete 统一的方法 
  *  
  * @param sql 
  *            insert,update,delete SQL 语句 
  * @return 受影响的行数 
  */  
 public int executeUpdate(String sql) {  
     int affectedLine = 0;  
     Connection conn = null;
     Statement statement = null;  
     try {  
         // 获得连接  
    	 conn = getCconnection();

         // 创建对象  
         statement = conn.createStatement();  

         // 执行SQL语句  
         affectedLine = statement.executeUpdate(sql);  
     } catch (SQLException e) {  
         System.out.println(e.getMessage());  
     } finally {  
         // 释放资源  
         // closeAll();
    	 closAll(conn, statement, null);
     }  
     return affectedLine;  
 }  

 
 /***
  * 
  * @param args
  * @throws Exception
  * 
  * @desc:测试
  * 
  */
  public static void main(String[] args)throws Exception
  {
	  BaseDao b=new BaseDao();
	  Connection con=b.getCconnection();
	  System.out.println(con.isClosed());
 }
}
