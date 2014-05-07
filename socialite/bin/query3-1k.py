
print "Loading the tables now ...  "
`person(int id).
person(id) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/person.csv"), (v0,v1,v2,v3,v4,v5,v6,v7)=$split(l,","), id=$toInt(v0).
person_isLocatedIn_place(int Personid, int Placeid).
person_isLocatedIn_place(Personid, Placeid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/person_isLocatedIn_place.csv"), (v0,v1)=$split(l,","), Personid=$toInt(v0), Placeid=$toInt(v1).
person_workAt_organisation(int Personid, int Organisationid).
person_workAt_organisation(Personid, Organisationid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/person_workAt_organisation.csv"), (v0,v1,v2)=$split(l,","), Personid=$toInt(v0), Organisationid=$toInt(v1).
organisation(int id).
organisation(id) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/organisation.csv"), (v0,v1,v2,v3)=$split(l,","), id=$toInt(v0).
place_isPartOf_place(int Placeid, int Placeid2).
place_isPartOf_place(Placeid, Placeid2) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/place_isPartOf_place.csv"), (v0,v1)=$split(l,","), Placeid=$toInt(v0), Placeid2=$toInt(v1).
person_knows_person(int Personid:0..1000, (int Personid2)).
person_knows_person(Personid, Personid2) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/person_knows_person.csv"), (v0,v1)=$split(l,","), Personid=$toInt(v0), Personid2=$toInt(v1).
person_hasInterest_tag(int Personid:0..1000, (int Tagid)).
person_hasInterest_tag(Personid, Tagid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/person_hasInterest_tag.csv"), (v0,v1)=$split(l,","), Personid=$toInt(v0), Tagid=$toInt(v1).
person_studyAt_organisation(int Personid, int Organisationid).
person_studyAt_organisation(Personid, Organisationid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/person_studyAt_organisation.csv"), (v0,v1,v2)=$split(l,","), Personid=$toInt(v0), Organisationid=$toInt(v1).
place(int id, String name).
place(id, name) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/place.csv"), (v0,v1,v2,v3)=$split(l,","), id=$toInt(v0), name=v1.
organisation_isLocatedIn_place(int Organisationid, int Placeid).
organisation_isLocatedIn_place(Organisationid, Placeid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/organisation_isLocatedIn_place.csv"), (v0,v1)=$split(l,","), Organisationid=$toInt(v0), Placeid=$toInt(v1).
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

print "Running query3(4, 3, \"Indonesia\")"
def inc(n, by): return n+by

`all_locs_Indonesia(int locid).
all_locs_Indonesia(locid) :- place(locid, "Indonesia");
	:- all_locs_Indonesia(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Indonesia(int orgid).
all_orgs_Indonesia(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Indonesia(locid).
loc_people_Indonesia(int pid).
loc_people_Indonesia(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Indonesia(locid).
org_people_Indonesia(int pid).
org_people_Indonesia(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Indonesia(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Indonesia(orgid).
all_people_Indonesia(int pid).
all_people_Indonesia(pid) :- loc_people_Indonesia(pid);
	:- org_people_Indonesia(pid).
hop1_Indonesia(int pid0, int pid1).
temp1_Indonesia(int pid0, int pid1).
temp1_Indonesia(pid0,pid1) :- all_people_Indonesia(pid0), person_knows_person(pid0, pid1).
hop1_Indonesia(pid0,pid1) :- temp1_Indonesia(pid0,pid1), all_people_Indonesia(pid1), pid0!=pid1.
hop2_Indonesia(int pid0, int pid2).
temp2_Indonesia(int pid0, int pid1).
temp2_Indonesia(pid0, pid2) :- temp1_Indonesia(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Indonesia(pid0,pid1) :- temp2_Indonesia(pid0,pid1), all_people_Indonesia(pid1), pid0!=pid1, not hop1_Indonesia(pid0,pid1).
hop3_Indonesia(int pid0, int pid3).
temp3_Indonesia(int pid0, int pid1).
temp3_Indonesia(pid0, pid2) :- temp2_Indonesia(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop3_Indonesia(pid0,pid1) :- temp3_Indonesia(pid0,pid1), all_people_Indonesia(pid1), pid0!=pid1, not hop2_Indonesia(pid0,pid1).
common_interests_Indonesia(int pid1, int pid2, int interest).
common_interests_Indonesia(pid1, pid2, interest) :- hop1_Indonesia(pid1, pid2), interest=-1;
	:- hop1_Indonesia(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Indonesia(pid1, pid2, interest) :- hop2_Indonesia(pid1, pid2), interest=-1;
	:- hop2_Indonesia(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Indonesia(pid1, pid2, interest) :- hop3_Indonesia(pid1, pid2), interest=-1;
	:- hop3_Indonesia(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Indonesia(int pid1, int pid2, int count).
interest_counts_Indonesia(pid1, pid2, $inc(1)) :- common_interests_Indonesia(pid1, pid2, interest).
sorted_counts_Indonesia(int count, int pid1, int pid2).
sorted_counts_Indonesia(count, pid1, pid2) :- interest_counts_Indonesia(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Indonesia(count,pid1,pid2)`
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

print "Running query3(3, 2, \"Egypt\")"
def inc(n, by): return n+by

`all_locs_Egypt(int locid).
all_locs_Egypt(locid) :- place(locid, "Egypt");
	:- all_locs_Egypt(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Egypt(int orgid).
all_orgs_Egypt(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Egypt(locid).
loc_people_Egypt(int pid).
loc_people_Egypt(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Egypt(locid).
org_people_Egypt(int pid).
org_people_Egypt(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Egypt(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Egypt(orgid).
all_people_Egypt(int pid).
all_people_Egypt(pid) :- loc_people_Egypt(pid);
	:- org_people_Egypt(pid).
hop1_Egypt(int pid0, int pid1).
temp1_Egypt(int pid0, int pid1).
temp1_Egypt(pid0,pid1) :- all_people_Egypt(pid0), person_knows_person(pid0, pid1).
hop1_Egypt(pid0,pid1) :- temp1_Egypt(pid0,pid1), all_people_Egypt(pid1), pid0!=pid1.
hop2_Egypt(int pid0, int pid2).
temp2_Egypt(int pid0, int pid1).
temp2_Egypt(pid0, pid2) :- temp1_Egypt(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Egypt(pid0,pid1) :- temp2_Egypt(pid0,pid1), all_people_Egypt(pid1), pid0!=pid1, not hop1_Egypt(pid0,pid1).
common_interests_Egypt(int pid1, int pid2, int interest).
common_interests_Egypt(pid1, pid2, interest) :- hop1_Egypt(pid1, pid2), interest=-1;
	:- hop1_Egypt(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Egypt(pid1, pid2, interest) :- hop2_Egypt(pid1, pid2), interest=-1;
	:- hop2_Egypt(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Egypt(int pid1, int pid2, int count).
interest_counts_Egypt(pid1, pid2, $inc(1)) :- common_interests_Egypt(pid1, pid2, interest).
sorted_counts_Egypt(int count, int pid1, int pid2).
sorted_counts_Egypt(count, pid1, pid2) :- interest_counts_Egypt(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Egypt(count,pid1,pid2)`
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

print "Running query3(3, 2, \"Italy\")"
def inc(n, by): return n+by

`all_locs_Italy(int locid).
all_locs_Italy(locid) :- place(locid, "Italy");
	:- all_locs_Italy(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Italy(int orgid).
all_orgs_Italy(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Italy(locid).
loc_people_Italy(int pid).
loc_people_Italy(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Italy(locid).
org_people_Italy(int pid).
org_people_Italy(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Italy(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Italy(orgid).
all_people_Italy(int pid).
all_people_Italy(pid) :- loc_people_Italy(pid);
	:- org_people_Italy(pid).
hop1_Italy(int pid0, int pid1).
temp1_Italy(int pid0, int pid1).
temp1_Italy(pid0,pid1) :- all_people_Italy(pid0), person_knows_person(pid0, pid1).
hop1_Italy(pid0,pid1) :- temp1_Italy(pid0,pid1), all_people_Italy(pid1), pid0!=pid1.
hop2_Italy(int pid0, int pid2).
temp2_Italy(int pid0, int pid1).
temp2_Italy(pid0, pid2) :- temp1_Italy(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Italy(pid0,pid1) :- temp2_Italy(pid0,pid1), all_people_Italy(pid1), pid0!=pid1, not hop1_Italy(pid0,pid1).
common_interests_Italy(int pid1, int pid2, int interest).
common_interests_Italy(pid1, pid2, interest) :- hop1_Italy(pid1, pid2), interest=-1;
	:- hop1_Italy(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Italy(pid1, pid2, interest) :- hop2_Italy(pid1, pid2), interest=-1;
	:- hop2_Italy(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Italy(int pid1, int pid2, int count).
interest_counts_Italy(pid1, pid2, $inc(1)) :- common_interests_Italy(pid1, pid2, interest).
sorted_counts_Italy(int count, int pid1, int pid2).
sorted_counts_Italy(count, pid1, pid2) :- interest_counts_Italy(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Italy(count,pid1,pid2)`
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

print "Running query3(5, 4, \"Chengdu\")"
def inc(n, by): return n+by

`all_locs_Chengdu(int locid).
all_locs_Chengdu(locid) :- place(locid, "Chengdu");
	:- all_locs_Chengdu(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Chengdu(int orgid).
all_orgs_Chengdu(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Chengdu(locid).
loc_people_Chengdu(int pid).
loc_people_Chengdu(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Chengdu(locid).
org_people_Chengdu(int pid).
org_people_Chengdu(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Chengdu(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Chengdu(orgid).
all_people_Chengdu(int pid).
all_people_Chengdu(pid) :- loc_people_Chengdu(pid);
	:- org_people_Chengdu(pid).
hop1_Chengdu(int pid0, int pid1).
temp1_Chengdu(int pid0, int pid1).
temp1_Chengdu(pid0,pid1) :- all_people_Chengdu(pid0), person_knows_person(pid0, pid1).
hop1_Chengdu(pid0,pid1) :- temp1_Chengdu(pid0,pid1), all_people_Chengdu(pid1), pid0!=pid1.
hop2_Chengdu(int pid0, int pid2).
temp2_Chengdu(int pid0, int pid1).
temp2_Chengdu(pid0, pid2) :- temp1_Chengdu(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Chengdu(pid0,pid1) :- temp2_Chengdu(pid0,pid1), all_people_Chengdu(pid1), pid0!=pid1, not hop1_Chengdu(pid0,pid1).
hop3_Chengdu(int pid0, int pid3).
temp3_Chengdu(int pid0, int pid1).
temp3_Chengdu(pid0, pid2) :- temp2_Chengdu(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop3_Chengdu(pid0,pid1) :- temp3_Chengdu(pid0,pid1), all_people_Chengdu(pid1), pid0!=pid1, not hop2_Chengdu(pid0,pid1).
hop4_Chengdu(int pid0, int pid4).
temp4_Chengdu(int pid0, int pid1).
temp4_Chengdu(pid0, pid2) :- temp3_Chengdu(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop4_Chengdu(pid0,pid1) :- temp4_Chengdu(pid0,pid1), all_people_Chengdu(pid1), pid0!=pid1, not hop3_Chengdu(pid0,pid1).
common_interests_Chengdu(int pid1, int pid2, int interest).
common_interests_Chengdu(pid1, pid2, interest) :- hop1_Chengdu(pid1, pid2), interest=-1;
	:- hop1_Chengdu(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Chengdu(pid1, pid2, interest) :- hop2_Chengdu(pid1, pid2), interest=-1;
	:- hop2_Chengdu(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Chengdu(pid1, pid2, interest) :- hop3_Chengdu(pid1, pid2), interest=-1;
	:- hop3_Chengdu(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Chengdu(pid1, pid2, interest) :- hop4_Chengdu(pid1, pid2), interest=-1;
	:- hop4_Chengdu(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Chengdu(int pid1, int pid2, int count).
interest_counts_Chengdu(pid1, pid2, $inc(1)) :- common_interests_Chengdu(pid1, pid2, interest).
sorted_counts_Chengdu(int count, int pid1, int pid2).
sorted_counts_Chengdu(count, pid1, pid2) :- interest_counts_Chengdu(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Chengdu(count,pid1,pid2)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1],x[2]))
used = set() 
results = 0 
for count,pid1,pid2 in result_set:
	if results >= 5:
		break 
	if pid1 > pid2:
		id = str(pid2) + '-' + str(pid1)
	else:
		id = str(pid1) + '-' + str(pid2)
	if id not in used: 
		print pid1,pid2,count
		used.add(id) 
		results += 1 

print "Running query3(3, 2, \"Peru\")"
def inc(n, by): return n+by

`all_locs_Peru(int locid).
all_locs_Peru(locid) :- place(locid, "Peru");
	:- all_locs_Peru(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Peru(int orgid).
all_orgs_Peru(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Peru(locid).
loc_people_Peru(int pid).
loc_people_Peru(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Peru(locid).
org_people_Peru(int pid).
org_people_Peru(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Peru(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Peru(orgid).
all_people_Peru(int pid).
all_people_Peru(pid) :- loc_people_Peru(pid);
	:- org_people_Peru(pid).
hop1_Peru(int pid0, int pid1).
temp1_Peru(int pid0, int pid1).
temp1_Peru(pid0,pid1) :- all_people_Peru(pid0), person_knows_person(pid0, pid1).
hop1_Peru(pid0,pid1) :- temp1_Peru(pid0,pid1), all_people_Peru(pid1), pid0!=pid1.
hop2_Peru(int pid0, int pid2).
temp2_Peru(int pid0, int pid1).
temp2_Peru(pid0, pid2) :- temp1_Peru(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Peru(pid0,pid1) :- temp2_Peru(pid0,pid1), all_people_Peru(pid1), pid0!=pid1, not hop1_Peru(pid0,pid1).
common_interests_Peru(int pid1, int pid2, int interest).
common_interests_Peru(pid1, pid2, interest) :- hop1_Peru(pid1, pid2), interest=-1;
	:- hop1_Peru(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Peru(pid1, pid2, interest) :- hop2_Peru(pid1, pid2), interest=-1;
	:- hop2_Peru(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Peru(int pid1, int pid2, int count).
interest_counts_Peru(pid1, pid2, $inc(1)) :- common_interests_Peru(pid1, pid2, interest).
sorted_counts_Peru(int count, int pid1, int pid2).
sorted_counts_Peru(count, pid1, pid2) :- interest_counts_Peru(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Peru(count,pid1,pid2)`
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

print "Running query3(3, 2, \"Democratic_Republic_of_the_Congo\")"
def inc(n, by): return n+by

`all_locs_Democratic_Republic_of_the_Congo(int locid).
all_locs_Democratic_Republic_of_the_Congo(locid) :- place(locid, "Democratic_Republic_of_the_Congo");
	:- all_locs_Democratic_Republic_of_the_Congo(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Democratic_Republic_of_the_Congo(int orgid).
all_orgs_Democratic_Republic_of_the_Congo(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Democratic_Republic_of_the_Congo(locid).
loc_people_Democratic_Republic_of_the_Congo(int pid).
loc_people_Democratic_Republic_of_the_Congo(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Democratic_Republic_of_the_Congo(locid).
org_people_Democratic_Republic_of_the_Congo(int pid).
org_people_Democratic_Republic_of_the_Congo(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Democratic_Republic_of_the_Congo(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Democratic_Republic_of_the_Congo(orgid).
all_people_Democratic_Republic_of_the_Congo(int pid).
all_people_Democratic_Republic_of_the_Congo(pid) :- loc_people_Democratic_Republic_of_the_Congo(pid);
	:- org_people_Democratic_Republic_of_the_Congo(pid).
hop1_Democratic_Republic_of_the_Congo(int pid0, int pid1).
temp1_Democratic_Republic_of_the_Congo(int pid0, int pid1).
temp1_Democratic_Republic_of_the_Congo(pid0,pid1) :- all_people_Democratic_Republic_of_the_Congo(pid0), person_knows_person(pid0, pid1).
hop1_Democratic_Republic_of_the_Congo(pid0,pid1) :- temp1_Democratic_Republic_of_the_Congo(pid0,pid1), all_people_Democratic_Republic_of_the_Congo(pid1), pid0!=pid1.
hop2_Democratic_Republic_of_the_Congo(int pid0, int pid2).
temp2_Democratic_Republic_of_the_Congo(int pid0, int pid1).
temp2_Democratic_Republic_of_the_Congo(pid0, pid2) :- temp1_Democratic_Republic_of_the_Congo(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Democratic_Republic_of_the_Congo(pid0,pid1) :- temp2_Democratic_Republic_of_the_Congo(pid0,pid1), all_people_Democratic_Republic_of_the_Congo(pid1), pid0!=pid1, not hop1_Democratic_Republic_of_the_Congo(pid0,pid1).
common_interests_Democratic_Republic_of_the_Congo(int pid1, int pid2, int interest).
common_interests_Democratic_Republic_of_the_Congo(pid1, pid2, interest) :- hop1_Democratic_Republic_of_the_Congo(pid1, pid2), interest=-1;
	:- hop1_Democratic_Republic_of_the_Congo(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Democratic_Republic_of_the_Congo(pid1, pid2, interest) :- hop2_Democratic_Republic_of_the_Congo(pid1, pid2), interest=-1;
	:- hop2_Democratic_Republic_of_the_Congo(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Democratic_Republic_of_the_Congo(int pid1, int pid2, int count).
interest_counts_Democratic_Republic_of_the_Congo(pid1, pid2, $inc(1)) :- common_interests_Democratic_Republic_of_the_Congo(pid1, pid2, interest).
sorted_counts_Democratic_Republic_of_the_Congo(int count, int pid1, int pid2).
sorted_counts_Democratic_Republic_of_the_Congo(count, pid1, pid2) :- interest_counts_Democratic_Republic_of_the_Congo(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Democratic_Republic_of_the_Congo(count,pid1,pid2)`
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

print "Running query3(7, 6, \"Ankara\")"
def inc(n, by): return n+by

`all_locs_Ankara(int locid).
all_locs_Ankara(locid) :- place(locid, "Ankara");
	:- all_locs_Ankara(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Ankara(int orgid).
all_orgs_Ankara(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Ankara(locid).
loc_people_Ankara(int pid).
loc_people_Ankara(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Ankara(locid).
org_people_Ankara(int pid).
org_people_Ankara(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Ankara(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Ankara(orgid).
all_people_Ankara(int pid).
all_people_Ankara(pid) :- loc_people_Ankara(pid);
	:- org_people_Ankara(pid).
hop1_Ankara(int pid0, int pid1).
temp1_Ankara(int pid0, int pid1).
temp1_Ankara(pid0,pid1) :- all_people_Ankara(pid0), person_knows_person(pid0, pid1).
hop1_Ankara(pid0,pid1) :- temp1_Ankara(pid0,pid1), all_people_Ankara(pid1), pid0!=pid1.
hop2_Ankara(int pid0, int pid2).
temp2_Ankara(int pid0, int pid1).
temp2_Ankara(pid0, pid2) :- temp1_Ankara(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Ankara(pid0,pid1) :- temp2_Ankara(pid0,pid1), all_people_Ankara(pid1), pid0!=pid1, not hop1_Ankara(pid0,pid1).
hop3_Ankara(int pid0, int pid3).
temp3_Ankara(int pid0, int pid1).
temp3_Ankara(pid0, pid2) :- temp2_Ankara(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop3_Ankara(pid0,pid1) :- temp3_Ankara(pid0,pid1), all_people_Ankara(pid1), pid0!=pid1, not hop2_Ankara(pid0,pid1).
hop4_Ankara(int pid0, int pid4).
temp4_Ankara(int pid0, int pid1).
temp4_Ankara(pid0, pid2) :- temp3_Ankara(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop4_Ankara(pid0,pid1) :- temp4_Ankara(pid0,pid1), all_people_Ankara(pid1), pid0!=pid1, not hop3_Ankara(pid0,pid1).
hop5_Ankara(int pid0, int pid5).
temp5_Ankara(int pid0, int pid1).
temp5_Ankara(pid0, pid2) :- temp4_Ankara(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop5_Ankara(pid0,pid1) :- temp5_Ankara(pid0,pid1), all_people_Ankara(pid1), pid0!=pid1, not hop4_Ankara(pid0,pid1).
hop6_Ankara(int pid0, int pid6).
temp6_Ankara(int pid0, int pid1).
temp6_Ankara(pid0, pid2) :- temp5_Ankara(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop6_Ankara(pid0,pid1) :- temp6_Ankara(pid0,pid1), all_people_Ankara(pid1), pid0!=pid1, not hop5_Ankara(pid0,pid1).
common_interests_Ankara(int pid1, int pid2, int interest).
common_interests_Ankara(pid1, pid2, interest) :- hop1_Ankara(pid1, pid2), interest=-1;
	:- hop1_Ankara(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Ankara(pid1, pid2, interest) :- hop2_Ankara(pid1, pid2), interest=-1;
	:- hop2_Ankara(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Ankara(pid1, pid2, interest) :- hop3_Ankara(pid1, pid2), interest=-1;
	:- hop3_Ankara(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Ankara(pid1, pid2, interest) :- hop4_Ankara(pid1, pid2), interest=-1;
	:- hop4_Ankara(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Ankara(pid1, pid2, interest) :- hop5_Ankara(pid1, pid2), interest=-1;
	:- hop5_Ankara(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Ankara(pid1, pid2, interest) :- hop6_Ankara(pid1, pid2), interest=-1;
	:- hop6_Ankara(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Ankara(int pid1, int pid2, int count).
interest_counts_Ankara(pid1, pid2, $inc(1)) :- common_interests_Ankara(pid1, pid2, interest).
sorted_counts_Ankara(int count, int pid1, int pid2).
sorted_counts_Ankara(count, pid1, pid2) :- interest_counts_Ankara(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Ankara(count,pid1,pid2)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1],x[2]))
used = set() 
results = 0 
for count,pid1,pid2 in result_set:
	if results >= 7:
		break 
	if pid1 > pid2:
		id = str(pid2) + '-' + str(pid1)
	else:
		id = str(pid1) + '-' + str(pid2)
	if id not in used: 
		print pid1,pid2,count
		used.add(id) 
		results += 1 

print "Running query3(3, 2, \"Luoyang\")"
def inc(n, by): return n+by

`all_locs_Luoyang(int locid).
all_locs_Luoyang(locid) :- place(locid, "Luoyang");
	:- all_locs_Luoyang(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Luoyang(int orgid).
all_orgs_Luoyang(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Luoyang(locid).
loc_people_Luoyang(int pid).
loc_people_Luoyang(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Luoyang(locid).
org_people_Luoyang(int pid).
org_people_Luoyang(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Luoyang(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Luoyang(orgid).
all_people_Luoyang(int pid).
all_people_Luoyang(pid) :- loc_people_Luoyang(pid);
	:- org_people_Luoyang(pid).
hop1_Luoyang(int pid0, int pid1).
temp1_Luoyang(int pid0, int pid1).
temp1_Luoyang(pid0,pid1) :- all_people_Luoyang(pid0), person_knows_person(pid0, pid1).
hop1_Luoyang(pid0,pid1) :- temp1_Luoyang(pid0,pid1), all_people_Luoyang(pid1), pid0!=pid1.
hop2_Luoyang(int pid0, int pid2).
temp2_Luoyang(int pid0, int pid1).
temp2_Luoyang(pid0, pid2) :- temp1_Luoyang(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Luoyang(pid0,pid1) :- temp2_Luoyang(pid0,pid1), all_people_Luoyang(pid1), pid0!=pid1, not hop1_Luoyang(pid0,pid1).
common_interests_Luoyang(int pid1, int pid2, int interest).
common_interests_Luoyang(pid1, pid2, interest) :- hop1_Luoyang(pid1, pid2), interest=-1;
	:- hop1_Luoyang(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Luoyang(pid1, pid2, interest) :- hop2_Luoyang(pid1, pid2), interest=-1;
	:- hop2_Luoyang(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Luoyang(int pid1, int pid2, int count).
interest_counts_Luoyang(pid1, pid2, $inc(1)) :- common_interests_Luoyang(pid1, pid2, interest).
sorted_counts_Luoyang(int count, int pid1, int pid2).
sorted_counts_Luoyang(count, pid1, pid2) :- interest_counts_Luoyang(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Luoyang(count,pid1,pid2)`
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

print "Running query3(4, 3, \"Taiwan\")"
def inc(n, by): return n+by

`all_locs_Taiwan(int locid).
all_locs_Taiwan(locid) :- place(locid, "Taiwan");
	:- all_locs_Taiwan(parentlocid), place_isPartOf_place(locid, parentlocid), place(locid, name).
all_orgs_Taiwan(int orgid).
all_orgs_Taiwan(orgid) :- organisation(orgid), organisation_isLocatedIn_place(orgid, locid), all_locs_Taiwan(locid).
loc_people_Taiwan(int pid).
loc_people_Taiwan(pid) :- person_isLocatedIn_place(pid, locid), all_locs_Taiwan(locid).
org_people_Taiwan(int pid).
org_people_Taiwan(pid) :- person_workAt_organisation(pid, orgid), all_orgs_Taiwan(orgid);
	:- person_studyAt_organisation(pid, orgid), all_orgs_Taiwan(orgid).
all_people_Taiwan(int pid).
all_people_Taiwan(pid) :- loc_people_Taiwan(pid);
	:- org_people_Taiwan(pid).
hop1_Taiwan(int pid0, int pid1).
temp1_Taiwan(int pid0, int pid1).
temp1_Taiwan(pid0,pid1) :- all_people_Taiwan(pid0), person_knows_person(pid0, pid1).
hop1_Taiwan(pid0,pid1) :- temp1_Taiwan(pid0,pid1), all_people_Taiwan(pid1), pid0!=pid1.
hop2_Taiwan(int pid0, int pid2).
temp2_Taiwan(int pid0, int pid1).
temp2_Taiwan(pid0, pid2) :- temp1_Taiwan(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop2_Taiwan(pid0,pid1) :- temp2_Taiwan(pid0,pid1), all_people_Taiwan(pid1), pid0!=pid1, not hop1_Taiwan(pid0,pid1).
hop3_Taiwan(int pid0, int pid3).
temp3_Taiwan(int pid0, int pid1).
temp3_Taiwan(pid0, pid2) :- temp2_Taiwan(pid0, pid1), person(pid1), person_knows_person(pid1, pid2).
hop3_Taiwan(pid0,pid1) :- temp3_Taiwan(pid0,pid1), all_people_Taiwan(pid1), pid0!=pid1, not hop2_Taiwan(pid0,pid1).
common_interests_Taiwan(int pid1, int pid2, int interest).
common_interests_Taiwan(pid1, pid2, interest) :- hop1_Taiwan(pid1, pid2), interest=-1;
	:- hop1_Taiwan(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Taiwan(pid1, pid2, interest) :- hop2_Taiwan(pid1, pid2), interest=-1;
	:- hop2_Taiwan(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
common_interests_Taiwan(pid1, pid2, interest) :- hop3_Taiwan(pid1, pid2), interest=-1;
	:- hop3_Taiwan(pid1, pid2), person_hasInterest_tag(pid1, interest), person_hasInterest_tag(pid2, interest).
interest_counts_Taiwan(int pid1, int pid2, int count).
interest_counts_Taiwan(pid1, pid2, $inc(1)) :- common_interests_Taiwan(pid1, pid2, interest).
sorted_counts_Taiwan(int count, int pid1, int pid2).
sorted_counts_Taiwan(count, pid1, pid2) :- interest_counts_Taiwan(pid1, pid2, c), count=c-1.
`
result_set = `sorted_counts_Taiwan(count,pid1,pid2)`
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

