package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;

import gr.ntua.cslab.Agent;
import gr.ntua.cslab.Metrics;

public class GS_FemaleOpt extends Abstract_SM_Algorithm
{
    private int[] nIndex;
    private int[][] mIndex;

    public GS_FemaleOpt(int n, String menFileName, String womenFileName)
    {
        super(n, menFileName, womenFileName);

        nIndex = new int[n];
        mIndex = new int[2][n];  
        
        for (int i = 0; i < n; i++)
        {
            mIndex[0][i] = Integer.MAX_VALUE;
            mIndex[1][i] = Integer.MAX_VALUE;
        } 
    }

    public GS_FemaleOpt(int n, Agent[][] agents)
    {
        super(n, agents);
        this.n = n;
        rounds = 0;
        time = 0;

        this.agents = agents;


        nIndex = new int[n];
        mIndex = new int[2][n];  
        
        for (int i = 0; i < n; i++)
        {
            mIndex[0][i] = Integer.MAX_VALUE;
            mIndex[1][i] = Integer.MAX_VALUE;
        } 
    }

    public int[] match()
    {
        long startTime = System.nanoTime();

    	while (!terminate())
    	{
    		rounds++;
    		for (int i = 0; i < n; i++) propose(i);
    	}

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        int[] matching = new int[n];
        for (int i = 0; i < n; i++) matching[i] = agents[0][i].getAgentAt(mIndex[0][i]);

        return matching;
    }

    private boolean terminate()
    {
    	for (int i = 0; i < n; i++)
    	{
    		if (mIndex[1][i] == Integer.MAX_VALUE) return false;
    	}
		return true;
    }

    private void propose(int proposer)
    {   
        //System.out.println("\nTime for Agent " + a.getID() + " of Side " + side + " to propose!");
        int proposeToIndex = nIndex[proposer];

        if (mIndex[1][proposer] == Integer.MAX_VALUE)
        {
            // Wants to propose
            int acceptor = agents[1][proposer].getAgentAt(proposeToIndex);
            //System.out.println("Agent " + a.getID() + " of Side " + side + " proposes to Agent " + b.getID());
            if (evaluate(acceptor, proposer))
            {
                //Engage with new
                mIndex[1][proposer] = proposeToIndex;
            }
            else
            {
                // rejected
                nIndex[proposer]++;
            }
        }
        else
        {
            //System.out.println("Agent " + a.getID() + " of Side " + side + " skips turn: PIndx = " + a.getPIndx() + " , MIndx = " + a.getMIndx());
        }
    }

    private boolean evaluate(int acceptor, int proposer)
    {
        int proposerRank = agents[0][acceptor].getRankOf(proposer);
        int marriedToIndex = mIndex[0][acceptor];
        if (marriedToIndex > proposerRank)
        {
            //System.out.println("Agent " + a.getID() + " of Side " + flip(sideB) + " accepts the proposal.");
            // Break up with old
            if (marriedToIndex != Integer.MAX_VALUE)
            {
                int old = agents[0][acceptor].getAgentAt(marriedToIndex);
                mIndex[1][old] = Integer.MAX_VALUE;                
            }
            
            //Engage with new
            mIndex[0][acceptor] = proposerRank;
            
            return true;
        }
        else
        {
            //System.out.println("Agent " + a.getID() + " of Side " + flip(sideB) + " rejects the proposal.");
            return false;
        }
    }

    private static String getName()
    {
        String className = Thread.currentThread().getStackTrace()[2].getClassName(); 
        return className;
    }
    
    private static void usage()
    {
        System.err.println("Proper Usage: java " + getName() + " n (MenFile WomenFile)");
        System.exit(1);
    }

    public static void main(String args[]) 
    {
        int n = 0;
        String menFile = null;
        String womenFile = null;

        if (args.length != 1 && args.length != 3) usage();

        try 
        {
            n = Integer.parseInt(args[0]);
        } 
        catch (Exception e) 
        {
            usage();
        }

        System.out.println("Size= " + n);

        if (args.length == 3)
        {
            menFile = args[1];
            womenFile = args[2];
        } 

        Abstract_SM_Algorithm smp = new GS_FemaleOpt(n, menFile, womenFile);
        int[] matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getName());
        smpMetrics.printPerformance();

/*
        if (!smpMetrics.checkPerfectMatching()) System.err.println("Error! Matching not perfect!");
        int bagents = smpMetrics.blockingAgents();
        if (bagents != 0) System.err.println("Error! Terminated with " + bagents + " blocking agents!");
*/
    }
}