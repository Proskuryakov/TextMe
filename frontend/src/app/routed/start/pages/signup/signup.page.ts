import {Component, OnInit} from '@angular/core';

interface SignupFormData {
  login: string;
  email: string;
  password: string;
  repeat_password: string;
}


@Component({
  templateUrl: './signup.page.html',
  styleUrls: ['./signup.page.sass']
})
// tslint:disable-next-line:component-class-suffix
export class SignupPage implements OnInit {

  formData: SignupFormData;

  constructor() {}

  ngOnInit(): void {}

  // действие при нажатии кноки Зарегистрироваться
  signupClick(value: SignupFormData): void {
    this.formData = value;
  }

  // подтверждение почты
  confirmEmail(): void {}

  repeatVerificationCode(): void {}

}
