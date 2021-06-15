import {Injectable} from '@angular/core';
import {UserApiService} from '../../features/profile/services/user-api.service';
import {RxStompService} from '@stomp/ng2-stompjs';
import {Message} from '@stomp/stompjs';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class SocketService {
  public LISTEN_PATH: string;
  public SEND_PATH: string;

  private headers = {Authorization: `Bearer ${localStorage.getItem('access_token')}`};

  constructor(
    private rxStompService: RxStompService,
    private readonly userApiService: UserApiService
  ) {
    this.SEND_PATH = '/app';
    const userInfo = userApiService.getCurrentUserInfoFromStorage();
    this.LISTEN_PATH = `/user/${userInfo.id}`;
  }

  publish(topic: string, payload: any = null): void {
    this.rxStompService.publish({
      destination: `${this.SEND_PATH}${topic}`,
      body: JSON.stringify(payload),
      headers: this.headers
    });
  }

  watch(topic: string): Observable<Message> {
    return this.rxStompService.watch({
      destination: `${this.LISTEN_PATH}${topic}`,
      subHeaders: this.headers
    });
  }

}
