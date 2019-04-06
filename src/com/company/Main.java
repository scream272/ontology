package com.company;
import com.google.gson.Gson;
/**
 *
 * 读取word文档中表格数据，支持doc、docx
 * @author Fise19
 *
 */
public class Main {
    public static void main(String[] args) {
        ExportDoc doc = new ExportDoc();
        String filePath = "D:\\clh\\毕业设计\\神华项目报告-中科.doc";
        doc.parseDoc(filePath);
        Gson gson = new Gson();
        System.out.println(gson.toJson(doc.entryList));
        System.out.println("entry num is " + doc.entryList.size());
    }
}