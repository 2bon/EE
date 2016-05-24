package net.dataninja.ee.textIndexer.dataGeneration;

import net.dataninja.ee.textIndexer.IndexInfo;
import net.dataninja.ee.textIndexer.XMLTextProcessor;

import java.io.IOException;

public class Wiki_title_extraction {
    private static Wiki_title_extraction ourInstance = new Wiki_title_extraction();
    private static int print=1;
    private static int count=0;

    private static XMLTextProcessor xmlTextProcessor =new XMLTextProcessor();

    public static Wiki_title_extraction getInstance() {
        return ourInstance;
    }

    private Wiki_title_extraction() {
    }

    public static Wiki_title_extraction getOurInstance () {
        return ourInstance;
    }

    public static void setOurInstance (final Wiki_title_extraction ourInstance) {
        Wiki_title_extraction.ourInstance = ourInstance;
    }

    public static int getPrint () {
        return print;
    }

    public static void setPrint (final int print) {
        Wiki_title_extraction.print = print;
    }

    public static int getCount () {
        return count;//
    }

    public static void setCount (final int count) {
        Wiki_title_extraction.count = count;
    }

    public static XMLTextProcessor getXmlTextProcessor () {
        return xmlTextProcessor;
    }

    public static void setXmlTextProcessor (final XMLTextProcessor xmlTextProcessor) {
        Wiki_title_extraction.xmlTextProcessor = xmlTextProcessor;
    }
    public static void gen(String homePath, IndexInfo idxInfo, boolean clean ) throws IOException {
        xmlTextProcessor.open(homePath,idxInfo,clean);

    }
}