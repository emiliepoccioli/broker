package com.dell.ejb.clients;

import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.dell.cassandra.RemoteCassandra;
import com.dell.cassandra.StatelessCassandraManager;

public class CassandraManager {


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



	public static RemoteCassandra getCassandra() {
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

		String appName = "BrokerEAR";

		/* The module name is the JAR name of the deployed EJB
        without the .jar suffix.
		 */
		String moduleName = "BrokerEJB";

		String distinctName = "";

		// The EJB bean implementation class name
		String beanName = StatelessCassandraManager.class.getSimpleName();

		// Fully qualified remote interface name
		final String interfaceName = RemoteCassandra.class.getName();

		// Create a look up string name
		String name = "ejb:" + appName + "/" + moduleName + "/" +
				distinctName    + "/" + beanName + "!" + interfaceName;
		System.out.println("EJB name to lookup: " + name);
		return name;
	}
}
