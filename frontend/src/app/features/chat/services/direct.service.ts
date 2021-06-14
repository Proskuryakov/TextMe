import { Injectable } from '@angular/core';
import {SocketClientService} from './socket-client.service';
import {NewMessageRequest} from '../models/message.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DirectService {

  constructor(
    private readonly socketService: SocketClientService
  ) { }


  sendMessage(message: NewMessageRequest): void{
    console.log(message);
    this.socketService.send('/app/direct/send-message/', message);
  }

  getSendMessage(func): void{
    this.socketService.onMessage('user/egor/queue/messenger/send', () => console.log('сообщение отправлено'));
  }

}
