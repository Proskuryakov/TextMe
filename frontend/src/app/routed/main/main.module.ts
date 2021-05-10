import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainRoutingModule } from './main-routing.module';
import { MainPage } from './pages/main/main.page';
import { NavigationPanelComponent } from './components/navigation-panel/navigation-panel.component';
import { MainPageUserCardComponent } from './components/main-page-user-card/main-page-user-card.component';
import { LeftNavbarLogoComponent } from './components/left-navbar-logo/left-navbar-logo.component';


@NgModule({
  declarations: [
    MainPage,
    NavigationPanelComponent,
    MainPageUserCardComponent,
    LeftNavbarLogoComponent
  ],
  imports: [
    CommonModule,
    MainRoutingModule
  ]
})
export class MainModule { }
