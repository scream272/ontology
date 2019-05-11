package com.company.utils;

import java.io.*;
import java.util.ArrayList;

public class FileProcesser {
    static private void generateNewDict(){
        ArrayList<String> arrayList = new ArrayList<>();
        try {
            FileReader fr = new FileReader("C:\\Users\\clh\\IdeaProjects\\ontology\\data\\dictionary\\custom\\化工词汇（超）.txt");
            BufferedReader bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                str = str + " n 1";
                arrayList.add(str);
            }
            bf.close();
            fr.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对ArrayList中存储的字符串进行处理
        int length = arrayList.size();
        try {
            FileWriter writer = new FileWriter("C:\\Users\\clh\\IdeaProjects\\ontology\\data\\dictionary\\custom\\化工词汇.txt");
            BufferedWriter bw = new BufferedWriter(writer);
            for (int i = 0; i < length; i++) {
                bw.write(arrayList.get(i) + "\t\n");
            }
            bw.close();
            writer.close();
        }catch (IOException e) {
            e.printStackTrace();
        }
        // 返回数组
    }
}
