import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {StartGuard} from './core/guards/start.guard';
import {MainGuard} from './core/guards/main.guard';
import {AdminGuard} from './core/guards/admin.guard';

const routes: Routes = [
  {
    path: '',
    canActivate: [MainGuard],
    loadChildren: () =>
      import('./routed/main/main.module').then(m => m.MainModule)
  },
  {
    path: 'start',
    canActivate: [StartGuard],
    loadChildren: () =>
      import('./routed/start/start.module').then(m => m.StartModule)
  },
  {
    path: 'admin',
    canActivate: [AdminGuard],
    loadChildren: () =>
      import('./routed/admin/admin.module').then(m => m.AdminModule)
  },
  {
    path: 'error',
    loadChildren: () =>
      import('./routed/error/error.module').then(m => m.ErrorModule)
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
