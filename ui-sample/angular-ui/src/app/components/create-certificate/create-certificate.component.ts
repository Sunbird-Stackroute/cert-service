import { Component, OnInit, ViewChild, ViewChildren, QueryList, OnChanges, ElementRef } from '@angular/core';
import { DataService } from '../../services/data/data.service';
import { ResourceService } from '../../services/resource/resource.service';
import { FormService } from '../../services/forms/form.service';
import { CertificateService } from '../../services/certificate/certificate.service';
import { DefaultTemplateComponent } from '../default-template/default-template.component';
import * as _ from 'lodash-es';
import urlConfig from '../../services/urlConfig.json';
import { CertReq, Store, Templates } from '../../services/interfaces/certificate';
import { Router } from '@angular/router';
import { IEmailCertificate } from 'src/app/services/email-certificate.model';
import * as $ from 'jquery';
import moment from 'moment';

@Component({
  selector: 'app-create-certificate',
  templateUrl: './create-certificate.component.html',
  styleUrls: ['./create-certificate.component.scss']
})
export class CreateCertificateComponent implements OnInit {
  @ViewChild('formData') formData: DefaultTemplateComponent;
  @ViewChildren('signatoryForm') signatoryFormData: QueryList<DefaultTemplateComponent>;
  @ViewChild('storageForm') storageFormData: DefaultTemplateComponent;
  dataService: DataService;
  formService: FormService;
  certificateService: CertificateService;
  resourceService: ResourceService;
  pdfUrl: string;
  public formFieldProperties: any;
  public selectedTemplate: any;
  public storeFieldProperties: any;
  public signatoryFieldProperties: any;
  public req: CertReq;
  router: Router
  signatory: Array<Number> = [1];
  signatoryCount = 1;
  preview: boolean = false;
  storageInfo: boolean = false;
  storageDetails = {};
  templateActive: boolean;
  htmlTemplateId: string;
  listOfTemplate: Array<Templates> = [];
  id: number = 1;
  showAllCertsKeys = [];
  showAllCertsValues = [];
  certSelected = [];
  certificateSelected;
  emailCertificateObject: IEmailCertificate;
  showAllCertToSendEmail = [];
  sendUserNotificationArray = [];
  getCertDataToSendEmail = [];
  svgUrl: string;
  constructor(dataService: DataService, formService: FormService, certificateService: CertificateService, resourceService: ResourceService, router: Router) {
    this.dataService = dataService;
    this.resourceService = resourceService;
    this.formService = formService;
    this.certificateService = certificateService;
    this.router = router;
  }

  ngOnInit() {
    this.templateActive = true;
    this.getTemplates();
    this.formService.getFormConfig("certificate").subscribe(res => {
      this.formFieldProperties = res.fields;
      console.log(this.formFieldProperties);
    });
    this.formService.getFormConfig("signatory").subscribe(res => {
      this.signatoryFieldProperties = res.fields;
    });
    this.formService.getFormConfig("store").subscribe(res => {
      this.storeFieldProperties = res.fields;
    });
  }


  getTemplates() {
    this.certificateService.getCertificateList().subscribe(res => {
      for (const key in res.result) {
        if (key.endsWith(`niitMeritCertificateHtml`)) {
          res.result[key] = `../../assets/certificates/niitMeritHtml.svg`;
        }
        if (key.endsWith(`niitParticipationHtml`)) {
          res.result[key] = `../../assets/certificates/niitParticipationHtml.svg`;
        }
      }
      this.showAllCertsKeys = Object.keys(res.result);
      this.showAllCertsValues = Object.values(res.result);
    });
  }
  createCertificate() {
    this.sendUserNotificationArray = [];
    const certificateData = this.generateData(_.pickBy(this.formData.formInputData));
    const requestData = {
      data: {
        params: {},
        request: {
          certificate: certificateData
        }
      },
      url:
        this.showAllCertsKeys[this.certificateSelected].endsWith('Html')
          ? urlConfig.URLS.GENERATE_CERT_HTML
          : urlConfig.URLS.GENERATE_CERT_SVG
    };
    this.dataService.post(requestData).subscribe(res => {
      console.log("RESPONSE", res)
      console.log('certificate generated successfully', res);
      // this.showAllCertsKeys[this.certificateSelected].endsWith('Svg')
      //   ? this.pdfUrl = ''
      //   : this.pdfUrl = res.result.response[0].pdfUrl
      this.showAllCertsKeys[this.certificateSelected].endsWith('Html')
        ? (this.pdfUrl = res.result.response[0].pdfUrl, this.svgUrl ='', window.open(this.pdfUrl)        )
        : (this.pdfUrl = '', this.svgUrl = res.result.response[0].svgUrl, window.open(this.svgUrl)        )
      // if (this.pdfUrl.startsWith("data")) {
      // } else if (this.pdfUrl.startsWith("http")) {
      //   window.open(this.pdfUrl);
      // } else {
      this.dowloadPdf();
      // }
      const emailnotifier = {
        pdfUrl: this.pdfUrl,
        courseName: certificateData.courseName,
        recipientEmail: certificateData.data[0].recipientEmail,
        recipientName: certificateData.data[0].recipientName,
        svgUrl: this.svgUrl
      };
      this.sendUserNotificationArray.push(emailnotifier);
      this.notifyUser(this.sendUserNotificationArray);

    });
  }
  /**
   *
   * @param request
   */
  generateData(request: any) {
    const signatoryList = [];
    const requestData = _.cloneDeep(request);
    var certificate = {
      data: [],
      issuer: {},
      signatoryList: [],
      htmlTemplateId: '',
      svgTemplateId: '',
      courseName: '',
      location: '',
      marks: '',
      name: '',
      description: '',
      certificateNum: '',
      studentRegNo: '',
      htmlTemplate: '',
      svgTemplate: '',
      recipientEmail: ''
    };
    const data = [{
      recipientName: requestData.recipientName,
      recipientEmail: requestData.recipientEmail,
      recipientPhone: requestData.recipientPhone,
    }];
    const issuer = {
      name: requestData.name,
      url: requestData.url,
    }
    this.signatoryFormData.forEach(issuer => {
      signatoryList.push(issuer.formInputData);
    });
    if (this.storageInfo) {
      certificate['storeConfig'] = this.getStorageDetail();
    }
    certificate.data = data;
    certificate.issuer = issuer;
    certificate.signatoryList = signatoryList;
    certificate.htmlTemplate = this.showAllCertsKeys[this.certificateSelected];
    certificate.svgTemplate = this.showAllCertsKeys[this.certificateSelected];
    certificate.htmlTemplateId = this.showAllCertsKeys[this.certificateSelected];
    certificate.svgTemplateId = this.showAllCertsKeys[this.certificateSelected];
    certificate.courseName = requestData.courseName;
    certificate.location = requestData.location;
    certificate.marks = requestData.marks;
    certificate.name = requestData.certificateName;
    certificate.description = requestData.certificateDescription;
    certificate.certificateNum = requestData.certificateNum;
    certificate.studentRegNo = requestData.studentRegNo;
    certificate.recipientEmail = requestData.recipientEmail;

    if (this.preview) {
      certificate['preview'] = "true";
      this.preview = false;
    }
    return certificate;
  }
  getStorageDetail() {
    this.storageDetails = _.pickBy(this.storageFormData.formInputData);
    const account = {
      account: '',
      key: '',
      path: '',
      containerName: ''
    }
    const storeConfig = {
      type: ''
    };
    const type = this.storageDetails['type'];
    storeConfig.type = type;
    account.account = this.storageDetails['account'];
    account.key = this.storageDetails['key'];
    account.containerName = this.storageDetails['containerName'];
    account.path = this.storageDetails['path'];
    storeConfig[type] = account;
    return storeConfig;
  }
  addSignatory() {
    this.signatory.push(this.signatoryCount++)
  }
  dowloadPdf() {
    const requestData = {
      data: {
        params: {},
        request: {
          pdfUrl: this.pdfUrl
        }
      },
      url: urlConfig.URLS.DOWLOAD_PDF
    }
    this.dataService.post(requestData).subscribe(res => {
      // window.open(res.result.signedUrl);
      // this.router.navigate(['']);
    });
  }
  removeSignatory() {
    if (this.signatory.length > 1) {
      this.signatory
    }
    if (this.signatoryCount > 1) {
      this.signatory.pop()
      this.signatoryCount--;
    }
  }
  previewCertificate() {
    this.preview = true;
    this.createCertificate();
  }



  notifyUser(emailCertificateObject) {
    console.log("emailCertificateObject componet", emailCertificateObject)
    this.certificateService.sendNotificationToUser(emailCertificateObject).subscribe(data => {
      console.log("DATA", data);
    })
  }

  applyFilter() {
    var queryData = {
      "request": {
        "query": {
          "bool": {
            "must": []
          }
        }
      }
    }
    queryData.request.query.bool.must.push({
      "range": {
        "data.issuedOn": {
          "gte": moment($("#startDate").val()).format("YYYY-MM-DD"),
          "lte": moment($("#endDate").val()).format("YYYY-MM-DD")
        }
      }
    });

    this.certificateService.searchCertificate(JSON.stringify(queryData)).subscribe(data => {
      this.showAllCertToSendEmail = [];
      let { result: { response: { content: resData } } } = data;
      console.log(data);
      resData.forEach(res => {
        let {
          _source: {
            pdfUrl: pdfUrl,
            svgUrl: svgUrl,
            data: {
              recipientEmail: recipientEmail,
              issuedOn: date,
              studentRegNo: regNo,
              certificateNum: certNo,
              recipient: { name: recipientName },
              badge: { issuer: { name: issuerName }, name: courseName }
            }
          }
        } = res;
        this.showAllCertToSendEmail.push({
          date,
          certNo,
          regNo,
          issuerName,
          recipientEmail,
          pdfUrl,
          svgUrl,
          recipientName,
          courseName
        });
      });
      console.log(this.showAllCertToSendEmail);
    })
  }

  clearApplyFilter() {
    $("#startDate").val('');
    $("#endDate").val('')
    $("#searchBy").val('');
    this.showAllCert();
  }

  selectedSvgCert(event) {
    event.stopPropagation();
    if (this.certSelected.length > 0) {
      if (this.certSelected[0] !== event.target.id) {
        if (!event.target.classList.contains('svg__icon--active')) {
          document.getElementById(this.certSelected[0]).classList.remove('svg__icon--active');
          event.target.classList.add('svg__icon--active');
          this.certSelected.pop();
          this.certSelected.push(event.target.id);
          this.certificateSelected = this.showAllCertsValues.indexOf(event.target.id);
        }
      } else {
        event.target.classList.remove('svg__icon--active');
        this.certSelected.pop();
        this.certificateSelected = '';
      }
    } else {
      if (!event.target.classList.contains('svg__icon--active')) {
        event.target.classList.add('svg__icon--active');
        this.certSelected.push(event.target.id);
        this.certificateSelected = this.showAllCertsValues.indexOf(event.target.id);
      }
    }
    console.log(this.certSelected);
    console.log(this.showAllCertsKeys[this.certificateSelected]);
    this.selectedTemplate = this.showAllCertsKeys[this.certificateSelected].includes('niitMerit');
    console.log(this.selectedTemplate);
  }

  showAllCert() {
    this.showAllCertToSendEmail = [];
    this.certificateService.searchCertificate({ "request": { "query": { "bool": {} } } }).subscribe(data => {
      let { result: { response: { content: resData } } } = data;
      console.log(data);
      resData.forEach(res => {
        let {
          _source: {
            pdfUrl: pdfUrl,
            svgUrl: svgUrl,
            data: {
              recipientEmail: recipientEmail,
              issuedOn: date,
              studentRegNo: regNo,
              certificateNum: certNo,
              recipient: { name: recipientName },
              badge: { issuer: { name: issuerName }, name: courseName }
            }
          }
        } = res;
        this.showAllCertToSendEmail.push({
          date,
          certNo,
          regNo,
          issuerName,
          recipientEmail,
          pdfUrl,
          svgUrl,
          recipientName,
          courseName
        });
      });
      console.log(this.showAllCertToSendEmail);
    })
  }

  sendMultipleNotifications() {
    this.sendUserNotificationArray = [];
    let getParentNode = document.getElementById("certTable"),
      checkPdfUrl,
      checkSvgUrl,
      findCheckBoxes = getParentNode.getElementsByTagName("input");

      // this.showAllCertsKeys[this.certificateSelected].endsWith('Svg')
      //   ? this.pdfUrl = ''
      //   : this.pdfUrl = res.result.response[0].pdfUrl

    for (var i = 0; i < findCheckBoxes.length; i++) {
      if (findCheckBoxes[i].checked) {
        $(findCheckBoxes[i]).data("url") === undefined || $(findCheckBoxes[i]).data("url") === null
          ? checkPdfUrl = ""
          : checkPdfUrl = $(findCheckBoxes[i]).data("url");

        $(findCheckBoxes[i]).data("svgurl") === undefined || $(findCheckBoxes[i]).data("svgurl") === null
          ? checkSvgUrl = ""
          : checkSvgUrl = $(findCheckBoxes[i]).data("svgurl");

        console.log("checkPdfUrl", checkPdfUrl);
        console.log($(findCheckBoxes[i]).data("course"));
        console.log($(findCheckBoxes[i]).data("email"));
        console.log($(findCheckBoxes[i]).data("name"));
        console.log($(findCheckBoxes[i]).data("svgUrl"));
        this.sendUserNotificationArray.push({
          pdfUrl: checkPdfUrl,
          courseName: $(findCheckBoxes[i]).data("course"),
          recipientEmail: $(findCheckBoxes[i]).data("email"),
          recipientName: $(findCheckBoxes[i]).data("name"),
          svgUrl: checkSvgUrl
        });
      }
    }
    this.notifyUser(this.sendUserNotificationArray.filter((item, index) => this.sendUserNotificationArray.indexOf(item) === index));
  }

  searchByFilter() {
    const searchData = $("#searchBy").val();
    searchData.toLowerCase();
    let queryData = {
      "request": {
        "query": {
          "bool": {
            "must": []
          }
        }
      }
    }
    if (searchData.startsWith("R")) {
      queryData.request.query.bool.must.push(
        { "match": { "data.studentRegNo": searchData } },
      );
    } else if (searchData.startsWith("C")) {
      queryData.request.query.bool.must.push(
        { "match": { "data.certificateNum": searchData } }
      );
    } else {
      queryData.request.query.bool.must.push(
        { "match": { "data.badge.issuer.name": searchData } },

      );
    }
    console.log(queryData);
    this.certificateService.searchCertificate(JSON.stringify(queryData)).subscribe(data => {
      this.showAllCertToSendEmail = [];
      let { result: { response: { content: resData } } } = data;
      console.log(data);
      resData.forEach(res => {
        let {
          _source: {
            pdfUrl: pdfUrl,
            data: {
              recipientEmail: recipientEmail,
              issuedOn: date,
              studentRegNo: regNo,
              certificateNum: certNo,
              recipient: { name: recipientName },
              badge: { issuer: { name: issuerName }, name: courseName }
            }
          }
        } = res;
        this.showAllCertToSendEmail.push({
          date,
          certNo,
          regNo,
          issuerName,
          recipientEmail,
          pdfUrl,
          recipientName,
          courseName
        });
      });
      console.log(this.showAllCertToSendEmail);
    });
  }
}