package ru.vsu.cs.textme.backend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.textme.backend.db.model.ReportSummary;
import ru.vsu.cs.textme.backend.db.model.ReportsData;
import ru.vsu.cs.textme.backend.db.model.request.BanRequest;
import ru.vsu.cs.textme.backend.security.CustomUserDetails;
import ru.vsu.cs.textme.backend.services.ReportService;

import java.util.List;

@RestController
@RequestMapping("api/report")
@RequiredArgsConstructor
public class ReportController {
    private final ReportService reportService;

    @GetMapping
    @RequestMapping("/{page}")
    @ResponseStatus(HttpStatus.OK)
    public List<ReportsData> getReportsPage(@PathVariable Integer page) {
        return reportService.getReportsListPage(page);
    }

    @GetMapping
    @RequestMapping("/{id}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public ReportSummary getReportSummary(@PathVariable Integer id, @PathVariable Integer page) {
        return reportService.getReportsSummary(id, page);
    }

    @PostMapping
    @RequestMapping("/ban")
    @ResponseStatus(HttpStatus.OK)
    public void getReportSummary(@RequestBody BanRequest request,
                                          @AuthenticationPrincipal CustomUserDetails details) {
        reportService.ban(details.getUser().getId(), request);
    }
}
