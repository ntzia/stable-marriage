package cslab.ntua.gr.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.Arrays;

import cslab.ntua.gr.algorithms.Abstract_SM_Algorithm;
import cslab.ntua.gr.algorithms.GS_FemaleOpt;
import cslab.ntua.gr.algorithms.GS_MaleOpt;

public class Rotation_Poset
{
    private ArrayList<Rotation> rotations;
    private Map<Rotation,List<Rotation>> neighbors, neighbors_reversed;
    // For the case of a poset with constraints: false -> excluded, true -> included or unbound
    public boolean[] constrained_rotations = null; 


    // Use to immediately construct the digraph
    // side = 0 constructs the poset of men rotations (that worsen the men)
    // Matchings can be null if not yet available
    public Rotation_Poset(Agent[][] agents, int side, Rotations rotations, Marriage maleOptMatching, Marriage femaleOptMatching)
    {
        if (side == 0)
        {
            this.rotations = rotations.men_rotations;
            construct_digraph_men(maleOptMatching, femaleOptMatching, agents);
        } 
        else 
        {
            this.rotations = rotations.women_rotations;
            construct_digraph_women(maleOptMatching, femaleOptMatching, agents);
        }
        // Possibly multi-graph, so remove duplicate edges (also removes self-loops)
        remove_duplicates();
        this.neighbors_reversed = reverse_graph();
    }

    // Use to construct the poset if the graph is already ready
    public Rotation_Poset(ArrayList<Rotation> rotations, Map<Rotation,List<Rotation>> neighbors, boolean[] constrained_rotations)
    {
        this.rotations = rotations;
        this.neighbors = neighbors;
        this.constrained_rotations = constrained_rotations;
        this.neighbors_reversed = reverse_graph();
    }

    private void construct_digraph_men(Marriage maleOptMatching, Marriage femaleOptMatching, Agent[][] agents)
    {
        int man, woman, man1, next_mate_of_woman, index, latest_type1_label;
        int n = agents[0].length;

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

    private void construct_digraph_women(Marriage maleOptMatching, Marriage femaleOptMatching, Agent[][] agents)
    {
        int man, woman, woman1, next_mate_of_man, index, latest_type1_label;
        int n = agents[0].length;

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
        boolean[] seen = new boolean[rotations.size()];
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
     * Performs a dfs search and returns all visited nodes as a list
     */
    private List<Rotation> do_dfs(Map<Rotation,List<Rotation>> dg, List<Rotation> starting_nodes)
    {
        boolean[] visited = new boolean[rotations.size()];
        Stack<Rotation> stack = new Stack<>();
        for (Rotation starting_node : starting_nodes) {
            if (!visited[starting_node.id]) {
                stack.push(starting_node);
                while (!stack.isEmpty()) {
                    Rotation current = stack.pop();
                    if (visited[current.id]) continue;
                    visited[current.id] = true;
                    for (Rotation neighbor : dg.get(current)) {
                        if (!visited[neighbor.id]) {
                            stack.push(neighbor);
                        }
                    }
                }
            }
        }
        List<Rotation> res = new ArrayList<Rotation>(rotations.size() / 10);
        for (int i = 0; i < rotations.size(); i++)
        {
            if (visited[i]) res.add(rotations.get(i));
        }
        return res;
    }

    /**
     * Performs a dfs search and returns all visited nodes as a boolean array
     */
    private boolean[] do_dfs_boolean(Map<Rotation,List<Rotation>> dg, List<Rotation> starting_nodes)
    {
        boolean[] visited = new boolean[rotations.size()];
        Stack<Rotation> stack = new Stack<>();
        for (Rotation starting_node : starting_nodes) {
            if (!visited[starting_node.id]) {
                stack.push(starting_node);
                while (!stack.isEmpty()) {
                    Rotation current = stack.pop();
                    if (visited[current.id]) continue;
                    visited[current.id] = true;
                    for (Rotation neighbor : dg.get(current)) {
                        if (!visited[neighbor.id]) {
                            stack.push(neighbor);
                        }
                    }
                }
            }
        }
        return visited;
    }

    /**
     * Given a list of rotations, modify the ids of the rotations to match the indexing of the list.
     * Additionally, modify the data structures of this object (list of rotations, neighbors, etc.)
     * so that they are consistent with the new ids.
     */
    public void rename_ids(ArrayList<Rotation> renaming_list)
    {
        // Build a hashmap that maps old ids to new ids
        Map<Integer, Integer> id_map_old_to_new = new HashMap<Integer, Integer>();
        for (int i = 0; i < renaming_list.size(); i++)
            id_map_old_to_new.put(renaming_list.get(i).id, i);

        // Now, we need to update the graph structure
        // Since we are using a hashmap with the rotation ids as hashcodes, we can keep the lists the same
        // and simply rebuild the hashmap after changing the id of the rotation objects
        Map<Rotation,List<Rotation>> updated_neighbors = new HashMap<Rotation,List<Rotation>>();
        for (int i = 0; i < renaming_list.size(); i++)
        {
            Rotation r = renaming_list.get(i);
            List<Rotation> neighbors_list = neighbors.get(r);
            // Update the id of the rotation
            r.id = i;
            // Insert the list in a new map
            updated_neighbors.put(r, neighbors_list);
        }
        this.neighbors = updated_neighbors;

        // Now we need to update the reversed neighbors list
        // Simply reverse the neighbors list
        this.neighbors_reversed = reverse_graph();

        // Finally, replace the list of rotations with the new one
        this.rotations = renaming_list;
    }

    /**
     * Modifies the poset according to a set of inclusion/exclusion constraints.
     * The constraints are given as a boolean list, that indicates which rotations are included/excluded.
     * The function returns null if the constraints cannot be satisfied,
     * or otherwise a new poset (new object) that has a new graph structure.
     * The rest of the data structures of the object (rotation list, agents, etc.) are pointing to the current object.
     * The indexes of rotations remain unchanged.
     * !! Caution: the function assumes that the rotation indexing follows a topological sort.
     */
    public Rotation_Poset modify_poset(List<Boolean> constraints)
    {
        // System.out.println("Modifying poset with constraints: " + constraints);
        
        // Check if the constraints can be satisfied
        // The constraints cannot be satisfied if there is a node that must be excluded (false in the boolean array)
        // which has an ancestor that must be included (true in the boolean array)
        List<Rotation> excluded_list = new ArrayList<Rotation>();
        for (int i = 0; i < constraints.size(); i++)
            if (!constraints.get(i)) excluded_list.add(rotations.get(i));
        // For an excluded rotation, we need to exclude all its ancestors
        // Note: we only need to do this for excluded rotations (and not the included ones) because of the topological sort
        // If we constrain a rotation then all its ancestors need to be constrained as well (either included or excluded=error)
        boolean[] updated_constraints = cant_eliminate_boolean(excluded_list);
        // Remove duplicates from the list representation of exclusions
        // excluded_list = excluded_list.stream().distinct().collect(Collectors.toList());

        // Update the constraints list and check for any violations
        for (int i = 0; i < updated_constraints.length; i++)
        {
            if (updated_constraints[i]) 
            {
                // Rotation was visited, thus should be excluded
                if (constraints.size() > i)
                    if (constraints.get(i)) 
                        return null; // Violation
                updated_constraints[i] = false; 
            }
            else 
            {
                updated_constraints[i] = true; // True means either included by force or unbound
            }
        }

        // We are ready to allocate memory for a new graph
        Map<Rotation,List<Rotation>> new_neighbors = new HashMap<Rotation,List<Rotation>>();
        for (int i = constraints.size(); i < rotations.size(); i++) // Included and excluded both result in deletion from the graph, so start with originally unconstrained
        {
            if (!updated_constraints[i]) continue; // Excluded results in deletion from the graph (can't be included here)
            // Create a new list of neighbors for the rotation
            Rotation r = rotations.get(i);
            List<Rotation> neighbors_list = new LinkedList<Rotation>();
            for (Rotation neighbor : neighbors.get(r))
            {
                // Check if the neighbor is part of the graph
                if (neighbor.id >= constraints.size() && updated_constraints[neighbor.id]) neighbors_list.add(neighbor);
            }
            new_neighbors.put(r, neighbors_list);
        }

        return new Rotation_Poset(rotations, new_neighbors, updated_constraints);
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
     * Given a chosen list of rotations that will not be eliminated, 
     * outputs all the rotations that must not be eliminated
     */
    public boolean[] cant_eliminate_boolean(List<Rotation> r)
    {
        return do_dfs_boolean(this.neighbors, r);
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
     * Report (as an ArrayList) the topological sort of the vertices; null for no such sort.
     */
    public ArrayList<Rotation> topSort() 
    {
        Map<Rotation, Integer> degree = inDegree();
        // Determine all vertices with zero in-degree
        Stack<Rotation> zeroVerts = new Stack<Rotation>();        // Stack as good as any here
        for (Rotation v: degree.keySet()) 
        {
            if (degree.get(v) == 0) zeroVerts.push(v);
        }
        // Determine the topological order
        ArrayList<Rotation> result = new ArrayList<Rotation>();
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
