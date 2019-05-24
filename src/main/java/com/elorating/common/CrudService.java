package com.elorating.common;

import java.util.List;
import java.util.Optional;

public interface CrudService<T> {

    Optional<T> get(String id);
    List<T> getAll();
    T save(T model);
    void delete(String id);
    void deleteAll();
}
