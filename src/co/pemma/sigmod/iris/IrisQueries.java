package co.pemma.sigmod.iris;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.deri.iris.Configuration;
import org.deri.iris.EvaluationException;
import org.deri.iris.KnowledgeBaseFactory;
import org.deri.iris.api.IKnowledgeBase;
import org.deri.iris.api.basics.IPredicate;
import org.deri.iris.api.basics.IQuery;
import org.deri.iris.api.basics.IRule;
import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.compiler.Parser;
import org.deri.iris.compiler.ParserException;
import org.deri.iris.storage.IRelation;


public class IrisQueries 
{
	public static void main(String[] args) throws ParseException
	{
		//		String program = "poop(?g) :- person(?g).\r\n ?-poop(?g).\r\n";

		//		String program = "all_locs(?locid) :- place(?locid, 'Democratic_Republic_of_the_Congo').\r\n"
		//				+ "all_locs(?locid) :- all_locs(?parentlocid), place_isPartOf_place(?locid, ?parentlocid), place(?locid, ?name).\r\n"
		//				+ "all_orgs(?orgid) :- organisation(?orgid), organisation_isLocatedIn_place(?orgid, ?locid), all_locs(?locid).\r\n"
		//				+ "loc_people(?pid) :-  all_locs(?locid), person_isLocatedIn_place(?pid, ?locid).\r\n"
		//				+ "?-loc_people(?pid).\r\n";
		//				//+ "?-all_orgs(?locid).\r\n";
		//		
		//		IRelation results = runQuery(program, 3).get(0);
		//		ITuple tuple;
		//		for (int i = 0; i < results.size(); i++) {
		//			tuple = results.get(i);
		//			for (int j = 0; j < tuple.size(); ++j)
		//				System.out.print(tuple.get(j) + "\t");
		//			System.out.println();
		//		}


		///// Query 2 /////
		query2(3, "1980-02-01"); // Chiang_Kai-shek    Augustine_of_Hippo     Napoleon 
//		// % component sizes 22 16 16
		query2(4, "1981-03-10"); // Chiang_Kai-shek    Napoleon     Mohandas_Karamchand_Gandhi     Sukarno
//		//								 // % component sizes 17 13 11 11
		query2(3, "1982-03-29"); // Chiang_Kai-shek    Mohandas_Karamchand_Gandhi 	  Napoleon
//		//								 // % component sizes 13 11 10
		query2(3, "1983-05-09"); // Chiang_Kai-shek    Mohandas_Karamchand_Gandhi     Augustine_of_Hippo
//		//								 // % component sizes 12 10 8
		query2(5, "1984-07-02"); // Chiang_Kai-shek     Aristotle     Mohandas_Karamchand_Gandhi     Augustine_of_Hippo     Fidel_Castro
//		//								 // % component sizes 10 7 6 5 5
		query2(3, "1985-05-31"); // Chiang_Kai-shek     Mohandas_Karamchand_Gandhi    Joseph_Stalin
//		//								 // % component sizes 6 6 5
		query2(3, "1986-06-14"); // Chiang_Kai-shek     Mohandas_Karamchand_Gandhi    Joseph_Stalin
//		//								 // % component sizes 6 6 5
		query2(7, "1987-06-24"); // Chiang_Kai-shek     Augustine_of_Hippo     Genghis_Khan     Haile_Selassie_I     Karl_Marx 
//		// 						Lyndon_B._Johnson     Robert_John_\"Mutt\"_Lange    % component sizes 4 3 3 3 3 3 3
		query2(3, "1988-11-10"); // Aristotle     Ho_Chi_Minh     Karl_Marx
//		//								 // % component sizes 2 2 2
//		query2(4, "1990-01-25"); // Arthur_Conan_Doyle     Ashoka     Barack_Obama    Benito_Mussolini
		// % component sizes 1 1 1 1

		///// Query 3 /////
		//				query3(3, 2, "Asia"); 		// 361|812 174|280 280|812 % common interest counts 4 3 3
		//		query3(4, 3, "Indonesia");  // 396|398 363|367 363|368 363|372 % common interest counts 2 1 1 1
		//		query3(3, 2, "Egypt"); 	    // 110|116 106|110 106|112 % common interest counts 1 0 0
		//		query3(3, 2, "Italy");		// 420|825 421|424 10|414 % common interest counts 1 1 0
		//		query3(5, 4, "Chengdu");	// 590|650 590|658 590|614 590|629 590|638 % common interest counts 1 1 0 0 0
		//		query3(3, 2, "Peru");		// 65|766 65|767 65|863 % common interest counts 0 0 0
		//		query3(3, 2, 
		//		"Democratic_Republic_of_the_Congo");// 99|100 99|101 99|102 % common interest counts 0 0 0
		//		query3(7, 6, "Ankara"); 	// 891|898 890|891 890|895 890|898 890|902 891|895 891|902 % common interest counts 1 0 0 0 0 0 0
		//		query3(3, 2, "Luoyang");	// 565|625 653|726 565|653 % common interest counts 2 1 0
		//		query3(4, 3, "Taiwan");		// 795|798 797|798 567|795 567|796 % common interest counts 1 1 0 0 

	}

	private static List<IRelation> runQuery(String program, int queryNumber) 
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		configuration.externalDataSources.add(new SigmodDataSource(queryNumber));

		Parser parser = new Parser();
		List<IRelation> results = new ArrayList<>();

		try {
//			System.out.println("Parsing the program...");
			parser.parse(program);

			List<IRule> rules = parser.getRules();

//			System.out.println("Loading facts...");
			//Map<IPredicate, IRelation> facts = CreateRelations.getFacts();
			Map<IPredicate, IRelation> facts = parser.getFacts();

//			System.out.println("Constructing knowledge base...");
			IKnowledgeBase knowledgeBase = KnowledgeBaseFactory.createKnowledgeBase(facts, rules, configuration );

//			System.out.println("Evaluating queries...");
			List<IVariable> variableBindings = new ArrayList<>();
			for(IQuery query : parser.getQueries())
			{
				results.add(knowledgeBase.execute( query, variableBindings ));
			}

		} catch (ParserException | EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return results;
	}

	public static void query2(int k, String d) throws ParseException
	{
		StringBuilder sb = new StringBuilder();
		String date = "_date(" + d.replace("-", ",")+ ")";		

		System.out.println("Running query2("+k+", "+date+")");
		
		sb.append("young_people(?id) :- person(?id, ?birthday), ?birthday >= "+date+".\r\n");

		sb.append("conn_comps(?pid1, ?pid2, ?name) :- tag(?tag, ?name), young_people(?pid1), young_people(?pid2), "
				+ " person_knows_person(?pid1, ?pid2), person_hasInterest_tag(?pid1, ?tag), person_hasInterest_tag(?pid2, ?tag), "
				+ "NOT_EQUAL(?pid1, ?pid2).\r\n");

		sb.append("reach(?pid1, ?pid2, ?tag) :- conn_comps(?pid1, ?pid2, ?tag).\r\n");
		sb.append("reach(?pid1, ?pid2, ?tag) :- reach(?pid1,?y, ?tag), conn_comps(?y, ?pid2, ?tag), "
				+ "NOT_EQUAL(?pid1, ?pid2), NOT_EQUAL(?pid1, ?y), NOT_EQUAL(?y, ?pid2).\r\n");

		//		sb.append("conn_comps(?pid, ?tag) :- conn_comps(?pid2, ?tag), young_people(?pid), "
		//				+ "person_knows_person(?pid2, ?pid), person_hasInterest_tag(?pid, ?tag).\n");


		sb.append("?-reach(?pid1, ?pid2, ?tag).\r\n");
		//		sb.append("?-young_people(?pid1).\r\n");

		IRelation results = runQuery(sb.toString(), 2).get(0);

		//		Map<String, Integer> postProcess = new HashMap<>();
		//		Set<String> seen = new HashSet<>();
		//
		//		ITuple tuple;
		//		String tag, tagId;
		//
		//		System.out.println(results.size());
		//		for (int i = 0; i < results.size(); i++)
		//		{
		//			tuple = results.get(i);
		//			tag = tuple.get(2).toString();
		//			tagId = tag +"-"+ tuple.get(0).toString();
		//			if (!seen.contains(tagId))
		//			{
		//				if (postProcess.containsKey(tag))
		//				{
		//					postProcess.put(tag, postProcess.get(tag)+1);
		//				}
		//				else
		//					postProcess.put(tag, 1);
		//			}
		//			seen.add(tagId);
		//		}

		Map<String, Integer> tagsLargestComponent = new HashMap<>();
		Map<String, List<String>> tagLists = new HashMap<>();
		Set<String> seen = new HashSet<>();
		List<String> list;
		ITuple tuple;
		String tag, id1, id2;

		System.out.println(results.size());
		for (int i = 0; i < results.size(); i++)
		{
			tuple = results.get(i);
			id1 = tuple.get(0).toString();
			id2 = tuple.get(1).toString();
			tag = tuple.get(2).toString();


			if (tagLists.containsKey(tag)){
				list = tagLists.get(tag);
			}
			else{
				list = new ArrayList<>();
			}
			list.add(id1);

			list.add(id2);
			tagLists.put(tag, list);
		}

		Map<String, Integer> idCounts;
		int largestCC;
		for (Entry<String, List<String>> e : tagLists.entrySet())
		{
			idCounts = new HashMap<>();
			largestCC = 0;

			for (String id : e.getValue())
			{
				if (idCounts.containsKey(id))
					idCounts.put(id, idCounts.get(id)+1);
				else
					idCounts.put(id, 1);
			}
			for (Entry<String, Integer> e2 : idCounts.entrySet())
			{
				if (e2.getValue() > largestCC)
					largestCC = e2.getValue();
			}
			// add max count for this label to map
			tagsLargestComponent.put(e.getKey(), (largestCC/2)+1);
		}
		topKResults(k, tagsLargestComponent, new SigmodComparator(2));
	}

	/**
	 * @param k number of person id pairs to return
	 * @param h maximum number of hops between people
	 * @param p place persons must be located in or work in
	 */
	public static void query3(int k, int h, String p)
	{
		long start = System.currentTimeMillis();

		// formulate query to get all pairs of people with shared interests meeting place and hop criteria
		String query = "all_locs(?locid) :- place(?locid, '"+p+"').\r\n"
				+ "all_locs(?locid) :- all_locs(?parentlocid), place_isPartOf_place(?locid, ?parentlocid), place(?locid, ?name).\r\n"
				+ "all_orgs(?orgid) :- organisation(?orgid), organisation_isLocatedIn_place(?orgid, ?locid), all_locs(?locid).\r\n"
				+ "loc_people(?pid) :- person_isLocatedIn_place(?pid, ?locid), all_locs(?locid).\r\n"
				//+ "?-loc_people(?pid).\r\n";
				+ "org_people(?pid) :- person_workAt_organisation(?pid, ?orgid), all_orgs(?orgid).\r\n"
				+ "org_people(?pid) :- person_studyAt_organisation(?pid, ?orgid), all_orgs(?orgid).\r\n"

		+ "all_people(?pid) :- loc_people(?pid).\r\n"
		+ "all_people(?pid) :- org_people(?pid).\r\n"
		+ genHopsQuery(h)
		+ "common_interests(?pid1,?pid2,'__null') :- all_hops(?pid1, ?pid2), all_people(?pid1), all_people(?pid2).\r\n"
		+ "common_interests(?pid1,?pid2,?interest) :- common_interests(?pid1, ?pid2, '__null'), person_hasInterest_tag(?pid1,?interest), person_hasInterest_tag(?pid2,?interest).\r\n"

		+ "?-common_interests(?pid1,?pid2,?interest).\r\n";

		// get results from query
		IRelation results = runQuery(query, 3).get(0);

		//		for (int i = 0; i < results.size(); i++)
		//			System.out.println(results.get(i).toString());
		//
		//		System.exit(0);

		// filter out duplicates and find top k shared interest pairs
		Map<String, Integer> sharedInterestCounts = new HashMap<>();
		ITuple tuple;
		String pairKey;
		for (int i = 0; i < results.size(); i++)
		{
			tuple = results.get(i);
			//System.out.println(tuple.toString());

			// skip self pairs
			if (!tuple.get(0).equals(tuple.get(1)))
			{
				// filter out duplicates by skipping second in lexicographical order
				if (tuple.get(0).compareTo(tuple.get(1)) < 0)
				{
					pairKey = tuple.get(0)+","+tuple.get(1);
					if (sharedInterestCounts.containsKey(pairKey))
						sharedInterestCounts.put(pairKey, sharedInterestCounts.get(pairKey)+1);
					else
						sharedInterestCounts.put(pairKey, 0);
				}
			}
		}
		topKResults(k, sharedInterestCounts, new SigmodComparator(3));
		System.out.println("Query took " + ((System.currentTimeMillis() - start)) +" miliseconds.");
	}

	private static void topKResults(int k, Map<String, Integer> sharedInterestCounts, Comparator<Pair<Integer, String>> comp)
	{
		// get top k
		PriorityQueue<Pair<Integer, String>> topKPairs = new PriorityQueue<>(k, comp);
		for (Entry<String, Integer> e : sharedInterestCounts.entrySet())
		{
			ImmutablePair<Integer, String> newPair = new ImmutablePair<>(e.getValue(), e.getKey());
			if (topKPairs.size() < k) 
			{
				topKPairs.add(newPair);
			}				
			else if(comp.compare(newPair,topKPairs.peek()) > 0)
			{
				topKPairs.poll();
				topKPairs.add(newPair);
			}
		}

		ArrayList<Pair<Integer, String>> resultArray = new ArrayList<>(k);
		while(!topKPairs.isEmpty())
		{
			resultArray.add(topKPairs.poll());
		}
		Collections.sort(resultArray, comp);
		for(int i = k-1; i >= 0; --i)
			System.out.println(resultArray.get(i).toString());

	}

	private static String genHopsQuery(int h) {
		StringBuilder query = new StringBuilder();
		for(int i = 0; i < h; ++i){
			query.append("hop"+(i+1)+"(?pid0,?pid"+(i+1)+") :- ");
			for(int j = 0; j < i+1; ++j){
				query.append("person(?pid"+j+"), person_knows_person(?pid"+j+", ?pid"+(j+1)+"), ");
			}
			query.append("person(?pid"+(i+1)+").\r\n");
			query.append("all_hops(?pid1,?pid2) :- hop"+(i+1)+"(?pid1,?pid2).\r\n");
		}
		return query.toString();
	}



}
