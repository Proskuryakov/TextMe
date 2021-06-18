import { Component, OnInit } from '@angular/core';
import {Info} from '../../../../features/profile/models/info.model';
import {ActivatedRoute, Router} from '@angular/router';
import {ReportApiService} from '../../../../features/admin/services/report-api.service';
import {ReportData} from '../../../../features/admin/models/report.model';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {formatDate} from '@angular/common';

@Component({
  templateUrl: './reports.page.html',
  styleUrls: ['./reports.page.sass']
})
export class ReportsPage implements OnInit {
  statusMessage = '';
  reports: ReportData[] = [];
  isError = false;
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
    this.loadReports();
  }

  loadReports(): void {
    this.isError = false;
    this.statusMessage = '';
    this.reportApiService.getReports().subscribe(
      (reports) => this.reports = reports,
      () => {
        this.isError = true;
        this.statusMessage = 'Ошибка при загрузке жалоб';
      }
    );
  }

  getImage(moder: Info): string {
    return this.userApiService.getImageUrl(moder);
  }

  deny(report: ReportData): void {
    this.reportApiService.denyReport(report.from.id, report.to.card.id).subscribe(
      () => {
        this.loadReports();
        this.statusMessage = `Жалоба на ${report.to.info.name} успешно отменена.`;
      },
      () => {
        this.isError = true;
        this.statusMessage = 'Ошибка отмены жалобы';
      }
    );
  }

  ban(report: ReportData): void {
    const days = this.selectedBanTime[report.to.card.id];
    const date = this.shiftDate(days);
    this.reportApiService.banReport(report.to.card.id, date).subscribe(
      () => {
        this.loadReports();
        this.statusMessage = `${report.to.info.name} успешно заблокирован до ${this.dateFormat(date)}`;
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

  openSummary(report: ReportData): void {
    this.router.navigate(['admin', 'reports', 'summary', report.to.card.id]);
  }
}
