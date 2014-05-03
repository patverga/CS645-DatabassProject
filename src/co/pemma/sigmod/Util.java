package co.pemma.sigmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
	
	@SuppressWarnings("serial")
	public final static Map<String,List<String>> query1Columns = new HashMap<String,List<String>>() {{
		put("person_knows_person", new ArrayList<String>() {{add("Person.id");add("Person.id2");}});
		put("comment_hasCreator_person", new ArrayList<String>() {{add("Comment.id");add("Person.id");}});
		put("comment_replyOf_comment", new ArrayList<String>() {{add("Comment.id");add("Comment.id2");}});
	}};
	
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
}
