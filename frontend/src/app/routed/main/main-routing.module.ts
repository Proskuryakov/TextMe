import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainPage} from './pages/main/main.page';
import {AllChatPage} from './pages/all-chat/all-chat.page';
import {ChatPage} from './pages/chat/chat.page';
import {ProfilePage} from './pages/profile/profile.page';

const routes: Routes = [
  {
    path: '',
    component: MainPage
  },
  {
    path: 'chat',
    component: AllChatPage
  },
  {
    path: 'chat/:id',
    component: ChatPage
  },
  {
    path: 'profile',
    component: ProfilePage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MainRoutingModule { }
