package com.elorating.common;

import org.modelmapper.ModelMapper;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * Abstract service to handle default CRUD operations
 * @param <D> Database Document type
 * @param <R> Database Repository type
 * @param <M> API Model type
 */
public abstract class AbstractCrudService<D, R extends MongoRepository<D, String>, M> {

    protected R repository;
    protected ModelMapper mapper;
    private Class<D> documentType;
    private Class<M> modelType;

    protected AbstractCrudService(R repository, Class<D> documentType, Class<M> modelType) {
        this.repository = repository;
        this.mapper = new ModelMapper();
        this.documentType = documentType;
        this.modelType = modelType;
    }

    /**
     * It's impossible to map generic lists List<D> to List<M>
     *     and this methods needs to be implemented in each service explicit
     * @return Models list
     */
    public abstract List<M> getAll();

    public Optional<M> get(String id) {
        Optional<D> document = repository.findById(id);
        return document.map(doc -> mapper.map(document.get(), modelType));
    }

    public M save(M model) {
        D document = mapper.map(model, documentType);
        return mapper.map(repository.save(document), modelType);
    }

    public void delete(String id) {
        repository.deleteById(id);
    }

    public void deleteAll() {
        repository.deleteAll();
    }
}
