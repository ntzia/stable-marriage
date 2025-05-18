package cslab.ntua.gr;

import java.util.HashMap;
import java.util.Map;

import cslab.ntua.gr.algorithms.Abstract_SM_Algorithm;
import cslab.ntua.gr.algorithms.DACC;
import cslab.ntua.gr.algorithms.GS_MaleOpt;
import cslab.ntua.gr.algorithms.HybridMultiSearch;
import cslab.ntua.gr.algorithms.PowerBalance;
import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Marriage;
import cslab.ntua.gr.entities.Rotation_Poset;
import cslab.ntua.gr.entities.Rotations;
import cslab.ntua.gr.tools.Metrics;;

public class Find_Example
{  
    public static void main(String args[]) 
    {
        int n = 8;

        boolean done = false;
        Agent[][] agents = new Agent[2][n];
        Abstract_SM_Algorithm smp1, smp2, smp3;
        Marriage matching1, matching2, matching3;
        Metrics smpMetrics1, smpMetrics2, smpMetrics3;
        int init, s_cnt, ls_max_step;

        while(!done)
        {
            for (int i = 0; i < n; i++)
            {
                // New man
                agents[0][i] = new Agent(n, i, 0);
                // New woman
                agents[1][i] = new Agent(n, i, 1);
            }

            /*
            String menFile = "men";
            String womenFile = "women";
            GS_MaleOpt dummy = new GS_MaleOpt(n, menFile, womenFile);
            agents = dummy.getAgents();
            */

            // DACC
            smp1 = new DACC(n, agents, "D");
            matching1 = smp1.match();

            // PB
            init = (int) (Math.ceil(Math.log(n) * Math.log(n) / (Math.log(2) * Math.log(2) * 10)));
            smp2 = new PowerBalance(n, agents, init*n, "SEq");
            matching2 = smp2.match();

            // HMS
            ls_max_step = (int) (Math.ceil(Math.log(n) * 10) / (Math.log(2)));
            s_cnt = (int) (Math.ceil(Math.log(n) * 2 / Math.log(2)));
            smp3 = new HybridMultiSearch(n, agents, init*n, ls_max_step, s_cnt, "SEq");
            matching3 = smp3.match();

            Rotations rots = new Rotations(n, agents, null, null);

            if (matching1.getSECost() > matching2.getSECost() && matching2.getSECost() > matching3.getSECost() && rots.count > 4
                && matching1.getECost() >= matching2.getECost() && matching2.getECost() >= matching3.getECost())
            {
                smpMetrics1 = new Metrics(smp1, matching1, "DACC");
                smpMetrics1.printPerformance();
                System.out.println("DACC found " + matchingID(matching1, agents));
                smpMetrics2 = new Metrics(smp2, matching2, "PB");
                smpMetrics2.printPerformance();
                System.out.println("PB found " + matchingID(matching2, agents));
                smpMetrics3 = new Metrics(smp3, matching3, "HMS");
                smpMetrics3.printPerformance();
                System.out.println("HMS found " + matchingID(matching3, agents));

                System.out.println("Number of rotations = " + rots.count);

                System.out.println("Preferences:");
                for (int j = 0; j < n; j++)
                    System.out.println(("Man " + j + ": " + agents[0][j].getPrefList()).replace("[", "").replace("]", "").replace(",", "").replace(" ", ""));
                for (int j = 0; j < n; j++)
                    System.out.println(("Woman " + j + ": " + agents[1][j].getPrefList()).replace("[", "").replace("]", "").replace(",", "").replace(" ", ""));

                Rotation_Poset poset = new Rotation_Poset(agents, 0, rots, null, null);
                System.out.println("Rotation Poset:");
                System.out.println(poset);

                System.out.println("Lattice:");
                Abstract_SM_Algorithm maleOpt = new GS_MaleOpt(n, agents);
                Marriage maleOptMatching = maleOpt.match();
                recursive_print(maleOptMatching, agents, rots, new HashMap<Integer, Integer>());

                done = true;
            }
        }
    }

    public static void recursive_print(Marriage m, Agent[][] agents, Rotations rots, Map<Integer, Integer> visited)
    {
        int id = matchingID(m, agents);
        if (visited.get(id) != null) return;
        
        System.out.println("* Matching " + id + "  -  Eg = " + m.getECost() + " - SEq = " + m.getSECost());
        visited.put(id, 1);
        
        Marriage neighbour;

        // Search for neighbours
        for (int i = 0; i < rots.count; i++)
        {
            if (rots.isExposed(m, i, 0))
            {
                System.out.println("\t\t-> Can eliminate rotation " + i + " to go to " + matchingID(rots.eliminate(m, i, 0), agents));
            }
        }

        // Go to neighbours
        for (int i = 0; i < rots.count; i++)
        {
            if (rots.isExposed(m, i, 0))
            {
                neighbour = rots.eliminate(m, i, 0);
                recursive_print(neighbour, agents, rots, visited);
            }
        }
    }

    public static int matchingID(Marriage m, Agent[][] agents)
    {
        // Returns 
        String s = "";
        for (int i = 0; i < m.n; i++)
        {
            s += (agents[0][i].getAgentAt(m.mIndex[0][i]));
        }
        return Integer.parseInt(s);
    }

}