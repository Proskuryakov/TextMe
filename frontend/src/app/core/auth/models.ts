export class User {
  constructor(email: string = '', nickname: string = '', password: string = '') {
    this.email = email;
    this.nickname = nickname;
    this.password = password;
  }

  email: string;
  nickname: string;
  password: string;
}

export class Token{
  accessToken: string;
}
