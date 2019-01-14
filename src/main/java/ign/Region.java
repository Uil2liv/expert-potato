package main.java.ign;

import org.w3c.dom.Document;

public class Region {
    public int offset, length;
    public String name;
    public double minX, minY, maxX, maxY;
    public int nbParts, nbPoints;

    public Region(int offset, int length) {
        this.offset = offset;
        this.length = length;
    }

    public void addToDoc(Document doc) {

    }
}
