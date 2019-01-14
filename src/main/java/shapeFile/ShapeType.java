package main.java.shapeFile;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public enum ShapeType {
    NULL_SHAPE(0),
    POINT(1),
    POLYLINE(3),
    POLYGON(5),
    MULTIPOINT(8),
    POINT_Z(11),
    POLYLINE_Z(13),
    POLYGON_Z(15),
    MULTIPOINT_Z(18),
    POINT_M(21),
    POLYLINE_M(23),
    POLYGON_M(25),
    MULTIPOINT_M(28),
    MULTIPATCH(31);

    private final int value;

    ShapeType(int val) {
        this.value = val;
    }

    private final static Map<Integer, ShapeType> intMap = new HashMap<>();
    static {
        for (ShapeType type : ShapeType.values()) {
            intMap.put(type.value, type);
        }
    }
    public static final ShapeType fromInt(int i) {
        return intMap.get(i);
    }
}