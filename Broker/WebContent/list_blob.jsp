<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<html>
<head>
<%@ page import="com.dell.storagebroker.servlets.*;"%>
</head>
<body>
	<c:out value="${LIST_BLOB}" />
	<c:set var="blob" scope="request" value="${LIST_BLOB}" />


	<form method="post" action="BrokerDelete">
		<input type="hidden" name="<%=BrokerDelete.PARAM_DELETE_BLOBID%>"
			id="<%=BrokerDelete.PARAM_DELETE_BLOBID%>" value="${blob.id}"></input>
		<input type="hidden" name="<%=BrokerDelete.PARAM_DELETE_USERID%>"
			id="<%=BrokerDelete.PARAM_DELETE_USERID%>" value="${blob.userId}"></input>
		<table border="1">
			<tr>
				<th>Name</th>
				<th>Cloud Name</th>
				<th>Storage Endpoint</th>
				<th>Storage Region</th>
				<th>Bucket</th>
				<th>Delete File</th>
			</tr>
			<tr>
				<td><input type="text" value="${blob.fileName}"
					name="<%=BrokerDelete.PARAM_DELETE_BLOBNAME%>"
					id="<%=BrokerDelete.PARAM_DELETE_BLOBNAME%>" readonly></td>
				<td><input type="text" value="${blob.cloudName}"
					name="<%=BrokerDelete.PARAM_DELETE_STORAGENAME%>"
					id="<%=BrokerDelete.PARAM_DELETE_STORAGENAME%>" readonly></td>
				<td><input type="text" value="${blob.storageEndpoint}"
					name="<%=BrokerDelete.PARAM_DELETE_STORAGEENDPOINT%>"
					id="<%=BrokerDelete.PARAM_DELETE_STORAGEENDPOINT%>" readonly></td>
				<td><input type="text" value="${blob.regionId}"
					name="<%=BrokerDelete.PARAM_DELETE_STORAGEREGION%>"
					id="<%=BrokerDelete.PARAM_DELETE_STORAGEREGION%>" readonly></td>
				<td><input type="text" value="${blob.container}"
					name="<%=BrokerDelete.PARAM_DELETE_BUCKETNAME%>"
					id="<%=BrokerDelete.PARAM_DELETE_BUCKETNAME%>" readonly></td>
				<td><input type="submit"
					name="<%=BrokerDelete.ACTION_DELETE_SINGLE_FILE%>"
					id="<%=BrokerDelete.ACTION_DELETE_SINGLE_FILE%>"
					value="Delete File"></td>
			</tr>
		</table>
	</form>
</body>
</html>