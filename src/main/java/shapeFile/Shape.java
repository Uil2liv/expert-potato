package main.java.shapeFile;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Shape {

    static public Shape createShape(ShapeType type, ByteBuffer buffer, int offset, int length) {
        switch(type) {
            case POLYGON:
                return  new Polygon(buffer, offset, length);
            default:
                return null;
        }
    }

    abstract public ArrayList<Element> getSvgElement(Document doc);
}
