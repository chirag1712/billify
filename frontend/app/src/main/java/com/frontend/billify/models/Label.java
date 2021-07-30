package com.frontend.billify.models;

import org.jetbrains.annotations.NotNull;

public class Label {
    private int lid;
    private String label_name;
    private String label_color;
    private int tid;
    private String transaction_name;

    public Label(int lid, String label_name, String label_color, int tid) {
        this.lid = lid;
        this.label_name = label_name;
        this.label_color = label_color;
        this.tid = tid;
    }

    public Label(int lid, String label_name, String label_color, int tid, String transaction_name) {
        this.lid = lid;
        this.label_name = label_name;
        this.label_color = label_color;
        this.tid = tid;
        this.transaction_name = transaction_name;
    }

    public Label(int lid, String label_name, String label_color) {
        this.lid = lid;
        this.label_name = label_name;
        this.label_color = label_color;
    }

    public int getLId() {
        return lid;
    }

    public void setLId(int lid) {
        this.lid = lid;
    }

    public String getLabel_name() {
        return label_name;
    }

    public void setLabel_name(String label_name) {
        this.label_name = label_name;
    }

    public String getLabel_color() {
        return label_color;
    }

    public void setLabel_color(String label_color) {
        this.label_color = label_color;
    }

    public int getTId() {
        return tid;
    }

    public void setTId(int tid) {
        this.tid = tid;
    }

    public String getTransaction_name() {
        return transaction_name;
    }

    public void setTransaction_name(String transaction_name) {
        this.transaction_name = transaction_name;
    }

    // To display label on view transaction screen
    @NotNull
    @Override
    public String toString() {
        return label_name;
    }
}
