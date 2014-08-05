package com.dell.cassandra;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.ejb.Local;
import javax.ejb.Remote;
import javax.ejb.Stateless;

import broker.commons.Blob;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;


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


	public String hello(){
		return "Hello";
	}



	public List<Blob> listBucket (String userId){

		System.out.println("List blobs for user : " + userId);
		String userBlobsQ = "SELECT * FROM broker.blob_by_user WHERE user_id = ?";
		PreparedStatement pStatement = prepareQuery(userBlobsQ); 
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(UUID.fromString(userId));
		ResultSet result = executeQuery(bStatement);
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
		System.out.println("cassandra side : list files : size : " + blobs.size());
		return blobs;
	}
	
	public Blob getObject(String userId, String objectId){
		System.out.println("List blobs for user : " + userId);
		String userBlobsQ = "SELECT * FROM broker.blob_by_user WHERE " + BLOB_USER_ID + " = ? AND " + BLOB_ID + " = ?";
		PreparedStatement pStatement = prepareQuery(userBlobsQ); 
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(UUID.fromString(userId), UUID.fromString(objectId));
		ResultSet result = executeQuery(bStatement);
		List<Row> rows = result.all();
		Blob blob = new Blob();
		for(Row row : rows){
			System.out.println("List blob");
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
			 String cloudName){
		System.out.println("List blobs for user : " + userId + " and storage " + cloudName);
		String userBlobsQ = "SELECT * FROM broker.blob_by_user WHERE " + BLOB_USER_ID + " = ? AND " + BLOB_STORAGE_NAME + " = ? ALLOW FILTERING";
		PreparedStatement pStatement = prepareQuery(userBlobsQ); 
		BoundStatement bStatement = new BoundStatement(pStatement);
		bStatement.bind(UUID.fromString(userId), cloudName);
		ResultSet result = executeQuery(bStatement);
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


	public void close(){
		session.close();
		cluster.close();
	}

	public static void main(String[] args){
		StatelessCassandraManager man = new StatelessCassandraManager();
		List<Blob> blobs = man.listBucket("10000000-1111-2222-3333-100000000000");
		for(Blob b : blobs){
			System.out.println(b.getFileName());
		}

		/*Metadata metadata = cluster.getMetadata();
		System.out.printf("Connected to cluster: %s\n", 
				metadata.getClusterName());
		for ( Host host : metadata.getAllHosts() ) {
			System.out.printf("Datacenter: %s; Host: %s; Rack: %s\n",
					host.getDatacenter(), host.getAddress(), host.getRack());
		}*/
	}

}
