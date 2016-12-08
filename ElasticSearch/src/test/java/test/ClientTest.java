package test;

import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;
import org.testng.AssertJUnit;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.client.Client;
import org.testng.annotations.Test;
import org.testng.Assert;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.stats.ClusterStatsRequest;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import clientfactory.ClientFactory;

public class ClientTest {
	
	@Test(groups="ClientTest.ConnectionTest")
	public void ConnectionTest(){
		 
		Client client = ClientFactory.getClient();
		
		ClusterHealthResponse health = client.admin().cluster().prepareHealth().get();
		String clustername = health.getClusterName();
		AssertJUnit.assertEquals(clustername, "mycluster");
		
		
	}
	 
	 
	 

}
