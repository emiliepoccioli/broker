package com.dell.cloud.dasein;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.UriBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import broker.commons.Blob;

public class Utils {

	public static final String JSON_BLOB_CLOUD_NAME = "cloudName";
	public static final String JSON_BLOB_USER_ID = "userId";
	public static final String JSON_BLOB_ID = "id";
	public static final String JSON_BLOB_NAME = "fileName";
	public static final String JSON_BLOB_BUCKET = "container";
	public static final String JSON_BLOB_STORAGE_ENDPOINT = "storageEndpoint";
	public static final String JSON_BLOB_STORAGE_REGION = "regionId";
	public static final String JSON_BLOB_LAST_MODIFIED = "lastModified";
	public static final String JSON_BLOB_POLICIES = "policies";
	public static final String JSON_BLOB_TAGS = "tags";
	public static final String JSON_BLOB_SIZE = "size";
	
	public static final String APPLICATION_JSON = "application/json";
	public static final String BROKER_SYNC_REST_ENDPOINT = "http://10.49.57.109:8080/BrokerSync/rest";

	public static String getClientIpAddr(HttpServletRequest request) {  
        String ip = request.getHeader("X-Forwarded-For");  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("WL-Proxy-Client-IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_CLIENT_IP");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");  
        }  
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {  
            ip = request.getRemoteAddr();  
        }  
        return ip;  
    }  

	public static String getCreateDate(Date creationDate){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(creationDate);
	}

	public static String getCurrentTimeStamp(){
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(cal.getTime());
	}


	public static List<Blob> getBlobsListFromDaseIn(String restUrl){
		ArrayList<Blob> blobsByUser = null;
		try{
			Client client = Client.create();
			URI uri = UriBuilder.fromPath(restUrl).build();
			WebResource webResource = client.resource(uri);
			ClientResponse clientResponse = webResource.accept(APPLICATION_JSON).get(ClientResponse.class);

			JSONArray jsonArray = new JSONArray(clientResponse.getEntity(String.class));
			blobsByUser = (ArrayList<Blob>)Utils.getBlobsListFromJson(jsonArray);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return blobsByUser;
	}

	public static List<Blob> getBlobsListFromJson(JSONArray jsonArray){
		List<Blob> blobsByUser = new ArrayList<Blob>();
		try{
			for(int i = 0; i < jsonArray.length();i++){
				Blob blob = new Blob();
				JSONObject jsonBlob = jsonArray.getJSONObject(i);
				blob.setContainer(jsonBlob.getString(JSON_BLOB_BUCKET));
				blob.setFileName(jsonBlob.getString(JSON_BLOB_NAME));
				blob.setSize(Long.parseLong(jsonBlob.getString(JSON_BLOB_SIZE)));
				blobsByUser.add(blob);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return blobsByUser;
	}
}
