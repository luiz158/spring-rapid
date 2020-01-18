package io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.HibernateForceEagerFetchUtil;
import io.github.vincemann.generic.crud.lib.test.forceEagerFetch.proxy.abs.HibernateForceEagerFetchProxy;
import lombok.Getter;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;


@Getter
public class CrudRepoHibernateForceEagerFetchProxy
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends JpaRepository<E, Id>
                >
        extends HibernateForceEagerFetchProxy
        implements JpaRepository<E, Id> {

    private R repository;

    public CrudRepoHibernateForceEagerFetchProxy(R repository, HibernateForceEagerFetchUtil util) {
        super(util);
        this.repository = repository;
    }


    @Override
    public <S extends E> List<S> saveAll(Iterable<S> iterable) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.saveAll(iterable));
    }

    @Override
    public <S extends E> S save(S s) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.save(s));
    }
    @Override
    public List<E> findAll() {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.findAll());
    }

    @Override
    public List<E> findAllById(Iterable<Id> iterable) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.findAllById(iterable));
    }

    @Override
    public Optional<E> findById(Id id) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_OptionalValue_NoException(() -> repository.findById(id));
    }

    @Override
    public boolean existsById(Id id) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.existsById(id));
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public void deleteById(Id id) {
        repository.deleteById(id);
    }

    @Override
    public void delete(E e) {
        repository.delete(e);
    }

    @Override
    public void deleteAll(Iterable<? extends E> iterable) {
        repository.deleteAll(iterable);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    @Override
    public List<E> findAll(Sort sort) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.findAll(sort));
    }

    @Override
    public void flush() {
        repository.flush();
    }

    @Override
    public <S extends E> S saveAndFlush(S s) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.saveAndFlush(s));
    }

    @Override
    public void deleteInBatch(Iterable<E> iterable) {
        repository.deleteInBatch(iterable);
    }

    @Override
    public void deleteAllInBatch() {
        repository.deleteAllInBatch();
    }

    @Override
    public E getOne(Id id) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.getOne(id));

    }

    @Override
    public <S extends E> List<S> findAll(Example<S> example) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.findAll(example));

    }

    @Override
    public <S extends E> List<S> findAll(Example<S> example, Sort sort) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.findAll(example, sort));
    }

    @Override
    public Page<E> findAll(Pageable pageable) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.findAll(pageable));
    }

    @Override
    public <S extends E> Optional<S> findOne(Example<S> example) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_OptionalValue_NoException(() -> repository.findOne(example));
    }

    @Override
    public <S extends E> Page<S> findAll(Example<S> example, Pageable pageable) {
        return getEagerFetchUtil().runInTransactionAndFetchEagerly_NoException(() -> repository.findAll(example,pageable));
    }

    @Override
    public <S extends E> long count(Example<S> example) {
        return repository.count(example);
    }

    @Override
    public <S extends E> boolean exists(Example<S> example) {
        return repository.exists(example);
    }
}