package co.pemma.sigmod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.deri.iris.api.basics.ITuple;
import org.deri.iris.api.factory.IConcreteFactory;
import org.deri.iris.api.terms.ITerm;
import org.deri.iris.api.terms.IVariable;
import org.deri.iris.basics.Tuple;
import org.deri.iris.terms.TermFactory;
import org.deri.iris.terms.concrete.ConcreteFactory;

public class CreateRelations 
{
	private static Map<String,List<String>> schema = readSchema();
	
	public static Map<String, List<String>> readSchema() 
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
						columns.add(type);
					}
					schema.put(table, columns);
				}
				// table name
				else if (!line.equals("\\s+"))
				{
					table = line.replaceAll(".csv$", "");
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
	
	public static void readData(String directoryString)
	{
		final File directory = new File(directoryString);

		for (final File file : directory.listFiles()) 
		{
			if (!file.isDirectory())
			{
				parseFile(file); 
			}
		}
	}

	public static List<ITuple> parseFile(File file)
	{
		String[] header = null;
		String line;
		List<ITuple> tuples = new ArrayList<>(); 

		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			header = reader.readLine().split("\\|");
			for (String col : header)
				System.out.print(col);
			System.out.println();
			
			while ((line = reader.readLine()) != null)
			{			
				//createTuple(line.split("\\|"));				
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tuples;
	}
	
	public static List<ITuple> getTuples(String predicateName, ITuple from, ITuple to)
	{
		String[] header = null;
		String line;
		List<ITuple> tuples = new ArrayList<>();
				
		try (BufferedReader reader = new BufferedReader(new FileReader(new File("data/"+predicateName+".csv"))))
		{
			reader.readLine();
			while ((line = reader.readLine()) != null)
			{		
				ITuple newTuple = createTuple(predicateName, line.split("\\|"));
				if(newTuple.compareTo(from) >= 0 && newTuple.compareTo(to) <= 0)
					tuples.add(newTuple);
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tuples;
	}

	private static ITuple createTuple(String predicateName, String[] columns) 
	{
		IConcreteFactory termFactory = ConcreteFactory.getInstance();
		List<String> types = schema.get(predicateName);
		List<ITerm> terms = new ArrayList<>();
				
		for(int i = 0; i < columns.length; i++){
			switch(types.get(i)){
			case "String":
				terms.add(termFactory.createNormalizedString(columns[i]));
				break;
				
			case "DateTime":
				// 2012-10-15T08:43:48Z
				// int year, int month, int day, int hour,
				// int minute, double second, int tzHour, int tzMinute
				SimpleDateFormat dateFormatter = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss'Z'");
				Date date;
				try {
					date = dateFormatter.parse(columns[i]);
					terms.add(termFactory.createDateTime(date.getYear(), date.getMonth(), 
							date.getDay(), date.getHours(), date.getMinutes(), 
							date.getSeconds(), 0, 0));
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				break;
				
			case "Integer":
				terms.add(termFactory.createInteger(Integer.parseInt(columns[i])));
				break;
				
			case "Long":
				terms.add(termFactory.createLong(Long.parseLong(columns[i])));
				break;
			}
		}
		return new Tuple(terms);
	}
}
