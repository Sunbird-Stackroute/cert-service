import { BrowserModule, Meta } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';
import { AvatarModule } from 'ngx-avatar';
import { HttpClientModule } from '@angular/common/http';
import { HttpModule } from '@angular/http'
import {
  SuiSelectModule, SuiModalModule, SuiAccordionModule, SuiPopupModule, SuiDropdownModule, SuiProgressModule,
  SuiRatingModule, SuiCollapseModule, SuiCheckboxModule, SuiTabsModule, SuiModule
} from 'ng2-semantic-ui';
import { RouterModule } from '@angular/router';
import { DefaultTemplateComponent } from './components/default-template/default-template.component';
import { FormsModule, ReactiveFormsModule, } from '@angular/forms';
import { CreateCertificateComponent } from './components/create-certificate/create-certificate.component';
import { AngularSvgIconModule } from 'angular-svg-icon';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatDatepickerModule } from '@angular/material';
import { MatNativeDateModule } from '@angular/material';
import { MatIconModule } from '@angular/material/icon';
import { CertificatesComponent } from './components/cert-verify/cert-verify.component';

import { SlickModule } from 'ngx-slick';
import { HighlightModule } from 'ngx-highlightjs';
import xml from 'highlight.js/lib/languages/xml';
import scss from 'highlight.js/lib/languages/scss';
import typescript from 'highlight.js/lib/languages/typescript';
import html from 'highlight.js/lib/languages/htmlbars';
import { ClipboardModule } from 'ngx-clipboard';
import { NgxPageScrollModule } from 'ngx-page-scroll';
import { Ng2IziToastModule } from 'ng2-izitoast';
import { Ng2SearchPipeModule } from 'ng2-search-filter';
// requires BrowserAnimationsModule
// import HeadroomModule
import { HeadroomModule } from '@ctrl/ngx-headroom';
import { ThemeService } from './theme.service';
import { CountdownModule } from 'ngx-countdown';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

export function hljsLanguages() {
  return [
    {name: 'html', func: html},
    {name: 'typescript', func: typescript},
    {name: 'scss', func: scss},
    {name: 'xml', func: xml}
  ];
}

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    DefaultTemplateComponent,
    CreateCertificateComponent,
    CertificatesComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    AvatarModule,
    HttpClientModule,
    HttpModule,
    SuiSelectModule,
    SuiModalModule,
    SuiAccordionModule,
    SuiPopupModule,
    SuiDropdownModule,
    SuiProgressModule,
    SuiRatingModule,
    SuiCollapseModule,
    SuiCheckboxModule,
    SuiTabsModule,
    RouterModule,
    FormsModule,
    ReactiveFormsModule,
    AngularSvgIconModule,
    BrowserAnimationsModule,
    MatFormFieldModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatIconModule,
    HighlightModule.forRoot({ languages: hljsLanguages }),
    SuiModule,
    Ng2SearchPipeModule,
    Ng2IziToastModule,
    SlickModule,
    ClipboardModule,
    HeadroomModule,
    NgxPageScrollModule,
    CountdownModule,
    FontAwesomeModule,
  ],
  providers: [ThemeService, Meta ],
  bootstrap: [AppComponent]
})
export class AppModule { }
