package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;
import org.apache.commons.cli.*;

import gr.ntua.cslab.entities.Rotation;
import gr.ntua.cslab.entities.Rotations;
import gr.ntua.cslab.entities.Rotation_Poset;
import gr.ntua.cslab.entities.Agent;
import gr.ntua.cslab.entities.Marriage;
import gr.ntua.cslab.tools.Metrics;
import gr.ntua.cslab.tools.Permutations;

public class Approx extends Abstract_SM_Algorithm
{
    private double epsilon;

    public Approx(int n, String menFileName, String womenFileName, double eps)
    {
        super(n, menFileName, womenFileName);
        this.epsilon = eps;
    }

    // Constructor for when agents are available
    public Approx(int n, Agent[][] agents, double eps)
    {
        super(n, agents);
        this.epsilon = eps;
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();
        
        // Initialize
        // Compute Delta and decide the side that yields it
        int delta, side;
        Marriage starting_m;
        Abstract_SM_Algorithm maleOpt = new GS_MaleOpt(n, agents);
        Marriage maleOptMatching = maleOpt.match();
        int maleOptSEq = maleOptMatching.getSECost();
        Abstract_SM_Algorithm femaleOpt = new GS_FemaleOpt(n, agents);
        Marriage femaleOptMatching = femaleOpt.match();
        int femaleOptSEq = femaleOptMatching.getSECost();
        if (maleOptSEq <= femaleOptSEq) 
        {
            delta = maleOptSEq;
            side = 0;
            starting_m = maleOptMatching;
        }
        else 
        {
            delta = femaleOptSEq;
            side = 1;
            starting_m = femaleOptMatching;
        }

        // Compute the rotation poset
        Rotations rots = new Rotations(n, agents, maleOptMatching, femaleOptMatching);
        ArrayList<Rotation> rotations;
        if (side == 0) rotations = rots.men_rotations;
        else rotations = rots.women_rotations;
        Rotation_Poset poset = new Rotation_Poset(n, agents, side, rots, maleOptMatching, femaleOptMatching);
        List<Rotation> topological_sorting = poset.topSort();
        // Partition rotations into two sets
        List<Rotation> r_large = new ArrayList<Rotation>();
        List<Rotation> r_small = new ArrayList<Rotation>();        
        for (Rotation r : rotations)
        {
            if (rotation_weight(r, side) > 2 * epsilon * delta) r_large.add(r);
            else r_small.add(r);
        }
        // Determine the maximum number of large rotations that can be selected
        double large_max = (1.0 + epsilon) / (2.0 * epsilon);
        if (large_max > r_large.size()) large_max = r_large.size();

        // Start
        byte[] selected;
        boolean[] not_valid;
        List<Rotation> r, r_min, not_in_r, excluded, valid_small;
        Marriage m, result = starting_m;
        boolean done = false;
        for (int i = 0; i <= large_max; i++)
        {
            // In this iteration we select i large rotations
            selected = new byte[r_large.size()];
            for (int j = 0; j < r_large.size() - i; j++) selected[j] = 0;
            for (int j = r_large.size() - i; j < r_large.size(); j++) selected[j] = 1;
            do
            {
                // Select R
                r = new ArrayList<Rotation>();
                not_in_r = new ArrayList<Rotation>();
                for (int j = 0; j < r_large.size(); j++)
                    if (selected[j] == 1) 
                        r.add(r_large.get(j));
                    else
                        not_in_r.add(r_large.get(j));   

                // Step 3(a)
                // Eliminate the selected large rotations
                r_min = poset.must_eliminate(r);
                m = eliminate_rotations(r_min, starting_m, side, topological_sorting, rots);
                if (m.hasBetterSE(result)) result = m;
                if (m.getSECost() <= epsilon * delta)
                {
                    result = m;
                    done = true;
                    break;
                }

                // Step 3(b)
                // Find the remaining valid small rotations
                excluded = poset.cant_eliminate(not_in_r);   
                not_valid = new boolean[rots.count];
                for (Rotation ro : r_large) not_valid[ro.id] = true;
                for (Rotation ro : r_min) not_valid[ro.id] = true;
                for (Rotation ro : excluded) not_valid[ro.id] = true;

                // Step 3(c)
                // Try eliminating them one by one
                for (Rotation ro : topological_sorting)
                {
                    if (!not_valid[ro.id]) 
                    {
                        m = rots.eliminate(m, ro.id, side);
                        if (m.hasBetterSE(result)) result = m;
                        if (m.getSECost() <= epsilon * delta)
                        {
                            result = m;
                            done = true;
                            break;
                        }
                    }
                }
            } while ((selected = Permutations.nextPermutation(selected)) != null); 
            if (done) break;
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        return result;
    }

    // Given a starting marriage, eliminates a given list of rotations according to a given topological sorting and outputs the resulting marriage
    private Marriage eliminate_rotations(List<Rotation> rs, Marriage m, int side, List<Rotation> topological_sorting, Rotations rots)
    {
        boolean[] to_be_eliminated = new boolean[rots.count];
        Marriage res = m;
        for (Rotation r : rs) to_be_eliminated[r.id] = true;
        for (Rotation r : topological_sorting) 
            if (to_be_eliminated[r.id])
                res = rots.eliminate(res, r.id, side);
        return res;
    }

    private int rotation_weight(Rotation r, int side)
    {
        int res = 0;
        if (side == 0)
        {
            for (int i = 0; i < r.size; i++)
            {
                res += agents[0][r.men.get(i)].getRankOf(r.women.get(r.getPrevIndex(i))) - agents[1][r.women.get(r.getPrevIndex(i))].getRankOf(r.men.get(i));
                res -= agents[0][r.men.get(i)].getRankOf(r.women.get(i)) - agents[1][r.women.get(i)].getRankOf(r.men.get(i));
            }            
        }
        else
        {
            for (int i = 0; i < r.size; i++)
            {
                res += agents[1][r.women.get(i)].getRankOf(r.men.get(r.getPrevIndex(i))) - agents[0][r.men.get(r.getPrevIndex(i))].getRankOf(r.women.get(i));
                res -= agents[1][r.women.get(i)].getRankOf(r.men.get(i)) - agents[0][r.men.get(i)].getRankOf(r.women.get(i));
            } 
        }
        return res;
    }

    private static String getName()
    {
        String className = Thread.currentThread().getStackTrace()[2].getClassName(); 
        return className;
    }

    private static String getFinalName(String toAppend)
    {
        String className = Thread.currentThread().getStackTrace()[2].getClassName(); 
        return className.substring(className.lastIndexOf('.') + 1) + "_" + toAppend;
    }

    public static void main(String args[]) 
    {
        // Parse the command line
        Options options = new Options();

        Option size = new Option("n", "size", true, "size of instance");
        size.setRequired(true);
        options.addOption(size);

        Option eps = new Option("e", "epsilon", true, "approximation guarrantee epsilon");
        eps.setRequired(true);
        options.addOption(eps);

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
        double e;
        e = Double.parseDouble(cmd.getOptionValue("epsilon"));
        String menFile = cmd.getOptionValue("men");
        String womenFile = cmd.getOptionValue("women");
        boolean v;
        if (cmd.hasOption("verify")) v = true;
        else v = false;

        Abstract_SM_Algorithm smp = new Approx(n, menFile, womenFile, e);
        Marriage matching = smp.match();
        Metrics smpMetrics;
        smpMetrics = new Metrics(smp, matching, getFinalName(cmd.getOptionValue("epsilon")));
        if (v) smpMetrics.perform_checks();   
        smpMetrics.printPerformance();
    }
}