package gr.ntua.cslab.tools;

import java.io.*;
import java.util.*;
import org.apache.commons.cli.*;

import gr.ntua.cslab.algorithms.*;
import gr.ntua.cslab.entities.Rotation;
import gr.ntua.cslab.entities.Rotations;
import gr.ntua.cslab.entities.Rotation_Poset;
import gr.ntua.cslab.entities.Agent;
import gr.ntua.cslab.entities.Marriage;

public class TestApprox
{
    public static void main(String args[]) 
    {
        Metrics smpMetrics;
        int delta, n, maleOptSEq, femaleOptSEq;
        Abstract_SM_Algorithm smp, smp_maleopt, smp_femaleopt, test;
        Agent[][] agents;
        Marriage m, maleOptMatching, femaleOptMatching, test_m;
        double e;

        // Test 1
        System.out.println("Test 1: Random Test Case n=250");
        n = 250;
        System.out.println("--------------------------------");
        smp = new Approx(n, null, null, 0.1);
        m = smp.match();
        agents = smp.getAgents();
        for (e = 0.07; e >= 0.02; e -= 0.01)
        {
            System.out.println("e = " + e + ": ");     
            smp = new Approx(n, agents, e);
            m = smp.match();
            smp_maleopt = new GS_MaleOpt(n, agents);
            maleOptMatching = smp_maleopt.match();
            maleOptSEq = maleOptMatching.getSECost();
            smp_femaleopt = new GS_FemaleOpt(n, agents);
            femaleOptMatching = smp_femaleopt.match();
            femaleOptSEq = femaleOptMatching.getSECost();
            if (maleOptSEq <= femaleOptSEq) delta = maleOptSEq;
            else delta = femaleOptSEq; 
            if (m.getSECost() > e * delta)  
            {
                System.out.println("Could not achieve the requested approximation guarrantee...");
                System.out.println("Checking the results of other algorithms:");

                test = new ESMA(n, agents);
                test_m = test.match();
                if (test_m.getSECost() <= e * delta) 
                {
                    System.err.println("Error: ESMA achieved the requested ratio!");
                    System.exit(1);
                }

                test = new Lotto(n, agents);
                test_m = test.match();
                if (test_m.getSECost() <= e * delta) 
                {
                    System.err.println("Error: Lotto achieved the requested ratio!");
                    System.exit(1);
                }

                test = new ROM(n, agents);
                test_m = test.match();
                if (test_m.getSECost() <= e * delta)
                {
                    System.err.println("Error: ROM achieved the requested ratio!");
                    System.exit(1);
                } 

                test = new EROM(n, agents);
                test_m = test.match();
                if (test_m.getSECost() <= e * delta)
                {
                    System.err.println("Error: EROM achieved the requested ratio!");
                    System.exit(1);
                } 

                test = new BiLS(n, agents, 0.0, "SEq");
                test_m = test.match();
                if (test_m.getSECost() <= e * delta) 
                {
                    System.err.println("Error: BiLS achieved the requested ratio!");
                    System.exit(1);
                }

                test = new PowerBalance(n, agents, 5, "SEq");
                test_m = test.match();
                if (test_m.getSECost() <= e * delta) 
                {
                    System.err.println("Error: PowerBalance achieved the requested ratio!");
                    System.exit(1);
                } 

                test = new DACC(n, agents, "D");
                test_m = test.match();
                if (test_m.getSECost() <= e * delta) 
                {
                    System.err.println("Error: DACC_D achieved the requested ratio!");
                    System.exit(1);
                } 

                test = new DACC(n, agents, "R");
                test_m = test.match();
                if (test_m.getSECost() <= e * delta) 
                {
                    System.err.println("Error: DACC_R achieved the requested ratio!");
                    System.exit(1);
                } 

                test = new Hybrid(n, agents, 10 * n, "SEq");
                test_m = test.match();
                if (test_m.getSECost() <= e * delta) 
                {
                    System.err.println("Error: Hybrid achieved the requested ratio!");
                    System.exit(1);
                } 

                System.out.println("Sucess!");
                continue;
            }
            smpMetrics = new Metrics(smp, m, "");
            smpMetrics.perform_checks();  

            if (m.getSECost() > e * delta)
            {
                System.err.println("Error: Returned a matching with a higher ratio than requested!");
            }  
            System.out.println("Approximation ratio = " + (m.getSECost() * 1.0 / delta)); 
        }
    }
}