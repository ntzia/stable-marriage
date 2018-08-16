#!/bin/bash

ALGLIST=("ESMA" "PowerBalance_SEq" "BiLS_SEq_0.125")

./get_mean.py Uniform Discrete Gauss UniformDiscrete
for d in "U" "D" "G" "UD"
do
	./plot.py "../outputs/${d}mean" "${d} Time" "../plots/${d}_Time" 2 "Time (sec)" ${ALGLIST[@]}
	./plot.py "../outputs/${d}mean" "${d} SEq" "../plots/${d}_SECost" 9 "SEq Ratio over DA" ${ALGLIST[@]}
	./plot.py "../outputs/${d}mean" "${d} Bal" "../plots/${d}_BCost" "Balance" "Bal Ratio over DA" ${ALGLIST[@]}
	echo "Done with $d"
done