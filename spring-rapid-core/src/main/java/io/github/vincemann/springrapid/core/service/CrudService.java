package io.github.vincemann.springrapid.core.service;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import io.github.vincemann.springrapid.core.service.exception.BadEntityException;
import org.springframework.data.repository.CrudRepository;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Interface for a Service offering Crud Operations.
 * @param <E>       Type of Entity which's crud operations are exposed by this Service
 * @param <Id>      Id Type of E
 */
@ServiceComponent
public interface CrudService
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable,
                R extends CrudRepository<E,Id>
        >
{

    Optional<E> findById(Id id) throws BadEntityException;

    /**
     * If full is false:
     * only non null members of @param entity will be taken into consideration for updating the database entity.
     * If full is true:
     * all members of @param entity will be taken into consideration for updating the database entity.
     * @param entity
     * @return updated (database) entity
     */
    E update(E entity, Boolean full) throws EntityNotFoundException, BadEntityException, BadEntityException;

    E save(E entity) throws  BadEntityException;

    Set<E> findAll();

    void deleteById(Id id) throws EntityNotFoundException, BadEntityException;

    Class<E> getEntityClass();

    R getRepository();
}