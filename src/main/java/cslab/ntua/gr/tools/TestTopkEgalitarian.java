package cslab.ntua.gr.tools;

import java.util.ArrayList;
import java.util.List;

import cslab.ntua.gr.algorithms.EnumerateAllSM;
import cslab.ntua.gr.algorithms.TopkEgalitarian;
import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Marriage;
import cslab.ntua.gr.tools.Metrics;

public class TestTopkEgalitarian
{
    public static void main(String args[]) 
    {
        Metrics smpMetrics;
        int n;
        Agent[][] agents;
        Marriage m;
        int n_arr[] = {10, 20, 30, 40};

        // Test 1
        for (int i = 0; i <= 3; i++)
        {
            System.out.println("Test " + i + ": Random Test Case n=" + n_arr[i]);
            n = n_arr[i];
            System.out.println("--------------------------------");
            TopkEgalitarian smp = new TopkEgalitarian(n, null, null, -1);
            List<Marriage> topk_marriages = new ArrayList<Marriage>();
            m = smp.match();
            if (m != null)
            {
                smpMetrics = new Metrics(smp, m, "");
                smpMetrics.perform_checks();
                topk_marriages.add(smp.match());
                while (true)
                {
                    m = smp.get_next_match();
                    if (m == null) break;
                    smpMetrics = new Metrics(smp, m, "");
                    smpMetrics.perform_checks();
                    topk_marriages.add(m);
                }
            }

            // Check if topk returns solutions in order of egalitarian cost
            for (int j = 0; j < topk_marriages.size() - 1; j++)
            {
                if (topk_marriages.get(j).getECost() > topk_marriages.get(j + 1).getECost())
                {
                    System.out.println("Error: Not in order of egalitarian cost!");
                    smpMetrics = new Metrics(smp, topk_marriages.get(j), "Top-k (k=" + (j) + ")");
                    smpMetrics.printPerformance();
                    smpMetrics = new Metrics(smp, topk_marriages.get(j + 1), "Top-k (k=" + (j + 1) + ")");
                    smpMetrics.printPerformance();
                    break;
                }
            }

            agents = smp.getAgents();

            System.out.println("Checking against EnumerateAllSM...");
            EnumerateAllSM allsm = new EnumerateAllSM(n, agents);
            List<Marriage> allsm_sols = allsm.allStableMatchings();
            for (Marriage allsm_m : allsm_sols)
            {
                smpMetrics = new Metrics(allsm, allsm_m, "");
                smpMetrics.perform_checks();
            }

            // Check if number of solutions is the same
            if (topk_marriages.size() != allsm_sols.size())
            {
                System.out.println("Error: Number of solutions is not the same!");
                System.out.println("TopkEgalitarian: " + topk_marriages.size());
                System.out.println("EnumerateAllSM: " + allsm_sols.size());
            }

            // Check if every solution in topk is also in allsm
            for (Marriage topk_m : topk_marriages)
            {
                boolean found = false;
                for (Marriage allsm_m : allsm_sols)
                {
                    if (topk_m.isEqualTo(allsm_m))
                    {
                        found = true;
                        break;
                    }
                }
                if (!found)
                    System.out.println("Error: Solution not in allsm!");

            }

            System.out.println();
        }

    }
}
