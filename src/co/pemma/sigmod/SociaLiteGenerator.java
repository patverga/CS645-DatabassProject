package co.pemma.sigmod;

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

public class SociaLiteGenerator 
{	
	public static final String queryFile = "socialite/bin/query.py";


	public static StringBuilder generateQueryTables(Map<String,List<String>> colMap)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("\nprint \"Loading the tables now ...  \"\n");

		Map<String, List<String>> schema = CreateRelations.readSchema();

		sb.append("`");
		for(Entry<String, List<String>> entry : colMap.entrySet() )
		{
			sb.append(generateTable(entry.getKey(), entry.getValue(), schema.get(entry.getKey())));
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
	public static StringBuilder generateTable(String tableName, List<String> colNames, List<String> schema)
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
			sb.append(type + " " + colNames.get(colNames.size()-1).replace(".", "") + ").");


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

			//`Values(a,b) :- l=read("path/to/file.txt"), (v1,v2)=$split(l), a=$toInt(v1), b=$toInt(v2).`

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


		sb.append(sortedOutput(k));

		return sb;
	}

	/**
	 * @param k number of results to return
	 * @return
	 */
	public static StringBuilder sortedOutput(int k)
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
		
//		/* communications: pairs of people who know each other and number of comments made in reply to each other */
//		sb.append("communication_counts(long pid1, long pid2, int count).\n");
//		sb.append("communication_counts(pid1, pid2, $inc(1)) :- communications(pid1, pid2).\n");
//		
//		/* communicators: pairs of people who know each other and have made > numComments comments in reply to each other */
//		sb.append("communicators(long pid1, long pid2).\n");
//		sb.append("communicators(pid1, pid2) :- communication_counts(pid1, pid2, count), count-1 > "+numComments+".\n");
		
//		sb.append("communicators(long pid1, long pid2).\n");
//		sb.append("communicators(pid1, pid2) :- communications(pid1, pid2).");
		
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

		sb.append("def inc(n, by): return n+by\n\n");
		sb.append("`");

		// find all of the people born after the defined date
		sb.append("young_people(long id).\n");
		sb.append("young_people(id) :- person(id, date), (y,m,d)=$split(date, \"-\"), "
				+ "y1=$toInt(y), y2="+year+", y1 >= y2, \n"
				+ "m1=$toInt(m), m2="+month+", m1 >= m2, \n"
				+ "d1=$toInt(d), d2="+day+", d1 >= d2. \n");

		sb.append("conn_comps(long pid, long tag).\n");
		sb.append("conn_comps(pid, tag) :- young_people(pid), person_hasInterest_tag(pid, tag);\n");
		sb.append("\t:- conn_comps(pid2, tag), young_people(pid), person_knows_person(pid2, pid), person_hasInterest_tag(pid, tag).\n");
		
		sb.append("comp_sizes(long tag, int count).\n");
		sb.append("comp_sizes(tag, $inc(1)) :- conn_comps(pid, tag).\n");
		
//		sb.append("sorted_comp_sizes(int count, long tag).\n");
//		sb.append("sorted_comp_sizes(count, tag) :- comp_sizes(tag, count).\n");
		sb.append("sorted_comp_sizes(int count, String tagName).\n");
		sb.append("sorted_comp_sizes(count, tagName) :- comp_sizes(tag, count), tag(tag, tagName).\n");
		sb.append("`\n");
		
		sb.append("for pid,tag in `sorted_comp_sizes(tag,count)`:\n");
		sb.append("\tprint pid,tag\n");
		
//		sb.append("for pid in `young_people(pid)`:\n");
//		sb.append("\tprint pid\n");

		return sb;
	}

	private static StringBuilder genHopsQuery(int h) {
		StringBuilder sb = new StringBuilder();
		sb.append("all_hops(long pid1, long pid2).");
		for(int i = 0; i < h; ++i){
			sb.append("hop"+(i+1)+"(long pid0, long pid"+(i+1)+").");
			sb.append("hop"+(i+1)+"(pid0,pid"+(i+1)+") :- ");
			for(int j = 0; j < i+1; ++j){
				sb.append("person(pid"+j+"), person_knows_person(pid"+j+", pid"+(j+1)+"), ");
			}
			sb.append("person(pid"+(i+1)+").\n");
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
		sb.append(generateQueryTables(Util.query2Columns));
		sb.append(generateQuery2(3, "1980-02-01"));


		/* Query 3 */
		//		sb.append(generateQueryTables(Util.query3Columns));
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


		/* Query 4 */
		//		sb.append(generateQueryTables(Util.query4Columns));
		//		sb.append(generateQuery4(3, "Bill Clinton"));



		exportPython(sb, queryFile);

		System.out.println("Done");

	}

}
