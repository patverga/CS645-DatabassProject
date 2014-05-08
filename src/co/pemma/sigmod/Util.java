package co.pemma.sigmod;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util 
{
	public static String DATA_LOCATION;
	public static int DATA_SIZE;
	public static String RESULT_FILE = "results/time";

	// Query 1
	@SuppressWarnings("serial")
	public final static Map<String,List<String>> query1Columns = new HashMap<String,List<String>>() {{
		put("person_knows_person", new ArrayList<String>() {{add("Person.id");add("Person.id2");}});
		put("comment_hasCreator_person", new ArrayList<String>() {{add("Comment.id");add("Person.id");}});
		put("comment_replyOf_comment", new ArrayList<String>() {{add("Comment.id");add("Comment.id2");}});
	}};


	// Query 2
	@SuppressWarnings("serial")
	public final static Map<String,List<String>> query2Columns = new HashMap<String,List<String>>() {{
		put("person", new ArrayList<String>() {{add("id");add("birthday");}});
		put("tag", new ArrayList<String>() {{add("id");add("name");}});
		put("person_hasInterest_tag", new ArrayList<String>() {{add("Person.id");add("Tag.id");}});
		put("person_knows_person", new ArrayList<String>() {{add("Person.id");add("Person.id2");}});
	}};	
	@SuppressWarnings("serial")
	public final static Map<String,List<String>> query2Indices = new HashMap<String,List<String>>() {{
		put("person", new ArrayList<String>() {{add("id");add("birthday");}});
		put("tag", new ArrayList<String>() {{add("id");add("name");}});
		put("person_hasInterest_tag", new ArrayList<String>() {{add("Person.id");add("Tag.id");}});
		put("person_knows_person", new ArrayList<String>() {{add("Person.id");add("Person.id2");}});
	}};
	@SuppressWarnings("serial")
	public final static Map<String,Boolean> query2TailNested = new HashMap<String,Boolean>() {{
		put("person", false);
		put("tag", false);
		put("person_hasInterest_tag", true);
		put("person_knows_person", true);
	}};

	// Query 3
	@SuppressWarnings("serial")
	public final static Map<String,List<String>> query3Columns = new HashMap<String,List<String>>() {{
		put("place", new ArrayList<String>() {{add("id");add("name");}});
		put("place_isPartOf_place", new ArrayList<String>() {{add("Place.id");add("Place.id2");}});
		put("organisation", new ArrayList<String>() {{add("id");}});
		put("organisation_isLocatedIn_place", new ArrayList<String>() {{add("Organisation.id");add("Place.id");}});
		put("person_isLocatedIn_place", new ArrayList<String>() {{add("Person.id");add("Place.id");}});
		put("person_workAt_organisation", new ArrayList<String>() {{add("Person.id");add("Organisation.id");}});
		put("person_studyAt_organisation", new ArrayList<String>() {{add("Person.id");add("Organisation.id");}});
		put("person_hasInterest_tag", new ArrayList<String>() {{add("Person.id");add("Tag.id");}});
		put("person_knows_person", new ArrayList<String>() {{add("Person.id");add("Person.id2");}});
		put("person", new ArrayList<String>() {{add("id");}});
	}};	
	@SuppressWarnings("serial")
	public final static Map<String,List<String>> query3Indices = new HashMap<String,List<String>>() {{
		put("place", new ArrayList<String>() {{add("id");}});
		put("place_isPartOf_place", new ArrayList<String>() {{add("Place.id");}});
		put("organisation", new ArrayList<String>() {{add("id");}});
		put("organisation_isLocatedIn_place", new ArrayList<String>() {{add("Organisation.id");add("Place.id");}});
		put("person_isLocatedIn_place", new ArrayList<String>() {{add("Place.id");}});
		put("person_workAt_organisation", new ArrayList<String>() {{add("Organisation.id");}});
		put("person_studyAt_organisation", new ArrayList<String>() {{add("Organisation.id");}});
		put("person_hasInterest_tag", new ArrayList<String>() {{add("Person.id");add("Tag.id");}});
		put("person_knows_person", new ArrayList<String>() {{add("Person.id");add("Person.id2");}});
		put("person", new ArrayList<String>() {{add("id");}});
	}};
	@SuppressWarnings("serial")
	public final static Map<String,Boolean> query3TailNested = new HashMap<String,Boolean>() {{
		put("place", false);
		put("place_isPartOf_place", false);//true);
		put("organisation", false);
		put("organisation_isLocatedIn_place", false); // doesn't work on this one?
		put("person_isLocatedIn_place", false);//true);
		put("person_workAt_organisation", false);//true);
		put("person_studyAt_organisation", false);//true);
		put("person_hasInterest_tag", true);//true);
		put("person_knows_person", true);//true);
		put("person", false);
	}};

	// Query 4
	@SuppressWarnings("serial")
	public final static Map<String,List<String>> query4Columns = new HashMap<String,List<String>>() {{
		put("person", new ArrayList<String>() {{add("id");add("birthday");}});
	}};


	public static void printResults(String system,long totalTime, int queryNumber, int dataSize)
	{
		try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(RESULT_FILE, true))))
		{
			writer.println(system + "\t" + totalTime + "\t" + queryNumber + "\t" + dataSize);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void setDataLocation(int dataSize)
	{
		DATA_SIZE = dataSize*1000;
		if (dataSize == 1)
			DATA_LOCATION =  "data";
		else if(dataSize == 10)
			DATA_LOCATION =  "data-10k";
		else
			DATA_LOCATION = null;
	}
}
