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

import co.pemma.sigmod.iris.CreateRelations;
import co.pemma.sigmod.Util;

public class SociaLiteGenerator 
{	
	static char prefix = 'a';
	public static final String queryFile = "socialite/bin/query.py";

	public static StringBuilder generateQueryTables(Map<String,List<String>> colMap, Map<String,List<String>> indexMap, Map<String,Boolean> tailNested)
	{		
		StringBuilder sb = new StringBuilder();
		sb.append("\nprint \"Loading the tables now ...  \"\n");

		Map<String, List<String>> schema = CreateRelations.readSchema();

		sb.append("`");

		List<String> indices;
		for(Entry<String, List<String>> entry : colMap.entrySet() )
		{
			String key = entry.getKey();
			if (indexMap != null && indexMap.containsKey(key))
				indices = indexMap.get(key);
			else
				indices = null;
			sb.append(generateTable(key, entry.getValue(), schema.get(key), indices, tailNested.get(key)));
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
	public static StringBuilder generateTable(String tableName, List<String> colNames, List<String> schema, List<String> indices, boolean tailNested)
	{		
		StringBuilder sb = new StringBuilder();

		// figure out indeces of the columns we want
		List<Integer> colIndeces = new ArrayList<>();		
		Map<Integer, String> schemaIndexNameMap = new HashMap<>();
		Map<String, Integer> schemaNameIndexMap = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader(Util.DATA_LOCATION+"/"+tableName+".csv")))
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
				if(!type.equals("String"))
					type = type.toLowerCase();
				if(type.equals("date"))
					type = "String";
				else if(type.equals("long"))
					type = "int";
				sb.append(type + " " + colNames.get(i).replace(".", "") + (tailNested? ":0..1000, "  : ", "));
			}
			type = schema.get(schemaNameIndexMap.get(colNames.get(colNames.size()-1)));
			if(!type.equals("String"))
				type = type.toLowerCase();
			if(type.equals("date"))
				type = "String";
			else if(type.equals("long"))
				type = "int";
			sb.append((tailNested?"(":"") + type + " " + colNames.get(colNames.size()-1).replace(".", "") + (tailNested?")":"") + ")");

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
			sb.append("l=$read(\"/home/pv/Documents/CS645-DatabassProject/"+Util.DATA_LOCATION+"/commas/"+ tableName + ".csv\"), ");

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

				if (type.equals("Integer") || type.equals("Long"))
					sb.append("$toInt(v" + index+")");
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
		sb.append("start_pairs(int pid1, int pid2).\n");
		sb.append("start_pairs("+pid1+"L, pid2) :- person_knows_person("+pid1+"L, pid2).\n");
		sb.append("start_pairs(pid1, pid2) :- start_pairs(x, pid1), person_knows_person(pid1, pid2).\n");

		/* end_pairs: pairs of people on a path to the end person */
		sb.append("end_pairs(int pid1, int pid2).\n");
		sb.append("end_pairs("+pid2+"L, pid2) :- person_knows_person("+pid2+"L, pid2).\n");
		sb.append("end_pairs(pid1, pid2) :- end_pairs(pid2, x), person_knows_person(pid1, pid2).\n");

		//		sb.append("garbage(int pid1, int pid2).\n");
		//		sb.append("garbage("+pid1+"L, pid2) :- start_pairs(pid1, pid2), end_pairs(pid1, pid2).\n");

		/* pairs: pairs of people on a path from the start to the end person */
		sb.append("pairs(int pid1, int pid2).\n");
		sb.append("pairs(pid1, pid2) :- start_pairs(pid1, pid2), end_pairs(pid1, pid2).\n");
		//		sb.append("pairs("+pid1+"L, pid2) :- person_knows_person("+pid1+"L, pid2).\n");

		/* communications: pairs of people who know each other for each comment made in reply to each other */
		//		sb.append("communications(int pid1, int pid2, int count).\n");
		//		sb.append("communications(pid1, pid2, $inc(1)) :- person_knows_person(pid1, pid2);\n");
		//		sb.append("\t:- pid1 != pid2, comment_hasCreator_person(cid1, pid1), comment_hasCreator_person(cid2, pid2), comment_replyOf_comment(cid1, cid2), person_knows_person(pid1, pid2).\n");
		//		sb.append("communications(int pid1, int pid2, int x).\n");
		//		sb.append("communications(pid1, pid2, x) :- person_knows_person(pid1, pid2), x=1;\n");
		//		sb.append("\t:- pid1 != pid2, x=1, comment_hasCreator_person(cid1, pid1), comment_hasCreator_person(cid2, pid2), comment_replyOf_comment(cid1, cid2), person_knows_person(pid1, pid2).\n");

		if(numComments > -1){
			sb.append("communications(int pid1, int pid2).\n");
			sb.append("communications(pid1, pid2) :- pairs(pid1, pid2), comment_hasCreator_person(cid1, pid1), comment_hasCreator_person(cid2, pid2), comment_replyOf_comment(cid1, cid2).\n");
		}

		sb.append("reach(int pid, int len).\n");
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
		sb.append("\nprint \"\\nRunning query2("+k+", " + d + ")\\n\"\n");

		String[] date = d.split("-");
		String year = date[0];
		String month = date[1];
		String day = date[2];

		String tablePrefix = ""+prefix++;

		int ageCutoff = Integer.parseInt(year)*10000+Integer.parseInt(month)*100+Integer.parseInt(day);

		sb.append("def inc(n, by): return n+by\n\n");
		sb.append("`");

		/* young_people: all of the people born after the defined date */
		sb.append("young_people_"+tablePrefix+"(int id).\n");
		sb.append("young_people_"+tablePrefix+"(id) :- person(id, date), (y,m,d)=$split(date, \"-\"), "
				+ "$toInt(y)*10000+$toInt(m)*100+$toInt(d) >= " + ageCutoff + ".\n");

		/* conn_comps: all pairs with paths and sharing tags */
		sb.append("conn_comps_"+tablePrefix+"(int pid1, int pid2, int tag).\n");
		sb.append("conn_comps_"+tablePrefix+"(pid1, pid2, tag) :- young_people_"+tablePrefix+"(pid1), person_hasInterest_tag(pid1, tag), person_hasInterest_tag(pid2, tag), young_people_"+tablePrefix+"(pid2), person_knows_person(pid1, pid2);\n");
		sb.append("\t:- conn_comps_"+tablePrefix+"(pid1, y, tag), person_knows_person(y, pid2), pid1 != pid2, young_people_"+tablePrefix+"(pid2), person_hasInterest_tag(pid2, tag).\n");

		/* comp_sizes: sizes of connected components */
		sb.append("comp_sizes_"+tablePrefix+"(int pid, int tag, int count).\n");
		sb.append("comp_sizes_"+tablePrefix+"(pid, tag, $inc(1)) :- conn_comps_"+tablePrefix+"(pid, _, tag);\n");
		sb.append("\t:- young_people_"+tablePrefix+"(pid), person_hasInterest_tag(pid, tag).");

		/* tag_sizes: tag names ordered by connected component sizes */
		sb.append("tag_sizes_"+tablePrefix+"(int count, String tagname).\n");
		sb.append("tag_sizes_"+tablePrefix+"(count, tagname) :- comp_sizes_"+tablePrefix+"(pid, tag, count), tag(tag, tagname).");

		sb.append("`\n");

		/* print result sorted lexicographically by tag name */
		//		sb.append("for count,tagname in `tag_sizes(count,tagname)`:\n");
		//		sb.append("\tprint count,tagname\n");
		//		sb.append("for pid,tag,count in `comp_sizes_debug(pid,tag,count)`:\n");
		//		sb.append("\tprint pid,tag,count\n");

		sb.append(sortedOutput2(k, tablePrefix));

		return sb;
	}

	/**
	 * Generate SociaLite code for SIGMOD query 2
	 * 
	 * @param k number of interest tags to return
	 * @param d only consider people born on this date or later
	 * @return StringBuffer representing the generated code
	 */
	public static StringBuilder generateQuery2Better(int k, String d)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\nprint \"\\nRunning query2("+k+", " + d + ")\\n\"\n");

		String[] date = d.split("-");
		String year = date[0];
		String month = date[1];
		String day = date[2];

		String tablePrefix = ""+prefix++;

		int ageCutoff = Integer.parseInt(year)*10000+Integer.parseInt(month)*100+Integer.parseInt(day);

		/* young_people: all of the people born after the defined date */
		sb.append("`young_people_"+tablePrefix+"(int id:0..1000).\n");
		sb.append("young_people_"+tablePrefix+"(id) :- person(id, date), (y,m,d)=$split(date, \"-\"), "
				+ "$toInt(y)*10000+$toInt(m)*100+$toInt(d) >= " + ageCutoff + ".\n");

		sb.append("young_people_interests_"+tablePrefix+"(int pid:0..1000, (int tag)).\n");
		sb.append("young_people_interests_"+tablePrefix+"(pid, tag) :- young_people_"+tablePrefix+"(pid), person_hasInterest_tag(pid, tag).\n");
	
		/* All valid edges, with interest labels */
		sb.append("edge_"+tablePrefix+"(int pid1:0..1000, (int pid2, int tag)).\n");
		sb.append("edge_"+tablePrefix+"(pid1, pid2, tag) :- young_people_interests_"+tablePrefix+"(pid1, tag), person_knows_person(pid1, pid2), young_people_interests_"+tablePrefix+"(pid2, tag).\n");
	
		sb.append("comp_"+tablePrefix+"(int pid:0..1000, (int tag, (int id))).");
		sb.append("comp_"+tablePrefix+"(pid, tag, id) :- young_people_interests_"+tablePrefix+"(pid, tag), id=pid;");
		sb.append("\t:- comp_"+tablePrefix+"(x, tag, id), edge_"+tablePrefix+"(x, pid, tag).");

		sb.append("comp_sum_"+tablePrefix+"(int id, int tag, int size).\n");
		sb.append("comp_sum_"+tablePrefix+"(id, tag, $sum(1)) :- comp_"+tablePrefix+"(id, tag, _).\n");

		sb.append("sorted_comp_size_"+tablePrefix+"(int size, String tagname).\n");
		sb.append("sorted_comp_size_"+tablePrefix+"(size, tagname) :- comp_sum_"+tablePrefix+"(id, tag, size), tag(tag, tagname).\n");
		
		sb.append("`\n");

		/* print result sorted lexicographically by tag name */
//		sb.append("for count, tag in `sorted_comp_size_"+tablePrefix+"(count, tag)`:\n");
//		sb.append("\tprint count, tag\n");

		sb.append(sortedOutput2(k, tablePrefix));

		return sb;
	}

	/**
	 * @param k number of results to return
	 * @return
	 */
	public static StringBuilder sortedOutput2(int k, String tablePrefix)
	{
		StringBuilder sb = new StringBuilder();

		// sort the results in the way we want
		//		sb.append("from operator import itemgetter \n");
//		sb.append("result_set = `sorted_comp_size_"+tablePrefix+"(count,name)`\n");
		sb.append("result_set = `tag_sizes_"+tablePrefix+"(count,name)`\n");
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
	 * Generate SociaLite code for SIGMOD query 3
	 * 
	 * @param k number of person id pairs to return
	 * @param h maximum number of hops between people
	 * @param p place persons must be located in or work in
	 * @return StringBuffer representing the generated code
	 */
	public static StringBuilder generateQuery3(int k, int h, String p)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\nprint \"Running query3("+k+", "+h+", \\\""+ p +"\\\")\"\n");

		/* all_locs: all the locations that we care about (have to get sub-locations) */
		sb.append("def inc(n, by): return n+by\n\n");
		sb.append("`");
		sb.append("all_locs_"+p+"(int locid).\n");
		sb.append("all_locs_"+p+"(locid) :- place(locid, \""+p+"\");\n");
		sb.append("\t:- all_locs_"+p+"(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).\n");

		/* all_orgs: all the organizations that we care about (orgs in all_locs places) */
		sb.append("all_orgs_"+p+"(int orgid).\n");
		sb.append("all_orgs_"+p+"(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_"+p+"(locid).\n");

		/* loc_people: people located in all_locs */
		sb.append("loc_people_"+p+"(int pid).\n");
		sb.append("loc_people_"+p+"(pid) :- person_isLocatedIn_place(pid, locid), all_locs_"+p+"(locid).\n");

		/* org_people: people who work at organizations in all_orgs */
		sb.append("org_people_"+p+"(int pid).\n");
		sb.append("org_people_"+p+"(pid) :- person_workAt_organisation(pid, orgid), all_orgs_"+p+"(orgid);\n");
		sb.append("\t:- person_studyAt_organisation(pid, orgid), all_orgs_"+p+"(orgid).\n");

		/* all_people: people from all_orgs or all_locs */
		sb.append("all_people_"+p+"(int pid).\n");
		sb.append("all_people_"+p+"(pid) :- loc_people_"+p+"(pid);\n");
		sb.append("\t:- org_people_"+p+"(pid).\n");

		/* all_hops: all_people who are h or less hops away from each other */
		sb.append(genHopsQuery(h, p));

		/* common_interests: people with common interests in all_hops */
		//		sb.append("common_interests(int pid1, int pid2, int interest).\n");
		//		sb.append("common_interests(pid1, pid2, interest) :- all_hops(pid1, pid2), pid1 != pid2, all_people(pid1), all_people(pid2), interest=-1L;\n");
		//		sb.append("\t:- all_people(pid1), all_people(pid2), pid1 != pid2, person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).\n");
		//		//sb.append("\t:- common_interests(pid1, pid2, interest), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).\n");

		sb.append("common_interests_"+p+"(int pid1, int pid2, int interest).\n");
		for(int i = 1; i <= h; ++i){
			sb.append("common_interests_"+p+"(pid1, pid2, interest) :- hop"+i+"_"+p+"(pid1, pid2), interest=-1;\n");
			sb.append("\t:- hop"+i+"_"+p+"(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).\n");
		}

		/* interest_counts: counts of interests for each pair */
		sb.append("interest_counts_"+p+"(int pid1, int pid2, int count).\n");
		sb.append("interest_counts_"+p+"(pid1, pid2, $inc(1)) :- common_interests_"+p+"(pid1, pid2, interest).\n");

		sb.append("sorted_counts_"+p+"(int count, int pid1, int pid2).\n");
		sb.append("sorted_counts_"+p+"(count, pid1, pid2) :- interest_counts_"+p+"(pid1, pid2, c), count=c-1.\n");
		sb.append("`\n");

		sb.append(sortedOutput3(k, p));

		return sb;
	}

	private static StringBuilder genHopsQuery(int h, String p) {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < h; ++i){
			sb.append("hop"+(i+1)+"_"+p+"(int pid0, int pid"+(i+1)+").\n");
			if(i == 0){
				sb.append("temp1_"+p+"(int pid0, int pid1).\n");
				sb.append("temp1_"+p+"(pid0,pid1) :- all_people_"+p+"(pid0), person_knows_person(pid0, pid1).\n");
				sb.append("hop"+(i+1)+"_"+p+"(pid0,pid"+(i+1)+") :- ");
				sb.append("temp1_"+p+"(pid0,pid1), all_people_"+p+"(pid"+(i+1)+"), pid0!=pid"+(i+1)+".\n");
			}
			else{
				sb.append("temp"+(i+1)+"_"+p+"(int pid0, int pid1).\n");
				sb.append("temp"+(i+1)+"_"+p+"(pid0, pid2) :- temp"+i+"_"+p+"(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).\n");
				sb.append("hop"+(i+1)+"_"+p+"(pid0,pid1) :- temp"+(i+1)+"_"+p+"(pid0,pid1), all_people_"+p+"(pid1), pid0!=pid1, not hop"+i+"_"+p+"(pid0,pid1).\n");
			}
		}
		return sb;
	}	

	/**
	 * @param k number of results to return
	 * @return
	 */
	public static StringBuilder sortedOutput3(int k, String p)
	{
		StringBuilder sb = new StringBuilder();

		// sort the results in the way we want
		//		sb.append("from operator import itemgetter \n");
		sb.append("result_set = `sorted_counts_"+p+"(count,pid1,pid2)`\n");
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
		int dataSize = 1;
		Util.setDataLocation(dataSize);
		
		System.out.println("Exporting py script");
		StringBuilder sb = new StringBuilder();

		/* Query 1 */
		//		sb.append(generateQueryTables(Util.query1Columns));
		//		sb.append(generateQuery1(576, 400, -1) + "\n");
		//		sb.append(generateQuery1(58, 402, 0) + "\n");
		//		sb.append(generateQuery1(266, 106, -1) + "\n");
		//		sb.append(generateQuery1(313, 523, -1) + "\n");
		//		sb.append(generateQuery1(858, 587, 1) + "\n");
		//		sb.append(generateQuery1(155, 355, -1) + "\n");
		//		sb.append(generateQuery1(947, 771, -1) + "\n");
		//		sb.append(generateQuery1(105, 608, 3) + "\n");
		//		sb.append(generateQuery1(128, 751, -1) + "\n");
		//		sb.append(generateQuery1(814, 641, 0) + "\n");


		/* Query 2 */
//		sb.append(generateQueryTables(Util.query2Columns, null, Util.query2TailNested));
//		sb.append(generateQuery2(3, "1980-02-01") + "\n"); // Chiang_Kai-shek    Augustine_of_Hippo     Napoleon 
//		sb.append(generateQuery2(4, "1981-03-10") + "\n"); // Chiang_Kai-shek    Napoleon     Mohandas_Karamchand_Gandhi     Sukarno
//		sb.append(generateQuery2(3, "1982-03-29") + "\n"); // Chiang_Kai-shek    Mohandas_Karamchand_Gandhi 	  Napoleon
//		sb.append(generateQuery2(3, "1983-05-09") + "\n"); // Chiang_Kai-shek    Mohandas_Karamchand_Gandhi     Augustine_of_Hippo
//		sb.append(generateQuery2(5, "1984-07-02") + "\n"); // Chiang_Kai-shek     Aristotle     Mohandas_Karamchand_Gandhi     Augustine_of_Hippo     Fidel_Castro
//		sb.append(generateQuery2(3, "1985-05-31") + "\n"); // Chiang_Kai-shek     Mohandas_Karamchand_Gandhi    Joseph_Stalin
//		sb.append(generateQuery2(3, "1986-06-14") + "\n"); // Chiang_Kai-shek     Mohandas_Karamchand_Gandhi    Joseph_Stalin
//		sb.append(generateQuery2(7, "1987-06-24") + "\n"); // Chiang_Kai-shek     Augustine_of_Hippo     Genghis_Khan     Haile_Selassie_I     Karl_Marx 
//		sb.append(generateQuery2(3, "1988-11-10") + "\n");
//		sb.append(generateQuery2(4, "1990-01-25") + "\n");

		/* Query 3 */
		//		sb.append(generateQueryTables(Util.query3Columns, Util.query3Indices, tailNested));
		sb.append(generateQueryTables(Util.query3Columns, null, Util.query3TailNested));
		//		sb.append(generateQuery3(3, 2, "Asia"));
		//		sb.append(generateQuery3(4, 3, "Indonesia"));
//		sb.append(generateQuery3(3, 2, "Egypt"));
		//		sb.append(generateQuery3(3, 2, "Italy"));
		//		sb.append(generateQuery3(5, 4, "Chengdu"));
		//		sb.append(generateQuery3(3, 2, "Peru"));
		//		sb.append(generateQuery3(3, 2, "Democratic_Republic_of_the_Congo"));
		//		sb.append(generateQuery3(7, 6, "Ankara"));
		//		sb.append(generateQuery3(3, 2, "Luoyang"));
		//		sb.append(generateQuery3(4, 3, "Taiwan"));

		/* Query 3 - 10k */
		sb.append(generateQuery3(3, 2, "Asia")); //				 230|1814 1814|1857 1814|2219 % common interest counts 5 5 5
		sb.append(generateQuery3(4, 3, "Dolgoprudny")); //		 8132|8195 8084|8132 8084|8161 8084|8185 % common interest counts 1 0 0 0
		sb.append(generateQuery3(3, 2, "Yongkang_District")); // 7953|7981 7953|7987 7953|7989 % common interest counts 1 1 1



		/* Query 4 */
		//		sb.append(generateQueryTables(Util.query4Columns));
		//		sb.append(generateQuery4(3, "Bill Clinton"));


		//		sb.append(test());

		exportPython(sb, queryFile);

		System.out.println("Done");

	}

	public static StringBuilder test(){
		StringBuilder sb = new StringBuilder();

		// load person_knows_person table
		//		sb.append("`person_knows_person(int Personid:0..1000, (int Personid2)).\n");
		sb.append("`person_knows_person(int Personid, (int Personid2)).\n");
		sb.append("person_knows_person(Personid, Personid2) :- l=$read(\"/home/pv/Documents/CS645-DatabassProject/data/commas/person_knows_person.csv\"), (v0,v1)=$split(l,\",\"), Personid=$toInt(v0), Personid2=$toInt(v1).\n\n");

		// compute shortest paths from pid 576
		sb.append("path(int end:0..1000, int len).\n");
		sb.append("path(t, $min(d)) :- person_knows_person(858, t), d=1;\n");
		sb.append("\t:- path(s, len), person_knows_person(s, t), d=len+1.\n");
		sb.append("`\n");

		// print result
		sb.append("for pid,len in `path(pid,len)`:\n");
		sb.append("\tprint pid,len\n");

		return sb;
	}

}
