package external;


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
 * array优化
 * 
 * @author CGF
 *
 */
public class PartSort {
    
    static String fileName = "/home/cgf/data/testData16G";
    static String file = "/home/cgf/data/part";
    static DataOutputStream outs[] = new DataOutputStream[200];
    static FileInputStream ins[] = new FileInputStream[200];
    static int M = 26;
    static long[] arrays = new long[1<<M];
    
    public static void main(String[] args) throws IOException {
        DataInputStream in = new DataInputStream(new FileInputStream(fileName));
        byte[] array = new byte[8];
        int cnt = 0; 
        int fileEnd = 0;
        while (in.available() != 0) {
            arrays[cnt++] = in.readLong();
            if(cnt == 1L<<M) {
                fileEnd ++;
                Arrays.sort(arrays);
                cnt = 0;
                System.out.println("读写文件：" + file+String.valueOf(fileEnd));
                outs[fileEnd] = new DataOutputStream(new FileOutputStream(file+String.valueOf(fileEnd)));
                for (int i = 0;i < arrays.length;i++) {
                    outs[fileEnd].writeLong(arrays[i]);
                }
            }
        }
        if (cnt != 0) {
        	System.out.println("??");
        	outs[fileEnd+1] = new DataOutputStream(new FileOutputStream(file+String.valueOf(fileEnd+1)));
        	for (int i = 0;i < cnt;i++) {
        		outs[fileEnd+1].writeLong(arrays[i]);
        	}
        }
        in.close();
    }
    
    public static long bytesToLong(byte[] bytes) {  
        ByteBuffer buffer = ByteBuffer.allocate(8); 
        buffer.put(bytes, 0, bytes.length);  
        buffer.flip();  //need flip   
        return buffer.getLong();  
    } 
}
