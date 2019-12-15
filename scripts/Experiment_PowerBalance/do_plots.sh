#!/bin/bash

mkdir -p ../../results/plots/Experiment_PowerBalance/

./process_B.py Uniform Discrete Gauss UniformDiscrete
./process_SE.py Uniform Discrete Gauss UniformDiscrete

for d in "U" "D" "UD" "G"
do

	./plot.py "../../results/outputs/Experiment_PowerBalance/${d}mean_SE" "../../results/outputs/Experiment_PowerBalance/${d}mean_B" "../../results/plots/Experiment_PowerBalance/${d}"

	echo "Done with $d"
done
