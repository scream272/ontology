package com.clh.protege;
import com.clh.protege.protege.ConvertClassToOwl;
import com.clh.protege.utils.Entry;
import com.clh.protege.utils.ExportDoc;
import com.clh.protege.word.TfIdfCounter;
import com.google.gson.*;
import com.hankcs.hanlp.corpus.tag.Nature;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 *
 * 读取word文档中表格数据，支持doc、docx
 * @author Fise19
 *
 */
public class Main {
    static private String rawDataPath = "C:\\Users\\clh\\IdeaProjects\\ontology\\resource\\神华项目报告-中科.doc";
    static private String jsonPath = "C:\\Users\\clh\\IdeaProjects\\ontology\\data\\phase1_data.json";
    static private List<String> causeDict;
    static private JsonParser jsonParser;
    static private ArrayList<Entry> entryList;
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
    static private String convertRawDataToJson(String infilePath, String outfilePath) {
        try {
            File outfile = new File(outfilePath);
            if (outfile.exists()) { // 如果已存在,直接读取
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
            entryList = doc.entryList;
            return gson.toJson(doc.entryList);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static private String mergeJsonValueIntoString(String data) {
        String result = "";
        try {
            JsonArray ja = (JsonArray) jsonParser.parse(data);  //创建jsonObject对象
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
                entryList.add(new Entry(jo.get("para").toString(), jo.get("bias").toString(), jo.get("conseq").toString(),
                                        jo.get("cause").toString(), jo.get("protection").toString(),jo.get("severity").toString(),
                                        jo.get("possibiliy").toString(), jo.get("level").toString(), jo.get("suggestion").toString()));
            }
        } catch (JsonIOException e) {
            e.printStackTrace();
        }
        return result;
    }

    static private List<String> selectImportantWord(String data, int size, Nature na) {
        List<String> result = new ArrayList<>();
        File wordCache = new File("C:\\Users\\clh\\IdeaProjects\\ontology\\data\\Important_" +
                                na.toString() +
                                size);
        // 如果发现之前有缓存好的结果，则直接取出
        if (wordCache.exists()) {
            FileReader reader = null;//定义一个fileReader对象，用来初始化BufferedReader
            try {
                reader = new FileReader(wordCache);
                BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
                String s = "";
                while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                    result.add(s);
                }
                bReader.close();
                return result;
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // 如果没有找到已经缓存的结果，则重新筛选
        TfIdfCounter tflc = new TfIdfCounter();
        result = tflc.getKeywordsWithTfIdf(data, size, na);
        try {
            wordCache.createNewFile();
            Writer write = new OutputStreamWriter(new FileOutputStream(wordCache), "UTF-8");
            for (String r: result) {
                write.write(r + "\n");
            }
            write.flush();
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    static private void buildProtegeOwl() {
        ConvertClassToOwl conver = new ConvertClassToOwl();
        conver.test();
    }
    static private void envInit() {
        jsonParser = new JsonParser();  //创建json解析器
        causeDict = Arrays.asList("造成", "引起", "引发");
        entryList = new ArrayList<>();
    }
    public static void main(String[] args) {
        /* 构造本体的作用：将文档进行结构化抽象，便于安全信息的传递、共享、查询
        *  1. 对文档中出现的关键实验器材，进行全面的危险系数评估（从不当操作可能引发的后果来分析）；
        *  2. 为实际生产过程中可能出现的危害，提供自动化故障处理建议；
        *  3. 汇总生产过程中需要关注的事项，用于指导生产 */
        envInit();
        // 步骤1：首先将原始的文档进行处理，将文档中的表格转换为json文件
        String dataJson = convertRawDataToJson(rawDataPath, jsonPath);

        // 步骤2：将json的value值合并成一个总的字符串，方便提取关键词
        String dataStr = mergeJsonValueIntoString(dataJson);

        // 步骤3：关键词抽取，按照词性来进行
        List<String> vWords = selectImportantWord(dataStr, 100, Nature.v);
        List<String> nWords = selectImportantWord(dataStr, 1000, Nature.n);
        System.out.println(vWords);
        System.out.println(nWords);

        // 步骤4：分析json文档，构造Event、iobject、IOperation三元表达
//        for (Entry entry : entryList) {
//            Equipment eq = Equipment.GetEquipment(entry.getNumAttr(1)); //TODO 剥离器材与Attribute
//        }
        // 步骤5：利用步骤3与步骤4的输出结果建立关系图

        // 步骤6：在关系图的基础上建立“后果”的倒排索引

        // 步骤7：利用关系图与倒排索引进行本体构建
        buildProtegeOwl();
        // 步骤8：本体的使用
        //file_process();
        testFunctinos();
    }

}