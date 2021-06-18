import {Info} from '../../profile/models/info.model';
import {Data} from '@angular/router';
import {Profile} from '../../profile/models/profile.model';

export class ReportData{
  from: Info;
  to: Profile;
  message: string;
  date: Date;
}

export class BanRequest {
  cardId: number;
  expired: Data;


  constructor(cardId: number, expired: Data) {
    this.cardId = cardId;
    this.expired = expired;
  }
}

export class DenyReportRequest {
  reporterId: number;
  cardId: number;

  constructor(reporterId: number, cardId: number) {
    this.reporterId = reporterId;
    this.cardId = cardId;
  }

}
