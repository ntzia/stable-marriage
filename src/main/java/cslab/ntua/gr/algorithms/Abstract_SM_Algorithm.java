package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.zip.*;
import java.nio.charset.Charset;

import gr.ntua.cslab.entities.Marriage;
import gr.ntua.cslab.entities.Agent;

public abstract class Abstract_SM_Algorithm
{
	protected int n;
	protected Agent[][] agents;
    protected long rounds;
    protected double time;

	public Abstract_SM_Algorithm(int n, Agent[][] agents)
    {
        this.n = n;
        this.rounds = 0;
        this.time = 0;
        this.agents = agents;
    }

    public Abstract_SM_Algorithm(int n, String menFileName, String womenFileName)
    {
    	this.n = n;
        rounds = 0;
        time = 0;

        agents = new Agent[2][n];

        BufferedReader br;
        ZipInputStream zipStream;
        FileReader fr;
        FileInputStream fis;
        BufferedInputStream bis;
        ZipInputStream zis;
        String sCurrentLine;
        StringBuilder builder;
        int i;

        
        if (menFileName == null)
        {
            for (i = 0; i < n; i++)
            {
                // New man
                agents[0][i] = new Agent(n, i, 0);
            }
        }
        else
        {
            if (menFileName.endsWith(".zip"))
            {
                br = null;
                zipStream = null;
                try 
                {
                    fis = new FileInputStream(menFileName);
                    bis = new BufferedInputStream(fis);
                    zipStream = new ZipInputStream(bis, Charset.forName("UTF-8"));
                    zipStream.getNextEntry();

                    br = new BufferedReader(new InputStreamReader(zipStream));
                    i = 0;
                    while ((sCurrentLine = br.readLine()) != null) 
                    {
                        agents[0][i] = new Agent(n, i, 0, sCurrentLine);
                        i++;
                    } 
                }
                catch (IOException e) 
                {
                    System.err.println("Caught IOException: " + e.getMessage());
                    System.exit(1);
                } 
                finally 
                {
                    try 
                    {
                        if (br != null) br.close();
                    }
                    catch (IOException ex) 
                    {
                        ex.printStackTrace();
                    }
                } 
            }
            else
            {
                br = null;
                fr = null;
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

        if (womenFileName == null)
        {
            for (i = 0; i < n; i++)
            {
                // New woman
                agents[1][i] = new Agent(n, i, 1);
            }
        }
        else
        {
            if (womenFileName.endsWith(".zip"))
            {
                br = null;
                zipStream = null;
                try 
                {
                    fis = new FileInputStream(womenFileName);
                    bis = new BufferedInputStream(fis);
                    zipStream = new ZipInputStream(bis, Charset.forName("UTF-8"));
                    zipStream.getNextEntry();

                    br = new BufferedReader(new InputStreamReader(zipStream));
                    i = 0;
                    while ((sCurrentLine = br.readLine()) != null) 
                    {
                        agents[1][i] = new Agent(n, i, 1, sCurrentLine);
                        i++;
                    }  
                }
                catch (IOException e) 
                {
                    System.err.println("Caught IOException: " + e.getMessage());
                    System.exit(1);
                } 
                finally 
                {
                    try 
                    {
                        if (br != null) br.close();
                    }
                    catch (IOException ex) 
                    {
                        ex.printStackTrace();
                    }
                } 
            }
            else
            {
                br = null;
                fr = null;
                try 
                {              
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
    }

    public abstract Marriage match();

    public Agent[][] getAgents(){ return agents; }
    public int getSize(){ return n; }
    public long getRounds(){ return rounds; }
    public double getTime(){ return time; }
}