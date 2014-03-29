package co.pemma.sigmod;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WinSigmodRunner 
{
	public static void main(String[] args)
	{
		Map<String, List<String>> schema = readSchema();
		CreateRelations.readData("data/outputDir-1k");
	}

	private static Map<String, List<String>> readSchema() 
	{
		Map<String, List<String>> schema = new HashMap<String, List<String>>();
		String line;
		try (BufferedReader reader = new BufferedReader(new FileReader("data/schema")))
		{
			String table = null, type;
			List<String> columns = null;
			while ((line = reader.readLine()) != null)
			{			
				// column names / types
				if (line.startsWith(" "))
				{
					for (String column : line.split("\\|"))
					{
						type = column.substring(0, column.indexOf(")"));
						type = type.substring(type.indexOf("(")+1, type.length());
						System.out.print(type + " ");
						columns.add(type);
					}
					System.out.println();
					schema.put(table, columns);
				}
				// table name
				else if (!line.equals("\\s+"))
				{
					table = line;
					columns = new ArrayList<String>();
				}
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return schema;			

	}
}
