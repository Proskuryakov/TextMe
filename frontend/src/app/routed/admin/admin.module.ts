import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {AdminRoutingModule} from './admin-routing.module';
import {ReportsPage} from './pages/reports/reports.page';
import {ModeratorsPage} from './pages/moderators/moderators.page';
import {FormsModule} from '@angular/forms';
import {MainModule} from '../main/main.module';
import {ReportsSummaryPage} from './pages/reports-summary/reports-summary.page';


@NgModule({
  declarations: [
    ReportsPage,
    ModeratorsPage,
    ReportsSummaryPage
  ],
  imports: [
    CommonModule,
    AdminRoutingModule,
    FormsModule,
    MainModule
  ]
})
export class AdminModule {}
