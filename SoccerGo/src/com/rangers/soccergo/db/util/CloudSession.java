package com.rangers.soccergo.db.util;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Component;




@Component("session")
public class CloudSession {
	private static URIBuilder uriBuilder = new URIBuilder();
	private static URI uri;
	private static String uriPath = "/1.1/classes/";
	static{		
				
	}
	private <T> void init(T t){
		String path = StringUtil.getClassName(t.getClass().toString());
		path = uriPath + path;
		try {
			uri = new URIBuilder()
			.setScheme("https")
			.setHost("leancloud.cn")
			.setPort(443)
			.setPath(path).build();
		} catch (URISyntaxException e) {
			
			e.printStackTrace();
		} 	
		System.out.println("uri 地址： "+uri);
	}
	private <T> void init(T t,Serializable id){
		String path = StringUtil.getClassName(t.toString());
		path = uriPath + path +"/"+ id;
		try {
			uri = new URIBuilder()
			.setScheme("https")
			.setHost("leancloud.cn")
			.setPort(443)
			.setPath(path).build();
		} catch (URISyntaxException e) {
			
			e.printStackTrace();
		} 
		//uri地址
		System.out.println("uri 地址： "+uri);
		
	}
	//保存
	public <T> void save(T t){		
		//初始化uri
		init(t);
		//将实体转化为json格式
		String json = JsonUtil.getInstance().obj2json(t);
		//执行post方法
		HttpClientUtil.getInstance().sendPostRequest(uri, json);		
	}
	//更新
	public <T> void update(T t){
		this.save(t);
	}
	
	//按照id查询
	public Object get(Class entity,Serializable id){
		init(entity,id);
		String res = HttpClientUtil.getInstance().sendGetRequest(uri);
		//System.out.println(res);
		if(res == null || res.equals("")||res.equals("{}")){
			return null;
		}
		else{			
			return JsonUtil.getInstance().json2obj(res, entity);
		}
	}
	//查询整个表  
	//返回 null 表示 表中没有值或失败
	public <T> List<T> get(T t){
		init(t);
		String res = HttpClientUtil.getInstance().sendGetRequest(uri);
		//System.out.println(res);		
		if(res == null || res.equals("")||res.equals("{\"results\":[]}")){
			return null;
		}
		else{
			JsonResult<T> jr = (JsonResult<T>) JsonUtil.getInstance().json2obj(res, JsonResult.class);		
			return jr.getResults();
		}	
	}
	//执行cql语句
	public CloudQuery executeQuery(String cql){
		uriBuilder.setScheme("https")
        .setHost("leancloud.cn")
        .setPort(443)
        .setPath("/1.1/cloudQuery");		
		return new CloudQuery(uriBuilder,cql);
	}
	//删除操作
	public <T> void delete(T t){
		//将实体转化为json格式
		String json = JsonUtil.getInstance().obj2json(t);
		Map<String,String> map = (Map<String, String>) JsonUtil.getInstance().json2obj(json, Map.class);
		String id = map.get("objectId");
		System.out.println(id);
		//初始化uri
		init(t.getClass(),id);
		HttpClientUtil.getInstance().sendDeleteRequest(uri);
	}
}
