package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.cli.*;

import gr.ntua.cslab.entities.Marriage;
import gr.ntua.cslab.entities.Agent;
import gr.ntua.cslab.tools.Metrics;

public class BestResponseDynamics extends Abstract_SM_Algorithm
{
    private int[][] married;

    public BestResponseDynamics(int n, String menFileName, String womenFileName)
    {
    	super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public BestResponseDynamics(int n, Agent[][] agents)
    {
        super(n, agents);
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
        boolean found_bp;
        married = new int[2][n];  
    	int side, other_side, r, divorced, proposerRank;
        List<Integer> proposers = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) proposers.add(i);
        List<Integer> indices = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) indices.add(i);

        // Generate a random matching
        java.util.Collections.shuffle(proposers);
        for (int i = 0; i < n; i++)
        {
            // proposers(i) marries i
            married[0][proposers.get(i)] = agents[0][proposers.get(i)].getRankOf(i);
            married[1][i] = agents[1][i].getRankOf(proposers.get(i));
        }

        // Active side = females
        side = 1;
        other_side = 0;

        // Start
        while (true)
        {
            found_bp = false;
            // Randomize order of agents to check for blocking pairs
            java.util.Collections.shuffle(proposers);
            for (int p : proposers)
            {
                // Check indices from first to last (best first)
                for (int index = 0; index < n; index++)
                {
                    // Check for blocking pair here
                    r = agents[side][p].getAgentAt(index);
                    proposerRank = agents[other_side][r].getRankOf(p);
                    if (married[side][p] > index && married[other_side][r] > proposerRank)
                    {
                        // p and r form a blocking pair
                        if (married[side][p] != Integer.MAX_VALUE)
                        {
                            divorced = agents[side][p].getAgentAt(married[side][p]);
                            married[other_side][divorced] = Integer.MAX_VALUE;
                        }
                        if (married[other_side][r] != Integer.MAX_VALUE)
                        {
                            divorced = agents[other_side][r].getAgentAt(married[other_side][r]);
                            married[side][divorced] = Integer.MAX_VALUE;
                        }
                        married[side][p] = index;
                        married[other_side][r] = proposerRank;
                        found_bp = true;
                        break;
                    }
                }
                if (found_bp) break;
            }
            // If no blocking pairs found, the solution is stable
            if (!found_bp) break;
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        Marriage result = new Marriage(n, married);
        return result;
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

        Abstract_SM_Algorithm smp = new BestResponseDynamics(n, menFile, womenFile);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks();
        smpMetrics.printPerformance();
    }
}
