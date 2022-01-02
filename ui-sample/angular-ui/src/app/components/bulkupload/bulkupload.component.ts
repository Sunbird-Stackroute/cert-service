import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { MatSnackBar, MatSnackBarVerticalPosition } from '@angular/material/snack-bar';
import { CertificateService } from 'src/app/services/certificate/certificate.service';

@Component({
  selector: 'app-bulkupload',
  templateUrl: './bulkupload.component.html',
  styleUrls: ['./bulkupload.component.scss']
})
export class BulkuploadComponent implements OnInit {
  verticalPosition: MatSnackBarVerticalPosition = 'top';
  disabled = true;
  fileName = '';
  uploadProgress: number;
  uploadSub: Subscription;
  requiredFileType: String;
  data: number
  durationInSeconds = 3;
  service: CertificateService;
  constructor(
    private http: HttpClient,
    readonly snackBar: MatSnackBar,
    service: CertificateService
  ) {
    this.service = service;
  }

  ngOnInit() {
  }

  selectedfFile: File = null;

  onFileSelected(event) {
    if (event.target.files.length > 0) {
      this.fileName = event.target.files[0].name;
      this.disabled = false;
      this.selectedfFile = event.target.files[0];
    }
    else {
      this.disabled = true;
    }
  }

  onUpload() {
    if (this.selectedfFile != null) {
      this.disabled = true;
      this.fileName = null;
      const fd = new FormData();
      fd.append('csv', this.selectedfFile);
      this.service.bulkCertificate(fd)
        .subscribe(event => {
          if (event.type != 0) {
            this.open("File Uploaded Successfully", "File Uploaded!");
          }
        }, (error) => {
          this.open("Unexpected error", "Unable to Upload File")
          console.log(error)
        });
    }
  }

  open(action: string, message: string) {
    return this.snackBar.open(message, action, {
      verticalPosition: this.verticalPosition,
      duration: this.durationInSeconds * 1000,
    });
  }
}
