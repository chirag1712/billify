package com.frontend.billify.models;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class Label {
    private int lid;
    private String label_name;
    private String label_color;
    public static ArrayList<Label> uniqueLabels = new ArrayList<>();

    static {
        uniqueLabels.add(new Label(1, "Unlabelled" ,"#F0A500"));
        uniqueLabels.add(new Label(2, "Food" ,"#FF4848"));
        uniqueLabels.add(new Label(3, "Entertainment" ,"#035397"));
        uniqueLabels.add(new Label(4, "Groceries" ,"#1EAE98"));
        uniqueLabels.add(new Label(5, "Shopping" ,"#334756"));
        uniqueLabels.add(new Label(6, "Electronics" ,"#D62AD0"));
        uniqueLabels.add(new Label(7, "Housing" ,"#6930C3"));
    }

    public Label(int lid, String label_name, String label_color) {
        this.lid = lid;
        this.label_name = label_name;
        this.label_color = label_color;
    }


    public Label(Label label) {
        this.lid = label.lid;
        this.label_name = label.label_name;
        this.label_color = label.label_color;
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

    // To display label on view transaction screen
    @NotNull
    @Override
    public String toString() {
        return label_name;
    }

    public static ArrayList<Label> getUniqueLabels() {
        return uniqueLabels;
    }
}
