import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginPage} from './pages/login/login.page';
import {SignupPage} from './pages/signup/signup.page';
import {StartPage} from './pages/start/start.page';
import {ActivatePage} from './pages/activate/activate.page';

const routes: Routes = [
  {
    path: '',
    component: StartPage
  },
  {
    path: 'login',
    component: LoginPage
  },
  {
    path: 'signup',
    component: SignupPage
  },
  {
    path: 'activate/:code',
    component: ActivatePage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class StartRoutingModule {
}
