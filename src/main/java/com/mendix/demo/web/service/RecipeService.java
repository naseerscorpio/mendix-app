package com.mendix.demo.web.service;

import com.mendix.demo.exception.MendixAppException;
import com.mendix.demo.web.lucene.LuceneIndexerAndSearcher;
import com.mendix.demo.web.model.Result;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by naseers on 14/04/2018.
 */
@Component
public class RecipeService {

    private static final Logger LOG = LoggerFactory.getLogger(RecipeService.class);

    @Autowired
    private LuceneIndexerAndSearcher indexerAndSearcher;

    private static final String INPUT_DIRECTORY = "C:\\Personal\\Projects\\mendix-app\\src\\main\\resources\\recipes";

    @PostConstruct
    public void init() throws ParserConfigurationException, IOException, XPathExpressionException, ParseException, InvalidTokenOffsetsException, SAXException, MendixAppException {
        indexerAndSearcher.indexFiles(INPUT_DIRECTORY);
    }

    public List<Result> getAllRecipes(Map<String, String[]> params) throws ParseException, InvalidTokenOffsetsException, IOException {
        return indexerAndSearcher.search(params);
    }

    public List<Result> getAllCategories() throws ParseException, InvalidTokenOffsetsException, IOException {
        return indexerAndSearcher.getCategories();
    }

    public List<Result> addRecipe(String recipeml) throws ParseException, InvalidTokenOffsetsException, IOException, SAXException, ParserConfigurationException, XPathExpressionException, MendixAppException {
        indexerAndSearcher.addRecipe(recipeml);
        return getAllRecipes(new HashMap<>());
    }

}
