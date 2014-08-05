package com.dell.cloud.dasein;

import broker.commons.CloudName;


public class CredentialsManager {

	
	
	public static CloudCredentials GetCredentials(CloudName cloudName) throws CredentialsException{
		switch(cloudName){
		case AMAZON: 
			return new CloudCredentials("AKIAI7O3XYGSHK4GW7CQ", "+HUYESFu+9R59kqB6aO++z0t12IuebK3SRU4rMwq", "5197-3932-4168");
		case SWIFT: 
			return new CloudCredentials("tester", "testing", "test");
		default:
			throw new CredentialsException("Unable to get Credentials for cloud : " + cloudName);
		}
	}
}
