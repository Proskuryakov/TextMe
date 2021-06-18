package ru.vsu.cs.textme.backend.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.vsu.cs.textme.backend.db.model.ReportSummary;
import ru.vsu.cs.textme.backend.db.model.ReportData;
import ru.vsu.cs.textme.backend.db.model.request.BanRequest;
import ru.vsu.cs.textme.backend.db.model.request.DenyReportRequest;
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
    public List<ReportData> getReportsPage(@PathVariable Integer page) {
        return reportService.getReportsListPage(page);
    }

    @GetMapping
    @RequestMapping("/{cardId}/{page}")
    @ResponseStatus(HttpStatus.OK)
    public ReportSummary getReportSummary(@PathVariable Integer cardId, @PathVariable Integer page) {
        return reportService.getReportsSummary(cardId, page);
    }

    @PostMapping
    @RequestMapping("/ban")
    @ResponseStatus(HttpStatus.OK)
    public void banUser(@RequestBody BanRequest request,
                                 @AuthenticationPrincipal CustomUserDetails details) {
        reportService.ban(details.getUser().getId(), request);
    }

    @PostMapping
    @RequestMapping("/deny")
    @ResponseStatus(HttpStatus.OK)
    public void denyReport(@RequestBody DenyReportRequest request, @AuthenticationPrincipal CustomUserDetails details) {
        reportService.deny(details.getUser().getId(), request);
    }
}
