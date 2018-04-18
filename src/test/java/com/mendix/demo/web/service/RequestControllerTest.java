package com.mendix.demo.web.service;

import com.mendix.demo.web.lucene.LuceneIndexerAndSearcher;
import com.mendix.demo.web.util.XMLValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by naseers on 18/04/2018.
 */
@RunWith(SpringRunner.class)
@WebMvcTest
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LuceneIndexerAndSearcher luceneIndexerAndSearcher;

    @MockBean
    private XMLValidator xmlValidator;

    @MockBean
    private RecipeService recipeService;


    @Test
    public void testGetAllRecipes() throws Exception {
        mockMvc.perform(get("/recipes")).andExpect(status().isOk());
    }


}
