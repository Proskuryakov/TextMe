import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AngularSvgIconModule} from 'angular-svg-icon';
import {HttpClientModule} from '@angular/common/http';

import {StartRoutingModule} from './start-routing.module';
import {LoginPage} from './pages/login/login.page';
import {FormsModule} from '@angular/forms';
import { SignupPage } from './pages/signup/signup.page';
import { StartPage } from './pages/start/start.page';
import { ActivatePage } from './pages/activate/activate.page';


@NgModule({
  imports: [
    HttpClientModule,
    AngularSvgIconModule.forRoot(),
    CommonModule,
    StartRoutingModule,
    FormsModule
  ],
  declarations: [
    LoginPage,
    SignupPage,
    StartPage,
    ActivatePage
  ]
})
export class StartModule {
}
