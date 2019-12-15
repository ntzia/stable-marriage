#!/bin/bash

mkdir -p ../../results/plots/Experiment_Approx

./get_mean.py Uniform Discrete Gauss UniformDiscrete

./plot.py 4000 U 0.01 0.00775 0.00025
./plot.py 4000 D 0.0005 0.00005 0.00005
./plot.py 4000 G 0.06 0.03 0.005
./plot.py 4000 UD 0.004 0.0025 0.00025
