package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.cli.*;

import gr.ntua.cslab.entities.Agent;
import gr.ntua.cslab.entities.Marriage;
import gr.ntua.cslab.tools.Metrics;

public class EROM extends Abstract_SM_Algorithm
{
    private int[][] married;
    private boolean[][][] valid;
    private ArrayList<Integer> proposers;

    public EROM(int n, String menFileName, String womenFileName)
    {
    	super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public EROM(int n, Agent[][] agents)
    {
        super(n, agents);
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
    	int side, p, proposer, k, p_res;
        married = new int[2][n];     
        for (int i = 0; i < n; i++)
        {
            married[0][i] = Integer.MAX_VALUE;
            married[1][i] = Integer.MAX_VALUE;
        } 
        proposers = new ArrayList<Integer>();
        for (int i = 0; i < 2*n; i++) proposers.add(i);
        java.util.Collections.shuffle(proposers);

        // Start proposing
        k = 1;
        while (!terminate())
        {
            for (int i = 0; i < 2*n; i++)
            {
                // Select proposer according to the random order that is now fixed
                p = proposers.get(i);
                if (p >= n)
                {
                    side = 1;
                    proposer = p - n;
                }
                else
                {
                    side = 0;
                    proposer = p;
                }
                if (married[side][proposer] == Integer.MAX_VALUE)
                {
                    // Propose until the proposer marries a single (-1) or depletes his list (-2)
                    p_res = 0;
                    while (p_res >= 0)
                    {
                        // Propose to top k preferences
                        for (int j = 0; j < k; j++)
                        {
                            p_res = propose(proposer, side, j, k);
                            // Continue only if the proposal was rejected
                            if (p_res >= 0)
                            {
                                // The abandoned one now proposes
                                proposer = p_res;
                                break;
                            }
                            if (p_res == -1) break;
                        }                        
                    }
                }
            }   
            // Increase round
            k++;    
        }
  
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        int[][] final_indices = new int[2][n];
        for (int i = 0; i < n; i++)
        {
            final_indices[0][i] = agents[0][i].getRankOf(married[0][i]);
            final_indices[1][i] = agents[1][i].getRankOf(married[1][i]);
        }      
        Marriage result = new Marriage(n, final_indices);
        return result;
    }

    private boolean terminate()
    {
        for (int i = 0; i < n; i++)
        {
            if (married[0][i] == Integer.MAX_VALUE) return false;
        }
        return true;
    }

    // Return values
    // -1 -> success
    // -2 -> failure
    // >=0 -> married someone previously engaged to (retval)
    private int propose(int proposer, int proposerSide, int idx, int k)
    {
        int acceptor, old;
        int acceptorSide = flip(proposerSide);

        if (idx == n) return -1;
        acceptor = agents[proposerSide][proposer].getAgentAt(idx);
        // If acceptor is single, he accepts if proposer is among top k preferences
        if (married[acceptorSide][acceptor] == Integer.MAX_VALUE)
        {
            if (agents[acceptorSide][acceptor].getRankOf(proposer) < k)
            {
                married[proposerSide][proposer] = acceptor;
                married[acceptorSide][acceptor] = proposer;
                return -1;                
            }
        }
        // If acceptor is married but prefers proposer, he accepts yet again
        else if (agents[acceptorSide][acceptor].getRankOf(proposer) < agents[acceptorSide][acceptor].getRankOf(married[acceptorSide][acceptor]))
        {
            old = married[acceptorSide][acceptor];
            married[proposerSide][old] = Integer.MAX_VALUE;
            married[proposerSide][proposer] = acceptor;
            married[acceptorSide][acceptor] = proposer;
            return old;
        }
        // Rejected
        return -2;
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

    private static String getFinalName()
    {
        String className = Thread.currentThread().getStackTrace()[2].getClassName(); 
        return className.substring(className.lastIndexOf('.') + 1);
    }

    public static void main(String args[]) 
    {
        // Parse the command line
        Options options = new Options();

        Option size = new Option("n", "size", true, "size of instance");
        size.setRequired(true);
        options.addOption(size);

        Option men = new Option("m", "men", true, "men preferences input file");
        men.setRequired(false);
        options.addOption(men);

        Option women = new Option("w", "women", true, "women preferences input file");
        women.setRequired(false);
        options.addOption(women);

        Option verify = new Option("v", "verify", false, "verify result");
        verify.setRequired(false);
        options.addOption(verify);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try
        {
            cmd = parser.parse(options, args);
        } 
        catch (ParseException e) 
        {
            System.err.println(e.getMessage());
            formatter.printHelp(getName(), options);
            System.exit(1);
        }

        int n = Integer.parseInt(cmd.getOptionValue("size"));
        String menFile = cmd.getOptionValue("men");
        String womenFile = cmd.getOptionValue("women");
        boolean v;
        if (cmd.hasOption("verify")) v = true;
        else v = false;

        Abstract_SM_Algorithm smp = new EROM(n, menFile, womenFile);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks(); 
        smpMetrics.printPerformance();     
    }
}
