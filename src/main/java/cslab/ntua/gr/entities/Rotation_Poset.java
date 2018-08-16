package gr.ntua.cslab.entities;

import java.util.*;

import gr.ntua.cslab.algorithms.Abstract_SM_Algorithm;
import gr.ntua.cslab.algorithms.GS_MaleOpt;
import gr.ntua.cslab.algorithms.GS_FemaleOpt;

public class Rotation_Poset
{
    private int n, count;
    private Agent[][] agents;
    private ArrayList<Rotation> rotations;
    private Map<Rotation,List<Rotation>> neighbors, neighbors_reversed;

    // Use to immediately construct the digraph
    // side = 0 constructs the poset of men rotations (that worsen the men)
    // Matchings can be null if not yet available
    public Rotation_Poset(int n, Agent[][] agents, int side, Rotations rotations, Marriage maleOptMatching, Marriage femaleOptMatching)
    {
        this.n = n;
        this.agents = agents;
        if (side == 0)
        {
            this.rotations = rotations.men_rotations;
            construct_digraph_men(maleOptMatching, femaleOptMatching);
        } 
        else 
        {
            this.rotations = rotations.women_rotations;
            construct_digraph_women(maleOptMatching, femaleOptMatching);
        }
        this.count = rotations.count;
        // Possibly hyper-graph, so remove duplicate edges (also removes self-loops)
        remove_duplicates();
        this.neighbors_reversed = reverse_graph();
    }

    private void construct_digraph_men(Marriage maleOptMatching, Marriage femaleOptMatching)
    {
        int man, woman, man1, next_mate_of_woman, index, latest_type1_label;

        // Initialize
        neighbors = new HashMap<Rotation,List<Rotation>>();
        int[][] labels = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) 
                labels[i][j] = -1;
        char[][] label_types = new char[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) 
                label_types[i][j] = 'x';
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

        // Labels
        for (Rotation r : rotations)
        {
            for (int i = 0; i < r.size; i++)
            {
                man = r.men.get(i);
                woman = r.women.get(i);
                index = agents[0][man].getRankOf(woman);
                labels[man][index] = r.id;
                label_types[man][index] = '1';

                next_mate_of_woman = r.men.get(r.getNextIndex(i));
                for (int j = agents[1][woman].getRankOf(man) - 1; j > agents[1][woman].getRankOf(next_mate_of_woman); j--)
                {
                    man1 = agents[1][woman].getAgentAt(j);
                    index = agents[0][man1].getRankOf(woman);
                    labels[man1][index] = r.id;
                    label_types[man1][index] = '2';
                }
            } 
        }
        // Nodes
        for (Rotation r : rotations) add(r);
        // Edges
        for (int i = 0; i < n; i++)
        {
            latest_type1_label = -1;
            for (int j = maleOptMatching.mIndex[0][i]; j <= femaleOptMatching.mIndex[0][i]; j++)
            {
                if (label_types[i][j] == '1') 
                {
                    if (latest_type1_label != -1) add(rotations.get(latest_type1_label), rotations.get(labels[i][j]));
                    latest_type1_label = labels[i][j];
                }
                else if (label_types[i][j] == '2') add(rotations.get(labels[i][j]), rotations.get(latest_type1_label));
            }
        }
        return;
    }

    private void construct_digraph_women(Marriage maleOptMatching, Marriage femaleOptMatching)
    {
        int man, woman, woman1, next_mate_of_man, index, latest_type1_label;

        // Initialize
        neighbors = new HashMap<Rotation,List<Rotation>>();
        int[][] labels = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) 
                labels[i][j] = -1;
        char[][] label_types = new char[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) 
                label_types[i][j] = 'x';
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

        // Labels
        for (Rotation r : rotations)
        {
            for (int i = 0; i < r.size; i++)
            {
                man = r.men.get(i);
                woman = r.women.get(i);
                index = agents[1][woman].getRankOf(man);
                labels[woman][index] = r.id;
                label_types[woman][index] = '1';

                next_mate_of_man = r.women.get(r.getNextIndex(i));
                for (int j = agents[0][man].getRankOf(woman) - 1; j > agents[0][man].getRankOf(next_mate_of_man); j--)
                {
                    woman1 = agents[0][man].getAgentAt(j);
                    index = agents[1][woman1].getRankOf(man);
                    labels[woman1][index] = r.id;
                    label_types[woman1][index] = '2';
                }
            } 
        }
        // Nodes
        for (Rotation r : rotations) add(r);
        // Edges
        for (int i = 0; i < n; i++)
        {
            latest_type1_label = -1;
            for (int j = femaleOptMatching.mIndex[1][i]; j <= maleOptMatching.mIndex[1][i]; j++)
            {
                if (label_types[i][j] == '1') 
                {
                    if (latest_type1_label != -1) add(rotations.get(latest_type1_label), rotations.get(labels[i][j]));
                    latest_type1_label = labels[i][j];
                }
                else if (label_types[i][j] == '2') add(rotations.get(labels[i][j]), rotations.get(latest_type1_label));
            }
        }
        return;
    }

    private void remove_duplicates()
    {
        // Carefully remove dupliate edges (and self-loops), so that time per edge is constant (edges are O(n^2))
        // Cant initialize from scratch seen array to false (that would be O(n^2) per node)
        // Use a stack to remember which nodes have been marked - only those have to be reverted to false after each node visit
        boolean[] seen = new boolean[count];
        List<Rotation> edges;
        Stack<Integer> used;
        Iterator<Rotation> it;
        Rotation edge;

        for (Rotation r : rotations)
        {
            edges = neighbors.get(r);
            used = new Stack<Integer>();

            // To detect self-loops
            seen[r.id] = true;
            used.push(r.id);

            // Scan neighbors
            it = edges.iterator();
            while (it.hasNext()) 
            {
                edge = it.next();
                if (seen[edge.id]) it.remove();
                seen[edge.id] = true;
                used.push(edge.id);
            }
            // Now restore false values to seen array
            while (!used.isEmpty()) seen[used.pop()] = false;
        }
    }

    private Map<Rotation,List<Rotation>> reverse_graph()
    {
        List<Rotation> edges;
        Map<Rotation,List<Rotation>> reversed = new HashMap<Rotation,List<Rotation>>();
        // Nodes
        for (Rotation r: neighbors.keySet()) reversed.put(r, new LinkedList<Rotation>());
        // Edges
        for (Rotation src: neighbors.keySet())
        {
            edges = neighbors.get(src);
            for (Rotation dest : edges) reversed.get(dest).add(src);
        }    
        return reversed;
    }

    /**
     * Recursive function for dfs search
     */
    private void dfs(Map<Rotation,List<Rotation>> dg, Rotation node, boolean[] visited)
    {
        if (visited[node.id]) return;
        visited[node.id] = true;
        for (Rotation neighbor : dg.get(node)) dfs(dg, neighbor, visited);
    }

    /**
     * Performs a dfs search and returns all visited nodes as a list
     */
    private List<Rotation> do_dfs(Map<Rotation,List<Rotation>> dg, List<Rotation> starting_nodes)
    {
        boolean[] visited = new boolean[count];
        for (Rotation starting_node : starting_nodes) dfs(dg, starting_node, visited);
        List<Rotation> res = new ArrayList<Rotation>();
        for (int i = 0; i < count; i++)
        {
            if (visited[i]) res.add(rotations.get(i));
        }
        return res;
    }

    /**
     * Given a chosen list of rotations that will not be eliminated, 
     * outputs all the rotations that must not be eliminated
     */
    public List<Rotation> cant_eliminate(List<Rotation> r)
    {
        return do_dfs(this.neighbors, r);
    }

    /**
     * Given a chosen list of rotations that will be eliminated, 
     * outputs all the rotations that must be eliminated as well
     */
    public List<Rotation> must_eliminate(List<Rotation> r)
    {
        return do_dfs(this.neighbors_reversed, r);
    }
    
    /**
     * Add a vertex to the graph.  Nothing happens if vertex is already in graph.
     */
    public void add(Rotation vertex) 
    {
        neighbors.put(vertex, new LinkedList<Rotation>());
    }
    
    /**
     * True iff graph contains vertex.
     */
    public boolean contains(Rotation vertex) 
    {
        return neighbors.containsKey(vertex);
    }
    
    /**
     * Add an edge to the graph; if either vertex does not exist, it's added.
     * This implementation allows the creation of multi-edges and self-loops.
     */
    public void add(Rotation from, Rotation to) 
    {
        neighbors.get(from).add(to);
    }

    /**
     * Report (as a Map) the in-degree of each vertex.
     */
    public Map<Rotation,Integer> inDegree() 
    {
        Map<Rotation,Integer> result = new HashMap<Rotation,Integer>();
        for (Rotation v: neighbors.keySet()) result.put(v, 0);       // All in-degrees are 0
        for (Rotation from: neighbors.keySet()) 
        {
            for (Rotation to: neighbors.get(from)) 
            {
                result.put(to, result.get(to) + 1);           // Increment in-degree
            }
        }
        return result;
    }

    /**
     * Report (as a List) the topological sort of the vertices; null for no such sort.
     */
    public List<Rotation> topSort() 
    {
        Map<Rotation, Integer> degree = inDegree();
        // Determine all vertices with zero in-degree
        Stack<Rotation> zeroVerts = new Stack<Rotation>();        // Stack as good as any here
        for (Rotation v: degree.keySet()) 
        {
            if (degree.get(v) == 0) zeroVerts.push(v);
        }
        // Determine the topological order
        List<Rotation> result = new ArrayList<Rotation>();
        while (!zeroVerts.isEmpty()) 
        {
            Rotation v = zeroVerts.pop();                  // Choose a vertex with zero in-degree
            result.add(v);                          // Vertex v is next in topol order
            // "Remove" vertex v by updating its neighbors
            for (Rotation neighbor: neighbors.get(v)) 
            {
                degree.put(neighbor, degree.get(neighbor) - 1);
                // Remember any vertices that now have zero in-degree
                if (degree.get(neighbor) == 0) zeroVerts.push(neighbor);
            }
        }
        // Check that we have used the entire graph (if not, there was a cycle)
        if (result.size() != neighbors.size()) return null;
        return result;
    }

    public Map<Rotation,List<Rotation>> getNeighbors()
    {
        return this.neighbors;
    }

    /**
     * String representation of graph.
     */
    @Override
    public String toString() 
    {
        StringBuffer s = new StringBuffer();
        for (Rotation v: neighbors.keySet()) s.append("\n    " + v + " -> " + neighbors.get(v));
        return s.toString();                
    }
}
