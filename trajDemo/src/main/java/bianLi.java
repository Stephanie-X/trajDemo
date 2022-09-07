import com.geomesa.storing.utils.WKTUtils;
import com.google.inject.internal.cglib.core.$LocalVariablesSorter;
import org.geotools.data.DataStore;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class bianLi {

//    public static void readAndWriteData(DataStore datastore, SimpleFeatureType sft, String path) throws IOException, ParseException {
//
//        FileReader fileReader = new FileReader(path);
//        BufferedReader bufferedReader = new BufferedReader(fileReader);
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String contentLine = bufferedReader.readLine();
//        int n = 0;
//        SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);
//        while (contentLine != null){
//            // SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);
//            String[] s = contentLine.split("_");
//            String trajID = s[0];
//            String[] strPoints = contentLine.split("-");
//            String points = strPoints[1];
//            Geometry read = WKTUtils.read(points);
//            String multipoint = read.toString();
//            int numGeometries = read.getNumGeometries();
//            String startPoint = read.getGeometryN(0).toString();
//            String endPoint = read.getGeometryN(numGeometries-1).toString();
//            // 获取起始时间点
//            int start = points.indexOf('(') + 2;
//            int end = points.indexOf(')') ;
//            String one = points.substring(start,end);
//            String[] s1 = one.split(" ");
//            String sTime = s1[2];
//            Long lstart = Long.valueOf(sTime);
//            String lstime = formatter.format(lstart);
//            Date startTime = formatter.parse(lstime);
//            // 获取终止时间点
//            int send = points.lastIndexOf('(') + 1;
//            int eend = points.lastIndexOf(')') - 1;
//            String endP = points.substring(send,eend);
//            String[] s2 = endP.split(" ");
//            String eTime = s2[2];
//            Long aLong = Long.valueOf(eTime);
//            String format = formatter.format(aLong);
//            Date endTime = formatter.parse(format);
//            // String spec = "trajID:String:index=true,startTime:Date,endTime:Date,*geom:MultiPoint:srid=4326,startPoint:Point:srid=4326,endPoint:Point:srid=4326
//            builder.set("trajID",trajID);
//            builder.set("startTime",startTime);
//            builder.set("endTime",endTime);
//            builder.set("geom",multipoint);
//            builder.set("startPoint",startPoint);
//            builder.set("endPoint",endPoint);
//            SimpleFeature feature = builder.buildFeature(null);
//            writeAFeature(datastore, sft, feature);
//            n++;
//            contentLine = bufferedReader.readLine();
//        }
//        bufferedReader.close();
//        System.out.println(n + " data has been written!");
//
//    }


    public static void main(String[] args) {
        String path = args[0];
        File file = new File(path);
        ArrayList<File> arr = getFileList(file);
        for(File f : arr){
            System.out.println(f.toString());
        }

    }

    public static  ArrayList<File> getFileList(File file){
        ArrayList<File> listfiles = new ArrayList<File>();
        if(file.isFile()){
            listfiles.add(file);
        }else if(file.isDirectory()){
            for(File f : file.listFiles()){
                if(f.isDirectory()){
                    listfiles.addAll(getFileList(f));
                }else if(f.isFile()){
                    if(! f.toString().contains("crc") && ! f.toString().contains("SUCCESS")){
                        listfiles.add(f);
                    }
                    }
                }
            }

        return listfiles;
    }



}
