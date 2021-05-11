import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../../../core/auth/auth.service';
import {User} from '../../../../core/auth/models';
import * as UIkit from 'uikit';

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

export class SignupPage implements OnInit {

  formData: SignupFormData;
  isButtonActive = true;
  error = false;
  isLoading = false;

  constructor(private readonly authService: AuthService) { }

  errorMessage: string;

  ngOnInit(): void { }

  // действие при нажатии кноки Зарегистрироваться
  signupClick(value: SignupFormData): void {
    this.formData = value;
    this.error = false;
    this.isLoading = false;
    this.isButtonActive = false;
    if (value.password !== value.repeat_password) {
      this.error = true;
      this.errorMessage = 'Пароли не совпадают';
    }else{
      this.signup();
    }
  }

  signup(): void {
    this.isLoading = true;
    const user = new User(
      this.formData.email,
      this.formData.login,
      this.formData.password
    );

    this.authService.signup(user).subscribe(
      () => {
        console.log('Registration success');
        this.isLoading = false;
        this.error = false;
        this.isButtonActive = false;
        UIkit.modal('#verify-mail-panel').show();
      },
      (error) => {
        console.log(error.error.message);
        this.isLoading = false;
        this.error = true;
        this.isButtonActive = true;
        this.errorMessage = 'Ошибка регистрации';
      }
    );
  }


}
