import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ReportsPage} from './pages/reports/reports.page';
import {ModeratorsPage} from './pages/moderators/moderators.page';
import {OnlyAdminGuard} from '../../core/guards/only-admin.guard';

const routes: Routes = [
  {
    path: '',
    redirectTo: 'reports'
  },
  {
    path: 'reports',
    component: ReportsPage
  },
  {
    path: 'moderators',
    canActivate: [OnlyAdminGuard],
    component: ModeratorsPage
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AdminRoutingModule { }
