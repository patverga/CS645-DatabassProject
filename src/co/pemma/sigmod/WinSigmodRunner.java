package co.pemma.sigmod;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
		
		query3(3, 2, "Asia");
		
	}

	private static void runQuery(String program) 
	{
		Configuration configuration = KnowledgeBaseFactory.getDefaultConfiguration();
		configuration.externalDataSources.add(new SigmodDataSource());
		
		Parser parser = new Parser();

		try {
			System.out.println("Parsing the program...");
			parser.parse(program);
			
			List<IRule> rules = parser.getRules();
			
			System.out.println("Loading facts...");
			//Map<IPredicate, IRelation> facts = CreateRelations.getFacts();
			Map<IPredicate, IRelation> facts = parser.getFacts();
			
			System.out.println("Constructing knowledge base...");
			IKnowledgeBase knowledgeBase = KnowledgeBaseFactory.createKnowledgeBase(facts, rules, configuration );
			
			long duration = -System.currentTimeMillis();
			StringBuilder output = new StringBuilder();

			System.out.println("Evaluating queries...");
			List<IVariable> variableBindings = new ArrayList<>();
			for(IQuery query : parser.getQueries()){
				duration = -System.currentTimeMillis();
				IRelation results = knowledgeBase.execute( query, variableBindings );
				duration += System.currentTimeMillis();

				
				
				String BAR = "|";
				String NEW_LINE = "\n";
				boolean SHOW_ROW_COUNT = true;
				boolean SHOW_QUERY_TIME = true;
				boolean SHOW_VARIABLE_BINDINGS = false;
				
				output.append( BAR ).append( NEW_LINE );
				output.append( "Query:      " ).append( query );
				if( SHOW_ROW_COUNT )
				{
					output.append( " ==>> " ).append( results.size() );
					if( results.size() == 1 )
						output.append( " row" );
					else
						output.append( " rows" );
				}
				if( SHOW_QUERY_TIME )
					output.append( " in " ).append( duration ).append( "ms" );
				
				output.append( NEW_LINE );
				
				if( SHOW_VARIABLE_BINDINGS )
				{
					output.append( "Variables:  " );
					boolean first = true;
					for( IVariable variable : variableBindings )
					{
						if( first )
							first = false;
						else
							output.append( ", " );
						output.append( variable );
					}
					output.append( NEW_LINE );
				}
			
				formatResults( output, results );
			}
			
		} catch (ParserException | EvaluationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Format the actual query results (tuples).
	 * @param builder
	 * @param m
	 */
	private static void formatResults( StringBuilder builder, IRelation m )
	{
		for(int t = 0; t < m.size(); ++t )
		{
			ITuple tuple = m.get( t );
			builder.append( tuple.toString() ).append( "\n" );
		}
		System.out.println(builder);
		//System.out.println(m.size() + " results.");

    }

	/**
	 * @param k number of person id pairs to return
	 * @param h maximum number of hops between people
	 * @param p place persons must be located in or work in
	 */
	public static void query3(int k, int h, String p)
	{
		// get all places
		String query = "all_locs(?locid) :- place(?locid, '"+p+"', ?x1, ?x2).\r\n"
		+ "all_locs(?locid) :- all_locs(?parentlocid), place_isPartOf_place(?locid, ?parentlocid), place(?locid, ?name, ?x1, ?x2).\r\n"
		+ "all_orgs(?orgid) :- organisation(?orgid, ?x1, ?x2, ?x3), organisation_isLocatedIn_place(?orgid, ?locid), all_locs(?locid).\r\n"
		+ "loc_people(?pid) :- person_isLocatedIn_place(?pid, ?locid), all_locs(?locid).\r\n"
		+ "org_people(?pid) :- person_workAt_organisation(?pid, ?orgid, ?x8), all_orgs(?orgid).\r\n"
		+ "org_people(?pid) :- person_studyAt_organisation(?pid, ?orgid, ?x8), all_orgs(?orgid).\r\n"
		
		+ "all_people(?pid) :- loc_people(?pid).\r\n"
		+ "all_people(?pid) :- org_people(?pid).\r\n"
		+ genHopsQuery(h)
		+ "common_interests(?pid1,?pid2,?interest) :- all_hops(?pid1, ?pid2), person_hasInterest_tag(?pid1,?interest), person_hasInterest_tag(?pid2,?interest).\r\n";
		query += "?-common_interests(?pid1,?pid2,?interest).\r\n";
//		query += "?-all_hops(?pid1, ?pid2).\r\n";
//		query += "?-hop1(?pid1, ?pid2).\r\n";
//		query += "?-hop2(?pid1, ?pid2).\r\n";
		
		
		runQuery(query);
	}

	private static String genHopsQuery(int h) {
		StringBuilder query = new StringBuilder();
		for(int i = 0; i < h; ++i){
			query.append("hop"+(i+1)+"(?pid0,?pid"+(i+1)+") :- ");
			for(int j = 0; j < i+1; ++j){
				query.append("all_people(?pid"+j+"), person_knows_person(?pid"+j+", ?pid"+(j+1)+"), ");
			}
			query.append("all_people(?pid"+(i+1)+").\r\n");
			query.append("all_hops(?pid1,?pid2) :- hop"+(i+1)+"(?pid1,?pid2).\r\n");
		}
		System.out.println(query.toString());
		return query.toString();
	}
	
	

}
