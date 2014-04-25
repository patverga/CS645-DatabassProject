package co.pemma.sigmod;

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
	public StringBuffer generateTables(String tableName, List<String> colNames, Map<String,List<String>> schema){
		StringBuffer sb = new StringBuffer();
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
		sb.append("`all_locs(Long locid).`");
		sb.append("`all_locs(locid) :- place(locid, '"+p+"').`");
		sb.append("`all_locs(locid) :- all_locs(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).`");
		
		/* all_orgs: all the organizations that we care about (orgs in all_locs places) */
		sb.append("`all_orgs(Long orgid).`");
		sb.append("`all_orgs(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs(locid).`");
		
		/* loc_people: people located in all_locs */
		sb.append("`loc_people(Long pid).`");
		sb.append("`loc_people(pid) :- person_isLocatedIn_place(pid, locid), all_locs(locid).`");
		
		/* org_people: people who work at organizations in all_orgs */
		sb.append("`org_people(Long pid).`");
		sb.append("`org_people(pid) :- person_workAt_organisation(pid, orgid), all_orgs(orgid).`");
		sb.append("`org_people(pid) :- person_studyAt_organisation(pid, orgid), all_orgs(orgid).`");
		
		/* all_people: people from all_orgs or all_locs */
		sb.append("`all_people(Long pid).`");
		sb.append("`loc_people(pid).`");
		sb.append("`org_people(pid).`");
		
		/* all_hops: all_people who are h or less hops away from each other */
		sb.append(genHopsQuery(h));
		
		/* common_interests: people with common interests in all_hops */
		sb.append("`common_interests(pid1,pid2,'__null') :- all_hops(pid1, pid2), all_people(pid1), all_people(pid2).`");
		sb.append("`common_interests(pid1,pid2,interest) :- common_interests(pid1, pid2, '__null'), person_hasInterest_tag(pid1,interest), person_hasInterest_tag(pid2,interest).`");
		
		/* interest_counts: counts of interests for each pair */
		
		return sb;
	}

	private StringBuffer genHopsQuery(int h) {
		StringBuffer sb = new StringBuffer();
		
		
		return sb;
	}
}
