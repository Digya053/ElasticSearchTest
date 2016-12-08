package miscellaneous;

import java.io.IOException;

import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.Client;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import clientfactory.ClientFactory;

public class Miscellaneous {

	public Miscellaneous(){}

	public Miscellaneous(Client client){
		this.client = client;
	}

	private Client client = ClientFactory.getClient();

	public void prefixQuery() throws IOException{
		/*		NewParser parserr = new NewParser();
		List<String> data = parserr.parse("longdata.json");
		System.out.println(data);
		for (String string : data) {
			IndexResponse r= this.client.prepareIndex("products","products").setSource(string).execute().actionGet();
			System.out.println(r.isCreated());
		}
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

		QueryBuilder queryBuilder=QueryBuilders.prefixQuery("location", "flo");
		SearchResponse rResponse = this.client.prepareSearch("products")
				.setQuery(queryBuilder).execute().actionGet();
		SearchHit[] searchResult = rResponse.getHits().hits();
		for (SearchHit searchHit : searchResult) {
			System.out.println(searchHit.getSource().toString());}
	}



	public SearchResponse multiMatchQuery(String index, 
			String type, 
			String queryString, 
			String... fields){
		QueryBuilder qb = QueryBuilders.multiMatchQuery(queryString, fields);
		SearchResponse searchResponse = this.client.prepareSearch(index)
				.setTypes(type)
				.setSearchType(SearchType.DFS_QUERY_AND_FETCH)
				.setQuery(qb)
				.execute()
				.actionGet();
		System.out.println(searchResponse);
		return searchResponse;
	}
	
	public String mutliSearch(){
		SearchRequestBuilder srb1 = client
			    .prepareSearch().setQuery(QueryBuilders.queryStringQuery("elasticsearch")).setSize(1);
			SearchRequestBuilder srb2 = client
			    .prepareSearch().setQuery(QueryBuilders.matchQuery("name", "kenechi")).setSize(1);
			
			SearchRequestBuilder srb3 = client
				    .prepareSearch().setQuery(QueryBuilders.queryStringQuery("product")).setSize(1);
				SearchRequestBuilder srb4 = client
				    .prepareSearch().setQuery(QueryBuilders.matchQuery("Brand", "samsung")).setSize(1);
			MultiSearchResponse sr = client.prepareMultiSearch()
			        .add(srb1)
			        .add(srb2)
			        .add(srb3) 
			        .add(srb4)
			        .execute().actionGet();
			System.out.println(sr);

			long nbHits = 0;
			for (MultiSearchResponse.Item item : sr.getResponses()) {
			    SearchResponse response = item.getResponse();
			    nbHits += response.getHits().getTotalHits();
			    
			}
			System.out.println(nbHits);
			return "MultiSearch operation successfullly";
	
	}



}


