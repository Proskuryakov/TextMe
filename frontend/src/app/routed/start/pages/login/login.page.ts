import {Component, OnInit} from '@angular/core';


interface LoginFormData {
  login: string;
  password: string;
}

@Component({
  templateUrl: './login.page.html',
  styleUrls: ['./login.page.sass']
})
// tslint:disable-next-line:component-class-suffix
export class LoginPage implements OnInit {

  constructor() {
  }

  ngOnInit(): void {
  }

  //  действие по нажатию кнопки "войти"
  handleFormSubmit(value: LoginFormData): void {}

}
