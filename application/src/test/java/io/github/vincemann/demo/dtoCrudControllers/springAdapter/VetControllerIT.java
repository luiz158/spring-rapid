package io.github.vincemann.demo.dtoCrudControllers.springAdapter;

import io.github.vincemann.demo.dtoCrudControllers.EntityInitializerControllerIT;
import io.github.vincemann.demo.dtoCrudControllers.VetController;
import io.github.vincemann.demo.dtos.SpecialtyDTO;
import io.github.vincemann.demo.dtos.VetDTO;
import io.github.vincemann.demo.model.Vet;
import io.github.vincemann.demo.service.VetService;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test","springdatajpa"})
class VetControllerIT extends EntityInitializerControllerIT<Vet, VetDTO, VetService, VetController, Long> {


    VetControllerIT(@Autowired VetController crudController) {
        super(crudController, 99L);
    }

    @Override
    protected List<VetDTO> provideValidTestDTOs() {
        return Arrays.asList(
                VetDTO.builder()
                     .firstName("Meister")
                     .lastName("Yoda")
                     .build(),
                //Vet mit valid specialty
                VetDTO.builder()
                        .firstName("Meister")
                        .lastName("Yoda")
                        .specialties(Collections.singleton(getTestSpecialty()))
                        .build()
        );
    }

    @Override
    protected List<VetDTO> provideInvalidTestDTOs() {
        return Arrays.asList(
                VetDTO.builder()
                        .firstName("Meister")
                        //no last name
                        //.lastName("Yoda")
                        .build(),
                //Vet with invalid specialty
                VetDTO.builder()
                        .firstName("Meister")
                        .lastName("Yoda")
                        .specialties(Collections.singleton(new SpecialtyDTO(null)))
                        .build()
        );
    }

    @Override
    protected void modifyTestEntity(VetDTO testEntityDTO) {
        testEntityDTO.setLastName("MODIFIED");
    }
}