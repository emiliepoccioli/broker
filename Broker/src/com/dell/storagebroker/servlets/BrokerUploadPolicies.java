package com.dell.storagebroker.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.dell.cloud.dasein.CloudManager;
import com.dell.cloud.dasein.Utils;
import com.sun.jersey.api.client.Client;

/**
 * Servlet implementation class BrokerUploadPolicies
 */
public class BrokerUploadPolicies extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String PARAM_UPLOAD_POLICIES_USER_UUID = "upload_policies_user_id";
	public static final String PARAM_UPLOAD_POLICIES_FILE = "upload_policies_file";
	public static final String PARAM_UPLOAD_POLICIES_POLICY = "upload_policies_policy";


	//STORAGE SERVICE JSON FIELDS
	public static final String JSON_STORAGE_CONTAINER = "container";
	public static final String JSON_STORAGE_CLOUDNAME = "cloudName";
	public static final String JSON_STORAGE_ENDPOINT = "storageEndpoint";
	public static final String JSON_STORAGE_NAME = "storageName";
	public static final String JSON_STORAGE_REGION = "storageRegion";


	public static final String ACTION_UPLOAD_POLICIES = "action_upload_policies";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BrokerUploadPolicies() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Client client = Client.create();
		System.out.println(">>>> Upload with policy <<<<<");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			System.out.println("Is multipart");
			String clientIPAddress = Utils.getClientIpAddr(request);
			String userId = "";
			String fileName = "";
			String policy = "";
			File fileToUpload = null;
			System.out.println("client ip address : " + clientIPAddress);
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);
			try {
				FileItemIterator iter = upload.getItemIterator(request);
				InputStream stream = null;
				while (iter.hasNext()){
					FileItemStream item = iter.next();
					String fieldName = item.getFieldName();

					stream = item.openStream();
					System.out.println("fieldName : " + fieldName);
					if(item.isFormField()){
						String fieldValue = Streams.asString(stream);
						System.out.println("Form field " + fieldName + " : " + fieldValue);
						switch(fieldName){
						case PARAM_UPLOAD_POLICIES_POLICY: 
							policy = fieldValue;
							break;
						case PARAM_UPLOAD_POLICIES_USER_UUID: 
							userId = fieldValue;
							break;
						}
					}
					//Upload file submitted in the form
					else{
						//Upload file submitted in the form
						if(fieldName != null && !"".equals(fieldName)){
							fileName = new File(item.getName()).getName();
							fileToUpload = new File(getServletContext().getRealPath("/" + fileName));
							FileOutputStream fos = new FileOutputStream(fileToUpload);
							long fileSize = Streams.copy(stream, fos, true);
						}
					}
				}
				CloudManager cloudManager = new CloudManager();
				//cloudManager.uploadFileWithPolicies(userId, fileToUpload, policy, clientIPAddress);
			}
			catch(Exception e){
				e.printStackTrace();
			}
			request.setAttribute("filename", fileName);
			request.getRequestDispatcher("/upload_success.jsp").forward(request, response);
		}
	}
}
