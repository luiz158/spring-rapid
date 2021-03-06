package io.github.vincemann.springrapid.entityrelationship.advice;

import io.github.vincemann.springrapid.core.model.IdentifiableEntity;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.child.BiDirChild;
import io.github.vincemann.springrapid.entityrelationship.model.biDir.parent.BiDirParent;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

@Aspect
@Slf4j
/**
 * Advice that keeps BiDirRelationships intact for {@link io.github.vincemann.springrapid.core.service.CrudService#save(IdentifiableEntity)} - operations.
 */
public class BiDirEntityPersistAdvice {

    @Before("io.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "io.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirParent)")
    public void prePersistBiDirParent(BiDirParent biDirParent) throws IllegalAccessException {
        log.debug("pre persist biDirParent hook reached for: " + biDirParent);
        setChildrensParentRef(biDirParent);
    }

    @Before("io.github.vincemann.springrapid.core.advice.SystemArchitecture.saveOperation() && " +
            "io.github.vincemann.springrapid.core.advice.SystemArchitecture.repoOperation() && " +
            "args(biDirChild)")
    public void prePersistBiDiChild(BiDirChild biDirChild) throws IllegalAccessException {
        log.debug("pre persist biDirChild hook reached for: " + biDirChild);
        setParentsChildRef(biDirChild);
    }

    private void setChildrensParentRef(BiDirParent biDirParent) throws IllegalAccessException {
        Set<? extends BiDirChild> children = biDirParent.getChildren();
        for (BiDirChild child : children) {
            child.setParentRef(biDirParent);
        }
        Set<Collection<? extends BiDirChild>> childCollections = biDirParent.getChildrenCollections().keySet();
        for (Collection<? extends BiDirChild> childCollection : childCollections) {
            for (BiDirChild biDirChild : childCollection) {
                biDirChild.setParentRef(biDirParent);
            }
        }
    }


    private void setParentsChildRef(BiDirChild biDirChild) throws IllegalAccessException {
        //set backreferences
        for (BiDirParent parent : biDirChild.findParents()) {
            parent.addChild(biDirChild);
        }
    }
}
