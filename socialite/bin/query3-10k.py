
print "Loading the tables now ...  "
`person(int id).
person(id) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data-10k/commas/person.csv"), (v0,v1,v2,v3,v4,v5,v6,v7)=$split(l,","), id=$toInt(v0).
person_isLocatedIn_place(int Personid, int Placeid).
person_isLocatedIn_place(Personid, Placeid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data-10k/commas/person_isLocatedIn_place.csv"), (v0,v1)=$split(l,","), Personid=$toInt(v0), Placeid=$toInt(v1).
person_workAt_organisation(int Personid, int Organisationid).
person_workAt_organisation(Personid, Organisationid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data-10k/commas/person_workAt_organisation.csv"), (v0,v1,v2)=$split(l,","), Personid=$toInt(v0), Organisationid=$toInt(v1).
organisation(int id).
organisation(id) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data-10k/commas/organisation.csv"), (v0,v1,v2,v3)=$split(l,","), id=$toInt(v0).
place_isPartOf_place(int Placeid, int Placeid2).
place_isPartOf_place(Placeid, Placeid2) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data-10k/commas/place_isPartOf_place.csv"), (v0,v1)=$split(l,","), Placeid=$toInt(v0), Placeid2=$toInt(v1).
person_knows_person(int Personid, int Personid2).
person_knows_person(Personid, Personid2) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data-10k/commas/person_knows_person.csv"), (v0,v1)=$split(l,","), Personid=$toInt(v0), Personid2=$toInt(v1).
person_hasInterest_tag(int Personid, int Tagid).
person_hasInterest_tag(Personid, Tagid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data-10k/commas/person_hasInterest_tag.csv"), (v0,v1)=$split(l,","), Personid=$toInt(v0), Tagid=$toInt(v1).
person_studyAt_organisation(int Personid, int Organisationid).
person_studyAt_organisation(Personid, Organisationid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data-10k/commas/person_studyAt_organisation.csv"), (v0,v1,v2)=$split(l,","), Personid=$toInt(v0), Organisationid=$toInt(v1).
place(int id, String name).
place(id, name) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data-10k/commas/place.csv"), (v0,v1,v2,v3)=$split(l,","), id=$toInt(v0), name=v1.
organisation_isLocatedIn_place(int Organisationid, int Placeid).
organisation_isLocatedIn_place(Organisationid, Placeid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data-10k/commas/organisation_isLocatedIn_place.csv"), (v0,v1)=$split(l,","), Organisationid=$toInt(v0), Placeid=$toInt(v1).
`
print "Done loading tables "

print "Running query3(3, 2, \"Asia\")"
def inc(n, by): return n+by

`all_locs_Asia(int locid).
all_locs_Asia(locid) :- place(locid, "Asia");
	:- all_locs_Asia(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Asia(int orgid).
all_orgs_Asia(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Asia(locid).
loc_people_Asia(int pid).
loc_people_Asia(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Asia(locid).
org_people_Asia(int pid).
org_people_Asia(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Asia(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Asia(orgid).
all_people_Asia(int pid).
all_people_Asia(pid) :- loc_people_Asia(pid);
	:- org_people_Asia(pid).
hop1_Asia(int pid0, int pid1).
temp1_Asia(int pid0, int pid1).
temp1_Asia(pid0,pid1) :- all_people_Asia(pid0), person_knows_person(pid0, pid1).
hop1_Asia(pid0,pid1) :- temp1_Asia(pid0,pid1), all_people_Asia(pid1), pid0!=pid1.
hop2_Asia(int pid0, int pid2).
temp2_Asia(int pid0, int pid1).
temp2_Asia(pid0, pid2) :- temp1_Asia(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Asia(pid0,pid1) :- temp2_Asia(pid0,pid1), all_people_Asia(pid1), pid0!=pid1, not hop1_Asia(pid0,pid1).
common_interests_Asia(int pid1, int pid2, int interest).
common_interests_Asia(pid1, pid2, interest) :- hop1_Asia(pid1, pid2), interest=-1;
	:- hop1_Asia(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Asia(pid1, pid2, interest) :- hop2_Asia(pid1, pid2), interest=-1;
	:- hop2_Asia(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Asia(int pid1, int pid2, int count).
interest_counts_Asia(pid1, pid2, $inc(1)) :- common_interests_Asia(pid1, pid2, interest).
sorted_counts_Asia(int count, int pid1, int pid2).
sorted_counts_Asia(count, pid1, pid2) :- interest_counts_Asia(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Asia(count,pid1,pid2)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1],x[2]))
used = set() 
results = 0 
for count,pid1,pid2 in result_set:
	if results >= 3:
		break 
	if pid1 > pid2:
		id = str(pid2) + '-' + str(pid1)
	else:
		id = str(pid1) + '-' + str(pid2)
	if id not in used: 
		print pid1,pid2,count
		used.add(id) 
		results += 1 

print "Running query3(4, 3, \"Dolgoprudny\")"
def inc(n, by): return n+by

`all_locs_Dolgoprudny(int locid).
all_locs_Dolgoprudny(locid) :- place(locid, "Dolgoprudny");
	:- all_locs_Dolgoprudny(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Dolgoprudny(int orgid).
all_orgs_Dolgoprudny(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Dolgoprudny(locid).
loc_people_Dolgoprudny(int pid).
loc_people_Dolgoprudny(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Dolgoprudny(locid).
org_people_Dolgoprudny(int pid).
org_people_Dolgoprudny(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Dolgoprudny(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Dolgoprudny(orgid).
all_people_Dolgoprudny(int pid).
all_people_Dolgoprudny(pid) :- loc_people_Dolgoprudny(pid);
	:- org_people_Dolgoprudny(pid).
hop1_Dolgoprudny(int pid0, int pid1).
temp1_Dolgoprudny(int pid0, int pid1).
temp1_Dolgoprudny(pid0,pid1) :- all_people_Dolgoprudny(pid0), person_knows_person(pid0, pid1).
hop1_Dolgoprudny(pid0,pid1) :- temp1_Dolgoprudny(pid0,pid1), all_people_Dolgoprudny(pid1), pid0!=pid1.
hop2_Dolgoprudny(int pid0, int pid2).
temp2_Dolgoprudny(int pid0, int pid1).
temp2_Dolgoprudny(pid0, pid2) :- temp1_Dolgoprudny(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Dolgoprudny(pid0,pid1) :- temp2_Dolgoprudny(pid0,pid1), all_people_Dolgoprudny(pid1), pid0!=pid1, not hop1_Dolgoprudny(pid0,pid1).
hop3_Dolgoprudny(int pid0, int pid3).
temp3_Dolgoprudny(int pid0, int pid1).
temp3_Dolgoprudny(pid0, pid2) :- temp2_Dolgoprudny(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop3_Dolgoprudny(pid0,pid1) :- temp3_Dolgoprudny(pid0,pid1), all_people_Dolgoprudny(pid1), pid0!=pid1, not hop2_Dolgoprudny(pid0,pid1).
common_interests_Dolgoprudny(int pid1, int pid2, int interest).
common_interests_Dolgoprudny(pid1, pid2, interest) :- hop1_Dolgoprudny(pid1, pid2), interest=-1;
	:- hop1_Dolgoprudny(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Dolgoprudny(pid1, pid2, interest) :- hop2_Dolgoprudny(pid1, pid2), interest=-1;
	:- hop2_Dolgoprudny(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Dolgoprudny(pid1, pid2, interest) :- hop3_Dolgoprudny(pid1, pid2), interest=-1;
	:- hop3_Dolgoprudny(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Dolgoprudny(int pid1, int pid2, int count).
interest_counts_Dolgoprudny(pid1, pid2, $inc(1)) :- common_interests_Dolgoprudny(pid1, pid2, interest).
sorted_counts_Dolgoprudny(int count, int pid1, int pid2).
sorted_counts_Dolgoprudny(count, pid1, pid2) :- interest_counts_Dolgoprudny(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Dolgoprudny(count,pid1,pid2)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1],x[2]))
used = set() 
results = 0 
for count,pid1,pid2 in result_set:
	if results >= 4:
		break 
	if pid1 > pid2:
		id = str(pid2) + '-' + str(pid1)
	else:
		id = str(pid1) + '-' + str(pid2)
	if id not in used: 
		print pid1,pid2,count
		used.add(id) 
		results += 1 

print "Running query3(3, 2, \"Yongkang_District\")"
def inc(n, by): return n+by

`all_locs_Yongkang_District(int locid).
all_locs_Yongkang_District(locid) :- place(locid, "Yongkang_District");
	:- all_locs_Yongkang_District(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Yongkang_District(int orgid).
all_orgs_Yongkang_District(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Yongkang_District(locid).
loc_people_Yongkang_District(int pid).
loc_people_Yongkang_District(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Yongkang_District(locid).
org_people_Yongkang_District(int pid).
org_people_Yongkang_District(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Yongkang_District(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Yongkang_District(orgid).
all_people_Yongkang_District(int pid).
all_people_Yongkang_District(pid) :- loc_people_Yongkang_District(pid);
	:- org_people_Yongkang_District(pid).
hop1_Yongkang_District(int pid0, int pid1).
temp1_Yongkang_District(int pid0, int pid1).
temp1_Yongkang_District(pid0,pid1) :- all_people_Yongkang_District(pid0), person_knows_person(pid0, pid1).
hop1_Yongkang_District(pid0,pid1) :- temp1_Yongkang_District(pid0,pid1), all_people_Yongkang_District(pid1), pid0!=pid1.
hop2_Yongkang_District(int pid0, int pid2).
temp2_Yongkang_District(int pid0, int pid1).
temp2_Yongkang_District(pid0, pid2) :- temp1_Yongkang_District(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Yongkang_District(pid0,pid1) :- temp2_Yongkang_District(pid0,pid1), all_people_Yongkang_District(pid1), pid0!=pid1, not hop1_Yongkang_District(pid0,pid1).
common_interests_Yongkang_District(int pid1, int pid2, int interest).
common_interests_Yongkang_District(pid1, pid2, interest) :- hop1_Yongkang_District(pid1, pid2), interest=-1;
	:- hop1_Yongkang_District(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Yongkang_District(pid1, pid2, interest) :- hop2_Yongkang_District(pid1, pid2), interest=-1;
	:- hop2_Yongkang_District(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Yongkang_District(int pid1, int pid2, int count).
interest_counts_Yongkang_District(pid1, pid2, $inc(1)) :- common_interests_Yongkang_District(pid1, pid2, interest).
sorted_counts_Yongkang_District(int count, int pid1, int pid2).
sorted_counts_Yongkang_District(count, pid1, pid2) :- interest_counts_Yongkang_District(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Yongkang_District(count,pid1,pid2)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1],x[2]))
used = set() 
results = 0 
for count,pid1,pid2 in result_set:
	if results >= 3:
		break 
	if pid1 > pid2:
		id = str(pid2) + '-' + str(pid1)
	else:
		id = str(pid1) + '-' + str(pid2)
	if id not in used: 
		print pid1,pid2,count
		used.add(id) 
		results += 1 

