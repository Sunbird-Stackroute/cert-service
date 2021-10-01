import { TestBed } from '@angular/core/testing';

import { CertificateService } from './certificate.service';

describe('CertificateService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CertificateService = TestBed.get(CertificateService);
    expect(service).toBeTruthy();
  });
});
