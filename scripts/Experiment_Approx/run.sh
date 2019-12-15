#/bin/bash

mkdir -p ../../results/outputs/Experiment_Approx

JAR_PATH="../../target/stable-marriage-1.0.jar"
OUT_PATH="../../results/outputs/Experiment_Approx/"
SUF=""
#SUF=".zip"

declare -a ALG_LIST=("GS_MaleOpt" "GS_FemaleOpt" "iBiLS -c SEq -p 0.125" "HybridMultiSearch -c SEq")

for i in {1..50}
do
		for impl in "${ALG_LIST[@]}"
		do
			for n in "4000"
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

		for e in "0.01" "0.00975" "0.0095" "0.00925" "0.009" "0.00875" "0.0085" "0.00825" "0.008"
		do
				## Uniform
				java -cp ${JAR_PATH} algorithms.Approx -e $e -n "$n" -m "../../datasets/Uniform/men${i}_n${n}${SUF}" -w "../../datasets/Uniform/women${i}_n${n}${SUF}" >> "${OUT_PATH}outU_${n}"
		done

		for e in "0.001" "0.0009" "0.0008" "0.0007" "0.0006" "0.0005" "0.00045" "0.0004" "0.00035" "0.0003" "0.00025" "0.0002" "0.00015" "0.0001" "0.00005"
		do
				## Discrete
				java -cp ${JAR_PATH} algorithms.Approx -e $e -n "$n" -m "../../datasets/Discrete/men${i}_n${n}_h40${SUF}" -w "../../datasets/Discrete/women${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outD_${n}"
		done	

		for e in "0.06" "0.055" "0.05" "0.045" "0.04" "0.035"
		do
				## Gauss
				java -cp ${JAR_PATH} algorithms.Approx -e $e -n "$n" -m "../../datasets/Gauss/men${i}_n${n}_h40${SUF}" -w "../../datasets/Gauss/women${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outG_${n}"
		done		

		for e in "0.01" "0.009" "0.008" "0.007" "0.006" "0.005" "0.00475" "0.0045" "0.00425" "0.004" "0.00375" "0.0035" "0.00325" "0.003" "0.00275" "0.0025"
		do
				## UniformDiscrete
				java -cp ${JAR_PATH} algorithms.Approx -e $e -n "$n" -m "../../datasets/Uniform/men${i}_n${n}${SUF}" -w "../../datasets/Discrete/women${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outUD_${n}"
		done	
done


