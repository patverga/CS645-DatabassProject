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

//		String program = "?-person_hasInterest_tag(?x, ?y), tag(?y, ?z, ?q), person(?x, ?a, ?b, ?c, ?d, ?e, ?f, ?g).";
		String program = "poop(?a,?b,?z) :- person_hasInterest_tag(?x, ?y), tag(?y, ?z, ?q), person(?x, ?a, ?b, ?c, ?d, ?e, ?f, ?g).\r\n ?-poop(?a,?b,?z).\r\n";

		//CreateRelations.readData("data/outputDir-1k");
		
		runQuery(program);
		
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
				boolean SHOW_VARIABLE_BINDINGS = true;
				
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

    }

	/**
	 * @param k number of person id pairs to return
	 * @param h maximum number of hops between people
	 * @param p place persons must be located in or work in
	 */
	public void query3(int k, int h, String p)
	{
		
	}

}
