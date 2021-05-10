import {NgModule, Optional, SkipSelf} from '@angular/core';
import {CommonModule} from '@angular/common';
import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import {AuthInterceptor} from './auth/auth.interceptor';


@NgModule({
  imports: [CommonModule, HttpClientModule],
  exports: [HttpClientModule],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    }
  ]
})

export class CoreModule {
  constructor(
    @Optional() @SkipSelf() coreModule?: CoreModule
  ) {
    // tslint:disable-next-line:triple-equals
    if (coreModule != undefined) {
      throw Error('Core module must be defined only once');
    }
  }
}
