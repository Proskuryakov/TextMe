import { Injectable } from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class TagApiService {
  tagURL = `${environment.api}/tag`;

  constructor(
    private readonly http: HttpClient
  ) { }

  getTagsLike(begin: string): Observable<string[]> {
    const params = new HttpParams().set('begin', begin);
    return this.http.get<string[]>(`${this.tagURL}/like`, {params});
  }

}
