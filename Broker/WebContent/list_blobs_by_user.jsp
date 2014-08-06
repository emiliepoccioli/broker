<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<%@ page import="com.dell.storagebroker.servlets.*;"%>
<title>Blobs By User</title>
</head>
<body>
	List Blobs
	<c:set var="blobs" scope="request" value="${LIST_BLOBS}" />
	<c:if test="${fn:length(blobs) > 0}">
		<table>
			<tr>
				<th style="width:200px">Name</th>
				<th style="width:200px">Cloud Name</th>
				<th style="width:250px">Storage Endpoint</th>
				<th style="width:200px">Storage Region</th>
				<th style="width:200px">Bucket</th>
				<th style="width:200px">Delete File</th>
			</tr>
		</table>
		<c:set var="i" value="0"></c:set>

		<c:forEach items="${LIST_BLOBS}" var="blob">
			<form method="post" action="BrokerDelete">
				<input type="hidden" name="<%=BrokerDelete.PARAM_DELETE_BLOBID%>"
					value="${blob.id}"></input> <input type="hidden"
					name="<%=BrokerDelete.PARAM_DELETE_USERID%>" value="${blob.userId}"></input>
				<table border="1">
					<tr>
						<td><input type="text" value="${blob.fileName}" style="border:none;width:200px"
							name="<%=BrokerDelete.PARAM_DELETE_BLOBNAME%>" readonly></td>
						<td><input type="text" value="${blob.cloudName}" style="border:none;width:200px"
							name="<%=BrokerDelete.PARAM_DELETE_STORAGENAME%>" readonly></td>
						<td><input type="text" value="${blob.storageEndpoint}" style="border:none;width:250px"
							name="<%=BrokerDelete.PARAM_DELETE_STORAGEENDPOINT%>" readonly></td>
						<td><input type="text" value="${blob.regionId}" style="border:none;width:200px"
							name="<%=BrokerDelete.PARAM_DELETE_STORAGEREGION%>" readonly></td>
						<td><input type="text" value="${blob.container}" style="border:none;width:200px"
							name="<%=BrokerDelete.PARAM_DELETE_BUCKETNAME%>" readonly></td>
						<td><input type="submit" style="border:none;width:200px"
							name="<%=BrokerDelete.ACTION_DELETE_SINGLE_FILE%>"
							value="Delete File"></td>
					</tr>
				</table>
			</form>
		</c:forEach>
	</c:if>
</body>
</html>