import {Component, OnInit} from '@angular/core';
import {Profile} from '../../../../features/profile/models/profile.model';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {AuthService} from '../../../../core/auth/auth.service';
import {Router} from '@angular/router';
import {CardApiService} from '../../../../features/card/services/card-api.service';
import {TagApiService} from '../../../../features/card/services/tag-api.service';
import {AppRole} from '../../../../features/profile/models/role.model';

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
  message = '';
  isError = false;
  selectedImage: File = null;
  selectedImageURL: string;
  newTag = '';
  foundTags: string[] = [];
  isJustUser = true;
  isAdmin = false;

  constructor(
    private readonly userApiService: UserApiService,
    private readonly cardApiService: CardApiService,
    private readonly tagApiService: TagApiService,
    private readonly authService: AuthService,
    private readonly router: Router
  ) {
  }

  ngOnInit(): void {
    this.initUser();
    this.initRoles();
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

  initRoles(): void {
    this.userApiService.getRoles().subscribe(
      (roles) => {
        if (AppRole.ROLE_ADMIN in roles) {
          this.isAdmin = true;
          this.isJustUser = false;
        } else if (AppRole.ROLE_MODER in roles) {
          this.isAdmin = false;
          this.isJustUser = false;
        } else {
          this.isAdmin = false;
          this.isJustUser = true;
        }
      },
      () => this.isJustUser = true
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

    const reader = new FileReader();
    reader.onload = () => {
      this.selectedImageURL = reader.result as string;
    };
    reader.readAsDataURL(this.selectedImage);

  }

  deleteSelectedImage(): void {
    this.selectedImage = null;
    this.selectedImageURL = undefined;
  }

  uploadImage(): void {
    this.isError = false;
    this.isLoading = true;
    this.userApiService.uploadImage(this.selectedImage).subscribe(
      () => {
        this.isLoading = false;
      },
      () => {
        this.isLoading = false;
        this.isError = true;
        this.message = 'Ошибка загрузки изображения';
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

  addNewTag(): void {
    this.addTag(this.newTag);
    this.newTag = '';
  }

  searchTags(): void {
    this.message = '';
    if (this.newTag.trim() === '') {
      this.foundTags = [];
      return;
    }
    this.tagApiService.getTagsLike(this.newTag).subscribe(
      (tags) => this.foundTags = tags,
      (error) => console.log(error)
    );
  }

  addTag(tag: string): void {
    this.cardApiService.addTag(tag).subscribe(
      () => {
        this.initUser();
        this.isError = false;
        this.message = `Тег "${tag}" успешно добавлен`;
      }, () => {
        this.isError = true;
        this.message = 'Ошибка при добавлении тега';
      }
    );
  }

  deleteTag(tag: string): void {
    this.cardApiService.deleteTag(tag).subscribe(
      () => {
        this.initUser();
        this.isError = false;
        this.message = `Тег "${tag}" успешно удален`;
      }, () => {
        this.isError = true;
        this.message = 'Ошибка при удалении тега';
      }
    );
  }

  adminNavigate(path: string): void {
    this.router.navigate(['admin', path]);
  }

}
