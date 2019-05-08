package com.company;
import com.google.gson.Gson;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.hankcs.hanlp.summary.TextRankKeyword;

import java.io.*;
import java.util.List;

/**
 *
 * 读取word文档中表格数据，支持doc、docx
 * @author Fise19
 *
 */
public class Main {
    static private String phase1(String infilePath) {
        String outfilePath = "C:\\Users\\clh\\IdeaProjects\\ontology\\data\\phase1_data.json";
        try {
            File outfile = new File(outfilePath);
            if (outfile.exists()) { // 如果已存在,删除旧文件
                FileReader reader = new FileReader(outfile);//定义一个fileReader对象，用来初始化BufferedReader
                BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
                StringBuilder sb = new StringBuilder();//定义一个字符串缓存，将字符串存放缓存中
                String s = "";
                while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                    sb.append(s + "\n");//将读取的字符串添加换行符后累加存放在缓存中
                }
                bReader.close();
                String str = sb.toString();
                return str;
            }
            outfile.createNewFile();
            ExportDoc doc = new ExportDoc();
            doc.parseDoc(infilePath);
            Gson gson = new Gson();
            // 将格式化后的字符串写入文件
            Writer write = new OutputStreamWriter(new FileOutputStream(outfile), "UTF-8");
            write.write(gson.toJson(doc.entryList));
            write.flush();
            write.close();
            return gson.toJson(doc.entryList);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String[] args) {
        String rawDataPath = "C:\\Users\\clh\\IdeaProjects\\ontology\\resource\\神华项目报告-中科.doc";

         List<Term> termList = StandardTokenizer.segment("商品和服务");
         System.out.println(termList);
        TextRankKeyword tk = new TextRankKeyword();
        System.out.println(tk.getKeywords(phase1(rawDataPath), 10));
    }
}