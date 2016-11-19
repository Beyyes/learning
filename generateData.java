package algorithm_exp1;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class generateData {
    
    static long a = 3L, c = 5L, m = 1125899906842679L;  
    static long X1 = 2016213617;
    static String fileName = "data/testData16G";
    
    public static void main(String[] args) throws IOException {
        //BufferedWriter writer = new BufferedWriter(new FileWriter("data/testAll2.txt"));
        DataOutputStream outStream = new DataOutputStream(new FileOutputStream(fileName));
        //outStream.writeLong(5L);
        //outStream.close();
        int cnt = 1;
        for (long i = 1;i <= 1L<<31; i++) {
            if(cnt > 10)
                break;
            X1 = (a*X1 + c) % m;
            System.out.println(X1);
            outStream.writeLong(X1);
            //System.out.println(X1);
            cnt++;
        }
        outStream.close();
        //writer.close();
    }
}
