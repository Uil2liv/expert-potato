package main.java.shapeFile;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;

public class Polygon extends Shape {
    private ByteBuffer buffer;
    private int offset;
    private int length;
    private ArrayList<Part> parts = new ArrayList<>();

    Polygon(ByteBuffer buffer, int offset, int length) {
        this.buffer = buffer;
        this.offset = offset;
        this.length = length;

        int nbParts = buffer.getInt();
        int nbPoints = buffer.getInt();

        int partsOffset[] = new int[nbParts];
        buffer.asIntBuffer().get(partsOffset);
        buffer.position(buffer.position() + nbParts * 4);

        for(int i = 0; i < nbParts; i++) {
            int partStart = partsOffset[i];
            int partEnd;
            if (i < nbParts - 1) {
                partEnd = partsOffset[i+1] - 1;
            } else {
                partEnd = nbPoints - 1;
            }
            int partLength = partEnd - partStart + 1;

            ByteBuffer partBuffer = buffer.slice();
            buffer.position(buffer.position() + (partLength * 16));
            partBuffer.limit(partLength*16);
            parts.add(new Part(partBuffer));
        }
    }

    @Override
    public ArrayList<Element> getSvgElement(Document doc) {
        ArrayList<Element> partsElements = new ArrayList<>();

        for(Part p : parts) {
            String points = "";
            while(p.buffer.position() < p.buffer.capacity() - 2) {

                if(p.buffer.position()%2 == 0) {
                    points += p.buffer.get();
                } else {
                    points += -p.buffer.get();
                }

                if(p.buffer.position() < p.buffer.capacity() - 3) {
                    points += " ";
                }
            }
            Element e = doc.createElement("polygon");
            e.setAttribute("points", points);
            partsElements.add(e);
        }

        return partsElements;
    }
}
