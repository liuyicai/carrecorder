package org.iotsys.czpt;


import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.Map.Entry;


import java.io.IOException;
import java.lang.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.parser.*;
import org.jsoup.helper.*;
import org.jsoup.select.*;
import org.apache.commons.logging.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.*;
import org.iotsys.dao.BaseDao;


/***********
 * @author :alex.liu(marker.liu@foxmail.com)
 * @sine: 2014-12-31
 * @description:
 *    (1)解析URL:
 *       http://www.ctis.cn/lwlk/terminal/query.action?rt=read&visitType=detail&entity.id=1083
 *    (2) 持久化存储
 *      将终端详情信息存储如数据库(MySQL)
 *    (3) 统计分析（待实现）
 *      对终端的BOM、功能列表进行分析。用于产品预研、成本分析和竞品分析。
 *
 */

public class CarDevice {

	/***
	 * 网页字段名称与数据库表字段映射。
	 */
	private HashMap<String,String>   m_Ch2SegMap = new HashMap<String,String>(){
		{
			// 采集 URI
			// http://www.ctis.cn/lwlk/terminal/query.action?rt=read&visitType=detail&entity.id=1083

			// 终端基本信息

			put("id", "id");

			// 产品名称 汽车行驶记录仪
			// '产品名称', `cpmc`
			put("产品名称", "cpmc");

			// 终端型号 KS880-BD
			put("终端型号", "zdbm");

			// 厂家名称 福建凯氏电子科技有限公司
			put("厂家名称", "cjmc");

			// 厂家编号 70967
			put("厂家编号", "cjbh");

			// 适应车型 客、危、货
			put("适应车型", "sycx");

			// 检测机构名称 国家通信导航与北斗卫星应用产品质量监督检验中心
			put("检测机构名称", "jcjgmc");

			// 通过批次 第7批
			put("通过批次", "tgpc");

			// 具备可选功能项 电子运单、视频信息、通话、超时停车提醒、信息服务
			put("具备可选功能项", "jbkxglx");

			// 不具备可选功能项 无
			put("不具备可选功能项", "bjbkxglx");

			// 生产厂家名称 福建凯氏电子科技有限公司
			put("生产厂家名称", "sccjmc");

			// 定位模式 GPS/北斗双模
			put("定位模式", "dwms");

			// 通信方式 GSM
			put("通信方式", "txfs");

			// 厂家信息
			// 厂家所在省 福建省
			put("厂家所在省", "cjszsf");

			// 厂家所在市 福州市
			put("厂家所在市", "cjszcs");

			// 厂家联系人 李晓烽
			put("厂家联系人", "cjlxr");

			// 厂家联系电话 0591-83332828
			put("厂家联系电话", "cjlxdh");

			// 厂家传真 0591-83337878
			put("厂家传真", "cjfaxno");

			// 厂家邮政编码 350007
			put("厂家邮政编码", "cjyzbm");

			// 厂家Email 26097208@qq.com
			put("厂家Email", "cjemailaddr");

			// 厂家地址 福建省福州市仓山区仓山镇霞湖241-2号
			put("厂家地址", "cjdz");

			// 维修网点 维修网点列表
			put("维修网点", "wxwd");

			// 终端详细规格
			// 微处理器型号 LPC1765
			put("微处理器型号", "wclqxh");

			// 微处理器厂家 NXP
			put("微处理器厂家", "wclqcj");

			// 数据存储型号 FM25CL SST25VF
			put("数据存储型号", "sjccxh");

			// 数据存储厂家 RAMTRON SST
			put("数据存储厂家", "sjcccj");

			// 卫星定位模块型号 TD3020C
			put("卫星定位模块型号", "wxdwmkxh");

			// 卫星定位模块厂家 东莞泰斗微电子
			put("卫星定位模块厂家", "wxdwmkcj");

			// 通信模块型号 SIM900A
			put("通信模块型号", "txmkxh");

			// 软件版本 福建凯氏电子科技有限公司（V1.58）
			put("软件版本", "rjbb");

			// 外部设备接口 USB接口、CAN总线接口、RS232接口(2个)
			put("外部设备接口", "wbsbjk");

			// 信息采 集模块
			// 驾驶员身份识别 中公华通(VRD01 NZ)
			put("驾驶员身份识别", "jsysfsb");

			// 电子运单 中公华通(VRD01 NZ)
			put("电子运单", "dzyd");

			// 车辆CAN总线数据 NXP（PCA825C251T）
			put("车辆CAN总线数据", "clzxsj");

			// 车辆货载状态 仙童（CD4021BC）
			put("车辆货载状态", "clhzzt");

			// 图像 青青子木（QVA-031101-0A4）
			put("图像", "txmk");

			// 音频 希姆通信息科技有限公司（SIM900A）
			put("音频", "ypmk");

			// 视频
			put("视频", "spmk");

			// 行驶记录信息 "RAMTRON（FM25CL）SST（SST25VF）"
			put("行驶记录信息", "xsjlxx");

			put("保留", "reserved");

		}
	};

	// 网页中，出现这些关键字，应该跳过
	private ArrayList<String> m_SkipList = new ArrayList<String>() {
		{
			// 终端基本信息
			add("终端基本信息");
			// 厂家信息
			add("厂家信息");

			// 终端详细规格
			add("终端详细规格");

			// 信息采集模块
			add("信息采集模块");
		}
	};
	/***
	 * 
	 */
	
	private HashMap<String,String>   m_rKVMap  = new HashMap<String,String>();
	
	String m_szID = null;
	int    m_iID = 0;
	
	
	
	public HashMap<String,String> GetHashMap()
	{
		return m_rKVMap;
	}
	
	/***
	 * 
	 */
	public CarDevice(String szPageContent,int iID)
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
				
				if (true == m_SkipList.contains(szNameVal))
				{
					szLastText = "";
				}
				else
				{
					if (szLastText.length() == 0) 
					{
						szLastText = szNameVal;
					} 
					else
					{
						final String szcpmc = "产品名称";
						String szKey = szLastText;
						String szVal = szNameVal;
						
						// 字段：平台名称，其值必需不为空。
						if ((szKey.equals(szcpmc)) && (szVal.isEmpty()))
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

	/***
	 * 保存到数据库中
	 */
	public int Save2DB()
	{
		String szSQL = "replace into tb_cardevice ";
		
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
