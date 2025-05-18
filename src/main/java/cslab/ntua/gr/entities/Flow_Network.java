package cslab.ntua.gr.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Collection;

import cslab.ntua.gr.algorithms.Abstract_SM_Algorithm;
import cslab.ntua.gr.algorithms.GS_MaleOpt;

public class Flow_Network
{
    private int size;
    private Collection<Rotation> rotations;
    private Rotation_Poset poset;
    private Map<Rotation,Map<Rotation,Integer>> graph;
    private Rotation src, dst;

    public Flow_Network(Rotation_Poset poset)
    {
        this.poset = poset;
        this.rotations = poset.getNeighbors().keySet();
        construct_flow_network();
        this.size = rotations.size() + 2;
    }

    private void construct_flow_network()
    {
        // Copy edges from poset (infinite capacity)
        Map<Rotation,List<Rotation>> edges = poset.getNeighbors();
        Map<Rotation,Integer> new_edges;
        graph = new HashMap<Rotation,Map<Rotation,Integer>>();
        for (Rotation from : edges.keySet())
        {
            graph.put(from, new HashMap<Rotation,Integer>());
            new_edges = graph.get(from);
            for (Rotation to : edges.get(from)) new_edges.put(to, Integer.MAX_VALUE);
        } 
        // Add source and destination
        src = new Rotation(-1);
        dst = new Rotation(-2);
        graph.put(src, new HashMap<Rotation,Integer>());
        graph.put(dst, new HashMap<Rotation,Integer>());
        // Add edges according to weight of rotations
        for (Rotation r : rotations)
        {
            if (r.weight > 0)
            {
                Map<Rotation,Integer> m = graph.get(r);
                if (m == null) continue;
                m.put(dst, r.weight);
            }
            if (r.weight < 0) 
            {
                Map<Rotation,Integer> m = graph.get(src);
                if (m == null) continue;
                m.put(r, Math.abs(r.weight));
            }
        }
    }

    // Returns true if there is a path from source 's' to sink 't' in residual graph. Also fills parent[] to store the path 
    private boolean bfs(Map<Rotation,Map<Rotation,Integer>> rGraph, Rotation s, Rotation t, Map<Rotation, Rotation> parent) 
    {
        // Create a visited set and mark 
        // all vertices as not visited     
        Map<Rotation, Boolean> visited = new HashMap<Rotation, Boolean>();
        for (Rotation i : rGraph.keySet()) visited.put(i, false);
        // Create a queue, enqueue source vertex
        // and mark source vertex as visited     
        Queue<Rotation> q = new LinkedList<Rotation>();
        q.add(s);
        visited.put(s, true);
        parent.put(s, null);
        // Standard BFS Loop     
        Map<Rotation,Integer> neighbors;
        while (!q.isEmpty()) 
        {
            Rotation i = q.poll();

            neighbors = rGraph.get(i);
            for (Rotation j: neighbors.keySet())
            {
                if (rGraph.get(i).get(j) > 0 && !visited.get(j)) 
                {
                    q.offer(j);
                    visited.put(j, true);
                    parent.put(j, i);
                }
            }
        }
        // If we reached sink in BFS starting 
        // from source, then return true, else false     
        return (visited.get(t) == true);
    }
     
    // A DFS based function to find all reachable vertices from s. The function marks visited[i] as true if i is reachable from s. 
    // The initial values in visited[] must be false. We can also use BFS to find reachable vertices
    private void dfs(Map<Rotation,Map<Rotation,Integer>> rGraph, Rotation s, Map<Rotation, Boolean> visited) 
    {
        Map<Rotation,Integer> neighbors;
        visited.put(s, true);
        neighbors = rGraph.get(s);
        for (Rotation i: neighbors.keySet())
        {
            if (rGraph.get(s).get(i) > 0 && !visited.get(i)) dfs(rGraph, i, visited);
        }
    }
 
    // Returns a list with the positive rotations of the minimum s-t cut
    public List<Rotation> minCut() 
    {
        List<Rotation> res = new ArrayList<Rotation>();
        Rotation u, v;
        Rotation s = src;
        Rotation t = dst;
         
        // Create a residual graph and fill the residual graph with given capacities in the original graph as residual capacities in residual graph
        // rGraph(i)(j) indicates residual capacity of edge i-j
        Map<Rotation,Map<Rotation,Integer>> rGraph = new HashMap<Rotation,Map<Rotation,Integer>>();
        for (Rotation i : graph.keySet()) 
        {
            rGraph.put(i, new HashMap<Rotation,Integer>());
            rGraph.get(i).putAll(graph.get(i));
        }
        // This array is filled by BFS to store path
        Map<Rotation, Rotation> parent = new HashMap<Rotation, Rotation>();
        // Augment the flow while there is path from source to sink  
        int pathFlow;   
        while (bfs(rGraph, s, t, parent)) 
        {         
            // Find minimum residual capacity of the edges along the path filled by BFS. 
            // Or we can say find the maximum flow through the path found.
            pathFlow = Integer.MAX_VALUE;         
            for (v = t; v != s; v = parent.get(v)) 
            {
                u = parent.get(v);
                pathFlow = Math.min(pathFlow, rGraph.get(u).get(v));
            }
            // update residual capacities of the edges and reverse edges along the path
            for (v = t; v != s; v = parent.get(v)) 
            {
                u = parent.get(v);
                remove_flow(rGraph, u, v, pathFlow);
                add_flow(rGraph, v, u, pathFlow);
            }
        }
        // Flow is maximum now, find vertices reachable from s     
        Map<Rotation, Boolean> isVisited = new HashMap<Rotation, Boolean>();
        for (Rotation i : rGraph.keySet()) isVisited.put(i, false); 
        dfs(rGraph, s, isVisited);
        // Print all edges that are from a reachable vertex to non-reachable vertex in the original graph     
        Map<Rotation,Integer> neighbors;
        for (Rotation i: graph.keySet())
        {
            neighbors = rGraph.get(i);
            for (Rotation j: neighbors.keySet())
            {
                if (isVisited.get(i) && !isVisited.get(j))
                {
                    if (j.id == -2) res.add(i);
                }
            }
        }
        return res;
    }

    private void add_flow(Map<Rotation,Map<Rotation,Integer>> rGraph, Rotation src, Rotation dest, int flow)
    {
        if (rGraph.get(src).get(dest) == null) rGraph.get(src).put(dest, flow);
        else rGraph.get(src).put(dest, rGraph.get(src).get(dest) + flow);
    }

    private void remove_flow(Map<Rotation,Map<Rotation,Integer>> rGraph, Rotation src, Rotation dest, int flow)
    {
        if (rGraph.get(src).get(dest) == null) rGraph.get(src).put(dest, -flow);
        else rGraph.get(src).put(dest, rGraph.get(src).get(dest) - flow);
    }

    /**
     * Main program (for testing).
     */
    public static void main(String[] args) 
    {
        int n;
        Abstract_SM_Algorithm smp;
        Agent[][] agents;

        n = 300;
        smp = new GS_MaleOpt(n, null, null);
        agents = smp.getAgents();
        Rotations rots = new Rotations(n, agents, null, null);
        Rotation_Poset poset = new Rotation_Poset(n, agents, 0, rots, null, null);
        Flow_Network g = new Flow_Network(poset);
        g.minCut();
    }
}
