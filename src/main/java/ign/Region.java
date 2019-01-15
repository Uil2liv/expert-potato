package main.java.ign;

import main.java.shapeFile.Shape;
import main.java.shapeFile.ShapeType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class Region {
    public int offset, length;
    private FileInputStream shapeFile;
    private Shape shape;
    public String name;
    public double minX, minY, maxX, maxY;
    public int nbParts, nbPoints;

    public Region(int offset, int length, FileInputStream shapeFile) {
        this.offset = offset;
        this.length = length;
        this.shapeFile = shapeFile;

        ByteBuffer shapesHeaderBuffer = ByteBuffer.allocate(100);
        shapesHeaderBuffer.order(ByteOrder.LITTLE_ENDIAN);
        ShapeType type = ShapeType.fromInt(shapesHeaderBuffer.getInt(32));
        this.shape = Shape.createShape(type, shapeFile, offset, length);

        ByteBuffer recordHeaderBuffer = ByteBuffer.allocate(52);
        recordHeaderBuffer.order(ByteOrder.LITTLE_ENDIAN);
        try {
            shapeFile.getChannel().position(offset);
            shapeFile.read(recordHeaderBuffer.array());
            recordHeaderBuffer.position(12);

            this.minX = recordHeaderBuffer.getDouble();
            this.minY = recordHeaderBuffer.getDouble();
            this.maxX = recordHeaderBuffer.getDouble();
            this.maxY = recordHeaderBuffer.getDouble();
            this.nbParts = recordHeaderBuffer.getInt();
            this.nbPoints = recordHeaderBuffer.getInt();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addToDoc(Element parent) {
        Document doc = parent.getOwnerDocument();
        Element region = doc.createElement("g");
        region.setAttribute("name", this.name );

        ArrayList<Element> parts = this.shape.getSvgElement(doc);

        for(Element e : parts) {
            region.appendChild(e);
        }

        doc.appendChild(region);
    }
}
