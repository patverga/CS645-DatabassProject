package co.pemma.sigmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

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


public class WinSigmodRunner 
{
	public static void main(String[] args)
	{
		//		String program = "poop(?a,?b,?z) :- person_hasInterest_tag(?x, ?y), tag(?y, ?z, ?q), person(?x, ?a, ?b, ?c, ?d, ?e, ?f, ?g).\r\n ?-poop(?a,?b,?z).\r\n";

		//		runQuery(program);

//		query3(3, 2, "Asia");
//		query3(4, 3, "Indonesia");
//		query3(3, 2, "Egypt");
//		query3(3, 2, "Italy");
//		query3(5, 4, "Chengdu");
//		query3(3, 2, "Peru");
		query3(3, 2, "Democratic_Republic_of_the_Congo");
//		query3(7, 6, "Ankara");
//		query3(3, 2, "Luoyang");
//		query3(4, 3, "Taiwan");

	}

	private static List<IRelation> runQuery(String program) 
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		configuration.externalDataSources.add(new SigmodDataSource());

		Parser parser = new Parser();
		List<IRelation> results = new ArrayList<>();

		try {
			System.out.println("Parsing the program...");
			parser.parse(program);

			List<IRule> rules = parser.getRules();

			System.out.println("Loading facts...");
			//Map<IPredicate, IRelation> facts = CreateRelations.getFacts();
			Map<IPredicate, IRelation> facts = parser.getFacts();

			System.out.println("Constructing knowledge base...");
			IKnowledgeBase knowledgeBase = KnowledgeBaseFactory.createKnowledgeBase(facts, rules, configuration );

			System.out.println("Evaluating queries...");
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
	 * @param k number of person id pairs to return
	 * @param h maximum number of hops between people
	 * @param p place persons must be located in or work in
	 */
	public static void query3(int k, int h, String p)
	{
		// formulate query to get all pairs of people with shared interests meeting place and hop criteria
		String query = "all_locs(?locid) :- place(?locid, '"+p+"', ?x1, ?x2).\r\n"
				+ "all_locs(?locid) :- all_locs(?parentlocid), place_isPartOf_place(?locid, ?parentlocid), place(?locid, ?name, ?x1, ?x2).\r\n"
				+ "all_orgs(?orgid) :- organisation(?orgid, ?x1, ?x2, ?x3), organisation_isLocatedIn_place(?orgid, ?locid), all_locs(?locid).\r\n"
				+ "loc_people(?pid) :- person_isLocatedIn_place(?pid, ?locid), all_locs(?locid).\r\n"
				+ "org_people(?pid) :- person_workAt_organisation(?pid, ?orgid, ?x8), all_orgs(?orgid).\r\n"
				+ "org_people(?pid) :- person_studyAt_organisation(?pid, ?orgid, ?x8), all_orgs(?orgid).\r\n"

		+ "all_people(?pid) :- loc_people(?pid).\r\n"
		+ "all_people(?pid) :- org_people(?pid).\r\n"
		+ genHopsQuery(h)
		+ "common_interests(?pid1,?pid2,'__null') :- all_hops(?pid1, ?pid2), all_people(?pid1), all_people(?pid2).\r\n"
		+ "common_interests(?pid1,?pid2,?interest) :- common_interests(?pid1, ?pid2, '__null'), person_hasInterest_tag(?pid1,?interest), person_hasInterest_tag(?pid2,?interest).\r\n"

		//+ "?-common_interests(?pid1,?pid2,?interest).\r\n";
		+ "?-all_hops(?pid1, ?pid2).\r\n";

		
		// get results from query
		IRelation results = runQuery(query).get(0);
		
		for (int i = 0; i < results.size(); i++)
			System.out.println(results.get(i).toString());

		System.exit(0);
		
		// filter out duplicates and find top k shared interest pairs
		Map<String, Integer> sharedInterestCounts = new HashMap<>();
		ITuple tuple;
		String pairKey;
		for (int i = 0; i < results.size(); i++)
		{
			tuple = results.get(i);
			System.out.println(tuple.toString());
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
		// get top k
		SigmodComparator comp = new SigmodComparator();
		PriorityQueue<Pair<Integer, String>> topKPairs = new PriorityQueue<>(k, comp);
		for (String key : sharedInterestCounts.keySet())
		{
			ImmutablePair<Integer, String> newPair = new ImmutablePair<>(sharedInterestCounts.get(key), key);
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
		System.out.println(topKPairs.size() + "  POOP");
		while(!topKPairs.isEmpty())
		{
			resultArray.add(topKPairs.poll());
		}
		for(int i = k-1; i >= 0; --i)
			System.out.println(resultArray.get(i).toString());
	}

	private static String genHopsQuery(int h) {
		StringBuilder query = new StringBuilder();
		for(int i = 0; i < h; ++i){
			query.append("hop"+(i+1)+"(?pid0,?pid"+(i+1)+") :- ");
			for(int j = 0; j < i+1; ++j){
				query.append("person(?pid"+j+", ?x1, ?x2, ?x3, ?x4, ?x5, ?x6, ?x7), person_knows_person(?pid"+j+", ?pid"+(j+1)+"), ");
			}
			query.append("person(?pid"+(i+1)+", ?x1, ?x2, ?x3, ?x4, ?x5, ?x6, ?x7).\r\n");
			query.append("all_hops(?pid1,?pid2) :- hop"+(i+1)+"(?pid1,?pid2).\r\n");
		}
		System.out.println(query.toString());
		return query.toString();
	}
	
	

}
