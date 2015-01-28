package org.iotsys.czpt;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.IOException;
import java.lang.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.apache.commons.logging.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;


import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;

import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.*;

import org.iotsys.dao.BaseDao;


/**
 * 
 * @author:alex.liuyicai(marker.liu@foxmail.com)
 * @sine:2014-12-31
 * @description：
 *   （1）解析URL（道路监控系统平台）：
 *   
 *   （2）持久化
 *   
 *   （3）统计分析（待实现）
 *     针对进行道路监控平台，研发厂家、运营事业单位和运营企业等进行分析。
 *
 */
public class CarMonitorPlat {

	/***
	 * 中文名称与数据库表中字段映射表
	 * 
	 */
	private HashMap<String,String>   m_Ch2SegMap = new HashMap<String,String>(){
			{
		     put("id","id");
		     put("平台名称","ptmc");
		     put("平台编号","ptbm");
		     put("平台版本","ptbb");
		     put("平台类别","ptlx");
		     put("检测机构名称","jcjgmc");
		     put("检测报告编号","jcbgbm");
		     put("申请机构名称","sqjgmc");
		     put("申请机构地址","sqjgdz");
		     put("邮编","yzbm");
		     put("联系人","lianxiren");
		     put("手机","shouji");
		     put("联系电话","lxdh");
		     put("电子邮件","emailaddr");
		     put("传真","faxno");
		     put("内部主管部门","nbzgbm");
		     put("部门负责人","bmfzr");
		     put("保留","reserver1");		
		
			}
			};
			
			
	private HashMap<String,String>   m_rKVMap  = new HashMap<String,String>();
	
	String m_szID = null;
	int    m_iID = 0;
	

	/**
	 * 
	 * @param szPageContent
	 */
	
	CarMonitorPlat(CarMonitorPlat  srcobj)
	{
		
		HashMap<String,String>  tmpMap = srcobj.GetHashMap();
		this.m_rKVMap.clear();
		
		Iterator<Entry<String,String>> entrySetIterator=tmpMap.entrySet().iterator();  
		
        while(entrySetIterator.hasNext()){  
            Entry<String,String> entry=entrySetIterator.next();  
            
            this.m_rKVMap.put(entry.getKey(),entry.getValue());
        }    
        
        this.m_szID = srcobj.GetIDString();
        this.m_iID  = new Integer(m_szID).intValue();
	}
	
    CarMonitorPlat(String szPageContent,int iID)
	{
		Document doc = Jsoup.parse(szPageContent);
		String szHtmlClsName = "w100";

		Elements tablelnks = doc.getElementsByClass(szHtmlClsName);
		m_rKVMap.clear();
		
		for (Element src : tablelnks) 
		{
			Elements tags = src.getElementsByTag("td");
			String szLastText = "";
			for (Element tt : tags)
			{
				final String szPTinfo = "平台基本信息";
				final String szJGinfo = "申请机构信息";
				
				String szNameVal = tt.text();
				
				// 字段名稱中，特殊值需要过滤
				if (false == (szNameVal.equals(szPTinfo) || szNameVal.equals(szJGinfo))) 
				{
					// System.out.println("tags Text = " + tt.text());
					// 字段的中文名稱
					if (szLastText.length() == 0) 
					{
						szLastText = szNameVal;
					} 
					else
					{
						// 特殊字段不能为空；
						// 平台名称
						final String szPtmc = "平台名称";
						
						String szKey = szLastText;
						String szVal = szNameVal;
						
						// 字段：平台名称，其值必需不为空。
						if ((szKey.equals(szPtmc)) && (szVal.isEmpty()))
						{
							break;
						}
					
						// 字段值：
						String szRealKey = m_Ch2SegMap.get(szKey);
						
						if ((null != szRealKey) && (false == szRealKey.isEmpty()))
						{
							// System.out.println("key:value="+szRealKey+":"+szVal);
							m_rKVMap.put(szRealKey, szVal);
						}
						
						szLastText = "";
					}
				} else {
					szLastText = "";
				}
			}
		}
		
		if (false == m_rKVMap.isEmpty())
		{
			m_szID = new Integer(iID).toString();
			m_rKVMap.put("id", m_szID);	
			m_iID = iID;
		}				
		
    }
	
	
	public HashMap<String,String> GetHashMap()
	{
		return m_rKVMap;
	}
	 
	public String GetIDString()
	{
		return m_szID;
	}
	
	/***
	 * 
	 */
	
	public int Save2DB()
	{
		String szSQL = "replace into tb_syspt ";
		
		String szSegList= new String ();

		String szValdot = new String ();
		
		ArrayList<String> list = new ArrayList<String>();
				
		Iterator<Entry<String,String>> entrySetIterator=m_rKVMap.entrySet().iterator();  

		int iSum=0;
			
		int iSegNum =0;
        while(entrySetIterator.hasNext())
        {  
            Entry<String,String> entry=entrySetIterator.next();
            if (0 == iSegNum)
            {
            	szSegList = "(" + 	 entry.getKey();
            	szValdot  = "(" +  "?";
            }
            else
            {
            	szSegList =szSegList + "," + entry.getKey();
            	szValdot  = szValdot + "," +  "?";
            }
            list.add(iSegNum,entry.getValue());
            
            iSegNum ++;
        }    
        /**
         * 
         */
        szSegList = szSegList +")";
        szValdot  = szValdot +")";   
        
        szSQL = szSQL + szSegList + " values " + szValdot;
        // System.out.println("SQL="+szSQL);
       
        BaseDao ssDao = new BaseDao();
        iSum = ssDao.executeUpdate(szSQL,list);		
        return iSum;
	}

}
