package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

import java.util.List;

@Data
public class Card {
    private Integer id;
    private String content;
    private List<Tag> tags;
}
