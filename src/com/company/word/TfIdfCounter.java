/*
 * <author>Hankcs</author>
 * <email>me@hankcs.com</email>
 * <create-date>2016-09-12 PM4:22</create-date>
 *
 * <copyright file="TfIdfKeyword.java" company="码农场">
 * Copyright (c) 2016, 码农场. All Right Reserved, http://www.hankcs.com/
 * This source is subject to Hankcs. Please contact Hankcs to get more information.
 * </copyright>
 */
package com.company.word;

import com.hankcs.hanlp.algorithm.MaxHeap;
import com.hankcs.hanlp.corpus.document.sentence.Sentence;
import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.summary.KeywordExtractor;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.NotionalTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TF-IDF统计工具兼关键词提取工具
 *
 * @author hankcs
 */
public class TfIdfCounter extends KeywordExtractor
{
    private boolean filterStopWord;
    private Map<Object, Map<String, Double>> tfMap;
    private Map<Object, Map<String, Double>> tfidfMap;
    private Map<String, Double> idf;

    public TfIdfCounter()
    {
        this(true);
    }

    public TfIdfCounter(boolean filterStopWord)
    {
        this(NotionalTokenizer.SEGMENT, filterStopWord);
    }

    public TfIdfCounter(Segment defaultSegment, boolean filterStopWord)
    {
        super(defaultSegment);
        this.filterStopWord = filterStopWord;
        tfMap = new HashMap<Object, Map<String, Double>>();
    }

    public TfIdfCounter(Segment defaultSegment)
    {
        this(defaultSegment, true);
    }

    @Override
    public List<String> getKeywords(List<Term> termList, int size)
    {
        return getKeywordsWithTfIdf(termList, size);
    }

    public List<String> getKeywordsWithTfIdf(String document, int size, Nature na)
    {
        return getKeywordsWithTfIdf(preprocess(document, na), size);
    }


    public List<String> getKeywordsWithTfIdf(List<Term> termList, int size)
    {
        List<String> result = new ArrayList<>();
        if (idf == null)
            compute();

        Map<String, Double> tfIdf = TfIdf.tfIdf(TfIdf.tf(convert(termList)), idf);
        List<Map.Entry<String, Double>> tpn = topN(tfIdf, size);
        for (int i = 0; i < tpn.size(); i++) {
            result.add(tpn.get(i).getKey());
        }
        return result;
    }

    public void add(Object id, List<Term> termList)
    {
        List<String> words = convert(termList);
        Map<String, Double> tf = TfIdf.tf(words);
        tfMap.put(id, tf);
        idf = null;
    }

    private static List<String> convert(List<Term> termList)
    {
        List<String> words = new ArrayList<String>(termList.size());
        for (Term term : termList)
        {
            words.add(term.word);
        }
        return words;
    }

    public void add(List<Term> termList)
    {
        add(tfMap.size(), termList);
    }

    protected void myfilter(List<Term> termList, Nature na)
    {
        ListIterator<Term> listIterator = termList.listIterator();
        while (listIterator.hasNext())
        {
            Term t = listIterator.next();
            // 如果Term属于停用词或者Term仅仅是一个字符，则删除
            if (!shouldInclude(t) || t.word.length() == 1 || t.nature != na)
                listIterator.remove();
        }
    }


    public List<Term> getSpecialTerms(String s) {

        List<Term> strs = new ArrayList<>();
        Pattern p = Pattern.compile("[A-Za-z]+-[A-Za-z0-9]+/?[A-Za-z0-9]*");
        Matcher m = p.matcher(s);
        while (m.find()) {
            strs.add(new Term(m.group().replace(" ",""), Nature.n));
        }
        Set<Term> set = new HashSet<>(strs);
        return new ArrayList<>(set);
    }
    private List<Term> preprocess(String text, Nature na)
    {
        List<Term> termList = NLPTokenizer.segment(text);
        termList.addAll(getSpecialTerms(text));
        if (filterStopWord)
        {
            myfilter(termList, na);
        }
        return termList;
    }

    public Map<Object, Map<String, Double>> compute()
    {
        idf = TfIdf.idfFromTfs(tfMap.values());
        tfidfMap = new HashMap<Object, Map<String, Double>>(idf.size());
        for (Map.Entry<Object, Map<String, Double>> entry : tfMap.entrySet())
        {
            Map<String, Double> tfidf = TfIdf.tfIdf(entry.getValue(), idf);
            tfidfMap.put(entry.getKey(), tfidf);
        }
        return tfidfMap;
    }

    public List<Map.Entry<String, Double>> getKeywordsOf(Object id)
    {
        return getKeywordsOf(id, 10);
    }


    public List<Map.Entry<String, Double>> getKeywordsOf(Object id, int size)
    {
        Map<String, Double> tfidfs = tfidfMap.get(id);
        if (tfidfs == null) return null;

        return topN(tfidfs, size);
    }

    private List<Map.Entry<String, Double>> topN(Map<String, Double> tfidfs, int size)
    {
        MaxHeap<Map.Entry<String, Double>> heap = new MaxHeap<Map.Entry<String, Double>>(size, new Comparator<Map.Entry<String, Double>>()
        {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
            {
                return o1.getValue().compareTo(o2.getValue());
            }
        });

        heap.addAll(tfidfs.entrySet());
        return heap.toList();
    }

    public Set<Object> documents()
    {
        return tfMap.keySet();
    }

    public Map<Object, Map<String, Double>> getTfMap()
    {
        return tfMap;
    }

    public List<Map.Entry<String, Double>> sortedAllTf()
    {
        return sort(allTf());
    }

    public List<Map.Entry<String, Integer>> sortedAllTfInt()
    {
        return doubleToInteger(sortedAllTf());
    }

    public Map<String, Double> allTf()
    {
        Map<String, Double> result = new HashMap<String, Double>();
        for (Map<String, Double> d : tfMap.values())
        {
            for (Map.Entry<String, Double> tf : d.entrySet())
            {
                Double f = result.get(tf.getKey());
                if (f == null)
                {
                    result.put(tf.getKey(), tf.getValue());
                }
                else
                {
                    result.put(tf.getKey(), f + tf.getValue());
                }
            }
        }

        return result;
    }

    private static List<Map.Entry<String, Double>> sort(Map<String, Double> map)
    {
        List<Map.Entry<String, Double>> list = new ArrayList<Map.Entry<String, Double>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Double>>()
        {
            @Override
            public int compare(Map.Entry<String, Double> o1, Map.Entry<String, Double> o2)
            {
                return o2.getValue().compareTo(o1.getValue());
            }
        });

        return list;
    }

    private static List<Map.Entry<String, Integer>> doubleToInteger(List<Map.Entry<String, Double>> list)
    {
        List<Map.Entry<String, Integer>> result = new ArrayList<Map.Entry<String, Integer>>(list.size());
        for (Map.Entry<String, Double> entry : list)
        {
            result.add(new AbstractMap.SimpleEntry<String, Integer>(entry.getKey(), entry.getValue().intValue()));
        }

        return result;
    }
}
