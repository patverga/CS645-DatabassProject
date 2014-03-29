package co.pemma.sigmod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class ReadData 
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
		String header = "";
		try (BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			if(header == null)
		} 
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
