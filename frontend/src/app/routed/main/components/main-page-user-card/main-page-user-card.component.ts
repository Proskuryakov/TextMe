import {Component, OnInit} from '@angular/core';

@Component({
  selector: 'app-main-page-user-card',
  templateUrl: './main-page-user-card.component.html',
  styleUrls: ['./main-page-user-card.component.sass']
})
export class MainPageUserCardComponent implements OnInit {

  imageSize = 100;

  constructor() {}

  ngOnInit(): void {}

  startChat(userId: number): void {}

  report(userId: number): void {}

  addToFavourites(userId: number): void {}

  notInterested(userId: number): void {}
}
