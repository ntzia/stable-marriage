package cslab.ntua.gr.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import cslab.ntua.gr.entities.Agent;
import cslab.ntua.gr.entities.Flow_Network;
import cslab.ntua.gr.entities.Marriage;
import cslab.ntua.gr.entities.Rotation;
import cslab.ntua.gr.entities.Rotation_Poset;
import cslab.ntua.gr.entities.Rotations;
import cslab.ntua.gr.tools.Metrics;

public class MinEgalitarian extends Abstract_SM_Algorithm
{
    public MinEgalitarian(int n, String menFileName, String womenFileName)
    {
        super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public MinEgalitarian(int n, Agent[][] agents)
    {
        super(n, agents);
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
        Abstract_SM_Algorithm maleOpt = new GS_MaleOpt(n, agents);
        Marriage maleOptMatching = maleOpt.match();
        Abstract_SM_Algorithm femaleOpt = new GS_FemaleOpt(n, agents);
        Marriage femaleOptMatching = femaleOpt.match();

        // Compute the rotation poset
        Rotations rots = new Rotations(n, agents, maleOptMatching, femaleOptMatching);
        ArrayList<Rotation> rotations = rots.men_rotations;
        Rotation_Poset poset = new Rotation_Poset(agents, 0, rots, maleOptMatching, femaleOptMatching);
        List<Rotation> topological_sorting = poset.topSort();
        // Compute the weight of the rotations
        for (Rotation r : rotations) r.compute_rotation_weight(agents);
        // Construct the flow network and find the positive rotations of the min-cut
        Flow_Network g = new Flow_Network(poset);
        Set<Rotation> not_selected = g.minCut();
        // The solution includes all other positive rotations
        boolean[] dont_select = new boolean[rots.count];
        for (Rotation r : not_selected) dont_select[r.id] = true;
        List<Rotation> solution = new ArrayList<Rotation>();
        // Their predecessors have to be included as well
        for (Rotation r : rotations)
        {
            if (r.weight > 0 && !dont_select[r.id]) solution.add(r);
        }
        solution = poset.must_eliminate(solution);
        Marriage res = Rotations.eliminate_rotations(solution, maleOptMatching, 0, topological_sorting, rots);

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

        Abstract_SM_Algorithm smp = new MinEgalitarian(n, menFile, womenFile);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks();  
        smpMetrics.printPerformance();
    }
}