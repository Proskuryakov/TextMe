import {LOCALE_ID, NgModule} from '@angular/core';
import {BrowserModule} from '@angular/platform-browser';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MainModule} from './routed/main/main.module';
import {CoreModule} from './core/core.module';
import {InjectableRxStompConfig, RxStompService, rxStompServiceFactory} from '@stomp/ng2-stompjs';
import {rxStompConfig} from './features/chat/config/rxstomp.config';

@NgModule({
  declarations: [
    AppComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    MainModule,
    CoreModule
  ],
  providers: [
    {
      provide: LOCALE_ID,
      useValue: 'ru-RU'
    },
    {
      provide: InjectableRxStompConfig,
      useValue: rxStompConfig,
    },
    {
      provide: RxStompService,
      useFactory: rxStompServiceFactory,
      deps: [InjectableRxStompConfig],
    },
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
