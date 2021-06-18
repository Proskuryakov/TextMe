import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {Profile} from '../../profile/models/profile.model';
import {TagRequest} from '../models/tag.model';
import {ReportRequest} from '../models/report.model';

@Injectable({
  providedIn: 'root'
})
export class CardApiService {
  cardURL = `${environment.api}/card`;

  constructor(private http: HttpClient) { }

  updateStatus(content: string): Observable<void>{
    return this.http.post<void>(`${this.cardURL}/user/content`, {content});
  }

  getRandomCards(): Observable<Profile[]> {
    return this.http.get<Profile[]>(`${this.cardURL}/users/random`);
  }

  getCards(page: number = 0, tag?: string): Observable<Profile[]> {
    let params;
    if (tag) {
      params = new HttpParams().set('tag', tag);
    }
    return this.http.get<Profile[]>(`${this.cardURL}/users/${page}`, {params});
  }

  addTag(tag: string): Observable<void> {
    return this.http.post<void>(`${this.cardURL}/user/tag`, {tag});
  }

  deleteTag(tag: string): Observable<void> {
    const tagRequest = new TagRequest();
    tagRequest.tag = tag;
    // @ts-ignore
    return this.http.delete<void>(`${this.cardURL}/user/tag`, {body: tagRequest});
  }

  report(cardId: number, message: string): Observable<void> {
    const reportRequest = new ReportRequest(cardId, message);
    return  this.http.post<void>(`${this.cardURL}/report`, reportRequest);
  }

}
