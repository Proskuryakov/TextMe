import {Injectable, OnDestroy} from '@angular/core';
import {environment} from '../../../../environments/environment';
import {UserApiService} from '../../profile/services/user-api.service';
import * as SockJS from 'sockjs-client';
import * as Stomp from '@stomp/stompjs';
import {AuthService} from '../../../core/auth/auth.service';

@Injectable({
  providedIn: 'root'
})
export class SocketClientService implements OnDestroy {
  public LISTEN_PATH: string;
  public SEND_PATH: string;

  private client: Stomp.Client;

  constructor(
    private readonly userApiService: UserApiService,
    private readonly authService: AuthService
  ) {
    this.connect();

    console.log('Клиента создали');
    console.log(this.client);

    this.SEND_PATH = '/app';
    userApiService.getCurrentUser().subscribe(
      (user) => {
        this.LISTEN_PATH = `/user/${user.info.id}`;
        console.log(this.LISTEN_PATH);
      }
    );
  }

  connect(): void {
    const socket = new SockJS(environment.socket_endpoint);
    this.client = Stomp.over(socket);

    const authToken = this.authService.getToken();
    const This = this;

    this.client.connect(
      {
        Authorization: `Bearer ${authToken}`
      },
      (frame) => {
        console.log('Connected: ' + frame);
        This.client.subscribe('/user/1/queue/messenger/send', (payload) => {
          console.log(payload);
        });
      },
      (error) => console.log(error));
  }

  onMessage(topic: string, func): void {
    const This = this;
    this.client.connect(
      {},
      () => {
        This.client.subscribe(
          topic,
          func
        );
      }
    );
  }


  send(topic: string, payload: any): void {
    const authToken = this.authService.getToken();
    this.client.send(
      topic,
      {Authorization: `Bearer ${authToken}`},
      JSON.stringify(payload)
    );
  }

  ngOnDestroy(): void {
    if (this.client != null) {
      this.client.disconnect(() => console.log('disconnect'), {});
    }
  }


}
