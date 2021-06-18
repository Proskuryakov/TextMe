import {Injectable} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Info} from '../../profile/models/info.model';
import {Profile} from '../../profile/models/profile.model';
import {AppRole} from '../../profile/models/role.model';
import {PermissionRequest} from '../models/permissiom.model';

@Injectable({
  providedIn: 'root'
})
export class AdminApiService {
  adminURL = `${environment.api}/admin`;

  constructor(private http: HttpClient) { }

  getModerators(page: number = 0): Observable<Info[]> {
    return this.http.get<Info[]>(`${this.adminURL}/moders/${page}`);
  }

  getUser(name: string): Observable<Profile> {
    const params = new HttpParams().set('name', name);
    return this.http.get<Profile>(`${this.adminURL}/user`, {params});
  }

  addModerator(userId: number): Observable<void>{
    const permissionRequest = new PermissionRequest(userId, AppRole.ROLE_MODER, true);
    return this.changePermission(permissionRequest);
  }

  deleteModerator(userId: number): Observable<void>{
    const permissionRequest = new PermissionRequest(userId, AppRole.ROLE_MODER, false);
    return this.changePermission(permissionRequest);
  }

  changePermission(permissionRequest: PermissionRequest): Observable<void> {
    return this.http.post<void>(`${this.adminURL}/permission`, permissionRequest);
  }

}
