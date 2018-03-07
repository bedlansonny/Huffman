import java.io.*;
import java.util.*;
public class Huffman
{
    static HashMap<Character, String> paths = new HashMap<>();
    public static void main(String args[]) throws IOException
    {

        //compress
        BitInputStream inputStream = new BitInputStream("War and Peace.txt");

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

        //iterate through the tree
        Node current = nodes.peek();
        visit(current, "");

        for(Character c : paths.keySet())
        {
            String output = paths.get(c);
            if(output.length() > 34)
                System.out.println(output);
        }

        inputStream.reset();
        BitOutputStream outputStream = new BitOutputStream("War and Peace comp.666");
        charInt = inputStream.read();
        while(charInt != -1)
        {
            String output = paths.get((char)charInt);
            outputStream.writeBits(output.length(), Integer.parseInt(output, 2));
            charInt = inputStream.read();
        }
        outputStream.close();



        //decompress
        BitInputStream inputStream1 = new BitInputStream("War and Peace comp.666");
        BitOutputStream outputStream1 = new BitOutputStream("War and Peace decomp.txt");
        int bit = inputStream1.readBits(1);
        current = nodes.peek();
        while(bit != -1)
        {
            if(current.c != 0)
            {
                outputStream1.write(current.c);
                current = nodes.peek();
            }
                if(bit == 0)
                    current = current.left;
                else if(bit == 1)
                    current = current.right;
                else
                    System.out.println("ERROR: BIT NOT 0 OR 1");
            bit = inputStream1.readBits(1);
        }
        outputStream1.close();


    }

    public static void visit(Node parent, String path)
    {
        if(parent.c != 0)
        {
            paths.put(parent.c, path);
            return;
        }
        visit(parent.left, path + "0");
        visit(parent.right, path + "1");
    }
}
class Node implements Comparable<Node>
{
    char c; //null char value is '\u0000' OR just 0
    int freq;
    Node left, right;

    public int compareTo(Node otherNode)
    {
        return this.freq - otherNode.freq;
    }
}