package co.pemma.sigmod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SociaLiteGenerator {

	/**
	 * Generate SociaLite code to load data into tables
	 * 
	 * @param tableName name of the table to generage SocialLite for
	 * @param colNames names of columns that we want to load
	 * @param schema SIGMOD db schema
	 * @return StringBuffer representing generated code
	 */
	public static StringBuffer generateTables(String tableName, List<String> colNames, List<String> schema)
	{
		StringBuffer sb = new StringBuffer();
		// figure out indeces of the columns we want
		List<Integer> colIndeces = new ArrayList<>();		
		Map<Integer, String> schemaIndexNameMap = new HashMap<>();
		Map<String, Integer> schemaNameIndexMap = new HashMap<>();
		try (BufferedReader reader = new BufferedReader(new FileReader("data/"+tableName+".csv")))
		{
			// start and end with a backtick
			sb.append("`");

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
			sb.append("` \n");
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(sb);
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
	public StringBuffer generateQuery3(int k, int h, String p){
		StringBuffer sb = new StringBuffer();
		
		/* all_locs: all the locations that we care about (have to get sub-locations) */
		sb.append("`\n");
		sb.append("all_locs(Long locid).\n");
		sb.append("all_locs(locid) :- place(locid, '"+p+"').\n");
		sb.append("all_locs(locid) :- all_locs(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).\n");
		
		/* all_orgs: all the organizations that we care about (orgs in all_locs places) */
		sb.append("all_orgs(Long orgid).\n");
		sb.append("all_orgs(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs(locid).\n");
		
		/* loc_people: people located in all_locs */
		sb.append("loc_people(Long pid).\n");
		sb.append("loc_people(pid) :- person_isLocatedIn_place(pid, locid), all_locs(locid).\n");
		
		/* org_people: people who work at organizations in all_orgs */
		sb.append("org_people(Long pid).\n");
		sb.append("org_people(pid) :- person_workAt_organisation(pid, orgid), all_orgs(orgid).\n");
		sb.append("org_people(pid) :- person_studyAt_organisation(pid, orgid), all_orgs(orgid).\n");
		
		/* all_people: people from all_orgs or all_locs */
		sb.append("all_people(Long pid).\n");
		sb.append("loc_people(pid).\n");
		sb.append("org_people(pid).\n");
		
		/* all_hops: all_people who are h or less hops away from each other */
		sb.append(genHopsQuery(h));
		
		/* common_interests: people with common interests in all_hops */
		sb.append("common_interests(Long pid1, Long pid2, String interest).");
		sb.append("common_interests(pid1,pid2,'__null') :- all_hops(pid1, pid2), all_people(pid1), all_people(pid2).\n");
		sb.append("common_interests(pid1,pid2,interest) :- common_interests(pid1, pid2, '__null'), person_hasInterest_tag(pid1,interest), person_hasInterest_tag(pid2,interest).\n");
		
		/* interest_counts: counts of interests for each pair */
		sb.append("interest_counts(Long pid1, Long pid2, int count).");
		sb.append("interest_counts(Long pid1,pid2,)");
		sb.append("");
		
		sb.append("`\n");
		return sb;
	}

	private StringBuffer genHopsQuery(int h) {
		StringBuffer sb = new StringBuffer();
		sb.append("all_hops(Long pid1, Long pid2).");
		for(int i = 0; i < h; ++i){
			sb.append("hop"+(i+1)+"(pid0,pid"+(i+1)+") :- ");
			for(int j = 0; j < i+1; ++j){
				sb.append("person(pid"+j+"), person_knows_person(pid"+j+", pid"+(j+1)+"), ");
			}
			sb.append("person(pid"+(i+1)+").\n");
			sb.append("all_hops(pid1,pid2) :- hop"+(i+1)+"(pid1,pid2).\n");
		}
		return sb;
	}

	public static void main(String[] args)
	{
		Map<String, List<String>> schema = CreateRelations.readSchema();
		String table = "post";
		List<String> cols = new ArrayList<>();
		cols.add("browserUsed");
		cols.add("locationIP");
		SociaLiteGenerator.generateTables(table, cols, schema.get(table));
	}
}
