all_locs(locid) :- place(locid, ‘Amherst’, _, _)
all_locs(locid) :- all_locs(parentlocid),
     	 place_isPartOf_place(locid, parentlocid),
 		       place(locid, name, _, _)

all_orgs(orgid) :- organisation(orgid, _, _, _),
  	       organisation_isLocatedIn_place(orgid, locid),
       all_locs(locid)

loc_people(pid) :- person_isLocatedIn_place(pid, locid), all_locs(locid)

org_people(pid) :- person_workAt_organisation(pid, orgid, _), all_orgs(orgid)
org_people(pid) :- person_studyAt_organisation(pid, orgid, _), all_orgs(orgid)

all_people(pid) :- loc_people(pid)
all_people(pid) :- org_people(pid)

hop1(pid0,pid1) :- person(pid0, _, _, _, _, _, _, _), 
       person_knows_person(pid0, pid1),
       person(pid1, _, _, _, _, _, _, _)
hop2(pid0,pid2) :- person(pid0, _, _, _, _, _, _, _), 
       person_knows_person(pid0, pid1),
       person(pid1, _, _, _, _, _, _, _),
       person_knows_person(pid1, pid2),
       person(pid2, _, _, _, _, _, _, _)

all_hops(pid1,pid2) :- hop1(pid1,pid2)
all_hops(pid1,pid2) :- hop2(pid1,pid2)

common_interests(pid1,pid2,'') :- all_hops(pid1, pid2),
    all_people(pid1),
    all_people(pid2)
common_interests(pid1,pid2,interest) :- common_interests(pid1, pid2, ''),
          person_hasInterest_tag(pid1,interest),
          person_hasInterest_tag(pid2,interest)