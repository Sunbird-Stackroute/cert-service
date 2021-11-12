import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import axios from 'axios';

@Injectable({
  providedIn: 'root'
})
export class CertVerifyService {
  status = { validateStatus: code => code >= 200 && code <= 500 };

  constructor() {
    axios.defaults.baseURL = environment.url;
    axios.defaults.headers = { 'content-type': 'application/json', 'Access-Control-Allow-Origin': '*' };
  }

  async certValidate(certId, accessCode) {
    return await axios.post(`/certs/v1/registry/validate`, { "request": { certId, accessCode } });
    // return await axios.post(`/certs/v1/registry/validate`, { "request": { certId, accessCode } }, this.status);
  }
}