package dbscan;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;

/**
 * 
 * @author FIT
 *
 *  374464    284441
 *  453320    606542
 */
public class dbscan {
    static int MAXN = 6000;
    
    static double eps = 50000.0;
    static int minPts = 200;
    static List<Point> points = new ArrayList<Point>();
    static int[] vis = new int[MAXN];
    static int[] flag = new int[MAXN];
    static List<Integer> noises = new ArrayList<Integer>();
    static Map<Integer, HashSet<Integer>> ansCluster = new HashMap<Integer, HashSet<Integer>>(); 
    static int clusterNum[] = new int[MAXN];
    static Map<Integer, HashSet<Integer>> calcCluster = new HashMap<Integer, HashSet<Integer>>();
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
    
    public static void purityCalc() throws FileNotFoundException {
        
        Scanner cin = new Scanner(new File("data/dataset1-label.dat"));
        int cnt = 0;
        while (cin.hasNext()) {
            int num = cin.nextInt();
            clusterNum[num]++;
            if (ansCluster.containsKey(num)) {
                ansCluster.get(num).add(cnt);
            } else {
                ansCluster.put(num, new HashSet<Integer>());
                ansCluster.get(num).add(cnt);
            }
            cnt++;
        }
        System.out.println("标准聚类cluster size: " + ansCluster.size());
        
        for (int i = 0;i < points.size();i++) {
            if(flag[i] != 0) {
                if(calcCluster.containsKey(flag[i])) {
                    calcCluster.get(flag[i]).add(i);
                } else {
                    calcCluster.put(flag[i], new HashSet<Integer>());
                    calcCluster.get(flag[i]).add(i);
                }
            }
        }
        System.out.println("dbscan cluster size: " + calcCluster.size());
//        for (Entry<Integer, HashSet<Integer>> entry : calcCluster.entrySet()) {
//            System.out.println(entry.getValue().size());
//        }
        
        double purity = 0, all = 0.0000;
        for (Entry<Integer, HashSet<Integer>> entry : calcCluster.entrySet()) {
            HashSet<Integer> s = entry.getValue();
            all += s.size();
            int max = -1;
            for (Entry<Integer, HashSet<Integer>> okEntry : ansCluster.entrySet()) {
                int num = 0;
                for (int i : s) {
                    if(okEntry.getValue().contains(i)) {
                        num++;
                    }
                }
                max = Math.max(max, num);
            }
            purity += max;
        }
        if(all != 0)
        System.out.println("purity:" + purity/all);
    }
    
    public static void FCalc() {
        double fscore = 0;
        int tp = 0, fp = 0, fn = 0, tpfp=0;
        Map<Integer, HashMap<Integer, Integer>> countMap = new HashMap<Integer, HashMap<Integer, Integer>>();
        
        for (Entry<Integer, HashSet<Integer>> calcEntry : calcCluster.entrySet()) {
            HashSet<Integer> s = calcEntry.getValue();
            // System.out.println(s.size());
            tpfp += C(s.size());
            countMap.put(calcEntry.getKey(), new HashMap<Integer, Integer>());
            for (Entry<Integer, HashSet<Integer>> okEntry : ansCluster.entrySet()) {
                int cnt = 0;
                for (int i : s) {
                    if(okEntry.getValue().contains(i)) {
                        cnt++;
                        clusterNum[okEntry.getKey()]--;
                    }
                }
                tp += C(cnt);
                if(cnt != 0) {
                    HashMap<Integer, Integer> tmp = countMap.get(calcEntry.getKey());
                    if(!tmp.containsKey(okEntry.getKey())) {
                        tmp.put(okEntry.getKey(), cnt);
                    } else {
                        tmp.put(okEntry.getKey(), tmp.get(okEntry.getKey())+cnt);
                    }
                    countMap.put(calcEntry.getKey(), tmp);
                }
            }
        }
        
        fp = tpfp - tp;
        
        for (Entry<Integer, HashSet<Integer>> calcEntry : calcCluster.entrySet()) {
            int key = calcEntry.getKey();
            // System.out.println(key);
            HashMap<Integer, Integer> tmp = countMap.get(key);
            for(Entry<Integer, Integer> entry : tmp.entrySet()) {
                // System.out.println(entry.getKey() + "," + entry.getValue());
            }
        }
        
        for (Entry<Integer, HashMap<Integer, Integer>> entry : countMap.entrySet()) {
            for (Entry<Integer, Integer> e : entry.getValue().entrySet()) {
                for (Entry<Integer, HashMap<Integer, Integer>> entry2 : countMap.entrySet()) {
                    for (Entry<Integer, Integer> e2 : entry2.getValue().entrySet()) {
                        if(e.getKey().equals(e2.getKey())) {
                            fn += e.getValue() * e2.getValue();
                        }
                    }
                }
                // 记录没有被分类的标号
                if(clusterNum[e.getValue()] != 0) {
                    fn += e.getValue() * clusterNum[e.getValue()];
                }
            }
        }
        
        double p = (double)tp/(double)(tp+fp), r = (double)tp/(double)(tp+fn);
        fscore = 2*p*r/(p+r);
        System.out.println("tp:"+tp + ",fp:" + fp + ",fn:" + fn);
        System.out.println("fscore value : " + fscore);
    }
    
    public static int C(int n) {
        return n*(n-1)/2;
    }
    
    public static void main(String[] args) throws IOException {
        init();
        for (int i = 0;i < points.size();i++) {
            if (vis[i] == 1)
                continue;
            vis[i] = 1;
            List<Integer> neibors = regionQuery(i);
            //System.out.println("X:" + i + ", neibors size:" + neibors.size());
            if (neibors.size() < minPts) { // noise
                noises.add(i);
            } else {
                // Set<Integer> s = new HashSet<Integer>();
                System.out.println("core:" + i + ", neibors size:" + neibors.size());
                ++ cluster;
                expand(i, neibors);
            }
        }
        
        System.out.println("all result clusters number :" + cluster);
        purityCalc();
        FCalc();
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
        //return Math.abs(dx) + Math.abs(dy);
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




