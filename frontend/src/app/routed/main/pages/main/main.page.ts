import { Component, OnInit } from '@angular/core';
import {Router} from '@angular/router';

@Component({
  templateUrl: './main.page.html',
  styleUrls: ['./main.page.sass']
})
export class MainPage implements OnInit {

  constructor(private readonly router: Router) { }

  ngOnInit(): void {
  }

  navigateTo(address: string): void{
    this.router.navigate([address]);
  }
}
