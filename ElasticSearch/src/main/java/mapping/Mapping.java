package mapping;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

import java.io.IOException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.xcontent.XContentBuilder;
import clientfactory.ClientFactory;

public class Mapping {
	
	private Client client = ClientFactory.getClient();
	
	public void Mapping() throws IOException{
		CreateIndexResponse ir =  this.client.admin().indices().
				create(new CreateIndexRequest("schools")).actionGet();
		XContentBuilder mapping = jsonBuilder()
				.startObject()
				.startObject("class")
				.field("dynamic", "strict")
				.startObject("properties")
				.startObject("Name of Student")
				.field("type", "string")
				// .field("index", "not_analyzed")
				.endObject()
				.startObject("Roll No")
				.field("type","integer")
				.endObject()
				.endObject()
				.endObject()
				.endObject();

		PutMappingResponse putMappingResponse = client.admin().indices()
				.preparePutMapping("school")
				.setType("class")
				.setSource(mapping)
				.execute().actionGet();
		
		System.out.println("Mapping Successful!!");
		System.out.println(putMappingResponse);
	}

}
