import java.io.*;
import java.util.*;
public class Huffman
{
    static HashMap<Character, String> paths = new HashMap<>();
    public static void main(String args[]) throws IOException
    {

        //compress
        String filename = "War and Peace.txt";
        String[] splitFileName = filename.split("\\.");
        BitInputStream inputStream = new BitInputStream(filename);

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

        //fix trailing zeros problem
        for(Character c : paths.keySet())
        {
            String path = paths.get(c);
            if(Integer.parseInt(path, 2)==0)
            {
                current = nodes.peek();
                for(int i = 0; i < path.length(); i++)
                    current = current.left;

                Node clone = new Node();
                clone.c = current.c;
                clone.freq = current.freq;

                current.c = 0;
                current.right = clone;

                paths.put(c, path + "1");
            }
        }

        for(Character c : paths.keySet())
        {
            String output = paths.get(c);
            //if(output.length() > 34)
                //System.out.println(c + ": " + output);
        }

        inputStream.reset();
        BitOutputStream outputStream = new BitOutputStream(splitFileName[0] + " comp.666");
        charInt = inputStream.read();
        while(charInt != -1)
        {
            String output = paths.get((char)charInt);
            outputStream.writeBits(output.length(), Integer.parseInt(output, 2));
            charInt = inputStream.read();
        }
        outputStream.close();



        //decompress
        BitInputStream inputStream1 = new BitInputStream(splitFileName[0] + " comp.666");
        BitOutputStream outputStream1 = new BitOutputStream(splitFileName[0] + " decomp." + splitFileName[1]);
        int bit = inputStream1.readBits(1);
        current = nodes.peek();
        while(current != null && bit != -1)
        {
            //System.out.print(bit);
            if(current.c != 0)
            {
                outputStream1.write(current.c);
                current = nodes.peek();
            }
                if(bit == 0)
                    current = current.left;
                else if(bit == 1)
                    current = current.right;
            bit = inputStream1.readBits(1);
        }
        outputStream1.close();


    }

    public static void visit(Node parent, String path)
    {
        if(parent.c != 0)
        {
            /*
            if(Integer.parseInt(path, 2) == 0)
            {
                //paths.put(parent.c, path + "1");
                Node clone = new Node();
                clone.c = parent.c;
                clone.freq = parent.freq;

                parent = new Node();
                parent.right = clone;
                parent.freq = parent.right.freq;

                paths.put(clone.c, path + "1");
            }
            else
            */
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