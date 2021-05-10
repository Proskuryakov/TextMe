import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../../../core/auth/auth.service';
import {User} from '../../../../core/auth/models';
import {Router} from '@angular/router';


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
  error = false;
  isLoading = false;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {}

  isEmail(text: string): boolean {
    const regexp = new RegExp(/^(([^<>()\[\]\\.,;:\s@"]+(\.[^<>()\[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);
    return regexp.test(text);
  }

  //  действие по нажатию кнопки "войти"
  handleFormSubmit(value: LoginFormData): void {
    this.error = false;
    this.isLoading = true;

    const user: User = new User();
    user.password = value.password;

    if (this.isEmail(value.login)){
      user.email = value.login;
    }else{
      user.nickname = value.login;
    }

    this.authService
      .login(user)
      .subscribe(
        () => {
          this.isLoading = false;
          this.router.navigate(['/']);
        },
        () => {
          this.isLoading = false;
          this.error = true;
        }
      );

  }

}
