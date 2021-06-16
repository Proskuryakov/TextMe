package ru.vsu.cs.textme.backend.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.vsu.cs.textme.backend.db.mapper.ReportMapper;
import ru.vsu.cs.textme.backend.db.model.ReportSummary;
import ru.vsu.cs.textme.backend.db.model.ReportsData;
import ru.vsu.cs.textme.backend.db.model.request.BanRequest;
import ru.vsu.cs.textme.backend.db.model.request.ReportRequest;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {
    private final ReportMapper mapper;
    public void addReport(Integer user, ReportRequest report) {
        mapper.addReport(user, report.getCard(), report.getMessage());
    }

    public List<ReportsData> getReportsListPage(Integer page) {
        return mapper.findReportsDataPage(128, page * 128);
    }

    public ReportSummary getReportsSummary(Integer id, Integer page) {
        var reports =  mapper.findReportProfilePage(id, 128, page * 128);
        var user = mapper.findCardOrUserInfoByCardId(id);
        return new ReportSummary(user, reports);
    }

    public void ban(Integer by, BanRequest request) {
        mapper.saveBan(by, request.getCardId(), request.getExpired());
    }
}
