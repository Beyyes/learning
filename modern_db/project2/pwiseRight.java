package pwise;

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
 * pwise实现，由uwise改了一些内容
 * 
 * @author CGF
 *
 */
public class Main {
	
	static int arrayMaxn = 10005;
    static List<Point> points = null;
    static int K = 4;
    static Map<Integer, HashSet<Integer>> fatherRelation = new HashMap<Integer, HashSet<Integer>>();
    static Map<Integer, HashSet<Integer>> sonRelation = new HashMap<Integer, HashSet<Integer>>();
    static Map<Integer, ArrayList<Integer>> layerMap = new HashMap<Integer, ArrayList<Integer>>();
    static int[] layerArray = new int[arrayMaxn];
    
    static List<Point> rePoints = new ArrayList<Point>();
    static int maxLayer = 0;
    static ArrayList<Integer> pointGroup = new ArrayList<Integer>();
    
    static int num[] = new int[arrayMaxn];
    static boolean OUTFLAG = !true;
    static boolean TwoDFlag = false;

    // static String filePath = "data/test.txt";
    static String filePath = "data/anti_2.txt";
    static long OUTPUTANS = 0;

    public static void main(String[] args) throws IOException {

        points = loadFileData(filePath);

        long starTime = System.currentTimeMillis();
        Collections.sort(points); 
        if (OUTFLAG) {
            for (int i = 0; i < points.size(); i++) {
                num[i] = points.get(i).number;
            }
        }

        if (TwoDFlag) {
            // two dimension : binary search | higher
            buildDSG2Dimension();
        } else {
            buildDSG();
        }

        // point group ordering，可以考虑order倒序的优化？
        // point group存储排序后的下标
        // 既判断fatherRelation又判断sonRelation的point数量，可能会有所优化
        for(int i = 1;i <= maxLayer;i++) {
        	ArrayList<Integer> arrays = layerMap.get(i);
        	for(int j = 0;j < arrays.size();j++) {
        		if (fatherRelation.containsKey(arrays.get(j))
                        && fatherRelation.get(arrays.get(j)).size() + 1 == K) {
                    OUTPUTANS++;
                    System.out.println("MEET:" + "u" + points.get(arrays.get(j)).number);
                } else if (fatherRelation.containsKey(arrays.get(j))
                        && fatherRelation.get(arrays.get(j)).size() + 1 > K) {
                    continue;
                } else {
                	layerArray[arrays.get(j)] = i;
                    pointGroup.add(arrays.get(j));
                }  
        	}
        }

        System.out.println("point group size :" + pointGroup.size());
        
        int curMaxLayer = 1;
        for (int pos = 0; pos < pointGroup.size(); pos++) {
            int node = pointGroup.get(pos);
            curMaxLayer = Math.max(curMaxLayer, layerArray[node]);
            
            // unit group fatherRelation的策略应用到此
            if (fatherRelation.containsKey(node)) {
                if (fatherRelation.get(node).size() + 1 > K) {
                    continue;
                } else if (fatherRelation.get(node).size() + 1 == K) {
                    // output
                    if (OUTFLAG) {
                        System.out.println(fatherRelation.get(node));
                    }
                    OUTPUTANS++; 
                    continue;
                }
            } else {
                if (K == 1) {
                    // ouput
                }
            }
            
            // Subset Pruning
            // unit wise的优化策略可以拿来优化point wise
            HashSet<Integer> allLastGroup = new HashSet<Integer>();
            allLastGroup.add(node);
            if (fatherRelation.containsKey(node)) {
                allLastGroup.addAll(fatherRelation.get(node));
            }
            for (int last = pos + 1; last < pointGroup.size(); last++) {
                allLastGroup.add(pointGroup.get(last));
                if (allLastGroup.size() >= K)
                    break;
                if (fatherRelation.containsKey(pointGroup.get(last)))
                    allLastGroup.addAll(fatherRelation.get(pointGroup.get(last)));
            }
            if (allLastGroup.size() < K)
                break;

            HashSet<Integer> group = new HashSet<>();
            group.add(node); // 添加当前节点，不需要添加子节点

            for (int tail = pos + 1; tail < pointGroup.size(); tail++) {
            	// pj.layer - maxl{pl.layer} >= 2
            	if(layerArray[pointGroup.get(tail)] - curMaxLayer >= 2) {
            		break;
            	}
            	dfsUWise(group, tail, curMaxLayer);
            }
        }

        System.out.println("满足条件的点数量：" + OUTPUTANS);
        long endTime = System.currentTimeMillis();
        long runTime = endTime - starTime;
        System.out.println("run time : " + runTime);
    }

    public static void dfsUWise(HashSet<Integer> group, int pos, int curMaxLayer) {
        if (pos >= pointGroup.size())
            return;

        HashSet<Integer> groupSet = new HashSet<Integer>();
        groupSet.addAll(group);
        
        // 如果要加的点为sky line点
        if (layerArray[pointGroup.get(pos)] == 1) {
        	groupSet.add(pointGroup.get(pos));
        } else {
        	// 判断pointGroup[pos]是否为groupSet子节点
        	boolean sonFlag = false;
        	for(int father : groupSet) {
        		if(sonRelation.containsKey(father) && 
        				sonRelation.get(father).contains(pointGroup.get(pos))) {
        			sonFlag = true;
        			break;
        		}
        	}
        	if (sonFlag) {
        		// 判断 curMaxLayer-L>=2可以直接return? 
        		if(layerArray[pointGroup.get(pos)]-curMaxLayer >= 2) 
        			return;
        		curMaxLayer = Math.max(curMaxLayer, layerArray[pointGroup.get(pos)]);
        		groupSet.add(pointGroup.get(pos));
        	} else {
        		// 不满足上面两个条件要return
        		return;
        	}
        }
        
        if(groupSet.size() > K) {
        	return;
        } else if(groupSet.size() == K) { // groupSet里点的数量为K, check并输出
        	HashSet<Integer> fatherSumHashSet = new HashSet<Integer>();
        	for(int s : groupSet) {
        		fatherSumHashSet.add(s); // 注意要先添加当前点
        		if(fatherRelation.containsKey(s))
        			fatherSumHashSet.addAll(fatherRelation.get(s));
        	}
        	if(fatherSumHashSet.size() == groupSet.size()) {
        		if(OUTFLAG) {
        			for(int s : groupSet) {
        				System.out.print(points.get(s).number + " ");
        			}
        			System.out.println();
        		}
        		OUTPUTANS++; 
        	} else {
        		return;
        	}
        	return;
        } else {
        	// 小于K也许可以剪枝？
        }

        for (int tail = pos + 1; tail < pointGroup.size(); tail++) {
        	// pj.layer - maxl{pl.layer} >= 2
        	if(layerArray[pointGroup.get(tail)] - curMaxLayer >= 2) {
        		break;
        	}
        	dfsUWise(groupSet, tail, curMaxLayer);
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
                int mid = L + (R - L) / 2;
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
        if (!sonRelation.containsKey(father)) {
            sonRelation.put(father, new HashSet<Integer>());
        }
        sonRelation.get(father).add(son);
        
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
                if (!sonRelation.containsKey(father)) {
                    sonRelation.put(father, new HashSet<Integer>());
                }
                sonRelation.get(father).add(son);
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
            if (values.length == 2)
                TwoDFlag = true;
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

