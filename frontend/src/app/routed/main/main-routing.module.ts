import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {MainPage} from './pages/main/main.page';
import {AllChatPage} from './pages/all-chat/all-chat.page';

const routes: Routes = [
  {
    path: '',
    component: MainPage
  },
  {
    path: 'chat',
    component: AllChatPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class MainRoutingModule { }
