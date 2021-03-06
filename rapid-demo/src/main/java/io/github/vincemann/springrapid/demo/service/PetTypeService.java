package io.github.vincemann.springrapid.demo.service;

import io.github.vincemann.springrapid.demo.model.PetType;
import io.github.vincemann.springrapid.demo.repo.PetTypeRepository;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.CrudService;

@ServiceComponent
public interface PetTypeService extends CrudService<PetType,Long, PetTypeRepository> {
}
