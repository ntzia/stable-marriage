package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.cli.*;

import gr.ntua.cslab.entities.Agent;
import gr.ntua.cslab.entities.Marriage;
import gr.ntua.cslab.tools.Metrics;

public class SDA_Random extends Abstract_SM_Algorithm
{
    private int[][] kappa;
    private boolean[][] married;

    public SDA_Random(int n, String menFileName, String womenFileName)
    {
        super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public SDA_Random(int n, Agent[][] agents)
    {
        super(n, agents);
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
        kappa = new int[2][n];
        married = new boolean[2][n];
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
            propose_SDA(proposer, side);

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

        Marriage result = new Marriage(n, kappa);
        return result;
    }

    private boolean terminate()
    {
        for (int i = 0; i < n; i++)
        {
            if (!married[0][i]) return false;
        }
        return true;
    }

    private int pickProposer()
    {
        return ThreadLocalRandom.current().nextInt(0, 2*n);
    }

    // Returns true if a proposal was issued, false otherwise
    private void propose_SDA(int proposer, int proposerSide)
    {
        int proposeToIndex = kappa[proposerSide][proposer];
        if (!married[proposerSide][proposer] && proposeToIndex < n)
        {
            // Wants to propose
            int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
            if (evaluate_SDA(acceptor, proposer, flip(proposerSide))) married[proposerSide][proposer] = true;
            else kappa[proposerSide][proposer]++;
        }
    }

    // Returns true if acceptor agrees to marry proposer
    private boolean evaluate_SDA(int acceptor, int proposer, int acceptorSide)
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
            }            
            //Engage with new
            married[acceptorSide][acceptor] = true;
            // Boost confidence if needed
            if (proposeToIndex > proposerRank) kappa[acceptorSide][acceptor] = proposerRank;            
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

        Abstract_SM_Algorithm smp = new SDA_Random(n, menFile, womenFile);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks();
        smpMetrics.printPerformance();
    }
}