package io.github.vincemann.demo.service.springDataJPA.it;

import io.github.vincemann.demo.model.Owner;
import io.github.vincemann.demo.model.Pet;
import io.github.vincemann.demo.model.PetType;
import io.github.vincemann.demo.repositories.OwnerRepository;
import io.github.vincemann.demo.service.OwnerService;
import io.github.vincemann.demo.service.PetService;
import io.github.vincemann.demo.service.PetTypeService;
import io.github.vincemann.generic.crud.lib.service.exception.BadEntityException;
import io.github.vincemann.generic.crud.lib.service.exception.EntityNotFoundException;
import io.github.vincemann.generic.crud.lib.service.exception.NoIdException;
import io.github.vincemann.generic.crud.lib.test.service.ForceEagerFetch_CrudServiceIntegrationTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;

//@DataJpaTest cant be used because i need autowired components from generic-crud-lib
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment =
        SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles(value = {"test", "springdatajpa"})
class OwnerJPAServiceIntegrationTest
        extends ForceEagerFetch_CrudServiceIntegrationTest
        <
                                        OwnerService,
                                        OwnerRepository,
                                        Owner,
                                        Long
                                >
{

    private Owner ownerWithoutPets;
    private Owner ownerWithOnePet;
    @Autowired
    private PetTypeService petTypeService;
    @Autowired
    private PetService petService;
    private Pet testPet;
    private PetType savedDogPetType;


    @BeforeEach
    public void setUp() throws Exception {

        savedDogPetType = petTypeService.save(new PetType("Dog"));

        testPet = Pet.builder()
                .petType(savedDogPetType)
                .name("Bello")
                .birthDate(LocalDate.now())
                .build();

        ownerWithoutPets = Owner.builder()
                .firstName("owner without pets")
                .lastName("owner without pets lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .build();

        ownerWithOnePet = Owner.builder()
                .firstName("owner with one pet")
                .lastName("owner with one pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Arrays.asList(testPet)))
                .build();
    }

    @Test
    public void saveOwnerWithoutPets_ShouldSucceed() throws NoIdException, BadEntityException {
        saveEntity_ShouldSucceed(ownerWithoutPets);
    }

    @Test
    public void saveOwnerWithPet_ShouldSucceed() throws NoIdException, BadEntityException {
        saveEntity_ShouldSucceed(ownerWithOnePet);
    }

    @Test
    public void saveOwnerWithPersistedPet_ShouldSucceed() throws NoIdException, BadEntityException {
        Pet savedPet = petService.save(testPet);


        Owner owner = Owner.builder()
                .firstName("owner with one already persisted pet")
                .lastName("owner with one already persisted pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Arrays.asList(savedPet)))
                .build();
        saveEntity_ShouldSucceed(owner);
    }


    @Test
    public void updateOwner_ChangeTelephoneNumber_ShouldSucceed() throws BadEntityException, EntityNotFoundException, NoIdException {
        Owner diffTelephoneNumberUpdate = Owner.builder()
                .firstName("ownername")
                .lastName("owner lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("42")
                .build();
        updateEntity_ShouldSucceed(ownerWithoutPets,diffTelephoneNumberUpdate);
    }

    @Test
    public void updateOwner_addAnotherPet_shouldSucceed() throws BadEntityException, NoIdException, EntityNotFoundException {
        Pet savedPet = petService.save(testPet);
        Pet petToAdd = Pet.builder()
                .name("petToAdd")
                .petType(savedDogPetType)
                .birthDate(LocalDate.now())
                .build();
        Pet savedPetToAdd = petService.save(petToAdd);


        Owner owner = Owner.builder()
                .firstName("owner with one already persisted pet")
                .lastName("owner with one already persisted pet lastName")
                .address("asljnflksamfslkmf")
                .city("n1 city")
                .telephone("12843723847324")
                .pets(new HashSet<>(Arrays.asList(savedPet)))
                .build();
        Owner savedOwner = repoSave(owner);

        savedOwner.getPets().add(savedPetToAdd);

        Owner updatedOwner = updateEntity_ShouldSucceed(savedOwner);
        Assertions.assertTrue(updatedOwner.getPets().contains(savedPetToAdd));
    }


}