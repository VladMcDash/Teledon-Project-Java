package org.example.repository;

public interface Repository<ID, T> {
    void add(T entity);
    void delete(ID id);
    void update(ID id, T entity);
    T findOne(ID id);
    Iterable<T> findAll();
}
//entity