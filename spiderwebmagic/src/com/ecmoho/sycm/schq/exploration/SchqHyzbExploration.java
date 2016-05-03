package com.ecmoho.sycm.schq.exploration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import com.ecmoho.base.Util.StringUtil;

/**
 * @author gusy
 * 市场行情——行业直播
 */
@Component("schqHyzbExploration")
public class SchqHyzbExploration extends SchqExploration{
	
	@Override
	public List<HashMap<String,String>> getUrlList(String account,String childAccountArr,int days) {
		 List<HashMap<String,String>> urlList=new ArrayList<HashMap<String,String>>();
		 HashMap<String,String> urlMap=null;
		 //存储日期
		 String nowDateStr=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
		 Map<String, Object> taskMap=schqDbcom.getSpider(account);
		 String accountid=StringUtil.objectVerString(taskMap.get("id"));
		 List<HashMap<String, String>> hymlList=SchqUrlUtil.getHyml(schqDbcom, schqHeaderBean).subList(0, 1);
		 
    	 for (int d = 1; d <=Integer.valueOf(days) ; d++) {
		    Calendar   cal   =   Calendar.getInstance();
		    cal.add(Calendar.DATE,   -d);
		    //d天之前
		    String yesterdayday = new SimpleDateFormat( "yyyy-MM-dd").format(cal.getTime());
		    List<Map<String, Object>> hyzbDpssList=schqDbcom.getSpiderChildList(childAccountArr);
		    for(int i=0;hyzbDpssList!=null&&i<hyzbDpssList.size();i++){
			    Map<String, Object> hyzbDpssMap=hyzbDpssList.get(i);
			    
			    String geturl=StringUtil.objectVerString(hyzbDpssMap.get("geturl"));
			    String childAccount=StringUtil.objectVerString(hyzbDpssMap.get("child_account"));
			    for(Map<String,String> mlMap:hymlList){
					    String targetUrl=geturl.replaceAll("##D##", yesterdayday).replaceAll("##CID##", mlMap.get("id"));	
					    //循环终端来源（0,所有终端，1，PC端，2，无线端）
					    for(int j=0;j<=2;j++){
						   //循环支付金额分段类型（0,分时段趋势图，1，时段累计图）
						    for(int k=0;k<=1;k++){
						       urlMap=new HashMap<String,String>();
							   String finalTargetUrl=targetUrl.replaceAll("##DE##", j+"").replaceAll("##T##", k+"").replaceAll("##L##", mlMap.get("level"));
	//						   System.out.println("finalTargetUrl："+finalTargetUrl);
							   urlMap.put("childAccount", childAccount);
							   urlMap.put("targetUrl", finalTargetUrl);
							   urlMap.put("accountid",accountid);
							   urlMap.put("create_at", yesterdayday);
							   urlMap.put("level", mlMap.get("level"));
							   urlMap.put("item1", mlMap.get("item1"));
							   urlMap.put("item2", mlMap.get("item2"));
							   urlMap.put("item3", mlMap.get("item3"));
							   urlMap.put("device", j+"");
							   urlMap.put("timeslotType", k+"");
							   urlMap.put("log_at", nowDateStr);
							   urlList.add(urlMap);
						    }
					   }
	    	      }
    	      }
	     }
		return urlList;
	}
	
}
