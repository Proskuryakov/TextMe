package ru.vsu.cs.textme.backend.db.model.info;

import lombok.Data;

import java.util.List;

@Data
public class Card {
    private Integer id;
    private String content;
    private List<String> tags;
}
