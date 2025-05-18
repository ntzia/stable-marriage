package cslab.ntua.gr.tools;

import java.util.List;

import cslab.ntua.gr.algorithms.Abstract_SM_Algorithm;
import cslab.ntua.gr.algorithms.GS_FemaleOpt;
import cslab.ntua.gr.algorithms.GS_MaleOpt;
import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Marriage;
import cslab.ntua.gr.entities.Rotation;
import cslab.ntua.gr.entities.Rotation_Poset;
import cslab.ntua.gr.entities.Rotations;
import cslab.ntua.gr.tools.Metrics;

public class TestPoset
{  
    /**
     * True iff graph is a dag (directed acyclic graph).
     */
    public static boolean isDag(Rotation_Poset poset) 
    {
        return poset.topSort() != null;
    }

    public static void main(String args[]) 
    {
        Metrics smpMetrics;
        int i, n;
        Abstract_SM_Algorithm smp, smp_femaleopt, smp_maleopt;
        Agent[][] agents;
        Marriage m, female_opt, male_opt;
        Rotations rots;
        Rotation_Poset poset;
        List<Rotation> order;

        // Test 1
        System.out.println("Test 1: Random Test Case n=20");
        n = 20;
        System.out.println("--------------------------------");
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        m = smp.match();  
        rots = new Rotations(n, agents, null, null);
        System.out.print("Constructing graph for men: ");
        poset = new Rotation_Poset(agents, 0, rots, null, null);
        System.out.println("The graph " + (isDag(poset)?"is":"is not") + " a dag");
        System.out.print("MaleOpt->FemaleOpt: ");
        order = poset.topSort();
        for (Rotation r : order)
        {
            if (rots.isExposed(m, r.id, 0)) 
            {
                m = rots.eliminate(m, r.id, 0);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
            else
            {
                System.out.println();
                System.out.println("Error: Rotation" + r.id + " is not exposed!");
                System.exit(1);
            }
        }
        smp_femaleopt = new GS_FemaleOpt(n, agents);
        female_opt = smp_femaleopt.match();
        if (!m.isEqualTo(female_opt)) System.out.println("Error!");
        else System.out.println("Success!!");

        System.out.print("Constructing graph for women: ");
        poset = new Rotation_Poset(agents, 1, rots, null, null);
        System.out.println("The graph " + (isDag(poset)?"is":"is not") + " a dag");
        System.out.print("FemaleOpt->MaleOpt: ");
        order = poset.topSort();
        for (Rotation r : order)
        {
            if (rots.isExposed(m, r.id, 1)) 
            {
                m = rots.eliminate(m, r.id, 1);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
            else
            {
                System.out.println();
                System.out.println("Error: Rotation" + r.id + " is not exposed!");
                System.exit(1);
            }
        }
        smp_maleopt = new GS_MaleOpt(n, agents);
        male_opt = smp_maleopt.match();
        if (!m.isEqualTo(male_opt)) System.out.println("Error!");
        else System.out.println("Success!!\n");

        // Test 2
        System.out.println("Test 2: Random Test Case n=200");
        n = 200;
        System.out.println("--------------------------------");
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        m = smp.match();  
        rots = new Rotations(n, agents, null, null);
        System.out.print("Constructing graph for men: ");
        poset = new Rotation_Poset(agents, 0, rots, null, null);
        System.out.println("The graph " + (isDag(poset)?"is":"is not") + " a dag");
        System.out.print("MaleOpt->FemaleOpt: ");
        order = poset.topSort();
        for (Rotation r : order)
        {
            if (rots.isExposed(m, r.id, 0)) 
            {
                m = rots.eliminate(m, r.id, 0);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
            else
            {
                System.out.println();
                System.out.println("Error: Rotation" + r.id + " is not exposed!");
                System.exit(1);
            }
        }
        smp_femaleopt = new GS_FemaleOpt(n, agents);
        female_opt = smp_femaleopt.match();
        if (!m.isEqualTo(female_opt)) System.out.println("Error!");
        else System.out.println("Success!!");

        System.out.print("Constructing graph for women: ");
        poset = new Rotation_Poset(agents, 1, rots, null, null);
        System.out.println("The graph " + (isDag(poset)?"is":"is not") + " a dag");
        System.out.print("FemaleOpt->MaleOpt: ");
        order = poset.topSort();
        for (Rotation r : order)
        {
            if (rots.isExposed(m, r.id, 1)) 
            {
                m = rots.eliminate(m, r.id, 1);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
            else
            {
                System.out.println();
                System.out.println("Error: Rotation" + r.id + " is not exposed!");
                System.exit(1);
            }
        }
        smp_maleopt = new GS_MaleOpt(n, agents);
        male_opt = smp_maleopt.match();
        if (!m.isEqualTo(male_opt)) System.out.println("Error!");
        else System.out.println("Success!!\n");

        // Test 3
        System.out.println("Test 3: Random Test Case n=1000");
        n = 1000;
        System.out.println("--------------------------------");
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        m = smp.match();  
        rots = new Rotations(n, agents, null, null);
        System.out.print("Constructing graph for men: ");
        poset = new Rotation_Poset(agents, 0, rots, null, null);
        System.out.println("The graph " + (isDag(poset)?"is":"is not") + " a dag");
        System.out.print("MaleOpt->FemaleOpt: ");
        order = poset.topSort();
        for (Rotation r : order)
        {
            if (rots.isExposed(m, r.id, 0)) 
            {
                m = rots.eliminate(m, r.id, 0);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
            else
            {
                System.out.println();
                System.out.println("Error: Rotation" + r.id + " is not exposed!");
                System.exit(1);
            }
        }
        smp_femaleopt = new GS_FemaleOpt(n, agents);
        female_opt = smp_femaleopt.match();
        if (!m.isEqualTo(female_opt)) System.out.println("Error!");
        else System.out.println("Success!!");

        System.out.print("Constructing graph for women: ");
        poset = new Rotation_Poset(agents, 1, rots, null, null);
        System.out.println("The graph " + (isDag(poset)?"is":"is not") + " a dag");
        System.out.print("FemaleOpt->MaleOpt: ");
        order = poset.topSort();
        for (Rotation r : order)
        {
            if (rots.isExposed(m, r.id, 1)) 
            {
                m = rots.eliminate(m, r.id, 1);
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
            }
            else
            {
                System.out.println();
                System.out.println("Error: Rotation" + r.id + " is not exposed!");
                System.exit(1);
            }
        }
        smp_maleopt = new GS_MaleOpt(n, agents);
        male_opt = smp_maleopt.match();
        if (!m.isEqualTo(male_opt)) System.out.println("Error!");
        else System.out.println("Success!!\n");

        // Test 4
        System.out.println("\nTest 4: Test Case from 1985 paper \" THREE FAST ALGORITHMS...\" n=8");
        System.out.println("--------------------------------");
        n = 8;
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

        smp = new GS_MaleOpt(8, agents);
        rots = new Rotations(8, agents, null, null);
        poset = new Rotation_Poset(agents, 0, rots, null, null);
        m = smp.match();

        System.out.println("Graph: " + poset);
        System.out.println("The graph " + (isDag(poset)?"is":"is not") + " a dag");

        // Test 5
        long startTime, endTime, elapsedTime;
        double time;
        System.out.println("\nTest 5: Scalability (Men)");
        System.out.println("--------------------------------");

        startTime = System.nanoTime();
        n = 250;
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        rots = new Rotations(n, agents, null, null);    
        poset = new Rotation_Poset(agents, 0, rots, null, null);
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;
        System.out.println("n = 250 : " + time + " sec");

        startTime = System.nanoTime();
        n = 500;
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        rots = new Rotations(n, agents, null, null);    
        poset = new Rotation_Poset(agents, 0, rots, null, null);
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;
        System.out.println("n = 500 : " + time + " sec");

        startTime = System.nanoTime();
        n = 1000;
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        rots = new Rotations(n, agents, null, null);    
        poset = new Rotation_Poset(agents, 0, rots, null, null);
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;
        System.out.println("n = 1000 : " + time + " sec");

        startTime = System.nanoTime();
        n = 2000;
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        rots = new Rotations(n, agents, null, null);    
        poset = new Rotation_Poset(agents, 0, rots, null, null);
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;
        System.out.println("n = 2000 : " + time + " sec");

        startTime = System.nanoTime();
        n = 4000;
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        rots = new Rotations(n, agents, null, null);    
        poset = new Rotation_Poset(agents, 0, rots, null, null);
        endTime = System.nanoTime();
        elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;
        System.out.println("n = 4000 : " + time + " sec");
    }
}