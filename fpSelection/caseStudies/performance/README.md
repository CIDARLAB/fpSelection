# Ranking results of the Heuristic Algorithms

This folder contains the **rank** of the solution produced by Simulated Annealing and Hill Climbing compared to the top 100 panel designs identified by exhaustive search. Each file contains 200 seeded runs of either Simulated Annealing or Hill Climbing, where an n-color panel was designed from a library of either 8 fluorophores or 12 fluorophores. The size of the panels range from 2 to 5. In each run, 50 independant iterations of Simulated Annealing or Hill Climbing (depending on the algorithm) were triggered and the best result among all 50 iterations was returned as the solution produced by the run. This solution was then ranked against the top 100 solutions produced by exhaustive search. 

This was repeated for all 3 cytometers (CytoFlex, Fortessa, and Macsquant). 

To recreate these solutions, please run the following commands in the following location: `/repo/fpSelection/`

```bash
mvn -Dtest=PerformanceTest#testCytoFlexRanks test
mvn -Dtest=PerformanceTest#testMacsquantRanks test
mvn -Dtest=PerformanceTest#testFortessaRanks test
```

The files created by these tests will automatically saved in this folder. 