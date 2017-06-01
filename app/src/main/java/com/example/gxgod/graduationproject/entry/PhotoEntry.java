package com.example.gxgod.graduationproject.entry;

import java.util.List;

/**
 * Created by Administrator on 2017/2/23 0023.
 */
public class PhotoEntry {
    private List<Result> results;
    public static class Result{
        private String url;
        public String getUrl() {
            return url;
        }
    }
    public List<Result> getResults() {
        return results;
    }
}
