/*=============================================================================
 |   Assignment:  Lab 01 - Building a Prim's MST for an input graph
 |
 |       Author:  Alexander Davila-Wollheim & Rickie
 |     Language:  Java
 |
 |   To Compile:  javac Lab01.java
 |
 |   To Execute:  java Lab01 filename
 |                     where filename is in the current directory and contains
 |                           a record containing the number of vertices,
 |                           a record containing the number of edges,
 |                           many records containing the following:
 |                              Source edge number (integer)
 |                              Destination edge number (integer)
 |                              Edge weight (double)
 |
 |        Class:  COP3503 - CS II Summer 2021
 |   Instructor:  McAlpin
 |     Due Date:  per assignment
 |
 +=============================================================================*/

/*
we need to parse the vertex, what theyre connected to, and the weight [v , c , w]
need to count the number of vertices to pass into our graph to make it,
 */

import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class Lab01 {
    public static void main(String args[]) throws IOException
    {
        File file = new File(args[0]);
        Scanner scan  = new Scanner(file);
        int numVertices = Integer.parseInt(scan.nextLine().trim());
        double aList[][] = new double[numVertices][numVertices];
        int edges = Integer.parseInt(scan.nextLine().trim());
        for(int i=0; i<edges; i++)
        {
            String line[] = scan.nextLine().split(" ");
            int from = Integer.parseInt(line[0]);
            int to = Integer.parseInt(line[1]);
            double w = Double.parseDouble(line[2]);
            aList[from][to]=w;
            aList[to][from]=w;
        }

        findMST(aList,numVertices);
    }

    //finds vertex with cheapest path
    static int findMinVertex(double[] cost, boolean[] edgesToUseInMST, int numOfVertices) {
        //minimum vertex to return
        int vertex = 0;
        //initial minimum value
        double cheapestEdge = Integer.MAX_VALUE;

        //iterate through all vertices
        for (int i = 0; i < numOfVertices; i++){
            //visit unvisited vertices and change to true bc it will be used in the MST so set to true (true meaning this edge will
            // be used)
            if(edgesToUseInMST[i]==false && cost[i]<cheapestEdge){
                //vertex has been visited
                //set the new min vertex
                vertex = i;
                //set the cheapest edge weight
                cheapestEdge = cost[i];
                //System.out.println("the minimum cost at i is " + cost[i] + "and vertex is " + vertex);
            }
        }

        return vertex;

    }

    static void findMST(double[][] aList, int numVertices) {

        int infinity = Integer.MAX_VALUE;
        //parent of each edge in the MST
        int pathCostFromParent[] = new int[numVertices];
        //weight of the each edge
        double cost[] = new double[numVertices];

        //initial cost for all edges is infinite. populate cost array with infinity
        for (int i = 0; i<numVertices; i++)
            cost[i] = infinity;

        //boolean array tells which edges will be in the MST
        boolean edgesToUseInMST[] = new boolean[numVertices];

        //int lowestValueVertex;

        pathCostFromParent[0] = -1;
        //path cost for parent node is 0
        cost[0] = 0;

        for(int i = 0; i < numVertices-1; i++){
            int lowestValueVertex = findMinVertex(cost, edgesToUseInMST, numVertices);
            edgesToUseInMST[lowestValueVertex] = true;

            //loop through the grid to-from perspective
            for(int j = 0; j<numVertices; j++){
                //check that weight at to-from position isnt empty and that it has not
                //been included yet in the in the boolean array
                if(aList[lowestValueVertex][j]!=0 && edgesToUseInMST[j] == false && aList[lowestValueVertex][j] < cost[j]){
                    //set new min cost in our cost array as the one to use in our list
                    cost[j] = aList[lowestValueVertex][j];
                    //add the vertex as part of the path to take
                    pathCostFromParent[j] = lowestValueVertex;
                    //System.out.println(pathCostFromParent[j]);
                }
            }
        }

        printMST(pathCostFromParent , aList, numVertices);

    }

    static void printMST(int[] pathCostFromParent, double[][] grid, int numOfVertices) {
        double totalPathCost=0;

        for(int i = 1; i < numOfVertices; i++){
            //System.out.println(pathCostFromParent[7]);
            System.out.println(pathCostFromParent[i] + "-" + i + " " + grid[pathCostFromParent[i]][i] );
            totalPathCost += grid[pathCostFromParent[i]][i];
        }

        System.out.println(totalPathCost);

    }
}
