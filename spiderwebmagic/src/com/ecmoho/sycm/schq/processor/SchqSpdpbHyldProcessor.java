package com.ecmoho.sycm.schq.processor;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import javax.annotation.Resource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ecmoho.base.Util.StringUtil;
import com.ecmoho.sycm.schq.exploration.SchqSpdpbHyldExploration;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
/**
 * 
 * @author gusy
 * 市场行情--商品店铺榜--行业粒度
 */
@Component("schqSpdpbHyldProcessor")
public class SchqSpdpbHyldProcessor extends  SchqProcessor{
//	@Resource(name="schqDbcom")
//	private  SchqDbcom schqDbcom;
//	@Resource(name="schqHeaderBean")
//	private  HeaderBean schqHeaderBean;
	@Resource(name="schqSpdpbHyldExploration")
	private SchqSpdpbHyldExploration schqSpdpbHyldExploration;
//	@Resource(name="schqCpfxCpphProcessor")
//  private SchqCpfxCpphProcessor schqCpfxCpphProcessor;
	
	public  void start(SchqSpdpbHyldProcessor schqSpdpbHyldProcessor){
		
		 //获取店铺列表
  		 List<Map<String, Object>> taskList=schqDbcom.getSpidersTaskList("sycm");
  		 for(int i=0;taskList!=null&&i<taskList.size();i++){
  			 Map<String, Object> taskMap=taskList.get(i);
  			 String id=StringUtil.objectVerString(taskMap.get("id"));
  			 if(id.equals("11")){
  				 String account=StringUtil.objectVerString(taskMap.get("account"));
  				 String refer_cookie=StringUtil.objectVerString(taskMap.get("reffer_cookie"));
  				 schqHeaderBean.setCookie(refer_cookie);
//  				'spdpb-hyld-rxsp','spdpb-hyld-llsp','spdpb-hyld-rxdp','spdpb-hyld-lldp'
  				 List<HashMap<String,String>> urlHyzbList=schqSpdpbHyldExploration.getSpdpbHyldUrlList(account,"'spdpb-hyld-lldp'");
  				 System.out.println("urlHyzbList.size()："+urlHyzbList.size());
  			     for(int j=0;j<urlHyzbList.size();j++){
  			    	 Map<String,String> map=urlHyzbList.get(j);
  			    	 schqHeaderBean.setUrlMap(map);
//  			     System.out.println(map.get("targetUrl"));
  			    	 Spider.create(schqSpdpbHyldProcessor).addUrl(map.get("targetUrl")).run();
  			     }
  			 }
  		 }
	}
	
	public static void main(String[] args) {
		ApplicationContext ac =new ClassPathXmlApplicationContext("conf/applicationContext.xml");
		SchqSpdpbHyldProcessor schqSpdpbHyldCpphProcessor=(SchqSpdpbHyldProcessor) ac.getBean("schqSpdpbHyldProcessor");
		schqSpdpbHyldCpphProcessor.start(schqSpdpbHyldCpphProcessor);
	}
	@Override
	public Site getSite() {
		return super.getSite().setSleepTime(10000);
		
	}
    @Override
	public void process(Page page) {
    	
	   	
	 
    	   String jsonStr=page.getJson().toString();
           System.out.println(jsonStr);
	       JSONObject finalTargetJsonObject=JSON.parseObject(jsonStr);
	       List<Map<String,String>> dataList= new ArrayList<Map<String,String>>();
		   Map<String,String> dataMap=null;
		   JSONArray dataJsonArray=null;
		   JSONObject dataJsonObject=null;
		   Map<String,String> urlMap=schqHeaderBean.getUrlMap();
		   String childAccount=urlMap.get("childAccount");
		   dataJsonArray=finalTargetJsonObject.getJSONObject("content").getJSONObject("data").getJSONArray("data");
	       for(int i=0;i<dataJsonArray.size();i++){
	    	   dataJsonObject=dataJsonArray.getJSONObject(i);
	    	   dataMap=getDataMap(dataMap, urlMap);
	    	   for(String key:dataJsonObject.keySet()){
		    	   dataMap.put(key, dataJsonObject.getString(key));
		       }
	    	   dataList.add(dataMap);
	       }
		   switch (childAccount) {
		       //商品店铺榜_行业粒度_热销商品榜
		       case "spdpb-hyld-rxsp":
		    	       schqDbcom.addList(dataList, "commodityshop_industry_hotcommodityranking");
			           break;
			   //商品店铺榜_行业粒度_流量商品榜     
		       case "spdpb-hyld-llsp":
				      schqDbcom.addList(dataList, "commodityshop_industry_flowcommodityranking");  
			          break;
			   //商品店铺榜_行业粒度_热销店铺榜     
		       case "spdpb-hyld-rxdp":
		    	      schqDbcom.addList(dataList, "commodityshop_industry_hotshopranking");  
			          break;
			   //商品店铺榜_行业粒度_流量店铺榜   
		       case "spdpb-hyld-lldp":
		    	       schqDbcom.addList(dataList, "commodityshop_industry_flowshopranking");  
			           break;
		       default:
			           break;
	        }
 
	}
    //获取数据附加信息
    public static Map<String,String> getDataMap(Map<String,String> dataMap,Map<String,String> urlMap){
 	       dataMap=new HashMap<String,String>();
		   dataMap.put("accountid",urlMap.get("accountid"));
		   dataMap.put("create_at", urlMap.get("create_at"));
		   dataMap.put("level", urlMap.get("level"));
		   dataMap.put("item1", urlMap.get("item1"));
		   dataMap.put("item2", urlMap.get("item2"));
		   dataMap.put("item3", urlMap.get("item3"));
		   dataMap.put("device", urlMap.get("device"));
		   dataMap.put("seller", urlMap.get("seller"));
		   dataMap.put("log_at", urlMap.get("log_at"));
		   return dataMap;
     }
}
