package cslab.ntua.gr.algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.Arrays;

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
import cslab.ntua.gr.entities.PQ_Element_Egalitarian;
import cslab.ntua.gr.entities.Rotation;
import cslab.ntua.gr.entities.Rotation_Poset;
import cslab.ntua.gr.entities.Rotations;
import cslab.ntua.gr.tools.Metrics;

public class TopkEgalitarian extends Abstract_SM_Algorithm{

    int k = 0;
    PriorityQueue<PQ_Element_Egalitarian> pq = new PriorityQueue<PQ_Element_Egalitarian>();
    PQ_Element_Egalitarian last_returned = null;
    int rots_cnt = -1;
    Marriage maleOptMatching = null;
    Marriage femaleOptMatching = null;
    Rotations rots = null;
    Rotation_Poset poset = null;
    ArrayList<Rotation> rotations_topsort = null;

    public TopkEgalitarian(int n, String menFileName, String womenFileName, int k)
    {
        super(n, menFileName, womenFileName);
        this.k = k;
    }

    // Constructor for when agents are available
    public TopkEgalitarian(int n, Agent[][] agents, int k)
    {
        super(n, agents);
        this.k = k;
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
        Abstract_SM_Algorithm maleOpt = new GS_MaleOpt(n, agents);
        maleOptMatching = maleOpt.match();
        Abstract_SM_Algorithm femaleOpt = new GS_FemaleOpt(n, agents);
        femaleOptMatching = femaleOpt.match();

        // Compute the rotation poset
        rots = new Rotations(n, agents, maleOptMatching, femaleOptMatching);
        this.rots_cnt = rots.count;
        poset = new Rotation_Poset(n, agents, 0, rots, maleOptMatching, femaleOptMatching);
        rotations_topsort = poset.topSort();
        // Rename all indexes and data structures for rotations so that they are consistent with the topological sort
        poset.rename_ids(rotations_topsort);
        // Compute the weight of the rotations
        for (Rotation r : rotations_topsort) r.compute_rotation_weight(agents);
        // Construct the flow network and find the positive rotations of the min-cut
        Flow_Network g = new Flow_Network(rotations_topsort, poset);
        List<Rotation> not_selected = g.minCut();
        // The solution includes all other positive rotations
        boolean[] dont_select = new boolean[rots_cnt];
        for (Rotation r : not_selected) dont_select[r.id] = true;
        List<Rotation> solution = new ArrayList<Rotation>();
        
        // Their predecessors have to be included as well
        for (Rotation r : rotations_topsort)
            if (r.weight > 0 && !dont_select[r.id]) 
                solution.add(r);
        solution = poset.must_eliminate(solution);

        boolean[] solution_bits = new boolean[rots_cnt];
        for (Rotation r : solution)
                solution_bits[r.id] = true;

        System.out.println("First solution: " + Arrays.toString(solution_bits));

        Marriage res = Rotations.eliminate_rotations(solution, maleOptMatching, 0, rotations_topsort, rots);

        last_returned = new PQ_Element_Egalitarian(solution_bits, -1, res, res.getECost());

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        return res;
    }

    public Marriage get_next_match()
    {
        long startTime = System.nanoTime();

        // Insert new candidates to the PQ as deviations from the last solution
        for (int i = last_returned.last_deviation_index; i < rots_cnt - 1; i++)
        {
            // Create successor constraints
            boolean[] new_solution_bits = new boolean[rots_cnt];
            for (int j = 0; j < i; j++) new_solution_bits[j] = last_returned.solution_bitset[j];
            new_solution_bits[i + 1] = !last_returned.solution_bitset[i + 1];
            // From constraints, generate modified poset, and its optimal solution
            // To construct the new poset, pass the constraints array as a subarray (sublist) from 0 to i+1
            
            Rotation_Poset new_poset = poset.modify_poset(IntStream.range(0, i + 1).mapToObj(j -> new_solution_bits[j]).collect(Collectors.toList()));
            // TODO: possible optimization: save the poset in the PQ and start the modification for successors from that
            if (new_poset == null) continue;

            // Construct the flow network and find the positive rotations of the min-cut
            Flow_Network g = new Flow_Network(rotations_topsort, new_poset);
            List<Rotation> not_selected = g.minCut();
            // The solution includes all other positive rotations (that are unbound)
            boolean[] dont_select = new boolean[rots_cnt];
            for (Rotation r : not_selected) dont_select[r.id] = true;
            List<Rotation> new_solution = new ArrayList<Rotation>();
            for (Rotation r : rotations_topsort)
                if (r.weight > 0 && !dont_select[r.id] && new_poset.constrained_rotations[r.id] == 2) 
                    new_solution.add(r);
            // Their predecessors have to be included as well
            new_solution = new_poset.must_eliminate(new_solution);
            // Finally, add the rotations that are included by constraint
            for (Rotation r : rotations_topsort)
                if (new_poset.constrained_rotations[r.id] == 1) 
                    new_solution.add(r);

            for (Rotation r : new_solution)
                new_solution_bits[r.id] = true;

            System.out.println("New solution bits: " + Arrays.toString(new_solution_bits));

                
            Marriage new_mar = Rotations.eliminate_rotations(new_solution, maleOptMatching, 0, rotations_topsort, rots);

            pq.add(new PQ_Element_Egalitarian(new_solution_bits, i + 1, new_mar, new_mar.getECost()));
            
        }

        last_returned = pq.poll();
        
        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        if (last_returned == null) return null;
        else return last_returned.solution;
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

        Option k_option = new Option("k", "kValue", true, "number of top k egalitarian marriages");
        k_option.setRequired(false);
        options.addOption(k_option);

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
        int k = cmd.hasOption("kValue") ? Integer.parseInt(cmd.getOptionValue("kValue")) : 1;
        if (k < 1) k = 1;
        boolean v;
        if (cmd.hasOption("verify")) v = true;
        else v = false;

        TopkEgalitarian smp = new TopkEgalitarian(n, menFile, womenFile, k);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks();  
        smpMetrics.printPerformance();

        for (int i = 2; i <= k; i++) 
        {
            matching = smp.get_next_match();
            if (matching == null) break;
            smpMetrics = new Metrics(smp, matching, getFinalName());
            if (v) smpMetrics.perform_checks();  
            smpMetrics.printPerformance();
        }

        // Try the existing MinEgalitarian algorithm
        System.out.println("Running MinEgalitarian...");
        Abstract_SM_Algorithm smp_eg = new MinEgalitarian(n, menFile, womenFile);
        Marriage matching_eg = smp_eg.match();
        Metrics smpEgMetrics = new Metrics(smp_eg, matching_eg, getFinalName());
        if (v) smpEgMetrics.perform_checks();  
        smpEgMetrics.printPerformance();
    }

}
