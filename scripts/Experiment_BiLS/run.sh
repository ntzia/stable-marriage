#/bin/bash

mkdir -p ../../results/outputs/Experiment_BiLS

JAR_PATH="../../target/stable-marriage-1.0.jar"
OUT_PATH="../../results/outputs/Experiment_BiLS/"
SUF=""
#SUF=".zip"

mkdir -p ../../datasets/temp/

for i in {1..2000}
do
		for n in 500 1000 1500
		do
				java -cp ${JAR_PATH} data.UniformDataGenerator ${n} ../../datasets/temp/Umen${i}_n${n}
				java -cp ${JAR_PATH} data.UniformDataGenerator ${n} ../../datasets/temp/Uwomen${i}_n${n}
				java -cp ${JAR_PATH} data.DiscreteDataGenerator ${n} 0.4 ../../datasets/temp/Dmen${i}_n${n}_h40
				java -cp ${JAR_PATH} data.DiscreteDataGenerator ${n} 0.4 ../../datasets/temp/Dwomen${i}_n${n}_h40
				java -cp ${JAR_PATH} data.GaussDataGenerator ${n} 0.4 ../../datasets/temp/Gmen${i}_n${n}_h40
				java -cp ${JAR_PATH} data.GaussDataGenerator ${n} 0.4 ../../datasets/temp/Gwomen${i}_n${n}_h40

				for prob in "0.0" "0.025" "0.05" "0.075" "0.1" "0.125" "0.15" "0.175" "0.2" "0.225" "0.25" "0.275" "0.3" "0.325" "0.35" "0.375" "0.4"
				do
					## Uniform
					java -cp ${JAR_PATH} algorithms.BiLS -p $prob -c SEq -n "$n" -m "../../datasets/temp/Umen${i}_n${n}${SUF}" -w "../../datasets/temp/Uwomen${i}_n${n}${SUF}" >> "${OUT_PATH}outU_${n}"
					## Discrete
					java -cp ${JAR_PATH} algorithms.BiLS -p $prob -c SEq -n "$n" -m "../../datasets/temp/Dmen${i}_n${n}_h40${SUF}" -w "../../datasets/temp/Dwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outD_${n}"
					## Gauss
					java -cp ${JAR_PATH} algorithms.BiLS -p $prob -c SEq -n "$n" -m "../../datasets/temp/Gmen${i}_n${n}_h40${SUF}" -w "../../datasets/temp/Gwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outG_${n}"
					## UniformDiscrete
					java -cp ${JAR_PATH} algorithms.BiLS -p $prob -c SEq -n "$n" -m "../../datasets/temp/Umen${i}_n${n}${SUF}" -w "../../datasets/temp/Dwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outUD_${n}"
				done

				for prob in "0.0" "0.025" "0.05" "0.075" "0.1" "0.125" "0.15" "0.175" "0.2" "0.225" "0.25" "0.275" "0.3" "0.325" "0.35" "0.375" "0.4"
				do
					## Uniform
					java -cp ${JAR_PATH} algorithms.iBiLS -p $prob -c SEq -n "$n" -m "../../datasets/temp/Umen${i}_n${n}${SUF}" -w "../../datasets/temp/Uwomen${i}_n${n}${SUF}" >> "${OUT_PATH}outU_${n}"
					## Discrete
					java -cp ${JAR_PATH} algorithms.iBiLS -p $prob -c SEq -n "$n" -m "../../datasets/temp/Dmen${i}_n${n}_h40${SUF}" -w "../../datasets/temp/Dwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outD_${n}"
					## Gauss
					java -cp ${JAR_PATH} algorithms.iBiLS -p $prob -c SEq -n "$n" -m "../../datasets/temp/Gmen${i}_n${n}_h40${SUF}" -w "../../datasets/temp/Gwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outG_${n}"
					## UniformDiscrete
					java -cp ${JAR_PATH} algorithms.iBiLS -p $prob -c SEq -n "$n" -m "../../datasets/temp/Umen${i}_n${n}${SUF}" -w "../../datasets/temp/Dwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outUD_${n}"
				done

				rm ../../datasets/temp/Umen${i}_n${n}
				rm ../../datasets/temp/Uwomen${i}_n${n}
				rm ../../datasets/temp/Dmen${i}_n${n}_h40
				rm ../../datasets/temp/Dwomen${i}_n${n}_h40
				rm ../../datasets/temp/Gmen${i}_n${n}_h40
				rm ../../datasets/temp/Gwomen${i}_n${n}_h40
		done
done

rm -r ../../datasets/temp/
