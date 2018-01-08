package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;

import gr.ntua.cslab.Agent;
import gr.ntua.cslab.Metrics;

public class ESMA extends Abstract_SM_Algorithm
{
    private int[][] nIndex, mIndex;

    public ESMA(int n, String menFileName, String womenFileName)
    {
    	super(n, menFileName, womenFileName);

        nIndex = new int[2][n];
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
    	int side;
     
    	while (!terminate())
    	{
    		rounds++;
    		side = pickProposers(rounds);
    		for (int i = 0; i < n; i++) propose(i, side);
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
    		if (nIndex[0][i] < mIndex[0][i]) return false;
    		if (nIndex[1][i] < mIndex[1][i]) return false;
    	}
		return true;
    }

    private int pickProposers(int r)
    {
    	if ((Math.sin(r*r)>0)) return 0;
        else return 1;
    }

    private void propose(int proposer, int proposerSide)
    {	
    	//System.out.println("\nTime for Agent " + a.getID() + " of Side " + side + " to propose!");
        int proposeToIndex = nIndex[proposerSide][proposer];
        int marriedToIndex = mIndex[proposerSide][proposer];

    	if (proposeToIndex < marriedToIndex && proposeToIndex < n)
    	{
            // Wants to propose
    		int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
    		//System.out.println("Agent " + a.getID() + " of Side " + side + " proposes to Agent " + b.getID());
    		if (evaluate(acceptor, proposer, flip(proposerSide)))
    		{
    			// Break up with old
    			if (marriedToIndex != Integer.MAX_VALUE)
    			{
    				int old = agents[proposerSide][proposer].getAgentAt(marriedToIndex);
    				mIndex[flip(proposerSide)][old] = Integer.MAX_VALUE;		
    			}
    			//Engage with new
    			mIndex[proposerSide][proposer] = proposeToIndex;
    		}
    		else
    		{
    			// b rejected a
    			nIndex[proposerSide][proposer]++;
    		}
    	}
    	else
    	{
    		//System.out.println("Agent " + a.getID() + " of Side " + side + " skips turn: PIndx = " + a.getPIndx() + " , MIndx = " + a.getMIndx());
    	}
    }

    private boolean evaluate(int acceptor, int proposer, int acceptorSide)
    {
        int proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
        int marriedToIndex = mIndex[acceptorSide][acceptor];
    	if (marriedToIndex > proposerRank)
    	{
    		//System.out.println("Agent " + a.getID() + " of Side " + flip(sideB) + " accepts the proposal.");
    		// Break up with old
    		if (marriedToIndex != Integer.MAX_VALUE)
    		{
                int old = agents[acceptorSide][acceptor].getAgentAt(marriedToIndex);
                mIndex[flip(acceptorSide)][old] = Integer.MAX_VALUE;   				
    		}
    		
    		//Engage with new
    		mIndex[acceptorSide][acceptor] = proposerRank;

            // Boost confidence if needed
    		if (nIndex[acceptorSide][acceptor] > proposerRank) 
                nIndex[acceptorSide][acceptor] = proposerRank + 1;
            
    		return true;
    	}
    	else
    	{
    		//System.out.println("Agent " + a.getID() + " of Side " + flip(sideB) + " rejects the proposal.");
    		return false;
    	}

    }

    private int flip(int side)
    {
    	return side^1;
    }

/*
    public ESMA_State next_state(ESMA_State previous, int side)
    {
        // Get previous state and fix agents to it
        int[][] propose_arr, married_arr; 
        propose_arr = previous.getProposeArray();
        married_arr = previous.getMarriedArray();
        this.nIndex = new int[2][n]; 
        this.mIndex = new int[2][n]; 
        for (int i = 0; i < n; i++)
        {
            this.nIndex[0][i] = propose_arr[0][i]; 
            this.nIndex[1][i] = propose_arr[1][i]; 
            this.mIndex[0][i] = married_arr[0][i]; 
            this.mIndex[1][i] = married_arr[1][i]; 
        }       
        String prev_sequence = previous.getSequence();

        // Run the step
        for (int i = 0; i < n; i++) propose(i, side);

        // Return the new state holding the results
        ESMA_State next = new ESMA_State(n, this.nIndex, this.mIndex, prev_sequence + String.valueOf(side));
        return next;
    }
*/

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

        Abstract_SM_Algorithm smp = new ESMA(n, menFile, womenFile);
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
