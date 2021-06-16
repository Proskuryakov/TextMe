import {Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {ChatFilterModel} from '../models/filter.model';
import {MessageInfo} from '../models/message.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class MessengerApiService {
  messengerURL = `${environment.api}/messenger`;

  constructor(
    private readonly http: HttpClient
  ) {}

  getChats(filter: ChatFilterModel = ChatFilterModel.All, page: number = 0): Observable<MessageInfo[]> {
    console.log(`${this.messengerURL}/list/${page}/${filter}`);
    return this.http.get<MessageInfo[]>(`${this.messengerURL}/list/${page}/${filter}`);
  }

  getChatMessages(id: number, page: number = 0): Observable<MessageInfo[]> {
    return this.http.get<MessageInfo[]>(`${this.messengerURL}/chat/${id}/${page}`);
  }

  getDirectMessages(id: number, page: number = 0): Observable<MessageInfo[]> {
    return this.http.get<MessageInfo[]>(`${this.messengerURL}/direct/${id}/${page}`);
  }


}
