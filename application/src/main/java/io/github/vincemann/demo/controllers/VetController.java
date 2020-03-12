package io.github.vincemann.demo.controllers;

import io.github.vincemann.demo.dtos.VetDto;
import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.generic.crud.lib.config.layers.component.WebController;
import io.github.vincemann.generic.crud.lib.controller.dtoMapper.DtoMappingContext;
import io.github.vincemann.generic.crud.lib.controller.springAdapter.SpringAdapterJsonDtoCrudController;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@WebController
public class VetController
        extends SpringAdapterJsonDtoCrudController<Vet, Long> {

    public VetController() {
        super(DtoMappingContext.DEFAULT(VetDto.class));
    }

    @RequestMapping({"/vets", "/vets/index", "/vets/index.html", "/vets.html"})
    public String listVets(Model model){
        model.addAttribute("vets", getCrudService().findAll());
        return "vets/index";
    }



}
