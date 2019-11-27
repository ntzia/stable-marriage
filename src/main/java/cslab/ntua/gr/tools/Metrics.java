package cslab.ntua.gr.tools;

import java.util.ArrayList;
import java.util.List;

import cslab.ntua.gr.algorithms.Abstract_SM_Algorithm;
import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Marriage;

public class Metrics
{
	private int n;
	private Agent[][] agents;
	private Marriage matching;
    private String name;
    private long rounds;
    private double time;

    public Metrics(Abstract_SM_Algorithm instance, Marriage m, String name)
    {
    	this.n = instance.getSize();
    	this.agents = instance.getAgents();
        this.rounds = instance.getRounds();
        this.time = instance.getTime();
        this.matching = m;
        this.name = name;
    }

    private int matchOf(int man)
    {
        return agents[0][man].getAgentAt(matching.mIndex[0][man]);
    }

    public void perform_checks()
    {
        if (!this.isMatching()) System.err.println("Error: Not a Matching!");
        if (!this.isPerfect()) System.err.println("Error: Matching not perfect!");
        int bpairs = this.blockingPairs();
        if (bpairs != 0) System.err.println("Error: Terminated with " + bpairs + " blocking pairs!");
    }

    public void printMatching()
    {
        for (int i = 0; i < n; i++) 
            System.out.println("Man " + i + " married to Woman " + matchOf(i));
    }

    public boolean isMatching()
    {
    	int spouse, spouseOfspouse;
        for (int i = 0; i < n; i++)
        {
            spouse = agents[0][i].getAgentAt(matching.mIndex[0][i]);
            spouseOfspouse = agents[1][spouse].getAgentAt(matching.mIndex[1][spouse]);
            if (spouseOfspouse != i) return false;
        }  
        return true;
    }

    public boolean isPerfect()
    {
        int[] tick = new int[n];
        for (int i = 0; i < n; i++) tick[matchOf(i)]++;
        for (int i = 0; i < n; i++)
        {
            if (tick[i] != 1) return false;
        }      
        return true;
    }

    public int blockingPairs()
    {
    	// Check for bpairs from the point of view of men
        int cnt = 0;
        int mIndex_of_i, mIndex_of_j;
        for (int i = 0; i < n; i++)
        {
            mIndex_of_i = matching.mIndex[0][i];
            for (int j = 0; j < n; j++)
            {
                mIndex_of_j = matching.mIndex[1][j];
                if (j != matchOf(i) && agents[1][j].getRankOf(i) < mIndex_of_j && agents[0][i].getRankOf(j) < mIndex_of_i) cnt++;
            }
        }
        // Compare against the point of view of women
        int cnt2 = 0;
        int mIndex_of_i2, mIndex_of_j2;
        for (int i = 0; i < n; i++)
        {
            mIndex_of_i2 = matching.mIndex[1][i];
            for (int j = 0; j < n; j++)
            {
                mIndex_of_j2 = matching.mIndex[0][j];
                if (j != agents[1][i].getAgentAt(matching.mIndex[1][i]) && agents[0][j].getRankOf(i) < mIndex_of_j2 && agents[1][i].getRankOf(j) < mIndex_of_i2) cnt2++;
            }
        }    
        if (cnt != cnt2)
        {
        	System.err.println("Error when computing blocking pairs!");
        	System.exit(1);
        }    
        return cnt;
    }

    public int egalitarianCost()
    {
        int cost = 0;
        for (int i = 0; i < n; i++)
        {
            cost += matching.mIndex[0][i];
            cost += matching.mIndex[1][i];
        }
        return cost;
    }

    public int sexEqualityCost()
    {
        int menCost = 0;
        int womenCost = 0;
        for (int i = 0; i < n; i++)
        {
            menCost += matching.mIndex[0][i];
            womenCost += matching.mIndex[1][i];
        }
        return Math.abs(menCost - womenCost);
    }

    public int maritalEqualityCost()
    {
        int cost = 0;
        for (int i = 0; i < n; i++)
        {
            int mate = matchOf(i);
            cost += Math.abs(agents[0][i].getRankOf(mate) - agents[1][mate].getRankOf(i));
        }
        return cost;
    }

    public int totalitarianEqualityCost()
    {
        int cost = 0;
        List<Integer> ranks = new ArrayList<Integer>();
        for (int i = 0; i < n; i++)
        {
            ranks.add(matching.mIndex[0][i]);
            ranks.add(matching.mIndex[1][i]);
        }
        double mean_rank = mean(ranks);
        for (int r : ranks)
        {
            cost += Math.abs(r - mean_rank);
        }
        return cost;
    }

    public int regretCost()
    {
        int cost = matching.mIndex[0][0];
        for (int i = 0; i < n; i++)
        {
            if (matching.mIndex[0][i] > cost) cost = matching.mIndex[0][i];
            if (matching.mIndex[1][i] > cost) cost = matching.mIndex[1][i];
        }
        return cost;
    }

    public void printCosts()
    {
        System.out.print(name + ":");
        System.out.print(" BlockingPairs= " + this.blockingPairs());
        System.out.print(" EgalitarianCost= " + this.egalitarianCost());
        System.out.print(" SexEqualityCost= " + this.sexEqualityCost());
        System.out.println(" RegretCost= " + this.regretCost()); 
    }

    public void printPerformance()
    {
        System.out.print(name + ":");
        System.out.print(" Time= " + this.time + " secs");
        System.out.print(" Rounds= " + this.rounds);
        System.out.print(" EgalitarianCost= " + this.egalitarianCost());
        System.out.print(" SexEqualityCost= " + this.sexEqualityCost());
        System.out.print(" RegretCost= " + this.regretCost());  
        System.out.print(" MaritalEqualityCost= " + this.maritalEqualityCost());
        System.out.print(" TotalitarianEqualityCost= " + this.totalitarianEqualityCost()); 
        System.out.println(" Size= " + this.n);      
    }

    private static double mean(List<Integer> table)
    {
        int total = 0;
        for (int i = 0; i < table.size(); i++)
        {
            int currentNum = table.get(i);
            total += currentNum;
        }
        return (double) total / (double) table.size();
    }
}