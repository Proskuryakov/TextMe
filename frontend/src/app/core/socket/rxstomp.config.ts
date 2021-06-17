import {InjectableRxStompConfig} from '@stomp/ng2-stompjs';
import {environment} from '../../../environments/environment';

export const rxStompConfig: InjectableRxStompConfig = {

  brokerURL: environment.socket,

  connectHeaders: {
    Authorization: `Bearer ${localStorage.getItem('access_token')}`
  },

  heartbeatIncoming: 0,
  heartbeatOutgoing: 20000,

  reconnectDelay: 500,

  debug: (msg: string): void => {
    console.log(new Date(), msg);
  },
};
