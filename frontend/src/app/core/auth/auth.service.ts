import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {environment} from '../../../environments/environment';
import {map} from 'rxjs/operators';
import {User, Token} from './models';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private tokenKey = 'access_token';

  constructor(private http: HttpClient) {}

  login(user: User): Observable<void> {
    return this.http
      .post<Token>(`${environment.api}/auth/`, user)
      .pipe(
        map((result) => {
          localStorage.setItem(this.tokenKey, result.accessToken);
          console.log('Auth success');
        })
      ) as Observable<void>;
  }

  getToken(): string {
    return localStorage.getItem(this.tokenKey);
  }

  isLoggedIn(): boolean {
    const authToken = localStorage.getItem(this.tokenKey);
    return (authToken !== null);
  }

  deleteToken(): void {
    localStorage.removeItem(this.tokenKey);
  }

  updateToken(newToken: Token): void{
    localStorage.setItem(this.tokenKey, newToken.accessToken);
  }

  signup(user: User): Observable<void> {
    return this.http.post<void>(`${environment.api}/auth/register`, user);
  }

  activate(code: string): Observable<void>{
    return this.http.get<void>(`${environment.api}/auth/activate/${code}`);
  }

}

