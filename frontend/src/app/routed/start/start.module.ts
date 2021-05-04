import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {AngularSvgIconModule} from 'angular-svg-icon';
import {HttpClientModule} from '@angular/common/http';

import {StartRoutingModule} from './start-routing.module';
import {LoginPage} from './pages/login/login.page';
import {FormsModule} from '@angular/forms';
import { SignupPage } from './pages/signup/signup.page';


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
    SignupPage
  ]
})
export class StartModule {
}
