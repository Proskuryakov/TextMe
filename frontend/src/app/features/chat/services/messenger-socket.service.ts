import {Injectable} from '@angular/core';
import {SocketService} from '../../../core/socket/socket.service';
import {ChatException, DirectException, MessageInfo, MessageUpdate, NewMessageRequest} from '../models/message.model';
import {Observable} from 'rxjs';
import {errorRoute, publishRoute, watchRoute} from '../models/messenger.socket.rout';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class MessengerSocketService {

  constructor(
    private readonly socketService: SocketService
  ) {}

  chatSendMessage(message: NewMessageRequest): void {
    this.socketService.publish(publishRoute.chat.send, message);
  }

  chatUpdateMessage(message: MessageUpdate): void {
    this.socketService.publish(publishRoute.chat.update, message);
  }

  chatDeleteMessage(msgId: number): void {
    this.socketService.publish(`${publishRoute.chat.delete}/${msgId}`);
  }

  chatReadMessage(msgId: number): void {
    this.socketService.publish(`${publishRoute.chat.read}/${msgId}`);
  }

  directSendMessage(message: NewMessageRequest): void {
    this.socketService.publish(publishRoute.direct.send, message);
  }

  directUpdateMessage(message: MessageUpdate): void {
    this.socketService.publish(publishRoute.direct.update, message);
  }

  directDeleteMessage(msgId: number): void {
    this.socketService.publish(`${publishRoute.direct.delete}/${msgId}`);
  }

  directReadMessage(msgId: number): void {
    this.socketService.publish(`${publishRoute.direct.read}/${msgId}`);
  }

  getSendMessage(): Observable<MessageInfo> {
    return this.socketService.watch(watchRoute.send).pipe(
      map(
        (message) => JSON.parse(message.body)
      )
    );
  }

  getUpdateMessage(): Observable<MessageInfo> {
    return this.socketService.watch(watchRoute.update).pipe(
      map(
        (message) => JSON.parse(message.body)
      )
    );
  }

  getDeleteMessage(): Observable<MessageInfo> {
    return this.socketService.watch(watchRoute.delete).pipe(
      map((message) => JSON.parse(message.body))
    );
  }

  getReadMessage(): Observable<MessageInfo> {
    return this.socketService.watch(watchRoute.read).pipe(
      map(
        (message) => JSON.parse(message.body)
      )
    );
  }

  getChatException(): Observable<ChatException> {
    return this.socketService.watch(errorRoute.chat).pipe(
      map(
        (error) => JSON.parse(error.body)
      )
    );
  }

  getDirectException(): Observable<DirectException> {
    return this.socketService.watch(errorRoute.direct).pipe(
      map(
        (error) => JSON.parse(error.body)
      )
    );
  }

}
