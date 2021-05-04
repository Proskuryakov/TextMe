import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainRoutingModule } from './main-routing.module';
import { MainPage } from './pages/main/main.page';
import { NavigationPanelComponent } from './components/navigation-panel/navigation-panel.component';


@NgModule({
  declarations: [
    MainPage,
    NavigationPanelComponent
  ],
  imports: [
    CommonModule,
    MainRoutingModule
  ]
})
export class MainModule { }
