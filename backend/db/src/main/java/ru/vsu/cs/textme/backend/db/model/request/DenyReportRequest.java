package ru.vsu.cs.textme.backend.db.model.request;

import lombok.Data;

@Data
public class DenyReportRequest {
    private Integer reporterId;
    private Integer cardId;
}
