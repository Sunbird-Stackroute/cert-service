import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { CreateCertificateComponent } from './components/create-certificate/create-certificate.component';
import { CertificatesComponent } from './components/cert-verify/cert-verify.component';

const routes: Routes = [
  { path: ``, redirectTo: `generate/certificate`, pathMatch: `full` },
  { path: `generate/certificate`, component: CreateCertificateComponent },
  { path: `certs/:certId`, component: CertificatesComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})

export class AppRoutingModule { }
