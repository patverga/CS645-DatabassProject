CS645-Project
=====================

Large-scale social network data analysis using Datalog.

Query 1
--------
- Get subset of people who are frequent communicators (+know each other)
- Want min of all paths between a and b in this induced subgraph

Query 2
--------
- Get people with birthday later than d
- Get graphs for each interest, connectivity defined as people knowing each other
- Max size of graphs

Query 3
---------
- Get all pairs of people h hops away from each other
- Filter by either live in same location or work/associated with same location
- For each pair count number of common interests
- Take max k

Query 4
---------
- Do all-pairs shortest paths where edges defined by sharing interests (members of same forum, given)
- For each node, compute centrality: function of number of nodes reachable and lengths of those paths
- Return k nodes with max centrality
