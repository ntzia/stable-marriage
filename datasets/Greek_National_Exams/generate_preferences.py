#!/usr/bin/env python

import sys
import os
import numpy as np
from random import randint
from random import shuffle
import operator
import math

if len(sys.argv) != 7:
	print "Usage: " + sys.argv[0] + " percentUsed studentOutFile schoolOutFile schoolClusters (-succinct | -complete) (-diverse | -skewed)" 
	sys.exit(1)

percent = float(sys.argv[1])
studentOutFile = sys.argv[2]
schoolOutFile = sys.argv[3]
clusters = int(sys.argv[4])

if (sys.argv[5] == "-succinct"): 
	succ = True
	compl = False
elif (sys.argv[5] == "-complete"): 
	succ = False
	compl = True
else:
	print "5th argument must be either -succinct or -complete" 
	sys.exit(1)

if (sys.argv[6] == "-diverse"): 
	div = True
	sk = False
elif (sys.argv[6] == "-skewed"): 
	div = False
	sk = True
else:
	print "6th argument must be either -diverse or -skewed" 
	sys.exit(1)

f_in1 = open("schools.txt", 'r')
f_in2 = open("grades.txt", 'r')

## First generate the preferences of schools
## Also find and report the n

f_out1 = open(studentOutFile, 'w')

## Parse the processed schools file
num_schools = 0
weights_of_schools = []
positions = []
n = 0
line = f_in1.readline()
while line:
	if line.startswith("School"):
		num_schools = num_schools + 1
		tokens = line.split()
		weights_of_schools.append(float(tokens[5]))
		pos = int(math.ceil(int(tokens[3]) * percent / 100.0))
		positions.append(pos)
		n = n + pos
	line = f_in1.readline()
print str(percent) + "% of the dataset used: n = " + str(n)

## Output the positions used if the output is succinct
if (succ):
	f_out = open("positions.txt", 'w')
	for school in range(num_schools):
		f_out.write(str(positions[school]) + "\n") 
	f_out.close()

## Map school positions to agents if the output is complete
if (compl):
	schools_to_agents = {}
	counter = 0
	for school in range(num_schools):
		schools_to_agents[school] = (counter, counter + positions[school] - 1)
		counter = counter + positions[school]

# Decide in which cluster each school belongs
schools_to_clusters = {}
for school in range(num_schools):
	schools_to_clusters[school] = randint(0, clusters - 1)

# Compute weight of clusters
weights_of_clusters = []
for c in range(clusters):
	weights_of_clusters.append(0.0)
for school in range(num_schools):
	c = schools_to_clusters[school]
	weights_of_clusters[c] += weights_of_schools[school]

for student in range(n):
	## prefs_succinct ranks the schools (length num_schools)
	## each student decides his favorite cluster
	## half the probability of the rest of the schools is transferred in the favorite cluster
	favorite_cluster = schools_to_clusters[np.random.choice(num_schools, 1, p=weights_of_schools)[0]]
	weight_of_fav_cluster = weights_of_clusters[favorite_cluster]
	weight_of_rest = 1.0 - weight_of_fav_cluster
	a = 0.5 * (weight_of_rest / weight_of_fav_cluster)
	weights = []
	for school in range(num_schools):
		if (schools_to_clusters[school] == favorite_cluster):
			weights.append((1.0 + a) * weights_of_schools[school])
		else:
			weights.append(0.5 * weights_of_schools[school])

	prefs_succinct = np.random.choice(num_schools, num_schools, replace=False, p=weights)

	if (succ):
		## Output the succinct prefs
		for p in prefs_succinct:
			f_out1.write(str(p))
			f_out1.write(" ")
		f_out1.write("\n")
	elif (compl):
		## Output the complete preference lists
		prefs = []
		for school in prefs_succinct:
			(agent_start, agent_end) = schools_to_agents[school]
			for a in range(agent_start, agent_end + 1):
				prefs.append(a)
		## prefs ranks all school positions
		for p in prefs:
			f_out1.write(str(p))
			f_out1.write(" ")
		f_out1.write("\n")

f_out1.close()

## -----------------------------------------------------------------------------------------------
## Now generate the preferences of schools

f_out2 = open(schoolOutFile, 'w')

# used for processing the "grades.txt file"
## [start, end)
start_grade_of_col = [0, 7000, 8000, 9000, 10000, 10500, 11000, 11500, 12000, 12500, 13000, 13500, 14000, 14500, 15000, 15500, 16000, 16500, 17000, 17500, 18000, 18500, 19000, 19500]
end_grade_of_col = [7000, 8000, 9000, 10000, 10500, 11000, 11500, 12000, 12500, 13000, 13500, 14000, 14500, 15000, 15500, 16000, 16500, 17000, 17500, 18000, 18500, 19000, 19500, 20000]


# Generate the preferences of each cluster
if (div):
	# Rating of students among clusters are uncorrelated
	prefs_of_clusters = []
	for i in range(clusters):
		prefs = []
		for student in range(0, n):
			prefs.append(student)
		shuffle(prefs)
		prefs_of_clusters.append(prefs)
elif (sk):
	# Ratings are based on a single list of grades
	ps = []
	line = f_in2.readline()
	while line:
		if line.startswith("Profile 4"):
			tokens = line.split()
			total = float(tokens[3])
			for a in tokens[5:]:
				ps.append(float(a) / total)
			break
		line = f_in2.readline()
	f_in2.close()
	## Now generate grades based on the statistics read
	global_grades = {}
	for student in range(0, n):
		selected_col = np.random.choice(len(start_grade_of_col), 1, p=ps)[0]
		grade = randint(start_grade_of_col[selected_col], end_grade_of_col[selected_col] - 1)
		global_grades[student] = grade

	# Each cluster adds noise to the grades and sorts
	prefs_of_clusters = []
	for i in range(clusters):
		grades = {}
		for student in range(0, n):
			grades[student] = global_grades[student] + 10000 * np.random.normal(0, 1)
		sorted_grades = sorted(grades.items(), key=operator.itemgetter(1), reverse=True)
		prefs = [stud for (stud, gr) in sorted_grades]
		prefs_of_clusters.append(prefs)

# Generate the preferences of schools according to their cluster
for school in range(0, num_schools):
	c = schools_to_clusters[school]
	prefs = prefs_of_clusters[c]

	# Create identical lists for each position of the school
	for i in range(positions[school]):	
		for p in prefs:
			f_out2.write(str(p))
			f_out2.write(" ")
		f_out2.write("\n")

f_out2.close()
