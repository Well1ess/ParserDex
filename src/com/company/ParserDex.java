package com.company;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Collections;
import java.util.zip.Adler32;

/**
 * Created by shs1330 on 2017/12/27.
 */
public class ParserDex {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        //读取元数据
        File dexFile = new File("force/Hello.dex");
        byte[] dexFileArray = readFileArray(dexFile);
        File apkFile = new File("force/app-debug.apk");
        byte[] apkFileArray = readFileArray(apkFile);

        int newClassDexLength = dexFileArray.length + apkFileArray.length + 4;
        byte[] newDexFileArray = new byte[newClassDexLength];

        //拷贝数据
        System.arraycopy(dexFileArray, 0, newDexFileArray, 0, dexFileArray.length);
        System.arraycopy(apkFileArray, 0, newDexFileArray, dexFileArray.length, apkFileArray.length);
        System.arraycopy(intToByte(apkFileArray.length), 0, newDexFileArray, newClassDexLength - 4, 4);

        //修改头部
        fixDexFileSize(newDexFileArray);
        //修改sha1
        fixDexSHA1(newDexFileArray);
        //修改adler32
        fixChecksum(newDexFileArray);

        //新建文件
        String str = "force/classes.dex";
        File file = new File(str);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FileOutputStream fileOutputStream = new FileOutputStream(str);
        fileOutputStream.write(newDexFileArray);
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    public static void fixDexFileSize(byte[] dexFileArray) {
        byte[] lenght = intToByte(dexFileArray.length);
        //小尾方式
        byte temp = lenght[3];
        lenght[3] = lenght[0];
        lenght[0] = temp;

        temp = lenght[2];
        lenght[2]= lenght[1];
        lenght[1] = temp;

        System.arraycopy(lenght, 0, dexFileArray, 32, 4);
    }

    public static void fixDexSHA1(byte[] dexFileArray) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        //sha1 从fileSize开始
        md.update(dexFileArray, 32, dexFileArray.length - 32);
        byte[] newdt = md.digest();

        //sha1 不是小尾模式
        System.arraycopy(newdt, 0, dexFileArray, 12, 20);
    }

    public static void fixChecksum(byte[] dexFileArray){
        Adler32 adler32 = new Adler32();
        adler32.update(dexFileArray, 12, dexFileArray.length - 12);
        byte[] checkSum = intToByte((int) adler32.getValue());

        //小尾方式
        byte temp = checkSum[3];
        checkSum[3] = checkSum[0];
        checkSum[0] = temp;

        temp = checkSum[2];
        checkSum[2]= checkSum[1];
        checkSum[1] = temp;

        System.arraycopy(checkSum, 0, dexFileArray, 8, 4);
    }

    public static void reverse(Object[] dest)
    {

    }

    public static byte[] intToByte(int number) {
        byte[] b = new byte[4];
        for (int i = 3; i >= 0; i--) {
            b[i] = (byte) (number % 256);
            number >>= 8;
        }
        return b;

    }

    public static byte[] readFileArray(File dexFile) {
        byte[] arrayOfByte = new byte[1024];
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            FileInputStream fileInputStream = new FileInputStream(dexFile);
            while (true) {
                int i = fileInputStream.read(arrayOfByte);
                if (i != -1) {
                    outputStream.write(arrayOfByte, 0, i);
                } else {
                    return outputStream.toByteArray();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
