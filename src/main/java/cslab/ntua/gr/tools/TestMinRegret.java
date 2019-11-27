package cslab.ntua.gr.tools;

import cslab.ntua.gr.algorithms.Abstract_SM_Algorithm;
import cslab.ntua.gr.algorithms.BiLS;
import cslab.ntua.gr.algorithms.DACC;
import cslab.ntua.gr.algorithms.EROM;
import cslab.ntua.gr.algorithms.ESMA;
import cslab.ntua.gr.algorithms.GS_FemaleOpt;
import cslab.ntua.gr.algorithms.GS_MaleOpt;
import cslab.ntua.gr.algorithms.Lotto;
import cslab.ntua.gr.algorithms.MinRegret;
import cslab.ntua.gr.algorithms.PowerBalance;
import cslab.ntua.gr.algorithms.ROM;
import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Marriage;
import cslab.ntua.gr.tools.Metrics;

public class TestMinRegret
{
    public static void main(String args[]) 
    {
        Metrics smpMetrics;
        int n;
        Abstract_SM_Algorithm smp, test;
        Agent[][] agents;
        Marriage m, test_m;
        int n_arr[] = {10, 250, 750, 2000};

        // Test 1
        for (int i = 0; i <= 3; i++)
        {
            System.out.println("Test " + i + ": Random Test Case n=" + n_arr[i]);
            n = n_arr[i];
            System.out.println("--------------------------------");
            smp = new MinRegret(n, null, null);
            m = smp.match();
            smpMetrics = new Metrics(smp, m, "");
            smpMetrics.perform_checks();
            agents = smp.getAgents();

            System.out.print("Checking against GS_MaleOpt: ");
            test = new GS_MaleOpt(n, agents);
            test_m = test.match();
            if (test_m.hasBetterRCost(m)) System.out.println("Error: Not Minimum Regret!");
            else System.out.println("Ok!");

            System.out.print("Checking against GS_FemaleOpt: ");
            test = new GS_FemaleOpt(n, agents);
            test_m = test.match();
            if (test_m.hasBetterRCost(m)) System.out.println("Error: Not Minimum Regret!");
            else System.out.println("Ok!");

            System.out.print("Checking against ESMA: ");
            test = new ESMA(n, agents);
            test_m = test.match();
            if (test_m.hasBetterRCost(m)) System.out.println("Error: Not Minimum Regret!");
            else System.out.println("Ok!");

            System.out.print("Checking against Lotto: ");
            test = new Lotto(n, agents);
            test_m = test.match();
            if (test_m.hasBetterRCost(m)) System.out.println("Error: Not Minimum Regret!");
            else System.out.println("Ok!");

            System.out.print("Checking against ROM: ");
            test = new ROM(n, agents);
            test_m = test.match();
            if (test_m.hasBetterRCost(m)) System.out.println("Error: Not Minimum Regret!");
            else System.out.println("Ok!");

            System.out.print("Checking against EROM: ");
            test = new EROM(n, agents);
            test_m = test.match();
            if (test_m.hasBetterRCost(m)) System.out.println("Error: Not Minimum Regret!");
            else System.out.println("Ok!");

            System.out.print("Checking against BiLS: ");
            test = new BiLS(n, agents, 0.0, "Bal");
            test_m = test.match();
            if (test_m.hasBetterRCost(m)) System.out.println("Error: Not Minimum Regret!");
            else System.out.println("Ok!");

            System.out.print("Checking against PowerBalance: ");
            test = new PowerBalance(n, agents, 5, "Bal");
            test_m = test.match();
            if (test_m.hasBetterRCost(m)) System.out.println("Error: Not Minimum Regret!");
            else System.out.println("Ok!");

            System.out.print("Checking against DACC(D): ");
            test = new DACC(n, agents, "D");
            test_m = test.match();
            if (test_m.hasBetterRCost(m)) System.out.println("Error: Not Minimum Regret!");
            else System.out.println("Ok!");

            System.out.print("Checking against DACC(R): ");
            test = new DACC(n, agents, "R");
            test_m = test.match();
            if (test_m.hasBetterRCost(m)) System.out.println("Error: Not Minimum Regret!");
            else System.out.println("Ok!");

            System.out.println();
        }

    }
}