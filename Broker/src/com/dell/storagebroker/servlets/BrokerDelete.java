package com.dell.storagebroker.servlets;

import java.io.IOException;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import com.dell.cloud.dasein.CloudManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

/**
 * Servlet implementation class BrokerDelete
 */
@WebServlet("/BrokerDelete")
public class BrokerDelete extends HttpServlet {
	private static final long serialVersionUID = 1L;

	//JSP PARAMS
	public static final String PARAM_DELETE_USERID = "param_delete_userid";
	public static final String PARAM_DELETE_BLOBID = "param_delete_blobid";
	public static final String PARAM_DELETE_BLOBNAME = "param_delete_blobname";
	public static final String PARAM_DELETE_STORAGENAME = "param_delete_storagename";
	public static final String PARAM_DELETE_STORAGEENDPOINT = "param_delete_storageendpoint";
	public static final String PARAM_DELETE_STORAGEREGION = "param_delete_storageregion";
	public static final String PARAM_DELETE_BUCKETNAME = "param_delete_bucket";

	public static final String ACTION_DELETE_SINGLE_FILE = "delete_single_file";
	public static final String BLOB_TO_DELETE = "blob_to_delete";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BrokerDelete() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("BrokerDelete : doGet");

		Client client = Client.create();
		//DELETE SINGLE FILE
		if(request.getParameter(ACTION_DELETE_SINGLE_FILE) != null){
			System.out.println("Client asked for single file deletion");
			String userId = request.getParameter(PARAM_DELETE_USERID);
			String blobId = request.getParameter(PARAM_DELETE_BLOBID);
			String storageName = request.getParameter(PARAM_DELETE_STORAGENAME);
			System.out.println("---- " + storageName);
			String restUrl = "http://10.49.57.109:8080/BrokerServer/rest";
			restUrl += "/user/" + userId + "/blob/" + blobId + "/storageName/" + storageName;
			URI uri = UriBuilder.fromPath(restUrl).build();
			WebResource webResource = client.resource(uri);
			ClientResponse clientResponse = webResource.type(MediaType.TEXT_PLAIN).delete(ClientResponse.class);

			//Delete file
			String fileName = request.getParameter(PARAM_DELETE_BLOBNAME);
			String storageEndpoint = request.getParameter(PARAM_DELETE_STORAGEENDPOINT);
			String storageRegion = request.getParameter(PARAM_DELETE_STORAGEREGION);
			String  bucketName = request.getParameter(PARAM_DELETE_BUCKETNAME);

			CloudManager cloudManager = new CloudManager();
			//cloudManager.deleteFile(fileName, storageName, storageEndpoint, storageRegion, bucketName, false);
			System.out.println("BrokerDelete : delete single file end");
			request.setAttribute("filename", fileName);
			request.getRequestDispatcher("/delete_success.jsp").forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
