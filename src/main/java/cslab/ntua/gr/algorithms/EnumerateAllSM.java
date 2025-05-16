package cslab.ntua.gr.algorithms;

import java.util.ArrayList;
import java.util.List;

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
        Marriage maleOptMatching = maleOpt.match();
        Abstract_SM_Algorithm femaleOpt = new GS_FemaleOpt(n, agents);
        Marriage femaleOptMatching = femaleOpt.match();
        // Compute the rotation poset
        Rotations rots = new Rotations(n, agents, maleOptMatching, femaleOptMatching);
        ArrayList<Rotation> rotations = rots.men_rotations;
        Rotation_Poset poset = new Rotation_Poset(n, agents, 0, rots, maleOptMatching, femaleOptMatching);
        List<Rotation> topological_sorting = poset.topSort();

        // Enumerate all closed subsets of the rotation poset
        // Go through the rotations in topsort order
        // For each one, try both possibilities (add it or not)
        // If we do not add it, then call cant_eliminate to produce a list of disallowed rotations
        // Transform the list into a set so that at any point we have a set of disallowed rotations that we can check
        // This will be a recursive function that starts with i=0 and goes up to the number of rotations
        

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        return res;
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
