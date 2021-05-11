import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../../../core/auth/auth.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-left-navbar-logo',
  templateUrl: './left-navbar-logo.component.html',
  styleUrls: ['./left-navbar-logo.component.sass']
})
export class LeftNavbarLogoComponent implements OnInit {

  imageSize = 50;

  constructor(
    private readonly authService: AuthService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {}

  logout(): void {
    this.authService.deleteToken();
    this.router.navigate(['/start']);
  }
}
