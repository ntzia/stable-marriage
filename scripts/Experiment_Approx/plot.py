#!/usr/bin/env python

import sys
import os
import numpy as np
import seaborn as sns

## We need matplotlib:
## $ apt-get install python-matplotlib
import matplotlib
#matplotlib.use('Agg')
import matplotlib.pyplot as plt
from matplotlib.ticker import ScalarFormatter
from matplotlib.ticker import FormatStrFormatter

import decimal
# create a new context for this task
ctx = decimal.Context()
# 20 digits should be enough for everyone :D
ctx.prec = 20
def float_to_str(f):
	"""
	Convert the given float to a string,
	without resorting to scientific notation
	"""
	d1 = ctx.create_decimal(repr(f))
	return format(d1, 'f')


if len(sys.argv) != 6:
	print "Usage: " + sys.argv[0] + " n distr e_max e_min e_step" 
	sys.exit(1)


n = int(sys.argv[1])
distr = sys.argv[2]
e_max = float(sys.argv[3])
e_min = float(sys.argv[4])
e_step = float(sys.argv[5])
e_list = []
e = e_min
while (e <= e_max + (e_step/10)):
	if (e == 1): e = int(e)
	e_list.append(e)
	e = e + e_step
e_string_list = []
for e in e_list:
	if (e == 0.00005):
		e_string_list.append(float_to_str(e))
	else:
		e_string_list.append(str(e))
print e_string_list
filename = "../../results/outputs/Experiment_Approx/" +  distr + "mean"
savename = "../../results/plots/Experiment_Approx/" + distr + "_" + str(n)

#first find gs value
gsval = -1.0
fp1 = open(filename)
flag = False
line = fp1.readline()
while line:

	if line.startswith("Size="):
		if line.startswith("Size= " + str(n)):
			flag = True
		else:
			flag = False

	if line.startswith("GS_MaleOpt:") and flag:
		tokens = line.split()
		val = float(tokens[9])
		if (gsval == -1.0 or val < gsval):
			gsval = val
		flag = False

	if line.startswith("GS_FemaleOpt:") and flag:
		tokens = line.split()
		val = float(tokens[9])
		if (gsval == -1 or val < gsval):
			gsval = val
		flag = False

	line = fp1.readline()

fp1.close()

#now find bils and hms value
fp1 = open(filename)
flag = False
line = fp1.readline()
while line:

	if line.startswith("Size="):
		if line == ("Size= " + str(n) + "\n"):
			flag = True
		else:
			flag = False

	if line.startswith("iBiLS_SEq_0.125:") and flag:
		tokens = line.split()
		bils_seq = float(tokens[9])
		bils_time = float(tokens[2])

	if line.startswith("HybridMultiSearch_SEq:") and flag:
		tokens = line.split()
		hms_seq = float(tokens[9])
		hms_time = float(tokens[2])

	line = fp1.readline()

fp1.close()


seq = []
time = []

for e in e_string_list:
	# add values
	if (distr == "D" and n == 1000 and e == "0.00005"):
			tokens = line.split()
			seq.append(np.nan)
			time.append(4010)
			continue
	if (distr == "D" and n == 4000 and e == "0.00005"):
			tokens = line.split()
			seq.append(np.nan)
			time.append(1402)
			continue
	if (distr == "G" and n == 1000 and e == "0.02"):
			tokens = line.split()
			seq.append(np.nan)
			time.append(1500)
			continue
	if (distr == "G" and n == 4000 and e == "0.03"):
			tokens = line.split()
			seq.append(np.nan)
			time.append(3000)
			continue	
	if (distr == "U" and n == 4000 and e == "0.00775"):
			tokens = line.split()
			seq.append(np.nan)
			time.append(2500)
			continue	

	if (distr == "UD" and n == 4000 and e == "0.0025"):
			tokens = line.split()
			seq.append(np.nan)
			time.append(2685)
			continue


	fp1 = open(filename)
	flag = False
	line = fp1.readline()
	while line:

		if line.startswith("Size="):
			if line == ("Size= " + str(n) + "\n"):
				flag = True
			else:
				flag = False

		if line.startswith("Approx_" + e + ":") and flag:
			tokens = line.split()
			seq.append(float(tokens[9]))
			time.append(float(tokens[2]))
			break

		line = fp1.readline()

	if (flag != True):
		print "Error: Couldn't find " + str(n) + " in " + sys.argv[1]

	fp1.close()

# find ratio
for i in range(len(e_list)):
	seq[i] = seq[i] / gsval
bils_seq = bils_seq / gsval
hms_seq = hms_seq / gsval
# make lists for bils and hms
bils_seq_list = []
bils_time_list = []
hms_seq_list = []
hms_time_list = []
for i in range(len(e_list)):
	bils_seq_list.append(bils_seq)
	bils_time_list.append(bils_time)
	hms_seq_list.append(hms_seq)
	hms_time_list.append(hms_time)

sns.set_context("paper", font_scale=1.7)
sns.set_style("whitegrid")

lns = []
fig, ax1 = plt.subplots()
ax1.grid(True)
ax1.set_xlabel("$\epsilon$")
ax1.xaxis.set_ticks(np.arange(0, len(e_list), 1))
e_list_labels = []
for i in range(len(e_list)):
	if (i % 2 == 0):
		e_list_labels.append(str(list(reversed(e_list))[i]))
	else:
		e_list_labels.append("")
e_list_labels = list(reversed(e_list_labels))
ax1.set_xticklabels(e_list_labels)

ax1.set_ylabel("SEq Ratio over DA")
ax2 = ax1.twinx()
ax2.grid(True)
ax2.set_ylabel("Time (sec)")

if (not(distr == "UD" and n == 4000)):
	ax1.set_yscale("log")
ax2.set_yscale("log")

ax1.invert_xaxis()

ax1.get_yaxis().set_major_formatter(ScalarFormatter())
ax2.get_yaxis().set_major_formatter(ScalarFormatter())

# Set limits for y
'''
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

# Custom for paper symmetry
if (sys.argv[2] == "Mean Egalitarian Cost\nUniform Distribution"):
	ymax = 10
'''


#ax1.set_autoscaley_on(False)
#ax1.set_ylim([ybotlim, ytoplim])


# Colors and markers
color_list = ['blue', 'red', 'green', 'saddlebrown', 'orange', 'black', 'cyan', 'magenta', 'orange', 'darkviolet', 'darkcyan']
markfacecollist = ['blue', 'none', 'green', 'saddlebrown', 'none', 'none', 'cyan', 'none', 'none', 'darkviolet', 'darkcyan']
markers = ['*', 'o', 'D', '^', 's', 'p', 'v', 'o', 'p', 'x', 'H']
markersizeList = [15, 15, 11, 13, 15, 16, 13, 14, 14, 12, 11]


# Rename for paper
'''
for impl in impl_names:
	if (impl == "PDB_MassProp"): impl_names[impl_names.index(impl)] = "PDB"
'''


lns += ax1.plot(seq, label="Approx Achieved Ratio", color='saddlebrown', marker='^', fillstyle='full', markeredgecolor='saddlebrown', markersize=13, markerfacecolor='saddlebrown')
lns += ax1.plot(bils_seq_list, label="iBiLS Achieved Ratio", color='crimson', marker='s', fillstyle='full', markeredgecolor='crimson', markersize=15, markerfacecolor='none')
lns += ax1.plot(hms_seq_list, label="HMS Achieved Ratio", color='red', marker='*', fillstyle='full', markeredgecolor='red', markersize=15, markerfacecolor='red')
#lns += ax1.plot(e_list, label="Identity Function", color='black', linestyle='--')

lns += ax2.plot(time, label="Approx Time", color='midnightblue', marker='v', fillstyle='full', markeredgecolor='midnightblue', markersize=13, markerfacecolor='midnightblue')
lns += ax2.plot(bils_time_list, label="iBiLS Time", color='blue', marker="o", fillstyle='full', markeredgecolor='blue', markersize=15, markerfacecolor='none')
lns += ax2.plot(hms_time_list, label="HMS Time", color='aqua', marker="d", fillstyle='full', markeredgecolor='aqua', markersize=13, markerfacecolor='aqua')

'''
lns += sns.lineplot(x=x, y=seq, label="Approx Achieved Ratio", color='saddlebrown', marker='^', fillstyle='full', markeredgecolor='saddlebrown', markersize=13, markerfacecolor='saddlebrown')
lns += sns.lineplot(x=x, y=bils_seq_list, label="iBiLS Achieved Ratio", color='crimson', marker='s', fillstyle='full', markeredgecolor='crimson', markersize=15, markerfacecolor='none')
lns += sns.lineplot(x=x, y=hms_seq_list, label="HMS Achieved Ratio", color='red', marker='*', fillstyle='full', markeredgecolor='red', markersize=15, markerfacecolor='red')
#lns += ax1.plot(e_list, label="Identity Function", color='black', linestyle='--')

lns += sns.lineplot(x=x, y=data[i],time, label="Approx Time", color='midnightblue', marker='v', fillstyle='full', markeredgecolor='midnightblue', markersize=13, markerfacecolor='midnightblue')
lns += sns.lineplot(bils_time_list, label="iBiLS Time", color='blue', marker="o", fillstyle='full', markeredgecolor='blue', markersize=15, markerfacecolor='none')
lns += sns.lineplot(hms_time_list, label="HMS Time", color='aqua', marker="d", fillstyle='full', markeredgecolor='aqua', markersize=13, markerfacecolor='aqua')
'''


if (distr == "U" and n == 100):
	print "U 100"
	ytoplim = 0.050
	ybotlim = 0.010
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([ybotlim, ytoplim])
	ax1.set_yticks([0.01, 0.02, 0.03, 0.04, 0.05])
	ax2.set_autoscaley_on(False)
	ax2.set_ylim([0.01, 100])
	ax2.set_yticks([0.01, 0.1, 1, 10, 100])

if (distr == "U" and n == 1000):
	print "U 1000"
	ytoplim = 0.030
	ybotlim = 0.0009
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([ybotlim, ytoplim])
	ax1.set_yticks([0.001, 0.010, 0.020])
	ax2.set_autoscaley_on(False)
	ax2.set_ylim([0.1, 1200])
	ax2.set_yticks([0.1, 1, 10, 100, 1000])

if (distr == "U" and n == 4000):
	print "U 4000"
	ytoplim = 0.0100
	ybotlim = 0.0004
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([ybotlim, ytoplim])
	ax1.set_yticks([0.0005, 0.0010, 0.0050, 0.0100])
	ax2.set_autoscaley_on(False)
	ax2.set_ylim([1, 1200])

if (distr == "D" and n == 100):
	ax2.set_autoscaley_on(False)
	ax2.set_ylim([0.01, 100])
	ax2.set_yticks([0.01, 0.1, 1, 10, 100])

if (distr == "D" and n == 1000):
	print "D 1000"
	ytoplim = 0.005
	ybotlim = 0.00006
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([ybotlim, ytoplim])
	ax1.set_yticks([0.0001, 0.001, 0.005])
	ax2.set_autoscaley_on(False)
	ax2.set_ylim([0.1, 1000])
	ax2.set_yticks([0.1, 1, 10, 100, 1000])

if (distr == "D" and n == 4000):
	print "D 4000"
	ytoplim = 0.00070
	ybotlim = 0.00002
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([ybotlim, ytoplim])
	ax1.set_yticks([0.00002, 0.0001, 0.0005])
	ax2.set_autoscaley_on(False)
	ax2.set_ylim([1, 1000])
	ax2.set_yticks([1, 10, 100, 1000])

if (distr == "G" and n == 100):
	print "G 100"
	ytoplim = 0.17
	ybotlim = 0.1
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([ybotlim, ytoplim])
	ax1.set_yticks([0.1, 0.12, 0.13, 0.14, 0.15])

if (distr == "G" and n == 1000):
	print "G 1000"
	ax2.set_autoscaley_on(False)
	ax2.set_ylim([0.1, 1000])
	ax2.set_yticks([0.1, 1, 10, 100, 1000])

if (distr == "G" and n == 4000):
	print "G 4000"
	ytoplim = 0.100
	ybotlim = 0.001
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([ybotlim, ytoplim])
	ax1.set_yticks([0.001, 0.010, 0.100])
	ax2.set_autoscaley_on(False)
	ax2.set_ylim([1, 1000])

'''
if (distr == "UD"):
	print "UD"
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([0.9, 1.1])
	ax1.set_yticks([0.9, 1.0, 1.1])
'''

if (distr == "UD" and n == 1000):
	ax2.set_autoscaley_on(False)
	ax2.set_ylim([0.1, 1000])
	ax2.set_yticks([0.1, 1, 10, 100, 1000])

if (distr == "UD" and n == 4000):
	print "UD 4000"
	ytoplim = 1.15
	ybotlim = 0.85
	ax1.set_autoscaley_on(False)
	ax1.set_ylim([ybotlim, ytoplim])
	ax1.set_yticks([0.9, 1.0, 1.1])
	print ax1.get_yticks()
	ax2.set_autoscaley_on(False)
	ax2.set_ylim([1, 1000])


labs = [l.get_label() for l in lns]

#plt.title(sys.argv[2])
plt.savefig(savename + ".pdf", format="pdf", bbox_inches="tight")
#plt.savefig(savename + ".svg", format="svg", bbox_inches="tight")


## Legend
plt.rc('text', usetex=True)  
plt.rc('font', family='serif', size=20) 

plt.cla()
figlegend = plt.figure(figsize=(15,0.5))

leg = figlegend.legend(lns, labs, ncol=7, fancybox=True, shadow=True, prop={'size':30})
#figlegend.axis('off')
figlegend.savefig(os.path.dirname(savename) + '/legend.pdf', format="pdf", bbox_inches="tight")
