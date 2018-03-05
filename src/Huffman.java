import java.io.*;
import java.util.*;
public class Huffman
{
    public static void main(String args[]) throws IOException
    {

        //compress
        BitInputStream inputStream = new BitInputStream("Hello World.txt");

        HashMap<Character, Integer> frequencies = new HashMap<>(); //<character, frequency>

        int charInt = inputStream.read();
        while(charInt != -1)
        {
            if(frequencies.containsKey((char)charInt))
                frequencies.put((char)charInt, frequencies.get((char)charInt)+1);
            else
                frequencies.put((char)charInt, 1);

            charInt = inputStream.read();
        }

        PriorityQueue<Node> nodes = new PriorityQueue<>();  //connect until this has a size of 1
        for(Map.Entry<Character, Integer> e : frequencies.entrySet())
        {
            Node n = new Node();
            n.c = e.getKey();
            n.freq = e.getValue();

            nodes.add(n);
        }

        while(nodes.size() > 1)
        {
            Node connector = new Node();
            connector.left = nodes.poll();
            connector.right = nodes.poll();
            connector.freq = connector.left.freq + connector.right.freq;

            nodes.add(connector);
        }

        HashMap<Character, String> paths = new HashMap<>(); //<character, path>
        //iterate through the tree
        Node current = nodes.peek();
        visit(current, paths);


    }

    public static void visit(Node parent, HashMap<Character, String> paths)
    {
        if(parent.c != 0)
        {
            paths.put(parent.c, parent.path);
        }

        if(parent.left != null)
        {
            parent.left.path = parent.path += "0";
        }
        if(parent.right != null)
        {
            parent.right.path = parent.path += "1";
        }
        visit(parent.left, paths);
        visit(parent.right, paths);
    }
}
class Node implements Comparable<Node>
{
    char c; //null char value is '\u0000' OR just 0
    int freq;
    String path = "";
    Node left, right;

    public int compareTo(Node otherNode)
    {
        return this.freq - otherNode.freq;
    }
}