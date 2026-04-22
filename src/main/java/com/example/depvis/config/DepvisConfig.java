package com.example.depvis.config;

import java.util.ArrayList;
import java.util.List;

public class DepvisConfig {

    public List<String> include = new ArrayList<>();
    public List<String> exclude = new ArrayList<>();
    public List<ForbiddenRule> forbidden = new ArrayList<>();
    public List<Object> layers = new ArrayList<>();

    // TODO: layered architecture rules (parse and enforce)

    public static class ForbiddenRule {
        public String from;
        public String to;
    }
}
