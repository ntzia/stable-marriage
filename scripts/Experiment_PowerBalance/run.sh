#/bin/bash

mkdir -p ../../results/outputs/Experiment_PowerBalance

JAR_PATH="../../target/stable-marriage-1.0.jar"
OUT_PATH="../../results/outputs/Experiment_PowerBalance/"
SUF=""
#SUF=".zip"

mkdir -p ../../datasets/temp/

for i in {1..100}
do
		for n in 250 500 1000 2000 4000
		do
				java -cp ${JAR_PATH} data.UniformDataGenerator ${n} ../../datasets/temp/Umen${i}_n${n}
				java -cp ${JAR_PATH} data.UniformDataGenerator ${n} ../../datasets/temp/Uwomen${i}_n${n}
				java -cp ${JAR_PATH} data.DiscreteDataGenerator ${n} 0.4 ../../datasets/temp/Dmen${i}_n${n}_h40
				java -cp ${JAR_PATH} data.DiscreteDataGenerator ${n} 0.4 ../../datasets/temp/Dwomen${i}_n${n}_h40
				java -cp ${JAR_PATH} data.GaussDataGenerator ${n} 0.4 ../../datasets/temp/Gmen${i}_n${n}_h40
				java -cp ${JAR_PATH} data.GaussDataGenerator ${n} 0.4 ../../datasets/temp/Gwomen${i}_n${n}_h40

				for initR in {0..100}
				do
					## Uniform
					java -cp ${JAR_PATH} algorithms.PowerBalance -c SEq -i "$initR" -n "$n" -m "../../datasets/temp/Umen${i}_n${n}${SUF}" -w "../../datasets/temp/Uwomen${i}_n${n}${SUF}" >> "${OUT_PATH}outUSEq_${n}"
					## Discrete
					java -cp ${JAR_PATH} algorithms.PowerBalance -c SEq -i "$initR" -n "$n" -m "../../datasets/temp/Dmen${i}_n${n}_h40${SUF}" -w "../../datasets/temp/Dwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outDSEq_${n}"
					## Gauss
					java -cp ${JAR_PATH} algorithms.PowerBalance -c SEq -i "$initR" -n "$n" -m "../../datasets/temp/Gmen${i}_n${n}_h40${SUF}" -w "../../datasets/temp/Gwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outGSEq_${n}"
					## UniformDiscrete
					java -cp ${JAR_PATH} algorithms.PowerBalance -c SEq -i "$initR" -n "$n" -m "../../datasets/temp/Umen${i}_n${n}${SUF}" -w "../../datasets/temp/Dwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outUDSEq_${n}"
				done

				for initR in {0..100}
				do
					## Uniform
					java -cp ${JAR_PATH} algorithms.PowerBalance -c Bal -i "$initR" -n "$n" -m "../../datasets/temp/Umen${i}_n${n}${SUF}" -w "../../datasets/temp/Uwomen${i}_n${n}${SUF}" >> "${OUT_PATH}outUBal_${n}"
					## Discrete
					java -cp ${JAR_PATH} algorithms.PowerBalance -c Bal -i "$initR" -n "$n" -m "../../datasets/temp/Dmen${i}_n${n}_h40${SUF}" -w "../../datasets/temp/Dwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outDBal_${n}"
					## Gauss
					java -cp ${JAR_PATH} algorithms.PowerBalance -c Bal -i "$initR" -n "$n" -m "../../datasets/temp/Gmen${i}_n${n}_h40${SUF}" -w "../../datasets/temp/Gwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outGBal_${n}"
					## UniformDiscrete
					java -cp ${JAR_PATH} algorithms.PowerBalance -c Bal -i "$initR" -n "$n" -m "../../datasets/temp/Umen${i}_n${n}${SUF}" -w "../../datasets/temp/Dwomen${i}_n${n}_h40${SUF}" >> "${OUT_PATH}outUDBal_${n}"
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
