Details
-------
This project contains the implementation of many algorithms that solve the well known stable marriage problem. (https://en.wikipedia.org/wiki/Stable_marriage_problem)

Algorithms implemented:
* Gale-Shapley (https://www.researchgate.net/publication/228108175_College_Admissions_and_Stability_of_Marriage)
* ESMA (http://ieeexplore.ieee.org/document/7372239)
* Lotto (https://link.springer.com/article/10.1023/A%3A1026453915989)
* PDB, EDS, LDS, CaC, CaC_SEOpt treat both sides in a fair manner while ensuring termination
* GreedySE, GreedyME try to optimize a metric in a greedy fashion

Usage
-----
Clone from github and run:
```
mvn package
```
To run an algorithm with random input you can execute the following classes from the produced jar:
*gr.ntua.cslab.algorithms.(algorithm)*

OR

Run experiments for diverse distributions and plot:
* Create datasets (*create_data.sh*)
* Run (*run_experiments.sh*)
* Plot (*do_plots.sh*)

Contact
-------
Nikos Tziavelis, ntzia@cslab.ece.ntua.gr
