import { Component, OnInit } from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Report, ReportSummary} from '../../../../features/admin/models/report.model';
import {ReportApiService} from '../../../../features/admin/services/report-api.service';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {Info} from '../../../../features/profile/models/info.model';
import {formatDate} from '@angular/common';

@Component({
  templateUrl: './reports-summary.page.html',
  styleUrls: ['./reports-summary.page.sass']
})
export class ReportsSummaryPage implements OnInit {
  statusMessage = '';
  reportSummary: ReportSummary;
  isError = false;
  cardId: number;
  banTime: { [id: number]: string; } = {
    1: '1',
    3: '3',
    7: '7',
    14: '14',
    30: '30',
    365: '365'
  };
  selectedBanTime: { [id: number]: number; } = {};
  constructor(
    private readonly activatedRoute: ActivatedRoute,
    private readonly router: Router,
    private readonly reportApiService: ReportApiService,
    private readonly userApiService: UserApiService
  )
  { }

  ngOnInit(): void {
    this.activatedRoute.params.subscribe(
      (params) => {
        this.cardId = params.id;
        this.loadReports();
      }
    );
  }

  loadReports(): void {
    this.isError = false;
    this.statusMessage = '';
    this.reportApiService.getReportSummary(this.cardId).subscribe(
      (report) => this.reportSummary = report,
      () => {
        this.isError = true;
        this.statusMessage = 'Ошибка при загрузке жалоб';
      }
    );
  }

  getImage(moder: Info): string {
    return this.userApiService.getImageUrl(moder);
  }

  deny(report: Report): void {
    this.reportApiService.denyReport(report.reporter.id, this.cardId).subscribe(
      () => {
        this.loadReports();
        this.statusMessage = `Жалоба на ${this.reportSummary.toCheck.info.name} успешно отменена.`;
      },
      () => {
        this.isError = true;
        this.statusMessage = 'Ошибка отмены жалобы';
      }
    );
  }

  ban(report: Report): void {
    const days = this.selectedBanTime[report.reporter.id];
    const date = this.shiftDate(days);
    this.reportApiService.banReport(this.cardId, date).subscribe(
      () => {
        this.loadReports();
        this.statusMessage = `${this.reportSummary.toCheck.info.name} успешно заблокирован до ${this.dateFormat(date)}`;
      },
      () => {
        this.isError = true;
        this.statusMessage = 'Ошибка блокировки';
      }
    );
  }

  shiftDate(shiftDays: number): Date {
    return new Date(new Date().getTime() + (1000 * 60 * 60 * 24) * shiftDays);
  }

  dateFormat(date: Date): string {
    return formatDate(date, 'dd.MM.yy HH:mm', 'en');
  }

  keys(banTime: { [p: number]: string }): string[] {
    return Object.keys(banTime);
  }

  navigateToReports(): void {
    this.router.navigate(['admin', 'reports']);
  }

}
