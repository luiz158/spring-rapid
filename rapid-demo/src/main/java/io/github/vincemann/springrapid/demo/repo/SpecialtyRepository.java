package io.github.vincemann.springrapid.demo.repo;

import io.github.vincemann.springrapid.core.slicing.components.ServiceComponent;
import io.github.vincemann.springrapid.demo.model.Specialty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@ServiceComponent
public interface SpecialtyRepository extends JpaRepository<Specialty,Long> {
}
