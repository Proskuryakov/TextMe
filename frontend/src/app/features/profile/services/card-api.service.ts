import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CardApiService {
  cardURL = `${environment.api}/card`;

  constructor(private http: HttpClient) { }

  updateStatus(content: string): Observable<void>{
    return this.http.post<void>(`${this.cardURL}/user/content`, {content});
  }

}
