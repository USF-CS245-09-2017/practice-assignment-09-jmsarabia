import java.util.*;
public class GraphAdjMatrix implements Graph{

	//UNWEIGHTED, DIRECTED
	private Edge[][] edges;
	private int numVertices;

	public GraphAdjMatrix(int vertices)
	{
		numVertices = vertices;
		edges = new Edge[vertices][vertices];
		//initializing edges graph with cost = 0
		for(int i = 0; i < vertices; i++)
		{
			for (int j = 0; j < vertices; j++)
			{
				Edge e = new Edge();
				e.cost = Integer.MAX_VALUE;			
				edges[i][j] = e;
			}
		}
	}

	//addEdge: adds undirected edge (adjacency matrix: adds weight to both locations) with weight/cost of w
	public void addEdge(int v1, int v2, int w)
	{
		edges[v1][v2].cost = w;
		edges[v2][v1].cost = w;
	}

	//getEdge: returns weight of the edge between v1 and v2 (will return -1 if no weighted edge)
	public int getEdge (int v1, int v2)
	{
		return edges[v1][v2].cost;
	}

	//Create spanning tree using a known boolean array, the cost of the edge, and the current path
	public int createSpanningTree ()
	{
		int mstWeight = 0;

		boolean[] knownMST = new boolean[numVertices];
		int[] costMST = new int[numVertices];
		int[] pathMST = new int[numVertices];

		//initialize arrays
		
		for(int i = 0; i < numVertices; i++)
		{
			knownMST[i] = false;
			costMST[i] = Integer.MAX_VALUE;
		}
		//assuming we start at 0; -1 for no cost and no path
		//knownMST[0] = true;
		costMST[0] = 0;
		pathMST[0] = -1;

		//Loop through through the matrix for all the vertices
		for(int currIndex = 0; currIndex < numVertices; currIndex++)
		{
			//Find the min value of the edges connected to this (could abstract to another method)
			int minValue = Integer.MAX_VALUE;
			int minIndex = -1;		
			for(int i = 0; i < numVertices; i++)
			{
				//If cost is lower, set the min value and min index
				if(costMST[i] < minValue && knownMST[i] == false)
				{	
					minValue = costMST[i];
					minIndex = i;
				}
			}
			
			knownMST[minIndex] = true;
			
			for(int k = 0; k < numVertices; k++)
			{
				//if the edge cost is not the default max value, meaning if there is an important edge, make the inner if check
				if(edges[minIndex][k].cost != Integer.MAX_VALUE && knownMST[k] == false && costMST[k] > edges[minIndex][k].cost)
				{
					//update the cost array if the edge is not known and the current cost in the matrix is less than the cost array's
					costMST[k] = edges[minIndex][k].cost;
					pathMST[k] = minIndex;
				}
			}
		}
		for(int i = 0; i < numVertices; i++)
		{
			mstWeight += costMST[i];
		}

		return mstWeight;
	}

	//minIndex: Find the index of the minimum value of the unknowns
	public int minIndex(int cost[], boolean known[])
	{
		//the cost of the minimum unknown vertex lies in relation to the current index (currIndex)
		//		initialized to max value and -1 for the first iteration
		int minValue = Integer.MAX_VALUE;
		int minIndex = -1;		
		for(int i = 0; i < numVertices; i++)
		{
			//If cost is lower
			if(cost[i] < minValue && known[i] == false)
			{	
				minValue = cost[i];
				minIndex = i;
			}
		}
		return minIndex;
	}


	//Prints matrix, for testing purposes
	public void print()
	{
		System.out.println("---------------------------------");
		for(int i = 0; i < edges.length; i++)
		{
			for(int j = 0; j < edges.length; j++)
			{
				System.out.print("| ");
				System.out.print(edges[i][j].cost + " ");
			}
			System.out.print("|\n");
		}//end outer for
		System.out.println("---------------------------------");
	}

	//Inner class Edge; neighbor and next if using adjacency list
	class Edge
	{
		int neighbor;
		int cost;
		Edge next;
	}

	@Override
	//addEdge: changes cost at src,tar position in edges array to 1
	public void addEdge(int src, int tar)
	{
		edges[src][tar].cost = 1;
	}

	@Override//topologicalSort: print a topological sorting of the graph (bfs and queue implementation)
	public void topologicalSort()
	{
		//build NumIncident array w/each position being the vertex:
		//	initialize with 0, then fill incoming 
		int[] numIncident = new int[numVertices];
		for(int v = 0; v < numVertices; v++)
		{
			numIncident[v] = 0;
		}
		for(int vert = 0; vert < numVertices; vert++)
		{
			for(int j = 0; j < numVertices; j++)
			{
				if(edges[vert][j].cost == 1)
				{
					numIncident[j]++;
				}
			}
		}

		/*Implement queue using LinkedList from java.util (FIFO), for adding in topological order
		 *	where vertices are added to the final array (topologicalOrder) if the vertex has 0 
		 *  incident edges
		 */
		Queue<Integer> queue = new LinkedList<Integer>();
		for(int i = 0; i < numVertices; i++)
		{
			if(numIncident[i] == 0)
			{
				queue.add(new Integer(i));
			}
		}
		int currentIndex = 0;
		int[] topologicalOrder = new int[numVertices];
		while(!queue.isEmpty())
		{
			//nextOnTopological should be the one with the lowest number of incident edges (0)
			int nextOnTopological = ((Integer)queue.remove()).intValue();
			topologicalOrder[currentIndex++] = nextOnTopological;
			for(int vertex = 0; vertex < numVertices; vertex++)
			{
				//if matrix shows an edge, decrement number of incident edges in numIncident array,
				//	then check if the vertex has 0 incidents and if it does, then add to queue
				if(edges[nextOnTopological][vertex].cost != 0)
				{
					if(--numIncident[vertex] == 0)
					{
						queue.add(new Integer(vertex));
					}
				}
			}//end for
		}//end while

		/*If the currentIndex is less than total vertices, the queue did not add all the vertices,
		 *	meaning there was some cycle because some incident edges did not decrement to 0 then get 
		 *	added to queue, so currentIndex did not update and ends with less than total num of vertices
		 */
		if(currentIndex < numVertices)
		{
			System.out.println("There may be a cycle in this graph");
		}
		System.out.println("----------------------------------");
		System.out.println("testing Topological Order:");
		for(int i = 0; i < topologicalOrder.length; i++)
		{
			System.out.print(topologicalOrder[i] + "| ");
		}
		System.out.println();
	}


	

}