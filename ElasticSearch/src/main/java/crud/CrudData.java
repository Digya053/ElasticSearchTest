package crud;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import clientfactory.ClientFactory;
import parser.NewParser;

public class CrudData {

	public CrudData(){}

	public CrudData(Client client){
		this.client = client;
	}

	private Client client = ClientFactory.getClient();

	public CreateIndexResponse createIndex(){
		CreateIndexResponse createResponse = client.admin().indices()
				.create(Requests.createIndexRequest("newindex")).actionGet();

		System.out.println(createResponse);
		return createResponse;
	}

	public CreateIndexResponse createIndexp(String index){
		CreateIndexResponse createResponse = client.admin().indices()
				.create(Requests.createIndexRequest(index)).actionGet();

		System.out.println(createResponse);
		System.out.println("Index created successfully");
		return createResponse;

	}

	public DeleteResponse deleleDatap(String index,String type,String id){
		DeleteResponse deleteResponse = client.prepareDelete(index,type,id)
				.execute().actionGet();

		System.out.println(deleteResponse);
		System.out.println("Data deleted successfully");
		return deleteResponse;

	}

	public DeleteIndexResponse deleteIndex(String index){
		DeleteIndexResponse deleteIndex =client.admin().indices()
				.delete(Requests.deleteIndexRequest(index)).actionGet();

		System.out.println(deleteIndex);
		System.out.println("Index deleted successfully");
		return deleteIndex;
	}


	public IndexResponse insertData() throws ElasticsearchException, IOException{

		IndexResponse response = client.prepareIndex("newindex", "newType", "1")
				.setSource(jsonBuilder()
						.startObject()
						.field("user", "kimchy")
						.field("postDate", new Date())
						.field("message", "trying out Elasticsearch")
						.endObject()
						)
				.execute()
				.actionGet();

		System.out.println(response);
		return response;

	}

	public IndexResponse insertDatap(String index, String type, String id, String data){
		IndexResponse response = client.prepareIndex(index, type, id)
				.setSource(data)
				.execute()
				.actionGet();

		System.out.println(response);
		System.out.println("Inserted successfully");
		return response;

	}


	public String insertDataFromFilep(String index, String type, String fileName ) throws IOException{

		NewParser nparser = new NewParser();
		List<String> data= nparser.parse(fileName);
		for (String datatoinsert : data) {
			IndexResponse response = client.prepareIndex(index,type,datatoinsert)
					.setSource(datatoinsert)
					.execute().actionGet();


			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			JsonParser jp = new JsonParser();
			JsonElement je = jp.parse(datatoinsert);
			String prettyJsonString = gson.toJson(je);
			System.out.println(response);
			System.out.println(datatoinsert);
		}
		System.out.println("Data inserted from file successfully");
		return fileName;

	}

	public BulkResponse bulkInsertDataFromFile() throws IOException{

		BulkRequestBuilder bulkRequest = client.prepareBulk();

		bulkRequest.add(client.prepareIndex("newindex", "newType", "10")
				.setSource(jsonBuilder().prettyPrint()
						.startObject()
						.field("user", "kimchy")
						.field("postDate", new Date())
						.field("message", "trying out Elasticsearch")
						.endObject()
						)
				);

		bulkRequest.add(client.prepareIndex("newIndex", "newType", "12")

				.setSource(jsonBuilder().prettyPrint()
						.startObject()
						.field("user", "kenechi")
						.field("postDate", new Date())
						.field("message", "another post")
						.endObject()
						)
				);
		System.out.println();

		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {

		}
		System.out.println(bulkRequest.toString());
		System.out.println(bulkResponse.toString());
		System.out.println("BulkResponse executed successfully");
		return bulkResponse;


	}

	public BulkResponse bulkInsertDataFromFilep(String index, String type,String filename) throws IOException{

		BulkRequestBuilder bulkRequest = client.prepareBulk();

		bulkRequest.add(client.prepareIndex("newindexp", "newTypep")
				.setSource(filename)
				);


		BulkResponse bulkResponse = bulkRequest.execute().actionGet();
		if (bulkResponse.hasFailures()) {
			System.out.println("Sorry, bulk insert from file has failed!!");

		}
		System.out.println("Bulk insert from file executed successfully!!");
		System.out.println(bulkResponse.toString());
		System.out.println(bulkRequest.toString());
		return bulkResponse;

	}
	
	public SearchResponse search(){
		SearchResponse response = client.prepareSearch("newindex", "newindexp")
		        .setTypes("newType", "newTypep")
		        .setSearchType(SearchType.DFS_QUERY_THEN_FETCH)
		        .setQuery(QueryBuilders.termQuery("message", "trying"))           
		        .setPostFilter(FilterBuilders.rangeFilter("user").from("a").to("m"))   
		        .setFrom(0).setSize(60).setExplain(true)
		        .addSort("_score",SortOrder.DESC)
		        .execute()
		        .actionGet();
		
		for(SearchHit hit : response.getHits()){
			System.out.println(hit.getSource());}
		System.out.println(response);
		return response;
		
	}
	
	
	public SearchResponse searchp(String index, String type, String field,String query){
		SearchResponse response = this.client.prepareSearch(index)
						.setTypes(type)
						.setQuery(QueryBuilders.termQuery("field","query"))
						.execute()
						.actionGet();
		System.out.println("Search executed successfully for field"+ (field) +"and query"
						+(query));
		return response;
	}
	
	
	public List<Map<String,Object>> getAllAsList(String index,String type){
		SearchResponse response = this.client.prepareSearch(index)
				.setTypes(type)
				.setQuery(QueryBuilders.matchAllQuery())
				.execute()
				.actionGet();
		
		List<Map<String,Object>> toReturn = new ArrayList<Map<String,Object>>();
		for(SearchHit hit : response.getHits()){
			System.out.println(toReturn.add(hit.getSource()));
		}
		System.out.println("Search executed successfully for field");
				
		return toReturn;
		
	}
	
	public List<Map<String, Object>> getAllAsMapAndList(String index, String type){
		SearchResponse response = this.client.prepareSearch(index)
						.setTypes(type)
						.setQuery(QueryBuilders.matchAllQuery())
						.execute()
						.actionGet();
		List<Map<String, Object>> toReturn = new ArrayList<Map<String, Object>>();

		for(SearchHit hit : response.getHits()){
			toReturn.add(hit.getSource());
		}
		
		return toReturn;
	}
	
	public SearchResponse multiMatchQuery(String index, 
			String type, 
			String queryString, 
			String... fields){
QueryBuilder qb = QueryBuilders.multiMatchQuery(queryString, fields);
SearchResponse searchResponse = this.client.prepareSearch(index)
		.setTypes(type)
		.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
		//.setQuery(query)
		//.setQuery(QueryBuilders.termQuery(query.get("field"), query.get("value")))
		.setQuery(qb)
		.execute()
		.actionGet();
return searchResponse;
}
	public void testNested() { 
		SearchResponse response = client.prepareSearch("score").setTypes("marks")
				.setQuery(QueryBuilders.termQuery("description", "am"))	
				.addSort(SortBuilders.fieldSort("marks.user").order(SortOrder.ASC)) 

				.execute()
				.actionGet(); 

		System.out.println(response); 
	} 
	
	public void Mapping() throws IOException{
		CreateIndexResponse ir =  this.client.admin().indices()
				.create(new CreateIndexRequest("school")).actionGet();
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
		System.out.println(putMappingResponse);
	}

	


}




