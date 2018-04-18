package com.mendix.demo.config;

import com.mendix.demo.web.lucene.LuceneIndexerAndSearcher;
import com.mendix.demo.web.util.XMLValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Created by naseers on 14/04/2018.
 */
@Configuration
public class MendixAppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(MendixAppConfig.class);

    private static final String XSD_PATH = "C:\\Personal\\Projects\\mendix-app\\src\\main\\resources\\recipe.xsd";

    @Bean
    public LuceneIndexerAndSearcher indexerAndSearcher() {
        return new LuceneIndexerAndSearcher();
    }

    @Bean
    public XMLValidator xmlValidator() throws IOException {
        return new XMLValidator(XSD_PATH);
    }

}
