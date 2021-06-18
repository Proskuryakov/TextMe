import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';
import {Profile} from '../../../../features/profile/models/profile.model';
import {CardApiService} from '../../../../features/card/services/card-api.service';
import {FavoriteApiService} from '../../../../features/card/services/favorite-api.service';

@Component({
  templateUrl: './main.page.html',
  styleUrls: ['./main.page.sass']
})
export class MainPage implements OnInit {

  cards: Profile[] = [];
  cardFilter = 'RANDOM';
  searchTag: string;
  isFavorites = false;

  constructor(
    private readonly router: Router,
    private readonly cardApiService: CardApiService,
    private readonly favoriteApiService: FavoriteApiService
  ) { }

  ngOnInit(): void {
    this.loadCards();
  }

  loadCards(): void {
    this.isFavorites = false;
    switch (this.cardFilter) {
      case 'RANDOM':
        this.cardApiService.getRandomCards().subscribe(
          (cards) => this.cards = cards,
          (error) => console.log(error)
        );
        break;
      case 'TARGET':
        this.cardApiService.getCards().subscribe(
          (cards) => this.cards = cards,
          (error) => console.log(error)
        );
        break;
      case 'FAVORITES':
        this.isFavorites = true;
        this.favoriteApiService.getFavorites().subscribe(
          (cards) => this.cards = cards,
          (error) => console.log(error)
        );
        break;
    }
  }

  searchCards(): void {
    if (this.searchTag.trim() === '') {
      this.loadCards();
    }else {
      this.cardApiService.getCards(0, this.searchTag).subscribe(
        (cards) => this.cards = cards,
        (error) => console.log(error)
      );
    }
  }

  navigateTo(address: string): void{
    this.router.navigate([address]);
  }
}
