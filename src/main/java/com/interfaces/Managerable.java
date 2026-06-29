package com.interfaces;

public interface Managerable {
    void add(Object obj);
    void delete(String id);
    Object findById(String id);
}
