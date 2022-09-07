package com.geomesa.storing.utils;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

public class WKTUtils {
    public static Geometry read(String WKTgeometry) {
        WKTReader reader = new WKTReader();
        try {
            return reader.read(WKTgeometry);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
