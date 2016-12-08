package crudtest;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import org.testng.Assert;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import clientfactory.ClientFactory;
import parser.NewParser;

public class CrudDataTest {

	private Client client = ClientFactory.getClient();

	@Test(enabled = true, priority=5,dependsOnGroups={"ClientTest.ConnectionTest"})
	public void createIndex(){
		CreateIndexResponse createResponse = client.admin().indices()
				.create(Requests.createIndexRequest("testindex")).actionGet();

		Assert.assertEquals(createResponse.isAcknowledged(),true);
	}

	@Test
	public void checkIndex(){
		boolean exists = this.client.admin().indices()
				.prepareExists("testindex").execute().actionGet().isExists();

		Assert.assertEquals(exists, false);
	}

	@Test(enabled = true,priority=6, dependsOnGroups={"ClientTest.ConnectionTest"})
	@Parameters("testindexp")
	public void createIndexp(
			@Optional("testindexp")String index){
		CreateIndexResponse createResponse = client.admin().indices()
				.create(Requests.createIndexRequest("testindexp")).actionGet();

		Assert.assertEquals(createResponse.isAcknowledged(),true);

	}

	@Test(priority=2,dependsOnMethods={"checkIndex","createIndex"}, dependsOnGroups={"ClientTest.ConnectionTest"})
	public void insertData() throws ElasticsearchException, IOException{

		IndexResponse response = client.prepareIndex("testindex", "testtype", "1")
				.setSource(jsonBuilder()
						.startObject()
						.field("user", "kimchy")
						.field("postDate", new Date())
						.field("message", "trying out Elasticsearch")
						.endObject()
						)
				.execute()
				.actionGet();

		Assert.assertEquals(response.isCreated(),true);


	}


	@Test(priority=0,enabled=true, dependsOnGroups={"ClientTest.ConnectionTest"},
			dependsOnMethods={"checkIndex","createIndex","createIndexp"})
	public void deleteDatap(){
		DeleteResponse deleteResponse = client.prepareDelete("testindex","testtype","1")
				.execute().actionGet();

		Assert.assertEquals(deleteResponse.isContextEmpty(),true);

	}

	@Parameters("testindex")
	@Test(enabled=true, priority=1,dependsOnMethods={"createIndex","createIndexp"},
	dependsOnGroups={"ClientTest.ConnectionTest"})
	public void deleteIndex(
			@Optional("testindex")String index){
		DeleteIndexResponse deleteIndex =client.admin().indices()
				.delete(Requests.deleteIndexRequest(index)).actionGet();

		Assert.assertEquals(deleteIndex.isAcknowledged(),true);



	}


	@Parameters({"testindexp","testtypep","4", "{\"Name\":\"Digya\"}"})
	@Test(priority=3,enabled=true, dependsOnMethods={"checkIndex","createIndexp"}, dependsOnGroups={"ClientTest.ConnectionTest"})
	public void insertDatap(
			@Optional("testindexp") String index, 
			@Optional("testtypep") String type, 
			@Optional("4") String id, 
			@Optional("{\"Name\":\"Digya\"}") String data){
		System.out.println("print ");
		IndexResponse response = client.prepareIndex(index, type, id)
				.setSource(data)
				.execute()
				.actionGet();

		Assert.assertEquals(response.isCreated(),true);

	}


	@Parameters({"testindex","testtype","file.json"})
	@Test(priority=4,enabled=true,dependsOnMethods={"checkIndex","deleteIndex","createIndex"}
	,dependsOnGroups={"ClientTest.ConnectionTest"})
	public void insertDataFromFilep(
			@Optional("testindex") String index, 
			@Optional("testtype") String type,
			@Optional("file.json") String fileName ) throws IOException{

		NewParser nparser = new NewParser();
		List<String> data= nparser.parse(fileName);
		for (String datatoinsert : data) {
			IndexResponse response = client.prepareIndex(index,type,datatoinsert)
					.setSource(datatoinsert)
					.execute().actionGet();

			Assert.assertEquals(response.isCreated(), true);

		}
	}

	@Parameters({"elasticsearch","elastisearhc","book"})
	@Test(priority=5,enabled = true)
	public void termSuggestMethod(
			@Optional("elasticsearch") String index, 
			@Optional("elasticsearhc") String text,
			@Optional("book") String field ){


		SuggestBuilder.SuggestionBuilder suggestBuilder = new TermSuggestionBuilder("my-suggestion")
				.text(text).field(field).suggestMode("always");

		SuggestResponse requestBuilder = client.prepareSuggest(index)
				.addSuggestion(suggestBuilder).execute().actionGet();



		Assert.assertEquals(requestBuilder.isContextEmpty(), true);


	}
}




