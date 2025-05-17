package cslab.ntua.gr.algorithms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;
import java.util.HashSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Marriage;
import cslab.ntua.gr.entities.Rotation;
import cslab.ntua.gr.entities.Rotation_Poset;
import cslab.ntua.gr.entities.Rotations;
import cslab.ntua.gr.tools.Metrics;

public class EnumerateAllSM extends Abstract_SM_Algorithm
{
    private Rotation_Poset poset = null;
    private List<Rotation> topological_sorting = null;
    Marriage maleOptMatching = null;
    Marriage femaleOptMatching = null;
    Rotations rots = null;


    public EnumerateAllSM(int n, String menFileName, String womenFileName)
    {
        super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public EnumerateAllSM(int n, Agent[][] agents)
    {
        super(n, agents);
    }

    public Marriage match()
    {
        return null;
    }

    public List<Marriage> allStableMatchings()
    {
        long startTime = System.nanoTime();
        List<Marriage> res = new ArrayList<Marriage>();

        // Initialize
        Abstract_SM_Algorithm maleOpt = new GS_MaleOpt(n, agents);
        maleOptMatching = maleOpt.match();
        Abstract_SM_Algorithm femaleOpt = new GS_FemaleOpt(n, agents);
        femaleOptMatching = femaleOpt.match();
        // Compute the rotation poset
        rots = new Rotations(n, agents, maleOptMatching, femaleOptMatching);
        poset = new Rotation_Poset(n, agents, 0, rots, maleOptMatching, femaleOptMatching);
        topological_sorting = poset.topSort();

        // Enumerate all closed subsets of the rotation poset
        // Go through the rotations in topsort order
        // For each one, try both possibilities (add it or not)
        // If we do not add it, then call cant_eliminate to produce a list of disallowed rotations
        // Transform the list into a set so that at any point we have a set of disallowed rotations that we can check
        // This will be a recursive function that starts with i=0 and goes up to the number of rotations
        enumerate(0, new HashSet<Rotation>(), new LinkedList<Rotation>(), res);

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        return res;
    }

    private void enumerate(int i, HashSet<Rotation> disallowed_rotations, LinkedList<Rotation> chosen_rotations, List<Marriage> res)
    {
        // Base case
        if (i == topological_sorting.size())
        {
            res.add(Rotations.eliminate_rotations(chosen_rotations, maleOptMatching, 0, topological_sorting, rots));
            return;
        }

        Rotation r = topological_sorting.get(i);

        // Try adding the current rotation
        if (!disallowed_rotations.contains(topological_sorting.get(i))) 
        {  
            chosen_rotations.add(r);
            enumerate(i + 1, disallowed_rotations, chosen_rotations, res);
            chosen_rotations.removeLast();
        }

        // Try not adding the current rotation
        HashSet<Rotation> new_disallowed_rotations = new HashSet<Rotation>(disallowed_rotations);
        new_disallowed_rotations.add(r);
        new_disallowed_rotations.addAll(poset.cant_eliminate(Arrays.asList(r)));
        enumerate(i + 1, new_disallowed_rotations, chosen_rotations, res);
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

        EnumerateAllSM smp = new EnumerateAllSM(n, menFile, womenFile);
        for (Marriage matching : smp.allStableMatchings())
        {
            Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
            if (v) smpMetrics.perform_checks();  
            smpMetrics.printPerformance();
        }
    }
}
