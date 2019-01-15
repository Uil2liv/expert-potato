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
    private FileInputStream shapeFile;
    private int offset;
    private int length;

    Polygon(FileInputStream shapeFile, int offset, int length) {
        this.shapeFile = shapeFile;
        this.offset = offset;
        this.length = length;
    }

    @Override
    public ArrayList<Element> getSvgElement(Document doc) {
        ArrayList<Element> parts = new ArrayList<>();
        ByteBuffer shapeBuffer = ByteBuffer.allocate(length);
        shapeBuffer.order(ByteOrder.LITTLE_ENDIAN);
        try {
            shapeFile.getChannel().position(offset);
            shapeFile.read(shapeBuffer.array());

            shapeBuffer.position(36);
            int nbParts = shapeBuffer.getInt();
            int nbPoints = shapeBuffer.getInt();
            IntBuffer partsBuffer = shapeBuffer.slice().asIntBuffer();
            partsBuffer.limit(nbParts);

            for(int i = 0; i < nbParts; i++) {

            }

        } catch(IOException e) {
            e.printStackTrace();
        }

        return parts;
    }
}
