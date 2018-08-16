package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.cli.*;

import gr.ntua.cslab.entities.Agent;
import gr.ntua.cslab.entities.Marriage;
import gr.ntua.cslab.tools.Metrics;

public class DA_Random extends Abstract_SM_Algorithm
{
    private int[][] kappa, married;

    public DA_Random(int n, String menFileName, String womenFileName)
    {
        super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public DA_Random(int n, Agent[][] agents)
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
        int side, proposer;
        int proposals = 0;
     
        while (true)
        {
            proposer = pickProposer();
            if (proposer >= n)
            {
                side = 1;
                proposer = proposer - n;
            }
            else
            {
                side = 0;
            }
            propose(proposer, side);

            proposals++;
            if (proposals == n)
            {
                proposals = 0;
                rounds++;
                if (terminate()) break;
            }
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
            if (kappa[0][i] < married[0][i]) return false;
            if (kappa[1][i] < married[1][i]) return false;
        }
        return true;
    }

    private int pickProposer()
    {
        return ThreadLocalRandom.current().nextInt(0, 2*n);
    }

    private void propose(int proposer, int proposerSide)
    {
        int proposeToIndex = kappa[proposerSide][proposer];
        int marriedToIndex = married[proposerSide][proposer];
        if (proposeToIndex < marriedToIndex && proposeToIndex < n)
        {
            // Wants to propose
            int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
            if (evaluate(acceptor, proposer, flip(proposerSide)))
            {
                // Break up with old
                if (marriedToIndex != Integer.MAX_VALUE)
                {
                    int old = agents[proposerSide][proposer].getAgentAt(marriedToIndex);
                    married[flip(proposerSide)][old] = Integer.MAX_VALUE;       
                }
                //Engage with new
                married[proposerSide][proposer] = proposeToIndex;
            }
            else kappa[proposerSide][proposer]++;
        }
    }

    private boolean evaluate(int acceptor, int proposer, int acceptorSide)
    {
        int proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
        int marriedToIndex = married[acceptorSide][acceptor];
        if (marriedToIndex > proposerRank)
        {
            // Break up with old
            if (marriedToIndex != Integer.MAX_VALUE)
            {
                int old = agents[acceptorSide][acceptor].getAgentAt(marriedToIndex);
                married[flip(acceptorSide)][old] = Integer.MAX_VALUE;                   
            }
            //Engage with new
            married[acceptorSide][acceptor] = proposerRank;
            // Boost confidence if needed
            if (kappa[acceptorSide][acceptor] > proposerRank) kappa[acceptorSide][acceptor] = proposerRank + 1;            
            return true;
        }
        else return false;
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

        Abstract_SM_Algorithm smp = new DA_Random(n, menFile, womenFile);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks();   
        smpMetrics.printPerformance();
    }
}