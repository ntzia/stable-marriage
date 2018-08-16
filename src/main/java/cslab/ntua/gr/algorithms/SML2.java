package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.cli.*;

import gr.ntua.cslab.entities.Agent;
import gr.ntua.cslab.entities.Marriage;
import gr.ntua.cslab.tools.Metrics;

public class SML2 extends Abstract_SM_Algorithm
{
    private double probability;
    private int[][] married;

    public SML2(int n, String menFileName, String womenFileName, Double prob)
    {
    	super(n, menFileName, womenFileName);
        this.probability = prob;
    }

    // Constructor for when agents are available
    public SML2(int n, Agent[][] agents, Double prob)
    {
        super(n, agents);
        this.probability = prob;
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
        int a1, a2, neighbour_cnt, chosen, counter, min_bp;
        int side = 0;
        int[][] neighbour_m, best_m;
        married = new int[2][n];
        List<Integer> undominated_pairs, bp_list;
        // Generate a random matching
        List<Integer> proposers = new ArrayList<Integer>();
        for (int i = 0; i < n; i++) proposers.add(i);
        java.util.Collections.shuffle(proposers);
        for (int i = 0; i < n; i++)
        {
            // proposers(i) marries i
            married[0][proposers.get(i)] = agents[0][proposers.get(i)].getRankOf(i);
            married[1][i] = agents[1][i].getRankOf(proposers.get(i));
        }

        // Start local search
        while (true)
        {
            // undominated_pairs = (m1, w1, m2, w2, ...)
            undominated_pairs = ub2(married, side);
            if (undominated_pairs.size() == 0) break;        

            // with some small probability, move to a random neighbour
            if (ThreadLocalRandom.current().nextInt(0, 100) < 100 * probability)
            {
                chosen = ThreadLocalRandom.current().nextInt(0, undominated_pairs.size() / 2);
                // (chosen,chosen+1) is the randomly chosen blocking pair
                married = eliminate(married, undominated_pairs.get(2 * chosen), undominated_pairs.get(2 * chosen + 1));
                continue;
            }

            min_bp = n * (n - 1);
            best_m = null;
            for (int i = 0; i < undominated_pairs.size(); i = i + 2)
            {
                a1 = undominated_pairs.get(i);
                a2 = undominated_pairs.get(i+1);

                neighbour_m = new int[2][n];
                neighbour_cnt = evaluate(a1, a2, neighbour_m);
                if (neighbour_cnt < min_bp)
                {
                    // Best = Neighbour
                    min_bp = neighbour_cnt;
                    best_m = neighbour_m;
                }
            }
            married = best_m;
            side = flip(side);
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        Marriage res = new Marriage(n, married);
        return res;
    }

    // Evaluate the result of eliminating the blocking pair (man, woman) from the current marriage (married array)
    // Returns the new amount of blocking pairs and the new marriage
    private int evaluate(int man, int woman, int[][] resulting_m)
    {
        for (int i = 0; i < n; i++)
        {
            resulting_m[0][i] = married[0][i];
            resulting_m[1][i] = married[1][i];
        }
        // man marries woman
        resulting_m = eliminate(resulting_m, man, woman);
        int res = count_blocking_pairs(resulting_m);
        return res; 
    }

    // Given a matching (m array), returns the number of its blocking pairs
    private int count_blocking_pairs(int[][] m)
    {
        int r, proposerRank;
        int cnt = 0;
        for (int p = 0; p < n; p++)
        {
            // Check indices from first to last
            for (int index = 0; index < m[0][p]; index++)
            {
                // Check for blocking pair here
                r = agents[0][p].getAgentAt(index);
                proposerRank = agents[1][r].getRankOf(p);
                if (m[1][r] > proposerRank)
                {
                    // p and r form a blocking pair
                    cnt++;
                }
            }
        }
        return cnt;
    }

    // Given a marriage m, returns the undominated blocking pairs
    // which are first checked from the pov of side
    // Also returns the blocking pairs dictated by side
    private List<Integer> ub2(int[][] m, int side)
    {
        int[] pos = new int[n];
        boolean[] fnd = new boolean[n];
        int[] ubp = new int[n];
        for (int i = 0; i < n; i++)
        {
            pos[i] = 0;
            fnd[i] = false;
            ubp[i] = -1;
        }
        boolean finished = false;
        List<Integer> res = new ArrayList<Integer>();

        int i, j, proposerRank, index;
        while (!finished)
        {
            for (j = 0; j < n; j++)
            {
                for (index = pos[j]; index < m[side][j]; index++)
                {
                    i = agents[side][j].getAgentAt(index);
                    proposerRank = agents[flip(side)][i].getRankOf(j);                    
                    if (m[flip(side)][i] > proposerRank)
                    {
                        // j (side) and i (other side) form a blocking pair
                        if (ubp[i] == -1)
                        {
                            ubp[i] = j;
                        }
                        else if (agents[flip(side)][i].getRankOf(j) < agents[flip(side)][i].getRankOf(ubp[i]))
                        {
                            fnd[ubp[i]] = false;
                            ubp[i] = j;
                        }
                        fnd[j] = true;
                        break;
                    }
                }
                pos[j] = index + 1;
            }
            finished = true;
            for (i = 0; i < n; i++)
            {
                if (!fnd[i] && pos[i] < m[side][i])
                {
                    finished = false;
                    break;
                }
            }
        }

        for (i = 0; i < n; i++)
        {
            if (ubp[i] != -1)
            {
                if (side == 0)
                {
                    res.add(ubp[i]);
                    res.add(i);
                }
                else
                {
                    res.add(i);
                    res.add(ubp[i]);                    
                }
            }
        }
        return res;
    }

    // Given a marriage m, eliminates the blocking pair (man, woman)
    private int[][] eliminate(int[][] m, int man, int woman)
    {
        int former_spouse = agents[0][man].getAgentAt(m[0][man]);
        int former_fiance = agents[1][woman].getAgentAt(m[1][woman]);
        // Marry man and woman
        m[0][man] = agents[0][man].getRankOf(woman);
        m[1][woman] = agents[1][woman].getRankOf(man);
        // Marry the divorced ones
        m[0][former_fiance] = agents[0][former_fiance].getRankOf(former_spouse);
        m[1][former_spouse] = agents[1][former_spouse].getRankOf(former_fiance);   
        return m;     
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

    private static String getFinalName(double prob)
    {
        String className = Thread.currentThread().getStackTrace()[2].getClassName(); 
        return className.substring(className.lastIndexOf('.') + 1) + "_" + prob;
    }

    public static void main(String args[]) 
    {
        // Parse the command line
        Options options = new Options();

        Option size = new Option("n", "size", true, "size of instance");
        size.setRequired(true);
        options.addOption(size);

        Option prob = new Option("p", "probability", true, "probability of random walk");
        prob.setRequired(true);
        options.addOption(prob);

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
        double probability = Double.parseDouble(cmd.getOptionValue("probability"));
        String menFile = cmd.getOptionValue("men");
        String womenFile = cmd.getOptionValue("women");
        boolean v;
        if (cmd.hasOption("verify")) v = true;
        else v = false;

        Abstract_SM_Algorithm smp = new SML2(n, menFile, womenFile, probability);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName(probability));
        if (v) smpMetrics.perform_checks();
        smpMetrics.printPerformance();     
    }
}
