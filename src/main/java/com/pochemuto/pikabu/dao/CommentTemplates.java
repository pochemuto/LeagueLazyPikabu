package com.pochemuto.pikabu.dao;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Alexander Kramarev (pochemuto@gmail.com)
 * @date 22.11.2015
 */
@XmlRootElement(name = "comments")
public class CommentTemplates extends AbstractList<String> {

    private final Random random = new Random();

    @XmlElement(name = "c")
    private final List<String> messages = new ArrayList<>();

    @Override
    public void add(int index, String message) {
        messages.add(index, message);
    }

    @Override
    public String remove(int index) {
        return messages.remove(index);
    }

    @Override
    public String get(int index) {
        return messages.get(index);
    }

    @Override
    public int size() {
        return messages.size();
    }

    public String getRandom() {
        return get(random.nextInt(size()));
    }
}
