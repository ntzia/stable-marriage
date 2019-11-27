package cslab.ntua.gr.algorithms;

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

public class MinRegret extends Abstract_SM_Algorithm
{
    public MinRegret(int n, String menFileName, String womenFileName)
    {
        super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public MinRegret(int n, Agent[][] agents)
    {
        super(n, agents);
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
        int rcost, i;
        Marriage current, prev, w_min, m_min, res;
        Abstract_SM_Algorithm maleOpt = new GS_MaleOpt(n, agents);
        Marriage maleOptMatching = maleOpt.match();
        Abstract_SM_Algorithm femaleOpt = new GS_FemaleOpt(n, agents);
        Marriage femaleOptMatching = femaleOpt.match();

        // Find the woman regret minimum
        current = maleOptMatching;
        prev = null;
        w_min = null;
        while (true)
        {
            rcost = current.getRCost();
            for (i = 0; i < n; i++) 
            {
                if (current.mIndex[1][i] == rcost) break;
            }
            // If no woman yields the regret cost, the previous marriage is the woman regret minimum
            if (i == n) 
            {
                w_min = prev;
                break;                
            }
            // Woman i yields the regret cost
            // If i and her husband are a couple in the woman optimal marriage, this marriage is the woman reget minimum
            if (current.mIndex[1][i] == femaleOptMatching.mIndex[1][i])
            {
                w_min = current;
                break;                
            }
            // If none of the above applies continue to the next marriage
            prev = current;
            current = breakmarriage(current, agents[1][i].getAgentAt(current.mIndex[1][i]), 0);
        }

        // Find the man regret minimum
        current = femaleOptMatching;
        prev = null;
        m_min = null;
        while (true)
        {
            rcost = current.getRCost();
            for (i = 0; i < n; i++) 
            {
                if (current.mIndex[0][i] == rcost) break;
            }
            // If no man yields the regret cost, the previous marriage is the man regret minimum
            if (i == n) 
            {
                m_min = prev;
                break;                
            }
            // Man i yields the regret cost
            // If i and his wife are a couple in the man optimal marriage, this marriage is the man reget minimum
            if (current.mIndex[0][i] == maleOptMatching.mIndex[0][i])
            {
                m_min = current;
                break;                
            }
            // If none of the above applies continue to the next marriage
            prev = current;
            current = breakmarriage(current, agents[0][i].getAgentAt(current.mIndex[0][i]), 1);
        }

        if (w_min == null) res = m_min;
        else if (m_min == null) res = w_min;
        else
        {
            if (w_min.hasBetterRCost(m_min)) res = w_min;
            else res = m_min;
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        return res;
    }

    private Marriage breakmarriage(Marriage current, int agent, int side)
    {
        int proposer, acceptor, spouse, proposerRank, old;

        // Initialize
        int[] kappa = new int[n];
        for (int i = 0; i < n; i++) kappa[i] = current.mIndex[side][i];
        int[][] married = new int[2][n];
        for (int i = 0; i < n; i++)
        {
            married[0][i] = current.mIndex[0][i];
            married[1][i] = current.mIndex[1][i];
        }

        // Break up agent
        spouse = agents[side][agent].getAgentAt(kappa[agent]);
        kappa[agent]++;
        married[side][agent] = Integer.MAX_VALUE;
        // Note: The spouse is NOT free and compares proposals to agent
        // The loop ends when spouse first accepts a proposal

        // Perform proposals (one agent active at all times)
        proposer = agent;
        while (true)
        {
            // Propose
            acceptor = agents[side][proposer].getAgentAt(kappa[proposer]);
            proposerRank = agents[flip(side)][acceptor].getRankOf(proposer);
            if (married[flip(side)][acceptor] > proposerRank)
            {
                //Engage
                if (acceptor != spouse)
                {
                    // Break up and marry
                    old = agents[flip(side)][acceptor].getAgentAt(married[flip(side)][acceptor]);
                    married[side][old] = Integer.MAX_VALUE;  
                    married[side][proposer] = kappa[proposer];
                    married[flip(side)][acceptor] = proposerRank;
                    proposer = old;              
                }
                else
                {
                    // Spouse finally marries again and concludes proposals
                    married[side][proposer] = kappa[proposer];
                    married[flip(side)][acceptor] = proposerRank;
                    break;
                }
            }
            else kappa[proposer]++;            
        }
        // Return the new marriage
        Marriage new_m = new Marriage(n, married);
        return new_m;
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

        Abstract_SM_Algorithm smp = new MinRegret(n, menFile, womenFile);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks();  
        smpMetrics.printPerformance();
    }
}