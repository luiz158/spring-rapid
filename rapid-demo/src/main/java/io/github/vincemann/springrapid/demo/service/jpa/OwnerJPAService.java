package io.github.vincemann.springrapid.demo.service.jpa;

import io.github.vincemann.springrapid.demo.model.Owner;
import io.github.vincemann.springrapid.demo.repo.OwnerRepository;
import io.github.vincemann.springrapid.demo.service.OwnerService;
import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.core.service.jpa.JPACrudService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Qualifier("noProxy")
@Service
@ServiceComponent
public class OwnerJPAService extends JPACrudService<Owner,Long, OwnerRepository> implements OwnerService {


    @Transactional
    @Override
    public Optional<Owner> findByLastName(String lastName) {
        return getRepository().findByLastName(lastName);
    }

    /**
     * Owner named "42" is owner of the year
     * @return
     */
    @Override
    public Optional<Owner> findOwnerOfTheYear() {
        return getRepository().findAll().stream().filter(owner -> {
            return owner.getFirstName().equals("42");
        }).findFirst();
    }
}
