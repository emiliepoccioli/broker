package com.dell.cassandra;

import java.util.List;

import javax.ejb.Remote;

import broker.commons.Blob;

@Remote
public interface RemoteCassandra extends ICassandra{


	public String hello();

	public List<Blob> listBucket (String userId);
	
	public Blob getObject(String userId, String objectId);

}