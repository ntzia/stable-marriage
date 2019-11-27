package cslab.ntua.gr;

import cslab.ntua.gr.algorithms.Abstract_SM_Algorithm;
import cslab.ntua.gr.algorithms.DACC;
import cslab.ntua.gr.algorithms.HybridMultiSearch;
import cslab.ntua.gr.algorithms.PowerBalance;
import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Marriage;
import cslab.ntua.gr.tools.Metrics;

public class Example
{  
    public static void main(String args[]) 
    {
        int i;
        // Test 4
        System.out.println("Example from 1985 paper \" THREE FAST ALGORITHMS...\" n=8");
        Agent[][] agents = new Agent[2][8];
        Agent ag;
        String line;
        int n = 8;
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

        Abstract_SM_Algorithm smp;
        Marriage matching;
        Metrics smpMetrics;

        // DACC
        smp = new DACC(n, agents, "D");
        matching = smp.match();
        smpMetrics = new Metrics(smp, matching, "DACC");
        smpMetrics.perform_checks();   
        smpMetrics.printPerformance();

        // PB
        int init = (int) (Math.ceil(Math.log(n) * Math.log(n) / (Math.log(2) * Math.log(2) * 10)));
        smp = new PowerBalance(n, agents, init*n, "SEq");
        matching = smp.match();
        smpMetrics = new Metrics(smp, matching, "PB");
        smpMetrics.perform_checks();   
        smpMetrics.printPerformance();

        // HMS
        int ls_max_step = (int) (Math.ceil(Math.log(n) * 10) / (Math.log(2)));
        int s_cnt = (int) (Math.ceil(Math.log(n) * 2 / Math.log(2)));
        smp = new HybridMultiSearch(n, agents, init*n, ls_max_step, s_cnt, "SEq");
        matching = smp.match();
        smpMetrics = new Metrics(smp, matching, "HMS");
        smpMetrics.perform_checks();
        smpMetrics.printPerformance();
    }
}