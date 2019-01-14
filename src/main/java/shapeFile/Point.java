package main.java.shapeFile;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileInputStream;

public class Point extends Shape {
    public double x, y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void read(FileInputStream file) {

    }

    @Override
    public Element[] createElements(Document doc) {
        return new Element[0];
    }
}
