package gr.ntua.cslab.entities;

import java.util.*;

public class Rotation
{
    public int size, id;
    public ArrayList<Integer> men, women;
    // {   (m1,w1)   ,   (m2,w2)   ,   ...}
    // each pair (men[i], women[i]) is a pair of this rotation
    // if it is a rotation for men (bad for men - good for women) men[i] will marry women[i-1] when rotating
    // if it is a rotation for women (bad for women - good for men) women[i] will marry men[i-1] when rotating

    public Rotation(int id)
    {
        this.size = 0;
        this.id = id;
        this.men = new ArrayList<Integer>();
        this.women = new ArrayList<Integer>();
    }

    public void add_pair(int man, int woman)
    {
        men.add(man);
        women.add(woman);
        size++;
    }

    public boolean isExposed(Agent[][] agents, Marriage m, int side)
    {
        // Need to know next() for this
        if (side == 0 && !m.nextMenComputed()) m.findNextForMen(agents);
        if (side == 1 && !m.nextWomenComputed()) m.findNextForWomen(agents);
        
        int man, woman, nextMan, nextWoman;
        for (int i = 0; i < size; i++)
        {
            man = men.get(i);
            woman = women.get(i);
            if (agents[0][man].getAgentAt(m.mIndex[0][man]) != woman) return false;
            if (side == 0)
            {
                nextMan = m.nextMen[man];
                if (nextMan != men.get(getPrevIndex(i))) return false;                
            }
            else
            {
                nextWoman = m.nextWomen[woman];
                if (nextWoman != women.get(getPrevIndex(i))) return false;                    
            }
        }
        return true;
    }

    public int getNextIndex(int index)
    {
        if (index == size - 1) return 0;
        return index + 1;
    }

    public int getPrevIndex(int index)
    {
        if (index == 0) return size - 1;
        return index - 1;
    }

    public boolean contains_woman(int w)
    {
        return women.contains(w);
    }

    @Override
    public boolean equals(Object obj) 
    {
        if (obj == null) return false;
        if (!Rotation.class.isAssignableFrom(obj.getClass())) return false;
        Rotation other = (Rotation) obj;
        if (this.size != other.size) return false;

        int first_man = this.men.get(0);
        int index_in_other = other.men.indexOf(first_man);
        if (index_in_other == -1) return false;
        for (int i = 0; i < size; i++)
        {
            if (!this.men.get(i).equals(other.men.get(index_in_other))) return false;
            if (!this.women.get(i).equals(other.women.get(index_in_other))) return false;
            index_in_other = getNextIndex(index_in_other);
        }
        return true;
    }

    public String toString2()
    {
        String s = "{";
        for (int i = 0; i < size; i++)
        {
            s += "(" + men.get(i) + "," + women.get(i) + ")";
        }
        s += "}";
        return s;
    }

    @Override
    public String toString()
    {
        String s = Integer.toString(id) + ":{";
        for (int i = 0; i < size; i++)
        {
            s += "(" + men.get(i) + "," + women.get(i) + ")";
        }
        s += "}";
        return s;
    }

    @Override
    public int hashCode() 
    {
        return id;
    }
}