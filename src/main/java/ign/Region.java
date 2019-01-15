package main.java.ign;

import main.java.shapeFile.Part;
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


    public Region(int offset, int length, FileInputStream shapeFile) {
        this.offset = offset;
        this.length = length;
        this.shapeFile = shapeFile;

        ByteBuffer shapesHeaderBuffer = ByteBuffer.allocate(100);
        shapesHeaderBuffer.order(ByteOrder.LITTLE_ENDIAN);
        try {
            shapeFile.getChannel().position(0);
            shapeFile.read(shapesHeaderBuffer.array());
            ShapeType type = ShapeType.fromInt(shapesHeaderBuffer.getInt(32));
            ByteBuffer recordBuffer = ByteBuffer.allocate(length + 8);


            recordBuffer.order(ByteOrder.LITTLE_ENDIAN);

            shapeFile.getChannel().position(offset);
            shapeFile.read(recordBuffer.array());
            recordBuffer.position(12);

            this.minX = recordBuffer.getDouble();
            this.minY = recordBuffer.getDouble();
            this.maxX = recordBuffer.getDouble();
            this.maxY = recordBuffer.getDouble();
            this.shape = Shape.createShape(type, recordBuffer, offset, length);

        } catch(IOException e) {
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

        parent.appendChild(region);
    }
}
