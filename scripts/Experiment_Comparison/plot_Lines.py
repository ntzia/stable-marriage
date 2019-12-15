#!/usr/bin/env python

import sys
import os
import numpy as np
import math
import seaborn as sns

## We need matplotlib:
## $ apt-get install python-matplotlib
import matplotlib
#matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.ticker import ScalarFormatter
from matplotlib.ticker import FormatStrFormatter

if len(sys.argv) < 6:
	print "Usage: " + sys.argv[0] + " inputFile titleName saveName parseNum yAxisName (implementation|implementations)" 
	sys.exit(1)

if sys.argv[4] == "Balance":
	bal = True
else:
	bal = False
	parseNum = int(sys.argv[4])

ylabel = sys.argv[5]

x_Axis = [250, 500, 1000, 2000, 4000]

if (sys.argv[4] == "2"): ratio = False
else: ratio = True

data = []
impl_names = []
impl_count = len(sys.argv) - 6
for i in range(impl_count):
	impl_names.append(sys.argv[i+6]) 
	data.append([])

if "PolyMin" in impl_names:
	egal_list = []
	regret_list = []

# find gs value if ratio is requested
if ratio:
	gs = []
	for curr_n in x_Axis:

			gsval = -1.0

			fp1 = open(sys.argv[1])
			flag = False

			line = fp1.readline()
			while line:

				if line.startswith("Size="):
					if line.startswith("Size= " + str(curr_n)):
						flag = True
					else:
						flag = False

				if line.startswith("GS_MaleOpt:") and flag:
					tokens = line.split()
					if bal:
						val = (float(tokens[7]) + float(tokens[9])) / 2.0
					else:
						val = float(tokens[parseNum])
					if (gsval == -1.0 or val < gsval):
						gsval = val
					flag = False

				if line.startswith("GS_FemaleOpt:") and flag:
					tokens = line.split()
					if bal:
						val = (float(tokens[7]) + float(tokens[9])) / 2.0
					else:
						val = float(tokens[parseNum])
					if (gsval == -1 or val < gsval):
						gsval = val
					flag = False

				line = fp1.readline()

			fp1.close()
			gs.append(gsval)

for i in range(impl_count):

	for curr_n in x_Axis:
		fp1 = open(sys.argv[1])
		flag = False

		# EROM D 8000
		if (impl_names[i] == "EROM" and curr_n == 8000 and sys.argv[2] == "D Time"):
			data[i].append(12000)
			continue
		if (impl_names[i] == "EROM" and curr_n == 8000 and (sys.argv[2] == "D SEq" or sys.argv[2] == "D Bal")):
			data[i].append(np.nan)
			continue
		# EROM G 8000
		if (impl_names[i] == "EROM" and curr_n == 8000 and sys.argv[2] == "G Time"):
			data[i].append(1725)
			continue
		if (impl_names[i] == "EROM" and curr_n == 8000 and (sys.argv[2] == "G SEq" or sys.argv[2] == "G Bal")):
			data[i].append(np.nan)
			continue
		# EROM UD 8000
		if (impl_names[i] == "EROM" and curr_n == 8000 and sys.argv[2] == "UD Time"):
			data[i].append(12000)
			continue
		if (impl_names[i] == "EROM" and curr_n == 8000 and (sys.argv[2] == "UD SEq" or sys.argv[2] == "UD Bal")):
			data[i].append(np.nan)
			continue		


		line = fp1.readline()
		while line:

			if line.startswith("Size="):
				if line == ("Size= " + str(curr_n) + "\n"):
					flag = True
				else:
					flag = False

			if line.startswith(impl_names[i] + ":") and flag:
				tokens = line.split()
				if bal:
					val = (float(tokens[7]) + float(tokens[9])) / 2.0
				else:
					val = float(tokens[parseNum])
				data[i].append(val)
				break

			line = fp1.readline()

		fp1.close()

# compute ratios if needed
'''
if (sys.argv[2] == "U Bal"):
	for i in range(impl_count):
		for j in range(len(data[i])):
			data[i][j] = data[i][j] * math.log(x_Axis[j], 2) * math.log(x_Axis[j], 2) / (gs[j])
elif (sys.argv[2] == "D Bal"):
	for i in range(impl_count):
		for j in range(len(data[i])):
			data[i][j] = data[i][j]  / (gs[j])
elif (sys.argv[2] == "G Bal"):
	for i in range(impl_count):
		for j in range(len(data[i])):
			data[i][j] = data[i][j] * math.log(x_Axis[j], 2) / (gs[j])
elif (sys.argv[2] == "G Bal"):
	for i in range(impl_count):
		for j in range(len(data[i])):
			data[i][j] = data[i][j] / (gs[j])
'''

'''
if ratio:
	for i in range(impl_count):
		for j in range(len(data[i])):
			data[i][j] = data[i][j] / gs[j]
'''

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

sns.set_context("paper", font_scale=1.7)
sns.set_style("whitegrid")
sns.set_palette("bright")

fig, ax1 = plt.subplots()
x = np.arange(0, len(x_Axis), 1)


# Set limits for y

ymin = sys.maxsize
ymax = 0
for i in range(impl_count):
	if (min(data[i]) < ymin): ymin = min(data[i])
	if (max(data[i]) > ymax): ymax = max(data[i])
ybotlim = ymin - (1.0 / 10) * (ymax - ymin)
ytoplim = ymax + (1.0 / 10) * (ymax - ymin)



#ax1.get_yaxis().set_minor_formatter(FormatStrFormatter("%.20f"))
'''
# Custom for paper symmetry
if (sys.argv[2] == "Mean Egalitarian Cost\nUniform Distribution"):
	ymax = 10
'''


#ax1.set_autoscaley_on(False)
#ax1.set_ylim([ybotlim, ytoplim])


# Colors and markers

color_list = ['blue', 'red', 'green', 'saddlebrown', 'darkorange', 'black', 'cyan', 'crimson', 'gold', 'purple', 'darkcyan']
color_list = sns.color_palette()
markfacecollist = ['blue', 'none', 'green', 'saddlebrown', 'none', 'none', 'cyan', 'crimson', 'gold', 'purple', 'darkcyan']
markfacecollist = sns.color_palette()
markfacecollist[2] = 'none'
markfacecollist[5] = 'none'
markfacecollist[8] = 'none'
markers = ['*', '<', 'D', '>', '^', 's', 'v', 'o', 'p', 'x', 'H']
markersizeList = [18, 13, 14, 13, 13, 17, 13, 14, 23, 17, 13]


for i in range(impl_count):
	sns_plot = sns.lineplot(x=x, y=data[i], label=impl_names[i], marker=markers[i], fillstyle='full', markeredgecolor=color_list[i], markersize=markersizeList[i], markerfacecolor=markfacecollist[i])

sns_plot.xaxis.set_ticks(x)
sns_plot.set_xticklabels(x_Axis)

sns_plot.set_yscale('log')
sns_plot.legend_.remove()
sns_plot.set(xlabel="n", ylabel=ylabel)

if (sys.argv[2].endswith("Time")):
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([0.01, 150])
	ax1.set_yticks([0.01, 0.1, 1, 10, 100])
	#ax1.get_yaxis().set_major_formatter(FormatStrFormatter("%.2f"))


plt.savefig(sys.argv[3] + ".pdf", format="pdf", bbox_inches="tight")

## Legend
plt.rc('text', usetex=True)  
plt.rc('font', family='serif', size=20) 

h, l = sns_plot.get_legend_handles_labels()
for i in range(len(l)):
	if l[i] == "PowerBalance_SEq": l[i] = "PowerBalance"
	if l[i] == "PowerBalance_Bal": l[i] = "PowerBalance"
	if (l[i] == "iBiLS_SEq_0.125"): l[i] = "iBiLS"
	if (l[i] == "iBiLS_Bal_0.125"): l[i] = "iBiLS"
	if (l[i] == "BiLS_SEq_0.0"): l[i] = "BiLS"
	if (l[i] == "BiLS_Bal_0.0"): l[i] = "BiLS"
	if (l[i] == "DACC_D"): l[i] = "DACC"
	if (l[i] == "DACC_R"): l[i] = "DACC\_R"
	if (l[i] == "Hybrid_SEq"): l[i] = "Hybrid"
	if (l[i] == "Hybrid_Bal"): l[i] = "Hybrid"
	if (l[i] == "HybridMultiSearch_SEq"): l[i] = "HybridMultiSearch"
	if (l[i] == "HybridMultiSearch_Bal"): l[i] = "HybridMultiSearch"

figlegend = plt.figure(figsize=(4 * len(impl_names), 0.5))
ax_leg = figlegend.add_subplot(111)
ax_leg.legend(h, l, loc='center', ncol=len(impl_names), fancybox=True, shadow=True, prop={'size':30})
ax_leg.axis('off')
figlegend.savefig(os.path.dirname(sys.argv[3]) + '/legend.pdf', format="pdf", bbox_inches="tight")