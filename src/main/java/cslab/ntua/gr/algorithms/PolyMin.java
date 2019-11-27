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

public class PolyMin extends Abstract_SM_Algorithm
{
    String cost_to_optimize;

    public PolyMin(int n, String menFileName, String womenFileName, String optCost)
    {
        super(n, menFileName, womenFileName);
        cost_to_optimize = optCost;
    }

    // Constructor for when agents are available
    public PolyMin(int n, Agent[][] agents, String optCost)
    {
        super(n, agents);
        cost_to_optimize = optCost;
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();
        
        Abstract_SM_Algorithm smp_egal = new MinEgalitarian(n, agents);
        Marriage matching_egal = smp_egal.match();

        Abstract_SM_Algorithm smp_regret = new MinRegret(n, agents);
        Marriage matching_regret = smp_regret.match();

        Marriage result = betterMarriage(matching_egal, matching_regret);

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        return result;
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

        Abstract_SM_Algorithm smp = new PolyMin(n, menFile, womenFile, opt_cost);
        Marriage matching = smp.match();
        Metrics smpMetrics;
        smpMetrics = new Metrics(smp, matching, getFinalName(opt_cost));
        if (v) smpMetrics.perform_checks();
        smpMetrics.printPerformance();
    }
}