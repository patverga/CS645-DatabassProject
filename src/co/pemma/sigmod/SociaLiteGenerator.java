package co.pemma.sigmod;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
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
			String[] tuple = line.split("\\|");

			// map col names to their indices and back
			for(int i = 0; i < tuple.length; i++)
			{
				schemaIndexNameMap.put(i, tuple[i]);
				schemaNameIndexMap.put(tuple[i], i);
				if (colNames.contains(tuple[i]))
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
	 * @return StringBuffer representing the generated code
	 */
	public static StringBuffer generateQuery3(){
		StringBuffer sb = new StringBuffer();
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
