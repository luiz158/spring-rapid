package io.github.vincemann.springrapid.demo.lib.controller;

import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContext;
import io.github.vincemann.springrapid.core.controller.rapid.RapidController;

public class ExampleRapidController
        extends RapidController<ExampleEntity,Long,ExampleService>
{
    public ExampleRapidController(DtoMappingContext dtoMappingContext) {
        super();
        setDtoMappingContext(dtoMappingContext);
    }

    @Override
    public DtoMappingContext provideDtoMappingContext() {
        return null;
    }
}
