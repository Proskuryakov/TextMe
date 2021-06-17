import {Component, OnInit} from '@angular/core';
import {Info} from '../../../../features/profile/models/info.model';
import {Message, MessageInfo, MessageStatus, MessageUpdate, NewMessageRequest} from '../../../../features/chat/models/message.model';
import {MessengerSocketService} from '../../../../features/chat/services/messenger-socket.service';
import {MessengerApiService} from '../../../../features/chat/services/messenger-api.service';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {ActivatedRoute, Router} from '@angular/router';
import * as UIkit from 'uikit';

@Component({
  templateUrl: './direct.page.html',
  styleUrls: ['./direct.page.sass']
})
export class DirectPage implements OnInit {

  myInfo: Info;
  messages: MessageInfo[] = [];
  newMessage: string;
  companionInfo: Info;
  selectedMessage: MessageInfo;

  constructor(
    private readonly msgSocketService: MessengerSocketService,
    private readonly msgApiService: MessengerApiService,
    private readonly userApiService: UserApiService,
    private readonly activatedRoute: ActivatedRoute,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.myInfo = this.userApiService.getCurrentUserInfoFromStorage();
    this.activatedRoute.params.subscribe(
      (params) => {
        this.getSelectedCompanion(params.id);
      }
    );
    this.subscribeAllMessage();
  }

  subscribeAllMessage(): void {
    this.msgSocketService.getSendMessage().subscribe((message) => {
      this.messages.unshift(message);
      if (!this.isMyMessage(message)){
        this.readMessage(message);
      }
    });

    this.msgSocketService.getReadMessage().subscribe((message) =>
      this.messages.find(msg => msg.message.id === message.message.id).message.status = MessageStatus.RECIEVED);

    this.msgSocketService.getDeleteMessage().subscribe((message) =>
      this.messages = this.messages.filter((msg) => msg.message.id !== message.message.id));

    this.msgSocketService.getUpdateMessage().subscribe((message) => {
      const updateMsgIndex = this.messages.findIndex((msg) => msg.message.id === message.message.id);
      this.messages[updateMsgIndex] = message;
    });

    this.msgSocketService.getDirectException().subscribe((exception) =>
      console.log('SOCKET exception', exception.error.toString()));
  }

  getSelectedCompanion(companionId: number): void {
    this.userApiService.getUser(companionId).subscribe(
      (user) => {
        this.companionInfo = user.info;
        this.loadMessages();
      },
      (error) => {
        this.router.navigate(['/messenger']);
      }
    );
  }

  loadMessages(): void {
    this.msgApiService.getDirectMessages(this.companionInfo.id).subscribe(
      (messages) => {
        this.messages = messages;
        this.readMessages();
      },
      (error) => console.log(error)
    );
  }

  readMessages(): void {
    this.messages
      .filter(msg => !this.isMyMessage(msg))
      .forEach(msg => this.readMessage(msg));
  }

  isMyMessage(message: MessageInfo): boolean {
    return message.from.id === this.myInfo.id;
  }

  formatDate(message: Message): string {
    return this.msgApiService.formatDate(message, 'dd.MM HH:mm');
  }

  messageClick(message: MessageInfo): void {
    if (this.isMyMessage(message)){
      this.selectedMessage = message;
      UIkit.modal('#update-delete-panel').show();
    }
  }

  deleteSelectedMessage(): void {
    UIkit.modal('#update-delete-panel').hide();
    if (this.selectedMessage == null) {
      return;
    }
    this.msgSocketService.directDeleteMessage(this.selectedMessage.message.id);
  }

  updateSelectedMessage(text: string): void {
    UIkit.modal('#update-delete-panel').hide();
    if (text.trim() === ''){
      return;
    }
    const updatedMessage = new MessageUpdate();
    updatedMessage.id = this.selectedMessage.message.id;
    updatedMessage.content = text.trim();
    this.msgSocketService.directUpdateMessage(updatedMessage);
  }

  sendMessage(): void {
    if (!this.newMessage) {
      return;
    }
    const directMessage = new NewMessageRequest();
    directMessage.message = this.newMessage.trim();
    directMessage.recipient = this.companionInfo.id;

    this.msgSocketService.directSendMessage(directMessage);
    this.newMessage = '';
  }

  readMessage(message: MessageInfo): void {
    this.msgSocketService.directReadMessage(message.message.id);
  }

  isUpdateMessage(message: MessageInfo): boolean {
    return message.message.dateUpdate !== null;
  }

  addFiles(): void {}

  isMessageRead(message: MessageInfo): boolean {
    return message.message.status === MessageStatus.RECIEVED;
  }

}
