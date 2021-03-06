package com.naturalprogrammer.spring.lemon.exceptions.handlers;

import io.github.vincemann.springrapid.core.service.exception.EntityNotFoundException;
import org.springframework.http.HttpStatus;

public class EntityNotFoundExceptionHandler extends AbstractExceptionHandler<EntityNotFoundException> {
    public EntityNotFoundExceptionHandler() {
        super(EntityNotFoundException.class);
    }

    @Override
    protected HttpStatus getStatus(EntityNotFoundException ex) {
        return HttpStatus.NOT_FOUND;
    }
}
