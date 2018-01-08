package gr.ntua.cslab.algorithms;

import java.io.*;

import gr.ntua.cslab.Agent;
import gr.ntua.cslab.Metrics;

public abstract class Abstract_SM_Algorithm
{
	protected int n;
	protected Agent[][] agents;
    protected int rounds;
    protected double time;

public Abstract_SM_Algorithm(int n, Agent[][] agents){}

    public Abstract_SM_Algorithm(int n, String menFileName, String womenFileName)
    {
    	this.n = n;
        rounds = 0;
        time = 0;

        agents = new Agent[2][n];
        if (menFileName == null)
        {
            for (int i = 0; i < n; i++)
            {
                // New man
                agents[0][i] = new Agent(n, i, 0);
                // New woman
                agents[1][i] = new Agent(n, i, 1);
            }
        }
        else
        {
            BufferedReader br = null;
            FileReader fr = null;
            String sCurrentLine;
            StringBuilder builder;
            int i;

            try 
            {
                // Read men file
                fr = new FileReader(menFileName);
                br = new BufferedReader(fr);

                i = 0;
                while ((sCurrentLine = br.readLine()) != null) 
                {
                    agents[0][i] = new Agent(n, i, 0, sCurrentLine);
                    i++;
                }

                // Read women file
                fr = new FileReader(womenFileName);
                br = new BufferedReader(fr);

                i = 0;
                while ((sCurrentLine = br.readLine()) != null) 
                {
                    agents[1][i] = new Agent(n, i, 1, sCurrentLine);
                    i++;
                }
                
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            } 
            finally 
            {
                try 
                {
                    if (br != null) br.close();
                    if (fr != null) fr.close();

                }
                catch (IOException ex) 
                {
                    ex.printStackTrace();
                }
            }
        }
    }

    public abstract int[] match();

    public Agent[][] getAgents(){ return agents; }
    public int getSize(){ return n; }
    public int getRounds(){ return rounds; }
    public double getTime(){ return time; }
}