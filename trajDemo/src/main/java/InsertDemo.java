import com.geomesa.storing.utils.WKTUtils;
import org.geotools.data.*;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.filter.identity.FeatureIdImpl;
import org.geotools.filter.text.cql2.CQLException;
import org.geotools.filter.text.ecql.ECQL;
import org.geotools.util.factory.Hints;
import org.locationtech.geomesa.utils.interop.SimpleFeatureTypes;

import org.locationtech.jts.geom.Geometry;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.opengis.filter.sort.SortBy;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class InsertDemo {

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
    public static void readAndWriteData(DataStore datastore, SimpleFeatureType sft, String path) throws IOException, ParseException {
        System.out.println("start writing");
        File file = new File(path);
        ArrayList<File> arr = getFileList(file);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        int n = 0;
        for(File f : arr){
            FileReader fileReader = new FileReader(f.toString());
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String contentLine = bufferedReader.readLine();
      //      SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);
            while (contentLine != null){
                SimpleFeatureBuilder builder = new SimpleFeatureBuilder(sft);
                String[] s = contentLine.split("_");
                String trajID = s[0];
                String[] strPoints = contentLine.split("-");
                String points = strPoints[1];
                Geometry read = WKTUtils.read(points);
                String multipoint = read.toString();
                int numGeometries = read.getNumGeometries();
                String startPoint = read.getGeometryN(0).toString();
                String endPoint = read.getGeometryN(numGeometries-1).toString();
                // 获取起始时间点
                int start = points.indexOf('(') + 2;
                int end = points.indexOf(')') ;
                String one = points.substring(start,end);
                String[] s1 = one.split(" ");
                String sTime = s1[2];
                Long lstart = Long.valueOf(sTime);
                String lstime = formatter.format(lstart);
                Date startTime = formatter.parse(lstime);
                // 获取终止时间点
                int send = points.lastIndexOf('(') + 1;
                int eend = points.lastIndexOf(')') - 1;
                String endP = points.substring(send,eend);
                String[] s2 = endP.split(" ");
                String eTime = s2[2];
                Long aLong = Long.valueOf(eTime);
                String format = formatter.format(aLong);
                Date endTime = formatter.parse(format);
                // String spec = "trajID:String:index=true,startTime:Date,endTime:Date,*geom:MultiPoint:srid=4326,startPoint:Point:srid=4326,endPoint:Point:srid=4326
                builder.set("trajID",trajID);
                builder.set("startTime",startTime);
                builder.set("endTime",endTime);
                builder.set("geom",multipoint);
                builder.set("startPoint",startPoint);
                builder.set("endPoint",endPoint);
                SimpleFeature feature = builder.buildFeature(null);
                writeAFeature(datastore, sft, feature);
                n++;
                contentLine = bufferedReader.readLine();
            }
            bufferedReader.close();
            System.out.println(n + " data has been written!");

        }


        System.out.println("totally " + n + " data has been written!");

    }

    public static void writeAFeature(DataStore datastore, SimpleFeatureType sft,  SimpleFeature feature) throws IOException {
        // use try-with-resources to ensure the writer is closed
        try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                     datastore.getFeatureWriterAppend(sft.getTypeName(), Transaction.AUTO_COMMIT)) {
            // using a geotools writer, you have to get a feature, modify it, then commit it
            // appending writers will always return 'false' for haveNext, so we don't need to bother checking
            SimpleFeature toWrite = writer.next();

            // copy attributes
            toWrite.setAttributes(feature.getAttributes());

            // if you want to set the feature ID, you have to cast to an implementation class
            // and add the USE_PROVIDED_FID hint to the user data
            // ((FeatureIdImpl) toWrite.getIdentifier()).setID(feature.getID());
            //toWrite.getUserData().put(Hints.USE_PROVIDED_FID, Boolean.TRUE);

            // alternatively, you can use the PROVIDED_FID hint directly
            toWrite.getUserData().put(Hints.PROVIDED_FID, feature.getID());

            // if no feature ID is set, a UUID will be generated for you

            // make sure to copy the user data, if there is any
            toWrite.getUserData().putAll(feature.getUserData());

            // write the feature
            writer.write();
        }
        //System.out.println("Written One Data");
    }


    public static void writeFeatures(DataStore datastore, SimpleFeatureType sft, List<SimpleFeature> features) throws IOException {
        if (features.size() > 0) {
            System.out.println("Writing test data");
            // use try-with-resources to ensure the writer is closed
            try (FeatureWriter<SimpleFeatureType, SimpleFeature> writer =
                         datastore.getFeatureWriterAppend(sft.getTypeName(), Transaction.AUTO_COMMIT)) {
                for (SimpleFeature feature : features) {
                    // using a geotools writer, you have to get a feature, modify it, then commit it
                    // appending writers will always return 'false' for haveNext, so we don't need to bother checking
                    SimpleFeature toWrite = writer.next();

                    // copy attributes
                    toWrite.setAttributes(feature.getAttributes());

                    // if you want to set the feature ID, you have to cast to an implementation class
                    // and add the USE_PROVIDED_FID hint to the user data
                    ((FeatureIdImpl) toWrite.getIdentifier()).setID(feature.getID());
                    toWrite.getUserData().put(Hints.USE_PROVIDED_FID, Boolean.TRUE);

                    // alternatively, you can use the PROVIDED_FID hint directly
                    // toWrite.getUserData().put(Hints.PROVIDED_FID, feature.getID());

                    // if no feature ID is set, a UUID will be generated for you

                    // make sure to copy the user data, if there is any
                    toWrite.getUserData().putAll(feature.getUserData());

                    // write the feature
                    writer.write();
                }
            }
            System.out.println("Wrote " + features.size() + " features");
            System.out.println();
        }
    }

    public static void queryWithFq(DataStore datastore, String sftTypeName, float startFq, float endFq) throws IOException {
        String range = "fq BETWEEN "+startFq+" AND "+endFq;
        System.out.println("Running query " + range);
        try {
            Query query = new Query(sftTypeName, ECQL.toFilter(range));

            if (query.getPropertyNames() != null) {
                System.out.println("Returning attributes " + Arrays.asList(query.getPropertyNames()));
            }
            if (query.getSortBy() != null) {
                SortBy sort = query.getSortBy()[0];
                System.out.println("Sorting by " + sort.getPropertyName() + " " + sort.getSortOrder());
            }

            // submit the query, and get back an iterator over matching features
            // use try-with-resources to ensure the reader is closed
            try (FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                         datastore.getFeatureReader(query, Transaction.AUTO_COMMIT)) {
                // loop through all results, only print out the first 10
                // print all
                int n = 0;
                while (reader.hasNext()) {
                    SimpleFeature feature = reader.next();
                    System.out.println(String.format("%02d", n) + " " + DataUtilities.encodeFeature(feature));
                    n++;
                }
                System.out.println();
                System.out.println("Returned " + n + " total features");
                System.out.println();
            }
        } catch (CQLException e) {
            e.printStackTrace();
        }

    }


    public static void queryFeature(DataStore datastore, String sftTypeName, String ecqlStr) throws IOException {
        System.out.println("Running query " + ecqlStr);
        try {
            Query query = new Query(sftTypeName, ECQL.toFilter(ecqlStr));

            if (query.getPropertyNames() != null) {
                System.out.println("Returning attributes " + Arrays.asList(query.getPropertyNames()));
            }
            if (query.getSortBy() != null) {
                SortBy sort = query.getSortBy()[0];
                System.out.println("Sorting by " + sort.getPropertyName() + " " + sort.getSortOrder());
            }

            // submit the query, and get back an iterator over matching features
            // use try-with-resources to ensure the reader is closed
            try (FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                         datastore.getFeatureReader(query, Transaction.AUTO_COMMIT)) {
                // loop through all results, only print out the first 10
                // print all
                int n = 0;
                while (reader.hasNext()) {
                    SimpleFeature feature = reader.next();
   //                 System.out.println(String.format("%02d", n) + " " + DataUtilities.encodeFeature(feature));
                    if (n++ < 10) {
                        // use geotools data utilities to get a printable string
                        System.out.println(String.format("%02d", n) + " " + DataUtilities.encodeFeature(feature));
                    } else if (n == 10) {
                        System.out.println("...");
                    }
                }
                System.out.println();
                System.out.println("Returned " + n + " total features");
                System.out.println();
            }
        } catch (CQLException e) {
            e.printStackTrace();
        }

    }

    public static void queryAllFeature(DataStore datastore, String sftTypeName, String ecqlStr) throws IOException {
        System.out.println("Running query " + ecqlStr);
        try {
            Query query = new Query(sftTypeName, ECQL.toFilter(ecqlStr));

            if (query.getPropertyNames() != null) {
                System.out.println("Returning attributes " + Arrays.asList(query.getPropertyNames()));
            }
            if (query.getSortBy() != null) {
                SortBy sort = query.getSortBy()[0];
                System.out.println("Sorting by " + sort.getPropertyName() + " " + sort.getSortOrder());
            }

            // submit the query, and get back an iterator over matching features
            // use try-with-resources to ensure the reader is closed
            try (FeatureReader<SimpleFeatureType, SimpleFeature> reader =
                         datastore.getFeatureReader(query, Transaction.AUTO_COMMIT)) {
                // loop through all results, only print out the first 10
                // print all
                int n = 0;
                while (reader.hasNext()) {
                    SimpleFeature feature = reader.next();
                    System.out.println(String.format("%02d", n) + " " + DataUtilities.encodeFeature(feature));
                    n++;

                }
                System.out.println();
                System.out.println("Returned " + n + " total features");
                System.out.println();
            }
        } catch (CQLException e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param args: 0, write/read; 1, table name; 2, type name; 3, data file path for 'write', ECQL string for 'read' or 'readAll'; 4(option), index types, e.g., s2,s3:pt:dtg,attr:fq:dtg,
     * @throws IOException
     * @throws ParseException
     */
    public static void main(String[] args) throws IOException, ParseException {
        Map<String, Serializable> parameters = new HashMap<>();
        parameters.put("hbase.catalog", args[1]);
        DataStore dataStore = DataStoreFinder.getDataStore(parameters);

        if(args[0].equals("write")){
            String spec = "trajID:String:index=true,startTime:Date,endTime:Date,*geom:MultiPoint:srid=4326,startPoint:Point:srid=4326,endPoint:Point:srid=4326";
            SimpleFeatureType sft = SimpleFeatureTypes.createType(args[2], spec);
            dataStore.createSchema(sft);
            if(args.length == 5)
                sft.getUserData().put("geomesa.indices.enabled", args[4]);
            String path = args[3];
          //  BufferedReader br = new BufferedReader(new FileReader(args[3]));
            readAndWriteData(dataStore, sft, path);
        }else if(args[0].equals("read")){
            /*FeatureSource<SimpleFeatureType, SimpleFeature> source = dataStore.getFeatureSource(args[2]);
            SimpleFeatureType sft = source.getSchema();*/
            queryFeature(dataStore, args[2], args[3]);

        }else if(args[0].equals("readAll")){
            queryAllFeature(dataStore, args[2], args[3]);

        } else
            System.out.println("Illegal input parameters!");

        /*
        queryWithFq(dataStore, sft.getTypeName(), (float)600, (float)700);*/

    }
}
