import {Component, OnInit} from '@angular/core';
import {Info} from '../../../../features/profile/models/info.model';
import {Message, MessageInfo, NewMessageRequest} from '../../../../features/chat/models/message.model';
import {MessengerSocketService} from '../../../../features/chat/services/messenger-socket.service';
import {MessengerApiService} from '../../../../features/chat/services/messenger-api.service';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  templateUrl: './direct.page.html',
  styleUrls: ['./direct.page.sass']
})
export class DirectPage implements OnInit {

  myInfo: Info;
  messages: MessageInfo[] = [];
  newMessage: string;
  companionInfo: Info;

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
    });

    this.msgSocketService.getDirectException().subscribe((exception) => console.log(exception.error));
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
      },
      (error) => console.log(error)
    );
  }

  isMyMessage(message: MessageInfo): boolean {
    return message.from.id === this.myInfo.id;
  }

  formatDate(message: Message): string {
    return this.msgApiService.formatDate(message, 'dd.MM HH:mm');
  }

  sendMessage(): void {
    console.log('тык');
    console.log(this.newMessage);
    if (!this.newMessage) {
      return;
    }
    const directMessage = new NewMessageRequest();
    directMessage.message = this.newMessage.trim();
    directMessage.recipient = this.companionInfo.id;

    this.msgSocketService.directSendMessage(directMessage);
    this.newMessage = '';
  }

  isUpdateMessage(message: MessageInfo): boolean {
    return message.message.dateUpdate !== null;
  }

  addFiles(): void {}

}
