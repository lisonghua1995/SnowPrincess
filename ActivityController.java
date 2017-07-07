package qiji.com.controller.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import qiji.com.authority.FireAuthority;
import qiji.com.config.Config;
import qiji.com.config.WebParam;
import qiji.com.controller.base.BaseListController;
import qiji.com.data.os.ActivityData;
import qiji.com.entity.os.OsActivity;
import qiji.com.entity.os.OsEditor;
import qiji.com.entity.os.OsPgc;
import qiji.com.service.activity.ActivityService;
import qiji.com.util.OssUploadUtil;
import qiji.com.util.StringUtil;
import qiji.com.util.UserUtil;
@Controller  
@RequestMapping("/activity") 
public class ActivityController extends BaseListController<OsActivity>{
	
	  @Autowired
	  protected ActivityService activityService;
	  @Autowired  
	  protected Config config;
	  
	   //进入列表
	    @RequestMapping("/Page")
	    public String Page() {
	    	return "/web/activity";
	   }
	       //进入编辑页面
	       @RequestMapping("/EditPage")
	       public String EditPage(Integer id,Model model) {
	    	    model.addAttribute("id", id);  
	    	    return "/web/activityedit";
	       }
	    
	  		public String queryListSql(String filter) {
	  			String sql = "select * from os_activity t where 1=1 and del_flag<>2" + filter;
	  			return sql;
	  		}
	  		// 查询数目
	  		public String queryListCountSql(String filter) {
	  			String sql = "select count(*) from os_activity t where 1=1 and del_flag<>2" + filter;
	  			return sql;
	  		}  
	  		//保存
	  		@RequestMapping(value = "/Save")
	  		@FireAuthority(isValidate=true)
	  		public String save(OsActivity activity,Integer editorid,@RequestParam(required=false) MultipartFile file
	  				,@RequestParam(required=false) MultipartFile file2,@RequestParam(required=false) MultipartFile file3){
	  			try {
	  				OsActivity activity0=new OsActivity();
	  				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
  			        String dateStr = format.format(new Date());
	  				//修改
	  				if(activity.getId()!=null)
	  				{
	  					activity0=activityService.findById(activity.getId());
	  					if(null != activity.getRemark()){
	  						activity0.setRemark(activity.getRemark());
	  					}
	  					if(null != activity.getName()){
	  						activity0.setName(activity.getName());
	  					}
	  					if(null != activity.getSort()){
	  						activity0.setSort(activity.getSort());
	  					}
	  					if(null != activity.getPublishdate()){
	  						activity0.setPublishdate(activity.getPublishdate());
	  					}
	  					if(null != activity.getClosedate()){
	  						activity0.setClosedate(activity.getClosedate());
	  					}
	  					if(null != activity.getSort()){
	  						activity0.setSort(activity.getSort());
	  					}
	  					if(null != activity.getState()){
	  						activity0.setState(activity.getState());
	  					}
	  					if(null != activity.getBody()){
	  						activity0.setBody(activity.getBody());
	  					}
	  					if(null != activity.getDescribe()){
	  						activity0.setDescribe(activity.getDescribe());
	  					}
	  					if(!StringUtil.empty(file.getOriginalFilename()))
	  					{
	  						OssUploadUtil.DeleteObject(activity0.getImageurl(),"activity头图","activity/"+activity0.getFolder());
	  						activity0.setImageurl(OssUploadUtil.UploadImageOSS_One(file, "activity头图", "activity/"+activity0.getFolder()));
	  						
	  					}	
	  					if(!StringUtil.empty(file2.getOriginalFilename()))
	  					{
	  						OssUploadUtil.DeleteObject(activity0.getImageurl2(),"activity列表","activity/"+activity0.getFolder());
	  						activity0.setImageurl2(OssUploadUtil.UploadImageOSS_One(file2, "activity列表", "activity/"+activity0.getFolder()));
	  					}
	  					if(!StringUtil.empty(file3.getOriginalFilename()))
	  					{
	  						OssUploadUtil.DeleteObject(activity0.getImageurl3(),"activity详细列表","activity/"+activity0.getFolder());
	  						activity0.setImageurl3(OssUploadUtil.UploadImageOSS_One(file3, "activity详细列表", "activity/"+activity0.getFolder()));
	  					}
	  					activity0.setUpdateDate(new Date());
	  					activity0.setTUserByUpdateBy(UserUtil.getLoginUser());
	  					activityService.update(activity0);
	  					MakeTemplate(activity0);
	  				}	
	  				else//新增
	  				{
	  					activity.setImageurl(OssUploadUtil.UploadImageOSS_One(file, "activity头图", "activity/"+dateStr));
	  					activity.setImageurl2(OssUploadUtil.UploadImageOSS_One(file2, "activity头图", "activity/"+dateStr));
	  					activity.setImageurl3(OssUploadUtil.UploadImageOSS_One(file3, "activity头图", "activity/"+dateStr));
	  					activity.setFolder(dateStr);
	  					activity.setCreateDate(new Date());
	  					activity.setTUserByCreateBy(UserUtil.getLoginUser());
	  					activity.setUpdateDate(new Date());
	  					activity.setLocation("ac"+OssUploadUtil.getCode());
	  					activity.setTUserByUpdateBy(UserUtil.getLoginUser());
	  					activity.setDelFlag(Integer.valueOf(WebParam.DELETE_1));
	  					activityService.save(activity);
	  					MakeTemplate(activity);
	  				}	
	  			} catch (Exception e) {
	  				msg=e.getCause().getMessage();
	  				// TODO Auto-generated catch block
	  				e.printStackTrace();
	  			}
	  			return "/web/activity";
	  		}
	  		 //返回数据
	  		 @RequestMapping("/List")
	  		 public @ResponseBody Map<String, Object> queryList(Integer page,Integer rows,Integer activityid ){
	  			List<ActivityData> wData = new ArrayList<ActivityData>();
	  			String filter="";
	  			if(activityid!=null)
	  			{
	  				filter+=" and t.id="+activityid;
	  			}
	  			list = (List<OsActivity>) activityService.getPagedDataBySQL(Integer.valueOf(page-1)*Integer.valueOf(rows), Integer.valueOf(rows), queryListSql(filter));
	  			Long Record;
	  			Record = activityService.getTotalCounts(queryListCountSql(filter));
	  			if (list != null) {
	  				for (int i = 0; i < list.size(); i++) {
	  					wData.add(new ActivityData(list.get(i),config));
	  				}
	  			}
	  			   Map<String, Object> result = new HashMap<String, Object>() ;
	  	            result.put("total",Record);
	  	            result.put("rows", wData);
	  			    return result;
	  		}  
	  		   @RequestMapping(value = "/Find")
	  		   @FireAuthority(isValidate=true)
	  			public @ResponseBody List<ActivityData> findByid(Integer id){
	  				List<ActivityData> wData = new ArrayList<ActivityData>();
	  				try {
	  					OsActivity activity=activityService.findById(id);
	  					wData.add(new ActivityData(activity,config));
	  				} catch (Exception e) {
	  					msg=e.getCause().getMessage();
	  					// TODO Auto-generated catch block
	  					e.printStackTrace();
	  				}
	  				    return wData;
	  		  }
	  		  @RequestMapping(value = "/Select")
	  			public @ResponseBody List<ActivityData> select(Integer state,Integer activityid){
	  				List<ActivityData> wData = new ArrayList<ActivityData>();
	  				try {
	  					String sql="select * from os_activity where 1=1";
	  					if(state!=null)
	  					{
	  						sql+=" and state="+state;
	  					}	
	  					list=activityService.findAllSQL(sql);
	  					if (list != null) {
	  						for (int i = 0; i < list.size(); i++) {
	  							wData.add(new ActivityData(list.get(i),config));
	  						}
	  					}
	  				} catch (Exception e) {
	  					msg=e.getCause().getMessage();
	  					// TODO Auto-generated catch block
	  					e.printStackTrace();
	  				}
	  				    return wData;
	  			} 
	  		  
	  			@RequestMapping("/template")
	  			public @ResponseBody  Map<String, Object> activityById(String id){
	  				instant =(OsActivity) activityService.findById(Integer.valueOf(id));
	  				  Map<String, Object> result = new HashMap<String, Object>() ;
	  		            result.put("content",instant.getBody());
	  				    return result;
	  			}
	  			
	  			@RequestMapping(value = "/Delete")
	  			@FireAuthority(isValidate=true)
	  			public @ResponseBody String delete(OsActivity activity){
	  				try {
	  					if(activity.getId()!=null)
	  					{
	  						activity=activityService.findById(activity.getId());
	  						activity.setUpdateDate(new Date());
	  						activity.setTUserByUpdateBy(UserUtil.getLoginUser());
	  						activity.setDelFlag(Integer.valueOf(WebParam.DELETE_2));
	  					}	
	  					activityService.update(activity);
	  				} catch (Exception e) {
	  					msg=e.getCause().getMessage();
	  					// TODO Auto-generated catch block
	  					e.printStackTrace();
	  				}
	  				return msg;
	  			} 
	               public String MakeTemplate(OsActivity o){
				   String page="";
				   String userimage="";
				   instant=o;
				   page=instant.getLocation();
				   String rootPath=request.getRealPath("/");
				   
				   File file = new File(rootPath+"/share/ac"+page+".html");
				   if(!file.exists()){//文件存在则
					   file.delete();
				   }
	        	   StringBuilder stringHtml = new StringBuilder();  
	        	   try{  
	        	      //打开文件  
	        	   PrintStream printStream = new PrintStream(new FileOutputStream(rootPath+"/share/"+page+".html"));  
	        	   //输入HTML文件内容
	        	   stringHtml.append("<html lang=\"zh-CN\"><head>"); 
	        	   stringHtml.append("<meta charset=\"GBK\">");  
	        	   stringHtml.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">"); 
	        	   stringHtml.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no\"/>"); 
	        	   stringHtml.append("<title>"+instant.getName()+"</title>"); 
	        	   stringHtml.append("<link href=\"favicon.ico\" rel=\"shortcut icon\" type=\"image/x-icon\" />"); 
	        	   stringHtml.append("<link href=\"favicon.ico\" rel=\"icon\" type=\"image/x-icon\" />"); 
	       		
	        	   stringHtml.append("<link href=\"css/style.css\" rel=\"stylesheet\">"); 
	        	   stringHtml.append("<link href=\"css/skin.css\" rel=\"stylesheet\">"); 
	        	   stringHtml.append("</head><body>"); 
	        	   stringHtml.append("<div class=\"main\"><div class=\"banner\"><img class=\"banimg\" src=\""+config.OSS_IP+"/"+config.OSS_Name+"/activity/"+instant.getFolder()+"/"+instant.getImageurl()+"\"><div class=\"bg\"><div class=\"bantext\"><div id=\"info\" class=\"info\" style=\"width:85%;\"><div class=\"inner\">");   
	        	   stringHtml.append("<p class=\"txt\" style=\"font-size:16px;margin-left:10px\">"+instant.getName()+"</p>");  
	        	   stringHtml.append("</div></div>"); 
	        	   stringHtml.append("<h4>"+instant.getDescribe()+"</h4>");
	        	   stringHtml.append("<h5>"+instant.getPublishdate()+"--"+instant.getClosedate()+"</h5></div></div></div>");  
	        	   stringHtml.append("<div class=\"con_text\">"+instant.getBody()+"</div>");  
	        	   
	        	   stringHtml.append("<footer style=\"float:left;position:static;\" onclick=\"window.location.href='"+config.WEBSITE+"/apk/Findos.apk'\"><p>Wanna know more about my little 'secret'</p><p>Find me at poseed</p>");
	        	   stringHtml.append(" <a href=\"#\"><img src=\"images/download.png\"></a>");  
	        	   stringHtml.append("</footer></div>");  
	        	   stringHtml.append(" <script type=\"text/javascript\" src=\"http://s7.addthis.com/js/300/addthis_widget.js#pubid=ra-585a15962673c074\"></script>");  
	        	   stringHtml.append("</body></html>");  
	        	          //将HTML文件内容写入文件中  
	        	   printStream.println(stringHtml.toString());
	        	   
			       }
	        	   catch (Exception e) {  
	        		   msg=e.getCause().getMessage();
	        	       e.printStackTrace();  
	        	   }
				   return msg;  
	           }
	    
	    
	    
	    
}
