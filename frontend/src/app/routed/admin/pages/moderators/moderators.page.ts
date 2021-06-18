import {Component, OnInit} from '@angular/core';
import {AdminApiService} from '../../../../features/admin/services/admin-api.service';
import {Info} from '../../../../features/profile/models/info.model';
import {UserApiService} from '../../../../features/profile/services/user-api.service';

@Component({
  templateUrl: './moderators.page.html',
  styleUrls: ['./moderators.page.sass']
})
export class ModeratorsPage implements OnInit {
  statusMessage = '';
  moderators: Info[] = [];
  newModerName: string;
  isError = false;
  myInfo: Info;

  constructor(
    private readonly adminApiService: AdminApiService,
    private readonly userApiService: UserApiService
  ) { }

  ngOnInit(): void {
    this.myInfo = this.userApiService.getCurrentUserInfoFromStorage();
    this.loadModerators();
  }

  loadModerators(): void {
    this.isError = false;
    this.statusMessage = '';
    this.adminApiService.getModerators().subscribe(
      (moderators) => this.moderators = moderators.filter((moder) => moder.id !== this.myInfo.id),
      () => {
        this.isError = true;
        this.statusMessage = 'Ошибка при загрузке модераторов';
      }
    );
  }

  deleteModer(moder: Info): void {
    this.isError = false;
    this.statusMessage = '';
    this.adminApiService.deleteModerator(moder.id).subscribe(
      () => {
        this.loadModerators();
        if (!this.isError) {
          this.statusMessage = `Пользователь ${moder.name} больше не модератор`;
        }
      },
      () => {
        this.isError = true;
        this.statusMessage = `Ошибка при разжаловании модератора ${moder.name}`;
      }
    );
  }

  addModer(): void {
    if (!this.newModerName) {
      return;
    }
    this.isError = false;
    this.statusMessage = '';
    this.adminApiService.getUser(this.newModerName).subscribe(
      (user) => {
        if (user === null) {
          this.isError = true;
          this.statusMessage = `Пользователь "${this.newModerName}" не найден`;
        }
        this.adminApiService.addModerator(user.info.id).subscribe(
          () => {
            this.loadModerators();
            this.statusMessage = `Пользователь "${this.newModerName}" стал модератором`;
            this.newModerName = '';
          },
          () => {
            this.isError = true;
            this.statusMessage = 'Произошла ошибки при выдавании прав модератора';
          }
        );
      }, () => {
        this.isError = true;
        this.statusMessage = `Пользователь "${this.newModerName}" не найден`;
      }
    );
  }


  getImage(moder: Info): string {
    return this.userApiService.getImageUrl(moder);
  }

}
