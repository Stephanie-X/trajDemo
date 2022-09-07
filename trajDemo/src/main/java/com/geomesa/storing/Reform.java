package com.geomesa.storing;

import com.geomesa.storing.utils.WKTUtils;
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


/**
 * 对存入postgis的数据进行预处理
 */
public class Reform {
    public static void main(String[] args) throws IOException {
        System.out.println("start to reform a new text");
      //  String pathIn = "D:\\aXsy\\TDrive\\out\\segment_014";
        String pathIn = "D:\\aXsy\\T-Drive trajectory data sample\\out";
        String path = "D:\\aXsy\\T-Drive trajectory data sample\\pgData";
     //   String path = "D://logs";  // 输出路径
        String title = "postgisData";
        /* 写入Txt文件 */
        File mkdirsName = new File(path);// 相对路径，如果没有则要建立一个新的output。txt文件
        if(!mkdirsName.exists()){
            mkdirsName.mkdirs();
        }
        File writename = new File(path+"\\"+title+".txt");
        // 判断文件是否存在，不存在即新建
        // 存在即根据操作系统添加换行符
//        if(!writename.exists()) {
//            writename.createNewFile(); // 创建新文件
//        } else {
//            String osName = System.getProperties().getProperty("os.name");
//            if (osName.equals("Linux")) {
//                content = "\r" + content;
//            } else {
//                content = "\r\n" + content;
//            }
//        }
        // 如果是在原有基础上写入则append属性为true，默认为false
   //     BufferedWriter out = new BufferedWriter(new FileWriter(writename,true));
        File file = new File(pathIn);
        ArrayList<File> arr = getFileList(file);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int n = 0;
        for(File f: arr){
            FileReader fileReader = new FileReader(f.toString());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String contentLine = bufferedReader.readLine();
            while (contentLine != null){
                String content = lineContent(contentLine);
                // 写入新的text中
                if(!writename.exists()) {
                    writename.createNewFile(); // 创建新文件
                } else {
                    String osName = System.getProperties().getProperty("os.name");
                    if (osName.equals("Linux")) {
                        content = "\r" + content;
                    } else {
                        content = "\r\n" + content;
                    }
                }
                BufferedWriter out = new BufferedWriter(new FileWriter(writename,true)); //如果是在原有基础上写入则append属性为true，默认为false
                out.write(content); // 写入TXT
                out.flush(); // 把缓存区内容压入文件
                out.close();
                n++;
                contentLine = bufferedReader.readLine();
            }

            bufferedReader.close();
            System.out.println(n + " data has been written!");
        }

        System.out.println("totally " + n + " data has been written!");

        }


    public static ArrayList<File> getFileList(File file){
        ArrayList<File> listfiles = new ArrayList<File>();
        if(file.isFile()){
            if(! file.toString().contains("crc") && ! file.toString().contains("SUCCESS")){
                listfiles.add(file);
            }
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

    public static String lineContent(String contentLine){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String[] s = contentLine.split("_");
        String trajID = s[0];  // 获取id
        String[] strPoints = contentLine.split("-");
        String points = strPoints[1];
        Geometry read = WKTUtils.read(points);
        String multipoint = read.toString();
        int numGeometries = read.getNumGeometries();
        String startPoint = read.getGeometryN(0).toString();  // 起始Point
        String endPoint = read.getGeometryN(numGeometries-1).toString();  // 最后一个Point
        // 获取起始时间点
        int start = points.indexOf('(') + 2;
        int end = points.indexOf(')') ;
        String one = points.substring(start,end);
        String[] s1 = one.split(" ");
        String sTime = s1[2];
        Long lstart = Long.valueOf(sTime);
        String startTime = formatter.format(lstart);
        //    Date startTime = formatter.parse(lstime);
        // 获取终止时间点
        int send = points.lastIndexOf('(') + 1;
        int eend = points.lastIndexOf(')') - 1;
        String endP = points.substring(send,eend);
        String[] s2 = endP.split(" ");
        String eTime = s2[2];
        Long aLong = Long.valueOf(eTime);
        String endTime = formatter.format(aLong);
        return trajID + ";" + startTime + ";" + endTime + ";" + startPoint + ";" + endPoint + ";" + multipoint;
    }

}
