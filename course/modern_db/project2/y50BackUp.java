package skyline;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 *
 * 将UnitGroup类直接转化为HashSet
 * 
 * fatherRelation ArrayList->HashSet
 * 
 * 
 * 
 * @author CGF
 *
 *
 */
public class test2 {

    //
    static List<Point> points = null;
    static int K = 2;
    static Map<Integer, HashSet<Integer>> fatherRelation = new HashMap<Integer, HashSet<Integer>>();
    static Map<Integer, ArrayList<Integer>> layerMap = new HashMap<Integer, ArrayList<Integer>>();

    static List<Point> rePoints = new ArrayList<Point>();
    static int maxLayer = 0;
    static ArrayList<Integer> oneUnitGroup = new ArrayList<Integer>();
    static int arrayMaxn = 10005;
    static int num[] = new int[arrayMaxn];
    static boolean OUTFLAG = !false;
    //tatic String filePath = "data/test.txt";
    static String filePath = "data/T2.txt";

    public static void main(String[] args) throws IOException {

        points = loadFileData(filePath);

        long starTime = System.currentTimeMillis();
        Collections.sort(points);
        if (OUTFLAG) {
            for (int i = 0; i < points.size(); i++) {
                num[i] = points.get(i).number;
            }
        }

        // two dimension : binary search | higher
        // buildDSG();
        buildDSG2Dimension();

        // unit group reordering
        // oneUnitGroup存储排序后的下标
        for (int i = maxLayer; i >= 1; i--) {
            ArrayList<Integer> arrays = layerMap.get(i);
            for (int j = arrays.size() - 1; j >= 0; j--) {
                // 下面的判断可能有优化？
                if (fatherRelation.containsKey(arrays.get(j))
                        && fatherRelation.get(arrays.get(j)).size() + 1 == K) {
                	if(OUTFLAG)
                		System.out.println("MEET:" + "u" + points.get(arrays.get(j)).number);
                } else if (fatherRelation.containsKey(arrays.get(j))
                        && fatherRelation.get(arrays.get(j)).size() + 1 > K) {
                    continue;
                } else {
                    oneUnitGroup.add(arrays.get(j));
                }
            }
        }

        System.out.println("one unit group size :" + oneUnitGroup.size());

        for (int pos = 0; pos < oneUnitGroup.size(); pos++) { 
            // Subset Pruning, 检查 Gi(last)
            int node = oneUnitGroup.get(pos);
            if (fatherRelation.containsKey(node)) {
                if (fatherRelation.get(node).size() + 1 > K) {
                    continue;
                } else if (fatherRelation.get(node).size() + 1 == K) {
                    // output
                    continue;
                }
            } else {
                if (K == 1) {
                    // ouput
                }
            }

            // Subset Pruning
            HashSet<Integer> allLastGroup = new HashSet<Integer>();
            allLastGroup.add(node);
            if (fatherRelation.containsKey(node)) {
                allLastGroup.addAll(fatherRelation.get(node));
            }
            for (int last = pos + 1; last < oneUnitGroup.size(); last++) {
                allLastGroup.add(oneUnitGroup.get(last));
                if (allLastGroup.size() >= K)
                    break;
                if (fatherRelation.containsKey(oneUnitGroup.get(last)))
                    allLastGroup.addAll(fatherRelation.get(oneUnitGroup.get(last)));
            }
            if (allLastGroup.size() < K)
                break;

            HashSet<Integer> group = new HashSet<>();
            group.add(node); // 添加当前节点
            if (fatherRelation.containsKey(node)) {
                group.addAll(fatherRelation.get(node));
            }

            for (int tail = pos + 1; tail < oneUnitGroup.size(); tail++)
                dfsUWise(group, tail);
        }

        long endTime = System.currentTimeMillis();
        long runTime = endTime - starTime;
        System.out.println("run time : " + runTime);
    }

    public static void dfsUWise(HashSet<Integer> group, int pos) {
        if (pos >= oneUnitGroup.size())
            return;

        HashSet<Integer> parentSet = new HashSet<Integer>();
        parentSet.addAll(group);

        if (parentSet.contains(oneUnitGroup.get(pos))) { // 父结点包含
            return;
        } else {
            parentSet.add(oneUnitGroup.get(pos));
            if (fatherRelation.containsKey(oneUnitGroup.get(pos)))
                parentSet.addAll(fatherRelation.get(oneUnitGroup.get(pos)));
        }

        HashSet<Integer> newGroup = new HashSet<>();
        newGroup = parentSet; // 不需UnitGroup，只需HashSet即可

        if (newGroup.size() == K) {
            if (OUTFLAG == true) {
                for (int x : newGroup) {
                    System.out.print(num[x] + " ");
                }
                System.out.println();
            }
            return;
        } else if (newGroup.size() > K) {
            return;
        }

        for (int tail = pos + 1; tail < oneUnitGroup.size(); tail++) {
            // tail set的点在父结点里
            if (newGroup.contains(oneUnitGroup.get(tail))) {
                continue;
            } else {
                dfsUWise(newGroup, tail);
            }
        }


    }

    /**
     * 构建DSG,数据为多维
     */
    public static void buildDSG() {
        maxLayer = 1;
        layerMap.put(1, new ArrayList<Integer>());
        layerMap.get(1).add(0);
        for (int pos = 1; pos < points.size(); pos++) {

            // 最后一层layer能支配当前点
            if (layerDominate(layerMap.get(maxLayer), points.get(pos))) {
                if (maxLayer + 1 > K) {
                    continue;
                }
                layerMap.put(++maxLayer, new ArrayList<Integer>());
                layerMap.get(maxLayer).add(pos);
                // 填加父亲关系
                for (int sLayer = 1; sLayer <= maxLayer; sLayer++) {
                    addFatherRelation(pos, layerMap.get(sLayer));
                }
                continue;
            }

            // 第一层layer不能支配当前点
            if (!layerDominate(layerMap.get(1), points.get(pos))) {
                layerMap.get(1).add(pos);
                continue;
            }

            // 判断从哪层开始不能支配该点
            for (int layer = 2; layer <= maxLayer; layer++) { // 这地方可把最后一层优化掉
                if (!layerDominate(layerMap.get(layer), points.get(pos))) {
                    layerMap.get(layer).add(pos);
                    for (int sLayer = 1; sLayer < layer; sLayer++) {
                        addFatherRelation(pos, layerMap.get(sLayer));
                    }
                    break;
                }
            }
        }
    }

    /**
     * 构建DSG,数据为二维
     */
    public static void buildDSG2Dimension() {
        maxLayer = 1;
        layerMap.put(1, new ArrayList<Integer>());
        layerMap.get(1).add(0);
        for (int pos = 1; pos < points.size(); pos++) {

            // 最后一层layer能支配当前点, 只需判断最后一个点
            if (dominate(layerMap.get(maxLayer).get(layerMap.get(maxLayer).size() - 1), pos)) {
                if (maxLayer + 1 > K) {
                    continue;
                }
                layerMap.put(++maxLayer, new ArrayList<Integer>());
                layerMap.get(maxLayer).add(pos);
                // 填加父亲关系
                for (int sLayer = 1; sLayer <= maxLayer; sLayer++) {
                    addFatherRelation(pos, layerMap.get(sLayer));
                }
                continue;
            }

            // 第一层layer不能支配当前点
            if (!dominate(layerMap.get(1).get(layerMap.get(1).size() - 1), pos)) {
                layerMap.get(1).add(pos);
                continue;
            }

            // binary search
            int L = 1, R = maxLayer, ans = 1;
            while (L <= R) {
                int mid = L + (R-L) / 2;
                if (!dominate(layerMap.get(mid).get(layerMap.get(mid).size() - 1), pos)) {
                    R = mid - 1;
                    ans = mid;
                } else {
                    L = mid + 1;
                }
            }
            layerMap.get(ans).add(pos);
            for (int sLayer = 1; sLayer < ans; sLayer++) {
                addFatherRelation(pos, layerMap.get(sLayer));
            }
        }
    }

    // 添加父子关系
    public static void addFatherRelation(int son, int father) {
        if (!fatherRelation.containsKey(son)) {
            fatherRelation.put(son, new HashSet<Integer>());
        }
        fatherRelation.get(son).add(father);
    }

    // 添加父子关系
    // 二维也不能通过y坐标来优化，本以为可以直接break
    public static void addFatherRelation(int son, ArrayList<Integer> fatherLists) {
        for (int father : fatherLists) {
            if (dominate(father, son)) {
                if (!fatherRelation.containsKey(son)) {
                    fatherRelation.put(son, new HashSet<Integer>());
                }
                fatherRelation.get(son).add(father);
            }
        }
    }
    
    // 判断某一层是否有点能支配点p
    public static boolean layerDominate(ArrayList<Integer> arrays, Point p) {
        for (int i : arrays) {
            if (dominate(points.get(i), p)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<Point> loadFileData(String filePath) throws IOException {
        ArrayList<Point> ans = new ArrayList<Point>();
        BufferedReader reader = null;
        reader = new BufferedReader(new FileReader(filePath));
        String line = "";
        int cnt = 0;
        while ((line = reader.readLine()) != null) {
            String[] values = line.split(" ");
            double[] x = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                x[i] = Double.parseDouble(values[i]);
            }
            Point p = new Point(++cnt, x);
            ans.add(p);
        }
        reader.close();
        return ans;
    }

    static class Point implements Comparable<Point> {
        public double[] x = new double[10];
        public int count = 0;
        public int number = 0;

        public Point(int num, double... values) {
            this.number = num;
            this.count = values.length;
            for (int i = 0; i < values.length; i++) {
                this.x[i] = values[i];
            }
        }

        public void output() {
            for (int i = 0; i < count; i++) {
                System.out.print(x[i] + " ");
            }
            System.out.println();
        }

        @Override
        public int compareTo(Point o) {
            for (int i = 0; i < count; i++) {
                if (x[i] == o.x[i])
                    continue;
                else if (x[i] > o.x[i])
                    return 1;
                else
                    return -1;
            }
            return 1;
        }
    }

    public static boolean dominate(int pos1, int pos2) { // dominate的定义还需要注意
        Point p1 = points.get(pos1);
        Point p2 = points.get(pos2);
        int greatFlag = 0;
        int lessFlag = 0;
        for (int i = 0; i < p1.count; i++) {
            if (p1.x[i] > p2.x[i]) {
                greatFlag = 1;
            } else if (p1.x[i] < p2.x[i]) {
                lessFlag = 1;
            }
        }
        if (greatFlag == 0 && lessFlag == 1)
            return true;
        return false;
    }

    public static boolean dominate(Point p1, Point p2) {
        int greatFlag = 0;
        int lessFlag = 0;
        for (int i = 0; i < p1.count; i++) {
            if (p1.x[i] > p2.x[i]) {
                greatFlag = 1;
            } else if (p1.x[i] < p2.x[i]) {
                lessFlag = 1;
            }
        }
        if (greatFlag == 0 && lessFlag == 1)
            return true;
        return false;
    }
}

