import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {ReportsPage} from './pages/reports/reports.page';
import {ModeratorsPage} from './pages/moderators/moderators.page';
import {OnlyAdminGuard} from '../../core/guards/only-admin.guard';
import {ReportsSummaryPage} from './pages/reports-summary/reports-summary.page';

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
    path: 'reports/summary/:id',
    component: ReportsSummaryPage
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
