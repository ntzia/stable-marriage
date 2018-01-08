#!/bin/bash

ALGLIST=("ESMA" "CaC_2" "PDB" "GreedySE" "GS_MaleOpt")

mkdir -p ../plots/Uniform
mkdir -p ../plots/Discrete40
mkdir -p ../plots/Gauss40
mkdir -p ../plots/UniformDiscrete40
mkdir -p ../plots/Discrete40Gauss40
mkdir -p ../plots/Gauss40Uniform

rm ../plots/Uniform/*
rm ../plots/Discrete40/*
rm ../plots/Gauss40/*
rm ../plots/UniformDiscrete40/*
rm ../plots/Discrete40Gauss40/*
rm ../plots/Gauss40Uniform/*

rm ../plots/legend*

FILEFORCOUNTER=/tmp/$$.tmp
cd ../outputs
for d in */ ;
do
	rm ${d}mean_output.txt
	echo 0 > $FILEFORCOUNTER
	for f in ${d}output*
	do
		COUNTER=$[$(cat $FILEFORCOUNTER) + 1]
		echo $COUNTER > $FILEFORCOUNTER
	done
	echo "$COUNTER in ${d}"
	../scripts/get_mean.py $COUNTER ../outputs/${d}

	../scripts/plot.py ${d}mean_output.txt ../plots/${d}Time_Mean 500 2000 500 2 "Time(sec)" ${ALGLIST[@]}
	../scripts/plot.py ${d}mean_output.txt ../plots/${d}ECost_Mean 500 2000 500 7 "Egalitarian Cost" ${ALGLIST[@]}
	../scripts/plot.py ${d}mean_output.txt ../plots/${d}RCost_Mean 500 2000 500 11 "Regret Cost" ${ALGLIST[@]}
	../scripts/plot.py ${d}mean_output.txt ../plots/${d}SECost_Mean 500 2000 500 9 "Sex Equality Cost" ${ALGLIST[@]}
	../scripts/plot.py ${d}mean_output.txt ../plots/${d}MECost_Mean 500 2000 500 13 "Marital Equality Cost" ${ALGLIST[@]}
	../scripts/plot.py ${d}mean_output.txt ../plots/${d}TECost_Mean 500 2000 500 15 "Totalitarian Equality Cost" ${ALGLIST[@]}

	echo "Done with $d"
done

../scripts/plotLegend.py Uniform/mean_output.txt ../plots ${ALGLIST[@]}