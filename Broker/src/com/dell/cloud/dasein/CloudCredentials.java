package com.dell.cloud.dasein;

public class CloudCredentials {

	
	public String publicKey;
	
	public String privateKey;
	
	public String accountNumber;
	
	
	public CloudCredentials(String publicKey, String privateKey, String accountNumber){
		this.publicKey = publicKey;
		this.privateKey = privateKey;
		this.accountNumber = accountNumber;
	}


	public String getPublicKey() {
		return publicKey;
	}


	public void setPublicKey(String publicKey) {
		this.publicKey = publicKey;
	}


	public String getPrivateKey() {
		return privateKey;
	}


	public void setPrivateKey(String privateKey) {
		this.privateKey = privateKey;
	}


	public String getAccountNumber() {
		return accountNumber;
	}


	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	
	
	
}
