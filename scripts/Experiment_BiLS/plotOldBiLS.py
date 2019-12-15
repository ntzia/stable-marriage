#!/usr/bin/env python

import sys
import os
import numpy as np

## We need matplotlib:
## $ apt-get install python-matplotlib
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt


if len(sys.argv) < 6:
	print "Usage: " + sys.argv[0] + " inputFile titleName saveName parseNum yAxisName (prob|probs)" 
	sys.exit(1)

# Fonts for paper
font = {'size'   : 20}
matplotlib.rc('font', **font)


parseNum = int(sys.argv[4])
ylabel = sys.argv[5]

n_list = [500, 1000, 1500]

data = []
prob_list = []
prob_count = len(sys.argv) - 6
for i in range(prob_count):
	prob_list.append(sys.argv[i+6]) 
	data.append([])
x_Axis = []
for p in prob_list:
	x_Axis.append(float(p))

for i in range(len(n_list)):

	curr_n = n_list[i]

	for prob in prob_list:
		fp1 = open(sys.argv[1])
		flag = False

		line = fp1.readline()
		while line:

			if line.startswith("Size="):
				if line == ("Size= " + str(curr_n) + "\n"):
					flag = True
				else:
					flag = False

			if line.startswith("BiLS_SEq_" + prob + ":") and flag:
				tokens = line.split()
				data[i].append(float(tokens[parseNum]))
				break

			line = fp1.readline()

		if (flag != True):
			print "Error: Couldn't find " + str(curr_n) + " in " + sys.argv[1]

		fp1.close()


lns = []

# Change y-scale for paper
'''
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
'''

fig, ax1 = plt.subplots()
ax1.grid(True)
ax1.set_xlabel("Probability")
xAx = np.arange(len(x_Axis))
ax1.xaxis.set_ticks(np.arange(0, len(x_Axis), 1))
ax1.set_xticklabels(x_Axis)
ax1.set_ylabel(ylabel)

for label in ax1.get_xticklabels()[1::2]:
    label.set_visible(False)


# Set limits for y
#ymin = sys.maxsize
#ymax = 0
#for i in range(prob_count):
#	if (min(data[i]) < ymin): ymin = min(data[i])
#	if (max(data[i]) > ymax): ymax = max(data[i])
#ytop = ymax + (1.0/100) * (ymax - ymin)
#ybot = ymin - (1.0/100) * (ymax - ymin)

'''
# Custom for paper symmetry
if (sys.argv[2] == "Mean Egalitarian Cost\nUniform Distribution"):
	ymax = 10
'''
#ax1.set_yscale('log', basey=2)

#ax1.set_autoscaley_on(False)
#ax1.set_ylim([ybot, ytop])

# Colors and markers
color_list = ['blue', 'red', 'green', 'saddlebrown', 'orange', 'black', 'cyan', 'magenta', 'orange', 'darkviolet', 'darkcyan']
markfacecollist = ['blue', 'none', 'green', 'saddlebrown', 'none', 'black', 'none', 'none', 'none', 'darkviolet', 'darkcyan']
markers = ['*', 'o', 'D', 'x', 's', '*', 's', 'o', 'p', 'x', 'H']
markersizeList = [15, 15, 11, 16, 15, 11, 14, 14, 14, 12, 11]

# Rename for paper
#for impl in impl_names:
#	if (impl == "PDB_MassProp"): impl_names[impl_names.index(impl)] = "PDB"
#	if (impl == "EDS_MassProp"): impl_names[impl_names.index(impl)] = "EDS"
#	if (impl == "LDS_MassProp"): impl_names[impl_names.index(impl)] = "LDS"
#	if (impl == "GS_MaleOpt"): impl_names[impl_names.index(impl)] = "MaleOpt"
#	if (impl == "GS_FemaleOpt"): impl_names[impl_names.index(impl)] = "FemOpt"
#	if (impl == "CaC_2"): impl_names[impl_names.index(impl)] = "CaC"


for i in range(len(n_list)):
		lns += ax1.plot(data[i], label="$n=" + str(n_list[i]) + "$", color=color_list[i], marker=markers[i], fillstyle='full', markeredgecolor=color_list[i], markersize=markersizeList[i], markerfacecolor=markfacecollist[i])


labs = [l.get_label() for l in lns]

#plt.title(sys.argv[2])
plt.savefig(sys.argv[3] + ".pdf", format="pdf", bbox_inches="tight")


## Legend
handles, labels = ax1.get_legend_handles_labels()
plt.cla()
figlegend = plt.figure(figsize=(10,0.5))

leg = figlegend.legend(handles, labels, ncol=len(n_list), fancybox=True, shadow=True, prop={'size':30})
leg.get_frame().set_facecolor('lightgrey')
figlegend.savefig(os.path.dirname(sys.argv[3]) + '/legend.pdf', format="pdf", bbox_inches="tight")