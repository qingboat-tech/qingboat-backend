package com.qingboat.as.utils.sensi;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class FilterResult {

    private String rst;
    private Set<String> sensitiveWords = new HashSet<>();

    public void setSensitiveWord(String word){
        sensitiveWords.add(word);
    }

}
