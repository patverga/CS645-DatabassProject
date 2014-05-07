
print "Loading the tables now ...  "
`person(int id, String birthday).
person(id, birthday) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/person.csv"), (v0,v1,v2,v3,v4,v5,v6,v7)=$split(l,","), id=$toInt(v0), birthday=v4.
tag(int id, String name).
tag(id, name) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/tag.csv"), (v0,v1,v2)=$split(l,","), id=$toInt(v0), name=v1.
person_knows_person(int Personid:0..1000, (int Personid2)).
person_knows_person(Personid, Personid2) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/person_knows_person.csv"), (v0,v1)=$split(l,","), Personid=$toInt(v0), Personid2=$toInt(v1).
person_hasInterest_tag(int Personid:0..1000, (int Tagid)).
person_hasInterest_tag(Personid, Tagid) :- l=$read("/home/pv/Documents/CS645-DatabassProject/data/commas/person_hasInterest_tag.csv"), (v0,v1)=$split(l,","), Personid=$toInt(v0), Tagid=$toInt(v1).
`
print "Done loading tables "

print "\nRunning query2(3, 1980-02-01)\n"
def inc(n, by): return n+by

`young_people_a(int id).
young_people_a(id) :- person(id, date), (y,m,d)=$split(date, "-"), $toInt(y)*10000+$toInt(m)*100+$toInt(d) >= 19800201.
conn_comps_a(int pid1, int pid2, int tag).
conn_comps_a(pid1, pid2, tag) :- young_people_a(pid1), person_hasInterest_tag(pid1, tag), person_hasInterest_tag(pid2, tag), young_people_a(pid2), person_knows_person(pid1, pid2);
	:- conn_comps_a(pid1, y, tag), person_knows_person(y, pid2), pid1 != pid2, young_people_a(pid2), person_hasInterest_tag(pid2, tag).
comp_sizes_a(int pid, int tag, int count).
comp_sizes_a(pid, tag, $inc(1)) :- conn_comps_a(pid, _, tag);
	:- young_people_a(pid), person_hasInterest_tag(pid, tag).tag_sizes_a(int count, String tagname).
tag_sizes_a(count, tagname) :- comp_sizes_a(pid, tag, count), tag(tag, tagname).`
result_set = `tag_sizes_a(count,name)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1]))
used = set()
results = 0
for count, name in result_set:
	if results >= 3:
		break
	if name not in used: 
		print name, count
		used.add(name)
		results += 1


print "\nRunning query2(4, 1982-01-30)\n"
def inc(n, by): return n+by

`young_people_b(int id).
young_people_b(id) :- person(id, date), (y,m,d)=$split(date, "-"), $toInt(y)*10000+$toInt(m)*100+$toInt(d) >= 19820130.
conn_comps_b(int pid1, int pid2, int tag).
conn_comps_b(pid1, pid2, tag) :- young_people_b(pid1), person_hasInterest_tag(pid1, tag), person_hasInterest_tag(pid2, tag), young_people_b(pid2), person_knows_person(pid1, pid2);
	:- conn_comps_b(pid1, y, tag), person_knows_person(y, pid2), pid1 != pid2, young_people_b(pid2), person_hasInterest_tag(pid2, tag).
comp_sizes_b(int pid, int tag, int count).
comp_sizes_b(pid, tag, $inc(1)) :- conn_comps_b(pid, _, tag);
	:- young_people_b(pid), person_hasInterest_tag(pid, tag).tag_sizes_b(int count, String tagname).
tag_sizes_b(count, tagname) :- comp_sizes_b(pid, tag, count), tag(tag, tagname).`
result_set = `tag_sizes_b(count,name)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1]))
used = set()
results = 0
for count, name in result_set:
	if results >= 4:
		break
	if name not in used: 
		print name, count
		used.add(name)
		results += 1


print "\nRunning query2(3, 1984-02-01)\n"
def inc(n, by): return n+by

`young_people_c(int id).
young_people_c(id) :- person(id, date), (y,m,d)=$split(date, "-"), $toInt(y)*10000+$toInt(m)*100+$toInt(d) >= 19840201.
conn_comps_c(int pid1, int pid2, int tag).
conn_comps_c(pid1, pid2, tag) :- young_people_c(pid1), person_hasInterest_tag(pid1, tag), person_hasInterest_tag(pid2, tag), young_people_c(pid2), person_knows_person(pid1, pid2);
	:- conn_comps_c(pid1, y, tag), person_knows_person(y, pid2), pid1 != pid2, young_people_c(pid2), person_hasInterest_tag(pid2, tag).
comp_sizes_c(int pid, int tag, int count).
comp_sizes_c(pid, tag, $inc(1)) :- conn_comps_c(pid, _, tag);
	:- young_people_c(pid), person_hasInterest_tag(pid, tag).tag_sizes_c(int count, String tagname).
tag_sizes_c(count, tagname) :- comp_sizes_c(pid, tag, count), tag(tag, tagname).`
result_set = `tag_sizes_c(count,name)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1]))
used = set()
results = 0
for count, name in result_set:
	if results >= 3:
		break
	if name not in used: 
		print name, count
		used.add(name)
		results += 1


print "\nRunning query2(3, 1986-01-28)\n"
def inc(n, by): return n+by

`young_people_d(int id).
young_people_d(id) :- person(id, date), (y,m,d)=$split(date, "-"), $toInt(y)*10000+$toInt(m)*100+$toInt(d) >= 19860128.
conn_comps_d(int pid1, int pid2, int tag).
conn_comps_d(pid1, pid2, tag) :- young_people_d(pid1), person_hasInterest_tag(pid1, tag), person_hasInterest_tag(pid2, tag), young_people_d(pid2), person_knows_person(pid1, pid2);
	:- conn_comps_d(pid1, y, tag), person_knows_person(y, pid2), pid1 != pid2, young_people_d(pid2), person_hasInterest_tag(pid2, tag).
comp_sizes_d(int pid, int tag, int count).
comp_sizes_d(pid, tag, $inc(1)) :- conn_comps_d(pid, _, tag);
	:- young_people_d(pid), person_hasInterest_tag(pid, tag).tag_sizes_d(int count, String tagname).
tag_sizes_d(count, tagname) :- comp_sizes_d(pid, tag, count), tag(tag, tagname).`
result_set = `tag_sizes_d(count,name)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1]))
used = set()
results = 0
for count, name in result_set:
	if results >= 3:
		break
	if name not in used: 
		print name, count
		used.add(name)
		results += 1


print "\nRunning query2(5, 1988-01-27)\n"
def inc(n, by): return n+by

`young_people_e(int id).
young_people_e(id) :- person(id, date), (y,m,d)=$split(date, "-"), $toInt(y)*10000+$toInt(m)*100+$toInt(d) >= 19880127.
conn_comps_e(int pid1, int pid2, int tag).
conn_comps_e(pid1, pid2, tag) :- young_people_e(pid1), person_hasInterest_tag(pid1, tag), person_hasInterest_tag(pid2, tag), young_people_e(pid2), person_knows_person(pid1, pid2);
	:- conn_comps_e(pid1, y, tag), person_knows_person(y, pid2), pid1 != pid2, young_people_e(pid2), person_hasInterest_tag(pid2, tag).
comp_sizes_e(int pid, int tag, int count).
comp_sizes_e(pid, tag, $inc(1)) :- conn_comps_e(pid, _, tag);
	:- young_people_e(pid), person_hasInterest_tag(pid, tag).tag_sizes_e(int count, String tagname).
tag_sizes_e(count, tagname) :- comp_sizes_e(pid, tag, count), tag(tag, tagname).`
result_set = `tag_sizes_e(count,name)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1]))
used = set()
results = 0
for count, name in result_set:
	if results >= 5:
		break
	if name not in used: 
		print name, count
		used.add(name)
		results += 1


print "\nRunning query2(3, 1990-01-31)\n"
def inc(n, by): return n+by

`young_people_f(int id).
young_people_f(id) :- person(id, date), (y,m,d)=$split(date, "-"), $toInt(y)*10000+$toInt(m)*100+$toInt(d) >= 19900131.
conn_comps_f(int pid1, int pid2, int tag).
conn_comps_f(pid1, pid2, tag) :- young_people_f(pid1), person_hasInterest_tag(pid1, tag), person_hasInterest_tag(pid2, tag), young_people_f(pid2), person_knows_person(pid1, pid2);
	:- conn_comps_f(pid1, y, tag), person_knows_person(y, pid2), pid1 != pid2, young_people_f(pid2), person_hasInterest_tag(pid2, tag).
comp_sizes_f(int pid, int tag, int count).
comp_sizes_f(pid, tag, $inc(1)) :- conn_comps_f(pid, _, tag);
	:- young_people_f(pid), person_hasInterest_tag(pid, tag).tag_sizes_f(int count, String tagname).
tag_sizes_f(count, tagname) :- comp_sizes_f(pid, tag, count), tag(tag, tagname).`
result_set = `tag_sizes_f(count,name)`
result_set = sorted(result_set, key=lambda x:(-x[0],x[1]))
used = set()
results = 0
for count, name in result_set:
	if results >= 3:
		break
	if name not in used: 
		print name, count
		used.add(name)
		results += 1


