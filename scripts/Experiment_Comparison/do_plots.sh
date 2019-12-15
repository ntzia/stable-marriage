#!/bin/bash

ALGLIST1=("PolyMin_SEq" "DACC_D" "PowerBalance_SEq" "BiLS_SEq_0.0" "iBiLS_SEq_0.125" "Hybrid_SEq" "HybridMultiSearch_SEq")
ALGLIST2=("PolyMin_Bal" "DACC_D" "PowerBalance_Bal" "BiLS_Bal_0.0" "iBiLS_Bal_0.125" "Hybrid_Bal" "HybridMultiSearch_Bal")

mkdir -p ../../results/plots/Experiment_Comparison

./get_mean.py Uniform Discrete Gauss UniformDiscrete

for d in "U" "D" "G" "UD"
do
	./plot_Boxes.py "$d" "${d}_SEq" "../../results/plots/Experiment_Comparison/${d}_SECost" 9 "SEq Ratio over DA" ${ALGLIST1[@]}
	./plot_Boxes.py "$d" "${d}_Bal" "../../results/plots/Experiment_Comparison/${d}_BCost" "Balance" "Bal Ratio over DA" ${ALGLIST2[@]}
	./plot_Lines.py "../../results/outputs/Experiment_Comparison/${d}mean" "${d} Time" "../../results/plots/Experiment_Comparison/${d}_Time" 2 "Time (sec)" ${ALGLIST1[@]}
	echo "Done with $d"
done