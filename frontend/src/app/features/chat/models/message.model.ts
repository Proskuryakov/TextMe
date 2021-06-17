import {Info} from '../../profile/models/info.model';

export enum MessageStatus {
  SENT = 'SENT', RECIEVED = 'RECIEVED', DELETED = 'DELETED'
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
  images: string[];
}

export class MessageUpdate {
  id: number;
  content: string;
}

export enum MessageError {
  ADDRESS_NOT_FOUND, MESSAGE_NOT_FOUND, FROM_BLOCKED, TO_BLOCKED, TIMEOUT, NOT_PERMS
}

export class DirectException {
  error: MessageError;
}

export class ChatException{
  error: MessageError;
}
