package com.mendix.demo.web.lucene;

import com.mendix.demo.exception.MendixAppException;
import com.mendix.demo.web.model.Result;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by naseers on 15/04/2018.
 */
public class LuceneIndexerAndSearcher {

    private static final Logger LOG = LoggerFactory.getLogger(LuceneIndexerAndSearcher.class);

    private static final String INDEX_DIRECTORY = "C:\\Personal\\Projects\\mendix-app\\src\\main\\resources\\recipes\\index";

    public static final int MAX_HIT_PER_PAGE = 10;
    public static final Version LUCENE_VERSION = Version.LUCENE_36;

    public static final String FILENAME_FIELD = "fileName";

    private static FieldDef[] FIELD_DEFS = {
            new FieldDef("title", Field.Store.YES, Field.Index.ANALYZED, "/recipeml/recipe/head/title", FieldTypes.STRING),
            new FieldDef("category", Field.Store.YES, Field.Index.ANALYZED, "/recipeml/recipe/head/categories/cat", FieldTypes.LIST)};


    private enum FieldTypes {
        STRING,
        LIST;
    }

    private static class FieldDef {
        String fieldName;
        Field.Store fieldStore;
        Field.Index fieldIndex;
        XPathExpression valueXpathExpr;
        FieldTypes fieldType;

        public FieldDef(String fieldName, Field.Store fieldStore, Field.Index fieldIndex, String valueXpath, FieldTypes fieldType) {
            this.fieldName = fieldName;
            this.fieldStore = fieldStore;
            this.fieldIndex = fieldIndex;
            this.fieldType = fieldType;

            XPath xpath = XPathFactory.newInstance().newXPath();
            try {
                valueXpathExpr = xpath.compile(valueXpath);
            } catch (XPathExpressionException e) {
                throw new IllegalArgumentException("Invalid xpath: " + valueXpath, e);
            }
        }
    }

    public void indexFiles(String xmlDataDirPath)
            throws IOException, SAXException, XPathExpressionException, ParserConfigurationException, ParseException, InvalidTokenOffsetsException, MendixAppException {
        File xmlDataFileDir = new File(xmlDataDirPath);
        if ((xmlDataFileDir.exists()) && (xmlDataFileDir.canRead())) {
            File indexFileDir = new File(INDEX_DIRECTORY);
            if ((indexFileDir.exists()) && (indexFileDir.canRead()) && (indexFileDir.canWrite())) {
                File[] xmlFiles = xmlDataFileDir.listFiles((dir, name) -> name.endsWith("xml"));
                indexFilesInDirectory(indexFileDir, xmlFiles);
            } else {
                LOG.error("Index directory '" + indexFileDir.getAbsolutePath() + "' does not exist or is not writeable/readable, please check the path");
            }
        } else {
            LOG.error("Document directory '" + xmlDataFileDir.getAbsolutePath() + "' does not exist or is not readable, please check the path");
        }
    }

    public void addRecipe(String recipeml)
            throws IOException, SAXException, XPathExpressionException, ParserConfigurationException, ParseException, InvalidTokenOffsetsException, MendixAppException {
        File indexFileDir = new File(INDEX_DIRECTORY);
        if ((indexFileDir.exists()) && (indexFileDir.canRead()) && (indexFileDir.canWrite())) {
            Directory indexDir = FSDirectory.open(indexFileDir);
            Analyzer analyzer = new StandardAnalyzer(LUCENE_VERSION);
            IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, analyzer);
            config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
            IndexWriter indexWriter = new IndexWriter(indexDir, config);

            addFileToIndex(indexWriter, new InputSource(new StringReader(recipeml)));

            indexWriter.forceMergeDeletes();
            LOG.info("Total number of indexed documents: " + indexWriter.numDocs());
            indexWriter.close();
        } else {
            LOG.error("Index directory '" + indexFileDir.getAbsolutePath() + "' does not exist or is not writeable/readable, please check the path");
        }

    }


    private void indexFilesInDirectory(File indexFileDir, File[] xmlFiles)
            throws XPathExpressionException, ParserConfigurationException, SAXException, IOException, MendixAppException {
        Directory indexDir = FSDirectory.open(indexFileDir);
        Analyzer analyzer = new StandardAnalyzer(LUCENE_VERSION);
        IndexWriterConfig config = new IndexWriterConfig(LUCENE_VERSION, analyzer);
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter indexWriter = new IndexWriter(indexDir, config);

        for (int i = 0; i < xmlFiles.length; i++) {
            LOG.info("indexing " + xmlFiles[i]);
            InputStream in = new FileInputStream(xmlFiles[i]);
            addFileToIndex(indexWriter, new InputSource(in));
        }
        indexWriter.forceMergeDeletes();
        LOG.info("Total number of indexed documents: " + indexWriter.numDocs());
        indexWriter.close();
    }


    public void addFileToIndex(IndexWriter indexWriter, InputSource source)
            throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, MendixAppException {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = domFactory.newDocumentBuilder();
        org.w3c.dom.Document xmlDoc = builder.parse(source);

        org.apache.lucene.document.Document luceneDoc = new org.apache.lucene.document.Document();

        for (int i = 0; i < FIELD_DEFS.length; i++) {
            evaluateAndAdd(luceneDoc, FIELD_DEFS[i], xmlDoc);
        }
        Term idTerm = new Term("id", luceneDoc.get("title"));
        luceneDoc.add(new Field("id", luceneDoc.get("title"), Field.Store.YES, Field.Index.NOT_ANALYZED_NO_NORMS));
        indexWriter.updateDocument(idTerm, luceneDoc);
    }

    private void evaluateAndAdd(org.apache.lucene.document.Document luceneDoc, FieldDef fieldDef, org.w3c.dom.Document xmlDoc) throws XPathExpressionException, MendixAppException {
        switch (fieldDef.fieldType) {
            case STRING:
                String result = (String) fieldDef.valueXpathExpr.evaluate(xmlDoc, XPathConstants.STRING);
                luceneDoc.add(new Field(fieldDef.fieldName, result, fieldDef.fieldStore, fieldDef.fieldIndex));
                break;
            case LIST:
                NodeList list = (NodeList) fieldDef.valueXpathExpr.evaluate(xmlDoc, XPathConstants.NODESET);
                for (int i = 0; i < list.getLength(); i++) {
                    Node node = list.item(i);
                    luceneDoc.add(new Field(fieldDef.fieldName, node.getTextContent(), fieldDef.fieldStore, fieldDef.fieldIndex));
                }
                break;
            default:
                throw new MendixAppException(String.format("Unknown evaluation type [%s]", fieldDef.fieldType));
        }
    }


    public List<Result> search(Map<String, String[]> params)
            throws IOException, ParseException, InvalidTokenOffsetsException {

        Directory indexDir = FSDirectory.open(new File(INDEX_DIRECTORY));
        IndexReader indexReader = IndexReader.open(indexDir);
        IndexSearcher searcher = new IndexSearcher(indexReader);
        Analyzer analyzer = new StandardAnalyzer(LUCENE_VERSION);
        Query query;
        if (params != null && params.size() > 0) {
            query = getQuery(params, analyzer);
        } else {
            query = new MatchAllDocsQuery();
        }
        TopScoreDocCollector collector = TopScoreDocCollector.create(MAX_HIT_PER_PAGE, true);
        LOG.info("Lucene Query String -> ", query.toString());
        searcher.search(query, collector);
        return displayHits(collector, searcher);
    }

    public List<Result> getCategories() throws IOException, ParseException, InvalidTokenOffsetsException {
        List<Result> results = search(new HashMap<>());
        Set<String> categoriesSet = new HashSet<>();
        for (int i = 0; i < results.size(); i++) {
            categoriesSet.addAll(Arrays.asList(results.get(i).getCategories()));
        }
        Result result = new Result();
        result.setCategories(categoriesSet.toArray(new String[categoriesSet.size()]));
        return Arrays.asList(result);
    }


    public Query getQuery(Map<String, String[]> params, Analyzer analyzer)
            throws IOException, ParseException {
        Collection<Map.Entry<String, String[]>> filters = params
                .entrySet()
                .stream()
                .filter(kv -> Arrays.stream(FIELD_DEFS).anyMatch(f -> f.fieldName.equals(kv.getKey())))
                .collect(Collectors.toList());

        BooleanQuery mainQuery = new BooleanQuery();
        filters.stream().forEach(kv -> {
            try {
                Query query = new QueryParser(LUCENE_VERSION, kv.getKey(),
                        analyzer).parse(createTextQuery(kv.getValue(), "AND"));
                mainQuery.add(query, BooleanClause.Occur.MUST);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        });
        return mainQuery;
    }

    public String getQueryString(Map<String, String[]> params)
            throws IOException {
        Collection<Map.Entry<String, String[]>> filters = params
                .entrySet()
                .stream()
                .filter(kv -> Arrays.stream(FIELD_DEFS).anyMatch(f -> f.fieldName.equals(kv.getKey())))
                .collect(Collectors.toList());

        StringBuffer query = new StringBuffer();
        filters.stream().forEach(kv -> {
            String term = String.format("%s:(%s)", kv.getKey(), createTextQuery(kv.getValue(), "AND"));
            query.append(term);
        });
        return query.toString();
    }


    private String createTextQuery(String[] values, String conj) {
        String[] wrapped = Arrays.stream(values).map(v -> "\"" + v + "\"").toArray(String[]::new);
        return values.length == 0 ? "*:*" : String.join(" " + conj + " ", wrapped);
    }


    public List<Result> displayHits(TopScoreDocCollector collector, IndexSearcher searcher)
            throws IOException, InvalidTokenOffsetsException {
        ScoreDoc[] hits = collector.topDocs().scoreDocs;

        LOG.info("Found " + collector.getTotalHits() + " hits.");
        List<Result> results = new ArrayList<>();
        for (int i = 0; i < hits.length; i++) {
            org.apache.lucene.document.Document doc = searcher.doc(hits[i].doc);
            LOG.info("<Hit " + (i + 1) + ", score " + hits[i].score + ">");
            LOG.info("Title: " + doc.get("title"));

            Result result = new Result();
            result.setTitle(doc.get("title"));
            result.setCategories(doc.getValues("category"));
            results.add(result);
        }
        return results;
    }
}
