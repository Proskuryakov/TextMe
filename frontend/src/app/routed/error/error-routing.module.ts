import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {BannedPage} from './pages/banned/banned.page';

const routes: Routes = [
  {
    path: 'banned',
    component: BannedPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ErrorRoutingModule { }
