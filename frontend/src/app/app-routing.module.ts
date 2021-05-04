import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';

const routes: Routes = [
  {
    path: '',
    loadChildren: () =>
      import('./routed/main/main.module').then(m => m.MainModule)
  },
  {
    path: 'start',
    loadChildren: () =>
      import('./routed/start/start.module').then(m => m.StartModule)
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
