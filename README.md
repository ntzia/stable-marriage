Details
-------
Implementation of algorithms that solve the stable marriage problem. 
(https://en.wikipedia.org/wiki/Stable_marriage_problem)

Algorithms implemented from the literature:
* Gale-Shapley  
[https://www.researchgate.net/publication/228108175_College_Admissions_and_Stability_of_Marriage]
	* GS_MaleOpt produces the optimal solution for men
	* GS_FemaleOpt produces the optimal solution for women
* MinRegret   
[https://dl.acm.org/citation.cfm?id=23802]
* MinEgalitarian  
[https://dl.acm.org/citation.cfm?id=28871]
* Approx  
[https://dl.acm.org/citation.cfm?id=1868239]
* DACC (2 variants, controlled with -s)  
[https://dl.acm.org/citation.cfm?id=2940727]
	* DACC_R (-s R) chooses the sequence of proposals randomly
	* DACC_D (-s D) chooses the sequence of proposals based on which side is "losing" (as PowerBalance does)
* ESMA  
[https://dl.acm.org/citation.cfm?id=2921985&preflayout=flat]
* Lotto  
[https://dl.acm.org/citation.cfm?id=593304]
* ROM  
[https://link.springer.com/article/10.1007/BF01211824]
* EROM  
[https://link.springer.com/article/10.1007/s11238-005-6846-0]
* Swing  
[https://dl.acm.org/citation.cfm?id=2485203]
* SML2  
[https://www.researchgate.net/publication/286062161_Local_Search_Approaches_in_Stable_Matching_Problems]
* BiLS  
[https://www.researchgate.net/publication/312256504_A_Bidirectional_Local_Search_for_the_Stable_Marriage_Problem]
* Better/Best Response Dynamics  
[https://dl.acm.org/citation.cfm?id=1386831]

Novel algorithms implemented:
* PDB, EDS, LDS: proposal-based approaches that terminate by monotonically increasing content couples
* PowerBalance: proposal-based algorithm that tries to keep a good balance between both sides and then terminates by a compromising procedure
* Hybrid, HybridMultiSearch: combinations of PowerBalance with the local search method

* iBiLS (refined version with rotations) [https://www.researchgate.net/publication/312256504_A_Bidirectional_Local_Search_for_the_Stable_Marriage_Problem]

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

Dependencies
-----
You need maven to build the project:
* sudo apt-get install maven

For the plotting scripts you need:

* pip install numpy
* pip install pandas
* pip install seaborn
* sudo apt-get install python-tk
* sudo apt-get install texlive-full

Tested on Ubuntu 18.04.

Contact
-------
Nikos Tziavelis, ntziavelis@gmail.com
