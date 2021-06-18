package ru.vsu.cs.textme.backend.db.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.vsu.cs.textme.backend.db.model.info.Info;
import ru.vsu.cs.textme.backend.db.model.info.Profile;

import java.util.List;

@Data
@AllArgsConstructor
public class ReportSummary{
    private Profile toCheck;
    private List<Report> reports;
}
