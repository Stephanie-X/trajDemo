import com.geomesa.storing.utils.WKTUtils;

import java.io.*;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.locationtech.jts.algorithm.HCoordinate;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTWriter;

public class testDemo {
    public static void main(String[] args) throws IOException, ParseException {
        FileReader fileReader = new FileReader("D:\\aXsy\\TDrive\\out\\segment_06\\part-00000");
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String contentLine = bufferedReader.readLine();
        while (contentLine != null){
            // 将每一行的内容转换为Trajectory类
            String[] s = contentLine.split("_");
            String trajID = s[0];
            String[] strPoints = contentLine.split("-");
            String points = strPoints[1];
            System.out.println(trajID);
            System.out.println(points);

            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Geometry read = WKTUtils.read(points);  // 这样转完就没有时间了
            String multipoint = read.toString();
            System.out.println("%%%%%%%%%%%%%%%%");
            System.out.println(multipoint);
            System.out.println("*************");
            System.out.println(read);
            System.out.println("************");
            int numGeometries = read.getNumGeometries();
            System.out.println(numGeometries);
            System.out.println(read.getGeometryN(0));
            System.out.println(read.getGeometryN(numGeometries-1));  // 可以拿到起始位置和终止位置，但是没有起始时间点和终止时间点
            // 获取起始时间点
            int start = points.indexOf('(') + 2;
            int end = points.indexOf(')') ;
            String one = points.substring(start,end);
            System.out.println(one);
            String[] s1 = one.split(" ");
            System.out.println(s1[2]);
            Long lstart = Long.valueOf(s1[2]);
            String lstime = formatter.format(lstart);
            System.out.println(lstime);
     //       Date startTime = formatter.parse(lstime);

            // 获取终止时间点
            int send = points.lastIndexOf('(') + 1;
            int eend = points.lastIndexOf(')') - 1;
            String endPoint = points.substring(send,eend);
            System.out.println(endPoint);
            String[] s2 = endPoint.split(" ");
            String endTime = s2[2];
            System.out.println(endTime);
            Long aLong = Long.valueOf(endTime);

            String format = formatter.format(aLong);
            System.out.println(format);
            Date parse = formatter.parse(format);
            System.out.println(parse);
//
//
//            //  MultiPoint multiPoint = new MultiPoint(WKTUtils.read(points)., new PrecisionModel(),4326);
//
//
//            //System.out.println(contentLine);
////            String[] zs = contentLine.split("Z");
////            // 还需要获取轨迹的起始点和终止点的时间，在时间存入的时候要是date类型
////            String trj = zs[1];
////            // 最好还要获得轨迹的起始点和终止点的gps坐标
////            // 另外在查询过程中将时间转换成标准格式
////            System.out.println(zs[1]);
            contentLine = bufferedReader.readLine();

//            contentLine = bufferedReader.readLine();
       }
        bufferedReader.close();
//
////        System.out.println("*************");
////        String t = "2008-02-02 13:37:30";
////        Timestamp timestamp = Timestamp.valueOf(t);
////        System.out.println(timestamp);
////        System.out.println("*********");
////        System.out.println(timestamp.getTime());
////        long time = timestamp.getTime();
////        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
////        String format = formatter.format(time);
////        System.out.println("**********");
////        System.out.println(format);
////        Date parse = formatter.parse(format);
////        System.out.println("**************");
////        System.out.println(parse);
//
////        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
////        Date parse = formatter.parse(t);
////        System.out.println(parse);
//
//
   }
}
