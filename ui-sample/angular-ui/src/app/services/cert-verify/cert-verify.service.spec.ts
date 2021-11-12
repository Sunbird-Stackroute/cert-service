import { TestBed } from '@angular/core/testing';

import { CertVerifyService } from './cert-verify.service';

describe('CertVerifyService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: CertVerifyService = TestBed.get(CertVerifyService);
    expect(service).toBeTruthy();
  });
});