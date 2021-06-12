import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MainRoutingModule } from './main-routing.module';
import { MainPage } from './pages/main/main.page';
import { NavigationPanelComponent } from './components/navigation-panel/navigation-panel.component';
import { MainPageUserCardComponent } from './components/main-page-user-card/main-page-user-card.component';
import { LeftNavbarLogoComponent } from './components/left-navbar-logo/left-navbar-logo.component';
import { AllChatPage } from './pages/all-chat/all-chat.page';
import { ChatPage } from './pages/chat/chat.page';
import { ProfilePage } from './pages/profile/profile.page';
import {FormsModule} from '@angular/forms';


@NgModule({
  declarations: [
    MainPage,
    NavigationPanelComponent,
    MainPageUserCardComponent,
    LeftNavbarLogoComponent,
    AllChatPage,
    ChatPage,
    ProfilePage
  ],
    imports: [
        CommonModule,
        MainRoutingModule,
        FormsModule
    ]
})
export class MainModule { }
