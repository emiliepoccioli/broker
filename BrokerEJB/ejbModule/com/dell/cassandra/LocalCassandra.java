package com.dell.cassandra;

import java.util.List;
import java.util.Set;

import javax.ejb.Local;

import com.dell.exceptions.BrokerException;
import com.dell.exceptions.PolicyException;

import broker.commons.Blob;
import broker.commons.Policies;
import broker.commons.StorageService;

@Local
public interface LocalCassandra extends ICassandra {
	
	public List<Blob> listBucket (String userId);
	
	public Blob getObject(String userId, String objectId);
	
	public List<Blob> getObjectByCloud(String userId, String storageName);
	
	public String addPendingUploadEntry(String userId, String storageName, String endpointName, String regionId, String blob, String bucketName);
	
	public void addPendingDeleteEntry(String blobId, String userId);
	
	public void setBlobUploadStatus(String blobId, String userId, String status);
	
	public void setBlobDeletionStatus(String blobId, String userId, String status);
	
	public void addBlob(String userId, String blobId, String fileName, String storageName,
			String storageEndpoint, String storageRegionId, String container, long size);
	
	public void removeBlob(String blobId);
	
	public Set<StorageService> UploadWithPolicies(Policies userPolicies) throws BrokerException, PolicyException;
	
	public Set<StorageService> getStoragesByTenant(String userId);
	
}
