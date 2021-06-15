import {Component, OnInit} from '@angular/core';
import {MessageInfo, NewMessageRequest} from '../../../../features/chat/models/message.model';
import {MessengerSocketService} from '../../../../features/chat/services/messenger-socket.service';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {Profile} from '../../../../features/profile/models/profile.model';

@Component({
  templateUrl: './chat.page.html',
  styleUrls: ['./chat.page.sass']
})
export class ChatPage implements OnInit {

  messages: MessageInfo[] = [];
  newMessage: string;
  user: Profile;

  constructor(
    private readonly msgSocketService: MessengerSocketService,
    private readonly userApiService: UserApiService
  ) {}

  ngOnInit(): void {
    this.userApiService.getCurrentUser().subscribe(
      user => {
        this.user = user;
      }
    );
    this.msgSocketService.getSendMessage().subscribe((message) => this.messages.push(message));
    this.msgSocketService.getDirectException().subscribe((exception) => console.log(exception.error));
  }

  sendMessage(): void {
    console.log('НАЖАЛИ КНОПКУ');
    const directMessage = new NewMessageRequest();
    directMessage.message = this.newMessage;
    directMessage.recipient = 1;
    // TODO убрать тестовый хардкод
    this.msgSocketService.directSendMessage(directMessage);
    this.newMessage = '';
  }
}
