import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {Profile} from '../../profile/models/profile.model';

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

}
