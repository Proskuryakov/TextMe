package ru.vsu.cs.textme.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.ReportMapper;
import ru.vsu.cs.textme.backend.db.model.ReportSummary;
import ru.vsu.cs.textme.backend.db.model.ReportData;
import ru.vsu.cs.textme.backend.db.model.request.BanRequest;
import ru.vsu.cs.textme.backend.db.model.request.DenyReportRequest;
import ru.vsu.cs.textme.backend.db.model.request.ReportRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportMapper mapper;
    public void setReport(Integer user, ReportRequest report) {
        mapper.setReport(user, report.getCard(), report.getMessage());
    }

    public List<ReportData> getReportsListPage(Integer page) {
        return mapper.findReportsDataPage(128, page * 128);
    }

    public ReportSummary getReportsSummary(Integer cardId, Integer page) {
        var reports =  mapper.findReportProfilePage(cardId, 128, page * 128);
        var user = mapper.findUserInfoById(cardId);
        return new ReportSummary(user, reports);
    }

    public void ban(Integer by, BanRequest request) {
        mapper.saveBan(by, request.getCardId(), request.getExpired());
    }

    public void deny(Integer id, DenyReportRequest request) {
        mapper.denyReport(id, request.getReporterId(), request.getCardId());
    }
}
