#!/usr/bin/env python

import sys
import os
import numpy as np

## We need matplotlib:
## $ apt-get install python-matplotlib
import matplotlib
matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib import cm
from matplotlib.ticker import ScalarFormatter
from matplotlib.ticker import FormatStrFormatter

if len(sys.argv) < 6:
	print "Usage: " + sys.argv[0] + " inputFile titleName saveName parseNum yAxisName (implementation|implementations)" 
	sys.exit(1)

# Fonts for paper
font = {'size'   : 20}
matplotlib.rc('font', **font)

if sys.argv[4] == "Balance":
	bal = True
else:
	bal = False
	parseNum = int(sys.argv[4])

ylabel = sys.argv[5]

x_Axis = [50, 100, 150, 200]

if (sys.argv[4] == "2"): ratio = False
else: ratio = True

data = []
impl_names = []
impl_count = len(sys.argv) - 6
for i in range(impl_count):
	impl_names.append(sys.argv[i+6]) 
	data.append([])

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


		# ROM U 16000
		if (impl_names[i] == "ROM" and curr_n == 16000 and sys.argv[2] == "U Time"):
			data[i].append(2408)
			continue
		if (impl_names[i] == "ROM" and curr_n == 16000 and (sys.argv[2] == "U SEq" or sys.argv[2] == "U Bal")):
			data[i].append(np.nan)
			continue
		# Lotto U 16000
		if (impl_names[i] == "Lotto" and curr_n == 16000 and sys.argv[2] == "U Time"):
			data[i].append(1233)
			continue
		if (impl_names[i] == "Lotto" and curr_n == 16000 and (sys.argv[2] == "U SEq" or sys.argv[2] == "U Bal")):
			data[i].append(np.nan)
			continue


		# EROM D 16000
		if (impl_names[i] == "EROM" and curr_n == 16000 and sys.argv[2] == "D Time"):
			data[i].append(100000)
			continue
		if (impl_names[i] == "EROM" and curr_n == 16000 and (sys.argv[2] == "D SEq" or sys.argv[2] == "D Bal")):
			data[i].append(np.nan)
			continue
		# ROM D 16000
		if (impl_names[i] == "ROM" and curr_n == 16000 and sys.argv[2] == "D Time"):
			data[i].append(4105)
			continue
		if (impl_names[i] == "ROM" and curr_n == 16000 and (sys.argv[2] == "D SEq" or sys.argv[2] == "D Bal")):
			data[i].append(np.nan)
			continue
		# DACC_R D 16000
		if (impl_names[i] == "DACC_R" and curr_n == 16000 and sys.argv[2] == "D Time"):
			data[i].append(2833)
			continue
		if (impl_names[i] == "DACC_R" and curr_n == 16000 and (sys.argv[2] == "D SEq" or sys.argv[2] == "D Bal")):
			data[i].append(np.nan)
			continue
		# Lotto D 16000
		if (impl_names[i] == "Lotto" and curr_n == 16000 and sys.argv[2] == "D Time"):
			data[i].append(1188)
			continue
		if (impl_names[i] == "Lotto" and curr_n == 16000 and (sys.argv[2] == "D SEq" or sys.argv[2] == "D Bal")):
			data[i].append(np.nan)
			continue


		# EROM G 16000
		if (impl_names[i] == "EROM" and curr_n == 16000 and sys.argv[2] == "G Time"):
			data[i].append(10000)
			continue
		if (impl_names[i] == "EROM" and curr_n == 16000 and (sys.argv[2] == "G SEq" or sys.argv[2] == "G Bal")):
			data[i].append(np.nan)
			continue
		# ROM G 16000
		if (impl_names[i] == "ROM" and curr_n == 16000 and sys.argv[2] == "G Time"):
			data[i].append(2490)
			continue
		if (impl_names[i] == "ROM" and curr_n == 16000 and (sys.argv[2] == "G SEq" or sys.argv[2] == "G Bal")):
			data[i].append(np.nan)
			continue
		# DACC_R G 16000
		if (impl_names[i] == "DACC_R" and curr_n == 16000 and sys.argv[2] == "G Time"):
			data[i].append(7217)
			continue
		if (impl_names[i] == "DACC_R" and curr_n == 16000 and (sys.argv[2] == "G SEq" or sys.argv[2] == "G Bal")):
			data[i].append(np.nan)
			continue
		# Lotto G 16000
		if (impl_names[i] == "Lotto" and curr_n == 16000 and sys.argv[2] == "G Time"):
			data[i].append(1261)
			continue
		if (impl_names[i] == "Lotto" and curr_n == 16000 and (sys.argv[2] == "G SEq" or sys.argv[2] == "G Bal")):
			data[i].append(np.nan)
			continue


		# ROM UD 16000
		if (impl_names[i] == "ROM" and curr_n == 16000 and sys.argv[2] == "UD Time"):
			data[i].append(5913)
			continue
		if (impl_names[i] == "ROM" and curr_n == 16000 and (sys.argv[2] == "UD SEq" or sys.argv[2] == "UD Bal")):
			data[i].append(np.nan)
			continue
		# EROM UD 16000
		if (impl_names[i] == "EROM" and curr_n == 16000 and sys.argv[2] == "UD Time"):
			data[i].append(100000)
			continue
		if (impl_names[i] == "EROM" and curr_n == 16000 and (sys.argv[2] == "UD SEq" or sys.argv[2] == "UD Bal")):
			data[i].append(np.nan)
			continue

		# Swing D-G-UD 4000
		if (impl_names[i] == "Swing" and curr_n == 4000 and (sys.argv[2] == "D Time" or sys.argv[2] == "G Time" or sys.argv[2] == "UD Time")):
			data[i].append(1500)
			continue
		if (impl_names[i] == "Swing" and curr_n == 4000 and (sys.argv[2] == "D SEq" or sys.argv[2] == "G SEq" or sys.argv[2] == "UD SEq")):
			data[i].append(np.nan)
			continue
		if (impl_names[i] == "Swing" and curr_n == 4000 and (sys.argv[2] == "D Bal" or sys.argv[2] == "G Bal" or sys.argv[2] == "UD Bal")):
			data[i].append(np.nan)
			continue	
		# Swing Rest
		if (impl_names[i] == "Swing" and curr_n > 4000):
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

		if (flag != True):
			print "Error: Couldn't find " + impl_names[i] + " in " + str(curr_n) + " " + sys.argv[1]

		fp1.close()

# compute ratios if needed
if ratio:
	for i in range(impl_count):
		for j in range(len(data[i])):
			data[i][j] = data[i][j] / gs[j]


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
ax1.set_xlabel("n")
ax1.set_ylabel(ylabel)
ax1.xaxis.set_ticks(np.arange(0, len(x_Axis), 1))
ax1.set_xticklabels(x_Axis)


# Set limits for y
ymin = sys.maxsize
ymax = 0
for i in range(impl_count):
	if (min(data[i]) < ymin): ymin = min(data[i])
	if (max(data[i]) > ymax): ymax = max(data[i])
ybotlim = ymin - (1.0 / 10) * (ymax - ymin)
ytoplim = ymax + (1.0 / 10) * (ymax - ymin)

ax1.set_yscale('log', basey=10)
ax1.get_yaxis().set_major_formatter(ScalarFormatter())
#ax1.get_yaxis().set_minor_formatter(FormatStrFormatter("%.20f"))
'''
# Custom for paper symmetry
if (sys.argv[2] == "Mean Egalitarian Cost\nUniform Distribution"):
	ymax = 10
'''


#ax1.set_autoscaley_on(False)
#ax1.set_ylim([ybotlim, ytoplim])


# Colors and markers
cm_subsection = np.linspace(0, 1, impl_count) 
colors = [ cm.nipy_spectral(x) for x in cm_subsection ]

color_list = ['blue', 'red', 'green', 'saddlebrown', 'darkorange', 'black', 'cyan', 'crimson', 'gold', 'purple', 'darkcyan']
markfacecollist = ['blue', 'none', 'green', 'saddlebrown', 'none', 'none', 'cyan', 'crimson', 'gold', 'purple', 'darkcyan']
markers = ['*', 'o', 'D', '^', 's', 'p', 'v', '<', '>', 'x', 'H']
markersizeList = [15, 15, 11, 13, 15, 16, 13, 13, 13, 15, 11]


for i in range(impl_count):
	lns += ax1.plot(data[i], label=impl_names[i], color=color_list[i], marker=markers[i], fillstyle='full', markeredgecolor=color_list[i], markersize=markersizeList[i], markerfacecolor=markfacecollist[i])

'''
if (sys.argv[2] == "U Bal"):
	print "U Bal"
	ytoplim = 1.1
	ybotlim = 0.07
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([ybotlim, ytoplim])
	#ax1.get_yaxis().set_minor_formatter(FormatStrFormatter("%.2f"))
	ax1.set_yticks([0.1, 0.2, 0.3, 0.4, 0.5, 1.0])
'''

labs = [l.get_label() for l in lns]

#plt.title(sys.argv[2])
plt.savefig(sys.argv[3] + ".pdf", format="pdf", bbox_inches="tight")

## Legend
plt.rc('text', usetex=True)  
plt.rc('font', family='serif', size=20) 

for impl in impl_names:
	if (impl == "BiLS_Rot_SEq_0.125"): impl_names[impl_names.index(impl)] = "BiLS"
	if (impl == "BiLS_Rot_Bal_0.125"): impl_names[impl_names.index(impl)] = "BiLS"
	if (impl == "PowerBalance_SEq"): impl_names[impl_names.index(impl)] = "PowerBalance"
	if (impl == "PowerBalance_Bal"): impl_names[impl_names.index(impl)] = "PowerBalance"
	if (impl == "DACC_D"): impl_names[impl_names.index(impl)] = "DACC\_D"
	if (impl == "DACC_R"): impl_names[impl_names.index(impl)] = "DACC\_R"
	if (impl == "Hybrid_Rot_SEq"): impl_names[impl_names.index(impl)] = "Hybrid"
	if (impl == "Hybrid_Rot_Bal"): impl_names[impl_names.index(impl)] = "Hybrid"
	if (impl == "Hybrid_Lookahead_Rot_SEq"): impl_names[impl_names.index(impl)] = "HybridLA"
	if (impl == "Hybrid_Lookahead_Rot_Bal"): impl_names[impl_names.index(impl)] = "HybridLA"
	if (impl == "HybridMultiSearch_SEq"): impl_names[impl_names.index(impl)] = "HybridMultiSearch"
	if (impl == "HybridMultiSearch_Bal"): impl_names[impl_names.index(impl)] = "HybridMultiSearch"

for i in range(len(impl_names)):
	impl_names[i] = r"\textsc{" + impl_names[i] + "}"

plt.cla()
figlegend = plt.figure(figsize=(2 * len(impl_names), 0.5))

leg = figlegend.legend(lns, impl_names, ncol=len(impl_names), fancybox=True, shadow=True, prop={'size':30})
leg.get_frame().set_facecolor('lightgrey')
figlegend.savefig('../plots/legend.pdf', format="pdf", bbox_inches="tight")