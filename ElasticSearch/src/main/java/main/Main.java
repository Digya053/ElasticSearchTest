package main;

import java.io.IOException;

import org.elasticsearch.ElasticsearchException;

import crud.CrudData;
import mapping.Mapping;
import miscellaneous.Miscellaneous;
import suggesters.Suggesters;

public class Main {

	public static void main(String[] args) throws ElasticsearchException, IOException{
		System.out.println("Congratulations, the program is working!!");


		CrudData cdt = new CrudData();
		Suggesters sg = new Suggesters();
		Miscellaneous ms = new Miscellaneous();

		cdt.deleteIndex("newindex");
		cdt.deleteIndex("newindexp");

		cdt.createIndex();
		cdt.createIndexp("newindexp");

		//cdt.deleteIndex("testindex");
		//cdt.deleteIndex("testindexp");
		cdt.deleleDatap("newindexp", "newTypep", "4");

		cdt.insertData();
		String data= "{"
				+"\"Name\" : \"Prasumsa\","
				+ "\"Class\" :\"12\""
				+"}";
		cdt.insertDatap("newindexp", "newTypep", "2",data);
		cdt.insertDataFromFilep("newindex", "newType", "data.json");

		cdt.bulkInsertDataFromFile();
		cdt.bulkInsertDataFromFilep("newindexp", "newType","longdata.json" );

		cdt.search();
		cdt.searchp("newindexp", "newType","message","another");

		sg.termSuggestMethod("elasticsearch","elastisearch","book");
		sg.phaseSuggestMethod("hotels", "hotle monaco", "name");
		sg.completionSuggestMethod();
		sg.fuzzySuggest();

		ms.prefixQuery();
		ms.multiMatchQuery("product","product","1345","Brand","Amount","Quantity");
		ms.mutliSearch();

		Mapping mp = new Mapping();
		mp.Mapping();

	}

}
