#!/usr/bin/env python

import sys
import os
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns
from matplotlib.ticker import ScalarFormatter
from matplotlib.ticker import FormatStrFormatter


if len(sys.argv) < 6:
	print "Usage: " + sys.argv[0] + " distribution titleName saveName parseNum yAxisName (implementation|implementations)" 
	sys.exit(1)

dist = sys.argv[1]
saveName = sys.argv[3]
yAxisName = sys.argv[5]

if sys.argv[4] == "Balance":
	bal = True
else:
	bal = False
	parseNum = int(sys.argv[4])

if (sys.argv[4] == "Balance"):
	x_Axis = [2000, 4000]
	number_of_instances = [50, 50]
	ratio = True
else:
	x_Axis = [2000, 4000]
	number_of_instances = [50, 50, 50]
	ratio = True

data = {}
data["n"] = []
impl_names = []
impl_count = len(sys.argv) - 6
for i in range(impl_count):
	impl_names.append(sys.argv[i+6])
	data[sys.argv[i+6]] = []

# find gs value if ratio is requested
if ratio:
	best_gs_val = {}

	for curr_n in x_Axis:
		gs_men = []
		gs_women = []

		fp1 = open("../../results/outputs/Experiment_Comparison/out" + dist + "_" + str(curr_n))
		line = fp1.readline()
		while line:
			if line.startswith("GS_MaleOpt:"):
				tokens = line.split()
				if bal:
					val = (float(tokens[7]) + float(tokens[9])) / 2.0
				else:
					val = float(tokens[parseNum])
				gs_men.append(val)

			if line.startswith("GS_FemaleOpt:"):
				tokens = line.split()
				if bal:
					val = (float(tokens[7]) + float(tokens[9])) / 2.0
				else:
					val = float(tokens[parseNum])
				gs_women.append(val)

			line = fp1.readline()
		fp1.close()

		# For each n, find the best mean value
		men_mean = np.mean(gs_men)
		women_mean = np.mean(gs_women)
		if (men_mean <= women_mean): best_gs_val[curr_n] = men_mean
		else: best_gs_val[curr_n] = women_mean

# Now scan the files for the values of each impl
for j in range(len(x_Axis)):
	curr_n = x_Axis[j]
	partial_data = {}
	for impl in impl_names:
		partial_data[impl] = []
	if "PolyMin" in impl_names:
		egal_list = []
		regret_list = []
	
	fp1 = open("../../results/outputs/Experiment_Comparison/out" + dist + "_" + str(curr_n))
	line = fp1.readline()
	while line:
		if "Time=" in line:
			tokens = line.split()
			impl = tokens[0][:-1]
			if impl in impl_names:
				if bal:
					val = (float(tokens[7]) + float(tokens[9])) / 2.0
				else:
					val = float(tokens[parseNum])
				if ratio: val = val / best_gs_val[curr_n]
				partial_data[impl].append(val)
		line = fp1.readline()
	fp1.close()

	# Verify that for each impl, we got the same number of values
	print "n=" + str(curr_n) + ":"
	for impl in impl_names:
		print "\t\t" + impl + " : " + str(len(partial_data[impl])) + " instances"
		if (len(partial_data[impl]) != number_of_instances[j]):
			# Check if the desired number of instances is a factor of the number of values 
			if (len(partial_data[impl]) % number_of_instances[j] == 0):
				### Assume that for each instance, the values are consecutive
				multiple = len(partial_data[impl]) / number_of_instances[j]
				partial_data[impl] = [sum(group) * 1.0 / multiple for group in zip(*[iter(partial_data[impl])]*multiple)]
			else:
				print "Error: Different number of values for " + impl + " (not a multiple of " + str(number_of_instances[j]) + ")!"
				sys.exit(1)
		data[impl] = data[impl] + partial_data[impl]
	# Add the necessary number of n to our data
	for i in range(number_of_instances[j]):
		data["n"].append(curr_n)


df = pd.DataFrame(data)
df = df[["n"] + impl_names]
#df = pd.DataFrame({'Group':['A','A','A','B','C','B','B','C','A','C', 'D', 'D'], 'Apple':np.random.rand(12),'Orange':np.random.rand(12), 'Banana':np.random.rand(12), 'Macha':np.random.rand(12), 'Pear':np.random.rand(12)})
#df = df[['Group','Apple','Orange', 'Banana', 'Macha', 'Pear']]


#sns.set(font_scale=1.5)
sns.set_context("paper", font_scale=1.7)

dd = pd.melt(df, id_vars=["n"], value_vars=impl_names, var_name='algorithm')
meanprops = dict(marker='o', color='black', markeredgecolor='black', markerfacecolor='black', markersize=4)
sns_plot = sns.boxplot(x="n", y="value", data=dd, hue='algorithm', width=0.5, whis='range', showmeans = True, meanprops = meanprops)

#dd = pd.melt(df, id_vars=['Group'], value_vars=['Apple', 'Orange', 'Banana', 'Macha', 'Pear'], var_name='fruits')
#sns_plot = sns.boxplot(x='Group', y='value', data=dd, hue='fruits', width=0.5)

sns_plot.set_yscale('log')
sns_plot.legend_.remove()
sns_plot.set(xlabel="n", ylabel=yAxisName)

if (sys.argv[4] == "Balance") or (sys.argv[2] == "UD_SEq"):
	sns_plot.get_yaxis().set_major_formatter(FormatStrFormatter("%.2f"))
	sns_plot.get_yaxis().set_minor_formatter(FormatStrFormatter("%.2f"))

if (sys.argv[2] == "U_Bal"):
	ytoplim = 0.21
	ybotlim = 0.13
	sns_plot.set_autoscaley_on(False)
	sns_plot.set_ylim([ybotlim, ytoplim])
	sns_plot.set_yticks([0.14, 0.17])
if (sys.argv[2] == "D_Bal"):
	ytoplim = 0.88
	ybotlim = 0.82
	sns_plot.set_autoscaley_on(False)
	sns_plot.set_ylim([ybotlim, ytoplim])
	sns_plot.set_yticks([0.82, 0.85, 0.88])
if (sys.argv[2] == "UD_Bal"):
	ytoplim = 1.08
	ybotlim = 0.98
	sns_plot.set_autoscaley_on(False)
	sns_plot.set_ylim([ybotlim, ytoplim])
	sns_plot.set_yticks([1.00, 1.04, 1.08])
if (sys.argv[2] == "UD_SEq"):
	#ytoplim = 1.08
	#ybotlim = 0.98
	#sns_plot.set_autoscaley_on(False)
	#sns_plot.set_ylim([ybotlim, ytoplim])
	sns_plot.set_yticks([1.1, 1.3])

fig = sns_plot.get_figure()
fig.savefig(saveName + ".pdf", format="pdf", bbox_inches="tight")

'''
figlegend = plt.figure(figsize=(2 * len(impl_names), 0.5))
leg = figlegend.legend(sns_plot, ncol=len(impl_names), fancybox=True, shadow=True, prop={'size':30})
leg.get_frame().set_facecolor('lightgrey')
'''

'''
figlegend = sns_plot.get_figure()
figlegend.savefig("legend2.pdf", format="pdf", bbox_inches="tight")
'''
plt.rc('text', usetex=True)  
plt.rc('font', family='serif', size=20) 

h, l = sns_plot.get_legend_handles_labels()
for i in range(len(l)):
	if l[i] == "PolyMin_SEq": l[i] = "PolyMin"
	if l[i] == "PolyMin_SEq": l[i] = "PolyMin"
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
figlegend.savefig(os.path.dirname(saveName) + '/legend2.pdf', format="pdf", bbox_inches="tight")