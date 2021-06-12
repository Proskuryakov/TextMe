import { Injectable } from '@angular/core';
import {HttpClient, HttpEvent} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {ProfileModel} from '../models/profile.model';
import {map} from 'rxjs/operators';
import {AuthService} from '../../../core/auth/auth.service';
import {Token} from '../../../core/auth/models';

@Injectable({
  providedIn: 'root'
})
export class UserApiService {
  private userURL = `${environment.api}/user`;
  defaultImage = 'logo/logo_white_blueback.svg';

  constructor(
    private http: HttpClient,
    private readonly authService: AuthService
  ) { }

  getCurrentUser(): Observable<ProfileModel> {
    return this.http.get<ProfileModel>(`${this.userURL}/`).pipe(
      map(user => {
        if (!user.info.imageUrl) { user.info.imageUrl = this.defaultImage; }
        if (!user.card.tags) { user.card.tags = ['Теги пока не добавлены']; }
        return user;
      })
    );
  }

  uploadImage(image: File): Observable<HttpEvent<void>>{
    const fd = new FormData();
    fd.append('image', image, image.name);
    return this.http.post<void>(`${this.userURL}/image`, fd, {
      reportProgress: true,
      observe: 'events'
    });
  }

  updateName(nickname: string): Observable<void>{
    return this.http.post<Token>(`${this.userURL}/nickname`, {name: nickname}).pipe(
      map( (newToken) => {
          console.log(newToken);
          this.authService.updateToken(newToken);
      })
    );
  }

  updatePassword(oldPass: string, newPass: string): Observable<void>{
    return this.http.post<void>(`${this.userURL}/pass`, {old: oldPass, change: newPass});
  }

}
