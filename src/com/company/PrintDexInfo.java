package com.company;

import java.io.*;

/**
 * Created by shs1330 on 2018/1/4.
 */
public class PrintDexInfo
{
    private static String FILENAME = "force/Hello.dex";

    private static int readToInt(byte[] data, int start, int length, boolean reverse){
        byte[] res = new byte[length];
        System.arraycopy(data, start, res, 0, length);

        if (reverse) {
            reverseArray(res, 2);
        }

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(res);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);

        int readInt = 0;
        try {
            readInt = dataInputStream.readInt();
            dataInputStream.close();
            byteArrayInputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return readInt;
    }

    private static void reverseArray(byte[] data, int len) {
        int num = data.length / 2;
        for (int i = 0; i < num; i++) {
            byte temp = data[i];
            //System.out.println(Integer.toHexString(temp & 0xFF));
//
            data[i] = data[data.length - i - 1];
            data[data.length - i -1] = temp;
//
//            temp = data[i * len + 1];
//            data[i * len + 1] = data[(data.length / len - i - 1) * len + 1];
//            data[(data.length / len - i - 1) * len + 1] = temp;
        }
    }

    public static void main(String[] args) {
        File dexFile = new File(FILENAME);
        byte[] dexFileArray = ParserDex.readFileArray(dexFile);

        print("大小",readToInt(dexFileArray, 32, 4, true) + "");
    }

    private static void print(String name, String info) {
        System.out.println(name + " : " + info + "\n");
    }

}
