import { Component, Input, OnInit, AfterViewInit, Output, EventEmitter, AfterViewChecked, OnChanges } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router'
import { DataService } from '../../services/data/data.service';
import urlConfig from '../../services/urlConfig.json';
import * as _ from 'lodash-es';
import { filter } from 'rxjs/operators';


@Component({
  selector: 'app-default-template',
  templateUrl: './default-template.component.html',
  styleUrls: ['./default-template.component.scss']
})
export class DefaultTemplateComponent implements OnInit, OnChanges {

  @Input() formFieldProperties: any;
  @Input() selectedTemplate: any;
  public formInputData: any = {};
  router: Router;
  activatedRoute: ActivatedRoute;
  userId: String;
  dataService: DataService;
  constructor(activatedRoute: ActivatedRoute, dataService: DataService) {
    this.activatedRoute = activatedRoute;
    this.dataService = dataService;
  }

  ngOnInit() {
    this.userId = this.activatedRoute.snapshot.params.id;
    if (this.userId) {
      this.getUserDetails();
    }
  }

  ngOnChanges() {
    console.log(this.formFieldProperties)
    this.formFieldProperties.forEach((field) => {
      this.formInputData[field.code] = field.value;
    });
  }
  getUserDetails() {
    const requestData = {
      data: {
        id: "open-saber.registry.read",
        request: {
          Person: {
            osid: this.userId
          },
          includeSignatures: true,
        }
      },
      url: urlConfig.URLS.READ,
    }
    this.dataService.post(requestData).subscribe(response => {
      this.formInputData = response.result.Person;
    },(err =>{
      console.log(err)
    }))
  }

}