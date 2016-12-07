package dbscan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * 
 * @author FIT
 *
 *  374464    284441
 *  453320    606542
 */
public class dbscan {
    static int MAXN = 6000;
    
    static double eps = 75000.0;
    static int minPts = 200;
    static List<Point> points = new ArrayList<Point>();
    static int[] vis = new int[MAXN];
    static int[] flag = new int[MAXN];
    static List<Integer> noises = new ArrayList<Integer>();
    //Map<Integer, > ret = new HashMap<Set<Integer>>(); 
    static int cluster = 0;
    
    public static void init() throws FileNotFoundException {
        //Scanner cin = new Scanner(new File("data/small"));
        Scanner cin = new Scanner(new File("data/dataset1.dat"));
        int cnt = 0;
        while (cin.hasNext()) {
            points.add(new Point(cin.nextDouble(), cin.nextDouble(), cnt++));
        }
        System.out.println(points.size());
    }
    
    public static void main(String[] args) throws IOException {
        init();
        for (int i = 0;i < points.size();i++) {
            if (vis[i] == 1)
                continue;
            vis[i] = 1;
            List<Integer> neibors = regionQuery(i);
//            System.out.println(i + "===" + neibors.size());
//            for (int x : neibors) {
//                System.out.print(x + " ");
//            }
            if (neibors.size() < minPts) { // noise
                noises.add(i);
            } else {
                // Set<Integer> s = new HashSet<Integer>();
                System.out.println("core:" + i + " " + neibors.size());
                ++ cluster;
                expand(i, neibors);
            }
        }
        
        System.out.println(cluster);
//        System.out.println("noise num:" + noises.size());
//        for (int i = 0;i < 25;i++) {
//            System.out.println(i + "," +flag[i]);
//        }
    }
    
    public static void expand(int p, List<Integer> nbs) {
        flag[p] = cluster;
        for (int i = 0;i < nbs.size();i++) {
            if(vis[nbs.get(i)] == 0) {
                vis[nbs.get(i)] = 1;
                List<Integer> tmp = regionQuery(nbs.get(i));
                if (tmp.size() > minPts)
                    nbs.addAll(tmp);
            }
            if(flag[nbs.get(i)]==0) {
                flag[nbs.get(i)] = cluster;
            }
        }
    }
    
    public static List<Integer> regionQuery(int i) {
        List<Integer> ans = new ArrayList<Integer>();
        for (int j = 0;j < points.size();j++) {
            if(getDistance(points.get(i), points.get(j)) <= eps) {
                ans.add(j);
            }
        }
        return ans;
    }
    
    public static double getDistance(Point p, Point q) {
        double dx = p.getX() - q.getX();
        double dy = p.getY() - q.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public static class Point {
        double x;
        double y;
        int num;
        
        public Point(double a, double b, int num) {
            this.x = a;
            this.y = b;
            this.num = num;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }
    }
}




