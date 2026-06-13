package com.mikolajk0wal.unittests;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.FluentQuery;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

abstract class BaseJpaInMemoryRepository<T, ID> implements JpaRepository<T, ID> {
    protected final String idFieldName;
    protected final Map<ID, T> entities = new ConcurrentHashMap<>();

    protected abstract ID generateId();

    public BaseJpaInMemoryRepository(String idFieldName) {
        this.idFieldName = idFieldName;
    }

    public BaseJpaInMemoryRepository() {
        this("id");
    }

    // Util For numeric identifiers
    protected final AtomicLong idGenerator = new AtomicLong(1);

    @Override
    public <S extends T> S save(S entity) {
        if (getId(entity) == null) {
            setId(entity, generateId());
        }
        entities.put(getId(entity), entity);
        return entity;
    }

    @Override
    public <S extends T> List<S> saveAll(Iterable<S> entities) {
        List<S> savedEntities = new ArrayList<>();
        entities.forEach(entity -> savedEntities.add(save(entity)));
        return savedEntities;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public boolean existsById(ID id) {
        return entities.containsKey(id);
    }

    @Override
    public List<T> findAll() {
        return new ArrayList<>(entities.values());
    }

    @Override
    public List<T> findAllById(Iterable<ID> ids) {
        List<T> result = new ArrayList<>();
        ids.forEach(id -> findById(id).ifPresent(result::add));
        return result;
    }

    @Override
    public long count() {
        return entities.size();
    }

    @Override
    public void deleteById(ID id) {
        entities.remove(id);
    }

    @Override
    public void delete(T entity) {
        ID id = getId(entity);
        if (id != null) {
            entities.remove(id);
        }
    }

    @Override
    public void deleteAllById(Iterable<? extends ID> ids) {
        ids.forEach(this::deleteById);
    }

    @Override
    public void deleteAll(Iterable<? extends T> entities) {
        entities.forEach(this::delete);
    }

    @Override
    public void deleteAll() {
        entities.clear();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return new ArrayList<>(entities.values());
    }

    @Override
    public Page<T> findAll(Pageable pageable) {
        List<T> allEntities = findAll(pageable.getSort());
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), allEntities.size());

        List<T> pageContent = allEntities.subList(start, end);
        return new PageImpl<>(pageContent, pageable, allEntities.size());
    }

    @Override
    public void flush() {
        // No-op for in-memory implementation
    }

    @Override
    public <S extends T> S saveAndFlush(S entity) {
        return save(entity);
    }

    @Override
    public <S extends T> List<S> saveAllAndFlush(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public void deleteAllInBatch(Iterable<T> entities) {
        deleteAll(entities);
    }

    @Override
    public void deleteAllByIdInBatch(Iterable<ID> ids) {
        deleteAllById(ids);
    }

    @Override
    public void deleteAllInBatch() {
        deleteAll();
    }

    @Override
    public T getOne(ID id) {
        return entities.get(id);
    }

    @Override
    public T getById(ID id) {
        return findById(id).orElseThrow(() -> new EntityNotFoundException("Entity not found with id: " + id));
    }

    @Override
    public T getReferenceById(ID id) {
        return getById(id);
    }

    @Override
    public <S extends T> Optional<S> findOne(Example<S> example) {
        return Optional.empty();
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example) {
        return new ArrayList<>();
    }

    @Override
    public <S extends T> List<S> findAll(Example<S> example, Sort sort) {
        return new ArrayList<>();
    }

    @Override
    public <S extends T> Page<S> findAll(Example<S> example, Pageable pageable) {
        return Page.empty();
    }

    @Override
    public <S extends T> long count(Example<S> example) {
        return 0;
    }

    @Override
    public <S extends T> boolean exists(Example<S> example) {
        return false;
    }

    @Override
    public <S extends T, R> R findBy(Example<S> example,
            Function<FluentQuery.FetchableFluentQuery<S>, R> queryFunction) {
        return null;
    }

    private void setId(T entity, ID fileId) {
        try {
            Field idField = findFieldInClassHierarchy(entity.getClass(), idFieldName);
            if (idField == null) {
                throw new RuntimeException("No id field found in class hierarchy for " + entity.getClass().getName());
            }
            idField.setAccessible(true);
            idField.set(entity, fileId);
        } catch (Exception e) {
            throw new RuntimeException("Cannot set id", e);
        }
    }

    private ID getId(T entity) {
        try {
            Field idField = findFieldInClassHierarchy(entity.getClass(), idFieldName);
            if (idField == null) {
                throw new RuntimeException("No id field found in class hierarchy for " + entity.getClass().getName());
            }
            idField.setAccessible(true);
            return (ID) idField.get(entity);
        } catch (Exception e) {
            throw new RuntimeException("Cannot get id", e);
        }
    }

    private Field findFieldInClassHierarchy(Class<?> clazz, String fieldName) {
        if (clazz == null || clazz == Object.class) {
            return null;
        }

        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            return findFieldInClassHierarchy(clazz.getSuperclass(), fieldName);
        }
    }
}
