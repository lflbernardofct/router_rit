/**
 * Redes Integradas de Telecomunicacoes 
 * MEEC/MERSIM 2026/2027
 *
 * Entry.java
 *
 * Hold ROUTE vector elements
 *
 * @author  Luis Bernardo
 */

package router;

import java.io.*;
import java.util.Arrays;

/** 
 * Hold ROUTE vector elements 
 */
public class Entry {
    
    /** Destination */
    public char dest;
    /** Distance */
    public int dist;
    
    /** Create a new instance of Entry */
    public Entry(){
        this.dest= ' ';
        this.dist= -1;
    }
    
    /**
     * Create a new instance of Entry
     * @param dest  destination address
     * @param dist  distance
     */
    public Entry(char dest, int dist){
        this.dest= dest;
        this.dist= dist;
    }
    
    /**
     * Create a new instance of Entry dupping another object
     * @param src object that will be copied
     */
    public Entry(Entry src){
        this.dest= src.dest;
        this.dist= src.dist;
    }
    
    /**
     * Create a new instance of Entry from an input stream
     * @param dis  input stream
     * @throws java.io.IOException 
     */
    public Entry(DataInputStream dis) throws java.io.IOException {
        readEntry(dis);
    }
 
    /**
     * Update the Entry fields
     * @param dest  new destination
     * @param dist  new distance
     */
    public void update(char dest, int dist) {
        this.dest= dest;
        this.dist= dist;
    }

    /**
     * Update the distance field
     * @param dist  new distance
     */
    public void update_dist(int dist) {
        this.dist= dist;
    }
    
    /**
     * Return a string with the entry contents
     * @return string with the entry contents
     */
    @Override
    public String toString() {
        return "("+dest+" , "+dist+")";
    }
    
    /**
     * Compare with another Entry object
     * @param e  an Entry object
     * @return true if entry is equal to e
     */
    public boolean equals_to(Entry e) {
        return (e!=null) && (dest==e.dest) && (e.dist == dist);
    }
    
    /**
     * Compares to the destination field of another object
     * @param e  an Entry object
     * @return true if the destination is equal
     */
    public boolean equals_dest(Entry e) {
        return dest == e.dest;
    }
    
    /**
     * Return a string with a vector contents
     * @param vec Entry vector
     * @return a string with the vector contents
     */
    public static String vector_to_string(Entry[] vec) {
        if (vec==null)
            return null;
        String str="["+vec.length;
        for (Entry e: vec) {
            str+= ":"+e.toString();
        }
        str += "]";
        return str;
    }
   
    /**
     * Compare if vec1 and vec2 are equal
     * @param vec1 vector 1
     * @param vec2 vector 2
     * @return true if they are equal, false otherwise
     */
    public static boolean equal_Entry_vec(Entry[] vec1, Entry[] vec2) {
        boolean[] used;
        if ((vec1==null) || (vec2==null))
            return false;
        if (vec1.length != vec2.length)
            return false;
        used= new boolean [vec1.length];
        Arrays.fill(used, Boolean.FALSE);
        int cnt= 0;
        for (Entry e1: vec1) {
            for (int i= 0; i<vec2.length; i++) {
                if (e1.equals_to(vec2[i]) && !used[i]) {
                    used[i]= true;
                    cnt++;
                }
            }
        }
        return (cnt==vec1.length);
    }
    
    /**
     * Write the Entry content to a DataOutputStream
     * @param dos  output stream
     * @throws java.io.IOException 
     */
    public void writeEntry(DataOutputStream dos) throws java.io.IOException {
        dos.writeChar(dest);
        dos.writeInt(dist);
    }
    
    /**
     * Read the Entry contents from one DataInputStream
     * @param dis  input stream
     * @throws java.io.IOException 
     */
    public final void readEntry(DataInputStream dis) throws java.io.IOException {
        dest= dis.readChar();
        if (!Character.isUpperCase(dest)) {
            throw new IOException("Invalid address '"+dest+"'");
        }            
        dist= dis.readInt();
        if ((dist<0) || (dist>Router.MAX_DISTANCE)) {
            throw new IOException("Invalid distance '"+dist+"'");
        }
    }
    
}
