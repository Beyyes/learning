package dbscan;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.math3.stat.clustering.Cluster;
import org.apache.commons.math3.stat.clustering.DBSCANClusterer;
import org.apache.commons.math3.stat.clustering.EuclideanDoublePoint;
import org.apache.commons.math3.stat.clustering.EuclideanIntegerPoint;

public class ApacheTest {
    static double eps = 75000.0;
    static int pts = 200;
    public static void main(String[] args) throws FileNotFoundException {
        Scanner cin = new Scanner(new File("data/dataset1.dat"));
        List<EuclideanDoublePoint> points = new ArrayList<EuclideanDoublePoint>();
        int cnt = 0;
        while (cin.hasNext()) {
            double[] data = new double[2];
            data[0] = cin.nextInt();
            data[1] = cin.nextInt();
            //System.out.println(data[0] + "," + data[1]);
            points.add(new EuclideanDoublePoint(data));
        }
        // System.out.println(points.size());
        DBSCANClusterer<EuclideanDoublePoint> d = new DBSCANClusterer<>(eps, pts);
        List<Cluster<EuclideanDoublePoint>> ans = d.cluster(points);
        System.out.println(ans.size());
//        for(Cluster<EuclideanIntegerPoint> c: ans){
//            System.out.println(c.getPoints().get(0));
//            System.out.println(c.getPoints().size());
//        }  
    }
}
