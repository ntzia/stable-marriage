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
import gr.ntua.cslab.tools.Metrics;

// The original implementation of the bidirectional local search algorithm that
// uses breakmarriage operations
public class BiLS extends Abstract_SM_Algorithm
{
    double probability;
    String cost_to_optimize;

    public BiLS(int n, String menFileName, String womenFileName, Double prob, String optCost)
    {
    	super(n, menFileName, womenFileName);
        this.probability = prob;
        this.cost_to_optimize = optCost;
    }

    // Constructor for when agents are available
    public BiLS(int n, Agent[][] agents, Double prob, String optCost)
    {
        super(n, agents);
        this.probability = prob;
        this.cost_to_optimize = optCost;
    }

    public Marriage match()
    {
        boolean forward, backward;
        Marriage best, neighbour, next, mleft, mright;
        List<Marriage> neighbourhood;
        long startTime = System.nanoTime();

        Abstract_SM_Algorithm maleOpt = new GS_MaleOpt(n, agents);
        Marriage maleOptMatching = maleOpt.match();
        Abstract_SM_Algorithm femaleOpt = new GS_FemaleOpt(n, agents);
        Marriage femaleOptMatching = femaleOpt.match();

        // Initialize according to gender optimal solutions
        mleft = maleOptMatching;
        mright = femaleOptMatching;

        best = betterMarriage(mleft, mright);

        // Local Search
        forward = true;
        backward = true;

        // Local Search
        forward = true;
        backward = true;
        // First start from the male optimal and use rotations of men until local optimum
        while (forward)
        {
            next = null;
            neighbourhood = new ArrayList<Marriage>();
            // Discover all neighbours
            for (int i = 0; i < n; i++)
            {
                neighbour = breakmarriage(mleft, i, 0);
                if (neighbour != null)
                {
                    neighbourhood.add(neighbour);
                    next = betterMarriage(neighbour, next);                        
                }
            }
            // With some small probability, move to a random neighbor
            if (ThreadLocalRandom.current().nextInt(0, 100) < 100 * probability)
            {
                if (neighbourhood.size() != 0)
                    next = neighbourhood.get(ThreadLocalRandom.current().nextInt(0, neighbourhood.size()));
            }
            if (mleft == betterMarriage(mleft, next))
            {
                forward = false;
                best = betterMarriage(mleft, best);
            }
            mleft = next;               
        }

        // Now start from the female optimal and use rotations of women until local optimum
        while (backward)
        {
            next = null;
            neighbourhood = new ArrayList<Marriage>();
            // Discover all neighbours
            for (int i = 0; i < n; i++)
            {
                neighbour = breakmarriage(mright, i, 1);
                if (neighbour != null)
                {
                    neighbourhood.add(neighbour);
                    next = betterMarriage(neighbour, next);                         
                }
            }
            // With some small probability, move to a random neighbor
            if (ThreadLocalRandom.current().nextInt(0, 100) < 100 * probability)
            {
                if (neighbourhood.size() != 0)
                    next = neighbourhood.get(ThreadLocalRandom.current().nextInt(0, neighbourhood.size()));
            }
            if (mright== betterMarriage(mright, next))
            {
                backward = false;
                best = betterMarriage(mright, best);
            }
            mright = next;
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        return best;
    }

    private Marriage breakmarriage(Marriage current, int agent, int side)
    {
        int proposer, acceptor, spouse, proposerRank, old;

        // Initialize
        int[] kappa = new int[n];
        for (int i = 0; i < n; i++) kappa[i] = current.mIndex[side][i];
        int[][] mIndex = new int[2][n];
        for (int i = 0; i < n; i++)
        {
            mIndex[0][i] = current.mIndex[0][i];
            mIndex[1][i] = current.mIndex[1][i];
        }

        // Break up agent
        spouse = agents[side][agent].getAgentAt(kappa[agent]);
        kappa[agent]++;
        mIndex[side][agent] = Integer.MAX_VALUE;
        // Note: The spouse is NOT free and compares proposals to agent
        // The loop ends when spouse first accepts a proposal

        // Perform proposals (one agent active at all times)
        proposer = agent;
        while (true)
        {
            if (kappa[proposer] == n) return null;

            // Propose
            acceptor = agents[side][proposer].getAgentAt(kappa[proposer]);
            proposerRank = agents[flip(side)][acceptor].getRankOf(proposer);
            if (mIndex[flip(side)][acceptor] > proposerRank)
            {
                //Engage
                if (acceptor != spouse)
                {
                    // Break up and marry
                    old = agents[flip(side)][acceptor].getAgentAt(mIndex[flip(side)][acceptor]);
                    mIndex[side][old] = Integer.MAX_VALUE;  
                    mIndex[side][proposer] = kappa[proposer];
                    mIndex[flip(side)][acceptor] = proposerRank;
                    proposer = old;              
                }
                else
                {
                    // Spouse finally marries again and concludes proposals
                    mIndex[side][proposer] = kappa[proposer];
                    mIndex[flip(side)][acceptor] = proposerRank;
                    break;
                }
            }
            else
            {
                // rejected
                kappa[proposer]++;
            }            
        }

        // Return the new marriage
        Marriage new_m = new Marriage(n, mIndex);
        return new_m;
    }

    private Marriage betterMarriage(Marriage m1, Marriage m2)
    {
        if (cost_to_optimize.equals("SEq"))
        {
            if (m1.hasBetterSE(m2)) return m1;
            else return m2;
        }
        else if (cost_to_optimize.equals("Bal"))
        {
            if (m1.hasBetterBal(m2)) return m1;
            else return m2;
        }
        System.err.println("Reached unreachable statement");
        return null;
    }

    private static String getFinalName(double prob, String toAppend)
    {
        String className = getName();
        return className.substring(className.lastIndexOf('.') + 1) + "_" + toAppend + "_" + prob;
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

        Option cost = new Option("c", "cost", true, "cost to optimize: SexEquality(SEq) or Balance(Bal)");
        cost.setRequired(true);
        options.addOption(cost);

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
        String opt_cost = cmd.getOptionValue("cost");
        if (!opt_cost.equals("SEq") && !opt_cost.equals("Bal"))
        {
            System.err.println("Error: Requested cost not supported");
            System.exit(1);
        }

        Abstract_SM_Algorithm smp = new BiLS(n, menFile, womenFile, probability, opt_cost);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName(probability, opt_cost));
        if (v) smpMetrics.perform_checks();
        smpMetrics.printPerformance();     
    }
}
