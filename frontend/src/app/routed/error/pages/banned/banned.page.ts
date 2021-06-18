import { Component, OnInit } from '@angular/core';
import {AuthService} from '../../../../core/auth/auth.service';
import {Router} from '@angular/router';

@Component({
  templateUrl: './banned.page.html',
  styleUrls: ['./banned.page.sass']
})
export class BannedPage implements OnInit {

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
  }

  getEndBanTime(): string {
    const message = localStorage.getItem('banned');
    const index = message.indexOf('BANNED TO');
    return message.substr(index + 10, 19);
  }

  logout(): void {
    this.authService.deleteToken();
    this.router.navigate(['start']);
  }

}
