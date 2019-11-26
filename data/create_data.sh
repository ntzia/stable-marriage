#!/bin/bash

JAR_PATH="../target/stable-marriage-1.0.jar"

mkdir -p Uniform
mkdir -p Gauss
mkdir -p Discrete
mkdir -p Real

rm Uniform/*
rm Gauss/*
rm Discrete/*
rm Real/*

for i in {1..5}
do 
	for n in {50..200..50}
	do
		java -cp ${JAR_PATH} cslab.ntua.gr.data.UniformDataGenerator ${n} Uniform/men${i}_n${n}.in
		java -cp ${JAR_PATH} cslab.ntua.gr.data.UniformDataGenerator ${n} Uniform/women${i}_n${n}.in
	done
done

for i in {1..5}
do 
	for n in {50..200..50}
	do
		java -cp ${JAR_PATH} cslab.ntua.gr.data.DiscreteDataGenerator ${n} 0.4 Discrete/men${i}_n${n}_h40.in
		java -cp ${JAR_PATH} cslab.ntua.gr.data.DiscreteDataGenerator ${n} 0.4 Discrete/women${i}_n${n}_h40.in
	done
done

for i in {1..5}
do 
	for n in {50..200..50}
	do
		java -cp ${JAR_PATH} cslab.ntua.gr.data.GaussDataGenerator ${n} 0.4 Gauss/men${i}_n${n}_h40.in
		java -cp ${JAR_PATH} cslab.ntua.gr.data.GaussDataGenerator ${n} 0.4 Gauss/women${i}_n${n}_h40.in
	done
done

for i in {1..50}
do
       for n in {20..100..10}
       do
               ./gen_real.py ${n} Real/men${i}_n${n}.in Real/women${i}_n${n}.in
       done
done