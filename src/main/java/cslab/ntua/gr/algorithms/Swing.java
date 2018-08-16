package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;
import org.apache.commons.cli.*;

import gr.ntua.cslab.entities.Marriage;
import gr.ntua.cslab.entities.Agent;
import gr.ntua.cslab.tools.Metrics;

public class Swing extends Abstract_SM_Algorithm
{
    private int[][] kappa, married;

    public Swing(int n, String menFileName, String womenFileName)
    {
    	super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public Swing(int n, Agent[][] agents)
    {
        super(n, agents);
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
        kappa = new int[2][n];
        married = new int[2][n];  
        for (int i = 0; i < n; i++)
        {
            married[0][i] = Integer.MAX_VALUE;
            married[1][i] = Integer.MAX_VALUE;
        } 
    	int side;

        // Propose     
    	while (!terminate())
    	{
    		side = pickProposers(rounds);
    		for (int p = 0; p < n; p++) propose(p, side);    
            rounds++;
    	}

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        Marriage result = new Marriage(n, married);
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

    private int pickProposers(long r)
    {
    	if (r % 2 == 0) return 0;
        else return 1;
    }

    private void propose(int proposer, int proposerSide)
    {
        int acceptor, divorced, proposerRank;
        int acceptorSide = flip(proposerSide);
        for (int i = 0; i <= kappa[proposerSide][proposer]; i++)
        {
            acceptor = agents[proposerSide][proposer].getAgentAt(i);
            proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
            if (proposerRank <= kappa[acceptorSide][acceptor])
            {
                // accepts
                if (married[proposerSide][proposer] != Integer.MAX_VALUE)
                {
                    divorced = agents[proposerSide][proposer].getAgentAt(married[proposerSide][proposer]);
                    married[acceptorSide][divorced] = Integer.MAX_VALUE;
                    kappa[acceptorSide][divorced] = agents[acceptorSide][divorced].getRankOf(proposer) + 1;
                }
                if (married[acceptorSide][acceptor] != Integer.MAX_VALUE)
                {
                    divorced = agents[acceptorSide][acceptor].getAgentAt(married[acceptorSide][acceptor]);
                    married[proposerSide][divorced] = Integer.MAX_VALUE;
                    kappa[proposerSide][divorced] = agents[proposerSide][divorced].getRankOf(acceptor) + 1;
                }
                married[proposerSide][proposer] = i;
                kappa[proposerSide][proposer] = i - 1;
                married[acceptorSide][acceptor] = proposerRank;
                kappa[acceptorSide][acceptor] = proposerRank - 1;
            }
        }
        if (married[proposerSide][proposer] == Integer.MAX_VALUE && kappa[proposerSide][proposer] < (n - 1)) 
            kappa[proposerSide][proposer]++;
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

        Abstract_SM_Algorithm smp = new Swing(n, menFile, womenFile);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks();
        smpMetrics.printPerformance();
    }
}
