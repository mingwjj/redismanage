package com.meritdata.redis.bean;

import java.util.List;

public class TreeBS {

    private String id;
    private String text;
    private List<TreeBS> nodes;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public List<TreeBS> getNodes() {
        return nodes;
    }

    public void setNodes(List<TreeBS> nodes) {
        this.nodes = nodes;
    }
}
