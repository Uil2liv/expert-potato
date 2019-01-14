package main.java.shapeFile;

import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Polygon extends Shape {
    private int id, length, nbParts, nbPoints;
    private double minX, minY, maxX, maxY;
    private int[] parts;
    private Point[] points;

    Polygon(int id, int length) {
        this.id = id;
        this.length = length;
    }

    @Override
    public void read(FileInputStream file) {
        ByteBuffer bb = ByteBuffer.allocate(length);
        bb.order(ByteOrder.LITTLE_ENDIAN);

        try {
            file.read(bb.array());

            ShapeType type = ShapeType.fromInt(bb.getInt());
            minX = bb.getDouble();
            minY = bb.getDouble();
            maxX = bb.getDouble();
            maxY = bb.getDouble();
            nbParts = bb.getInt();
            parts = new int[nbParts];
            nbPoints = bb.getInt();
            points = new Point[nbPoints];
            for(int i = 0; i < nbParts; i++) {
                parts[i] = bb.getInt();
            }
            for(int i = 0; bb.hasRemaining(); i++) {
                points[i] = new Point(bb.getDouble(), bb.getDouble());
            }

            System.out.println(nbParts + ", " + nbPoints);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Element[] createElements(Document doc) {
        Element[] els = new Element[nbParts];
            int start, end;
            String pointsAtt;
            for (int i = 0; i < nbParts; i++) {
                if (i < parts.length - 1) {
                    start = parts[i];
                    end = parts[i + 1] - 2;
                } else {
                    start = parts[i];
                    end = points.length - 2;
                }

                pointsAtt = "";
                for (int p = start; p <= end; p++) {
                    pointsAtt += points[p].x + "," + (-points[p].y);
                    if (p < end)
                        pointsAtt += " ";
                }

                Element el = doc.createElement("polygon");
                el.setAttribute("points", pointsAtt);

                els[i] = el;
            }
        return els;
    }
}
