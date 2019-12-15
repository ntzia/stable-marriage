#!/usr/bin/env python

import sys
import os
import numpy as np
import math

## We need matplotlib:
## $ apt-get install python-matplotlib
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt


if len(sys.argv) != 4:
	print "Usage: " + sys.argv[0] + " inputFileSE inputFileB saveName" 
	sys.exit(1)

# Fonts for paper
font = {'size'   : 20}
matplotlib.rc('font', **font)

x_Axis = [250, 500, 1000, 2000, 4000]

minParamValues_SE = []
for i in range(len(x_Axis)):
	curr_n = x_Axis[i]

	fp1 = open(sys.argv[1])
	flag = False

	line = fp1.readline()
	while line:

		if line.startswith("Size="):
			if line == ("Size= " + str(curr_n) + "\n"):
				flag = True
			else:
				flag = False

		if line.startswith("BestParam=") and flag:
			tokens = line.split()
			val = float(tokens[1])
			minParamValues_SE.append(val)
			break


		line = fp1.readline()

	if (flag != True):
		print "Error: Couldn't find " + str(curr_n) + " in " + sys.argv[1]

	fp1.close()


minParamValues_B = []
for i in range(len(x_Axis)):
	curr_n = x_Axis[i]

	fp1 = open(sys.argv[2])
	flag = False

	line = fp1.readline()
	while line:

		if line.startswith("Size="):
			if line == ("Size= " + str(curr_n) + "\n"):
				flag = True
			else:
				flag = False

		if line.startswith("BestParam=") and flag:
			tokens = line.split()
			val = float(tokens[1])
			minParamValues_B.append(val)
			break


		line = fp1.readline()

	if (flag != True):
		print "Error: Couldn't find " + str(curr_n) + " in " + sys.argv[2]

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
lns = []
ax1.grid(True)
ax1.set_xlabel("n")
xAx = np.arange(len(x_Axis))
ax1.xaxis.set_ticks(np.arange(0, len(x_Axis), 1))
ax1.set_xticklabels(x_Axis)
ax1.set_ylabel("#Rounds / n")


# Custom
if (sys.argv[1].startswith("outputs/UDmean")):
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([-0.5, 18])


# Colors and markers
color_list = ['blue', 'red', 'green', 'saddlebrown', 'orange', 'black', 'cyan', 'magenta', 'orange', 'darkviolet', 'darkcyan']
markfacecollist = ['blue', 'none', 'green', 'saddlebrown', 'none', 'black', 'none', 'none', 'none', 'darkviolet', 'darkcyan']
markers = ['*', 'o', 'D', 'x', 's', '*', 's', 'o', 'p', 'x', 'H']
markersizeList = [15, 15, 11, 16, 15, 11, 14, 14, 14, 12, 11]
names = ["$OptSEq$", "$OptBal$", "$\logn$", "$\log^2{n} / 10$", "$\log^3{n} / 100$"]


lns += ax1.plot(minParamValues_SE, color=color_list[0], marker=markers[0], fillstyle='full', markeredgecolor=color_list[0], markersize=markersizeList[0], markerfacecolor=markfacecollist[0])
lns += ax1.plot(minParamValues_B, color=color_list[1], marker=markers[1], fillstyle='full', markeredgecolor=color_list[1], markersize=markersizeList[1], markerfacecolor=markfacecollist[1])

loglist = []
for i in x_Axis:
	loglist.append(math.log(i, 2))
lns += ax1.plot(loglist, color=color_list[2], marker=markers[2], fillstyle='full', markeredgecolor=color_list[2], markersize=markersizeList[2], markerfacecolor=markfacecollist[2])

log2_list = []
for i in x_Axis:
	log2_list.append(math.log(i, 2) * math.log(i, 2) / 10.0)
lns += ax1.plot(log2_list, color=color_list[3], marker=markers[3], fillstyle='full', markeredgecolor=color_list[3], markersize=markersizeList[3], markerfacecolor=markfacecollist[3])

#sqrt_list = []
#for i in x_Axis:
#	sqrt_list.append(math.sqrt(i) / 3.0)
#lns += ax1.plot(sqrt_list, color='yellow', marker='*', fillstyle='full', markeredgecolor='yellow', markersize=10, markerfacecolor='yellow')

log3list = []
for i in x_Axis:
	log3list.append(math.log(i, 2) * math.log(i, 2) * math.log(i, 2) / 100.0)
lns += ax1.plot(log3list, color=color_list[4], marker=markers[4], fillstyle='full', markeredgecolor=color_list[4], markersize=markersizeList[4], markerfacecolor=markfacecollist[4])

#plt.title(sys.argv[2])
plt.savefig(sys.argv[3] + ".pdf", format="pdf", bbox_inches="tight")
plt.savefig(sys.argv[3] + ".svg", format="svg", bbox_inches="tight")



## Legend
plt.cla()
figlegend = plt.figure(figsize=(12,0.5))

leg = figlegend.legend(lns, names, ncol=5, fancybox=True, shadow=True, prop={'size':30})
leg.get_frame().set_facecolor('lightgrey')
figlegend.savefig(os.path.dirname(sys.argv[3]) + '/legend.pdf', format="pdf", bbox_inches="tight")