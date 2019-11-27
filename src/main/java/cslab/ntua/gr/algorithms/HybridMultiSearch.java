package cslab.ntua.gr.algorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Marriage;
import cslab.ntua.gr.entities.Rotations;
import cslab.ntua.gr.tools.Metrics;

public class HybridMultiSearch extends Abstract_SM_Algorithm
{
    private int[][] kappa;
    private boolean married[][];
    private int pbRounds, ls_max_step, srch_cnt;
    String cost_to_optimize;

    public HybridMultiSearch(int n, String menFileName, String womenFileName, int pbRounds, int ls_max_step, int srch_cnt, String optCost)
    {
        super(n, menFileName, womenFileName);
        this.pbRounds = pbRounds;
        this.ls_max_step = ls_max_step;
        this.srch_cnt = srch_cnt;
        this.cost_to_optimize = optCost;
    }

    // Constructor for when agents are available
    public HybridMultiSearch(int n, Agent[][] agents, int pbRounds, int ls_max_step, int srch_cnt, String optCost)
    {
        super(n, agents);
        this.pbRounds = pbRounds;
        this.ls_max_step = ls_max_step;
        this.srch_cnt = srch_cnt;
        this.cost_to_optimize = optCost;
    }
    
    public Marriage match()
    {
        long startTime = System.nanoTime();
        
        // Initialize
        kappa = new int[2][n];
        married = new boolean[2][n];
        Collection<Marriage> pb_solutions = new LinkedList<Marriage>();
        Collection<Marriage> two_solutions;
        int side;
        boolean forced_term = false;

        // Determine stopping points
        // Always include last round
        // Next include first round
        // Evenly split the remaining in [2, last-1]
        int remaining_stopping_points = srch_cnt;
        Stack<Integer> stopping_points = new Stack<Integer>();
        List<Integer> temp = new ArrayList<Integer>();
        stopping_points.push(pbRounds);
        remaining_stopping_points--;
        int curr = 2;
        double step = ((pbRounds - 1) - 2) * 1.0 / (remaining_stopping_points - 1);
        while (remaining_stopping_points > 1)
        {
            temp.add(curr);
            remaining_stopping_points--;  
            curr = (int) Math.floor(curr + step);    
        }
        Collections.reverse(temp);
        stopping_points.addAll(temp);
        if (remaining_stopping_points > 0)
        {
            stopping_points.push(1);
            remaining_stopping_points--;         
        }

        // Phase 1: Propose
        while (!terminate())
        {
            rounds++;
            side = pickProposers();
            for (int i = 0; i < n; i++) propose_SDA(kappa, married, i, side);

            if (rounds == stopping_points.peek())
            {
                stopping_points.pop();
                two_solutions = finish_matching();
                pb_solutions.addAll(two_solutions);
            }
            if (rounds >= pbRounds)
            {
                forced_term = true;
                break;
            }
        }
        if (!forced_term) pb_solutions.add(new Marriage(n, kappa));

        // Phase 2: Local Search on lattice
        Marriage current, best_neighbour, neighbour;
        boolean done;
        Abstract_SM_Algorithm maleOpt = new GS_MaleOpt(n, agents);
        Marriage maleOptMatching = maleOpt.match();
        Abstract_SM_Algorithm femaleOpt = new GS_FemaleOpt(n, agents);
        Marriage femaleOptMatching = femaleOpt.match();
        Rotations rotations = new Rotations(n, agents, maleOptMatching, femaleOptMatching);
        Marriage best = null;
        int steps;

        for (Marriage starting_marriage : pb_solutions)
        {
            current = starting_marriage;
            steps = 0;
            while (true)
            {
                side = pickStrongSide(current.mIndex);
                done = true;
                best_neighbour = current;
                // Search for neighbours
                for (int i = 0; i < rotations.count; i++)
                {
                    if (rotations.isExposed(current, i, side))
                    {
                        neighbour = rotations.eliminate(current, i, side);
                        if (neighbour == betterMarriage(neighbour, best_neighbour)) 
                        {
                            best_neighbour = neighbour;
                            done = false;
                        } 
                    }
                }
                current = best_neighbour;
                steps++;
                if (done || steps >= ls_max_step)
                {
                    best = betterMarriage(current, best);
                    break;
                }
            }            
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        return best;
    }

    private Collection<Marriage> finish_matching()
    {
        // First compromise men
        int[][] kappa1 = new int[2][n];
        boolean[][] married1 = new boolean[2][n];
        for (int i = 0; i < n; i++)
        {
            kappa1[0][i] = kappa[0][i];
            kappa1[1][i] = kappa[1][i];
            married1[0][i] = married[0][i];
            married1[1][i] = married[1][i];
        }
        boolean stop;
        // Men propose until idle
        do
        {
            stop = true;
            for (int i = 0; i < n; i++)
            { 
                while (propose_SDA(kappa1, married1, i, 0)) stop = false;;
            }
        }
        while (!stop);
        // Women finish the matching
        do
        {
            stop = true;
            for (int i = 0; i < n; i++)
            { 
                while (propose_SDA(kappa1, married1, i, 1)) stop = false;
            }
        }
        while (!stop);   
        Marriage m1 = new Marriage(n, kappa1);

        // Now compromise women
        int[][] kappa2 = new int[2][n];
        boolean[][] married2 = new boolean[2][n];
        for (int i = 0; i < n; i++)
        {
            kappa2[0][i] = kappa[0][i];
            kappa2[1][i] = kappa[1][i];
            married2[0][i] = married[0][i];
            married2[1][i] = married[1][i];
        }
        // Women propose until idle
        do
        {
            stop = true;
            for (int i = 0; i < n; i++)
            { 
                while (propose_SDA(kappa2, married2, i, 1)) stop = false;
            }
        }
        while (!stop);
        // Men finish the matching
        do
        {
            stop = true;
            for (int i = 0; i < n; i++)
            { 
                while (propose_SDA(kappa2, married2, i, 0)) stop = false;
            }
        }
        while (!stop);
        Marriage m2 = new Marriage(n, kappa2);

        Collection<Marriage> res = new LinkedList<Marriage>();
        res.add(m1);
        res.add(m2);
        return res;
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
        return pickStrongSide(kappa);
    }  

    private int pickStrongSide(int[][] positions)
    {
        int menCost = 0;
        int womenCost = 0;
        for (int i = 0; i < n; i++)
        {
            menCost += positions[0][i];
            womenCost += positions[1][i];
        }
        if (menCost >= womenCost) return 1;
        else return 0;
    }

    // Returns true if a proposal was issued, false otherwise
    private boolean propose_SDA(int[][] kappaArr, boolean[][] marriedArr, int proposer, int proposerSide)
    {
        int proposeToIndex = kappaArr[proposerSide][proposer];
        if (!marriedArr[proposerSide][proposer] && proposeToIndex < n)
        {
            // Wants to propose
            int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
            if (evaluate_SDA(kappaArr, marriedArr, acceptor, proposer, flip(proposerSide))) marriedArr[proposerSide][proposer] = true;
            else kappaArr[proposerSide][proposer]++;
            return true;
        }
        else return false;
    }

    // Returns true if acceptor agrees to marry proposer
    private boolean evaluate_SDA(int[][] kappaArr, boolean[][] marriedArr, int acceptor, int proposer, int acceptorSide)
    {
        int proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
        int proposeToIndex = kappaArr[acceptorSide][acceptor];
        if (proposeToIndex >= proposerRank)
        {
            // Break up with old
            if (marriedArr[acceptorSide][acceptor])
            {
                int old = agents[acceptorSide][acceptor].getAgentAt(proposeToIndex);
                marriedArr[flip(acceptorSide)][old] = false;                
            }            
            //Engage with new
            marriedArr[acceptorSide][acceptor] = true;
            // Boost confidence if needed
            if (proposeToIndex > proposerRank) kappaArr[acceptorSide][acceptor] = proposerRank;            
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
        String res = "";
        String className = getName();
        res += className.substring(className.lastIndexOf('.') + 1);
        res += "_" + cmd.getOptionValue("cost");
        if (cmd.hasOption("PBRounds")) res += "_" + cmd.getOptionValue("PBRounds");
        if (cmd.hasOption("LSSteps")) res += "_" + cmd.getOptionValue("LSSteps");
        if (cmd.hasOption("#Searches")) res += "_" + cmd.getOptionValue("#Searches");
        return res;
    }

    public static void main(String args[]) 
    {
        // Parse the command line
        Options options = new Options();

        Option size = new Option("n", "size", true, "size of instance");
        size.setRequired(true);
        options.addOption(size);

        Option pbRounds = new Option("pb", "PBRounds", true, "PowerBalance rounds(*n)");
        pbRounds.setRequired(false);
        options.addOption(pbRounds);

        Option ls_steps = new Option("ls", "LSSteps", true, "Maximum number of Local Search Steps");
        ls_steps.setRequired(false);
        options.addOption(ls_steps);

        Option searches = new Option("s", "#Searches", true, "Number of different searches (*2)");
        searches.setRequired(false);
        options.addOption(searches);

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
        int pb;
        if (cmd.hasOption("PBRounds")) pb = Integer.parseInt(cmd.getOptionValue("PBRounds"));
        else pb = (int) (Math.ceil(Math.log(n) * Math.log(n) / (Math.log(2) * Math.log(2) * 10)));
        int ls_max_step;
        if (cmd.hasOption("LSSteps")) ls_max_step = Integer.parseInt(cmd.getOptionValue("LSSteps"));
        else ls_max_step = (int) (Math.ceil(Math.log(n) * 10) / (Math.log(2)));
        int s_cnt;
        if (cmd.hasOption("#Searches")) s_cnt = Integer.parseInt(cmd.getOptionValue("#Searches"));
        else if (n == 1) s_cnt = 1;
        else s_cnt = (int) (Math.ceil(Math.log(n) * 2 / Math.log(2)));
        if (s_cnt < 1)
        {
            System.err.println("Error: Requested zero searches");
            System.exit(1);
        }

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
        
        Abstract_SM_Algorithm smp = new HybridMultiSearch(n, menFile, womenFile, pb*n, ls_max_step, s_cnt, opt_cost);
        Marriage matching = smp.match();
        Metrics smpMetrics;
        smpMetrics = new Metrics(smp, matching, getFinalName(cmd, opt_cost));
        if (v) smpMetrics.perform_checks();
        smpMetrics.printPerformance();
    }
}