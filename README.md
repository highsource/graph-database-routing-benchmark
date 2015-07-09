# Routing benchmark on popular graph databases

The purpose of this project is to get aquainted with popular graph databases and to study their performance of routing on decent-size real world networks.

# Queries

This project focuses on routing so the queries we'd like to test are:
* Shortest path between two nodes in a graph, with and without a heuristic distance function ([A*](https://en.wikipedia.org/wiki/A*_search_algorithm)/[Dijkstra](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm))
* .. also considering constraints on edges
* Nodes reachable below certain cost

# Data

The ultimate goal would be to test a continental-size network, for instance the European railroad network. Something which is not too small, but also not too huge, something that would still fit in RAM on a single machine.

At the moment the idea is to use a GTFS timetable of a relatively large transport network, for instance the transport union of Berlin:

http://www.gtfs-data-exchange.com/agency/berliner-verkehrsbetriebe/

It is about 250MB in size, quite large to be interesting, but small enough to fit in RAM.

The timetable described by GTFS will be extanded in temporal-spatial graph. Each node will be a combination of a stop and a time. Trips, transfers and simple waiting form edges. The cost of the edge will be the travel time between stops, transfer time or waiting time.

We'll first study this for a single day (a normal working day).

# Databases in scope

We'll start with these:
* [Neo4j](http://neo4j.com/)
* [OrientDB](http://orientdb.com/)
* [ArangoDB](https://www.arangodb.com/)
* ???

Performance will also be compared to the in-memory routing via [JGraphT](http://jgrapht.org/).
