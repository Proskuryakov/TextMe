import { Injectable } from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {BanRequest, DenyReportRequest, ReportData} from '../models/report.model';

@Injectable({
  providedIn: 'root'
})
export class ReportApiService {
  reportURL = `${environment.api}/report`;

  constructor(private http: HttpClient) { }

  getReports(page: number = 0): Observable<ReportData[]> {
    return this.http.get<ReportData[]>(`${this.reportURL}/${page}`);
  }

  getReportsByCard(cardId: number, page: number = 0): Observable<ReportData[]> {
    return this.http.get<ReportData[]>(`${this.reportURL}/${cardId}/${page}`);
  }

  denyReport(reporterId: number, cardId: number): Observable<void> {
    const denyReportRequest = new DenyReportRequest(reporterId, cardId);
    return this.http.post<void>(`${this.reportURL}/deny`, denyReportRequest);
  }

  banReport(cardId: number, date: Date): Observable<void> {
    const banRequest = new BanRequest(cardId, date);
    return this.http.post<void>(`${this.reportURL}/ban`, banRequest);
  }

}
