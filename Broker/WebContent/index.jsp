<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%@ page import="com.dell.storagebroker.servlets.*;" %>
<title>Storage Broker Client</title>
</head>
<body>
<h2>Storage Broker Client v3</h2>

<h3>List all files 2</h3>
<form method="POST" action="BrokerList">
<table>
<tr><td>User Id: </td><td><input type="text" id="<%=BrokerList.PARAM_LIST_ALL_USER_ID%>" name="<%=BrokerList.PARAM_LIST_ALL_USER_ID%>" size="30"></td></tr>
<tr><td colspan="2"><input type="submit" value="List All" name="<%=BrokerList.ACTION_LIST_BUCKET%>" id="<%=BrokerList.ACTION_LIST_BUCKET%>"></td></tr>
</table>
</form>

<h3>List files by id</h3>
<form method="POST" action="BrokerList">
<table>
<tr><td>User Id: </td><td><input type="text" id="<%=BrokerList.PARAM_LIST_OBJECT_USER_ID%>" name="<%=BrokerList.PARAM_LIST_OBJECT_USER_ID%>" size="30"></td></tr>
<tr><td>Blob Id: </td><td><input type="text" id="<%=BrokerList.PARAM_BLOB_ID%>" name="<%=BrokerList.PARAM_BLOB_ID%>" size="30"></td></tr>
<tr><td colspan="2"><input type="submit" value="List Object" name="<%=BrokerList.ACTION_LIST_OBJECT%>" id="<%=BrokerList.ACTION_LIST_OBJECT%>"></td></tr>
</table>
</form>

<h3>List user file by storage</h3>
<form method="POST" action="BrokerList">
<input type="text" id="<%=BrokerList.PARAM_LIST_STORAGE_USER_ID%>" name="<%=BrokerList.PARAM_LIST_STORAGE_USER_ID%>" size="30">
    	<select name="<%=BrokerList.PARAM_STORAGE_NAME%>" id="<%=BrokerList.PARAM_STORAGE_NAME%>">
  			<option value="amazon_ap">Amazon</option>
  			<option value="swift">Swift</option>
		</select>
<input type="submit" value="List Storage" name="<%=BrokerList.ACTION_LIST_STORAGE%>" id="<%=BrokerList.ACTION_LIST_STORAGE%>">
</form>

<div>
<h3>Upload File</h3>
<form method="POST" action="BrokerUpload" enctype="multipart/form-data">
<table>
    <tr><td>User ID:</td><td><input type="text" name="<%=BrokerUpload.PARAM_UPLOAD_USER_UUID%>" id="<%=BrokerUpload.PARAM_UPLOAD_USER_UUID%>" size="50"></td></tr>
    <tr><td>Cloud Storage:</td>
    	<td>
    	<select name="<%=BrokerUpload.PARAM_UPLOAD_STORAGE_NAME%>" id="<%=BrokerUpload.PARAM_UPLOAD_STORAGE_NAME%>">
  			<option value="amazon_ap">Amazon AP</option>
  			<option value="swift">Swift</option>
		</select>
		</td>
    </tr>
    <tr><td>File to upload:</td><td><input type="file" name="<%=BrokerUpload.PARAM_UPLOAD_FILE%>" id="<%=BrokerUpload.PARAM_UPLOAD_FILE%>"></td></tr>
	<tr><td>Bucket name:</td><td><input type="text" name="<%=BrokerUpload.PARAM_UPLOAD_BUCKET_NAME%>" id="<%=BrokerUpload.PARAM_UPLOAD_BUCKET_NAME%>" size="50"></td></tr>
	<tr><td>Storage Endpoint:</td><td><input type="text" name="<%=BrokerUpload.PARAM_UPLOAD_STORAGE_ENDPOINT%>" id="<%=BrokerUpload.PARAM_UPLOAD_STORAGE_ENDPOINT%>" size="50"></td></tr>
	<tr><td>Storage Region Id:</td><td><input type="text" name="<%=BrokerUpload.PARAM_UPLOAD_STORAGE_REGION%>" id="<%=BrokerUpload.PARAM_UPLOAD_STORAGE_REGION%>" size="50"></td></tr>
	
	<tr><td colspan="2"><input type="submit" value="Upload File" name="<%=BrokerUpload.ACTION_UPLOAD%>" id="<%=BrokerUpload.ACTION_UPLOAD%>"></td></tr>
</table>
</form>
</div>


<div>
<h3>Upload File with Policies</h3>
<form method="POST" action="BrokerUploadPolicies" enctype="multipart/form-data">
<table>
    <tr><td>User ID:</td><td><input type="text" name="<%=BrokerUploadPolicies.PARAM_UPLOAD_POLICIES_USER_UUID%>" id="<%=BrokerUploadPolicies.PARAM_UPLOAD_POLICIES_USER_UUID%>" size="50"></td></tr>
    <tr><td>Policy:</td>
    	<td>
    	<select name="<%=BrokerUploadPolicies.PARAM_UPLOAD_POLICIES_POLICY%>" id="<%=BrokerUploadPolicies.PARAM_UPLOAD_POLICIES_POLICY%>">
  			<option value="policy_ip_address">IP Address</option>
  			<option value="policy_zone">Zone</option>
  			<option value="policy_replication">Replication</option>

		</select>
		</td>
    </tr>
    <tr><td>File to upload:</td><td><input type="file" name="<%=BrokerUploadPolicies.PARAM_UPLOAD_POLICIES_FILE%>" id="<%=BrokerUploadPolicies.PARAM_UPLOAD_POLICIES_FILE%>"></td></tr>
	<tr><td colspan="2"><input type="submit" value="Upload File" name="<%=BrokerUploadPolicies.ACTION_UPLOAD_POLICIES%>" id="<%=BrokerUploadPolicies.ACTION_UPLOAD_POLICIES%>"></td></tr>
</table>
</form>
</div>

</body>
</html>
