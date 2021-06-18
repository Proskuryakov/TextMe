import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../../environments/environment';
import {Profile} from '../models/profile.model';
import {map} from 'rxjs/operators';
import {AuthService} from '../../../core/auth/auth.service';
import {Token} from '../../../core/auth/models';
import {Info} from '../models/info.model';
import {AppRole} from '../models/role.model';

@Injectable({
  providedIn: 'root'
})
export class UserApiService {
  private userURL = `${environment.api}/user`;
  userInfoKey = 'user_info';
  defaultImage = 'logo/logo_white_blueback.svg';

  constructor(
    private http: HttpClient,
    private readonly authService: AuthService
  ) { }

  getCurrentUser(): Observable<Profile> {
    return this.http.get<Profile>(`${this.userURL}/`).pipe(
      map(user => {
        if (!user.info.imageUrl) { user.info.imageUrl = this.defaultImage; }
        if (user.card.tags.length === 0) { user.card.tags = ['Теги', 'пока', 'не', 'добавлены']; }
        return user;
      })
    );
  }

  getUser(id: number): Observable<Profile> {
    return this.http.get<Profile>(`${this.userURL}/${id}`).pipe(
      map(user => {
        if (!user.info.imageUrl) { user.info.imageUrl = this.defaultImage; }
        if (user.card.tags.length === 0) { user.card.tags = ['Теги', 'пока', 'не', 'добавлены']; }
        return user;
      })
    );
  }

  uploadImage(image: File): Observable<void>{
    const fd = new FormData();
    fd.append('image', image, image.name);
    return this.http.post<void>(`${this.userURL}/image`, fd);
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

  getRoles(): Observable<AppRole[]> {
    return this.http.get<AppRole[]>(`${this.userURL}/roles`);
  }

  saveCurrentUserInfoToStorage(): void{
    this.getCurrentUser().subscribe(
      (profile) => localStorage.setItem(this.userInfoKey, JSON.stringify(profile.info))
    );
  }

  deleteCurrentUserInfoFromStorage(): void{
    localStorage.removeItem(this.userInfoKey);
  }

  getCurrentUserInfoFromStorage(): Info {
    return JSON.parse(localStorage.getItem(this.userInfoKey));
  }

  getImageUrl(profile: Info): string {
    if (!profile.imageUrl){
      return this.defaultImage;
    }
    return profile.imageUrl;
  }

  isModerOrAdmin(): Observable<boolean> {
    return this.getRoles().pipe(
      map((roles) => {
        console.log(roles);
        return AppRole.ROLE_ADMIN in roles || AppRole.ROLE_MODER in roles;
      })
    );
  }

}
