package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import gr.ntua.cslab.Agent;
import gr.ntua.cslab.Metrics;

public class CaC extends Abstract_SM_Algorithm
{
    private int[][] nIndex, mIndex;
    int initialRounds;

    public CaC(int n, String menFileName, String womenFileName, int initRounds)
    {
        super(n, menFileName, womenFileName);

        nIndex = new int[2][n];
        mIndex = new int[2][n];  
        
        for (int i = 0; i < n; i++)
        {
            mIndex[0][i] = Integer.MAX_VALUE;
            mIndex[1][i] = Integer.MAX_VALUE;
        }

        initialRounds = initRounds;
    }

    public int[] match()
    {
        long startTime = System.nanoTime();
        int side;
     
        while (!terminate())
        {
            rounds++;
            if (rounds == initialRounds)
            {
                finish_matching();
                break;
            }

            side = pickProposers(rounds);
            for (int i = 0; i < n; i++) propose_noMotivated(i, side);
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        int[] matching = new int[n];
        for (int i = 0; i < n; i++) matching[i] = agents[0][i].getAgentAt(mIndex[0][i]);

        return matching;
    }

    private void finish_matching()
    {
        int comp_side = pick_compromising_side();

        boolean stop;
        do
        {
            stop = true;
            rounds++;
            for (int i = 0; i < n; i++)
            { 
                if (propose_noMotivated(i, comp_side)) stop = false;
            }
        }
        while (!stop);

        do
        {
            stop = true;
            rounds++;
            for (int i = 0; i < n; i++)
            { 
                if (propose_noMotivated(i, flip(comp_side))) stop = false;
            }
        }
        while (!stop);    
    }

    private int pick_compromising_side()
    {
        int menCost = 0;
        int womenCost = 0;

        for (int i = 0; i < n; i++)
        {
            menCost += nIndex[0][i];
            womenCost += nIndex[1][i];
        }

        if (menCost >= womenCost) return 1;
        else return 0;
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

    private boolean propose_noMotivated(int proposer, int proposerSide)
    {   
        //System.out.println("\nTime for Agent " + a.getID() + " of Side " + side + " to propose!");
        int proposeToIndex = nIndex[proposerSide][proposer];
        int marriedToIndex = mIndex[proposerSide][proposer];

        if (proposeToIndex < marriedToIndex && proposeToIndex < n)
        {
            // Wants to propose
            int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
            //System.out.println("Agent " + a.getID() + " of Side " + side + " proposes to Agent " + b.getID());
            if (evaluate_noMotivated(acceptor, proposer, flip(proposerSide)))
            {
                //Engage with new
                mIndex[proposerSide][proposer] = proposeToIndex;
            }
            else
            {
                // b rejected a
                nIndex[proposerSide][proposer]++;
            }
            return true;
        }
        else
        {
            //System.out.println("Agent " + a.getID() + " of Side " + side + " skips turn: PIndx = " + a.getPIndx() + " , MIndx = " + a.getMIndx());
            return false;
        }
    }

    private boolean evaluate_noMotivated(int acceptor, int proposer, int acceptorSide)
    {
        int proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
        int proposeToIndex = nIndex[acceptorSide][acceptor];
        int marriedToIndex = mIndex[acceptorSide][acceptor];

        if (proposeToIndex >= proposerRank)
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
            if (proposeToIndex > proposerRank) 
                nIndex[acceptorSide][acceptor] = proposerRank;
                // !!!! NOT +1 because evaluate condition is (proposeToIndex >= proposerRank)
                // +1 means i will accept if the guy at +1 proposes me
            
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

    private static String getName()
    {
        String className = Thread.currentThread().getStackTrace()[2].getClassName(); 
        return className;
    }

    private static void usage()
    {
        System.err.println("Proper Usage: java " + getName() + " n initRoundsOfn (MenFile WomenFile)");
        System.exit(1);
    }

    public static void main(String args[]) 
    {
        int n = 0;
        int init = 0;
        String menFile = null;
        String womenFile = null;

        if (args.length != 2 && args.length != 4) usage();

        try 
        {
            n = Integer.parseInt(args[0]);
            init = Integer.parseInt(args[1]);
        } 
        catch (Exception e) 
        {
            usage();
        }

        System.out.println("Size= " + n);

        if (args.length == 4)
        {
            menFile = args[2];
            womenFile = args[3];
        } 

        Abstract_SM_Algorithm smp = new CaC(n, menFile, womenFile, init*n);
        int[] matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getName() + "_" + init);
        smpMetrics.printPerformance();

        /*
        if (!smpMetrics.checkPerfectMatching()) System.err.println("Error! Matching not perfect!");
        int bagents = smpMetrics.blockingAgents();
        if (bagents != 0) System.err.println("Error! Terminated with " + bagents + " blocking agents!");
        */
    }
}

/*
    private boolean propose_noMotivated(int proposer, int proposerSide)
    {   
        int proposeToIndex = kappa[proposerSide][proposer];

        int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
        if (evaluate_noMotivated(acceptor, proposer, flip(proposerSide)))
        {
            //Engage with new
            married[proposerSide][proposer] = true;
            candidate_proposers.remove(Integer.valueOf(proposer + proposerSide*n));
            return true;
        }
        else
        {
            // Rejected
            kappa[proposerSide][proposer]++;
            // Corner case: end of list
            if (kappa[proposerSide][proposer] == n) 
            {
                candidate_proposers.remove(Integer.valueOf(proposer + proposerSide*n));
                return true;
            }
            return false;
        }
    }

    private boolean evaluate_noMotivated(int acceptor, int proposer, int acceptorSide)
    {
        int proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
        int proposeToIndex = kappa[acceptorSide][acceptor];

        if (proposeToIndex >= proposerRank)
        {
            // Break up with old
            if (married[acceptorSide][acceptor])
            {
                int old = agents[acceptorSide][acceptor].getAgentAt(proposeToIndex);
                married[flip(acceptorSide)][old] = false;    
                candidate_proposers.add(old + flip(acceptorSide)*n);
            }
            else
            {
                candidate_proposers.remove(Integer.valueOf(acceptor + acceptorSide*n));
            }
            
            //Engage with new
            married[acceptorSide][acceptor] = true;

            // Boost confidence if needed
            if (proposeToIndex > proposerRank) 
                kappa[acceptorSide][acceptor] = proposerRank;
                // !!!! NOT +1 because evaluate condition is (proposeToIndex >= proposerRank)
                // +1 means i will accept if the guy at +1 proposes me
            
            return true;
        }
        else
        {
            return false;
        }
    }
*/