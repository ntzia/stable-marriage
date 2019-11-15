package cslab.ntua.gr.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import cslab.ntua.gr.algorithms.Abstract_SM_Algorithm;
import cslab.ntua.gr.algorithms.GS_MaleOpt;

public class Flow_Network
{
    private int size;
    private ArrayList<Rotation> rotations;
    private Rotation_Poset poset;
    private Map<Rotation,Map<Rotation,Integer>> graph;
    private Rotation src, dst;

    public Flow_Network(ArrayList<Rotation> rots, Rotation_Poset poset, int[] rotation_weights)
    {
        this.rotations = rots;
        this.poset = poset;
        construct_flow_network(rotation_weights);
        this.size = rotations.size() + 2;
    }

    private void construct_flow_network(int[] rotation_weights)
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
        src = new Rotation(rotations.size());
        dst = new Rotation(rotations.size() + 1);
        graph.put(src, new HashMap<Rotation,Integer>());
        graph.put(dst, new HashMap<Rotation,Integer>());
        // Add edges according to weight of rotations
        int w;
        for (Rotation r : rotations)
        {
            w = rotation_weights[r.id];
            if (w > 0) graph.get(r).put(dst, w);
            if (w < 0) graph.get(src).put(r, Math.abs(w));
        }
    }

    // Returns true if there is a path from source 's' to sink 't' in residual graph. Also fills parent[] to store the path 
    private boolean bfs(Map<Rotation,Map<Rotation,Integer>> rGraph, Rotation s, Rotation t, Rotation[] parent) 
    {
        // Create a visited array and mark 
        // all vertices as not visited     
        boolean[] visited = new boolean[size];
        // Create a queue, enqueue source vertex
        // and mark source vertex as visited     
        Queue<Rotation> q = new LinkedList<Rotation>();
        q.add(s);
        visited[s.id] = true;
        parent[s.id] = null;
        // Standard BFS Loop     
        Map<Rotation,Integer> neighbors;
        while (!q.isEmpty()) 
        {
            Rotation i = q.poll();

            neighbors = rGraph.get(i);
            for (Rotation j: neighbors.keySet())
            {
                if (rGraph.get(i).get(j) > 0 && !visited[j.id]) 
                {
                    q.offer(j);
                    visited[j.id] = true;
                    parent[j.id] = i;
                }
            }
        }
        // If we reached sink in BFS starting 
        // from source, then return true, else false     
        return (visited[t.id] == true);
    }
     
    // A DFS based function to find all reachable vertices from s. The function marks visited[i] as true if i is reachable from s. 
    // The initial values in visited[] must be false. We can also use BFS to find reachable vertices
    private void dfs(Map<Rotation,Map<Rotation,Integer>> rGraph, Rotation s, boolean[] visited) 
    {
        Map<Rotation,Integer> neighbors;
        visited[s.id] = true;
        neighbors = rGraph.get(s);
        for (Rotation i: neighbors.keySet())
        {
            if (rGraph.get(s).get(i) > 0 && !visited[i.id]) dfs(rGraph, i, visited);
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
        // This array is filled by BFS and to store path
        Rotation[] parent = new Rotation[size]; 
        // Augment the flow while tere is path from source to sink  
        int pathFlow;   
        while (bfs(rGraph, s, t, parent)) 
        {         
            // Find minimum residual capacity of the edges along the path filled by BFS. 
            // Or we can say find the maximum flow through the path found.
            pathFlow = Integer.MAX_VALUE;         
            for (v = t; v != s; v = parent[v.id]) 
            {
                u = parent[v.id];
                pathFlow = Math.min(pathFlow, rGraph.get(u).get(v));
            }
            // update residual capacities of the edges and reverse edges along the path
            for (v = t; v != s; v = parent[v.id]) 
            {
                u = parent[v.id];
                remove_flow(rGraph, u, v, pathFlow);
                add_flow(rGraph, v, u, pathFlow);
            }
        }
        // Flow is maximum now, find vertices reachable from s     
        boolean[] isVisited = new boolean[size];     
        dfs(rGraph, s, isVisited);
        // Print all edges that are from a reachable vertex to non-reachable vertex in the original graph     
        Map<Rotation,Integer> neighbors;
        for (Rotation i: graph.keySet())
        {
            neighbors = rGraph.get(i);
            for (Rotation j: neighbors.keySet())
            {
                if (isVisited[i.id] && !isVisited[j.id])
                {
                    if (j.id == size - 1) res.add(i);
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
        Flow_Network g = new Flow_Network(rots.men_rotations, poset, null);
        g.minCut();
    }
}
