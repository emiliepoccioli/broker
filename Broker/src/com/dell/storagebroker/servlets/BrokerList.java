package com.dell.storagebroker.servlets;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dell.cassandra.RemoteCassandra;
import com.dell.cloud.dasein.CloudManager;

/**
 * Servlet implementation class BrokerClient
 */
public class BrokerList extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static String PARAM_STORAGE_ENDPOINT = "storage_endpoint";
	public static String PARAM_STORAGE_NAME = "storage_name";
	public static String PARAM_STORAGE_REGION = "storage_region_id";
	public static String PARAM_FILE_NAME = "file_name";
	public static String PARAM_BUCKET_NAME = "bucket_name";
	public static String PARAM_USER_UUID = "user_uuid";
	public static String PARAM_BLOB_ID = "id";
	public static String PARAM_BLOB_FILE = "file_to_upload";

	public static String PARAM_LIST_ALL_USER_ID = "list_all_user_uuid";
	public static String PARAM_LIST_OBJECT_USER_ID = "list_object_user_uuid";
	public static String PARAM_LIST_STORAGE_USER_ID = "list_storage_user_uuid";

	//Actions
	public static String ACTION_LIST_BUCKET = "list_bucket";
	public static String ACTION_LIST_OBJECT = "list_object";
	public static String ACTION_LIST_STORAGE = "list_storage";
	public static String ACTION_UPLOAD_WITH_ENDPOINT = "upload_file_with_endpoint";


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

	
	@EJB
	public RemoteCassandra cassandraManager;

	
	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BrokerList() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("StorageBroker Servlet : List ACTION");
		
		
		System.out.println("CASSANDRA EJB : " + cassandraManager);
		try{
			CloudManager cloudManager = new CloudManager();
			//LIST ALL BLOBS FOR A GIVEN USER
			if(request.getParameter(ACTION_LIST_BUCKET) != null){
				String userId = request.getParameter(PARAM_LIST_ALL_USER_ID);	

				request.setAttribute("LIST_BLOBS", cloudManager.getBlobs(userId, cassandraManager));
				request.getRequestDispatcher("/list_blobs_by_user.jsp").forward(request, response);
			}
			//LIST DETAILS ON A GIVEN BLOB
			else if(request.getParameter(ACTION_LIST_OBJECT) != null){
				String userId = request.getParameter(PARAM_LIST_OBJECT_USER_ID);
				String blobId = request.getParameter(PARAM_BLOB_ID);

				request.setAttribute("LIST_BLOB", cloudManager.getBlob(userId, blobId, cassandraManager));
				request.getRequestDispatcher("/list_blob.jsp").forward(request, response);
			}
			//LIST ALL BLOBS FOR A GIVEN STORAGE SERVICE
			else if(request.getParameter(ACTION_LIST_STORAGE) != null){
				String userId = request.getParameter(PARAM_LIST_STORAGE_USER_ID);
				String storageName = request.getParameter(PARAM_STORAGE_NAME);

				request.setAttribute("LIST_BLOBS", cloudManager.getBlobs(userId, storageName, cassandraManager));
				request.getRequestDispatcher("/list_blobs_by_user.jsp").forward(request, response);
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
