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

public class ESMA extends Abstract_SM_Algorithm
{
    private int[][] kappa, married;

    public ESMA(int n, String menFileName, String womenFileName)
    {
    	super(n, menFileName, womenFileName);
    }

    // Constructor for when agents are available
    public ESMA(int n, Agent[][] agents)
    {
        super(n, agents);
    }

    public Marriage match()
    {
        long startTime = System.nanoTime();

        // Initialize
        kappa = new int[2][n];
        married = new int[2][n];  
        for (int i = 0; i < n; i++)
        {
            married[0][i] = Integer.MAX_VALUE;
            married[1][i] = Integer.MAX_VALUE;
        } 
    	int side;

        // Propose     
    	while (!terminate())
    	{
    		rounds++;
    		side = pickProposers(rounds);
    		for (int i = 0; i < n; i++) propose(i, side);
    	}

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        Marriage result = new Marriage(n, married);
        return result;
    }

    private boolean terminate()
    {
    	for (int i = 0; i < n; i++)
    	{
    		if (kappa[0][i] < married[0][i]) return false;
    		if (kappa[1][i] < married[1][i]) return false;
    	}
		return true;
    }

    private int pickProposers(long r)
    {
    	if ((Math.sin(r*r)>0)) return 0;
        else return 1;
    }

    private void propose(int proposer, int proposerSide)
    {
        int proposeToIndex = kappa[proposerSide][proposer];
        int marriedToIndex = married[proposerSide][proposer];
    	if (proposeToIndex < marriedToIndex && proposeToIndex < n)
    	{
            // Wants to propose
    		int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
    		if (evaluate(acceptor, proposer, flip(proposerSide)))
    		{
    			// Break up with old
    			if (marriedToIndex != Integer.MAX_VALUE)
    			{
    				int old = agents[proposerSide][proposer].getAgentAt(marriedToIndex);
    				married[flip(proposerSide)][old] = Integer.MAX_VALUE;		
    			}
    			//Engage with new
    			married[proposerSide][proposer] = proposeToIndex;
    		}
    		else kappa[proposerSide][proposer]++;
    	}
    }

    private boolean evaluate(int acceptor, int proposer, int acceptorSide)
    {
        int proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
        int marriedToIndex = married[acceptorSide][acceptor];
    	if (marriedToIndex > proposerRank)
    	{
    		// Break up with old
    		if (marriedToIndex != Integer.MAX_VALUE)
    		{
                int old = agents[acceptorSide][acceptor].getAgentAt(marriedToIndex);
                married[flip(acceptorSide)][old] = Integer.MAX_VALUE;   				
    		}
    		//Engage with new
    		married[acceptorSide][acceptor] = proposerRank;
            // Boost confidence if needed
    		if (kappa[acceptorSide][acceptor] > proposerRank) kappa[acceptorSide][acceptor] = proposerRank + 1;            
    		return true;
    	}
    	else return false;
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

        Abstract_SM_Algorithm smp = new ESMA(n, menFile, womenFile);
        Marriage matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getFinalName());
        if (v) smpMetrics.perform_checks();
        smpMetrics.printPerformance();
    }
}
