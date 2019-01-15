package main.java.shapeFile;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

public class Part {
    private DoubleBuffer buffer;

    public Part(ByteBuffer buffer) {
        this.buffer = buffer.asDoubleBuffer();
    }
}
