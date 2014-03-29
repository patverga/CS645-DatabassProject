package co.pemma.sigmod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class CreateRelations 
{

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

	public static void parseFile(File file)
	{
		String[] header = null;
		String line;

		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			header = reader.readLine().split("|");
			for (String col : header)
				System.out.print(col);
			System.out.println();
			
			while ((line = reader.readLine()) != null)
			{			
				createTuple(line.split("|"));				
			}
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void createTuple(String[] columns) 
	{
		
	}
}
