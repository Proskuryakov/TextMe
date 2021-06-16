package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;

@Data
public class ReportsData {
    private Integer id;
    private String name;
    private String image;
    private Integer card;
    private String type;
    private Integer count;
}
