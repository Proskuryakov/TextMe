import {Component, OnInit} from '@angular/core';
import {AuthService} from '../../../../core/auth/auth.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  templateUrl: './activate.page.html',
  styleUrls: ['./activate.page.sass']
})
export class ActivatePage implements OnInit {

  error = false;

  constructor(
    private readonly authService: AuthService,
    private readonly activateRoute: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this.activate();
  }

  activate(): void {
    const code = this.activateRoute.snapshot.params.code;
    this.authService.activate(code).subscribe(
      () => this.error = false,
      (error) => {
        this.error = true;
        console.log(error.message);
      }
    );

  }

}
