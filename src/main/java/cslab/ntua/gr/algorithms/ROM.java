package cslab.ntua.gr.algorithms;

import java.util.LinkedList;

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

public class ROM extends Abstract_SM_Algorithm
{
    private int[][] married;
    private boolean[][][] valid;
    private LinkedList<Integer> remaining_agents;

    public ROM(int n, String menFileName, String womenFileName)
    {
    	super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public ROM(int n, Agent[][] agents)
    {
        super(n, agents);
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
    	int side, p, newcomer, other_side, p_res, idx;
        married = new int[2][n];  
        valid = new boolean[2][n][n];          
        for (int i = 0; i < n; i++)
        {
            married[0][i] = Integer.MAX_VALUE;
            married[1][i] = Integer.MAX_VALUE;
        } 
        remaining_agents = new LinkedList<Integer>();
        for (int i = 0; i < 2*n; i++) remaining_agents.add(i);
        java.util.Collections.shuffle(remaining_agents);

        for (int k = 0; k < 2*n; k++)
        {
            // Select new player
            newcomer = remaining_agents.poll();
            if (newcomer >= n)
            {
                side = 1;
                p = newcomer - n;
            }
            else
            {
                side = 0;
                p = newcomer;
            }
            // Make him valid in the game
            other_side = flip(side);
            for (int i = 0; i < n; i++)
            {
                idx = agents[other_side][i].getRankOf(p);
                valid[other_side][i][idx] = true;
            }
            // Start proposing
            while (true)
            {
                p_res = propose(p, side);
                if (p_res == -1) break;
                else p = p_res;
            }
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

    // Return values
    // -1 -> rejected by all OR married single
    // else -> married someone previously engaged to (retval)
    private int propose(int proposer, int proposerSide)
    {
        int proposeToIndex, acceptor, old;
        int acceptorSide = flip(proposerSide);

        proposeToIndex = 0;
        while (true)
        {
            if (proposeToIndex == n) return -1;
            if (valid[proposerSide][proposer][proposeToIndex])
            {
                // This index is valid for proposing
                acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
                // If acceptor is single, he accepts
                if (married[acceptorSide][acceptor] == Integer.MAX_VALUE)
                {
                    married[proposerSide][proposer] = acceptor;
                    married[acceptorSide][acceptor] = proposer;
                    return -1;
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
            }
            // In all other cases he rejects
            proposeToIndex++;
        }
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

        Abstract_SM_Algorithm smp = new ROM(n, menFile, womenFile);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks(); 
        smpMetrics.printPerformance();     
    }
}
