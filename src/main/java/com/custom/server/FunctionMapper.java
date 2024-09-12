package com.custom.server;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * @author Gemaxis
 * @date 2024/08/12 23:04
 **/
public class FunctionMapper {
    private final Map<String, Map<String, Function<Object[], Object>>> serviceMethodMap = new HashMap<>();

}
