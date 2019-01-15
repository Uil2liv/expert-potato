package main.java.shapeFile;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

public class Part {
    public DoubleBuffer buffer;

    public Part(ByteBuffer buffer) {
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        this.buffer = buffer.asDoubleBuffer();
    }
}
