package main.java.shapeFile;

import java.io.FileInputStream;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class Shape {

    static public Shape createShape(ShapeType type, int id, int length) {
        switch(type) {
            case POLYGON:
                return  new Polygon(id, length);
            default:
                return null;
        }
    }

    public abstract void read (FileInputStream file);

    public abstract Element[] createElements(Document doc);
}
