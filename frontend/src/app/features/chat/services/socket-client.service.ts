import {Injectable, OnDestroy, OnInit} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {UserApiService} from '../../profile/services/user-api.service';
// import * as SockJS from 'sockjs-client';
// import * as Stomp from '@stomp/stompjs';
import {RxStompService} from '@stomp/ng2-stompjs';
import {AuthService} from '../../../core/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class SocketClientService implements OnDestroy {
  public LISTEN_PATH: string;
  public SEND_PATH: string;


  constructor(
    private rxStompService: RxStompService,
    private readonly userApiService: UserApiService,
    private readonly authService: AuthService
  ) {
    console.log(this.rxStompService.connected());
    this.SEND_PATH = '/app';
    userApiService.getCurrentUser().subscribe(
      (user) => {
        this.LISTEN_PATH = `/user/${user.info.id}`;
        console.log(this.LISTEN_PATH);
      }
    );
  }

  onMessage(topic: string, func): void {
    // const This = this;
    // this.client.connect(
    //   {},
    //   () => {
    //     This.client.subscribe(
    //       topic,
    //       func
    //     );
    //   }
    // );
  }

  send(topic: string, payload: any): void {
    this.rxStompService.publish({destination: topic, body: payload});
  }

  ngOnDestroy(): void {
    // if (this.client != null) {
    //   this.client.disconnect(() => console.log('disconnect'), {});
    // }
  }


}
