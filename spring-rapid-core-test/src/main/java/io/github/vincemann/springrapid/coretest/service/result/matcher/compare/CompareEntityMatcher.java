package io.github.vincemann.springrapid.coretest.service.result.matcher.compare;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public abstract class CompareEntityMatcher {
    private CompareEntityMatcherContext compareEntityContext;
}