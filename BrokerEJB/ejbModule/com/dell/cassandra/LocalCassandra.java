package com.dell.cassandra;

import java.util.List;

import javax.ejb.Local;

import broker.commons.Blob;

@Local
public interface LocalCassandra extends ICassandra {

	public String hello();
	
	public List<Blob> listBucket (String userId);
	
	public Blob getObject(String userId, String objectId);
	
}
