package algorithm_exp1;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class generateSmallData {
    
    static long a = 3L, c = 5L, m = 1125899906842679L;  
    static long X1 = 2016213617;
    static String fileName = "data/small_data";
    
    public static void main(String[] args) throws IOException {
        //BufferedWriter writer = new BufferedWriter(new FileWriter("data/testAll.txt"));
        DataOutputStream outStream = new DataOutputStream(new FileOutputStream(fileName));
        //outStream.writeLong(5L);
        //outStream.close();
        for (long i = 6L;i <= 10L; i++) {
            //X1 = (a*X1 + c) % m;
            outStream.writeLong(i);
            //System.out.println(X1);
        }
        outStream.close();
        //writer.close();
    }
}
