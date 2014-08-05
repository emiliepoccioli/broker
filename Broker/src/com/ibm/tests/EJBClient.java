package com.ibm.tests;

import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import broker.commons.Blob;

import com.dell.cassandra.RemoteCassandra;
import com.dell.cassandra.StatelessCassandraManager;

public class EJBClient {
	
	private static Context initialContext;
	 
    private static final String PKG_INTERFACES = "org.jboss.ejb.client.naming";
 
    public static Context getInitialContext() throws NamingException {
        if (initialContext == null) {            
            Properties jndiProperties = new Properties();
            jndiProperties.put(Context.URL_PKG_PREFIXES, PKG_INTERFACES);
            jndiProperties.put(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
            jndiProperties.put(javax.naming.Context.PROVIDER_URL, "remote://localhost:4447");
            jndiProperties.put(javax.naming.Context.SECURITY_PRINCIPAL, "broker");
            jndiProperties.put(javax.naming.Context.SECURITY_CREDENTIALS, "br0ker");
            jndiProperties.put("jboss.naming.client.ejb.context", true);
            initialContext = new InitialContext(jndiProperties);
        }
        return initialContext;
    }
    
   
 
    private static RemoteCassandra doLookup() {
        Context context = null;
        RemoteCassandra bean = null;
        try {
            // 1. Obtaining Context
            context = getInitialContext();
            // 2. Generate JNDI Lookup name
            String lookupName = getLookupName();
            // 3. Lookup and cast
            bean = (RemoteCassandra) context.lookup(lookupName);
 
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return bean;
    }
 
    private static String getLookupName() {
/*
The app name is the EAR name of the deployed EJB without .ear suffix.
Since we haven't deployed the application as a .ear,
the app name for us will be an empty string
*/
        String appName = "BrokerEAR";
 
        /* The module name is the JAR name of the deployed EJB
        without the .jar suffix.
        */
        String moduleName = "BrokerEJB";
 
/*AS7 allows each deployment to have an (optional) distinct name.
This can be an empty string if distinct name is not specified.
*/
        String distinctName = "";
 
        // The EJB bean implementation class name
        String beanName = StatelessCassandraManager.class.getSimpleName();
 
        // Fully qualified remote interface name
        final String interfaceName = RemoteCassandra.class.getName();
 
        // Create a look up string name
        String name = "ejb:" + appName + "/" + moduleName + "/" +
            distinctName    + "/" + beanName + "!" + interfaceName;
 
        return name;
    }
    
	
	public static void main(String[] args){
		RemoteCassandra bean = doLookup();
        List<Blob> blobs = bean.listBucket("10000000-1111-2222-3333-100000000000");
        for(Blob b : blobs){
			System.out.println(b.getFileName());
		}
	}
}
