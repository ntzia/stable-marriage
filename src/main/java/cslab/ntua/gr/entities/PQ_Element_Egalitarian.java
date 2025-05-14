package cslab.ntua.gr.entities;

import java.util.BitSet;

public class PQ_Element_Egalitarian implements Comparable<PQ_Element_Egalitarian> {
    public boolean[] solution_bitset;
    public int last_deviation_index;
    public Marriage solution;
    public int egalitarian_cost;

    public PQ_Element_Egalitarian(boolean[] solution_bitset, int last_deviation_index, Marriage solution, int egalitarian_cost) {
        this.solution_bitset = solution_bitset;
        this.last_deviation_index = last_deviation_index;
        this.solution = solution;
        this.egalitarian_cost = egalitarian_cost;
    }

    @Override
    public int compareTo(PQ_Element_Egalitarian o) {
        if (this.egalitarian_cost < o.egalitarian_cost)
            return -1;
        else if (this.egalitarian_cost > o.egalitarian_cost)
            return 1;
        else
            return 0;
    }

}
