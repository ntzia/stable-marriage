package cslab.ntua.gr.tools;

import java.util.concurrent.ThreadLocalRandom;

import cslab.ntua.gr.algorithms.Abstract_SM_Algorithm;
import cslab.ntua.gr.algorithms.GS_FemaleOpt;
import cslab.ntua.gr.algorithms.GS_MaleOpt;
import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Marriage;
import cslab.ntua.gr.entities.Rotation;
import cslab.ntua.gr.entities.Rotations;
import gr.ntua.cslab.tools.Metrics;

public class TestRotations
{
    public static void main(String args[]) 
    {
        Metrics smpMetrics;
        int i, n;
        Abstract_SM_Algorithm smp, smp_maleopt, smp_femaleopt;
        Agent[][] agents;
        Marriage m, male_opt, female_opt;

        // Test 1
        System.out.println("Test 1: Random Test Case n=20");
        n = 20;
        System.out.println("--------------------------------");
        System.out.print("MaleOpt->FemaleOpt: ");
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        Rotations rot_test1 = new Rotations(n, agents, null, null);
        smp_femaleopt = new GS_FemaleOpt(n, agents);
        female_opt = smp_femaleopt.match();
        m = smp.match();    
        while (!m.isEqualTo(female_opt))
        {
            i = ThreadLocalRandom.current().nextInt(0, rot_test1.count);
            if (rot_test1.isExposed(m, i, 0)) 
            {
                m = rot_test1.eliminate(m, i, 0);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
        }
        System.out.println("Success!");

        System.out.print("FemaleOpt->MaleOpt: ");
        smp = new GS_FemaleOpt(n, agents);
        smp_maleopt = new GS_MaleOpt(n, agents);
        male_opt = smp_maleopt.match();
        m = smp.match();  
        while (!m.isEqualTo(male_opt))
        {
            i = ThreadLocalRandom.current().nextInt(0, rot_test1.count);
            if (rot_test1.isExposed(m, i, 1)) 
            {
                m = rot_test1.eliminate(m, i, 1);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
        }
        System.out.println("Success!\n");


        // Test 2
        System.out.println("Test 2: Random Test Case n=200");
        n = 200;
        System.out.println("--------------------------------");
        System.out.print("MaleOpt->FemaleOpt: ");
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        Rotations rot_test2 = new Rotations(n, agents, null, null);
        smp_femaleopt = new GS_FemaleOpt(n, agents);
        female_opt = smp_femaleopt.match();
        m = smp.match();    
        while (!m.isEqualTo(female_opt))
        {
            i = ThreadLocalRandom.current().nextInt(0, rot_test2.count);
            if (rot_test2.isExposed(m, i, 0)) 
            {
                m = rot_test2.eliminate(m, i, 0);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
        }
        System.out.println("Success!");

        System.out.print("FemaleOpt->MaleOpt: ");
        smp = new GS_FemaleOpt(n, agents);
        smp_maleopt = new GS_MaleOpt(n, agents);
        male_opt = smp_maleopt.match();
        m = smp.match();  
        while (!m.isEqualTo(male_opt))
        {
            i = ThreadLocalRandom.current().nextInt(0, rot_test2.count);
            if (rot_test2.isExposed(m, i, 1)) 
            {
                m = rot_test2.eliminate(m, i, 1);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
        }
        System.out.println("Success!\n");


        // Test 3
        System.out.println("Test 3: Random Test Case n=1000");
        n = 1000;
        System.out.println("--------------------------------");
        System.out.print("MaleOpt->FemaleOpt: ");
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        Rotations rot_test3 = new Rotations(n, agents, null, null);
        smp_femaleopt = new GS_FemaleOpt(n, agents);
        female_opt = smp_femaleopt.match();
        m = smp.match();    
        while (!m.isEqualTo(female_opt))
        {
            i = ThreadLocalRandom.current().nextInt(0, rot_test3.count);
            if (rot_test3.isExposed(m, i, 0)) 
            {
                m = rot_test3.eliminate(m, i, 0);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
        }
        System.out.println("Success!");

        System.out.print("FemaleOpt->MaleOpt: ");
        smp = new GS_FemaleOpt(n, agents);
        smp_maleopt = new GS_MaleOpt(n, agents);
        male_opt = smp_maleopt.match();
        m = smp.match();  
        while (!m.isEqualTo(male_opt))
        {
            i = ThreadLocalRandom.current().nextInt(0, rot_test3.count);
            if (rot_test3.isExposed(m, i, 1)) 
            {
                m = rot_test3.eliminate(m, i, 1);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
        }
        System.out.println("Success!\n");


        // Test 4
        System.out.println("Test 4: Test Case from 1985 paper \" THREE FAST ALGORITHMS...\" n=8");
        System.out.println("--------------------------------");
        agents = new Agent[2][8];
        Agent ag;
        String line;
        // MEN
        int[] arr00 = {5,7,1,2,6,8,4,3};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr00[i] - 1) + " ";
        }
        ag = new Agent(8, 0, 0, line);
        agents[0][0] = ag;
        int[] arr01 = {2,3,7,5,4,1,8,6};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr01[i] - 1) + " ";
        }
        ag = new Agent(8, 1, 0, line);
        agents[0][1] = ag;
        int[] arr02 = {8,5,1,4,6,2,3,7};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr02[i] - 1) + " ";
        }
        ag = new Agent(8, 2, 0, line);
        agents[0][2] = ag;
        int[] arr03 = {3,2,7,4,1,6,8,5};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr03[i] - 1) + " ";
        }
        ag = new Agent(8, 3, 0, line);
        agents[0][3] = ag;
        int[] arr04 = {7,2,5,1,3,6,8,4};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr04[i] - 1) + " ";
        }
        ag = new Agent(8, 4, 0, line);
        agents[0][4] = ag;
        int[] arr05 = {1,6,7,5,8,4,2,3};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr05[i] - 1) + " ";
        }
        ag = new Agent(8, 5, 0, line);
        agents[0][5] = ag;
        int[] arr06 = {2,5,7,6,3,4,8,1};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr06[i] - 1) + " ";
        }
        ag = new Agent(8, 6, 0, line);
        agents[0][6] = ag;
        int[] arr07 = {3,8,4,5,7,2,6,1};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr07[i] - 1) + " ";
        }
        ag = new Agent(8, 7, 0, line);
        agents[0][7] = ag;

        // WOMEN
        int[] arr10 = {5,3,7,6,1,2,8,4};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr10[i] - 1) + " ";
        }
        ag = new Agent(8, 0, 1, line);
        agents[1][0] = ag;
        int[] arr11 = {8,6,3,5,7,2,1,4};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr11[i] - 1) + " ";
        }
        ag = new Agent(8, 1, 1, line);
        agents[1][1] = ag;
        int[] arr12 = {1,5,6,2,4,8,7,3};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr12[i] - 1) + " ";
        }
        ag = new Agent(8, 2, 1, line);
        agents[1][2] = ag;
        int[] arr13 = {8,7,3,2,4,1,5,6};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr13[i] - 1) + " ";
        }
        ag = new Agent(8, 3, 1, line);
        agents[1][3] = ag;
        int[] arr14 = {6,4,7,3,8,1,2,5};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr14[i] - 1) + " ";
        }
        ag = new Agent(8, 4, 1, line);
        agents[1][4] = ag;
        int[] arr15 = {2,8,5,4,6,3,7,1};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr15[i] - 1) + " ";
        }
        ag = new Agent(8, 5, 1, line);
        agents[1][5] = ag;
        int[] arr16 = {7,5,2,1,8,6,4,3};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr16[i] - 1) + " ";
        }
        ag = new Agent(8, 6, 1, line);
        agents[1][6] = ag;
        int[] arr17 = {7,4,1,5,2,3,6,8};
        line = "";
        for (i = 0; i < 8; i++)
        {
            line += (arr17[i] - 1) + " ";
        }
        ag = new Agent(8, 7, 1, line);
        agents[1][7] = ag;

        System.out.println("MaleOpt->FemaleOpt: ");
        smp = new GS_MaleOpt(8, agents);
        Rotations rot_test4 = new Rotations(8, agents, null, null);
        smp_femaleopt = new GS_FemaleOpt(8, agents);
        female_opt = smp_femaleopt.match();
        m = smp.match();
        System.out.println(m.marriageToStr2(agents) + " ->");        
        while (!m.isEqualTo(female_opt))
        {
            i = ThreadLocalRandom.current().nextInt(0, rot_test4.count);
            if (rot_test4.isExposed(m, i, 0)) 
            {
                m = rot_test4.eliminate(m, i, 0);
                System.out.println(m.marriageToStr2(agents) + " ->");         
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
        }
        System.out.println("Success!");

        System.out.println("FemaleOpt->MaleOpt: ");
        smp = new GS_FemaleOpt(8, agents);
        smp_maleopt = new GS_MaleOpt(8, agents);
        male_opt = smp_maleopt.match();
        m = smp.match();
        System.out.println(m.marriageToStr2(agents) + " ->");        
        while (!m.isEqualTo(male_opt))
        {
            i = ThreadLocalRandom.current().nextInt(0, rot_test4.count);
            if (rot_test4.isExposed(m, i, 1)) 
            {
                m = rot_test4.eliminate(m, i, 1);
                System.out.println(m.marriageToStr2(agents) + " ->");         
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
        }
        System.out.println("Success!\n");


        // Test 5
        System.out.println("Test 5: Validating Method of Calculating Women Rotations for Random Test Case n=200");
        n = 200;
        System.out.println("--------------------------------");
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        Rotations rot_test51 = new Rotations(n, agents, null, null);
        Rotations rot_test52 = new Rotations(n, agents);
        rot_test52.find_women_rotations(null, null);
        for (Rotation rot : rot_test51.women_rotations)
        {
            if (!rot_test52.women_rotations.contains(rot))
            {
                System.err.println("Error: Rotation obtained by shifting a rotation of men can not be found in rotations obtained by breakmarriages!");
                System.exit(1);
            }
        }
        System.out.println("Success! Rotations obtained by shifting rotations of men are those obtained by the method of breakmarriages!");
    }
}