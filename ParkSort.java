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
 * array优化
 * 
 * @author CGF
 *
 */
public class Main2 {
    
    static String fileName = "data/testData16G";
    //static String fileName = "data/small_data";
    static String file = "data/part";
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
        in.close();
        int cycNum = fileEnd;
        while (cycNum > 1) {
            for(int i = 1;i <= cycNum;i += 2) {
                if (cycNum == fileEnd) {
                    ins[i] = new FileInputStream(file + String.valueOf(i));
                    ins[i+1] = new FileInputStream(file + String.valueOf(i+1));
                } else {
                    ins[i] = new FileInputStream(file + "_mid_"+cycNum*2 +"_"+ String.valueOf(i));
                    ins[i+1] = new FileInputStream(file + "_mid_"+cycNum*2 +"_"+ String.valueOf(i+1));
                }
                    
                DataOutputStream tmpOut = new DataOutputStream(new FileOutputStream(file+"_mid_"+cycNum+"_"+(i+1)/2));
                System.out.println("写文件：" + file+"_mid_"+cycNum+"_"+(i+1)/2);
                long x = -1, y = -1;
                while(ins[i].available() != 0 && ins[i+1].available() != 0) {
                    if(x == -1) {
                        ins[i].read(array, 0, 8);
                        x = bytesToLong(array);
                    }
                    if(y == -1) {
                        ins[i+1].read(array, 0, 8);
                        y = bytesToLong(array);
                    }
                    if(x < y) {
                        tmpOut.writeLong(x);
                        x = -1;
                    } else {
                        tmpOut.writeLong(y);
                        y = -1;
                    }
                }
                if (x != -1) {
                    tmpOut.writeLong(x);
                }
                if (y != -1) {
                    tmpOut.writeLong(y);
                }
                while(ins[i].available() != 0) {
                    ins[i].read(array, 0, 8);
                    tmpOut.writeLong(bytesToLong(array));
                }
                while(ins[i+1].available() != 0) {
                    ins[i+1].read(array, 0, 8);
                    tmpOut.writeLong(bytesToLong(array));
                }
                tmpOut.close();
            }
            cycNum /= 2;
        }
        
    }
    
    public static long bytesToLong(byte[] bytes) {  
        ByteBuffer buffer = ByteBuffer.allocate(8); 
        buffer.put(bytes, 0, bytes.length);  
        buffer.flip();  //need flip   
        return buffer.getLong();  
    } 
}
