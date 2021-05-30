package ru.vsu.cs.textme.backend.db.model.info;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CardInfo extends Info {
    private Card card;
}
