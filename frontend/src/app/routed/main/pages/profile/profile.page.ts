import {Component, OnInit} from '@angular/core';
import {Profile} from '../../../../features/profile/models/profile.model';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {AuthService} from '../../../../core/auth/auth.service';
import {Router} from '@angular/router';
import {HttpEventType} from '@angular/common/http';
import {CardApiService} from '../../../../features/profile/services/card-api.service';

@Component({
  templateUrl: './profile.page.html',
  styleUrls: ['./profile.page.sass']
})
export class ProfilePage implements OnInit {
  user: Profile;

  newName = '';
  newStatus = '';
  passwordForm: any = {
    old_password: '',
    new_password: '',
    new_password_again: ''
  };
  isLoading = false;
  isProgressBar = false;
  message = '';
  isError = false;
  uploadProgress = 0;
  selectedImage: File = null;
  selectedImageURL: string;

  constructor(
    private readonly userApiService: UserApiService,
    private readonly cardApiService: CardApiService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {
  }

  ngOnInit(): void {
    this.initUser();
  }

  initUser(): void {
    this.userApiService.getCurrentUser().subscribe(
      (user) => {
        this.user = user;
      },
      (error) => {
        console.log(error.error.message);
      }
    );
  }

  logout(): void {
    this.authService.deleteToken();
    this.userApiService.deleteCurrentUserInfoFromStorage();
    this.router.navigate(['/start']);
  }

  onImageSelected(event: Event): void {
    // @ts-ignore
    this.selectedImage = (event.target as HTMLInputElement).files[0];
    console.log('image selected');
    console.log(this.selectedImage);

    const reader = new FileReader();
    reader.onload = () => {
      this.selectedImageURL = reader.result as string;
    };
    reader.readAsDataURL(this.selectedImage);

  }

  uploadImage(): void {
    this.isProgressBar = true;
    this.userApiService.uploadImage(this.selectedImage).subscribe(
      event => {
        // tslint:disable-next-line:triple-equals
        if (event.type == HttpEventType.UploadProgress) {
          this.uploadProgress = Math.round(event.loaded / event.total * 100);
          // tslint:disable-next-line:triple-equals
        } else if (event.type == HttpEventType.Response) {
          console.log(event);
          this.isProgressBar = false;
        }
      }
    );
  }

  updateNickname(): void {
    this.message = '';
    this.isLoading = true;
    this.userApiService.updateName(this.newName).subscribe(
      () => {
        this.initUser();
        this.isLoading = false;
        this.message = 'Имя пользователя успешно обновлено';
        this.isError = false;
        this.newName = '';
      },
      () => {
        this.isLoading = false;
        this.message = 'Ошибка обновления имени пользователя';
        this.isError = true;
      }
    );
  }

  updateStatus(): void {
    this.message = '';
    this.isLoading = true;
    this.cardApiService.updateStatus(this.newStatus).subscribe(
      () => {
        this.initUser();
        this.isLoading = false;
        this.message = 'Статус успешно обновлён';
        this.isError = false;
        this.newStatus = '';
      },
      () => {
        this.isLoading = false;
        this.message = 'Ошибка обновления статуса';
        this.isError = true;
      }
    );
  }

  updatePassword(): void {
    this.isError = true;
    if (this.passwordForm.new_password_again !== this.passwordForm.new_password) {
      this.message = 'Новые пароли не совпадают';
      return;
    } else if (this.passwordForm.old_password === this.passwordForm.new_password) {
      this.message = 'Новый пароль не отличается от старого';
      return;
    }
    this.message = '';
    this.isLoading = true;
    this.userApiService.updatePassword(this.passwordForm.old_password, this.passwordForm.new_password).subscribe(
      () => {
        this.isLoading = false;
        this.isError = false;
        this.message = 'Пароль успешно обновлён';
      },
      () => {
        this.isLoading = false;
        this.isError = true;
        this.message = 'Ошибка при обновлении пароля';
      }
    );
  }

  isPasswordChangBtnDisable(): boolean {
    const minPasswordLen = 6;
    return this.passwordForm.old_password.length < minPasswordLen ||
      this.passwordForm.new_password.length < minPasswordLen ||
      this.passwordForm.new_password_again.length < minPasswordLen;
  }

}
