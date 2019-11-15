package cslab.ntua.gr.algorithms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;
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

public class DACC extends Abstract_SM_Algorithm
{
    private int[][] married, kappa;
    private boolean[][][] deleted;
    private Stack<Integer> cc;
    private ArrayList<Set<Integer>> appliedTo;
    private String sequence;
    private int[] costs;

    public DACC(int n, String menFileName, String womenFileName, String sequence)
    {
        super(n, menFileName, womenFileName);
        this.sequence = sequence;
    }

    // Constructor for when agents are available
    public DACC(int n, Agent[][] agents, String sequence)
    {
        super(n, agents);
        this.sequence = sequence;
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();
        int side, j, cc_p, cc_side, proposer;

        // Initialize
        deleted = new boolean[2][n][n];
        married = new int[2][n];  
        for (int i = 0; i < n; i++)
        {
            married[0][i] = Integer.MAX_VALUE;
            married[1][i] = Integer.MAX_VALUE;
        }
        kappa = new int[2][n];  
        cc = new Stack<Integer>();
        appliedTo = new ArrayList<Set<Integer>>();
        for (int i = 0; i < 2 * n; i++) appliedTo.add(new HashSet<Integer>());
        costs = new int[2];
        costs[0] = 0;
        costs[1] = 0;

        // Propose
        if (sequence.equals("D"))
        {
            side = 0;
            while (!terminate())
            {
                rounds++;
                side = pickProposers();
                for (int i = 0; i < n; i++) 
                {
                    while (!cc.empty())
                    {
                        j = cc.peek();
                        // Decode
                        if (j < n) {cc_p = j; cc_side = 0;}
                        else {cc_p = j - n; cc_side = 1;}
                        propose(cc_p, cc_side);
                        if (married[cc_side][cc_p] != Integer.MAX_VALUE || kappa[cc_side][cc_p] == n) cc.remove(Integer.valueOf(j));
                    } 
                    propose(i, side);
                }
            }            
        }
        else if (sequence.equals("R"))
        {
            int proposals = 0;
            while (!terminate())
            {
                proposer = ThreadLocalRandom.current().nextInt(0, 2*n);
                if (proposer >= n)
                {
                    side = 1;
                    proposer = proposer - n;
                }
                else
                {
                    side = 0;
                }
                while (!cc.empty())
                {
                    j = cc.peek();
                    // Decode
                    if (j < n) {cc_p = j; cc_side = 0;}
                    else {cc_p = j - n; cc_side = 1;}
                    propose(cc_p, cc_side);
                    if (married[cc_side][cc_p] != Integer.MAX_VALUE || kappa[cc_side][cc_p] == n) cc.remove(Integer.valueOf(j));
                } 
                propose(proposer, side);
                proposals++;
                // Check for termination
                if (proposals >= n)
                {
                    proposals = 0;
                    if (terminate()) break;
                }
            }
        }
        /*
        else if (sequence.equals("SA"))
        {
            int proposals = 0;
            while (true)
            {
                // Compensation Chain
                while (!cc.empty())
                {
                    j = cc.peek();
                    // Decode
                    if (j < n) {cc_p = j; cc_side = 0;}
                    else {cc_p = j - n; cc_side = 1;}
                    propose(cc_p, cc_side);
                    if (married[cc_side][cc_p] != Integer.MAX_VALUE || kappa[cc_side][cc_p] == n) cc.remove(Integer.valueOf(j));
                } 
                // Do one proposal with strong agent
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

                // Check for termination
                proposals++;
                if (proposals >= n)
                {
                    proposals = 0;
                    if (terminate()) break;
                    if (strongSideIdle()) compensate_random_weak();
                }
            }
        }
        else if (sequence.equals("EO"))
        {
            side = 1;
            while (!terminate())
            {
                rounds++;
                side = flip(side);
                for (int i = 0; i < n; i++) 
                {
                    while (!cc.empty())
                    {
                        j = cc.peek();
                        // Decode
                        if (j < n) {cc_p = j; cc_side = 0;}
                        else {cc_p = j - n; cc_side = 1;}
                        propose(cc_p, cc_side);
                        if (married[cc_side][cc_p] != Integer.MAX_VALUE || kappa[cc_side][cc_p] == n) cc.remove(Integer.valueOf(j));
                    } 
                    propose(i, side);
                }
            }            
        }
        */

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        Marriage result = new Marriage(n, married);
        return result;
    }

    /*
    private void compensate_random_weak()
    {
        int weak_side = flip(pickStrongSide());
        ArrayList<Integer> weak_actives = new ArrayList<Integer>();
        for (int i = 0; i < n; i++)    
        {
            if (kappa[weak_side][i] < married[weak_side][i])
            {
                // Active!
                weak_actives.add(i + n * weak_side);
            }
        }  
        int randIdx = ThreadLocalRandom.current().nextInt(0, weak_actives.size());
        cc.push(weak_actives.get(randIdx));
        return;
    }
    */

    private boolean terminate()
    {
        for (int i = 0; i < n; i++)
        {
            if (kappa[0][i] < married[0][i]) return false;
            if (kappa[1][i] < married[1][i]) return false;
        }
        return true;
    }

    /*
    private boolean strongSideIdle()
    {
        int strongSide = pickStrongSide();
        for (int i = 0; i < n; i++)
        {
            if (kappa[strongSide][i] < married[strongSide][i]) return false;
        }
        return true;
    }
    */

    private int pickProposers()
    {
        // First check if a side is idle
        boolean idle = true;
        for (int i = 0; i < n; i++)
        {
            if (kappa[0][i] < married[0][i] && kappa[0][i] < n)
            {
                idle = false;
                break;
            }
        }
        if (idle) return 1;
        idle = true;
        for (int i = 0; i < n; i++)
        {
            if (kappa[1][i] < married[1][i] && kappa[1][i] < n)
            {
                idle = false;
                break;
            }
        }
        if (idle) return 0;

        // No side is idle, decide upon costs
        return pickStrongSide();
    }  

    /*
    private int pickProposer()
    {     
        int side = pickStrongSide();  
        int i = ThreadLocalRandom.current().nextInt(0, n);
        return (i + n * side);
    }  
    */

    private int pickStrongSide()
    {
        if (costs[0] >= costs[1]) return 1;
        else return 0;
    }

    private void propose(int proposer, int proposerSide)
    {  
        Set<Integer> s;
        int proposer_spouse, acceptor_spouse, index_of_p, index_of_a;
        int acceptorSide = flip(proposerSide);

        // If already matched or budget set empty, then return
        if (married[proposerSide][proposer] <= kappa[proposerSide][proposer] || kappa[proposerSide][proposer] == n) return;
        // Proposer applies to acceptor
        int acceptor = agents[proposerSide][proposer].getAgentAt(kappa[proposerSide][proposer]);

        // Record that proposer applied to acceptor
        s = appliedTo.get(acceptor + n * acceptorSide);
        s.add(proposer);
        // Increase budget of acceptor
        int proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
        deleted[acceptorSide][acceptor][proposerRank] = false;
        if (kappa[acceptorSide][acceptor] > proposerRank) 
        {
            costs[acceptorSide] -= kappa[acceptorSide][acceptor];
            costs[acceptorSide] += proposerRank;
            kappa[acceptorSide][acceptor] = proposerRank;
        }

        if (married[acceptorSide][acceptor] > proposerRank)
        {
            // Proposer accepted
            // If proposer was matched to someone different
            if (married[proposerSide][proposer] != Integer.MAX_VALUE)
            {
                // Compensate if deceived
                proposer_spouse = agents[proposerSide][proposer].getAgentAt(married[proposerSide][proposer]);
                if (appliedTo.get(proposer_spouse + n * acceptorSide).contains(proposer)) cc.push(proposer_spouse + n * acceptorSide); 
                // Remove from budget
                index_of_p = agents[acceptorSide][proposer_spouse].getRankOf(proposer);
                deleted[acceptorSide][proposer_spouse][index_of_p] = true;
                costs[acceptorSide] -= kappa[acceptorSide][proposer_spouse];
                kappa[acceptorSide][proposer_spouse] = next_valid_index(proposer_spouse, acceptorSide, kappa[acceptorSide][proposer_spouse]);
                costs[acceptorSide] += kappa[acceptorSide][proposer_spouse];
                // Divorce
                married[acceptorSide][proposer_spouse] = Integer.MAX_VALUE;
            }
            // If acceptor was matched to someone different
            if (married[acceptorSide][acceptor] != Integer.MAX_VALUE)
            {
                // Compensate if deceived
                acceptor_spouse = agents[acceptorSide][acceptor].getAgentAt(married[acceptorSide][acceptor]);
                if (appliedTo.get(acceptor_spouse + n * proposerSide).contains(acceptor)) cc.push(acceptor_spouse + n * proposerSide); 
                // Remove from budget
                index_of_a = agents[proposerSide][acceptor_spouse].getRankOf(acceptor);
                deleted[proposerSide][acceptor_spouse][index_of_a] = true;
                costs[proposerSide] -= kappa[proposerSide][acceptor_spouse];
                kappa[proposerSide][acceptor_spouse] = next_valid_index(acceptor_spouse, proposerSide, kappa[proposerSide][acceptor_spouse]);
                costs[proposerSide] += kappa[proposerSide][acceptor_spouse];
                // Divorce
                married[proposerSide][acceptor_spouse] = Integer.MAX_VALUE;
            }
            // Match proposer with acceptor
            married[proposerSide][proposer] = agents[proposerSide][proposer].getRankOf(acceptor);
            married[acceptorSide][acceptor] = agents[acceptorSide][acceptor].getRankOf(proposer);;
        }
        else
        {
            // Proposer rejected
            // Remove acceptor from proposer's budget
            deleted[proposerSide][proposer][kappa[proposerSide][proposer]] = true;
            costs[proposerSide] -= kappa[proposerSide][proposer];
            kappa[proposerSide][proposer] = next_valid_index(proposer, proposerSide, kappa[proposerSide][proposer]);
            costs[proposerSide] += kappa[proposerSide][proposer];
        }
    }

    private int next_valid_index(int a, int side, int idx)
    {
        for (int i = idx; i < n; i++)
        {
            if (!deleted[side][a][i]) return i;
        }
        return n;
    }

    private static String getFinalName(String toAppend)
    {
        String className = getName();
        return className.substring(className.lastIndexOf('.') + 1) + "_" + toAppend;
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

        Option sequence = new Option("s", "sequence", true, "proposing sequence: Deterministic(D) or Randomized(R)");
        sequence.setRequired(true);
        options.addOption(sequence);

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
        String seq = cmd.getOptionValue("sequence");
        if (!seq.equals("D") && !seq.equals("R"))
        //if (!seq.equals("D") && !seq.equals("R") && !seq.equals("SA") && !seq.equals("EO"))
        {
            System.err.println("Error: Requested sequence not supported");
            System.exit(1);
        }

        Abstract_SM_Algorithm smp = new DACC(n, menFile, womenFile, seq);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName(seq));
        if (v) smpMetrics.perform_checks();    
        smpMetrics.printPerformance();

    }
}
