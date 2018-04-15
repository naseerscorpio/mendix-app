package com.mendix.demo.web.service;

import com.mendix.demo.exception.MendixAppException;
import com.mendix.demo.web.RequestController;
import com.mendix.demo.web.lucene.LuceneIndexerAndSearcher;
import com.mendix.demo.web.model.Result;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Created by naseers on 15/04/2018.
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {RecipeService.class, RequestController.class, LuceneIndexerAndSearcher.class})
@RestClientTest(RecipeService.class)
public class RecipeServiceTest {

    @Autowired
    private RecipeService recipeService;

    @Test
    public void getAllRecipesShouldReturnAllAvailableRecipes() throws ParseException, InvalidTokenOffsetsException, IOException {
        List<Result> resultList = recipeService.getAllRecipes(new HashMap<>());
        Assert.assertTrue("Invalid Result List! ", resultList.size() > 0);
    }

    @Test
    public void testGetAllCategories() throws ParseException, InvalidTokenOffsetsException, IOException {
        List<Result> resultList = recipeService.getAllCategories();
        Assert.assertTrue("Invalid Result List! ", resultList.size() > 0);
    }

    @Test
    public void testAddRecipe() throws ParseException, InvalidTokenOffsetsException, IOException, SAXException, ParserConfigurationException, XPathExpressionException, MendixAppException {
        List<Result> resultList = recipeService.addRecipe("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<recipeml version=\"0.5\">\n" +
                "    <recipe>\n" +
                "        <head>\n" +
                "            <title>Lucene Cake</title>\n" +
                "            <categories>\n" +
                "                <cat>Lucene</cat>\n" +
                "            </categories>\n" +
                "            <yield>1</yield>\n" +
                "        </head>\n" +
                "   </recipe>\n" +
                "</recipeml>\n");
        Assert.assertTrue("Invalid Result List! ", resultList.size() > 0);
    }
}
