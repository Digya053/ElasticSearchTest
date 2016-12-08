package suggesters;

import java.io.IOException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.suggest.SuggestRequestBuilder;
import org.elasticsearch.action.suggest.SuggestResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestionBuilder;
import org.elasticsearch.search.suggest.term.TermSuggestionBuilder;
import clientfactory.ClientFactory;

public class Suggesters {
	
	public Suggesters(){}

	public Suggesters(Client client){
		this.client = client;
	}
	
	private Client client = ClientFactory.getClient();

	public SuggestRequestBuilder termSuggestMethod(String index, String text,String field){
		SuggestBuilder.SuggestionBuilder suggestBuilder = new TermSuggestionBuilder("my-suggestion")
				.text(text).field(field).suggestMode("always");

		SuggestRequestBuilder requestBuilder = client.prepareSuggest(index)
				.addSuggestion(suggestBuilder);

		System.out.println(requestBuilder.get());
		return requestBuilder;

	}
	
	public void phaseSuggestMethod(String index, String text, String field){

		SuggestBuilder.SuggestionBuilder phraseSuggestionBuilder = 
				new PhraseSuggestionBuilder("phrase-suggest")
				.size(1)
				.text(text)
				.field(field);

		SuggestRequestBuilder requestBuilder = this.client.prepareSuggest(index)
				.addSuggestion(phraseSuggestionBuilder);

		System.out.println(requestBuilder.get());
	}

	public void completionSuggestMethod(){

		SuggestResponse completeSuggestionBuilder=this.client.prepareSuggest("hotels")
				.addSuggestion(new CompletionSuggestionBuilder("complete-suggest")
						.field("name_suggest")

						.text("m")
						.size(10))
				.execute().actionGet();

		System.out.println(completeSuggestionBuilder);


	}

	public void fuzzySuggest() throws IOException{
		/*NewParser parserr = new NewParser();
		List<String> data = parserr.parse("longdata.json");
		System.out.println(data);
		for (String string : data) {
			IndexResponse r= this.client.prepareIndex("product","product").
					setSource(string).execute().actionGet();
			System.out.println(r.isCreated());
		}
		try {
			Thread.sleep(2000);
		} 
			catch (InterruptedException e) {
				// TODO Auto-generated catch block
			e.printStackTrace();
		}*/

		Fuzziness fuzziness = null;
		QueryBuilder qb = QueryBuilders.fuzzyQuery("location", "brazil")
				.fuzziness(fuzziness.TWO);
		SearchResponse fResponse = this.client.prepareSearch("product")
				.setQuery(qb).execute().actionGet();
		System.out.println(fResponse.toString());
		System.out.println(fResponse.getHits().hits());
		SearchHit[] searchResult = fResponse.getHits().hits();
		
		for (SearchHit sh : searchResult) {
			System.out.println(sh.getSource().toString());

		}
	}
}

