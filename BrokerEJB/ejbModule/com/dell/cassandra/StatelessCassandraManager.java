package com.dell.cassandra;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Stateless;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import broker.commons.Blob;
import broker.commons.Policies;
import broker.commons.StorageService;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.utils.UUIDs;
import com.dell.exceptions.BrokerException;
import com.dell.exceptions.PolicyException;



@Stateless
public class StatelessCassandraManager implements LocalCassandra, RemoteCassandra{


	private static final long serialVersionUID = 1L;

	private Session session;
	private static Cluster cluster;

	public static final String CASSANDRA_ENDPOINT = "10.49.57.110";
	public static final String CASSANDRA_KEYSPACE = "demodb";

	//BLOB TABLE FIELDS
	public static final String BLOB_USER_ID = "user_id";
	public static final String BLOB_ID = "id"; 
	public static final String BLOB_NAME = "name"; 
	public static final String BLOB_LAST_MODIFIED = "last_modified";
	public static final String BLOB_STORAGE_NAME = "storage_name";
	public static final String BLOB_STORAGE_ENDPOINT = "endpoint"; 
	public static final String BLOB_REGION_ID = "regionId"; 
	public static String BLOB_CONTAINER = "container"; 
	public static String BLOB_SIZE = "size"; 
	public static String BLOB_POLICIES = "policies"; 
	public static String BLOB_TAGS = "tags";

	//Cassandra fields
	public static final String UPLOADS_BLOB_ID = "blob_id";
	public static final String UPLOADS_USER_ID = "user_id";
	public static final String UPLOADS_STORAGE_NAME = "storage_name";
	public static final String UPLOADS_STORAGE_ENDPOINT = "storage_endpoint";
	public static final String UPLOADS_STORAGE_REGION = "storage_region";
	public static final String UPLOADS_FILE_NAME = "file_name";
	public static final String UPLOADS_CONTAINER_NAME = "container_name";
	public static final String UPLOADS_STATUS = "status";
	public static final String UPLOADS_STATUS_MODIFIED = "status_modified";

	//Cassandra fields: PENDING DELETIONS
	public static final String DELETIONS_BLOB_ID = "blob_id";
	public static final String DELETIONS_USER_ID = "user_id";
	public static final String DELETIONS_STORAGE_NAME = "storage_name";
	public static final String DELETIONS_STORAGE_ENDPOINT = "storage_endpoint";
	public static final String DELETIONS_STORAGE_REGION = "storage_region";
	public static final String DELETIONS_FILE_NAME = "file_name";
	public static final String DELETIONS_CONTAINER_NAME = "container_name";
	public static final String DELETIONS_STATUS = "status";
	public static final String DELETIONS_STATUS_MODIFIED = "status_modified";

	//STORAGE SERVICE TABLE FIELDS
	public static String STORAGE_NAME = "name";
	public static String STORAGE_CLOUD_NAME = "cloud_name";
	public static String STORAGE_ENDPOINT = "endpoint";
	public static String STORAGE_REGION_ID = "region_id";
	public static String STORAGE_BUCKETS = "buckets";

	//STORAGES BY USER TABLE FIELDS
	public static final String USER_STORAGE_USERID = "user_id";
	public static final String USER_STORAGE_NAME = "storage_name";
	public static final String USER_STORAGE_ENDPOINT = "endpoint"; 
	public static final String USER_STORAGE_REGIONID = "regionid"; 
	public static String USER_STORAGE_BUCKETS = "buckets"; 
	public static String USER_STORAGE_ZONE = "zone"; 

	//USERS TABLE FIELDS
	public static final String USERS_USERID = "id";
	public static final String USERS_LOGIN = "username";
	public static final String USERS_PASSWORD = "password"; 
	public static final String USERS_ZONE = "zone"; 
	public static String USERS_COUNTRY = "country"; 

	public static final String PENDING_STATUS = "PENDING";

	//POLICIES JSON FIELDS
	public static final String POLICY_STATEMENT = "Statement";
	public static final String POLICY_SID = "Sid";
	public static final String POLICY_EFFECT = "Effect";
	public static final String POLICY_ACTION = "Action";
	public static final String POLICY_RESOURCES = "Resource";
	public static final String POLICY_CONDITIONS = "Condition";
	public static final String POLICY_CONDITIONS_IPADD = "IpAddress";
	public static final String POLICY_CONDITIONS_BUCKET = "Bucket";
	public static final String POLICY_ACCOUNTS = "Account";



	public void setBlobUploadStatus(String blobId, String userId, String status){
		String blobUploadedQ = "UPDATE broker.uploads set status = '" + status + "' where blob_id = ? and user_id = ?;";
		//PreparedStatement prepStatement = prepareQuery(blobUploadedQ);
		//BoundStatement boundStatement = new BoundStatement(prepStatement);
		//boundStatement.bind(UUID.fromString(blobId), UUID.fromString(userId));
		executeQuery(blobUploadedQ, UUID.fromString(blobId), UUID.fromString(userId));
	}

	public void setBlobDeletionStatus(String blobId, String userId, String status){
		//Set pending Request to Terminated
		String setPendingDeletionStatusQ = "UPDATE broker.deletions set status = '" + status + "' where blob_id = ? AND user_id = ?;";
		//PreparedStatement prepStatement = prepareQuery(setPendingDeletionStatusQ);
		//BoundStatement boundStatement = new BoundStatement(prepStatement);
		//boundStatement.bind(UUID.fromString(blobId), UUID.fromString(userId));
		//executeQuery(boundStatement);
		executeQuery(setPendingDeletionStatusQ, UUID.fromString(blobId), UUID.fromString(userId));
	}


	public void addBlob(String userId, String blobId, String fileName, String storageName,
			String storageEndpoint, String storageRegionId, String container, long size){
		//Insert an entry in blob_by_user
		String addBlobRequest = "INSERT INTO broker.blob_by_user (user_id, id, name, last_modified, storage_name, endpoint, regionId, container, size, policies, tags) "+
				"VALUES (? , ?, ?, ?, ?, ? , ?, ?, ?, ?, ?);";
		//PreparedStatement prepStatement = prepareQuery(addBlobRequest);
		//BoundStatement boundStatement = new BoundStatement(prepStatement);
		//boundStatement.bind(UUID.fromString(userId), UUID.fromString(blobId), fileName, null, storageName,
		//storageEndpoint, storageRegionId, container, size, null, null);
		//executeQuery(boundStatement);
		executeQuery(addBlobRequest, UUID.fromString(userId), UUID.fromString(blobId), fileName, null, storageName,
				storageEndpoint, storageRegionId, container, size, null, null);
	}

	public void removeBlob(String blobId){
		String removeFileRequest = "DELETE from broker.blob_by_user where id = ?;";
		/*PreparedStatement prepStatement = prepareQuery(removeFileRequest);
		BoundStatement boundStatement = new BoundStatement(prepStatement);
		boundStatement.bind(UUID.fromString(blobId));
		executeQuery(boundStatement);*/
		executeQuery(removeFileRequest, UUID.fromString(blobId));
	}

	public String addPendingUploadEntry(String userId, String storageName, String endpointName, String regionId, String blob, String bucketName){
		String uploadRequest = "INSERT INTO broker.uploads (blob_id, user_id, storage_name, storage_endpoint, storage_region, file_name, container_name, status, status_modified) "
				+ "VALUES (?,?,?,?,?,?,?,?,?);";
		System.out.println("Create an entry in uploads table");
		//Create an entry in uploads table
		System.out.println("Prepare query for adding pending upload");
		UUID blobId = UUIDs.timeBased();
		//PreparedStatement pStatement = prepareQuery(uploadRequest);
		/*BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(blobId,
				UUID.fromString(userId), 
				storageName, 
				endpointName, 
				regionId, 
				blob, 
				bucketName, 
				PENDING_STATUS, 
				null);
		executeQuery(bStatement);*/
		executeQuery(uploadRequest, blobId,
				UUID.fromString(userId), 
				storageName, 
				endpointName, 
				regionId, 
				blob, 
				bucketName, 
				PENDING_STATUS, 
				null);
		return blobId.toString();
	}

	public void addPendingDeleteEntry(String blobId, String userId){

		//Retrieve blob based on id and user id
		Blob blob = getBlobById(UUID.fromString(blobId), UUID.fromString(userId));

		String addDeletePendingBlobQ = "INSERT INTO broker.deletions (blob_id, user_id, storage_name, storage_endpoint, storage_region, file_name, container_name, status, status_modified) " +
				"values (?, ?, ?, ?, ?, ?, ?, ?, ?);";	
		/*PreparedStatement pStatement = prepareQuery(addDeletePendingBlobQ);
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(blob.getId(),
				blob.getUserId(),
				blob.getCloudName(),
				blob.getStorageEndpoint(),
				blob.getRegionId(),
				blob.getFileName(),
				blob.getContainer(),
				PENDING_STATUS,
				null);
		executeQuery(bStatement);*/
		executeQuery(addDeletePendingBlobQ, blob.getId(),
				blob.getUserId(),
				blob.getCloudName(),
				blob.getStorageEndpoint(),
				blob.getRegionId(),
				blob.getFileName(),
				blob.getContainer(),
				PENDING_STATUS,
				null);
	}

	public Set<StorageService> UploadWithPolicies(Policies userPolicies) throws BrokerException, PolicyException{
		System.out.println("CassandraManagerEJB : Upload policies ");
		//Retrieve policy based on name from json file
		Policies policies = getPolicyBySid(userPolicies.getName());
		policies.setUserId(userPolicies.getUserId());
		switch(policies.getAction()){
		case "PUT": return getDestinationClouds(policies);
		}

		throw new PolicyException("Unable to determine the policy to apply");
	}

	public Policies getPolicyBySid(String policySId) throws PolicyException {
		String getPolicyQ = "SELECT * FROM broker.policies where name = ?;";	
		/*PreparedStatement pStatement = prepareQuery(getPolicyQ);
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind("basic");
		ResultSet resultSet = executeQuery(bStatement);*/
		ResultSet resultSet = executeQuery(getPolicyQ, "basic");
		Row row = resultSet.one();
		if(row != null){
			try {
				JSONObject policyContent = new JSONObject(row.getString("content"));
				String version = (String) policyContent.get("Version");
				JSONArray statements = policyContent.getJSONArray(POLICY_STATEMENT);
				//Browse all statements
				for(int i = 0; i < statements.length();i++){
					JSONObject stmt = statements.getJSONObject(i);
					if(stmt.get(POLICY_SID).equals(policySId)){
						System.out.println(stmt.toString());
						Policies policies = new Policies();
						//Set policy name
						policies.setName(policySId);
						//Set policy action
						policies.setAction(stmt.getString(POLICY_ACTION));
						//Set policy effect
						policies.setEffect(stmt.getString(POLICY_EFFECT));
						//Set policy resources
						JSONArray jsonResources = stmt.getJSONArray(POLICY_RESOURCES);
						HashSet<String> cloudResources = new HashSet<String>();
						for(int j = 0; j < jsonResources.length();j++){
							cloudResources.add((String)jsonResources.get(j));
						}
						policies.setResources(cloudResources);
						//Set policy conditions
						JSONObject jsonConditions = stmt.getJSONObject(POLICY_CONDITIONS);
						Map<String, List<String>> conditions = new HashMap<String, List<String>>();
						//IP address conditions
						JSONArray jsonIPAdd = (JSONArray)jsonConditions.get(POLICY_CONDITIONS_IPADD);
						ArrayList<String> ipAdd = new ArrayList<String>();
						for(int j = 0; j < jsonIPAdd.length();j++){
							ipAdd.add((String)jsonIPAdd.get(j));
						}
						conditions.put(POLICY_CONDITIONS_IPADD, ipAdd);
						System.out.println(conditions);
						policies.setConditions(conditions);

						return policies;
					}
				}
			} catch (JSONException e) {
				throw new PolicyException("Impossible to parse JSON policies file");
			}
		}

		throw new PolicyException("No policy has been found with the given SID: " + policySId);
	}

	public Set<StorageService> getDestinationClouds(Policies policies) throws BrokerException{
		System.out.println("Get destinations clouds based on policies");
		Set<StorageService> destinationStorages = new HashSet<StorageService>();
		//Get all user storages
		Set<StorageService> userStorages = getStoragesByTenant(policies.getUserId());
		Set<String> destinationResources = policies.getResources();
		//For each storage resource
		for(String storageName : destinationResources){
			//Check if user has been registered to use it
			for(StorageService userStorage : userStorages){
				if(userStorage.getStorageName().toLowerCase().equals(storageName.toLowerCase())){
					//The user is registered
					switch(policies.getName()){
					case Policies.REPLICATION_ALL_STORAGE:
					case Policies.REPLICATION_ZONE_US:
					case Policies.REPLICATION_ZONE_AP:
						//Set container to use the first one found (random order)
						setStorageContainerRandom(userStorage);
						System.out.println("Random Container Found : " + userStorage.getContainer()); break;
					default: 
						//Set container to null
						//In this case, we need to pick all containers found in containers attribute
						userStorage.setContainer(null);
					}
					destinationStorages.add(userStorage);
				}
			}
		}
		return destinationStorages;
	}

	public void setStorageContainerRandom(StorageService storage){
		storage.setContainer(storage.getContainers().iterator().next());
	}

	public Set<StorageService> getStoragesByTenant(String tenantId){
		String getStorageServicesByUserQ = "SELECT * FROM  broker.storage_by_tenant WHERE tenant_id = ?";
		/*PreparedStatement pStatement = prepareQuery(getStorageServicesByUserQ);
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(UUID.fromString(userId));
		ResultSet resultSet = executeQuery(bStatement);*/
		ResultSet resultSet = executeQuery(getStorageServicesByUserQ, UUID.fromString(tenantId));
		List<Row> rows = resultSet.all();
		Iterator<Row> it = rows.iterator();
		Set<StorageService> storages = new HashSet<StorageService>();
		while(it.hasNext()){
			Row row = it.next();
			StorageService storage = new StorageService(row.getString(USER_STORAGE_NAME), row.getString(USER_STORAGE_ENDPOINT), row.getString(USER_STORAGE_REGIONID));
			storage.setContainers(row.getSet(USER_STORAGE_BUCKETS, String.class));
			storages.add(storage);
		}
		return storages;
	}

	public StorageService getStorageService(String storageName) throws BrokerException{
		String getStorageServiceQ = "SELECT * FROM broker.storage_service WHERE name = ?";
		/*PreparedStatement pStatement = prepareQuery(getStorageServiceQ);
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(storageName);
		ResultSet resultSet = executeQuery(bStatement);*/
		ResultSet resultSet = executeQuery(getStorageServiceQ, storageName);
		Row row = resultSet.one();
		if(row != null){
			StorageService storage = new StorageService(storageName, row.getString(STORAGE_ENDPOINT), row.getString(STORAGE_REGION_ID));
			storage.setCloudName(row.getString(STORAGE_CLOUD_NAME));
			return storage;
		}
		throw new BrokerException("Unable to find storage : " + storageName + " in broker");
	}

	private Blob getBlobById(UUID blobId, UUID userId){
		String getBlobToDelete = "select * from broker.blob_by_user where id = ? and user_id = ?;";
		/*PreparedStatement pStatement = prepareQuery(getBlobToDelete);
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(blobId, userId);
		ResultSet resultSet = executeQuery(bStatement);*/

		ResultSet resultSet = executeQuery(getBlobToDelete, blobId, userId);
		Row row = resultSet.one();
		Blob blob = new Blob();
		blob.setCloudName(row.getString(BLOB_STORAGE_NAME));
		blob.setContainer(row.getString(BLOB_CONTAINER));
		blob.setFileName(row.getString(BLOB_NAME));
		blob.setId(blobId);
		blob.setPolicies(null);
		blob.setRegionId(row.getString(BLOB_REGION_ID));
		blob.setStorageEndpoint(row.getString(BLOB_STORAGE_ENDPOINT));
		blob.setUserId(userId);
		return blob;
	}

	public List<Blob> listBucket (String userId){

		System.out.println("!!!!! List blobs for user : " + userId);
		String userBlobsQ = "SELECT * FROM broker.blob_by_user WHERE user_id = ?";
		/*PreparedStatement pStatement = prepareQuery(userBlobsQ); 
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(UUID.fromString(userId));
		ResultSet result = executeQuery(bStatement);*/
		ResultSet result = executeQuery(userBlobsQ, UUID.fromString(userId));
		List<Blob> blobs = new ArrayList<Blob>();
		if(result != null){
			List<Row> rows = result.all();
			for(Row row : rows){
				Blob blob = new Blob();
				blob.setUserId(row.getUUID(BLOB_USER_ID));
				blob.setCloudName(row.getString(BLOB_STORAGE_NAME));
				blob.setFileName(row.getString(BLOB_NAME));
				blob.setId(row.getUUID(BLOB_ID));
				blob.setLastModified(row.getDate(BLOB_LAST_MODIFIED));
				blob.setRegionId(row.getString(BLOB_REGION_ID));
				blob.setStorageEndpoint(row.getString(BLOB_STORAGE_ENDPOINT));
				blob.setContainer(row.getString(BLOB_CONTAINER));
				blobs.add(blob);
			}
		}
		return blobs;
	}

	public Blob getObject(String userId, String objectId){
		System.out.println("List blobs for user : " + userId);
		String userBlobsQ = "SELECT * FROM broker.blob_by_user WHERE " + BLOB_USER_ID + " = ? AND " + BLOB_ID + " = ?";
		/*PreparedStatement pStatement = prepareQuery(userBlobsQ); 
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(UUID.fromString(userId), UUID.fromString(objectId));
		ResultSet result = executeQuery(bStatement);*/
		ResultSet result = executeQuery(userBlobsQ, UUID.fromString(userId), UUID.fromString(objectId));
		List<Row> rows = result.all();
		Blob blob = new Blob();
		for(Row row : rows){
			blob.setUserId(row.getUUID(BLOB_USER_ID));
			blob.setCloudName(row.getString(BLOB_STORAGE_NAME));
			blob.setFileName(row.getString(BLOB_NAME));
			blob.setId(row.getUUID(BLOB_ID));
			blob.setLastModified(row.getDate(BLOB_LAST_MODIFIED));
			blob.setRegionId(row.getString(BLOB_REGION_ID));
			blob.setStorageEndpoint(row.getString(BLOB_STORAGE_ENDPOINT));
			blob.setContainer(row.getString(BLOB_CONTAINER));
		}
		return blob;
	}


	public List<Blob> getObjectByCloud(String userId, 
			String storageName){
		System.out.println("List blobs for user : " + userId + " and storage " + storageName);
		String userBlobsQ = "SELECT * FROM broker.blob_by_user WHERE " + BLOB_USER_ID + " = ? AND " + BLOB_STORAGE_NAME + " = ? ALLOW FILTERING";
		/*PreparedStatement pStatement = prepareQuery(userBlobsQ); 
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(UUID.fromString(userId), storageName);
		ResultSet result = executeQuery(bStatement);*/
		ResultSet result = executeQuery(userBlobsQ, UUID.fromString(userId), storageName);
		List<Row> rows = result.all();
		List<Blob> blobs = new ArrayList<Blob>();
		for(Row row : rows){
			Blob blob = new Blob();
			blob.setUserId(row.getUUID(BLOB_USER_ID));
			blob.setCloudName(row.getString(BLOB_STORAGE_NAME));
			blob.setFileName(row.getString(BLOB_NAME));
			blob.setId(row.getUUID(BLOB_ID));
			blob.setLastModified(row.getDate(BLOB_LAST_MODIFIED));
			blob.setRegionId(row.getString(BLOB_REGION_ID));
			blob.setStorageEndpoint(row.getString(BLOB_STORAGE_ENDPOINT));
			blob.setContainer(row.getString(BLOB_CONTAINER));
			blobs.add(blob);
		}
		return blobs;
	}


	public void connect(String node, String keyspace) {
		if(cluster == null || cluster.isClosed()){
			cluster = Cluster.builder().addContactPoint(node).build();
		}
		if(session == null || session.isClosed()){
			session = cluster.connect(keyspace);
		}
	}

	public PreparedStatement prepareQuery(String query){
		connect(CASSANDRA_ENDPOINT, CASSANDRA_KEYSPACE);
		return session.prepare(query); 
	}

	public ResultSet executeQuery(BoundStatement statement){
		connect(CASSANDRA_ENDPOINT, CASSANDRA_KEYSPACE);
		return session.execute(statement);
	}

	public ResultSet executeQuery(String query, Object ... args){
		connect(CASSANDRA_ENDPOINT, CASSANDRA_KEYSPACE);
		return session.execute(query, args);
	}


	public void close(){
		session.close();
		cluster.close();
	}

	public static void main(String[] args){
		StatelessCassandraManager man = new StatelessCassandraManager();
		try {
			man.getPolicyBySid(Policies.REPLICATION_ALL_STORAGE);
		} catch (PolicyException e) {
			e.printStackTrace();
		}
		/*List<Blob> blobs = man.listBucket("10000000-1111-2222-3333-100000000000");
		for(Blob b : blobs){
			System.out.println(b.getFileName());
		}*/

		/*Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n", 
				metadata.getClusterName());
		for ( Host host : metadata.getAllHosts() ) {
			System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
					host.getDatacenter(), host.getAddress(), host.getRack());
		}*/
	}

}
