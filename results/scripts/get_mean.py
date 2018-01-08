#!/usr/bin/env python

import sys
import os
import numpy as np

if len(sys.argv) != 3:
	print "Usage: " + sys.argv[0] + " NumOfOutputs Directory" 
	sys.exit(1)

n = int(sys.argv[1])
di = sys.argv[2]

fm = open(di + 'mean_output.txt', 'w')

fileNames = []

for i in range(1, n+1):
	fileNames.append(di + "output" + str(i) + ".txt")

files = []
lines = []

for fn in fileNames:
	ftemp = open(fn, 'r')
	files.append(ftemp)

for fi in files:
	lines.append(fi.readline())
while lines[0]:

	if lines[0].startswith("Size="):
		fm.write(lines[0])

	elif "Time" in lines[0]:
		times = []
		rounds = []
		ECosts = []
		SEcosts = []
		RCosts = []
		MEcosts = []
		TEcosts = []
		for line in lines:
			tokens = line.split()
			times.append(float(tokens[2]))
			rounds.append(float(tokens[5]))
			ECosts.append(float(tokens[7]))
			SEcosts.append(float(tokens[9]))
			RCosts.append(float(tokens[11]))
			MEcosts.append(float(tokens[13]))
			TEcosts.append(float(tokens[15]))

		mtime = np.mean(times)
		mrounds = np.mean(rounds)
		mECost = np.mean(ECosts)
		mSEcost = np.mean(SEcosts)
		mRCost = np.mean(RCosts)
		mMECost = np.mean(MEcosts)
		mTECost = np.mean(TEcosts)

		stdtime = np.std(times)
		stdrounds = np.std(rounds)
		stdECost = np.std(ECosts)
		stdSEcost = np.std(SEcosts)
		stdRCost = np.std(RCosts)
		stdMEcost = np.std(MEcosts)
		stdTEcost = np.std(TEcosts)

		fm.write(tokens[0] + " Time= "+str(mtime)+" secs Rounds= "+str(mrounds)+ " EgalitarianCost= "+str(mECost)+" SexEqualityCost= "+str(mSEcost)+" RegretCost= "+str(mRCost)+" MaritalEqualityCost= "+str(mMECost)+" TotalitarianEqualityCost= "+str(mTECost)+"\n")
	
	i = 0
	for fi in files:
		lines[i] = fi.readline()
		i = i + 1

for fi in files:
	fi.close();

fm.close();
