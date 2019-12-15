#!/usr/bin/env python

import sys
import os
import numpy as np

if len(sys.argv) < 2:
	print "Usage: " + sys.argv[0] + " (distribution|Distributions)" 
	sys.exit(1)

abbr = {"Uniform" : "U", "Discrete" : "D", "UniformDiscrete" : "UD", "Gauss" : "G" }

for d in sys.argv[1:]:
	
	print(d + ":")
	fm = open("../../results/outputs/Experiment_Approx/" + abbr[d] + 'mean', 'w')

	for file in os.listdir("../../results/outputs/Experiment_Approx/"):

		if file.startswith("out" + abbr[d] + "_"):

			tokens = file.split("_")
			size = int(tokens[1])
			print("\tn = " + str(size) + ":")

			f = open("../../results/outputs/Experiment_Approx/" + file, 'r')
			counters = {}
			times = {}
			ECosts = {}
			SECosts = {}
			RCosts = {}

			line = f.readline()
			while line:

				tokens = line.split(":")
				impl_name = tokens[0]
				tokens = line.split()

				if counters.has_key(impl_name):
					counters[impl_name] = counters[impl_name] + 1
					times[impl_name] = times[impl_name] + float(tokens[2])
					ECosts[impl_name] = ECosts[impl_name] + float(tokens[7])
					SECosts[impl_name] = SECosts[impl_name] + float(tokens[9])
					RCosts[impl_name] = RCosts[impl_name] + float(tokens[11])
				else:
					counters[impl_name] = 1
					times[impl_name] = float(tokens[2])
					ECosts[impl_name] = float(tokens[7])
					SECosts[impl_name] = float(tokens[9])
					RCosts[impl_name] = float(tokens[11])

				line = f.readline()

			for impl in counters:
				print("\t\t" + str(impl) + ": " + str(counters[impl]) + " instances")
				times[impl] = times[impl] / counters[impl]
				ECosts[impl] = ECosts[impl] / counters[impl]
				SECosts[impl] = SECosts[impl] / counters[impl]
				RCosts[impl] = RCosts[impl] / counters[impl]

				fm.write("Size= " + str(size) + "\n")
				fm.write(impl + ": Time= "+str(times[impl])+" secs Rounds= 0 EgalitarianCost= "+str(ECosts[impl])+" SexEqualityCost= "+str(SECosts[impl])+" RegretCost= "+str(RCosts[impl])+"\n")

			f.close()

	fm.close()
