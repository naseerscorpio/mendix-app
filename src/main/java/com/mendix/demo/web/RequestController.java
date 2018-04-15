package com.mendix.demo.web;

import com.google.common.collect.Maps;
import com.mendix.demo.exception.MendixAppException;
import com.mendix.demo.web.model.Result;
import com.mendix.demo.web.response.ResponseEnvelope;
import com.mendix.demo.web.service.RecipeService;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by naseers on 14/04/2018.
 */
@RestController
@RequestMapping("recipes")
public class RequestController {

    @Autowired
    private RecipeService recipeService;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public ResponseEnvelope getAllRecipes(HttpServletRequest request) throws MendixAppException, ParseException, InvalidTokenOffsetsException, IOException {
        Map<String, String[]> params = request.getParameterMap();
        Map<String, String[]> mParams = Maps.transformValues(params, v -> Arrays.stream(v).map(i -> i.contains(" ") ? new StringBuilder(i.length() + 2).append('"').append(i).append('"').toString() : i)
                .toArray(String[]::new));
        List<Result> recipes = recipeService.getAllRecipes(mParams);
        return getResponseEnvelope(recipes, true);
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEnvelope getAllRecipeCatagories(HttpServletRequest request) throws MendixAppException, ParseException, InvalidTokenOffsetsException, IOException {
        List<Result> recipes = recipeService.getAllCategories();
        return getResponseEnvelope(recipes, true);
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEnvelope addRecipe(@RequestBody String recipe, HttpServletRequest request) throws MendixAppException, ParserConfigurationException, ParseException, IOException, XPathExpressionException, InvalidTokenOffsetsException, SAXException {
        List<Result> recipes = recipeService.addRecipe(recipe);
        return getResponseEnvelope(recipes, true);
    }


    private ResponseEnvelope getResponseEnvelope(Object data, boolean success) {
        return new ResponseEnvelope(data, success);
    }


}
