package io.github.vincemann.generic.crud.lib.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.exception.DtoMappingException;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.DtoSerializingException;
import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.service.CrudService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;


/**
 * Impl of {@link DtoCrudController} that handles the following:
 * Mapping of ServiceEntity to Dto and vice versa.
 * Interaction with specified  {@link CrudService}.
 * Supply hook Methods.
 *
 * @param <E>        Entity Type, of entity, which's curd endpoints are exposed by this Controller
 * @param <Id>       Id Type of {@link E}
 */
@Slf4j
@Getter
@Setter
public abstract class JsonDtoCrudController
        <
                E extends IdentifiableEntity<Id>,
                Id extends Serializable
        >
            implements DtoCrudController<Id> {

    //todo merge into spring adapter -> wo ist der sinn hier zu trennen?

    private CrudService<E,Id,? extends CrudRepository<E,Id>> crudService;
    private DtoMapper dtoMapper;
    private DtoMappingContext dtoMappingContext;
    @Setter
    private ObjectMapper jsonMapper;

    @SuppressWarnings("unchecked")
    private Class<E> entityClass = (Class<E>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];


    public JsonDtoCrudController(DtoMappingContext dtoMappingContext) {
        this.dtoMappingContext = dtoMappingContext;
    }

    @Autowired
    public void injectCrudService(CrudService<E,Id,? extends CrudRepository<E,Id>> crudService) {
        this.crudService = crudService;
    }

    public void setDtoMappingContext(DtoMappingContext dtoMappingContext) {
        this.dtoMappingContext = dtoMappingContext;
    }

    @Autowired
    public void injectJsonMapper(ObjectMapper mapper) {
        this.jsonMapper = mapper;
    }

    @Autowired
    public void injectDtoMapper(DtoMapper dtoMapper) {
        this.dtoMapper = dtoMapper;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<String> find(Id id) throws NoIdException, EntityNotFoundException, DtoMappingException, DtoSerializingException {
        try {
            Optional<E> optionalEntity = crudService.findById(id);
            if (optionalEntity.isPresent()) {
                IdentifiableEntity<?> dto = dtoMapper.mapToDto(optionalEntity.get(), getDtoMappingContext().getFindReturnDtoClass());
                return ok(jsonMapper.writeValueAsString(dto));
            } else {
                throw new EntityNotFoundException();
            }
        }catch (JsonProcessingException e){
            throw new DtoSerializingException(e);
        }
    }

    @Override
    public ResponseEntity<String> findAll() throws DtoMappingException, DtoSerializingException {
        try {
            Set<E> all = crudService.findAll();
            Collection<IdentifiableEntity<Id>> dtos = new HashSet<>();
            for (E e : all) {
                dtos.add(dtoMapper.mapToDto(e, getDtoMappingContext().getFindAllReturnDtoClass()));
            }
            String json = jsonMapper.writeValueAsString(dtos);
            return ok(json);
        } catch (JsonProcessingException e) {
            throw new DtoSerializingException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<String> create(IdentifiableEntity<Id> dto) throws BadEntityException, DtoMappingException, DtoSerializingException {
        try {
            //i expect that dto has the right dto type -> callers responsibility
            E entity = mapToEntity(dto);
            E savedEntity = crudService.save(entity);
            IdentifiableEntity<?> resultDto = dtoMapper.mapToDto(savedEntity, getDtoMappingContext().getCreateReturnDtoClass());
            return new ResponseEntity<>(
                    jsonMapper.writeValueAsString(resultDto),
                    HttpStatus.OK);
        }catch (JsonProcessingException e){
            throw new DtoSerializingException(e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<String> update(IdentifiableEntity<Id> dto, boolean full) throws BadEntityException, DtoMappingException, NoIdException, EntityNotFoundException, DtoSerializingException {
        try {
            //i expect that dto has the right dto type -> callers responsibility
            E entity = mapToEntity(dto);
            E updatedEntity = crudService.update(entity,full);
            //no idea why casting is necessary here?
            IdentifiableEntity<?> resultDto = dtoMapper.mapToDto(updatedEntity, getDtoMappingContext().getUpdateReturnDtoClass());
            return new ResponseEntity<>(
                    jsonMapper.writeValueAsString(resultDto),
                    HttpStatus.OK);
        }catch (JsonProcessingException e){
            throw new DtoSerializingException(e);
        }
    }


    @Override
    public ResponseEntity<String> delete(Id id) throws NoIdException, EntityNotFoundException {
        crudService.deleteById(id);
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON_UTF8).build();
    }


    private E mapToEntity(IdentifiableEntity<Id> dto) throws DtoMappingException {
        return dtoMapper.mapToEntity(dto,entityClass);
    }


    protected ResponseEntity<String> ok(String jsonDto) {
        return new ResponseEntity<>(jsonDto, HttpStatus.OK);
    }

    public <S extends CrudService<E, Id,? extends CrudRepository<E,Id>>> S getCastedCrudService(){
        return (S) crudService;
    }

}