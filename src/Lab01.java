/*=============================================================================
 |   Assignment:  Lab 01 - Building a Prim's MST for an input graph
 |
 |       Author:  Alexander Davila-Wollheim & Rickie Mobley
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.io.File;
import java.util.Scanner;

class Lab01 {

    static class Edges {
        int to;
        int from;
        double weight;

        public Edges(int to, int from, double weight) {
            this.to = to;
            this.from = from;
            this.weight = weight;
        }
    }

    static class HeapNodes {
        int vertex;
        double key;
    }

    static class ResultSets {
        int parent;
        double weight;
    }

    public static class Graph {
        int vertices;
        LinkedList<Edges>[] adjacencylist;
        //ArrayList<LinkedList<Edges>> adjacencylist;

        Graph(int vertices) {
            this.vertices = vertices;
            adjacencylist = new LinkedList[vertices];
            //initialize adjacency lists for all the vertices
            for (int i = 0; i < vertices; i++) {
                adjacencylist[i] = new LinkedList<>();
            }
        }

        public void addEdge(int to, int from, double weight) {
            Edges edge = new Edges(to, from, weight);
            adjacencylist[to].addFirst(edge);

            edge = new Edges(from, to, weight);
            adjacencylist[from].addFirst(edge); //for undirected graph
        }

        public void primMSTs() {

            boolean[] inHeap = new boolean[vertices];
            ResultSets[] resultSet = new ResultSets[vertices];
            //keys[] used to store the key to know whether min hea update is required
            Double[] key = new Double[vertices];
            //create heapNode for all the vertices
            HeapNodes[] heapNodes = new HeapNodes[vertices];
            for (int i = 0; i < vertices; i++) {
                heapNodes[i] = new HeapNodes();
                heapNodes[i].vertex = i;
                heapNodes[i].key = Integer.MAX_VALUE;
                resultSet[i] = new ResultSets();
                resultSet[i].parent = -1;
                inHeap[i] = true;
                key[i] = Double.MAX_VALUE;
            }

            //decrease the key for the first index
            heapNodes[0].key = 0;

            //add all the vertices to the MinHeap
            MinHeap minHeap = new MinHeap(vertices);
            //add all the vertices to priority queue
            for (int i = 0; i < vertices; i++) {
                minHeap.insert(heapNodes[i]);
            }

            //while minHeap is not empty
            while (!minHeap.isEmpty()) {
                //extract the min
                HeapNodes extractedNode = minHeap.extractMin();

                //extracted vertex
                int extractedVertex = extractedNode.vertex;
                inHeap[extractedVertex] = false;

                //iterate through all the adjacent vertices
                LinkedList<Edges> list = adjacencylist[extractedVertex];
                for (Edges edge : list) {
                    //only if edge destination is present in heap
                    if (inHeap[edge.from]) {
                        int from = edge.from;
                        double newKey = edge.weight;
                        //check if updated key < existing key, if yes, update if
                        if (key[from] > newKey) {
                            decreaseKey(minHeap, newKey, from);
                            //update the parent node for destination
                            resultSet[from].parent = extractedVertex;
                            resultSet[from].weight = newKey;
                            key[from] = newKey;
                        }
                    }
                }
            }
            //print mst
            printMST(resultSet);
        }

        public void decreaseKey(MinHeap minHeap, double newKey, int vertex) {

            //get the index which key's needs a decrease;
            int index = minHeap.indexes[vertex];

            //get the node and update its value
            HeapNodes node = minHeap.mH[index];
            node.key = newKey;
            minHeap.bubbleUp(index);
        }

        public void printMST(ResultSets[] resultSet) {
            double totalCostMST = 0.0;
            DecimalFormat finalFormat = new DecimalFormat();
            finalFormat.setMaximumFractionDigits(5);

            for (int i = 1; i < vertices; i++) {
                System.err.println(i + "-" + resultSet[i].parent + " " + resultSet[i].weight);
                totalCostMST += resultSet[i].weight;
            }

            System.out.println(finalFormat.format(totalCostMST));
        }
    }

    static class MinHeap {
        int capacity;
        int currentSize;
        HeapNodes[] mH;
        int[] indexes; //will be used to decrease the key


        public MinHeap(int capacity) {
            this.capacity = capacity;
            mH = new HeapNodes[capacity + 1];
            indexes = new int[capacity];
            mH[0] = new HeapNodes();
            mH[0].key = Integer.MIN_VALUE;
            mH[0].vertex = -1;
            currentSize = 0;
        }


        public void insert(HeapNodes x) {
            currentSize++;
            int idx = currentSize;
            mH[idx] = x;
            indexes[x.vertex] = idx;
            bubbleUp(idx);
        }

        public void bubbleUp(int pos) {
            int parentIdx = pos / 2;
            int currentIdx = pos;
            while (currentIdx > 0 && mH[parentIdx].key > mH[currentIdx].key) {
                HeapNodes currentNode = mH[currentIdx];
                HeapNodes parentNode = mH[parentIdx];

                //swap the positions
                indexes[currentNode.vertex] = parentIdx;
                indexes[parentNode.vertex] = currentIdx;
                swap(currentIdx, parentIdx);
                currentIdx = parentIdx;
                parentIdx = parentIdx / 2;
            }
        }

        public HeapNodes extractMin() {
            HeapNodes min = mH[1];
            HeapNodes lastNode = mH[currentSize];
//            update the indexes[] and move the last node to the top
            indexes[lastNode.vertex] = 1;
            mH[1] = lastNode;
            mH[currentSize] = null;
            sinkDown(1);
            currentSize--;
            return min;
        }

        public void sinkDown(int k) {
            int smallest = k;
            int leftChildIdx = 2 * k;
            int rightChildIdx = 2 * k + 1;
            if (leftChildIdx < heapSize() && mH[smallest].key > mH[leftChildIdx].key) {
                smallest = leftChildIdx;
            }
            if (rightChildIdx < heapSize() && mH[smallest].key > mH[rightChildIdx].key) {
                smallest = rightChildIdx;
            }
            if (smallest != k) {

                HeapNodes smallestNode = mH[smallest];
                HeapNodes kNode = mH[k];

                //swap the positions
                indexes[smallestNode.vertex] = k;
                indexes[kNode.vertex] = smallest;
                swap(k, smallest);
                sinkDown(smallest);
            }
        }

        public void swap(int a, int b) {
            HeapNodes temp = mH[a];
            mH[a] = mH[b];
            mH[b] = temp;
        }

        public boolean isEmpty() {
            return currentSize == 0;
        }

        public int heapSize() {
            return currentSize;
        }
    }

    public static void main(String[] args) throws Exception {
        File file = new File(args[0]);
        Scanner scan = new Scanner(file);
        int numVertices = Integer.parseInt(scan.nextLine());
        int numEdges = Integer.parseInt(scan.nextLine());

        Graph graph = new Graph(numVertices);

        for (int i = 1; i <= numEdges; i++) {

            String edgeAndWeight[] = scan.nextLine().split(" ");

            int to = Integer.parseInt(edgeAndWeight[0]);
            int from = Integer.parseInt(edgeAndWeight[1]);
            double weight = Double.parseDouble(edgeAndWeight[2]);
            graph.addEdge(to, from, weight);

        }
        graph.primMSTs();
    }


}
