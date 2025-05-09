package cslab.ntua.gr.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import cslab.ntua.gr.algorithms.Abstract_SM_Algorithm;
import cslab.ntua.gr.algorithms.GS_FemaleOpt;
import cslab.ntua.gr.algorithms.GS_MaleOpt;

public class Rotations
{
    private int n;
    Agent[][] agents;
    private int countM, countW;
    public int count;
    public ArrayList<Rotation> men_rotations, women_rotations;
    // each (men[i], women[i]) is a pair of this rotation
    // men_rotations benefit the women, placing men at lower indices

    // Use to instantiate without computing rotations
    public Rotations(int n, Agent[][] agents)
    {
        this.n = n;
        this.agents = agents;
        this.countM = 0;
        this.countW = 0;
        this.count = 0;
        this.men_rotations = new ArrayList<Rotation>();
        this.women_rotations = new ArrayList<Rotation>();
    }

    // Use to immediately compute all rotations
    // Matchings can be null if not yet available
    public Rotations(int n, Agent[][] agents, Marriage maleOptMatching, Marriage femaleOptMatching)
    {
        this.n = n;
        this.agents = agents;
    	this.countM = 0;
        this.countW = 0;
    	this.men_rotations = new ArrayList<Rotation>();
    	this.women_rotations = new ArrayList<Rotation>();
        find_all_rotations(maleOptMatching, femaleOptMatching);
        if (this.countM != this.countW) 
        {
            System.err.println("Error: Women rotations are not the same in number as men ones!");
            System.exit(1);
        }
        else this.count = this.countM;
    }

    // Given a starting marriage, eliminates a given list of rotations according to a given topological sorting and outputs the resulting marriage
    public static Marriage eliminate_rotations(List<Rotation> rs, Marriage m, int side, List<Rotation> topological_sorting, Rotations rots)
    {
        boolean[] to_be_eliminated = new boolean[rots.count];
        Marriage res = m;
        for (Rotation r : rs) to_be_eliminated[r.id] = true;
        for (Rotation r : topological_sorting) 
            if (to_be_eliminated[r.id])
                res = rots.eliminate(res, r.id, side);
        return res;
    }

    private void find_all_rotations(Marriage maleOptMatching, Marriage femaleOptMatching)
    {
        find_men_rotations(maleOptMatching, femaleOptMatching);
        // find_women_rotations(maleOptMatching, femaleOptMatching);
        find_women_rotations2();    // faster
    }

    // Arguments can be null if gs matchings are not yet available
    public void find_men_rotations(Marriage maleOptMatching, Marriage femaleOptMatching)
    {
        // Initialize
        int m, m1;
        this.countM = 0;
        if (maleOptMatching == null)
        {
            Abstract_SM_Algorithm maleOpt = new GS_MaleOpt(n, agents);
            maleOptMatching = maleOpt.match();            
        }
        if (femaleOptMatching == null)
        {
            Abstract_SM_Algorithm femaleOpt = new GS_FemaleOpt(n, agents);
            femaleOptMatching = femaleOpt.match();            
        }
        int[] final_index = new int[n];
        for (int i = 0; i < n; i++)
        { 
            final_index[i] = femaleOptMatching.mIndex[0][i];
        }
        Stack<Integer> s = new Stack<Integer>();
        Marriage current = maleOptMatching;
        boolean[] in_stack = new boolean[n];

        int i = 0;
        while (i < n)
        {
            if (s.empty())
            {
                while (current.mIndex[0][i] == final_index[i])
                {
                    i = i + 1;
                    if (i == n) break;
                } 
                if (i < n) 
                {
                    s.push(i);
                    in_stack[i] = true;
                }
            }
            else
            {
                m = s.peek();
                m = next(current, m, 0);
                while (!in_stack[m])
                {
                    s.push(m);
                    in_stack[m] = true;
                    m = next(current, m, 0);
                }
                m1 = s.pop();
                in_stack[m1] = false;
                Rotation rot = new Rotation(countM);
                rot.add_pair(m1, agents[0][m1].getAgentAt(current.mIndex[0][m1]));
                while (m != m1)
                {
                    m1 = s.pop();
                    in_stack[m1] = false;
                    rot.add_pair(m1, agents[0][m1].getAgentAt(current.mIndex[0][m1]));               
                }
                men_rotations.add(rot);
                countM++;
                current = eliminate(current, countM - 1, 0);
            }
        }
    }

    // Arguments can be null if gs matchings are not yet available
    public void find_women_rotations(Marriage maleOptMatching, Marriage femaleOptMatching)
    {
        // Initialize
        int w, w1;
        this.countW = 0;
        if (maleOptMatching == null)
        {
            Abstract_SM_Algorithm maleOpt = new GS_MaleOpt(n, agents);
            maleOptMatching = maleOpt.match();            
        }
        if (femaleOptMatching == null)
        {
            Abstract_SM_Algorithm femaleOpt = new GS_FemaleOpt(n, agents);
            femaleOptMatching = femaleOpt.match();            
        }
        int[] final_index = new int[n];
        for (int i = 0; i < n; i++)
        { 
            final_index[i] = maleOptMatching.mIndex[1][i];
        }
        Stack<Integer> s = new Stack<Integer>();
        Marriage current = femaleOptMatching;
        boolean[] in_stack = new boolean[n];

        int i = 0;
        while (i < n)
        {
            if (s.empty())
            {
                while (current.mIndex[1][i] == final_index[i])
                {
                    i = i + 1;
                    if (i == n) break;
                } 
                if (i < n) 
                {
                    s.push(i);
                    in_stack[i] = true;
                }
            }
            else
            {
                w = s.peek();
                w = next(current, w, 1);
                while (!in_stack[w])
                {
                    s.push(w);
                    in_stack[w] = true;
                    w = next(current, w, 1);
                }
                w1 = s.pop();
                in_stack[w1] = false;
                Rotation rot = new Rotation(countW);
                rot.add_pair(agents[1][w1].getAgentAt(current.mIndex[1][w1]), w1);
                while (w != w1)
                {
                    w1 = s.pop();
                    in_stack[w1] = false;
                    rot.add_pair(agents[1][w1].getAgentAt(current.mIndex[1][w1]), w1);           
                }
                women_rotations.add(rot);
                countW++;
                current = eliminate(current, countW - 1, 1);
            }
        }
    }

    // Can be used if men rotations are available - faster than find_women_rotations()
    public void find_women_rotations2()
    {
        if (!women_rotations.isEmpty())
        {
            System.err.println("Error: Women rotations already calculated, yet find_women_rotations2() was called!");
            System.exit(1);
        }
        Rotation women_rotation;
        int man, woman;
        for (Rotation men_rotation : men_rotations)
        {
            women_rotation = new Rotation(men_rotation.id);
            for (int i = 0; i < men_rotation.size; i++)
            {
                man = men_rotation.men.get(i);
                woman = men_rotation.women.get(men_rotation.getPrevIndex(i));
                women_rotation.add_pair(man, woman);   
            } 
            women_rotations.add(women_rotation);
        }
        countW = countM;
    }

    // if s(agent) is the first partner that prefers agent over its match in m (and is less preferred by agent than its match in m) 
    // then next returns the spouse of s(agent) in m
    // returns n if not existent
    private int next(Marriage m, int agent, int side)
    {
        int s;
        int kappa = m.mIndex[side][agent];
        kappa++;
        while (true)
        {
            if (kappa == n) return n;
            s = agents[side][agent].getAgentAt(kappa);
            if (agents[flip(side)][s].getRankOf(agent) < m.mIndex[flip(side)][s]) break;
            kappa++;
        }
        return agents[flip(side)][s].getAgentAt(m.mIndex[flip(side)][s]);
    }

    public Marriage eliminate(Marriage m, int rotation_no, int side)
    {
        int man, woman;
        Rotation r;
        Marriage res = new Marriage(m);
        if (side == 0) r = men_rotations.get(rotation_no);
        else r = women_rotations.get(rotation_no);

        if (side == 0)
        {
            for (int i = 0; i < r.size; i++)
            {
                man = r.men.get(i);
                woman = r.women.get(r.getPrevIndex(i));
                res.mIndex[0][man] = agents[0][man].getRankOf(woman);
                res.mIndex[1][woman] = agents[1][woman].getRankOf(man);
                // System.out.println("Man " + man + " marries woman " + woman);
            }            
        }
        else
        {
            for (int i = 0; i < r.size; i++)
            {
                woman = r.women.get(i);
                man = r.men.get(r.getPrevIndex(i));
                res.mIndex[0][man] = agents[0][man].getRankOf(woman);
                res.mIndex[1][woman] = agents[1][woman].getRankOf(man);
                // System.out.println("Man " + man + " marries woman " + woman);
            }            
        }
        return res;
    }

    public boolean isExposed(Marriage m, int rotation_no, int side)
    {
        Rotation r;
        if (side == 0) r = men_rotations.get(rotation_no);
        else r = women_rotations.get(rotation_no);
        if (r.isExposed(agents, m, side)) return true;
        else return false;
    }

    private int flip(int side)
    {
        return side^1;
    }
}
