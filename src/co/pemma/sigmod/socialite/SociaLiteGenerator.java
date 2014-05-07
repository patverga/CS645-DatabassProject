package co.pemma.sigmod.socialite;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import co.pemma.sigmod.Util;
import co.pemma.sigmod.iris.CreateRelations;

public class SociaLiteGenerator 
{	
	public static final String queryFile = "socialite/bin/query.py";

	public static StringBuilder generateQueryTables(Map<String,List<String>> colMap, Map<String,List<String>> indexMap)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\nprint \"Loading the tables now ...  \"\n");

		Map<String, List<String>> schema = CreateRelations.readSchema();

		sb.append("`");

		List<String> indices;
		for(Entry<String, List<String>> entry : colMap.entrySet() )
		{
			if (indexMap != null && indexMap.containsKey(entry.getKey()))
				indices = indexMap.get(entry.getKey());
			else
				indices = null;
			sb.append( generateTable(entry.getKey(), entry.getValue(), schema.get(entry.getKey()), indices) );
		}
		sb.append("`");

		sb.append("\nprint \"Done loading tables \"\n");

		return sb;
	}

	/**
	 * Generate SociaLite code to load data into tables
	 * 
	 * @param tableName name of the table to generate SociaLite for
	 * @param colNames names of columns that we want to load
	 * @param schema SIGMOD db schema
	 * @return StringBuffer representing generated code
	 */
	public static StringBuilder generateTable(String tableName, List<String> colNames, List<String> schema, List<String> indices)
	{		
		StringBuilder sb = new StringBuilder();

		// figure out indeces of the columns we want
		List<Integer> colIndeces = new ArrayList<>();		
		Map<Integer, String> schemaIndexNameMap = new HashMap<>();
		Map<String, Integer> schemaNameIndexMap = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader("data/"+tableName+".csv")))
		{
			// start and end with a backtick
			//			sb.append("`");

			// first line of file is schema
			String line  = reader.readLine();
			String[] tuple = line.split("\\|");

			// map col names to their indices and back
			for(int i = 0; i < tuple.length; i++)
			{
				String col = tuple[i];
				if (schemaNameIndexMap.containsKey(col))
					col += "2";
				schemaIndexNameMap.put(i, col);
				schemaNameIndexMap.put(col, i);
				if (colNames.contains(col))
					colIndeces.add(i);
			}

			// define the table
			String type;
			sb.append(tableName+"(");
			for (int i = 0; i < colNames.size()-1; i++)
			{
				type = schema.get(schemaNameIndexMap.get(colNames.get(i)));
				if (!type.equals("String"))
					type = type.toLowerCase();
				sb.append(type + " " + colNames.get(i).replace(".", "") + ", ");
			}
			type = schema.get(schemaNameIndexMap.get(colNames.get(colNames.size()-1)));
			if (!type.equals("String"))
				type = type.toLowerCase();
			sb.append(type + " " + colNames.get(colNames.size()-1).replace(".", "")+")");

			// set indices over table
			if (indices != null && !indices.isEmpty())
			{
				for (int i = 0; i < indices.size()-1; i++)
					sb.append(" indexby " + indices.get(i).replace(".", "") + ",");			
				sb.append(" indexby " + indices.get(indices.size()-1).replace(".", ""));
			}
			sb.append(".");

			// read the data from file into the table
			sb.append("\n");
			sb.append(tableName+"(");
			for (int i = 0; i < colNames.size()-1; i++)
				sb.append(colNames.get(i).replace(".", "")+", ");
			sb.append(colNames.get(colNames.size()-1).replace(".", "")+")");
			sb.append(" :- ");
			sb.append("l=$read(\"/home/pv/Documents/CS645-DatabassProject/data/commas/"+ tableName + ".csv\"), ");

			String splitString = "(";
			for (int i = 0; i < tuple.length-1; i++)
				splitString += "v"+i+",";
			splitString += "v"+(tuple.length-1)+")";
			sb.append(splitString + "=$split(l,\",\")");

			int index;
			for(String col : colNames)
			{

				sb.append(", "+col.replace(".", "")+"=");
				index = schemaNameIndexMap.get(col);
				type = schema.get(index);

				if (type.equals("Integer"))
					sb.append("$toInt(v" + index+")");
				else if (type.equals("Long"))
					sb.append("$toLong(v" + index+")");
				else
					sb.append("v" + index);
			}
			sb.append(".\n");

			// start and end with a backtick
			//			sb.append(".` \n");
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb;
	}

	/**
	 * Generate SociaLite code for SIGMOD query 3
	 * 
	 * @param k number of person id pairs to return
	 * @param h maximum number of hops between people
	 * @param p place persons must be located in or work in
	 * @return StringBuffer representing the generated code
	 */
	public static StringBuilder generateQuery3(int k, int h, String p){
		StringBuilder sb = new StringBuilder();

		sb.append("\nimport time\n");
		sb.append("\nstart_time = time.time()\n");
		sb.append("\nprint \"Running query3("+k+", "+h+", \\\""+ p +"\\\")\"\n");
		/* all_locs: all the locations that we care about (have to get sub-locations) */
		sb.append("def inc(n, by): return n+by\n\n");
		sb.append("`");
		sb.append("all_locs(long locid).\n");
		sb.append("all_locs(locid) :- place(locid, \""+p+"\");\n");
		sb.append("\t:- all_locs(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).\n");

		/* all_orgs: all the organizations that we care about (orgs in all_locs places) */
		sb.append("all_orgs(long orgid).\n");
		sb.append("all_orgs(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs(locid).\n");

		/* loc_people: people located in all_locs */
		sb.append("loc_people(long pid).\n");
		sb.append("loc_people(pid) :- person_isLocatedIn_place(pid, locid), all_locs(locid).\n");

		/* org_people: people who work at organizations in all_orgs */
		sb.append("org_people(long pid).\n");
		sb.append("org_people(pid) :- person_workAt_organisation(pid, orgid), all_orgs(orgid);\n");
		sb.append("\t:- person_studyAt_organisation(pid, orgid), all_orgs(orgid).\n");

		/* all_people: people from all_orgs or all_locs */
		sb.append("all_people(long pid).\n");
		sb.append("all_people(pid) :- loc_people(pid);\n");
		sb.append("\t:- org_people(pid).\n");

		/* all_hops: all_people who are h or less hops away from each other */
		sb.append(genHopsQuery(h));

		/* common_interests: people with common interests in all_hops */
		sb.append("common_interests(long pid1, long pid2, long interest).\n");
		sb.append("common_interests(pid1, pid2, interest) :- all_hops(pid1, pid2), pid1 != pid2, all_people(pid1), all_people(pid2), interest=-1L;\n");
		sb.append("\t:- all_hops(pid1, pid2), all_people(pid1), all_people(pid2), pid1 != pid2, person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).\n");
		//sb.append("\t:- common_interests(pid1, pid2, interest), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).\n");

		/* interest_counts: counts of interests for each pair */
		sb.append("interest_counts(long pid1, long pid2, int count).\n");
		sb.append("interest_counts(pid1, pid2, $inc(1)) :- common_interests(pid1, pid2, interest).\n");

		sb.append("sorted_counts(int count, long pid1, long pid2).\n");
		sb.append("sorted_counts(count, pid1, pid2) :- interest_counts(pid1, pid2, c), count=c-1.\n");
		sb.append("`\n");

		//		sb.append("for count,pid1,pid2 in `sorted_counts(count,pid1,pid2)`:\n");
		//		sb.append("\tprint pid1,pid2,count\n");
		//		sb.append("count=0\n");
		//		sb.append("for pid1, pid2, count in `interest_counts(pid1, pid2, count)`:\n");
		//		sb.append("\tprint pid1, pid2, count\n");
		//		sb.append("\tcount += 1\n");
		//		sb.append("\tif count>"+k+": break;\n");


		sb.append(sortedOutput3(k));
		sb.append("\nprint time.time() - start_time\n");
		
		return sb;
	}

	/**
	 * @param k number of results to return
	 * @return
	 */
	public static StringBuilder sortedOutput2(int k)
	{
		StringBuilder sb = new StringBuilder();

		// sort the results in the way we want
		sb.append("from operator import itemgetter \n");
		sb.append("result_set = `tag_sizes(count,name)`\n");
		sb.append("result_set = sorted(result_set, key=lambda x:(-x[0],x[1]))\n");

		// print results, removing duplicates
		sb.append("used = set()\n");
		sb.append("results = 0\n");
		sb.append("for count, name in result_set:\n");
		sb.append("\tif results >= "+k+":\n");
		sb.append("\t\tbreak\n");
		sb.append("\tif name not in used: \n");
		sb.append("\t\tprint name, count\n");
		sb.append("\t\tused.add(name)\n");
		sb.append("\t\tresults += 1\n");

		return sb;
	}
	
	/**
	 * @param k number of results to return
	 * @return
	 */
	public static StringBuilder sortedOutput3(int k)
	{
		StringBuilder sb = new StringBuilder();

		// sort the results in the way we want
		sb.append("from operator import itemgetter \n");
		sb.append("result_set = `sorted_counts(count,pid1,pid2)`\n");
		sb.append("result_set = sorted(result_set, key=lambda x:(-x[0],x[1],x[2]))\n");

		// keep a set to remove duplicate (a,b) = (b,a)
		sb.append("used = set() \n");
		sb.append("results = 0 \n");
		sb.append("for count,pid1,pid2 in result_set:\n");
		sb.append("\tif results >= "+k+":\n");
		sb.append("\t\tbreak \n");
		sb.append("\tif pid1 > pid2:\n");
		sb.append("\t\tid = str(pid2) + '-' + str(pid1)\n");
		sb.append("\telse:\n");
		sb.append("\t\tid = str(pid1) + '-' + str(pid2)\n");

		// if this is a new element print it, add to set
		sb.append("\tif id not in used: \n");
		sb.append("\t\tprint pid1,pid2,count\n");
		sb.append("\t\tused.add(id) \n");
		sb.append("\t\tresults += 1 \n");

		return sb;
	}

	/**
	 * Generate SociaLite code for SIGMOD query 1
	 * 
	 * @param pid1 pid of first person
	 * @param pid2 pid of second person
	 * @param numComments min number of comments between people to consider them frequent communicators
	 * @return StringBuffer representing the generated code
	 */
	public static StringBuilder generateQuery1(long pid1, long pid2, int numComments){
		StringBuilder sb = new StringBuilder();

		/* aggregate function for incrementing a count */
		sb.append("def inc(n, by): return n+by\n\n");
//		sb.append("c = 0\n");
//		sb.append("def counter(n,by):\n");
//		sb.append("\tc += by\n");
//		sb.append("\treturn c-by\n\n");
		sb.append("`");

		/* start_pairs: pairs of people on a path from the start person */
		sb.append("start_pairs(long pid1, long pid2).\n");
		sb.append("start_pairs("+pid1+"L, pid2) :- person_knows_person("+pid1+"L, pid2).\n");
		sb.append("start_pairs(pid1, pid2) :- start_pairs(x, pid1), person_knows_person(pid1, pid2).\n");
		
		/* end_pairs: pairs of people on a path to the end person */
		sb.append("end_pairs(long pid1, long pid2).\n");
		sb.append("end_pairs("+pid2+"L, pid2) :- person_knows_person("+pid2+"L, pid2).\n");
		sb.append("end_pairs(pid1, pid2) :- end_pairs(pid2, x), person_knows_person(pid1, pid2).\n");
		
//		sb.append("garbage(long pid1, long pid2).\n");
//		sb.append("garbage("+pid1+"L, pid2) :- start_pairs(pid1, pid2), end_pairs(pid1, pid2).\n");
		
		/* pairs: pairs of people on a path from the start to the end person */
		sb.append("pairs(long pid1, long pid2).\n");
		sb.append("pairs(pid1, pid2) :- start_pairs(pid1, pid2), end_pairs(pid1, pid2).\n");
//		sb.append("pairs("+pid1+"L, pid2) :- person_knows_person("+pid1+"L, pid2).\n");
		
//		sb.append("poop(long pid1, long pid2).\n");
//		sb.append("poop("+pid1+"L, pid2) :- person_knows_person("+pid1+"L, pid2).\n");
		
		/* communications: pairs of people who know each other for each comment made in reply to each other */
//		sb.append("communications(long pid1, long pid2, int count).\n");
//		sb.append("communications(pid1, pid2, $inc(1)) :- person_knows_person(pid1, pid2);\n");
//		sb.append("\t:- pid1 != pid2, comment_hasCreator_person(cid1, pid1), comment_hasCreator_person(cid2, pid2), comment_replyOf_comment(cid1, cid2), person_knows_person(pid1, pid2).\n");
//		sb.append("communications(long pid1, long pid2, int x).\n");
//		sb.append("communications(pid1, pid2, x) :- person_knows_person(pid1, pid2), x=1;\n");
//		sb.append("\t:- pid1 != pid2, x=1, comment_hasCreator_person(cid1, pid1), comment_hasCreator_person(cid2, pid2), comment_replyOf_comment(cid1, cid2), person_knows_person(pid1, pid2).\n");
		
		if(numComments > -1){
			sb.append("communications(long pid1, long pid2).\n");
			sb.append("communications(pid1, pid2) :- pairs(pid1, pid2), comment_hasCreator_person(cid1, pid1), comment_hasCreator_person(cid2, pid2), comment_replyOf_comment(cid1, cid2).\n");
		}
		
		sb.append("reach(long pid, int len).\n");
		sb.append("reach(pid, len) :- pairs("+pid1+"L, pid), len=1.\n");
		sb.append("reach(pid, len) :- reach(y,c), pairs(y,pid), len=c+1.\n");
		
		sb.append("`\n");
		
		sb.append("for pid1,pid2,c in `reach(pid1,pid2)`:\n");
		sb.append("\tprint pid1,pid2,c\n");
		 
		return sb;
	}
	
	/**
	 * Generate SociaLite code for SIGMOD query 2
	 * 
	 * @param k number of interest tags to return
	 * @param d only consider people born on this date or later
	 * @return StringBuffer representing the generated code
	 */
	public static StringBuilder generateQuery2(int k, String d)
	{
		StringBuilder sb = new StringBuilder();
		String[] date = d.split("-");
		String year = date[0];
		String month = date[1];
		String day = date[2];
		
		int ageCutoff = Integer.parseInt(year)*10000+Integer.parseInt(month)*100+Integer.parseInt(day);

		sb.append("def inc(n, by): return n+by\n\n");
		sb.append("`");

		/* young_people: all of the people born after the defined date */
		sb.append("young_people(long id).\n");
		sb.append("young_people(id) :- person(id, date), (y,m,d)=$split(date, \"-\"), "
				+ "$toInt(y)*10000+$toInt(m)*100+$toInt(d) >= " + ageCutoff + ".\n");
		
		/* conn_comps: all pairs with paths and sharing tags */
//		sb.append("conn_comps(long pid1, long pid2, long tag).\n");
//		sb.append("conn_comps(pid1, pid2, tag) :- young_people(pid1), person_hasInterest_tag(pid1, tag), pid2=pid1;\n");
//		sb.append("\t:- conn_comps(pid1, y, tag), person_knows_person(y, pid2), young_people(pid2), person_hasInterest_tag(pid2, tag).\n");
		sb.append("conn_comps(long pid1, long pid2, long tag).\n");
		sb.append("conn_comps(pid1, pid2, tag) :- young_people(pid1), person_hasInterest_tag(pid1, tag), person_hasInterest_tag(pid2, tag), young_people(pid2), person_knows_person(pid1, pid2);\n");
		sb.append("\t:- conn_comps(pid1, y, tag), person_knows_person(y, pid2), pid1 != pid2, young_people(pid2), person_hasInterest_tag(pid2, tag).\n");
		
		/*
		sb.append("conn_comps(?pid1, ?pid2, ?name) :- tag(?tag, ?name), young_people(?pid1), young_people(?pid2), "
				+ " person_knows_person(?pid1, ?pid2), person_hasInterest_tag(?pid1, ?tag), person_hasInterest_tag(?pid2, ?tag), "
				+ "NOT_EQUAL(?pid1, ?pid2).\r\n");

		sb.append("reach(?pid1, ?pid1, ?name) :- young_people(?pid1), person_hasInterest_tag(?pid1, ?tag), tag(?tag, ?name).\r\n");
		sb.append("reach(?pid1, ?pid2, ?tag) :- reach(?pid1,?y, ?tag), conn_comps(?y, ?pid2, ?tag).\r\n");
		*/
		
		/* comp_sizes: sizes of connected components */
		sb.append("comp_sizes(long pid, long tag, int count).\n");
		sb.append("comp_sizes(pid, tag, $inc(1)) :- conn_comps(pid, _, tag);\n");
		sb.append("\t:- young_people(pid), person_hasInterest_tag(pid, tag).");
//		sb.append("comp_sizes_debug(long pid, String tag, int count).\n");
//		sb.append("comp_sizes_debug(pid, tag, $inc(1)) :- conn_comps(pid, _, tid), tag(tid, tag).\n");
		
		/* tag_sizes: tag names ordered by connected component sizes */
		sb.append("tag_sizes(int count, String tagname).\n");
		sb.append("tag_sizes(count, tagname) :- comp_sizes(pid, tag, count), tag(tag, tagname).");

		sb.append("`\n");
		
		/* print result sorted lexicographically by tag name */
//		sb.append("for count,tagname in `tag_sizes(count,tagname)`:\n");
//		sb.append("\tprint count,tagname\n");
//		sb.append("for pid,tag,count in `comp_sizes_debug(pid,tag,count)`:\n");
//		sb.append("\tprint pid,tag,count\n");
		
		sb.append(sortedOutput2(k));

		return sb;
	}

	private static StringBuilder genHopsQuery(int h) {
		StringBuilder sb = new StringBuilder();
		sb.append("all_hops(long pid1, long pid2).\n");
		for(int i = 0; i < h; ++i){
			sb.append("hop"+(i+1)+"(long pid0, long pid"+(i+1)+").\n");
			sb.append("hop"+(i+1)+"(pid0,pid"+(i+1)+") :- ");
			sb.append("all_people(pid0), person_knows_person(pid0, pid1), ");
			for(int j = 1; j < i+1; ++j){
				sb.append("person(pid"+j+"), person_knows_person(pid"+j+", pid"+(j+1)+"), ");
			}
			sb.append("all_people(pid"+(i+1)+"), pid0!=pid"+(i+1)+".\n");
			sb.append("all_hops(pid1,pid2) :- hop"+(i+1)+"(pid1,pid2).\n");
		}
		return sb;
	}

	private static void exportPython(StringBuilder sb, String fileName) 
	{
		try(PrintWriter writer = new PrintWriter(fileName))
		{
			System.out.println(sb);
			writer.println(sb);
		} 
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		System.out.println("Exporting py script");
		StringBuilder sb = new StringBuilder();

		/* Query 1 */
		//		sb.append(generateQueryTables(Util.query1Columns));
		//		sb.append(generateQuery1(576, 400, -1));
		//		sb.append(generateQuery1(58, 402, 0));
		//		sb.append(generateQuery1(266, 106, -1));
		//		sb.append(generateQuery1(313, 523, -1));
		//		sb.append(generateQuery1(858, 587, 1));
		//		sb.append(generateQuery1(155, 355, -1));
		//		sb.append(generateQuery1(947, 771, -1));
		//		sb.append(generateQuery1(105, 608, 3));
		//		sb.append(generateQuery1(128, 751, -1));
		//		sb.append(generateQuery1(814, 641, 0));


		/* Query 2 */
//		sb.append(generateQueryTables(Util.query2Columns, null));
//		sb.append(generateQuery2(3, "1980-02-01")); // Chiang_Kai-shek    Augustine_of_Hippo     Napoleon 
//		sb.append(generateQuery2(4, "1981-03-10")); // Chiang_Kai-shek    Napoleon     Mohandas_Karamchand_Gandhi     Sukarno
//		sb.append(generateQuery2(3, "1982-03-29")); // Chiang_Kai-shek    Mohandas_Karamchand_Gandhi 	  Napoleon
//		sb.append(generateQuery2(3, "1983-05-09")); // Chiang_Kai-shek    Mohandas_Karamchand_Gandhi     Augustine_of_Hippo
//		sb.append(generateQuery2(5, "1984-07-02")); // Chiang_Kai-shek     Aristotle     Mohandas_Karamchand_Gandhi     Augustine_of_Hippo     Fidel_Castro
//		sb.append(generateQuery2(3, "1985-05-31")); // Chiang_Kai-shek     Mohandas_Karamchand_Gandhi    Joseph_Stalin
//		sb.append(generateQuery2(3, "1986-06-14")); // Chiang_Kai-shek     Mohandas_Karamchand_Gandhi    Joseph_Stalin
//		sb.append(generateQuery2(7, "1987-06-24")); // Chiang_Kai-shek     Augustine_of_Hippo     Genghis_Khan     Haile_Selassie_I     Karl_Marx 
//		sb.append(generateQuery2(3, "1988-11-10"));
//		sb.append(generateQuery2(4, "1990-01-25"));


		/* Query 3 */
//				sb.append(generateQueryTables(Util.query3Columns, Util.query3Indices));
				sb.append(generateQueryTables(Util.query3Columns, null));

//				sb.append(generateQuery3(3, 2, "Asia"));
//				sb.append(generateQuery3(4, 3, "Indonesia"));
//				sb.append(generateQuery3(3, 2, "Egypt"));
		//		sb.append(generateQuery3(3, 2, "Italy"));
//				sb.append(generateQuery3(5, 4, "Chengdu"));
		//		sb.append(generateQuery3(3, 2, "Peru"));
		//		sb.append(generateQuery3(3, 2, "Democratic_Republic_of_the_Congo"));
				sb.append(generateQuery3(7, 6, "Ankara"));
		//		sb.append(generateQuery3(3, 2, "Luoyang"));
		//		sb.append(generateQuery3(4, 3, "Taiwan"));


		/* Query 4 */
		//		sb.append(generateQueryTables(Util.query4Columns));
		//		sb.append(generateQuery4(3, "Bill Clinton"));



		exportPython(sb, queryFile);

		System.out.println("Done");

	}

}
