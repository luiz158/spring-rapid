package io.github.vincemann.springrapid.demo.lib.controller;

import io.github.vincemann.springrapid.core.model.IdentifiableEntityImpl;
import lombok.*;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@ToString
public class ExampleReadDto extends IdentifiableEntityImpl<Long> {
    private String name;
}
