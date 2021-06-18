import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AdminRoutingModule } from './admin-routing.module';
import { ReportsPage } from './pages/reports/reports.page';
import { ModeratorsPage } from './pages/moderators/moderators.page';


@NgModule({
  declarations: [
    ReportsPage,
    ModeratorsPage
  ],
  imports: [
    CommonModule,
    AdminRoutingModule
  ]
})
export class AdminModule { }
