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
import gr.ntua.cslab.tools.Metrics;

public class PowerBalance extends Abstract_SM_Algorithm
{
    private int[][] kappa;
    private boolean married[][];
    int initialRounds;
    String cost_to_optimize;

    public PowerBalance(int n, String menFileName, String womenFileName, int initRounds, String optCost)
    {
        super(n, menFileName, womenFileName);
        initialRounds = initRounds;
        cost_to_optimize = optCost;
    }

    // Constructor for when agents are available
    public PowerBalance(int n, Agent[][] agents, int initRounds, String optCost)
    {
        super(n, agents);
        initialRounds = initRounds;
        cost_to_optimize = optCost;
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();
        
        // Initialize
        kappa = new int[2][n];
        married = new boolean[2][n];
        int side;

        // Propose
        while (!terminate())
        {
            // After initialRounds of matchmaking, use the compromising procedure
            rounds++;
            if (rounds >= initialRounds)
            {
                kappa = finish_matching();
                break;
            }
            side = pickProposers();
            for (int i = 0; i < n; i++) propose_SDA(i, side);
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        Marriage result = new Marriage(n, kappa);
        return result;
    }

    private int[][] finish_matching()
    {
        // Save state
        int[][] kappaSaved = new int[2][n];
        boolean[][] marriedSaved = new boolean[2][n];
        for (int i = 0; i < n; i++)
        {
            kappaSaved[0][i] = kappa[0][i];
            kappaSaved[1][i] = kappa[1][i];
            marriedSaved[0][i] = married[0][i];
            marriedSaved[1][i] = married[1][i];
        }
        // First try compromising men
        boolean stop;
        // Men propose until idle
        do
        {
            stop = true;
            for (int i = 0; i < n; i++)
            { 
                while (propose_SDA(i, 0)) stop = false;;
            }
        }
        while (!stop);
        // Women finish the matching
        do
        {
            stop = true;
            for (int i = 0; i < n; i++)
            { 
                while (propose_SDA(i, 1)) stop = false;
            }
        }
        while (!stop);   
        // At this point kappaSaved and marriedSaved hold the saved state
        // Kappa holds the result of men compromising 
        // Swap to run again
        int[][] temp = kappa;
        kappa = kappaSaved;
        kappaSaved = temp;
        married = marriedSaved;
        // Now try compromising women
        // Women propose until idle
        do
        {
            stop = true;
            for (int i = 0; i < n; i++)
            { 
                while (propose_SDA(i, 1)) stop = false;
            }
        }
        while (!stop);
        // Men finish the matching
        do
        {
            stop = true;
            for (int i = 0; i < n; i++)
            { 
                while (propose_SDA(i, 0)) stop = false;
            }
        }
        while (!stop);
        // Return the best result of the two
        Marriage m1 = new Marriage(n, kappaSaved);
        Marriage m2 = new Marriage(n, kappa);
        if (m1 == betterMarriage(m1, m2)) return kappaSaved;
        else return kappa;
    }

    private boolean terminate()
    {
        for (int i = 0; i < n; i++)
        {
            if (!married[0][i]) return false;
        }
        return true;
    }

    private int pickProposers()
    {
        // First check if a side is idle
        boolean idle = true;
        for (int i = 0; i < n; i++)
        {
            if (!married[0][i] && kappa[0][i] < n)
            {
                idle = false;
                break;
            }
        }
        if (idle) return 1;
        idle = true;
        for (int i = 0; i < n; i++)
        {
            if (!married[1][i] && kappa[1][i] < n)
            {
                idle = false;
                break;
            }
        }
        if (idle) return 0;

        // No side is idle, decide upon costs
        return pickStrongSide();
    }  

    private int pickStrongSide()
    {
        int menCost = 0;
        int womenCost = 0;
        for (int i = 0; i < n; i++)
        {
            menCost += kappa[0][i];
            womenCost += kappa[1][i];
        }
        if (menCost > womenCost) return 1;
        else if (womenCost > menCost) return 0;
        // If the costs are the same, decide randomly
        else return (Math.random() < 0.5)?0:1;
    }

    // Returns true if a proposal was issued, false otherwise
    private boolean propose_SDA(int proposer, int proposerSide)
    {
        int proposeToIndex = kappa[proposerSide][proposer];
        if (!married[proposerSide][proposer] && proposeToIndex < n)
        {
            // Wants to propose
            int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
            if (evaluate_SDA(acceptor, proposer, flip(proposerSide))) married[proposerSide][proposer] = true;
            else kappa[proposerSide][proposer]++;
            return true;
        }
        else return false;
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

    private static String getFinalName(CommandLine cmd, String toAppend)
    {
        String className = getName();
        if (cmd.hasOption("initRounds"))
        {
            int init = Integer.parseInt(cmd.getOptionValue("initRounds"));
            return className.substring(className.lastIndexOf('.') + 1) + "_" + toAppend + "_" + init;
        }
        else
        {
            return className.substring(className.lastIndexOf('.') + 1) + "_" + toAppend;
        }
    }

    public static void main(String args[]) 
    {
        // Parse the command line
        Options options = new Options();

        Option size = new Option("n", "size", true, "size of instance");
        size.setRequired(true);
        options.addOption(size);

        Option initR = new Option("i", "initRounds", true, "initial rounds(*n) before compromise");
        initR.setRequired(false);
        options.addOption(initR);

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
        int init;
        if (cmd.hasOption("initRounds")) init = Integer.parseInt(cmd.getOptionValue("initRounds"));
        else init = (int) (Math.ceil(Math.log(n) * Math.log(n) / (Math.log(2) * Math.log(2) * 10)));
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

        Abstract_SM_Algorithm smp = new PowerBalance(n, menFile, womenFile, init*n, opt_cost);
        Marriage matching = smp.match();
        Metrics smpMetrics;
        smpMetrics = new Metrics(smp, matching, getFinalName(cmd, opt_cost));
        if (v) smpMetrics.perform_checks();
        smpMetrics.printPerformance();
    }
}