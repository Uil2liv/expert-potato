package main.java;

import main.java.shapeFile.ShapeType;
import main.java.shapeFile.Shape;
import main.java.ign.Region;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class shp2svg {
    public static void main(String[] args) {
        int outputWidth = 1280, outputHeight = 720;
        double outputRatio = (double)outputWidth / (double)outputHeight;
        String query[] = {"BRETAGNE"};
        //String sourceFolder = "D:\\Users\\Yvonnick\\Downloads\\ADMIN-EXPRESS_2-0__SHP__FRA_2018-12-17\\ADMIN-EXPRESS\\1_DONNEES_LIVRAISON_2018-12-17\\ADE_2-0_SHP_LAMB93_FR\\";
        String sourceFolder = "C:\\Users\\VSHD3626\\Downloads\\ADMIN-EXPRESS_2-0__SHP__FRA_2018-12-17\\ADMIN-EXPRESS\\1_DONNEES_LIVRAISON_2018-12-17\\ADE_2-0_SHP_LAMB93_FR\\";
        String dataSet = "REGION";
        String prefix = sourceFolder + dataSet;
        ArrayList<Region> regions = new ArrayList<>();

        try {
            FileInputStream indexFile = new FileInputStream(prefix + ".shx");
            FileInputStream semanticsFile = new FileInputStream(prefix + ".dbf");
            FileInputStream shapesFile = new FileInputStream(prefix + ".shp");

            ByteBuffer semHeaderBuffer = ByteBuffer.allocate(32);
            semHeaderBuffer.order(ByteOrder.LITTLE_ENDIAN);
            semanticsFile.read(semHeaderBuffer.array());
            int semOffset = semHeaderBuffer.getShort(8);
            int semLength = semHeaderBuffer.getShort(10);

            ByteBuffer indexBuffer = ByteBuffer.allocate(8);
            ByteBuffer semanticsBuffer = ByteBuffer.allocate(semLength);
            semanticsFile.getChannel().position(semOffset);
            ByteBuffer shapeBuffer = ByteBuffer.allocate(52);
            shapeBuffer.order(ByteOrder.LITTLE_ENDIAN);


            indexFile.skip(100);
            while(indexFile.read(indexBuffer.array()) != -1) {
                indexBuffer.position(0);
                shapeBuffer.position(12);
                semanticsFile.read(semanticsBuffer.array());

                Region region = new Region(indexBuffer.getInt() * 2, indexBuffer.getInt() * 2);
                byte nameBytes[] = new byte[35];
                semanticsBuffer.position(25);
                semanticsBuffer.get(nameBytes, 0, 34);
                region.name = new String(nameBytes, StandardCharsets.UTF_8).trim();

                shapesFile.getChannel().position(region.offset);
                shapesFile.read(shapeBuffer.array());

                region.minX = shapeBuffer.getDouble();
                region.minY = shapeBuffer.getDouble();
                region.maxX = shapeBuffer.getDouble();
                region.maxY = shapeBuffer.getDouble();
                region.nbParts = shapeBuffer.getInt();
                region.nbPoints = shapeBuffer.getInt();

                regions.add(region);
            }

            ArrayList<Region> outputRegions = new ArrayList<>();
            for(Region r : regions) {
                if(Arrays.stream(query).anyMatch(r.name::equals)) {
                    outputRegions.add(r);
                }
            }

            double minX = outputRegions.stream().map(region -> region.minX).min(Comparator.comparing(Double::doubleValue)).get();
            double minY = outputRegions.stream().map(region -> region.minY).min(Comparator.comparing(Double::doubleValue)).get();
            double maxX = outputRegions.stream().map(region -> region.maxX).max(Comparator.comparing(Double::doubleValue)).get();
            double maxY = outputRegions.stream().map(region -> region.maxY).max(Comparator.comparing(Double::doubleValue)).get();
            
            double width = maxX - minX;
            double height = maxY - minY;
            double aspectRatio = width / height;

            double viewBoxWidth, viewBoxHeight, viewBoxX, viewBoxY;
            if(aspectRatio < outputRatio) {
                viewBoxWidth = height * outputRatio;
                viewBoxHeight = height;
                viewBoxX = minX - (viewBoxWidth - width) / 2;
                viewBoxY = -maxY;
            } else {
                viewBoxWidth = width;
                viewBoxHeight = width / outputRatio;
                viewBoxX = minX;
                viewBoxY = -maxY - (viewBoxHeight - height) / 2;
            }
            String viewBox = viewBoxX + " " + viewBoxY + " " + viewBoxWidth + " " + viewBoxHeight;

            Document doc;
            DocumentBuilderFactory  dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.newDocument();

            Element root = doc.createElement("svg");
            root.setAttribute("version", "1.1");
            root.setAttribute("xmlns", "http://www.w3.org/2000/svg");
            root.setAttribute("width", Integer.toString(outputWidth));
            root.setAttribute("height", Integer.toString(outputHeight));
            root.setAttribute("viewBox", viewBox);
            doc.appendChild(root);

            for(Region r : outputRegions) {
                r.addToDoc(doc);
            }

            System.out.println("Done!");
        } catch (IOException|ParserConfigurationException e) {
            e.printStackTrace();
        }
    }
    public static void convertShapeFile() {
        FileInputStream inFile;
        try  {
            inFile = new FileInputStream("D:\\Users\\Yvonnick\\Downloads\\ADMIN-EXPRESS_2-0__SHP__FRA_2018-12-17\\ADMIN-EXPRESS\\1_DONNEES_LIVRAISON_2018-12-17\\ADE_2-0_SHP_LAMB93_FR\\REGION.shp");
            ByteBuffer bb = ByteBuffer.allocate(100);

            inFile.read(bb.array());

            bb.position(24);
            int fileLength = bb.getInt() * 2;

            bb.order(ByteOrder.LITTLE_ENDIAN);

            bb.position(32);
            ShapeType type = ShapeType.fromInt(bb.getInt());
            System.out.println("ShapeType: " + type);

            double minX = bb.getDouble();
            System.out.println("Min X: " + minX);

            double minY = bb.getDouble();
            System.out.println("Min Y: " + minY);

            double maxX = bb.getDouble();
            System.out.println("Max X: " + maxX);

            double maxY = bb.getDouble();
            System.out.println("Max Y: " + maxY);

            double width = maxX - minX;
            double height = maxY - minY;

            ArrayList<Shape> shapes = new ArrayList<>();
            ByteBuffer hb = ByteBuffer.allocate(8);
            Shape shape;
            int id, length;
            while (inFile.read(hb.array()) != -1) {
                hb.position(0);
                id = hb.getInt();
                length = hb.getInt() * 2;
                shape = Shape.createShape(type, id, length);

                shape.read(inFile);

                shapes.add(shape);
            }


            Document doc;

            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.newDocument();

                Element root = doc.createElement("svg");
                root.setAttribute("version", "1.1");
                root.setAttribute("xmlns", "http://www.w3.org/2000/svg");
                root.setAttribute("width", "1280");
                root.setAttribute("height", "720");
                root.setAttribute("viewBox", minX + " " + (-maxY) + " " + width + " " + height);
                doc.appendChild(root);

                Element els[];
                for (Shape s : shapes) {
                    els = s.createElements(doc);
                    for (Element el : els) {
                        root.appendChild(el);
                    }
                }

                try {
                    Transformer tr = TransformerFactory.newInstance().newTransformer();
                    tr.setOutputProperty(OutputKeys.INDENT, "yes");
                    tr.setOutputProperty(OutputKeys.METHOD, "xml");
                    tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

                    tr.transform(new DOMSource(doc), new StreamResult(new FileOutputStream("svgSample.svg")));
                } catch (TransformerException|FileNotFoundException e) {
                    e.printStackTrace();
                }

            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}