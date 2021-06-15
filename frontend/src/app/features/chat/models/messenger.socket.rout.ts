export const publishRoute = {
  direct: {
    send: '/direct/send-message/',
    update: '/direct/update-message/',
    delete: '/direct/delete-message', // +{msgId}
    read: '/direct/read-message' // +{msgId}
  },
  chat: {
    send: '/chat/send-message/',
    update: '/chat/update-message/',
    delete: '/chat/delete-message', // +{msgId}
    read: '/chat/read-message' // +{msgId}
  }
};

export const watchRoute = {
  send: '/queue/messenger/send',
  update: '/queue/messenger/update',
  delete: '/queue/messenger/delete',
  read: '/queue/messenger/read'
};

export const errorRoute = {
  direct: '/queue/direct/errors',
  chat: '/queue/chat/errors'
};
