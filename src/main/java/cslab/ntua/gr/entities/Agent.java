package gr.ntua.cslab.entities;

import java.io.*;
import java.util.*;
import java.util.Collections;

public class Agent
{
    private int n, side, id;
    private List<Integer> prefs;
    private int[] inversePrefs;

    // Creates a copy of an existing agent
    public Agent(Agent copy)
    {
        this.n = copy.getN();
        this.id = copy.getID();
        this.side = copy.getSide();

        prefs = new ArrayList<Integer>();
        List<Integer> old_prefs = copy.getPrefList();
        for (int j = 0; j < n; j++) prefs.add(old_prefs.get(j));

        inversePrefs = new int[n];
        int[] old_inversePrefs = copy.getInvPrefs();
        for (int j = 0; j < n; j++) inversePrefs[j] = old_inversePrefs[j];
    }

    // Creates an agent with random preferences (uniform)
    public Agent(int n, int id, int side)
    {
        this.n = n;
        this.id = id;
        this.side = side;

        prefs = new ArrayList<Integer>();
        for (int j = 0; j < n; j++) prefs.add(j);
        java.util.Collections.shuffle(prefs);

        inversePrefs = new int[n];
        for (int j = 0; j < n; j++) inversePrefs[prefs.get(j)] = j;
    }

    // Creates an agent with preferences read from an input file
    public Agent(int n, int id, int side, String lineWithPrefs)
    {
        this.n = n;
        this.id = id;
        this.side = side;

        prefs = new ArrayList<Integer>();
        String[] tokens = lineWithPrefs.split("\\s+");
        for (int j = 0; j < n; j++) prefs.add(Integer.parseInt(tokens[j]));

        inversePrefs = new int[n];
        for (int j = 0; j < n; j++) inversePrefs[prefs.get(j)] = j;
    }

    public int getAgentAt(int index)
    {
        return prefs.get(index);
    }

    public int getRankOf(int agentNo)
    {
        return inversePrefs[agentNo];
    }

    public boolean cmp(int a, int b)
    {
        if (inversePrefs[a] < inversePrefs[b]) return true;
        else return false;
    }

    public boolean prefers_first(Integer a, int b)
    {
        if (a == null) return false;
        if (b == Integer.MAX_VALUE) return true;

        if (inversePrefs[Integer.valueOf(a)] < inversePrefs[b]) return true;
        else return false;        
    }

    public int getN(){ return n; }
    public int getID(){ return id; }
    public int getSide(){ return side; }
    public List<Integer> getPrefList(){ return prefs; }
    public int[] getInvPrefs(){ return inversePrefs; }
}
