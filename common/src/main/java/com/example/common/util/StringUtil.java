package com.example.common.util;

import java.util.Arrays;
import java.util.List;

public class StringUtil<T> {
    public List ToList(String string){
        List<String> lis = Arrays.asList(string.split(""));
        return lis;
    }

}
