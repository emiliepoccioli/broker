package com.dell.storagebroker.servlets;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.UriBuilder;

import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

import com.dell.cloud.dasein.CloudManager;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
/**
 * Servlet implementation class BrokerUpload
 */
public class BrokerUpload extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public static final String PARAM_UPLOAD_USER_UUID = "upload_user_id";
	public static final String PARAM_UPLOAD_FILE = "upload_file";
	public static final String PARAM_UPLOAD_BUCKET_NAME = "upload_bucket_name";
	public static final String PARAM_UPLOAD_STORAGE_NAME = "upload_storage_name";
	public static final String PARAM_UPLOAD_STORAGE_ENDPOINT = "upload_storage_endpoint";
	public static final String PARAM_UPLOAD_STORAGE_REGION = "upload_storage_region";

	public static final String ACTION_UPLOAD = "action_upload";

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public BrokerUpload() {
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
		System.out.println("Upload with endpoint ");
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			FileItemFactory factory = new DiskFileItemFactory();
			ServletFileUpload upload = new ServletFileUpload(factory);

			String bucketName = "";
			String cloudName = "";
			String storageEndpoint = "";
			String regionId = "";
			String userId = "";
			String fileName = "";
			File fileToUpload = null;

			try {
				FileItemIterator iter = upload.getItemIterator(request);
				InputStream stream = null;
				while (iter.hasNext()){
					FileItemStream item = iter.next();
					String fieldName = item.getFieldName();

					stream = item.openStream();
					if(item.isFormField()){
						String fieldValue = Streams.asString(stream);
						switch(fieldName){
						case PARAM_UPLOAD_BUCKET_NAME: 
							bucketName = fieldValue;
							System.out.println("bucket name found : " + bucketName); 
							break;
						case PARAM_UPLOAD_STORAGE_NAME: 
							cloudName = fieldValue;
							System.out.println("cloud name found : " + cloudName);
							break;
						case PARAM_UPLOAD_STORAGE_ENDPOINT: 
							storageEndpoint = fieldValue;
							break;
						case PARAM_UPLOAD_STORAGE_REGION: 
							regionId = fieldValue;
							break;
						case PARAM_UPLOAD_USER_UUID: 
							userId = fieldValue;
							break;
						}
					}	
					else {
						//Upload file submitted in the form
						if(fieldName != null && !"".equals(fieldName)){
							fileName = new File(item.getName()).getName();
							fileToUpload = new File(getServletContext().getRealPath("/" + fileName));
							System.out.println("Directory to upload : " + getServletContext().getRealPath("/" + fileName));
							FileOutputStream fos = new FileOutputStream(fileToUpload);
							long fileSize = Streams.copy(stream, fos, true);
						}
					}
				}
			} catch (FileUploadException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			//Build REST URL
			String restUrl = "http://10.49.57.109:8080/BrokerServer/rest";
			restUrl += "/user/" + userId;

			restUrl += "/blob/" + fileName;
			restUrl += "/cloud/" + cloudName;
			restUrl += "/bucket/" + bucketName;
			restUrl += "/endpoint/" + storageEndpoint;
			restUrl += "/regionId/" + regionId;
			System.out.println(restUrl);
			//Calls Storage Broker to create an upload entry in Cassandra
			URI uri = UriBuilder.fromPath(restUrl).build();
			WebResource webResource = client.resource(uri);
			ClientResponse clientResponse = webResource.accept("text/plain").get(ClientResponse.class);

			if (clientResponse.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + clientResponse.getStatus());
			}

			//Upload file to Storage Service
			CloudManager cloudManager = new CloudManager();
			//cloudManager.uploadFile(cloudName, bucketName, storageEndpoint, regionId, fileName, fileToUpload);
			request.setAttribute("filename", fileName);
			request.getRequestDispatcher("/upload_success.jsp").forward(request, response);
		}
	}


}
