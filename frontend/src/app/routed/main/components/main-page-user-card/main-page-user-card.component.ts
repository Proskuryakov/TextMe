import {Component, Input, OnInit} from '@angular/core';
import {Profile} from '../../../../features/profile/models/profile.model';
import {UserApiService} from '../../../../features/profile/services/user-api.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-main-page-user-card',
  templateUrl: './main-page-user-card.component.html',
  styleUrls: ['./main-page-user-card.component.sass']
})
export class MainPageUserCardComponent implements OnInit {

  @Input() profile: Profile;

  imageSize = 100;

  constructor(
    private readonly userApiService: UserApiService,
    private readonly router: Router
  ) {}

  ngOnInit(): void {}

  startChat(): void {
    this.router.navigate(['direct', this.profile.info.id]);
  }

  report(): void {}

  addToFavourites(): void {}

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

}
