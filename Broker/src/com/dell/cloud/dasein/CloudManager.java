package com.dell.cloud.dasein;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dasein.cloud.CloudProvider;
import org.dasein.cloud.ProviderContext;
import org.dasein.cloud.storage.BlobStoreSupport;
import org.dasein.cloud.storage.StorageServices;
import org.json.JSONArray;
import org.json.JSONObject;

import broker.commons.*;

import com.dell.ejb.clients.CassandraManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class CloudManager {

	public static final String APPLICATION_JSON = "application/json";
	public static final String BROKER_REST_ENDPOINT = "http://10.49.57.109:8080/BrokerServer/rest";


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
	
	//STORAGE SERVICE JSON FIELDS
	public static final String JSON_STORAGE_CONTAINER = "container";
	public static final String JSON_STORAGE_CLOUDNAME = "cloudName";
	public static final String JSON_STORAGE_ENDPOINT = "storageEndpoint";
	public static final String JSON_STORAGE_NAME = "storageName";
	public static final String JSON_STORAGE_REGION = "storageRegion";


	/*public List<Blob> getBlobs(String userId) throws Exception{
		String restUrl = BROKER_REST_ENDPOINT;
		//Append User Id
		restUrl += "/user/" + userId + "/object";
		return getBlobsFromRestJson(restUrl);
	}


	public List<Blob> getBlobs(String userId, String storageName) throws Exception{
		String restUrl = BROKER_REST_ENDPOINT;
		//Append User Id and Storage Name
		restUrl += "/user/" + userId + "/object/cloud/" + storageName;
		return getBlobsFromRestJson(restUrl);	
	}
	
	public Blob getBlob(String userId, String blobId) throws Exception{
		String restUrl = "http://10.49.57.109:8080/BrokerServer/rest";
		//Append userId and blob id
		restUrl += "/user/" + userId + "/object/" + blobId;
		return getBlobFromRestJson(restUrl);
	}*/
	
	public List<Blob> getBlobs(String userId){
		System.out.println("** GET BLOBS **");
		return CassandraManager.getCassandra().listBucket(userId);
	}

	public BlobStoreSupport getBlobStoreSupport(String cloudName, String storageEndpoint, String regionId, CloudCredentials cloudCredentials){

		try{
			CloudName enumCloudName = CloudName.valueOf(cloudName.toUpperCase());
			String cloudClass = getCloudClass(enumCloudName);
			CloudProvider cloud = (CloudProvider)Class.forName(cloudClass).newInstance();

			//Set cloud provider context
			ProviderContext context = new ProviderContext();
			switch(enumCloudName){
			case AMAZON: 
				context.setEndpoint(cloudName); break;
			case SWIFT : 
				context.setEndpoint(storageEndpoint); break;
			}
			//Common cloud attributes
			context.setAccountNumber(cloudCredentials.getAccountNumber());
			context.setRegionId(regionId);
			context.setStorageEndpoint(storageEndpoint);
			context.setAccessKeys(cloudCredentials.getPublicKey().getBytes(), cloudCredentials.getPrivateKey().getBytes());

			cloud.connect(context);
			StorageServices storageServices =  cloud.getStorageServices();

			if(storageServices != null){
				return storageServices.getBlobStoreSupport();
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}


	public void uploadFile(String cloudName, String bucketName, String storageEndpoint, String regionId, String fileName, File file) throws CredentialsException{
		CloudCredentials cloudCredentials = CredentialsManager.GetCredentials(CloudName.valueOf(cloudName));
		BlobStoreSupport blobStore = getBlobStoreSupport(cloudName,storageEndpoint, regionId, cloudCredentials);
		if(blobStore != null){
			try {
				blobStore.upload(file, bucketName, fileName.toLowerCase(), false, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void uploadFile(String cloudName, String bucketName, String storageEndpoint, String regionId, String fileName, File file, CloudCredentials cloudCredentials){
		BlobStoreSupport blobStore = getBlobStoreSupport(cloudName,storageEndpoint, regionId, cloudCredentials);
		if(blobStore != null){
			try {
				blobStore.upload(file, bucketName, fileName.toLowerCase(), false, null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/*public void uploadFileWithPolicies(String userId, File fileToUpload, String policy, String argument) throws CredentialsException{
		String fileName = fileToUpload.getName().toLowerCase();
		//Build REST URL
		String restUrl = "http://10.49.57.109:8080/BrokerServer/rest";
		restUrl += "/user/" + userId;
		restUrl += "/blob/" + fileName;
		restUrl += "/policy/" + policy;
		restUrl += "/argument/" + argument;

		//Get the appropriate storage service given the policy
		ClientResponse clientResponse = getClientResponse(restUrl, APPLICATION_JSON);
		JSONObject jsonObject = new JSONObject(clientResponse.getEntity(String.class));
		String storageName = jsonObject.getString(JSON_STORAGE_NAME);
		String bucketName = jsonObject.getString(JSON_STORAGE_CONTAINER);
		String storageEndpoint = jsonObject.getString(JSON_STORAGE_ENDPOINT);
		String regionId = jsonObject.getString(JSON_STORAGE_REGION);
		uploadFile(storageName, bucketName, storageEndpoint, regionId, fileName, fileToUpload);
	}
	
	public void uploadFileWithPolicies(String userId, File fileToUpload, String policy, String argument) throws Exception{
		
		String fileName = fileToUpload.getName().toLowerCase();
		//Build REST URL
		String restUrl = "http://10.49.57.109:8080/BrokerServer/rest";
		restUrl += "/user/" + userId;
		restUrl += "/blob/" + fileName;
		restUrl += "/policy/" + policy;
		restUrl += "/argument/" + argument;
		
		//Get the appropriate storage service given the policy
		ClientResponse clientResponse = getClientResponse(restUrl, APPLICATION_JSON);
		JSONObject jsonObject = new JSONObject(clientResponse.getEntity(String.class));
		String storageName = jsonObject.getString(JSON_STORAGE_NAME);
		String bucketName = jsonObject.getString(JSON_STORAGE_CONTAINER);
		String storageEndpoint = jsonObject.getString(JSON_STORAGE_ENDPOINT);
		String regionId = jsonObject.getString(JSON_STORAGE_REGION);

		uploadFile(storageName, bucketName, storageEndpoint, regionId, fileName, fileToUpload);
	}
	


	public  void deleteFile(String fileName, String cloudName, String storageEndpoint, String regionId, String bucketName, boolean multipart) throws CredentialsException{
		CloudCredentials cloudCredentials = CredentialsManager.GetCredentials(CloudName.valueOf(cloudName));
		BlobStoreSupport blobStore = getBlobStoreSupport(cloudName,storageEndpoint, regionId, cloudCredentials);
		if(blobStore != null){
			try {
				blobStore.removeFile(bucketName, fileName.toLowerCase(), multipart);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private ClientResponse getClientResponse(String restUrl, String applicationType) throws Exception{
		Client client = Client.create();
		WebResource webResource = client.resource(restUrl);
		ClientResponse clientResponse = webResource.accept(applicationType).get(ClientResponse.class);

		if (clientResponse.getStatus() != 200) {
			throw new Exception("Failed : HTTP error code : " + clientResponse.getStatus());
		}
		return clientResponse;
	}


	private List<Blob> getBlobsFromRestJson(String restUrl) throws Exception{
		ClientResponse clientResponse = getClientResponse(restUrl, APPLICATION_JSON);
		JSONArray jsonArray = new JSONArray(clientResponse.getEntity(String.class));
		return getBlobsFromJsonArray(jsonArray);	
	}
	
	private Blob getBlobFromRestJson(String restUrl) throws Exception{
		ClientResponse clientResponse = getClientResponse(restUrl, APPLICATION_JSON);
		JSONObject jsonObject = new JSONObject(clientResponse.getEntity(String.class));
		return getBlobFromJson(jsonObject);	
	}

	private List<Blob> getBlobsFromJsonArray(JSONArray jsonArray) throws Exception{
		List<Blob>  blobs = new ArrayList<Blob>();
		for(int i = 0; i < jsonArray.length();i++){
			Blob blob = new Blob();
			JSONObject jsonBlob = jsonArray.getJSONObject(i);
			blob.setCloudName(jsonBlob.getString(JSON_BLOB_CLOUD_NAME));
			blob.setContainer(jsonBlob.getString(JSON_BLOB_BUCKET));
			blob.setFileName(jsonBlob.getString(JSON_BLOB_NAME));
			blob.setRegionId(jsonBlob.getString(JSON_BLOB_STORAGE_REGION));
			blob.setStorageEndpoint(jsonBlob.getString(JSON_BLOB_STORAGE_ENDPOINT));
			blob.setId(jsonBlob.getString(JSON_BLOB_ID));
			blob.setUserId(jsonBlob.getString(JSON_BLOB_USER_ID));
			System.out.println("json obj["+ i+ "] : " + jsonBlob);
			blobs.add(blob);
		}
		return blobs;
	}
	private Blob getBlobFromJson(JSONObject jsonObject) throws Exception{
		Blob blob = new Blob();
		blob.setCloudName(jsonObject.getString(JSON_BLOB_CLOUD_NAME));
		blob.setContainer(jsonObject.getString(JSON_BLOB_BUCKET));
		blob.setFileName(jsonObject.getString(JSON_BLOB_NAME));
		blob.setRegionId(jsonObject.getString(JSON_BLOB_STORAGE_REGION));
		blob.setStorageEndpoint(jsonObject.getString(JSON_BLOB_STORAGE_ENDPOINT));
		blob.setId(jsonObject.getString(JSON_BLOB_ID));
		blob.setUserId(jsonObject.getString(JSON_BLOB_USER_ID));
		return blob;
	}*/
	
	public static String getCloudClass(CloudName cloudName) throws Exception{
		switch(cloudName){
		case AMAZON :  return broker.commons.CloudPackage.AMAZON.getValue();
		case AZURE:  return CloudPackage.AZURE.getValue();
		case SWIFT: return CloudPackage.SWIFT.getValue();
		default: throw new Exception("Cannot Retrieve Dasein associated plugin for: " + cloudName);
		}
	}

}
