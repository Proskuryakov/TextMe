import {Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {ChatFilterModel} from '../models/filter.model';
import {Message, MessageInfo} from '../models/message.model';
import {Observable} from 'rxjs';
import {formatDate} from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class MessengerApiService {
  messengerURL = `${environment.api}/messenger`;

  constructor(
    private readonly http: HttpClient
  ) {}

  getChats(filter: ChatFilterModel = ChatFilterModel.All, page: number = 0): Observable<MessageInfo[]> {
    return this.http.get<MessageInfo[]>(`${this.messengerURL}/list/${page}/${filter}`);
  }

  getChatMessages(id: number, page: number = 0): Observable<MessageInfo[]> {
    return this.http.get<MessageInfo[]>(`${this.messengerURL}/chat/${id}/${page}`);
  }

  getDirectMessages(id: number, page: number = 0): Observable<MessageInfo[]> {
    return this.http.get<MessageInfo[]>(`${this.messengerURL}/direct/${id}/${page}`);
  }

  formatDate(message: Message, old: string = 'dd.MM.yy', today: string = 'HH:mm'): string {
    const date = message.dateUpdate > message.dateCreate ? message.dateUpdate : message.dateCreate;
    if (
      formatDate(date, 'dd.MM.yy', 'en') <
      formatDate(new Date(), 'dd.MM.yy', 'en')
    ) {
      return formatDate(date, old, 'en');
    } else {
      return formatDate(date, today, 'en');
    }
  }

}
