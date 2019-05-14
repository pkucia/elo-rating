package com.elorating.common;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public abstract class AbstractCrudService<T, R extends MongoRepository<T, String>> {

    protected R repository;

    protected AbstractCrudService(R repository) {
        this.repository = repository;
    }

    public Optional<T> get(String id) {
        return repository.findById(id);
    }

    public List<T> getAll() {
        return repository.findAll();
    }

    public T save(T model) {
        return repository.save(model);
    }

    public List<T> save(Iterable<T> models) {
        return repository.saveAll(models);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
