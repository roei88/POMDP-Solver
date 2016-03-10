# POMDP-Solver

#Short description:
  Sequential decision making under uncertainty using belief-state MDP for decision-making: the EU refugee problem.
  The environment consists of a weighted unidrected graph, where each vertex has a known probability of having police and food.
  The probabilities of police are mutually independent, and they do not move.
  We do not know the locations of the border police, and food Police and food appear independently, with a known given probability, at   each vertex. They are revealed with certainty when a refugee reaches a neigbouring vertex.
  If there is food at a node, there is a sufficient amount that it is never exhausted. The refugee's only action are traveling between   vertices. 
  Traversal costs are just the weight of the edge in this variant, except when starting the action in a vertex with food, in which case   the travel cost of the edge is divided by 2. 
  That is, traversing from vertex v0 to vertex v1, that are connected by e1 with weight of 5, will cost the refugee agent -5 points if   v0 does not contain any food, and -2.5 if it does. If police is present at v1 then that move will be illigal.
  Each simulation a single refugee agent is starting at vertex s, and has only one goal at vertex t.
  The Simulator purpuse is to find a policy that brings the refugee agent from vertex s to t, on a route that has minimal expected       cost, without encountering police.

#project structure:
  The POMDP-Solver contains five packages:
    dataStructures	- Contains the Bag and Stack ds that are part of the graph and the environment types
    env - Represents the environment, that is the world we are acting in, which is comprised of a graph and info about the vertexes and           agents.
    graph	- undirected weighed graph data sctructure.
    io	- I\O realted operations.
    main	- Contains the main simulator method.
    pomdp - The heart of the solver, contains all legal states, actions and calculates the optimal policy.

#Integrated packages: (added from princeton uni. cs department and modifed partially according to needs)
  graph
  io
  dataStructures
#Fully writen packages: (fully writen fro scrach)
  main
  env
  pomdp
#Input folder
  Graph.txt - Represents the graph.
  vertexInfo.txt - Represents the data about vertexes (police and food probabilities).
  locations.txt - Contains real police and food locations for simulating a run from source to goal.
  
