package clientfactory;

import org.elasticsearch.client.Client;

import configuration.Configuration;

public class ClientFactory {
	
	public ClientFactory(){}
	
	static Configuration conf = new Configuration();
	 public static Client getClient(){
		 return conf.client();
	 }

}
