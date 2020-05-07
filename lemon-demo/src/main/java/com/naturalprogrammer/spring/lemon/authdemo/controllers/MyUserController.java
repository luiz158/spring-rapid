package com.naturalprogrammer.spring.lemon.authdemo.controllers;

import com.naturalprogrammer.spring.lemon.auth.controller.LemonController;
import com.naturalprogrammer.spring.lemon.auth.security.domain.LemonUserDto;
import com.naturalprogrammer.spring.lemon.authdemo.domain.MySignupForm;
import com.naturalprogrammer.spring.lemon.authdemo.dto.AdminUpdateUserDto;
import com.naturalprogrammer.spring.lemon.authdemo.dto.UserUpdateDto;
import com.naturalprogrammer.spring.lemon.authdemo.domain.User;
import io.github.vincemann.springrapid.acl.Role;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.RapidDtoEndpoint;
import io.github.vincemann.springrapid.core.controller.dtoMapper.context.DtoMappingContextBuilder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(MyUserController.BASE_URI)
public class MyUserController extends LemonController<User, Long, MySignupForm> {

    public static final String BASE_URI = "/api/core";


    public MyUserController() {
        super(
                DtoMappingContextBuilder.builder()
                        .forAll(LemonUserDto.class)
                        .forEndpoint(RapidDtoEndpoint.UPDATE, UserUpdateDto.class)
                        .withRoles(Role.ADMIN)
                        .forEndpoint(RapidDtoEndpoint.UPDATE, AdminUpdateUserDto.class)
                        .build()
        );
    }


}