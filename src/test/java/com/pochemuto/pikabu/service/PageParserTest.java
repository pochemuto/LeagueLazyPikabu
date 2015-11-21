package com.pochemuto.pikabu.service;

import org.junit.Test;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 22.11.2015
 */
public class PageParserTest {

    @Test
    public void testFindUrls() throws Exception {
        PageParser parser = new PageParser();

        assertThat(parser.findUrls("правда, через три часа исправили: http://www.rbc.ru/rbcfreenews/5650900c9a79476a38d68e35, вот так вот."),
            contains("http://www.rbc.ru/rbcfreenews/5650900c9a79476a38d68e35"));

        assertThat(parser.findUrls("правда, через три http://pikabu.ru/story/_3787186 часа исправили: http://www.rbc.ru/rbcfreenews/5650900c9a79476a38d68e35, вот так вот."),
            contains("http://www.rbc.ru/rbcfreenews/5650900c9a79476a38d68e35"));
        assertThat(parser.findUrls("правда, через три http://pikabu.ru/story/_3787186 часа исправили: http://www.rbc.ru/rbcfreenews/5650900c9a79476a38d68e35, вот так вот."),
            hasSize(1));
    }
}