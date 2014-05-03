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
	public static final String query3File = "socialite/bin/query3.py";


	public static StringBuilder generateQuery3Tables()
	{
		StringBuilder sb = new StringBuilder();
		Map<String, List<String>> schema = CreateRelations.readSchema();

		sb.append("`");
		for(Entry<String, List<String>> entry : Util.query3Columns.entrySet() )
		{
			sb.append(generateTable2(entry.getKey(), entry.getValue(), schema.get(entry.getKey())));
		}
		sb.append("`");
		System.out.println(sb);

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
			String col;
			String[] tuple = line.split("\\|");

			// map col names to their indices and back
			for(int i = 0; i < tuple.length; i++)
			{
				col = tuple[i];
				if (schemaNameIndexMap.containsKey(col))
					col += "2";
				schemaIndexNameMap.put(i, col);
				schemaNameIndexMap.put(col, i);
				if (colNames.contains(col))
					colIndeces.add(i);
			}

			// define the table
			sb.append(tableName+"(");
			for (int i = 0; i < colNames.size()-1; i++)
				sb.append(schema.get(schemaNameIndexMap.get(colNames.get(i))) + " " + colNames.get(i) + ", ");
			sb.append(schema.get(schemaNameIndexMap.get(colNames.get(colNames.size()-1))) + " " + colNames.get(colNames.size()-1) + ").");

			// read in and set the tuples
			while ((line = reader.readLine()) != null)
			{	
				sb.append("\n");
				sb.append(tableName+"(");
				for (int i = 0; i < colNames.size()-1; i++)
					sb.append(colNames.get(i)+", ");
				sb.append(colNames.get(colNames.size()-1)+")");
				sb.append(" :- ");
				tuple = line.split("\\|");
				int colIndex; 
				for (int i = 0; i < colIndeces.size()-1; i++)
				{
					colIndex = colIndeces.get(i);
					sb.append(schemaIndexNameMap.get(colIndex) +"=\""+tuple[colIndex]+"\", ");
				}				
				colIndex = colIndeces.get(colIndeces.size()-1);
				sb.append(schemaIndexNameMap.get(colIndex) +"=\""+tuple[colIndex]+"\".");
			}
			// start and end with a backtick
			//			sb.append("` \n");
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb;
	}

	/**
	 * Generate SociaLite code to load data into tables
	 * 
	 * @param tableName name of the table to generage SocialLite for
	 * @param colNames names of columns that we want to load
	 * @param schema SIGMOD db schema
	 * @return StringBuffer representing generated code
	 */
	public static StringBuilder generateTable2(String tableName, List<String> colNames, List<String> schema)
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
		sb.append("common_interests(pid1, pid2, interest) :- all_hops(pid1, pid2), pid1 != pid2, all_people(pid1), all_people(pid2), interest=$toLong(\"-1\");\n");
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
	 * 
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
		sb.append("set = set() \n");
		sb.append("results = 0 \n");
		sb.append("for count,pid1,pid2 in result_set:\n");
		sb.append("\tif results >= "+k+":\n");
		sb.append("\t\tbreak \n");
		sb.append("\tif pid1 > pid2:\n");
		sb.append("\t\tid = str(pid2) + '-' + str(pid1)\n");
		sb.append("\telse:\n");
		sb.append("\t\tid = str(pid1) + '-' + str(pid2)\n");
		
		// if this is a new element print it, add to set
		sb.append("\tif id not in set: \n");
		sb.append("\t\tprint pid1,pid2,count\n");
		sb.append("\t\tset.add(id) \n");
		sb.append("\t\tresults += 1 \n");
		
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

	private static void exportPython(StringBuilder sb) 
	{
		try(PrintWriter writer = new PrintWriter(query3File))
		{
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
		sb.append("\nprint \"Loading the tables now ...  \"\n");
		sb.append(generateQuery3Tables());

		sb.append("\nprint \"Done loading tables\"\n");
		sb.append(generateQuery3(3, 2, "Asia"));
		sb.append(generateQuery3(4, 3, "Indonesia"));
		sb.append(generateQuery3(3, 2, "Egypt"));
		sb.append(generateQuery3(3, 2, "Italy"));
		sb.append(generateQuery3(5, 4, "Chengdu"));
		sb.append(generateQuery3(3, 2, "Peru"));
		sb.append(generateQuery3(3, 2, "Democratic_Republic_of_the_Congo"));
		sb.append(generateQuery3(7, 6, "Ankara"));
		sb.append(generateQuery3(3, 2, "Luoyang"));
		sb.append(generateQuery3(4, 3, "Taiwan"));
		
		exportPython(sb);

		System.out.println("Done");

	}
}
