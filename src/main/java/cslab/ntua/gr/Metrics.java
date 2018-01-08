package gr.ntua.cslab;

import java.io.*;
import java.util.*;

import gr.ntua.cslab.algorithms.Abstract_SM_Algorithm;

public class Metrics
{
	private int n;
	private Agent[][] agents;
	private int[] matching, inverseMatching;
    private String name;
    private int rounds;
    private double time;

    public Metrics(Abstract_SM_Algorithm instance, int[] matching, String name)
    {
    	n = instance.getSize();
    	agents = instance.getAgents();
        rounds = instance.getRounds();
        time = instance.getTime();
        this.matching = matching;
        this.name = name;

        inverseMatching = new int[n];
        for (int i = 0; i < n; i++) inverseMatching[matching[i]] = i;
    }

    public void printMatching()
    {
    	for (int i = 0; i < n; i++) 
    		System.out.println("Man " + i + " married to Woman " + matching[i]);
    }

    public boolean checkPerfectMatching()
    {
        int[] tick = new int[n];
        for (int i = 0; i < n; i++)
            tick[matching[i]]++;
        for (int i = 0; i < n; i++)
        {
            if (tick[i] != 1) return false;
        }      
        return true;
    }

    public int blockingPairs()
    {
    	int cnt = 0;

    	for (int i = 0; i < n; i++)
    	{
    		int mateI = matching[i];
    		for (int j = 0; j < n; j++)
    		{
    			int mateJ = inverseMatching[j];
    			if (j != mateI && agents[1][j].cmp(i, mateJ) && agents[0][i].cmp(j, mateI)) cnt++;
    		}
    	}
    	return cnt;
    }

    public int blockingAgents()
    {
    	int cnt = 0;

    	// count men
    	for (int i = 0; i < n; i++)
    	{
    		int mateI = matching[i];
    		for (int j = 0; j < n; j++)
    		{
                int mateJ = inverseMatching[j];
    			if (j != mateI && agents[1][j].cmp(i, mateJ) && agents[0][i].cmp(j, mateI)) 
    			{
    				cnt++;
    				break;
    			}
    		}
    	}

    	// count women
    	for (int j = 0; j < n; j++)
    	{
            int mateJ = inverseMatching[j];
    	
    		for (int i = 0; i < n; i++)
    		{
    			int mateI = matching[i];
    			if (i != mateJ && agents[1][j].cmp(i, mateJ) && agents[0][i].cmp(j, mateI)) 
    			{
    				cnt++;
    				break;
    			}
    		}
    	}
    	return cnt;
    }

    public int egalitarianCost()
    {
    	int cost = 0;
    	for (int i = 0; i < n; i++)
    	{
    		int mate = matching[i];
    		cost += agents[0][i].getRankOf(mate);
    		cost += agents[1][mate].getRankOf(i);
    	}
    	return cost;
    }

    public int sexEqualityCost()
    {
    	int menCost = 0;
    	int womenCost = 0;

    	for (int i = 0; i < n; i++)
    	{
    		int mate = matching[i];
    		menCost += agents[0][i].getRankOf(mate);
    		womenCost += agents[1][mate].getRankOf(i);
    	}
    	return Math.abs(menCost - womenCost);
    }

    public int maritalEqualityCost()
    {
        int cost = 0;
        for (int i = 0; i < n; i++)
        {
            int mate = matching[i];
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
            int mate = matching[i];
            ranks.add(agents[0][i].getRankOf(mate));
            ranks.add(agents[1][mate].getRankOf(i));
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
    	int cost = agents[0][0].getRankOf(matching[0]);
    	for (int i = 0; i < n; i++)
    	{
    		int mate = matching[i];
    		if (agents[0][i].getRankOf(mate) > cost) cost = agents[0][i].getRankOf(mate);
    		if (agents[1][mate].getRankOf(i) > cost) cost = agents[1][mate].getRankOf(i);
    	}
    	return cost;
    }

    public void printCosts()
    {
        System.out.print(name + ":");
        System.out.print(" BlockingAgents= " + this.blockingAgents());
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
        System.out.println(" TotalitarianEqualityCost= " + this.totalitarianEqualityCost());       
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