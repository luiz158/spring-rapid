package io.github.vincemann.demo.dtoCrudControllers;

import io.github.vincemann.generic.crud.lib.controller.springAdapter.DTOCrudControllerSpringAdapter;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.idFetchingStrategy.IdFetchingStrategy;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.mediaTypeStrategy.MediaTypeStrategy;
import io.github.vincemann.demo.dtos.SpecialtyDto;
import io.github.vincemann.demo.model.Specialty;
import io.github.vincemann.demo.service.SpecialtyService;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.validationStrategy.ValidationStrategy;
import io.github.vincemann.generic.crud.lib.service.EndpointService;
import org.springframework.stereotype.Controller;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMapper;

@Controller
public class SpecialtyController extends DTOCrudControllerSpringAdapter<Specialty, SpecialtyDto,Long, SpecialtyService> {


    public SpecialtyController(SpecialtyService crudService, IdFetchingStrategy<Long> longIdFetchingStrategy, MediaTypeStrategy mediaTypeStrategy, ValidationStrategy validationStrategy, DtoMapper dtoMapper, EndpointService endpointService) {
        super(crudService,
                longIdFetchingStrategy,
                mediaTypeStrategy,
                validationStrategy,
                dtoMapper,
                endpointService
                );
    }

}
