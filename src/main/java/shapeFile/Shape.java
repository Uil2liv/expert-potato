package main.java.shapeFile;

import java.io.FileInputStream;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Shape {

    static public Shape createShape(ShapeType type, FileInputStream shapeFile, int offset, int length) {
        switch(type) {
            case POLYGON:
                return  new Polygon(shapeFile, offset, length);
            default:
                return null;
        }
    }

    abstract public ArrayList<Element> getSvgElement(Document doc);
}
