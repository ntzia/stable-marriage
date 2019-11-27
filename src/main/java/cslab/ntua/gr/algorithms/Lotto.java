package cslab.ntua.gr.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Marriage;
import cslab.ntua.gr.tools.Metrics;

public class Lotto extends Abstract_SM_Algorithm
{
    private int[][] married;
    private boolean[][][] deleted;
    private int[][] startFrom;
    private int[][] endAt;

    public Lotto(int n, String menFileName, String womenFileName)
    {
    	super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public Lotto(int n, Agent[][] agents)
    {
        super(n, agents);
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
        int rand, p, couples_to_still_match, spouse;
        married = new int[2][n]; 
        startFrom = new int[2][n]; 
        endAt = new int[2][n];
        deleted = new boolean[2][n][n]; 
        for (int i = 0; i < n; i++)
        {
            endAt[0][i] = n-1;
            endAt[1][i] = n-1;
        }
        int[] matching = new int[n];

        // Initial refining (achieved with Extended GS algorithm)
        extended_gs(0);
        extended_gs(1);

        // Create pool of unmatched agents
        List<Integer> unmatched_men = new ArrayList<Integer>();
        for (int i = 0; i < n; i++)
        {
            unmatched_men.add(i);
        }
        List<Integer> unmatched_women = new ArrayList<Integer>();
        for (int i = 0; i < n; i++)
        {
            unmatched_women.add(i);
        }

        // Begin proposals
        for (int i = 0; i < n; i++)
        {
            couples_to_still_match = n - i;

            // Pick random proposer
            rand = ThreadLocalRandom.current().nextInt(0, 2 * couples_to_still_match);
            if (rand < couples_to_still_match)
            {
                p = unmatched_men.get(rand);
                // Match and further reduce lists
                spouse = agents[0][p].getAgentAt(first_valid_index(p, 0));
                matching[p] = spouse;
                shorten_lists(p, 0, spouse);
                // Update pool of proposers
                unmatched_men.remove(rand);
                unmatched_women.remove(unmatched_women.indexOf(spouse));
            }
            else
            {
                p = unmatched_women.get(rand - couples_to_still_match);
                // Match and further reduce lists
                spouse = agents[1][p].getAgentAt(first_valid_index(p, 1));
                matching[spouse] = p;
                shorten_lists(p, 1, spouse);
                // Update pool of proposers
                unmatched_women.remove(rand - couples_to_still_match);
                unmatched_men.remove(unmatched_men.indexOf(spouse));  
            }

            // Refine and continue
            extended_gs(0);
            extended_gs(1);
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        int[][] final_indices = new int[2][n];
        for (int i = 0; i < n; i++)
        {
            final_indices[0][i] = first_valid_index(i, 0);
            final_indices[1][i] = first_valid_index(i, 1);
        }      
        Marriage result = new Marriage(n, final_indices);
        return result;
    }

    // Reduces preference lists according to the GS algorithm in which side(param) proposes
    private void extended_gs(int side)
    {
        int p, r, previous_spouse;
        boolean done;
        for (int i = 0; i < n; i++)
        {
            married[0][i] = Integer.MAX_VALUE;
            married[1][i] = Integer.MAX_VALUE;
        } 

        done = false;
        while (!done)
        {
            done = true;
            for (p = 0; p < n; p++)
            {
                if (married[side][p] == Integer.MAX_VALUE)
                {
                    // Find receiver
                    r = agents[side][p].getAgentAt(first_valid_index(p, side));
                    // If receiver is married, he abandons former spouse
                    previous_spouse = married[flip(side)][r];
                    if (previous_spouse != Integer.MAX_VALUE) married[side][previous_spouse] = Integer.MAX_VALUE;
                    // Marry them
                    married[side][p] = r;
                    married[flip(side)][r] = p;
                    // Reduce lists
                    shorten_lists(r, flip(side), p);

                    done = false;
                }
            }
        }
    }

    // a removes from his list everyone after b
    private void shorten_lists(int a, int sideA, int b)
    {
        int i, other, index_of_a, index_of_b;

        index_of_b = agents[sideA][a].getRankOf(b);
        for (i = index_of_b + 1; i <= last_valid_index(a, sideA); i++)
        {
            deleted[sideA][a][i] = true;
            other = agents[sideA][a].getAgentAt(i);
            index_of_a = agents[flip(sideA)][other].getRankOf(a);
            deleted[flip(sideA)][other][index_of_a] = true;
        }
    }

    private int first_valid_index(int a, int side)
    {
        int start_searching_from = startFrom[side][a];
        for (int i = start_searching_from; i < n; i++)
        {
            if (!deleted[side][a][i])
            {
                startFrom[side][a] = i;
                return i;
            }
        }
        return -1;
    }

    private int last_valid_index(int a, int side)
    {
        int start_searching_from = endAt[side][a];
        for (int i = start_searching_from; i >= 0; i--)
        {
            if (!deleted[side][a][i])
            {
                endAt[side][a] = i;
                return i;
            }
        }
        return -1;
    }

    private static String getFinalName()
    {
        String className = getName();
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

        Abstract_SM_Algorithm smp = new Lotto(n, menFile, womenFile);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks();    
        smpMetrics.printPerformance();
    }
}
