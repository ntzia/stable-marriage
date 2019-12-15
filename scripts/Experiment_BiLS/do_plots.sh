#!/bin/bash

mkdir -p ../../results/plots/Experiment_BiLS

PROBARRAY=("0.0" "0.025" "0.05" "0.075" "0.1" "0.125" "0.15" "0.175" "0.2" "0.225" "0.25" "0.275" "0.3" "0.325" "0.35" "0.375" "0.4")

./get_mean.py Uniform Discrete Gauss UniformDiscrete

for d in "U" "D" "UD" "G"
do

	./plotiBiLS.py "../../results/outputs/Experiment_BiLS/${d}mean" $'Mean Sex Equality Cost\n'"${d::-1}"' Distribution' "../../results/plots/Experiment_BiLS/iBiLS_${d}_SECost" 9 "SEq" ${PROBARRAY[@]}
	./plotOldBiLS.py "../../results/outputs/Experiment_BiLS/${d}mean" $'Mean Sex Equality Cost\n'"${d::-1}"' Distribution' "../../results/plots/Experiment_BiLS/OldBiLS_${d}_SECost" 9 "SEq" ${PROBARRAY[@]}
	echo "Done with $d"
done
