package cslab.ntua.gr.tools;

import java.util.Comparator;

import cslab.ntua.gr.entities.Agent;

public class AgentsComparator implements Comparator<Integer>
{
    private Agent ag;

    public AgentsComparator(Agent a)
    {
        this.ag = a;
    }

    @Override
    public int compare(Integer a1, Integer a2) 
    {
        if (ag.getRankOf(a1) < ag.getRankOf(a2)) return -1;
        else if (ag.getRankOf(a1) > ag.getRankOf(a2)) return 1;
        else return 0;
    }
}