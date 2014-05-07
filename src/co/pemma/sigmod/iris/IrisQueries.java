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
	static String DATA_LOCATION = "data-10k";
	
	public static void main(String[] args) throws ParseException
	{
		long start = System.currentTimeMillis();


		//		testQuery1();

//		testQuery2();

				testQuery3();

		System.out.println("Query took " + ((System.currentTimeMillis() - start)) +" miliseconds.");

	}


	private static void testQuery1()
	{

		/* Query 1 */
		//				query1(576, 400, -1);
		//		query1(58, 402, 0);
		//		query1(266, 106, -1);
		//		sb.append(generateQuery1(313, 523, -1) + "\n");
		//		sb.append(generateQuery1(858, 587, 1) + "\n");
		//		sb.append(generateQuery1(155, 355, -1) + "\n");
		query1(947, 771, -1);
		//		sb.append(generateQuery1(105, 608, 3) + "\n");
		//		sb.append(generateQuery1(128, 751, -1) + "\n");
		//		sb.append(generateQuery1(814, 641, 0) + "\n");

	}

	private static void testQuery2() throws ParseException {
		///// Query 2 - 1k /////
//		query2(3, "1980-02-01"); // Chiang_Kai-shek    Augustine_of_Hippo     Napoleon 
//		// % component sizes 22 16 16
//		query2(4, "1981-03-10"); // Chiang_Kai-shek    Napoleon     Mohandas_Karamchand_Gandhi     Sukarno
//		// % component sizes 17 13 11 11
//		query2(3, "1982-03-29"); // Chiang_Kai-shek    Mohandas_Karamchand_Gandhi 	  Napoleon
//		// % component sizes 13 11 10
//		query2(3, "1983-05-09"); // Chiang_Kai-shek    Mohandas_Karamchand_Gandhi     Augustine_of_Hippo
//		// % component sizes 12 10 8
//		query2(5, "1984-07-02"); // Chiang_Kai-shek     Aristotle     Mohandas_Karamchand_Gandhi     Augustine_of_Hippo     Fidel_Castro
//		// % component sizes 10 7 6 5 5
//		query2(3, "1985-05-31"); // Chiang_Kai-shek     Mohandas_Karamchand_Gandhi    Joseph_Stalin
//		// % component sizes 6 6 5
//		query2(3, "1986-06-14"); // Chiang_Kai-shek     Mohandas_Karamchand_Gandhi    Joseph_Stalin
//		// % component sizes 6 6 5
//		query2(7, "1987-06-24"); // Chiang_Kai-shek     Augustine_of_Hippo     Genghis_Khan     Haile_Selassie_I     Karl_Marx 
//		// Lyndon_B._Johnson     Robert_John_\"Mutt\"_Lange    % component sizes 4 3 3 3 3 3 3
//		query2(3, "1988-11-10"); // Aristotle     Ho_Chi_Minh     Karl_Marx
//		// % component sizes 2 2 2
//		query2(4, "1990-01-25"); // Arthur_Conan_Doyle     Ashoka     Barack_Obama    Benito_Mussolini
//		// % component sizes 1 1 1 1
		
		
		// 10k queries
		query2(3, "1980-02-01"); // Chiang_Kai-shek Sukarno George_W._Bush % component sizes 216 128 96
		query2(4, "1982-01-30"); //	Chiang_Kai-shek Sukarno Mohandas_Karamchand_Gandhi Ho_Chi_Minh % component sizes 176 102 69 68
		query2(3, "1984-02-01"); // Chiang_Kai-shek Sukarno Ho_Chi_Minh % component sizes 136 77 52
		query2(3, "1986-01-28"); // Chiang_Kai-shek Sukarno Ho_Chi_Minh % component sizes 80 50 32
		query2(5, "1988-01-27"); // Chiang_Kai-shek Ho_Chi_Minh Sukarno Thaksin_Shinawatra Elena_Likhovtseva % component sizes 38 21 18 16 14		
		query2(3, "1990-01-31"); // Abraham_Lincoln Amartya_Sen Roy_Orbison % component sizes 1 1 1

		
		
		
	}


	private static void testQuery3() {
		///// Query 3 /////
//		query3(3, 2, "Asia"); 		// 361|812 174|280 280|812 % common interest counts 4 3 3
//		query3(4, 3, "Indonesia");  // 396|398 363|367 363|368 363|372 % common interest counts 2 1 1 1
//		query3(3, 2, "Egypt"); 	    // 110|116 106|110 106|112 % common interest counts 1 0 0
//		query3(3, 2, "Italy");		// 420|825 421|424 10|414 % common interest counts 1 1 0
//		query3(5, 4, "Chengdu");	// 590|650 590|658 590|614 590|629 590|638 % common interest counts 1 1 0 0 0
//		query3(3, 2, "Peru");		// 65|766 65|767 65|863 % common interest counts 0 0 0
//		query3(3, 2, 				// 99|100 99|101 99|102 % common interest counts 0 0 0
//				"Democratic_Republic_of_the_Congo");
//		query3(7, 6, "Ankara"); 	// 891|898 890|891 890|895 890|898 890|902 891|895 891|902 % common interest counts 1 0 0 0 0 0 0
//		query3(3, 2, "Luoyang");	// 565|625 653|726 565|653 % common interest counts 2 1 0
//		query3(4, 3, "Taiwan");		// 795|798 797|798 567|795 567|796 % common interest counts 1 1 0 0 
		
		// 10k queries
		query3(3, 2, "Asia");				//  230|1814 1814|1857 1814|2219 % common interest counts 5 5 5
		query3(4, 3, "Dolgoprudny");		//	8132|8195 8084|8132 8084|8161 8084|8185 % common interest counts 1 0 0 0
		query3(3, 2, "Yongkang_District");	//	7953|7981 7953|7987 7953|7989 % common interest counts 1 1 1		
	}

	private static List<IRelation> runQuery(String program, int queryNumber) 
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		configuration.externalDataSources.add(new SigmodDataSource(queryNumber, DATA_LOCATION));

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

	/**
	 * Generate Iris code for SIGMOD query 1
	 * 
	 * @param pid1 pid of first person
	 * @param pid2 pid of second person
	 * @param k min number of comments between people to consider them frequent communicators
	 * @return StringBuffer representing the generated code
	 */
	public static StringBuilder query1(long pid1, long pid2, int k)
	{
		System.out.println("Running query1("+pid1+", "+pid2+", "+k+")");

		StringBuilder sb = new StringBuilder();
		/* start_pairs: pairs of people on a path from the start person */
		sb.append("start_pairs(?"+pid1+", ?pid2) :- person_knows_person(?"+pid1+", ?pid2).\n");
		//		sb.append("start_pairs(?pid1, ?pid2) :- start_pairs(?x, ?pid1), person_knows_person(?pid1, ?pid2).\n");

		/* end_pairs: pairs of people on a path to the end person */
		sb.append("end_pairs(?pid2, ?"+pid2+") :- person_knows_person(?"+pid2+", ?pid2).\n");
		//		sb.append("end_pairs(?pid1, ?pid2) :- end_pairs(?pid2, ?x), person_knows_person(?pid1, ?pid2).\n");

		/* pairs: pairs of people on a path from the start to the end person */
		sb.append("pairs(?pid1, ?pid2) :- start_pairs(?pid1, ?pid2).\n");
		sb.append("pairs(?pid1, ?pid2) :- end_pairs(?pid1, ?pid2).\n");

		if (k > 0)
		{
			// people who have commented on each others comments
			sb.append("communications(?pid1, ?pid2) :- comment_hasCreator_person(?cid1, ?pid1), comment_hasCreator_person(?cid2, ?pid2), comment_replyOf_comment(?cid1, ?cid2).\n");		
			// people who have commented on each others comments and know each other
			sb.append("friend_communications(?pid1, ?pid2) :- communications(?pid1, ?pid2), person_knows_person(?pid1, ?pid2).\n");
			// people who know each other, commented, and are on paths containing the start and/or end pids
			sb.append("valid_pairs(?pid1, ?pid2) :- communications(?pid1, ?pid2), pairs(?pid1, ?pid2).\n");
			////  TO DO  ////
			// each pair must occur atleast k times
			//			sb.append("pair_counts(?pid1, ?pid2, 0) :- valid_pairs(?pid1, ?pid2).\n");
			//			sb.append("pair_counts(?pid1, ?pid2, ?count) :- valid_pairs(?pid1, ?pid2), pair_counts(?pid1, ?pid2, ?c), ADD(?c, 1, ?count).\n");

		}
		else
		{
			// people who know each other, commented, and are on paths containing the start and/or end pids
			sb.append("valid_pairs(?pid1, ?pid2) :- pairs(?pid1, ?pid2).\n");
		}


		sb.append("reach(?pid1, ?pid2) :- valid_pairs(?pid1, ?pid2).\r\n");
		sb.append("reach(?pid1, ?pid2) :- reach(?pid1,?y), valid_pairs(?y, ?pid2), NOT_EQUAL(?pid1, ?pid2).\r\n");

		sb.append("?-reach(?pid1, ?pid2).\r\n");

		IRelation results = runQuery(sb.toString(), 1).get(0);
		ITuple tuple;

		String id1, id2;
		long lid1, lid2;
		System.out.println(results.size());
		Map<String, Integer> idCounts = new HashMap<>();

		for (int i = 0; i < results.size(); i++)
		{
			tuple = results.get(i);
			id1 = tuple.get(0).toString();
			id2 = tuple.get(1).toString();
			lid1 = Long.parseLong(id1);
			lid2 = Long.parseLong(id2);
			if (lid2 == pid1 || lid2 == pid2)
			{
				if (idCounts.containsKey(id1))
					idCounts.put(id1, idCounts.get(id1)+1);
				else
					idCounts.put(id1, 1);
			}
			if (lid1 == pid1 || lid1 == pid2)
			{
				if (idCounts.containsKey(id2))
					idCounts.put(id2, idCounts.get(id2)+1);
				else
					idCounts.put(id2, 1);		
			}
		}

		topKResults(idCounts.size(), idCounts, new SigmodComparator(2));

		return sb;
	}

	public static void query2(int k, String d) throws ParseException
	{
		StringBuilder sb = new StringBuilder();
		String date = d.replace("-", ",");		

		System.out.println("Running query2("+k+", "+date+")");

		sb.append("young_people(?id) :- person(?id, ?birthday), ?birthday >= _date("+date+").\r\n");

		sb.append("conn_comps(?pid1, ?pid2, ?name) :- tag(?tag, ?name), young_people(?pid1), young_people(?pid2), "
				+ " person_knows_person(?pid1, ?pid2), person_hasInterest_tag(?pid1, ?tag), person_hasInterest_tag(?pid2, ?tag), "
				+ "NOT_EQUAL(?pid1, ?pid2).\r\n");

		sb.append("reach(?pid1, ?pid1, ?name) :- young_people(?pid1), person_hasInterest_tag(?pid1, ?tag), tag(?tag, ?name).\r\n");
		sb.append("reach(?pid1, ?pid2, ?tag) :- reach(?pid1,?y, ?tag), conn_comps(?y, ?pid2, ?tag).\r\n");

		sb.append("?-reach(?pid1, ?pid2, ?tag).\r\n");

		IRelation results = runQuery(sb.toString(), 2).get(0);


		Map<String, Integer> tagsLargestComponent = new HashMap<>();
		Map<String, List<String>> tagLists = new HashMap<>();
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
			tagsLargestComponent.put(e.getKey(), (largestCC/2));
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
		System.out.println("Running query3("+k+", "+h+", "+p+")");

		// formulate query to get all pairs of people with shared interests meeting place and hop criteria
		StringBuilder sb = new StringBuilder();
		sb.append("all_locs(?locid) :- place(?locid, '"+p+"').\r\n");
		sb.append("all_locs(?locid) :- all_locs(?parentlocid), place_isPartOf_place(?locid, ?parentlocid), place(?locid, ?name).\r\n");
		sb.append("all_orgs(?orgid) :- organisation(?orgid), organisation_isLocatedIn_place(?orgid, ?locid), all_locs(?locid).\r\n");
		sb.append("loc_people(?pid) :- person_isLocatedIn_place(?pid, ?locid), all_locs(?locid).\r\n");
		//+ "?-loc_people(?pid).\r\n";
		sb.append("org_people(?pid) :- person_workAt_organisation(?pid, ?orgid), all_orgs(?orgid).\r\n");
		sb.append("org_people(?pid) :- person_studyAt_organisation(?pid, ?orgid), all_orgs(?orgid).\r\n");

		sb.append("all_people(?pid) :- loc_people(?pid).\r\n");
		sb.append("all_people(?pid) :- org_people(?pid).\r\n");

		sb.append(genHopsQuery(h));
		//		+ "common_interests(?pid1, ?pid2, '__null') :- all_hops(?pid1, ?pid2), all_people(?pid1), all_people(?pid2).\r\n"
		//		+ "common_interests(?pid1, ?pid2, ?interest) :- common_interests(?pid1, ?pid2, '__null'), person_hasInterest_tag(?pid1,?interest), person_hasInterest_tag(?pid2,?interest).\r\n"

		for(int i = 1; i <= h; ++i){
			sb.append("common_interests(?pid1, ?pid2, ?interest) :- hop"+i+"(?pid1, ?pid2), ?interest=-1.\n");
			sb.append("common_interests(?pid1, ?pid2, ?interest) :- hop"+i+"(?pid1, ?pid2), person_hasInterest_tag(?pid1, ?interest), person_hasInterest_tag(?pid2, ?interest).\n");
		}

		sb.append("?-common_interests(?pid1, ?pid2, ?interest).\r\n");

		// get results from query
		IRelation results = runQuery(sb.toString(), 3).get(0);

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
	}

	private static StringBuilder genHopsQuery(int h) 
	{
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < h; ++i)
		{
			if(i == 0){
				sb.append("temp1(?pid0, ?pid1) :- all_people(?pid0), person_knows_person(?pid0, ?pid1).\n");
				sb.append("hop"+(i+1)+"(?pid0, ?pid"+(i+1)+") :- ");
				sb.append("temp1(?pid0, ?pid1), all_people(?pid"+(i+1)+"), ?pid0!=?pid"+(i+1)+".\n");
			}
			else{
				sb.append("temp"+(i+1)+"(?pid0, ?pid2) :- temp"+i+"(?pid0, ?pid1), person(?pid1), person_knows_person(?pid1, ?pid2).\n");
				sb.append("hop"+(i+1)+"(?pid0, ?pid1) :- temp"+(i+1)+"(?pid0, ?pid1), all_people(?pid1), ?pid0!=?pid1, not hop"+i+"(?pid0, ?pid1).\n");
			}
		}
		return sb;
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
}
