Details
-------
This project contains the implementation of many algorithms that solve the well known stable marriage problem. (https://en.wikipedia.org/wiki/Stable_marriage_problem)

Algorithms implemented from the literature:
* Gale-Shapley [https://www.researchgate.net/publication/228108175_College_Admissions_and_Stability_of_Marriage]
* MinRegret [https://dl.acm.org/citation.cfm?id=23802]
* MinEgalitarian [https://dl.acm.org/citation.cfm?id=28871]
* Approx [https://dl.acm.org/citation.cfm?id=1868239]
* ESMA [https://dl.acm.org/citation.cfm?id=2921985&preflayout=flat]
* Swing [https://dl.acm.org/citation.cfm?id=2485203]
* DACC (2 variations)  [https://dl.acm.org/citation.cfm?id=2940727]
* Lotto [https://dl.acm.org/citation.cfm?id=593304]
* ROM [https://link.springer.com/article/10.1007/BF01211824]
* EROM [https://link.springer.com/article/10.1007/s11238-005-6846-0]
* SML2 [https://www.researchgate.net/publication/286062161_Local_Search_Approaches_in_Stable_Matching_Problems]
* BiLS (refined version with rotations) [https://www.researchgate.net/publication/312256504_A_Bidirectional_Local_Search_for_the_Stable_Marriage_Problem]
* Better/Best Response Dynamics [https://dl.acm.org/citation.cfm?id=1386831]

Novel algorithms implemented:
* PDB, EDS, LDS: proposal-based approaches that terminate by monotonically increasing content couples
* PowerBalance: proposal-based algorithm that tries to keep a good balance between both sides and then terminates by a compromising procedure
* Hybrid, HybridMultiSearch: combinations of PowerBalance with the local search method
* DA_Random, SDA_Random: random proposals utlizing two different mechanisms

Usage
-----
Clone from github and run:
```
mvn package
```
To run an algorithm with random input (uniform lists) you can execute the following classes from the produced jar:

*gr.ntua.cslab.algorithms.(algorithm)*

OR

Run experiments for diverse distributions and plot results:
* Create datasets (*create_data.sh*)
* Run (*run_experiments.sh*)
* Plot (*do_plots.sh*)

There is also the option to produce and run zipped input files to save space.

Contact
-------
Nikos Tziavelis, ntzia@cslab.ece.ntua.gr
