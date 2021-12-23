import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BulkuploadComponent } from './bulkupload.component';

describe('BulkuploadComponent', () => {
  let component: BulkuploadComponent;
  let fixture: ComponentFixture<BulkuploadComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BulkuploadComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BulkuploadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
