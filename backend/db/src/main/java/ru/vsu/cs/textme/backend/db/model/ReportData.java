package ru.vsu.cs.textme.backend.db.model;

import lombok.Data;
import ru.vsu.cs.textme.backend.db.model.info.Info;

import java.sql.Timestamp;

@Data
public class ReportData {
    private Info from;
    private Info to ;
    private String message;
    private Timestamp date;
}
