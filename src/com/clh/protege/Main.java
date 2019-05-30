package com.clh.protege;

import com.clh.protege.ievent.FinalEvent;
import com.clh.protege.utils.Display;
import com.hankcs.hanlp.corpus.tag.Nature;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.util.iterator.ExtendedIterator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static com.clh.protege.MyProcess.om;
import static com.clh.protege.MyProcess.selectImportantWord;
import static com.clh.protege.protege.OwlModel.NS;

public class Main {
    static String[] causedict = new String[]{"原因", "由于", "造成", "因", "致", "为", "引"};
    static String[] topdict = new String[]{"偏差P", "初始原因R", "后果C", "操作O", "索引"};
    static String[] inducedict = new String[]{"多", "少", "无", "过量", "异常", "早", "晚"};
    static String[] opdict = new String[]{"怎么", "如何", "要", "能", "需", "办", "措施"};
    static public boolean isCauseWord(String w) {
        for (String s: causedict) {
            if (s.contains(w) || w.contains(s)) {
                return true;
            }
        }
        return false;
    }
    static public boolean isInduceWord(String w) {
        for (String s: inducedict) {
            if (s.contains(w) || w.contains(s)) {
                return true;
            }
        }
        return false;
    }
    static public boolean isOpWord(String w) {
        for (String s: opdict) {
            if (s.contains(w) || w.contains(s)) {
                return true;
            }
        }
        return false;
    }
    static public boolean isTopConcept(String s) {
        for (String t: topdict) {
            if (s.equals(t))
                return true;
        }
        return false;
    }
    static public boolean like(String a, String b) {
        String[] a_list = a.split(" ");
        String[] b_list = b.split(" ");
        for (String aa: a_list) {
            if (b.contains(aa))
                return true;
        }
        for (String bb: b_list) {
            if (a.contains(bb))
                return true;
        }
        return false;
    }
    static public String getOntName(OntClass o) {
        return o.getURI().replace(NS, "");
    }
    static public String query(String q) {
        boolean findp = false;
        boolean findr = false;
        boolean findc = false;
        boolean findo = false;
        boolean findi = false;
        if (isOpWord(q)) {
            findo = true;
        }
        String result = "";
        result += "启动问题分析>>\n";
        List<Map.Entry<String, Double>> ws = selectImportantWord(q, 5, null, false);
        result += "\n========== 阶段1：从问题中抽取关键词 ==========\n";
        List<String> querywords = new ArrayList<>();
        List<String> eventwords = new ArrayList<>();
        int keywordCount = 0;
        for (Map.Entry<String, Double> e: ws) {
            keywordCount += 1;
            querywords.add(e.getKey());
            result = result + String.format("关键词 %d：%s\n", keywordCount, e.getKey());
        }

        result += "\n========== 阶段2：对Query Words 基于本体进行概念提取 ==========\n";
        for (String s: querywords) {
            result += ">>分析关键词 " + s + "\n";
            if (isCauseWord(s)) {
                result += "该词为表示因果关系的连接词\n\n";
                findr = true;
                continue;
            }
            if (isInduceWord(s)) {
                result += "该词为表示偏差的引导词\n\n";
                findp = true;
                continue;
            }
            ExtendedIterator<OntClass> iter = om.baseOnt.listClasses();
            while (iter.hasNext()) {
                boolean stopflag = false;
                OntClass item = iter.next();
                System.out.println("概念: " + item.getURI().replace(NS, ""));
                if (like(item.getURI().replace(NS, ""), s)) {
                    result += "== 找到相关实例/概念，开始递归向上查找 ==\n";
                    eventwords.add(s);
                    OntClass classiter = item;
                    do {
                        result += String.format("--> %s\n", classiter.getURI().replace(NS, ""));
                        if (isTopConcept(classiter.getURI().replace(NS, ""))) {
                            String tmp = classiter.getURI().replace(NS, "");
                            if (tmp.equals("偏差P"))
                                findp = true;
                            if (tmp.equals("初始原因R"))
                                findr = true;
                            if (tmp.equals("后果C"))
                                findc = true;
                            if (tmp.equals("操作O"))
                                findo = true;
                            if (tmp.equals("索引"))
                                findi = true;
                            stopflag = true;
                            break;
                        }
                        classiter = classiter.getSuperClass();
                    } while (classiter != null);
                }
                if (stopflag)
                    break;
            }
            result += "\n";
        }

        result += "\n========== 阶段3：基于本体拓扑的搜索 ==========\n";
        result += "由阶段2可知，涉及到的顶层概念及其关系：\n";
        if (findr || findp) {
            result += "->[初始原因]->[偏差]";
        }
        if (findc) {
            result += "->[后果]";
        }
        if (findo) {
            result += "->[措施]";
        }
        result += "\n\n列出所有实例：\n";
        for (String s: eventwords) {
            for (Map.Entry<String, FinalEvent> finalEventEntry : FinalEvent.allFinalEventMap.entrySet()) {
                if (like(s, finalEventEntry.getKey())) {
                    if (findp || findr) {
                        result += String.format("[初始原因]：%s\n", finalEventEntry.getValue().inite.content);
                        result += String.format("--[初始原因类型]：%s\n", finalEventEntry.getValue().inite.type);
                        result += String.format("[偏差]：%s\n", finalEventEntry.getValue().middlee.content);
                    }
                    result += String.format("[后果]：%s\n", finalEventEntry.getKey());
                    result += String.format("--[后果类型]：%s\n", finalEventEntry.getValue().type);
                    if (findo) {
                        result += String.format("[解决措施]：%s\n", finalEventEntry.getValue().op.name);
                    }
                    result += "\n";
                }
            }
        }
        return result;
    }
    static private void queryEngine() {
        // 显示UI界面
        Display d = new Display();
        d.showFrame();
        //创建Scanner对象
        //System.in表示标准化输出，也就是键盘输出
        Scanner sc = new Scanner(System.in);
        //利用hasNextXXX()判断是否还有下一输入项
        while (sc.hasNext()) {
            //利用nextXXX()方法输出内容
            String str = sc.next();
            System.out.println(str);
        }
    }
    public static void main(String[] args) {
        MyProcess p = new MyProcess();
        MyProcess.run();
        //        // 步骤8：本体的使用
        queryEngine();
    }
}
