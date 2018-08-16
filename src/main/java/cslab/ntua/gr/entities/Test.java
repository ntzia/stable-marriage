package gr.ntua.cslab.entities;

import java.util.*;
 
public class Test 
{
    private Map<Rotation,Map<Rotation,Integer>> graph;
    private int size;

    public Test(Map<Rotation,Map<Rotation,Integer>> graph)
    {
        this.graph = graph;
        size = 6;
    }

    // Returns true if there is a path
    // from source 's' to sink 't' in residual 
    // graph. Also fills parent[] to store the path 
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
     
    // A DFS based function to find all reachable 
    // vertices from s. The function marks visited[i] 
    // as true if i is reachable from s. The initial 
    // values in visited[] must be false. We can also 
    // use BFS to find reachable vertices
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
 
    // Prints the minimum s-t cut
    private void minCut(Rotation s, Rotation t) 
    {
        Rotation u, v;
         
        // Create a residual graph and fill the residual 
        // graph with given capacities in the original 
        // graph as residual capacities in residual graph
        // rGraph[i][j] indicates residual capacity of edge i-j
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
            // Find minimum residual capacity of the edges 
            // along the path filled by BFS. Or we can say 
            // find the maximum flow through the path found.
            pathFlow = Integer.MAX_VALUE;         
            for (v = t; v != s; v = parent[v.id]) 
            {
                u = parent[v.id];
                pathFlow = Math.min(pathFlow, rGraph.get(u).get(v));
            }
            // update residual capacities of the edges and 
            // reverse edges along the path
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
         
        // Print all edges that are from a reachable vertex to
        // non-reachable vertex in the original graph     
        Map<Rotation,Integer> neighbors;
        for (Rotation i: graph.keySet())
        {
            neighbors = rGraph.get(i);
            for (Rotation j: neighbors.keySet())
            {
                if (isVisited[i.id] && !isVisited[j.id])
                {
                    System.out.println(i.id + " - " + j.id);
                }
            }
        }
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
 
    //Driver Program
    public static void main(String args[]) 
    {
        // Let us create a graph shown in the above example
        Map<Rotation,Map<Rotation,Integer>> graph = new HashMap<Rotation,Map<Rotation,Integer>>();

        Rotation r0 = new Rotation(0);
        Rotation r1 = new Rotation(1);
        Rotation r2 = new Rotation(2);
        Rotation r3 = new Rotation(3);
        Rotation r4 = new Rotation(4);
        Rotation r5 = new Rotation(5);

        graph.put(r0, new HashMap<Rotation,Integer>());
        graph.put(r1, new HashMap<Rotation,Integer>());
        graph.put(r2, new HashMap<Rotation,Integer>());
        graph.put(r3, new HashMap<Rotation,Integer>());
        graph.put(r4, new HashMap<Rotation,Integer>());
        graph.put(r5, new HashMap<Rotation,Integer>());

        graph.get(r0).put(r1, 16);
        graph.get(r0).put(r2, 13);

        graph.get(r1).put(r2, 10);
        graph.get(r1).put(r3, 12);

        graph.get(r2).put(r1, 4);
        graph.get(r2).put(r4, 14);

        graph.get(r3).put(r2, 9);
        graph.get(r3).put(r5, 20);

        graph.get(r4).put(r3, 7);
        graph.get(r4).put(r5, 4);

        Test t = new Test(graph);
        t.minCut(r0, r5);
    }
}