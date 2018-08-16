package gr.ntua.cslab.entities;

public class Marriage
{
    public int[][] mIndex;
    public int n;
    public int[] nextMen, nextWomen;
    public boolean nextMenComputed, nextWomenComputed;

    public Marriage(int n, int[][] mIndex) 
    {
        this.n = n;
        this.mIndex = mIndex;
        this.nextMen = new int[n];
        this.nextWomen = new int[n];
        this.nextMenComputed = false;
        this.nextWomenComputed = false;
    }

    // For cloning
    public Marriage(Marriage m) 
    {
        this.n = m.n;
        this.mIndex = new int[2][n];
        for (int i = 0; i < n; i++)
        {
            this.mIndex[0][i] = m.mIndex[0][i];
            this.mIndex[1][i] = m.mIndex[1][i];
        }
        this.nextMen = new int[n];
        this.nextWomen = new int[n];
        this.nextMenComputed = false;
        this.nextWomenComputed = false;
    }

    public boolean hasBetterECost(Marriage other)
    {
        if (other == null) return true;
        if (this.getECost() < other.getECost()) return true;
        else return false;
    }

    public boolean hasBetterRCost(Marriage other)
    {
        if (other == null) return true;
        if (this.getRCost() < other.getRCost()) return true;
        else return false;
    }

    public boolean hasBetterSE(Marriage other)
    {
        if (other == null) return true;
        if (this.getSECost() < other.getSECost()) return true;
        else return false;
    }

    public boolean hasBetterBal(Marriage other)
    {
        if (other == null) return true;
        if (this.getBal() < other.getBal()) return true;
        else return false;
    }

    public int getRCost()
    {
        int cost = 0;

        for (int i = 0; i < n; i++)
        {
            if (mIndex[0][i] > cost) cost = mIndex[0][i];
            if (mIndex[1][i] > cost) cost = mIndex[1][i];
        }
        return cost;
    }

    public int getECost()
    {
        int menCost = 0;
        int womenCost = 0;

        for (int i = 0; i < n; i++)
        {
            menCost += mIndex[0][i];
            womenCost += mIndex[1][i];
        }
        return menCost + womenCost;
    }

    public int getSECost()
    {
        int menCost = 0;
        int womenCost = 0;

        for (int i = 0; i < n; i++)
        {
            menCost += mIndex[0][i];
            womenCost += mIndex[1][i];
        }
        return Math.abs(menCost - womenCost);
    }

    public int getBal()
    {
        int menCost = 0;
        int womenCost = 0;

        for (int i = 0; i < n; i++)
        {
            menCost += mIndex[0][i];
            womenCost += mIndex[1][i];
        }
        if (menCost >= womenCost) return menCost;
        else return womenCost;
    }

    public int getMenCost()
    {
        int menCost = 0;

        for (int i = 0; i < n; i++)
        {
            menCost += mIndex[0][i];
        }
        return menCost;
    }

    public void findNextForMen(Agent[][] agents)
    {
        int s = -1, kappa;
        for (int i = 0; i < n; i++)
        {
            kappa = this.mIndex[0][i];
            kappa++;
            while (true)
            {
                if (kappa == n) break;
                s = agents[0][i].getAgentAt(kappa);
                if (agents[1][s].getRankOf(i) < this.mIndex[1][s]) break;
                kappa++;
            }
            if (s != -1) nextMen[i] = agents[1][s].getAgentAt(this.mIndex[1][s]);
            else nextMen[i] = n;
        }
        this.nextMenComputed = true;
    }

    public void findNextForWomen(Agent[][] agents)
    {
        int s = -1, kappa;
        for (int i = 0; i < n; i++)
        {
            kappa = this.mIndex[1][i];
            kappa++;
            while (true)
            {
                if (kappa == n) break;
                s = agents[1][i].getAgentAt(kappa);
                if (agents[0][s].getRankOf(i) < this.mIndex[0][s]) break;
                kappa++;
            }
            if (s != -1) nextWomen[i] = agents[0][s].getAgentAt(this.mIndex[0][s]);
            else nextWomen[i] = n;
        }
        this.nextWomenComputed = true;
    }

    public boolean nextMenComputed()
    {
        if (nextMenComputed) return true;
        else return false;
    }

    public boolean nextWomenComputed()
    {
        if (nextWomenComputed) return true;
        else return false;
    }

    public String marriageToStr(Agent[][] agents)
    {
        String s = "{ ";
        for (int i = 0; i < n; i++)
        {
            s += "(" + i + "," + agents[0][i].getAgentAt(this.mIndex[0][i]) + ") ";
        }
        s += "}";
        return s;
    }

    // Useful for running examples with 1-indexing
    public String marriageToStr2(Agent[][] agents)
    {
        String s = "(";
        for (int i = 0; i < n; i++)
        {
            s += (agents[0][i].getAgentAt(this.mIndex[0][i]) + 1) + " ";
        }
        s += ")";
        return s;
    }

    public boolean isEqualTo(Marriage m)
    {
        for (int i = 0; i < n; i++)
        {
            if (this.mIndex[0][i] != m.mIndex[0][i]) return false;
            if (this.mIndex[1][i] != m.mIndex[1][i]) return false;
        }
        return true;
    }
}