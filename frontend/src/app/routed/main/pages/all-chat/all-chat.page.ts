import {Component, OnInit} from '@angular/core';
import {MessengerApiService} from '../../../../features/chat/services/messenger-api.service';
import {Info} from '../../../../features/profile/models/info.model';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {DestinationType, Message, MessageInfo, MessageStatus} from '../../../../features/chat/models/message.model';
import {ChatFilterModel} from '../../../../features/chat/models/filter.model';
import {Router} from '@angular/router';

@Component({
  templateUrl: './all-chat.page.html',
  styleUrls: ['./all-chat.page.sass']
})
export class AllChatPage implements OnInit {

  userInfo: Info = null;
  chats: MessageInfo[] = [];
  chatFilter = 'ALL';
  messageFilter = 'ALL';
  searchName = '';

  constructor(
    private readonly msgApiService: MessengerApiService,
    private readonly userApiService: UserApiService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {
    this.userInfo = this.userApiService.getCurrentUserInfoFromStorage();
    this.msgApiService.getChats().subscribe(
      (chats) => this.chats = chats,
      (error) => console.log(error)
    );
  }

  filter(chats: MessageInfo[]): MessageInfo[] {
    switch (this.messageFilter) {
      case 'ALL':
        return chats;
      case 'OLD':
        return chats.filter(value => value.message.status === MessageStatus.READ);
      case 'NEW':
        return chats.filter(value => value.message.status === MessageStatus.SENT);
    }
  }

  reloadChats(): void {
    this.msgApiService.getChats(ChatFilterModel[this.chatFilter]).subscribe(
      (chats) => this.chats = this.filter(chats),
      (error) => console.log(error)
    );
  }

  search(): void {
    if (this.searchName.trim() === '') {
      this.reloadChats();
      return;
    }
    this.msgApiService.getChats(ChatFilterModel[this.chatFilter]).subscribe(
      (chats) => this.chats = this.filter(chats)
        .filter(value => this.getCompanion(value).name.includes(this.searchName.trim())),
      (error) => console.log(error)
    );
  }

  endSearch(): void {
    this.reloadChats();
  }

  getImageUrl(chat: MessageInfo): string {
    if (chat.destination === DestinationType.CHAT) {
      return this.userApiService.getImageUrl(chat.to);
    } else {
      return this.userApiService.getImageUrl(this.getCompanion(chat));
    }
  }

  isMyMessage(from: Info): boolean {
    return from.id === this.userInfo.id;
  }

  getCompanion(chat: MessageInfo): Info {
    if (chat.destination === DestinationType.CHAT) {
      return chat.to;
    }
    return this.isMyMessage(chat.from) ? chat.to : chat.from;
  }

  getFormatDate(message: Message): string {
    return this.msgApiService.formatDate(message);
  }

  open(chat: MessageInfo): void {
    const path = chat.destination.toString().toLowerCase();
    const id = this.getCompanion(chat).id;
    this.router.navigate([path, id]);
  }

  isRead(message: Message): boolean {
    return message.status === MessageStatus.READ;
  }

  getChatName(chat: MessageInfo): string {
    return this.getCompanion(chat).name;
  }

  getMessageAuthor(chat: MessageInfo): string {
    if (this.isMyMessage(chat.from)) {
      return 'Вы: ';
    } else if (chat.destination === DestinationType.CHAT) {
      return `${chat.from.name}:`;
    } else {
      return '';
    }
  }
}
