import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { SignupComponent } from './components/signup/signup.component';
import { LandingPageComponent } from './components/landingpage/landingpage.component';
import { LoginComponent } from './components/login/login.component';
import { CreateCertificateComponent } from './components/create-certificate/create-certificate.component';

const routes: Routes = [
  {
    path: '',
    component: LandingPageComponent,
  },
  {
    path: 'signUp', component: SignupComponent,
  },
  {
    path: 'logIn', component: LoginComponent
  },
  {
    path: 'generate/certificate', component: CreateCertificateComponent
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
