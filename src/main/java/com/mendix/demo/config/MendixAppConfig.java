package com.mendix.demo.config;

import com.mendix.demo.web.lucene.LuceneIndexerAndSearcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by naseers on 14/04/2018.
 */
@Configuration
public class MendixAppConfig {
    private static final Logger LOG = LoggerFactory.getLogger(MendixAppConfig.class);

    @Bean
    public LuceneIndexerAndSearcher indexerAndSearcher() {
        return new LuceneIndexerAndSearcher();
    }

}
