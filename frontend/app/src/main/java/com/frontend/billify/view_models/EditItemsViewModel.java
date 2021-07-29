package com.frontend.billify.view_models;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.frontend.billify.R;

import java.util.ArrayList;
import java.util.Arrays;

public class EditItemsViewModel extends ViewModel {
    private ArrayList<String> itemNames;
    private ArrayList<Float> itemPrices;


    public void addItem(String itemName, float itemPrice) {
        itemNames.add(itemName);
        itemPrices.add(itemPrice);
    }

    public void removeItem(int index) {
        itemNames.remove(index);
        itemPrices.remove(index);
    }

    public ArrayList<String> getItemNames() {
        return itemNames;
    }

    public ArrayList<Float> getItemPrices() {
        return itemPrices;
    }

    public void setItemNames(ArrayList<String> itemNames) {
        this.itemNames = itemNames;
    }

    public void setItemPrices(ArrayList<Float> itemPrices) {
        this.itemPrices = itemPrices;
    }
}
