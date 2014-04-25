package co.pemma.sigmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {
	@SuppressWarnings("serial")
	public final static Map<String,List<String>> query3Columns = new HashMap<String,List<String>>() {{
		put("place", new ArrayList<String>() {{add("id");add("name");}});
		put("place_isPartOf_place", new ArrayList<String>() {{add("Place.id");add("Place.id0");}});
		put("organsation", new ArrayList<String>() {{add("id");}});
		put("organisation_isLocatedIn_place", new ArrayList<String>() {{add("Organisation.id");add("Place.id");}});
		put("person_isLocatedIn_place", new ArrayList<String>() {{add("Person.id");add("Location.id");}});
		put("person_workAt_organisation", new ArrayList<String>() {{add("Person.id");add("Organisation.id");}});
		put("person_studyAt_organisation", new ArrayList<String>() {{add("Person.id");add("Organisation.id");}});
		put("person_hasInterest_tag", new ArrayList<String>() {{add("Person.id");add("Tag.id");}});
		put("person_knows_person", new ArrayList<String>() {{add("Person.id");add("Person.id0");}});
		put("person", new ArrayList<String>() {{add("id");}});
	}};
}
