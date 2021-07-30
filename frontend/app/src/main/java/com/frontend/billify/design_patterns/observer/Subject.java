package com.frontend.billify.design_patterns.observer;

import java.util.ArrayList;
import java.util.List;

public abstract class Subject {
    public List<Observer> observers;

    public Subject() {
        this.observers = new ArrayList<>();;
    }

    public void AttachObserver(Observer observer) {
        observers.add(observer);
    }

    public void DetachObserver(Observer observer) {
        observers.remove(observer);
    }

    public void Notify() {
        for (Observer observer: observers) {
            observer.Update();
        }
    }

    public void Notify(Object o) {
        for (Observer observer: observers) {
            observer.Update(o);
        }
    }
}
