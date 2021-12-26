import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { map, catchError } from 'rxjs/operators';
import urlConfig from '../urlConfig.json';
import { IEmailCertificate } from '../email-certificate.model';

@Injectable({
  providedIn: 'root'
})

export class CertificateService {
  http: HttpClient;
  baseUrl: string;
  searchArray = ['tanya', 'raju', 'rishabh', 'vijay']
  constructor(http: HttpClient) {
    this.http = http;
    this.baseUrl = urlConfig.URLS.BASE_URL;
  }

  public getCertificateList(): Observable<any> {
    const headers = { 'content-type': 'application/json', 'Access-Control-Allow-Origin': '*' }
    return this.http.get(`${this.baseUrl}${urlConfig.URLS.SHOW_CERT}`, { headers }).pipe(
      map((response: any) => {
        return response;
      }),
      catchError((err) => {
        return err;
      })
    );
  }

  sendNotificationToUser(emailCertificate: IEmailCertificate): Observable<any> {
    console.log("emailCertificateObject", emailCertificate);
    const headers = { 'content-type': 'application/json', 'Access-Control-Allow-Origin': '*' }
    const requestBody = emailCertificate;
    console.log("requestBody", requestBody)
    return this.http.post(`${urlConfig.URLS.BASE_URL}${urlConfig.URLS.EMAIL_NOTIFICATION}`, requestBody, { 'headers': headers })
  }

  public searchCertificate(queryInput): Observable<any> {
    const requestBody = queryInput;
    const headers = { 'content-type': 'application/json', 'Access-Control-Allow-Origin': '*' }
    return this.http.post(`${urlConfig.URLS.
    SEARCH_CERT}`, requestBody, { headers } )
  }

  public displayCertificate(queryInput): Observable<any> {
    const requestBody = queryInput;
    const headers = { 'content-type': 'application/json', 'Access-Control-Allow-Origin': '*' }
    return this.http.post(`${urlConfig.URLS.DISPLAY_CERT}`, requestBody, { headers } )
  }
}
