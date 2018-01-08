#!/usr/bin/env python

import sys
import os
import numpy as np

## We need matplotlib:
## $ apt-get install python-matplotlib
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt


if len(sys.argv) < 9:
	print "Usage: " + sys.argv[0] + " inputFile saveName minN maxN stepN parseNum yAxisName (implementation|implementations)" 
	sys.exit(1)

# Fonts for paper
font = {'size'   : 20}
matplotlib.rc('font', **font)

minN = int(sys.argv[3])
maxN = int(sys.argv[4])
stepN = int(sys.argv[5])

parseNum = int(sys.argv[6])
ylabel = sys.argv[7]

x_Axis = np.arange(minN,maxN + 1,stepN)

data = []
impl_names = []
impl_count = len(sys.argv) - 8
for i in range(impl_count):
	impl_names.append(sys.argv[i+8]) 
	data.append([])

for i in range(impl_count):

	for curr_n in x_Axis:
		fp1 = open(sys.argv[1])
		flag = False

		line = fp1.readline()
		while line:

			if line.startswith("Size="):
				if line.startswith("Size= " + str(curr_n)):
					flag = True
				else:
					flag = False

			if line.startswith("gr.ntua.cslab.algorithms." + impl_names[i] + ":") and flag:
				tokens = line.split()
				data[i].append(float(tokens[parseNum]))
				break

			line = fp1.readline()

		if (flag != True):
			print "Error: Couldn't find " + str(curr_n) + " in " + sys.argv[1]

		fp1.close()


lns = []

# Change y-scale for paper
if (parseNum == 7):
	for i in range(impl_count):
		for j in range(len(data[i])):
			data[i][j] = data[i][j] / 100000
	ylabel = ylabel + " ($*10^5$)"
if (parseNum == 12):
	for i in range(impl_count):
		for j in range(len(data[i])):
			data[i][j] = data[i][j] / 100
	ylabel = ylabel + " ($*10^2$)"

fig, ax1 = plt.subplots()
#figlegend = plt.figure(figsize=(15,0.5))
ax1.grid(True)
ax1.set_xlabel("n")
xAx = np.arange(len(x_Axis))
ax1.xaxis.set_ticks(np.arange(0, len(x_Axis), 1))
ax1.set_xticklabels(x_Axis)
ax1.set_ylabel(ylabel)


# Set limits for y
ymin = sys.maxsize
ymax = 0
for i in range(impl_count):
	if (min(data[i]) < ymin): ymin = min(data[i])
	if (max(data[i]) > ymax): ymax = max(data[i])

ax1.set_autoscaley_on(False)
ax1.set_ylim([ymin, ymax])

# Colors and markers
color_list = ['blue', 'red', 'yellow', 'saddlebrown', 'darkgreen', 'cyan', 'magenta', 'black']
markfacecollist = ['blue', 'red', 'yellow', 'saddlebrown', 'darkgreen', 'none', 'none', 'black']
markers = ['<', '>', '^', 'v', 'D', 's', 'o', '*', '_']
markersizeList = [9, 9, 9, 9, 7, 12, 12, 9, 10]

# Rename for paper
for impl in impl_names:
	if (impl == "PDB_MassProp"): impl_names[impl_names.index(impl)] = "PDB"
	if (impl == "EDS_MassProp"): impl_names[impl_names.index(impl)] = "EDS"
	if (impl == "LDS_MassProp"): impl_names[impl_names.index(impl)] = "LDS"
	if (impl == "GS_MaleOpt"): impl_names[impl_names.index(impl)] = "MaleOpt"
	if (impl == "GS_FemaleOpt"): impl_names[impl_names.index(impl)] = "FemOpt"
	#if (impl == "CaC_2"): impl_names[impl_names.index(impl)] = "CaC"

if (parseNum == 9 or parseNum == 2 or parseNum == 13 or parseNum == 15):
	for i in range(impl_count):
		lns += ax1.semilogy(data[i], label=impl_names[i], color=color_list[i], marker=markers[i], fillstyle='full', markeredgecolor=color_list[i], markersize=markersizeList[i], markerfacecolor=markfacecollist[i])
else:
	for i in range(impl_count):
		lns += ax1.plot(data[i], label=impl_names[i], color=color_list[i], marker=markers[i], fillstyle='full', markeredgecolor=color_list[i], markersize=markersizeList[i], markerfacecolor=markfacecollist[i])

labs = [l.get_label() for l in lns]

#plt.savefig(sys.argv[3] + ".pdf", format="pdf", bbox_inches="tight")
plt.savefig(sys.argv[2] + ".svg", format="svg", bbox_inches="tight")

