#!/bin/bash

mkdir -p ../datasets/Uniform
mkdir -p ../datasets/Gauss
mkdir -p ../datasets/Discrete

rm ../datasets/Uniform/*
rm ../datasets/Gauss/*
rm ../datasets/Discrete/*

JAR_PATH="../target/stable-marriage-1.0.jar"

for i in {1..5}
do 
	for n in {500..2000..500}
	do
		java -cp ${JAR_PATH} gr.ntua.cslab.data.UniformDataGenerator ${n} ../datasets/Uniform/men${i}_n${n}
		java -cp ${JAR_PATH} gr.ntua.cslab.data.UniformDataGenerator ${n} ../datasets/Uniform/women${i}_n${n}
	done
done

for i in {1..5}
do 
	for n in {500..2000..500}
	do
		java -cp ${JAR_PATH} gr.ntua.cslab.data.DiscreteDataGenerator ${n} 0.4 ../datasets/Discrete/men${i}_n${n}_h40
		java -cp ${JAR_PATH} gr.ntua.cslab.data.DiscreteDataGenerator ${n} 0.4 ../datasets/Discrete/women${i}_n${n}_h40
	done
done

for i in {1..5}
do 
	for n in {500..2000..500}
	do
		java -cp ${JAR_PATH} gr.ntua.cslab.data.GaussDataGenerator ${n} 0.4 ../datasets/Gauss/men${i}_n${n}_h40
		java -cp ${JAR_PATH} gr.ntua.cslab.data.GaussDataGenerator ${n} 0.4 ../datasets/Gauss/women${i}_n${n}_h40
	done
done