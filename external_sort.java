package algorithm_exp1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/**
 * 多路归并 & 败者树
 * 
 * @author CGF
 *
 */
public class MultiRoadSort {
    
    static String file = "C:\\data\\part";
    static DataOutputStream outs[] = new DataOutputStream[40];
    static DataInputStream ins[] = new DataInputStream[40];
    static int M = 26;
    static long[] arrays = new long[1<<M];
    
    public static void main(String[] args) throws IOException {
        byte[] array = new byte[8];
        int cnt = 0; 
        int fileEnd = 0;
       
        int cycNum = fileEnd;
        for (int i = 1;i <= 32;i++) {
            ins[i] = new DataInputStream(new FileInputStream(file + String.valueOf(i))); 
        }
        
        long[] num = new long[40];
        for (int i = 1;i <= 32;i++) {
            num[i] = -1;
        }
        long minValue = Long.MAX_VALUE;
        int pos = -1;
        DataOutputStream out = new DataOutputStream(new FileOutputStream("C:\\data\\ansoutput"));
        
        while (true) {
            int flag = 1;
            minValue = Long.MAX_VALUE;
            pos = -1;
            for (int i = 1;i <= 32;i++) {
                if (num[i] == -1) {
                    if (ins[i].available() != 0) { 
                        num[i] = ins[i].readLong();
                        flag = 0;
                        if (num[i] < minValue) {
                            minValue = num[i];
                            pos = i;
                        }
                    }
                } else {
                    if (num[i] < minValue) {
                        minValue = num[i];
                        pos = i;
                    }
                    flag = 0;
                    continue;
                }
            }
            
            if (flag == 1) {
                break;
            }
        }
    }
    
    public static long bytesToLong(byte[] bytes) {  
        ByteBuffer buffer = ByteBuffer.allocate(8); 
        buffer.put(bytes, 0, bytes.length);  
        buffer.flip();  //need flip   
        return buffer.getLong();  
    } 
}
