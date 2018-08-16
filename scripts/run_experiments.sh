#/bin/bash

declare -a ALG_LIST=("GS_MaleOpt" "GS_FemaleOpt" "ESMA" "PowerBalance -c SEq" "BiLS -c SEq -p 0.125")

JAR_PATH="../target/stable-marriage-2.0.jar"
OUT_PATH="../results/outputs/"
SUF=".zip"

for i in {1..5}
do
		for n in {50..200..50}
		do
				for impl in "${ALG_LIST[@]}"
				do
					## Uniform
					java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl -n "$n" -m "../datasets/Uniform/men${i}_n${n}${SUF}" -w "../datasets/Uniform/women${i}_n${n}${SUF}" >> "${OUT_PATH}outU_${n}"
					## Discrete
					java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl -n "$n" -m "../datasets/Discrete/men${i}_n${n}_h40${SUF}" -w "../datasets/Discrete/women${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outD_${n}"
					## Gauss
					java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl -n "$n" -m "../datasets/Gauss/men${i}_n${n}_h40${SUF}" -w "../datasets/Gauss/women${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outG_${n}"
					## UniformDiscrete
					java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl -n "$n" -m "../datasets/Uniform/men${i}_n${n}${SUF}" -w "../datasets/Discrete/women${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outUD_${n}"
				done
		done
done
