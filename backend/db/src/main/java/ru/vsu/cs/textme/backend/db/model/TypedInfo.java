package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.vsu.cs.textme.backend.db.model.info.Info;

@EqualsAndHashCode(callSuper = true)
@Data
public class TypedInfo extends Info {
    private String type;
}
