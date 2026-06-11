package com.interfaces;

public interface Managerable {
    public void add(Object obj);
    public void delete(String id);
    public Object findById(String id);
}
