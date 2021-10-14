#!/usr/bin/env python

import sys
from random import random

W_RANDOM, W_RANK=0.5, 0.5

class RankGenerator:
    def __init__(self):
	freqs = [
		[0.02661,0.00571,0.00249,0.00250,0.00436,0.00404,0.00539,0.00534,0.00360,0.01151],
		[0.00442,0.01155,0.00191,0.00152,0.00271,0.00253,0.00324,0.00310,0.00209,0.00611],
		[0.00251,0.00353,0.00575,0.00158,0.00248,0.00209,0.00277,0.00255,0.00192,0.00565],
		[0.00209,0.00210,0.00193,0.00455,0.00308,0.00223,0.00317,0.00307,0.00206,0.00642],
		[0.00230,0.00208,0.00239,0.00288,0.00906,0.00389,0.00565,0.00504,0.00360,0.01147],
		[0.00152,0.00117,0.00124,0.00138,0.00293,0.00552,0.00403,0.00431,0.00314,0.00962],
		[0.00137,0.00115,0.00132,0.00152,0.00317,0.00332,0.00872,0.00651,0.00513,0.01504],
		[0.00125,0.00076,0.00093,0.00102,0.00255,0.00235,0.00374,0.00835,0.00631,0.01828],
		[0.00095,0.00065,0.00084,0.00097,0.00178,0.00214,0.00277,0.00385,0.00814,0.02309],
		[0.00276,0.00217,0.00168,0.00150,0.00379,0.00350,0.00605,0.00883,0.01375,0.56255]]
	self.serial = []
	acc =0.0
	for i in range(len(freqs)):
	    for j in range(len(freqs)):
		acc += freqs[i][j]
		self.serial.append([i+1, j+1, acc])

    def get_scores(self, chance):
	acc = 0.0
	start, finish = 0, len(self.serial)-1
	while finish - start > 1:
	    median = (finish+start)/2
	    if chance < self.serial[median][2]:
		finish = median
	    else:
		start = median
	idx = finish
	if chance < self.serial[start][2]:
	    idx = start
	return (self.serial[idx][0], self.serial[idx][1])

def fuzzy_cmp(a, b):
    if a[1] < b[1]:
        return -1
    elif a[1] > b[1]:
        return 1
    else:
        foo = women_rank
        if not SORTING_MEN:
            foo = men_rank
        score = W_RANDOM*(random()-0.5) + W_RANK*((foo[a[0]] - foo[b[0]])/N)
        if score > 0:
            return 1
        else:
            return -1
        
def scores_to_prefs(l):
    "Converts scores list to preference list"
    t = []
    for i in range(len(l)):
        t.append([i, l[i]])
    t = sorted(t, cmp = fuzzy_cmp, reverse=True)
    return [x[0] for x in t]

if len(sys.argv)< 4:
    print "Please provide number of agents for each group and file names for the preference lists."
    sys.exit(1)

N = int(sys.argv[1])
men, women = [], []
men_rank, women_rank = [], []

for i in range(N):
    men_rank.append(0)
    women_rank.append(0)
    men.append([])
    women.append([])
    for j in range(N):
        men[i].append(0)
        women[i].append(0)

r = RankGenerator()
for i in range(N):
    for j in range(N):
        [m, w] = r.get_scores(random())
        men[i][j] = m
        women_rank[j] += m
        women[j][i] = w
        men_rank[i] += w

f = open(sys.argv[2], mode = 'w')
SORTING_MEN = True
for i in range(N):
    f.write("%s\n" % ' '.join([str(x) for x in scores_to_prefs(men[i])]))

f = open(sys.argv[3], mode = 'w')
SORTING_MEN = False
for i in range(N):
    f.write("%s\n" % ' '.join([str(x) for x in scores_to_prefs(women[i])]))
