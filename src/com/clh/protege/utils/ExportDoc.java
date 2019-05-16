package com.clh.protege.utils;

import java.io.FileInputStream;
import java.util.*;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;
import org.apache.poi.hwpf.usermodel.Table;
import org.apache.poi.hwpf.usermodel.TableCell;
import org.apache.poi.hwpf.usermodel.TableIterator;
import org.apache.poi.hwpf.usermodel.TableRow;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

/**
 *
 * 读取word文档中表格数据，支持doc、docx
 * @author Fise19
 *
 */
public class ExportDoc {
    /**
     * 读取文档中表格
     * @param filePath
     */
    static private int entryStartNum = 7;
    public ArrayList<Entry> entryList = new ArrayList<>();
    public void parseDoc(String filePath){
        try{
            FileInputStream in = new FileInputStream(filePath);//载入文档
            // 处理doc格式 即office2003版本
            POIFSFileSystem pfs = new POIFSFileSystem(in);
            HWPFDocument hwpf = new HWPFDocument(pfs);
            Range range = hwpf.getRange();//得到文档的读取范围
            TableIterator it = new TableIterator(range);
            // 迭代文档中的表格
            int num = 0;
            while (it.hasNext()) {
                Table tb = (Table) it.next();
                boolean is_node = false;
                //迭代行，默认从0开始,可以依据需要设置i的值,改变起始行数，也可设置读取到那行，只需修改循环的判断条件即可
                for (int i = 0; i < tb.numRows(); i++) {
                    TableRow tr = tb.getRow(i);
                    Entry tmpEntry = null;
                    if (i >= entryStartNum) {
                        tmpEntry = new Entry("", "", "", "", "",
                                            "", "", "", "");
                    }
                    //迭代列，默认从0开始
                    for (int j = 0; j < tr.numCells(); j++) {
                        TableCell td = tr.getCell(j);//取得单元格
                        String s = td.text();
                        // 取得单元格的内容
                        if(null != s && !"".equals(s)){
                            s = s.substring(0, s.length()-1);
                        }
                        if (s.equals("神华宁煤400万吨/年煤间接液化油品合成HAZOP安全评价项目会议记录表")) {
                            is_node = true;
                            num += 1;
                        }
                        if (is_node && tmpEntry != null && j >= 1) {
                            tmpEntry.setNumAttr(j, s.replaceAll("\\s*", "").replace("　", ""));
                        }
                    }
                    if (tmpEntry != null && is_node) {
                        entryList.add(tmpEntry);
                    }
                }
            }
            System.out.println("fill the blank! for " + entryList.size());
            for (int i = 1; i < entryList.size(); i++) {
                for (int j = 1; j <= Entry.attrCount; j++) {
                    if (entryList.get(i).getNumAttr(j).equals("")) {
                        entryList.get(i).setNumAttr(j, entryList.get(i - 1).getNumAttr(j));
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
