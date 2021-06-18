import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {Profile} from '../../../../features/profile/models/profile.model';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {Router} from '@angular/router';
import * as UIkit from 'uikit';
import {CardApiService} from '../../../../features/card/services/card-api.service';
import {FavoriteApiService} from '../../../../features/card/services/favorite-api.service';

@Component({
  selector: 'app-main-page-user-card',
  templateUrl: './main-page-user-card.component.html',
  styleUrls: ['./main-page-user-card.component.sass']
})
export class MainPageUserCardComponent implements OnInit {

  @Input() profile: Profile;
  @Input() isFavorites = false;
  @Output() favoriteDelete = new EventEmitter<void>();
  reportMessage = '';
  isSend = false;
  isError = false;
  sendResultMessage = '';

  imageSize = 100;

  constructor(
    private readonly userApiService: UserApiService,
    private readonly cardApiService: CardApiService,
    private readonly favoriteApiService: FavoriteApiService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {}

  startChat(): void {
    this.router.navigate(['direct', this.profile.info.id]);
  }

  report(): void {
    UIkit.modal('#report-dialog').show();
  }

  addToFavorites(): void {
    this.favoriteApiService.addFavorite(this.profile.info.id).subscribe(() => this.isFavorites = true);
  }

  deleteFromFavorites(): void {
    this.favoriteApiService.deleteFavorite(this.profile.info.id).subscribe(() => {
      this.isFavorites = false;
      this.favoriteDelete.emit();
    });
  }

  getImageUrl(): string {
    return this.userApiService.getImageUrl(this.profile.info);
  }

  getTags(): string[] {
    if (this.profile.card.tags.length > 0) {
      return this.profile.card.tags;
    } else {
      return ['Теги', 'пока', 'не', 'добавлены'];
    }
  }

  sendReport(): void {
    this.isSend = false;
    this.isError = false;
    this.cardApiService.report(this.profile.card.id, this.reportMessage).subscribe(
      () => {
        this.isSend = true;
        this.sendResultMessage = `Жалоба на пользователя ${this.profile.info.name} успешно отправлена`;
      },
      () => {
        this.isError = true;
        this.sendResultMessage = `Ошибка при отправке жалобы`;
      }
    );
  }

}
