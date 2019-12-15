#/bin/bash

declare -a ALG_LIST=("GS_MaleOpt" "GS_FemaleOpt" "PolyMin -c SEq" "PolyMin -c Bal" "DACC -s D" "PowerBalance -c SEq" "PowerBalance -c Bal" "iBiLS -c SEq -p 0.125" "iBiLS -c Bal -p 0.125" "BiLS -c SEq -p 0" "BiLS -c Bal -p 0" "Hybrid -c SEq" "Hybrid -c Bal" "HybridMultiSearch -c SEq" "HybridMultiSearch -c Bal")

mkdir -p ../../results/outputs/Experiment_Comparison

JAR_PATH="../../target/stable-marriage-1.0.jar"
OUT_PATH="../../results/outputs/Experiment_Comparison/"
SUF=""
#SUF=".zip"

for i in {1..50}
do
		for n in 250 500 1000 2000 4000
		do
				for impl in "${ALG_LIST[@]}"
				do
					## Uniform
					java -cp ${JAR_PATH} algorithms.$impl -n "$n" -m "../../datasets/Uniform/men${i}_n${n}${SUF}" -w "../../datasets/Uniform/women${i}_n${n}${SUF}" >> "${OUT_PATH}outU_${n}"
					## Discrete
					java -cp ${JAR_PATH} algorithms.$impl -n "$n" -m "../../datasets/Discrete/men${i}_n${n}_h40${SUF}" -w "../../datasets/Discrete/women${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outD_${n}"
					## Gauss
					java -cp ${JAR_PATH} algorithms.$impl -n "$n" -m "../../datasets/Gauss/men${i}_n${n}_h40${SUF}" -w "../../datasets/Gauss/women${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outG_${n}"
					## UniformDiscrete
					java -cp ${JAR_PATH} algorithms.$impl -n "$n" -m "../../datasets/Uniform/men${i}_n${n}${SUF}" -w "../../datasets/Discrete/women${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outUD_${n}"
				done
		done
done
