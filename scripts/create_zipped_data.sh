#!/bin/bash

mkdir -p ../datasets/Uniform
mkdir -p ../datasets/Gauss
mkdir -p ../datasets/Discrete

rm ../datasets/Uniform/*
rm ../datasets/Gauss/*
rm ../datasets/Discrete/*

JAR_PATH="../target/stable-marriage-2.0.jar"

for i in {1..5}
do 
	for n in {50..200..50}
	do
		java -cp ${JAR_PATH} gr.ntua.cslab.data.Zip_UniformDataGenerator ${n} ../datasets/Uniform/men${i}_n${n}
		java -cp ${JAR_PATH} gr.ntua.cslab.data.Zip_UniformDataGenerator ${n} ../datasets/Uniform/women${i}_n${n}
	done
done

for i in {1..5}
do 
	for n in {50..200..50}
	do
		java -cp ${JAR_PATH} gr.ntua.cslab.data.Zip_DiscreteDataGenerator ${n} 0.4 ../datasets/Discrete/men${i}_n${n}_h40
		java -cp ${JAR_PATH} gr.ntua.cslab.data.Zip_DiscreteDataGenerator ${n} 0.4 ../datasets/Discrete/women${i}_n${n}_h40
	done
done

for i in {1..5}
do 
	for n in {50..200..50}
	do
		java -cp ${JAR_PATH} gr.ntua.cslab.data.Zip_GaussDataGenerator ${n} 0.4 ../datasets/Gauss/men${i}_n${n}_h40
		java -cp ${JAR_PATH} gr.ntua.cslab.data.Zip_GaussDataGenerator ${n} 0.4 ../datasets/Gauss/women${i}_n${n}_h40
	done
done