#/bin/bash

ALG_LIST="ESMA GS_MaleOpt GS_FemaleOpt Lotto PDB EDS LDS GreedySE GreedyME"

ALG_LIST_WITH_PARAM="CaC CaC_SEOpt"
PARAM_LIST="2 3"

JAR_PATH="../target/stable-marriage-1.0.jar"

mkdir -p ../results/outputs/Uniform
mkdir -p ../results/outputs/Discrete40
mkdir -p ../results/outputs/Gauss40
mkdir -p ../results/outputs/UniformDiscrete40
mkdir -p ../results/outputs/Discrete40Gauss40
mkdir -p ../results/outputs/Gauss40Uniform

for i in {1..5}
do
		for n in {500..2000..500}
		do
				for impl in $ALG_LIST
				do
					## Uniform
					java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n ../datasets/Uniform/men${i}_n${n} ../datasets/Uniform/women${i}_n${n} >> ../results/outputs/Uniform/output${i}.txt
					## Discrete
					java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n ../datasets/Discrete/men${i}_n${n}_h40 ../datasets/Discrete/women${i}_n${n}_h40 >> ../results/outputs/Discrete40/output${i}.txt
					## Gauss
					java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n ../datasets/Gauss/men${i}_n${n}_h40 ../datasets/Gauss/women${i}_n${n}_h40 >> ../results/outputs/Gauss40/output${i}.txt
					## UniformDiscrete
					java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n ../datasets/Uniform/men${i}_n${n} ../datasets/Discrete/women${i}_n${n}_h40 >> ../results/outputs/UniformDiscrete40/output${i}.txt
					## DiscreteGauss
					java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n ../datasets/Discrete/men${i}_n${n}_h40 ../datasets/Gauss/women${i}_n${n}_h40 >> ../results/outputs/Discrete40Gauss40/output${i}.txt
					## GaussUniform
					java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n ../datasets/Gauss/men${i}_n${n}_h40 ../datasets/Uniform/women${i}_n${n} >> ../results/outputs/Gauss40Uniform/output${i}.txt
				done

				for impl in $ALG_LIST_WITH_PARAM
				do
					for param in $PARAM_LIST
					do
						## Uniform
						java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n $param ../datasets/Uniform/men${i}_n${n} ../datasets/Uniform/women${i}_n${n} >> ../results/outputs/Uniform/output${i}.txt
						## Discrete
						java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n $param ../datasets/Discrete/men${i}_n${n}_h40 ../datasets/Discrete/women${i}_n${n}_h40 >> ../results/outputs/Discrete40/output${i}.txt
						## Gauss
						java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n $param ../datasets/Gauss/men${i}_n${n}_h40 ../datasets/Gauss/women${i}_n${n}_h40 >> ../results/outputs/Gauss40/output${i}.txt
						## UniformDiscrete
						java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n $param ../datasets/Uniform/men${i}_n${n} ../datasets/Discrete/women${i}_n${n}_h40 >> ../results/outputs/UniformDiscrete40/output${i}.txt
						## DiscreteGauss
						java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n $param ../datasets/Discrete/men${i}_n${n}_h40 ../datasets/Gauss/women${i}_n${n}_h40 >> ../results/outputs/Discrete40Gauss40/output${i}.txt
						## GaussUniform
						java -cp ${JAR_PATH} gr.ntua.cslab.algorithms.$impl $n $param ../datasets/Gauss/men${i}_n${n}_h40 ../datasets/Uniform/women${i}_n${n} >> ../results/outputs/Gauss40Uniform/output${i}.txt
					done
				done
		done
done
