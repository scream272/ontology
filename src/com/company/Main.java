package com.company;
import com.google.gson.*;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.hankcs.hanlp.summary.TextRankKeyword;

import java.io.*;
import java.util.List;
import com.company.word.NewWordDiscover;

/**
 *
 * 读取word文档中表格数据，支持doc、docx
 * @author Fise19
 *
 */
public class Main {
    static private String rawDataPath = "C:\\Users\\clh\\IdeaProjects\\ontology\\resource\\神华项目报告-中科.doc";
    static private String jsonPath = "C:\\Users\\clh\\IdeaProjects\\ontology\\data\\phase1_data.json";

    static private void testFunctinos() {
//        List<Term> termList = StandardTokenizer.segment("反应器超温，泄漏，遇点火源引发火灾爆炸，人员中毒伤亡");
//        System.out.println(termList);
//
//        System.out.println(NLPTokenizer.segment("我新造一个词叫幻想乡你能识别并标注正确词性吗？"));
//        System.out.println(NLPTokenizer.analyze("反应器超温，泄漏，遇点火源引发火灾爆炸，人员中毒伤亡").translateLabels());
//        TextRankKeyword tk = new TextRankKeyword();
//        System.out.println(tk.getKeywords(convertRawDataToJson(rawDataPath), 10));
//        NewWordDiscover wd = new NewWordDiscover();
//        System.out.println(wd.discover(jsonPath, 3));
    }
    static private String convertRawDataToJson(String infilePath) {
        try {
            File outfile = new File(infilePath);
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

    static private String mergeJsonValueIntoString(String infilePath) {
        JsonParser parse =new JsonParser();  //创建json解析器
        String result = "";
        try {
            JsonArray ja = (JsonArray) parse.parse(new FileReader(infilePath));  //创建jsonObject对象

            for (JsonElement entry : ja) {
                JsonObject jo = (JsonObject) entry;
                result = result + jo.get("para");
                result = result + jo.get("bias");
                result = result + jo.get("conseq");
                result = result + jo.get("cause");
                result = result + jo.get("protection");
                result = result + jo.get("severity");
                result = result + jo.get("possibiliy");
                result = result + jo.get("level");
                result = result + jo.get("suggestion");
            }
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }
    static private String SelectImportantWord(String data) {
        NewWordDiscover wd = new NewWordDiscover();
        String result = wd.discover(data, 3).toString();
        System.out.println(result);
        return result;
    }
    public static void main(String[] args) {
        // convertRawDataToJson(rawDataPath);
        String dataStr = mergeJsonValueIntoString(jsonPath);
        SelectImportantWord(dataStr);
        testFunctinos();
    }

}