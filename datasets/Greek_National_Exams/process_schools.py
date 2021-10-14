#!/usr/bin/env python

import sys
import os
import numpy as np
import xlrd

# output file
f = open("schools.txt", 'w')

# path to the file you want to extract data from
src1 = "preferences_2017.xls"
book1 = xlrd.open_workbook(src1)
# select the sheet that the data resids in
work_sheet1 = book1.sheet_by_index(0)

# path to the file you want to extract data from
src2 = "preference_of_admits_2017.xls"
book2 = xlrd.open_workbook(src2)
# select the sheet that the data resids in
work_sheet2 = book2.sheet_by_index(0)

# get the total number of rows
last_row1 = work_sheet1.nrows - 1
last_row2 = work_sheet2.nrows - 1

# get the total number of first preferences
total_prefs = 0
r = 2
while (r <= last_row1):
	total_prefs += int(work_sheet1.cell_value(r, 3))
	r = r + 1

school_num = 0
current_row = 2
n = 0
while (current_row <= last_row1):

	school_id = work_sheet1.cell_value(current_row, 1)
	first_prefs = int(work_sheet1.cell_value(current_row, 3))

	# Search file 2 for the number of positions
	r = 2
	while (r <= last_row2):
		if (work_sheet2.cell_value(r, 1) == school_id):
			positions = int(work_sheet2.cell_value(r, 10))
			n = n + positions
			break
		r = r + 1
		
	f.write("School " + str(school_num) + " : " +  str(positions) + " positions " +  str(first_prefs * 1.0 / total_prefs) + " weight\n")
	school_num = school_num + 1
	current_row = current_row + 1

print "Total number of schools = " + str(school_num)
print "Total number of positions = " + str(n)
