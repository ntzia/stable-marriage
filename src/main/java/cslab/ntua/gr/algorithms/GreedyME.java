package gr.ntua.cslab.algorithms;

import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import gr.ntua.cslab.Agent;
import gr.ntua.cslab.Metrics;

public class GreedyME extends Abstract_SM_Algorithm
{
    private int[][] kappa, lambda;
    private boolean married[][];
    private List<Integer> candidate_proposers;
    private ArrayList<ArrayList<LinkedList<Reminiscence>>> memory;
    private boolean remember;
    private int singles, curr_cost, curr_egal_cost;

    public GreedyME(int n, String menFileName, String womenFileName)
    {
        super(n, menFileName, womenFileName);

        kappa = new int[2][n];
        lambda = new int[2][n];
        married = new boolean[2][n];  
        for (int i = 0; i < n; i++)
        {
            married[0][i] = false;
            married[1][i] = false;
        }
        memory = new ArrayList<ArrayList<LinkedList<Reminiscence>>>(2);
        memory.add(new ArrayList<LinkedList<Reminiscence>>(n));
        memory.add(new ArrayList<LinkedList<Reminiscence>>(n));
        ArrayList<LinkedList<Reminiscence>> men_memory = memory.get(0);
        ArrayList<LinkedList<Reminiscence>> women_memory = memory.get(1);
        for (int i = 0; i < n; i++)
        {
            men_memory.add(new LinkedList<Reminiscence>());
            women_memory.add(new LinkedList<Reminiscence>());
        }
    }

    public int[] match()
    {
        long startTime = System.nanoTime();
        Move m;
        LinkedList<Reminiscence> rems;
        Reminiscence rem;
        boolean already_in_memory;
        int proposals = 0;
        remember = false;
        singles = 2 * n;
        curr_cost = n * n;
        curr_egal_cost = 0;
        int best_cost = curr_cost;
        candidate_proposers = new ArrayList<Integer>();
        for (int i = 0; i < 2*n; i++) candidate_proposers.add(i);

        int state_ecost;

        while (candidate_proposers.size() != 0)
        {
            // Save current state temporarily, because it will change during execute
            state_ecost = curr_egal_cost;

            m = pick_move();
            execute(m);
            // If new global minimum, reset memory
            if (m.cost < best_cost)
            {
                clear_mem();
                remember = false;
                best_cost = m.cost;
            } 
            // If the move does not progress, turn on remembering
            if (m.cost >= curr_cost) remember = true;
            // In remembering mode, save ALL the moves and as much info about the CURRENT(to avoid confusion) state as possible
            if (remember) 
            {
                if (m.times_executed != 0)
                {
                    rem = m.associated_reminescence;
                    rem.times_executed++;
                }
                else 
                {
                    rems = memory.get(m.pside).get(m.proposer);
                    rems.add(new Reminiscence(m.receiver, curr_cost, state_ecost));
                }
            }
            curr_cost = m.cost;
            // Rest of state metrics have updated in execute

            proposals++;
            if (proposals > n*n)
            {
                System.err.println("Enforcing Termination at CC = " + (n - (singles / 2)));
                if ((n - (singles / 2)) != (n - 1)) System.err.println("Stuck at CC!=n-1");
                finish_matching();
                break;
            }
        }

        long endTime = System.nanoTime();
        long elapsedTime = endTime - startTime;
        time = elapsedTime / 1.0E09;

        int[] matching = new int[n];
        for (int i = 0; i < n; i++) matching[i] = agents[0][i].getAgentAt(kappa[0][i]);

        return matching;
    }

    private void finish_matching()
    {
        int comp_side = pick_compromising_side();

        boolean stop;
        do
        {
            stop = true;
            for (int i = 0; i < n; i++)
            { 
                if (propose_noMotivated(i, comp_side)) stop = false;
            }
        }
        while (!stop);

        do
        {
            stop = true;
            for (int i = 0; i < n; i++)
            { 
                if (propose_noMotivated(i, flip(comp_side))) stop = false;
            }
        }
        while (!stop);    
    }

    private int pick_compromising_side()
    {
        int menCost = 0;
        int womenCost = 0;

        for (int i = 0; i < n; i++)
        {
            menCost += kappa[0][i];
            womenCost += kappa[1][i];
        }

        if (menCost >= womenCost) return 1;
        else return 0;
    }

    private Move pick_move()
    {
        int p, side, r, c, previous_spouse, idx, times_executed;  
        Move m, best_m;
        boolean move_repeated;
        LinkedList<Reminiscence> rems;
        Reminiscence associated_rem;
        
        find_lambdas();
        best_m = null;

        for (int i : candidate_proposers)
        {
            // Decode
            if (i < n) {p = i; side = 0;}
            else {p = i - n; side = 1;}

            for (idx = kappa[side][p]; idx <= lambda[side][p]; idx++)
            {
                // Corner case: still rejected by everyone
                if (idx == n) break;

                r = agents[side][p].getAgentAt(idx);
                if (agents[flip(side)][r].getRankOf(p) <= lambda[flip(side)][r])
                {
                    // Valid move
                    // Compute cost
                    if (married[flip(side)][r])
                    {
                        previous_spouse = agents[flip(side)][r].getAgentAt(kappa[flip(side)][r]);
                        c = curr_cost;
                        c -= Math.abs(agents[side][previous_spouse].getRankOf(r) - agents[flip(side)][r].getRankOf(previous_spouse));
                        c += Math.abs(agents[side][p].getRankOf(r) - agents[flip(side)][r].getRankOf(p));
                    }
                    else
                    {
                        c = curr_cost;
                        c -= n;
                        c += Math.abs(agents[side][p].getRankOf(r) - agents[flip(side)][r].getRankOf(p));
                    }

                    // Check in memory to see if this move has been repeated
                    times_executed = 0;
                    rems = memory.get(side).get(p);
                    associated_rem = null;
                    for (Reminiscence rem : rems)
                    {
                        if (rem.receiver == r && rem.cost == curr_cost && rem.e_cost == curr_egal_cost)
                        {
                            // Move in memory
                            times_executed = rem.times_executed;
                            associated_rem = rem;
                            break;
                        }
                    }

                    //if (move_repeated) System.out.println("Located repeated move: Receiver=" + r + " Cost=" + c + " Mem=" + memory.get(side).get(p));

                    m = new Move(p, side, r, c, times_executed, associated_rem);
                    if (m.isBetterThan(best_m)) best_m = m;
                }
            }   
        }
        //if (best_m.repeated && (n - (singles / 2)) != n - 1) System.out.println("Selected repeated move at CC = " + (n - (singles / 2)));
        return best_m;
    }

    private void find_lambdas()
    {
        int curr_index, a;
        boolean done;

        for (int p = 0; p < n; p++)
        {
            for (int side = 0; side < 2; side++)
            {
                curr_index = kappa[side][p];
                done = false;
                while (!done)
                {
                    a = agents[side][p].getAgentAt(curr_index);
                    if (kappa[flip(side)][a] >= agents[flip(side)][a].getRankOf(p))
                    {
                        // Proposal will be successful here
                        done = true;
                    }
                    else
                    {
                        curr_index++;
                        // Corner case: None accepts
                        if (curr_index == n) done = true;
                    }
                }
                lambda[side][p] = curr_index;
            }
        }       
    }

    private void execute(Move m)
    {
        // Engage proposer
        married[m.pside][m.proposer] = true;
        candidate_proposers.remove(Integer.valueOf(m.proposer + m.pside*n));

        if (married[m.rside][m.receiver])
        {
            // Receiver already engaged
            int old = agents[m.rside][m.receiver].getAgentAt(kappa[m.rside][m.receiver]);
            married[m.pside][old] = false;    
            candidate_proposers.add(old + m.pside*n);
        }
        else
        {
            // CC increase
            // Engage recever
            married[m.rside][m.receiver] = true;
            candidate_proposers.remove(Integer.valueOf(m.receiver + m.rside*n));
            singles -= 2;
        }

        // Update state
        curr_egal_cost -= (kappa[m.pside][m.proposer] + kappa[m.rside][m.receiver]);
        curr_egal_cost += (agents[m.pside][m.proposer].getRankOf(m.receiver) + agents[m.rside][m.receiver].getRankOf(m.proposer));

        // Fix kappas
        kappa[m.pside][m.proposer] = agents[m.pside][m.proposer].getRankOf(m.receiver);
        kappa[m.rside][m.receiver] = agents[m.rside][m.receiver].getRankOf(m.proposer);
    }

    private void clear_mem()
    {
        for (int i = 0; i < n; i++)
        {
            memory.get(0).get(i).clear();
            memory.get(1).get(i).clear();
        }
    }

    private boolean propose_noMotivated(int proposer, int proposerSide)
    {   
        //System.out.println("\nTime for Agent " + a.getID() + " of Side " + side + " to propose!");
        int proposeToIndex = kappa[proposerSide][proposer];

        if (!married[proposerSide][proposer] && proposeToIndex < n)
        {
            // Wants to propose
            int acceptor = agents[proposerSide][proposer].getAgentAt(proposeToIndex);
            //System.out.println("Agent " + a.getID() + " of Side " + side + " proposes to Agent " + b.getID());
            if (evaluate_noMotivated(acceptor, proposer, flip(proposerSide)))
            {
                //Engage with new
                married[proposerSide][proposer] = true;
            }
            else
            {
                // b rejected a
                kappa[proposerSide][proposer]++;
            }
            return true;
        }
        else
        {
            //System.out.println("Agent " + a.getID() + " of Side " + side + " skips turn: PIndx = " + a.getPIndx() + " , MIndx = " + a.getMIndx());
            return false;
        }
    }

    private boolean evaluate_noMotivated(int acceptor, int proposer, int acceptorSide)
    {
        int proposerRank = agents[acceptorSide][acceptor].getRankOf(proposer);
        int proposeToIndex = kappa[acceptorSide][acceptor];

        if (proposeToIndex >= proposerRank)
        {
            //System.out.println("Agent " + a.getID() + " of Side " + flip(sideB) + " accepts the proposal.");
            // Break up with old
            if (married[acceptorSide][acceptor])
            {
                int old = agents[acceptorSide][acceptor].getAgentAt(proposeToIndex);
                married[flip(acceptorSide)][old] = false;                
            }
            
            //Engage with new
            married[acceptorSide][acceptor] = true;

            // Boost confidence if needed
            if (proposeToIndex > proposerRank) 
                kappa[acceptorSide][acceptor] = proposerRank;
                // !!!! NOT +1 because evaluate condition is (proposeToIndex >= proposerRank)
                // +1 means i will accept if the guy at +1 proposes me
            
            return true;
        }
        else
        {
            //System.out.println("Agent " + a.getID() + " of Side " + flip(sideB) + " rejects the proposal.");
            return false;
        }
    }

    private int flip(int side)
    {
        return side^1;
    }

    private static String getName()
    {
        String className = Thread.currentThread().getStackTrace()[2].getClassName(); 
        return className;
    }

    private static void usage()
    {
        System.err.println("Proper Usage: java " + getName() + " n (MenFile WomenFile)");
        System.exit(1);
    }

    public static void main(String args[]) 
    {
        int n = 0;
        String menFile = null;
        String womenFile = null;

        if (args.length != 1 && args.length != 3) usage();

        try 
        {
            n = Integer.parseInt(args[0]);
        } 
        catch (Exception e) 
        {
            usage();
        }

        System.out.println("Size= " + n);

        if (args.length == 3)
        {
            menFile = args[1];
            womenFile = args[2];
        } 

        Abstract_SM_Algorithm smp = new GreedyME(n, menFile, womenFile);
        int[] matching = smp.match();
        Metrics smpMetrics = new Metrics(smp, matching, getName());
        smpMetrics.printPerformance();

/*
        if (!smpMetrics.checkPerfectMatching()) System.err.println("Error! Matching not perfect!");
        int bagents = smpMetrics.blockingAgents();
        if (bagents != 0) System.err.println("Error! Terminated with " + bagents + " blocking agents!");
*/ 
    }

    public class Move
    { 
        public int proposer, pside, receiver, rside;
        public int cost;
        public int times_executed;
        public Reminiscence associated_reminescence;

        public Move(int p, int proposing_side, int r, int c, int times_ex, Reminiscence rem) 
        { 
            this.proposer = p; 
            this.pside = proposing_side; 
            this.receiver = r; 
            this.rside = flip(proposing_side); 
            this.cost = c;
            this.times_executed = times_ex;
            this.associated_reminescence = rem;
        }

        private int flip(int side)
        {
            return side^1;
        }

        public boolean isBetterThan(Move other_move)
        {
            // Must check for null (anything is better than nothing)
            if (other_move == null) return true;

            // First compare repeatability
            if (this.times_executed < other_move.times_executed) return true;
            if (this.times_executed > other_move.times_executed) return false;

            // Equal regarding repeatability
            // Compare costs
            if (this.cost < other_move.cost) return true;
            else return false;
        }
/*
        @Override
        public String toString() 
        {
            return "(" + id + "," + rank + ")";
        }

        @Override
        public boolean equals(Object other)
        {
            if (other == null) return false;
            if (other == this) return true;
            if (!(other instanceof RankedAgent))return false;
            RankedAgent otherMyClass = (RankedAgent)other;
            if (otherMyClass.id == this.id) return true;
            else return false;
        }
*/
    }

    public class Reminiscence
    { 
        public int receiver, cost, e_cost;
        public int times_executed;

        public Reminiscence(int r, int c, int ecost) 
        {
            this.receiver = r; 
            this.cost = c;
            this.e_cost = ecost;
            this.times_executed = 1;
        }

/*
        @Override
        public boolean equals(Object other)
        {
            if (other == null) return false;
            if (other == this) return true;
            if (!(other instanceof Reminiscence)) return false;
            Reminiscence otherRem = (Reminiscence) other;
            //if (otherRem.receiver == this.receiver && otherRem.cost == this.cost && otherRem.egalitarian_cost == this.egalitarian_cost) return true;
            if (otherRem.receiver == this.receiver && otherRem.cost == this.cost) return true;
            else return false;
        }
*/
        @Override
        public String toString() 
        {
            return "(" + receiver + "," + cost + ")";
        }
    }
}