package ru.vsu.cs.textme.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.ReportMapper;
import ru.vsu.cs.textme.backend.db.model.request.ReportRequest;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportMapper mapper;
    public void addReport(Integer user, ReportRequest report) {
        mapper.addReport(user, report.getCard(), report.getMessage());
    }
}
