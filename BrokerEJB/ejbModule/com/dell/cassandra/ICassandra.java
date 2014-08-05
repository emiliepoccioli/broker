package com.dell.cassandra;

import java.io.Serializable;
import java.util.List;

import broker.commons.Blob;

public interface ICassandra extends Serializable{

	public String hello();
	public List<Blob> listBucket (String userId);
	public Blob getObject(String userId, String objectId);
}
