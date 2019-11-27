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

public class EDS extends Abstract_SM_Algorithm
{
    private int[][] nIndex, mIndex;
    private int starting_side;

    public EDS(int n, String menFileName, String womenFileName, int starting_side)
    {
        super(n, menFileName, womenFileName);
        this.starting_side = starting_side;
    }

    // Constructor for when agents are available
    public EDS(int n, Agent[][] agents, int starting_side)
    {
        super(n, agents);
        this.starting_side = starting_side;
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
        int side = starting_side;
        boolean cc_increase;
        int propose_res;
        nIndex = new int[2][n];
        mIndex = new int[2][n];  
        for (int i = 0; i < n; i++)
        {
            mIndex[0][i] = Integer.MAX_VALUE;
            mIndex[1][i] = Integer.MAX_VALUE;
        }

        while (!terminate())
        {
            rounds++;
            cc_increase = false;
            for (int i = 0; i < n; i++)
            {
                propose_res = propose(i, side);
                if (propose_res == 2) cc_increase = true;
            } 

            // If CC did not increase, force cc increase 
            if (!cc_increase) stage_1(side);
            // Swap the roles of Proposers - Receivers
            side = flip(side);
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        Marriage res = new Marriage(n, nIndex);
        return res;
    }

    // Suspend Discontent
    // Try proposing with all the proposers at once
    private void stage_1(int proposers_side)
    {
        int propose_res;
        boolean idle;

        suspend_discontent(proposers_side);

        while (true)
        {
            idle = true;

            for (int i = 0; i < n; i++)
            {
                propose_res = propose_noMotivated(i, proposers_side);
                if (propose_res != 0) idle = false;
                if (propose_res == 2) return;
            } 

            // If Proposers side is idle, continue to stage 2
            if (idle) break;
        }

        // Stage 1 failed -> Proposers are idle
        // Continue to stage 2
        stage_2(flip(proposers_side));
    }

    private void suspend_discontent(int proposers_side)
    {
        int partner;
        int receivers_side = flip(proposers_side);

        for (int i = 0; i < n; i++)
        {
            if (nIndex[receivers_side][i] < mIndex[receivers_side][i] && mIndex[receivers_side][i] != Integer.MAX_VALUE)
            {
                // Motivated
                partner = agents[receivers_side][i].getAgentAt(mIndex[receivers_side][i]);
                // Break up
                mIndex[receivers_side][i] = Integer.MAX_VALUE;
                mIndex[proposers_side][partner] = Integer.MAX_VALUE;
            }            
        }   
    }

    // Terminate with repeated proposals by Receivers
    private void stage_2(int receivers_side)
    {
        boolean stop;

        do
        {
            stop = true;
            rounds++;
            for (int i = 0; i < n; i++)
            { 
                if (propose_noMotivated(i, receivers_side) != 0) stop = false;
            }
        }
        while (!stop); 
    }

/*
    private int compute_cc()
    {
        int partner_marriedToIndex, partner_proposeToIndex, partner;
        int proposeToIndex, marriedToIndex;
        int cc = 0;

        for (int i = 0; i < n; i++)
        {
            if (mIndex[0][i] != Integer.MAX_VALUE)
            {
                proposeToIndex = nIndex[0][i];
                marriedToIndex = mIndex[0][i];

                partner = agents[0][i].getAgentAt(marriedToIndex);
                partner_marriedToIndex = mIndex[1][partner];
                partner_proposeToIndex = nIndex[1][partner];

                if (marriedToIndex <= proposeToIndex && partner_marriedToIndex <= partner_proposeToIndex) cc++;
            }
        }

        return cc;
    }
*/

    private boolean terminate()
    {
        for (int i = 0; i < n; i++)
        {
            if (nIndex[0][i] < mIndex[0][i]) return false;
            if (nIndex[1][i] < mIndex[1][i]) return false;
        }
        return true;
    }

    // Return Values
    // 0 -> idling
    // 1 -> proposed but NO cc increase
    // 2 -> proposed and cc increase
    private int propose(int proposer, int proposerSide)
    {   
        //System.out.println("\nTime for Agent " + a.getID() + " of Side " + side + " to propose!");
        int proposeToIndex = nIndex[proposerSide][proposer];
        int marriedToIndex = mIndex[proposerSide][proposer];
        int answer;

        if (proposeToIndex < marriedToIndex && proposeToIndex < n)
        {
            // Wants to propose
            int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
            //System.out.println("Agent " + a.getID() + " of Side " + side + " proposes to Agent " + b.getID());
            answer = evaluate(acceptor, proposer, flip(proposerSide));
            if (answer == 2)
            {
                // Break up with old
                if (marriedToIndex != Integer.MAX_VALUE)
                {
                    int old = agents[proposerSide][proposer].getAgentAt(marriedToIndex);
                    mIndex[flip(proposerSide)][old] = Integer.MAX_VALUE;        
                }
                //Engage with new
                mIndex[proposerSide][proposer] = proposeToIndex;
                // CC increase
                return 2;
            }
            else if (answer == 1)
            {
                // Break up with old
                if (marriedToIndex != Integer.MAX_VALUE)
                {
                    int old = agents[proposerSide][proposer].getAgentAt(marriedToIndex);
                    mIndex[flip(proposerSide)][old] = Integer.MAX_VALUE;        
                }
                //Engage with new
                mIndex[proposerSide][proposer] = proposeToIndex;
                // Acceptor said yes, but NO CC increase
                return 1;
            }
            else
            {
                // b rejected a
                nIndex[proposerSide][proposer]++;
                // Check if Motivated became Content!!
                if (nIndex[proposerSide][proposer] == marriedToIndex) return 2; // CC increase!!
                else return 1; // No CC increase
            }
        }
        else
        {
            //System.out.println("Agent " + a.getID() + " of Side " + side + " skips turn: PIndx = " + a.getPIndx() + " , MIndx = " + a.getMIndx());
            // Idle
            return 0;
        }
    }

    // Return Values
    // 0 -> reject
    // 1 -> accept but NO cc increase
    // 2 -> accept and cc increased
    private int evaluate(int acceptor, int proposer, int acceptorSide)
    {
        int proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
        int proposeToIndex = nIndex[acceptorSide][acceptor];
        int marriedToIndex = mIndex[acceptorSide][acceptor];
        int answer;

        if (marriedToIndex > proposerRank)
        {
            //System.out.println("Agent " + a.getID() + " of Side " + flip(sideB) + " accepts the proposal.");

            answer = 2;
            
            // Break up with old
            if (marriedToIndex != Integer.MAX_VALUE)
            {
                int old = agents[acceptorSide][acceptor].getAgentAt(marriedToIndex);

                // CC increase depends on whether acceptor is in a doubly Content pair
                int partner_marriedToIndex = mIndex[flip(acceptorSide)][old];
                int partner_proposeToIndex = nIndex[flip(acceptorSide)][old];
                if (marriedToIndex <= proposeToIndex && partner_marriedToIndex <= partner_proposeToIndex) answer = 1;

                mIndex[flip(acceptorSide)][old] = Integer.MAX_VALUE;                
            }

            // CC increase also depends on whether acceptor will be Motivated after accepting
            if (proposerRank > proposeToIndex) answer = 1;
            
            //Engage with new
            mIndex[acceptorSide][acceptor] = proposerRank;

            // Boost confidence if needed
            if (proposeToIndex > proposerRank) 
                nIndex[acceptorSide][acceptor] = proposerRank;                  
                // !!!! NOT +1 because of propose_noMotivated
        }
        else
        {
            //System.out.println("Agent " + a.getID() + " of Side " + flip(sideB) + " rejects the proposal.");
            answer = 0;
        }
        return answer;

    }

    // Return Values
    // 0 -> idling
    // 1 -> proposed but NO cc increase
    // 2 -> proposed and cc increased
    private int propose_noMotivated(int proposer, int proposerSide)
    {   
        //System.out.println("\nTime for Agent " + a.getID() + " of Side " + side + " to propose!");
        int proposeToIndex = nIndex[proposerSide][proposer];
        int marriedToIndex = mIndex[proposerSide][proposer];
        int answer;

        if (proposeToIndex < marriedToIndex && proposeToIndex < n)
        {
            // Wants to propose
            int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
            //System.out.println("Agent " + a.getID() + " of Side " + side + " proposes to Agent " + b.getID());
            answer = evaluate_noMotivated(acceptor, proposer, flip(proposerSide));
            if (answer == 2)
            {
                // Break up with old
                if (marriedToIndex != Integer.MAX_VALUE)
                {
                    int old = agents[proposerSide][proposer].getAgentAt(marriedToIndex);
                    mIndex[flip(proposerSide)][old] = Integer.MAX_VALUE;        
                }
                // Engage with new
                mIndex[proposerSide][proposer] = proposeToIndex;
                // CC increase
                return 2;
            }
            else if (answer == 1)
            {
                // Break up with old
                if (marriedToIndex != Integer.MAX_VALUE)
                {
                    int old = agents[proposerSide][proposer].getAgentAt(marriedToIndex);
                    mIndex[flip(proposerSide)][old] = Integer.MAX_VALUE;        
                }
                // Engage with new
                mIndex[proposerSide][proposer] = proposeToIndex;
                // Acceptor said yes, but NO CC increase
                return 1;                
            }
            else
            {
                // b rejected a
                nIndex[proposerSide][proposer]++;
                // Check if Motivated became Content!!
                if (nIndex[proposerSide][proposer] == marriedToIndex) return 2; // CC increase!!
                else return 1; // No CC increase
            }
        }
        else
        {
            //System.out.println("Agent " + a.getID() + " of Side " + side + " skips turn: PIndx = " + a.getPIndx() + " , MIndx = " + a.getMIndx());
            // Idle
            return 0;
        }
    }

    // Return Values
    // 0 -> reject
    // 1 -> accept but NO cc increase
    // 2 -> accept and cc increased
    private int evaluate_noMotivated(int acceptor, int proposer, int acceptorSide)
    {
        int proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
        int proposeToIndex = nIndex[acceptorSide][acceptor];
        int marriedToIndex = mIndex[acceptorSide][acceptor];
        int answer;

        if (proposeToIndex >= proposerRank)
        {
            //System.out.println("Agent " + acceptor + " of Side " + acceptorSide + " accepts the proposal.");

            answer = 2;

            // Break up with old
            if (marriedToIndex != Integer.MAX_VALUE)
            {
                int partner = agents[acceptorSide][acceptor].getAgentAt(marriedToIndex);

                // CC increase depends on whether acceptor is in a doubly Content pair
                int partner_marriedToIndex = mIndex[flip(acceptorSide)][partner];
                int partner_proposeToIndex = nIndex[flip(acceptorSide)][partner];
                // Previously doubly Content pair -> No CC increase
                if (marriedToIndex <= proposeToIndex && partner_marriedToIndex <= partner_proposeToIndex) answer = 1;

                mIndex[flip(acceptorSide)][partner] = Integer.MAX_VALUE;   
            } 
            
            //Engage with new
            mIndex[acceptorSide][acceptor] = proposerRank;

            // Boost confidence if needed
            if (proposeToIndex > proposerRank) 
                nIndex[acceptorSide][acceptor] = proposerRank;
                // !!!! NOT +1 because evaluate condition is (proposeToIndex >= proposerRank)
                // +1 means i will accept if the guy at +1 proposes me
        }
        else
        {
            //System.out.println("Agent " + a.getID() + " of Side " + flip(sideB) + " rejects the proposal.");
            answer = 0;
        }
        return answer;
    }

    private static String getFinalName(CommandLine cmd)
    {
        String className = getName();
        if (cmd.hasOption("starting_side"))
        {
            int starting_side = Integer.parseInt(cmd.getOptionValue("starting_side"));
            return className.substring(className.lastIndexOf('.') + 1) + "_" + starting_side;
        }
        else
        {
            return className.substring(className.lastIndexOf('.') + 1);
        }
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

        Option start_side = new Option("ss", "starting_side", true, "starting side");
        start_side.setRequired(false);
        options.addOption(start_side);

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
        int starting_side;
        if (cmd.hasOption("starting_side")) starting_side = Integer.parseInt(cmd.getOptionValue("starting_side"));
        // By default, randomize the starting side
        else starting_side = (Math.random() < 0.5)?0:1;
        boolean v;
        if (cmd.hasOption("verify")) v = true;
        else v = false;

        Abstract_SM_Algorithm smp = new EDS(n, menFile, womenFile, starting_side);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName(cmd));
        if (v) smpMetrics.perform_checks();
        smpMetrics.printPerformance();
    }
}
