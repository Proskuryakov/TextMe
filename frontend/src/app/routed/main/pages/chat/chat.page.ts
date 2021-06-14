import {Component, OnInit} from '@angular/core';
import {
  DestinationType,
  Message,
  MessageInfo,
  MessageStatus,
  NewMessageRequest
} from '../../../../features/chat/models/message.model';
import {DirectService} from '../../../../features/chat/services/direct.service';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {Profile} from '../../../../features/profile/models/profile.model';
import {Info} from '../../../../features/profile/models/info.model';

@Component({
  templateUrl: './chat.page.html',
  styleUrls: ['./chat.page.sass']
})
export class ChatPage implements OnInit {

  messages: MessageInfo[];
  newMessage: string;
  user: Profile;

  constructor(
    private readonly directService: DirectService,
    private readonly userApiService: UserApiService
  ) {
    userApiService.getCurrentUser().subscribe(
      user => this.user = user
    );
    //
    // const message = new MessageInfo();
    // message.destination = DestinationType.CHAT;
    // message.from = this.user.info;
    // message.to = new Info();
    // message.to.id = 2;
    // message.to.name = 'danila';
    // message.message = new Message();
    // message.message.content = 'Привет';
    // message.message.dateCreate = new Date();
    // message.message.id = 1;
    // message.message.status = MessageStatus.READ;
    //
    // const message1 = new MessageInfo();
    // message1.destination = DestinationType.CHAT;
    // message1.from = new Info();
    // message1.from.id = 2;
    // message1.from.name = 'danila';
    // message1.to = new Info();
    // message1.to.id = 1;
    // message1.to.name = 'egor';
    // message1.message = new Message();
    // message1.message.content = 'Привет';
    // message1.message.dateCreate = new Date();
    // message1.message.id = 2;
    // message1.message.status = MessageStatus.READ;
    // this.messages.push(message, message1);
  }

  ngOnInit(): void {
    // this.directService.getSendMessage().subscribe(
    //   (message) => {
    //     this.messages.push(message);
    //   }
    // );
  }

  sendMessage(): void {
    console.log('НАЖАЛИ КНОПКУ');
    const directMessage = new NewMessageRequest();
    directMessage.message = this.newMessage;
    directMessage.recipient = 1;
    // TODO убрать тестовый хардкод
    this.directService.sendMessage(directMessage);
    this.newMessage = '';
  }
}
