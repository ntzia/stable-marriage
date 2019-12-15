#!/usr/bin/env python

import sys
import os
import numpy as np

if len(sys.argv) < 2:
	print "Usage: " + sys.argv[0] + " (distribution|Distributions)" 
	sys.exit(1)

abbr = {"Uniform" : "U", "Discrete" : "D", "UniformDiscrete" : "UD", "Gauss" : "G" }

start_param = -1

for d in sys.argv[1:]:
	
	print(d + ":")
	fm = open("../../results/outputs/Experiment_PowerBalance/" + abbr[d] + 'mean_B', 'w')

	for file in os.listdir("../../results/outputs/Experiment_PowerBalance/"):

		if file.startswith("out" + abbr[d] + "Bal_"):

			tokens = file.split("_")
			size = int(tokens[1])
			print("\tn = " + str(size) + ":")

			f = open("../../results/outputs/Experiment_PowerBalance/" + file, 'r')
			best_params = []

			p = start_param
			count = 0
			best_cost = sys.maxsize
			best_param = sys.maxsize
			line = f.readline()
			while line:

				tokens = line.split(":")
				impl_name = tokens[0]
				tokens = line.split()
				tokens2 = impl_name.split("_")
				new_p = float(tokens2[2])

				if (new_p < p):
					#print "best param = " + str(best_param)
					#print "New Instance"
					count = count + 1
					best_params.append(best_param)
					if (best_param == p):
						print "Found an instance with best=end_param"
					best_cost = sys.maxsize
					best_param = sys.maxsize

				#print "p=" + str(new_p) + " balance = " + str((float(tokens[9]) + float(tokens[7])) / 2.0)

				p = new_p

				if (((float(tokens[9]) + float(tokens[7])) / 2.0) < best_cost):
					best_cost = (float(tokens[9]) + float(tokens[7])) / 2.0
					best_param = p

				line = f.readline()

			# Last instance
			count = count + 1
			best_params.append(best_param)
			if (best_param == p):
				print "Found an instance with best=end_param"

			# Find mean
			m = np.mean(best_params)

			print("\t\t" + str(count) + " instances")

			fm.write("Size= " + str(size) + "\n")
			fm.write("BestParam= "+str(m) + "\n")

			f.close()

	fm.close()
