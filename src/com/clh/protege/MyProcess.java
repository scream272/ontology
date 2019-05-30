package com.clh.protege;
import com.clh.protege.ievent.FinalEvent;
import com.clh.protege.ievent.InitEvent;
import com.clh.protege.ievent.MiddleEvent;
import com.clh.protege.iobject.Attribute;
import com.clh.protege.iobject.Equipment;
import com.clh.protege.ioperation.Precaution;
import com.clh.protege.protege.OwlModel;
import com.clh.protege.utils.Display;
import com.clh.protege.utils.Entry;
import com.clh.protege.utils.ExportDoc;
import com.clh.protege.utils.Log;
import com.clh.protege.word.TfIdfCounter;
import com.google.gson.*;
import com.hankcs.hanlp.corpus.tag.Nature;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * 读取word文档中表格数据，支持doc、docx
 * @author Fise19
 *
 */
public class MyProcess {
    static private String rawDataPath = "C:\\Users\\clh\\IdeaProjects\\ontology\\resource\\神华项目报告-中科.doc";
    static private String jsonPath = "C:\\Users\\clh\\IdeaProjects\\ontology\\data\\phase1_data.json";
    static private String docsDir = "C:\\Users\\clh\\IdeaProjects\\ontology\\data\\";
    static private List<String> causeDict;
    static private JsonParser jsonParser;
    static private ArrayList<Entry> entryList;
    static public OwlModel om;
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
        // generateDocuments(entryList);
        return result;
    }

    static public List<Map.Entry<String, Double>> selectImportantWord(String data, int size, Nature na, boolean usecache) {
        List<Map.Entry<String, Double>> result = new ArrayList<>();
        File wordCache = new File("C:\\Users\\clh\\IdeaProjects\\ontology\\data\\Important_" +
                ((na == null)? "null" : na.toString()) +
                                size);
        // 如果发现之前有缓存好的结果，则直接取出
        if (wordCache.exists() && usecache) {
            FileReader reader = null;//定义一个fileReader对象，用来初始化BufferedReader
            Map<String, Double> map = new HashMap<>();
            try {
                reader = new FileReader(wordCache);
                BufferedReader bReader = new BufferedReader(reader);//new一个BufferedReader对象，将文件内容读取到缓存
                String s = "";
                while ((s = bReader.readLine()) != null) {//逐行读取文件内容，不读取换行符和末尾的空格
                    String [] entry = s.split(" ");
                    map.put(entry[0], Double.valueOf(entry[1]));
                }
                bReader.close();
                result.addAll(map.entrySet());
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
        if (usecache) {
            try {
                wordCache.createNewFile();
                Writer write = new OutputStreamWriter(new FileOutputStream(wordCache), "UTF-8");
                for (int i = 0; i < result.size(); i++) {
                    write.write(result.get(i).getKey() + " " + result.get(i).getValue() + "\n");
                }
                write.flush();
                write.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    static public void buildProtegeOwlFromEntryList(ArrayList<Entry> el, String resultPath) {
        om = new OwlModel();
        om.convertClassToOwl(resultPath);
    }

    static private void generateDocuments(ArrayList<Entry> el) {
        int docNum = 60;
        try {
            String content = "This is the content to write into file";

            File file = new File("/users/mkyong/filename.txt");

            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();

            System.out.println("Done");

        } catch (IOException e) {
            e.printStackTrace();
        }
        for(Entry e: entryList) {
        }
    }
    static private void buildEventObjectOperation(ArrayList<Entry> el) {
        int count = 0;
        for (Entry entry : el) {
            // 解析Equipment与Attribute
            Pattern p = Pattern.compile("[A-Za-z]+-[A-Za-z0-9]+/?[A-Za-z0-9]*");
            Matcher m = p.matcher(entry.para);
            String attrname = entry.para;
            List<Equipment> equilist = new ArrayList<>();
            while (m.find()) {
                count = count + 1;
                if (count > 500)
                    return;
                String eqname = m.group();
                Equipment eq = Equipment.GetEquipment(eqname);
                equilist.add(eq);
                attrname = attrname.replace(eqname, "").replace("\\", "");
            }
            for (Equipment e: equilist) {
                Attribute attr = e.GetAttribute(attrname);
                Attribute.Bias bias = attr.AddBias(entry.bias);
                /* 解析Event
                 * InitEvent为 entry.cause
                 * Middle Event为 Equipment + Attribute + Bias
                 * Final event为 entry.conseq
                 */
                FinalEvent finale = FinalEvent.GetFinalEvent(e, entry.conseq);
                MiddleEvent middlee = MiddleEvent.GetMiddleEvent(e, attr, bias);
                InitEvent inite = InitEvent.GetInitEvent(e, entry.cause);
                inite.middlee = middlee;
                inite.finale = finale;
                middlee.inite = inite;
                middlee.finale = finale;
                finale.inite = inite;
                finale.middlee = middlee;
                inite.riskScore = entry.possibiliy;
                finale.op = new Precaution(entry.suggestion);
            }
        }
    }
    static private void envInit() {
        jsonParser = new JsonParser();  //创建json解析器
        causeDict = Arrays.asList("造成", "引起", "引发");
        entryList = new ArrayList<>();
        Log.loglevel = 1;
    }
    public static void run() {
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
        List<Map.Entry<String, Double>> vWords = selectImportantWord(dataStr, 100, Nature.v, true);
        List<Map.Entry<String, Double>> nWords = selectImportantWord(dataStr, 1000, Nature.n, true);
        Log.Debug(vWords.toString());
        Log.Debug(nWords.toString());
        // 将统计到的关键词用K-Means算法进行聚类，矩阵纵坐标为所有的关键字，横坐标为excel表格的每一行，值
        // 为TF-IDF值
        // 每个关键词之间的欧式距离为单词向量的欧式距离

        // 步骤4：分析json文档，构造Event、iobject、IOperation三元表达
        buildEventObjectOperation(entryList);

        // 步骤5：利用步骤3与步骤4的输出结果建立关系图

        // 步骤6：在关系图的基础上建立“后果”的倒排索引

        // 步骤7：利用关系图与倒排索引进行本体构建
        buildProtegeOwlFromEntryList(entryList, "entrylist.owl");

        /* 对每个查询语句进行分词，分别对每个分词结果，在本体中进行搜索得出关联实体与实例集，这些集合的∩
         * 即为候选结果。*/
        testFunctinos();
    }

}