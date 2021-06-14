import {Info} from '../../profile/models/info.model';

export enum MessageStatus {
  SENT, READ, DELETED
}

export class Message {
  id: number;
  content: string;
  dateCreate: Date;
  dateUpdate: Date;
  status: MessageStatus;

}

export enum DestinationType{
  CHAT, DIRECT
}

export class MessageInfo {
  from: Info;
  to: Info;
  message: Message;
  destination: DestinationType;
  images: string[];
}

export class NewMessageRequest {
  recipient: number;
  message: string;
}

