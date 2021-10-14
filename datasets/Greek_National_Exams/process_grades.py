#!/usr/bin/env python

import sys
import os
import numpy as np
import xlrd

# output file
f = open("grades.txt", 'w')

src = "grades.xls"
book = xlrd.open_workbook(src)
work_sheet = book.sheet_by_index(0)

## Find the relevant columns
last_col = work_sheet.ncols - 1
cols_no = 0
cols = []
col = 3
while (col <= last_col):
	cols_no = cols_no + 1
	cols.append(col)
	col = col + 2

for i in range(0, 9):
	f.write("Profile " + str(i) + " : ")
	f.write(str(int(work_sheet.cell_value(i + 3, 2))) + " Total ")
	for col in cols:
		f.write(str(int(work_sheet.cell_value(i + 3, col))))
		f.write(" ")
	f.write("\n")