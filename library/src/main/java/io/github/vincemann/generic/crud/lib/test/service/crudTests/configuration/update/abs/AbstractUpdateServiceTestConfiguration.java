package io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.update.abs;

import io.github.vincemann.generic.crud.lib.model.IdentifiableEntity;
import io.github.vincemann.generic.crud.lib.test.postUpdateCallback.PostUpdateCallback;
import io.github.vincemann.generic.crud.lib.test.equalChecker.EqualChecker;
import io.github.vincemann.generic.crud.lib.test.service.crudTests.configuration.abs.AbstractServiceTestConfiguration;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class AbstractUpdateServiceTestConfiguration<E extends IdentifiableEntity<Id>,Id extends Serializable>
                                            extends AbstractServiceTestConfiguration<E,Id> {
    private Boolean fullUpdate;
    private PostUpdateCallback<E, Id> postUpdateCallback;

    public AbstractUpdateServiceTestConfiguration(Boolean fullUpdate, PostUpdateCallback<E, Id> postUpdateCallback, EqualChecker<E> repoEntityEqualChecker) {
        super(repoEntityEqualChecker);
        this.fullUpdate = fullUpdate;
        this.postUpdateCallback = postUpdateCallback;
    }


}