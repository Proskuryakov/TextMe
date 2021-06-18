import { Injectable } from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {FavoriteRequest} from '../models/favorite.model';
import {Profile} from '../../profile/models/profile.model';

@Injectable({
  providedIn: 'root'
})
export class FavoriteApiService {
  favoriteURL = `${environment.api}/favorite`;

  constructor(private http: HttpClient) { }

  getFavorites(page: number = 0): Observable<Profile[]> {
    return this.http.get<Profile[]>(`${this.favoriteURL}/${page}`);
  }

  addFavorite(id: number): Observable<void> {
    const favoriteRequest = new FavoriteRequest(id);
    return this.http.post<void>(`${this.favoriteURL}`, favoriteRequest);
  }

  deleteFavorite(id: number): Observable<void> {
    const favoriteRequest = new FavoriteRequest(id);
    // @ts-ignore
    return this.http.delete<void>(`${this.favoriteURL}`, {body: favoriteRequest});
  }

}
