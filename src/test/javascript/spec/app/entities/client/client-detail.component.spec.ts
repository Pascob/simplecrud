import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';
import { JhiDataUtils } from 'ng-jhipster';

import { SimpleCrudTestModule } from '../../../test.module';
import { ClientDetailComponent } from 'app/entities/client/client-detail.component';
import { Client } from 'app/shared/model/client.model';

describe('Component Tests', () => {
  describe('Client Management Detail Component', () => {
    let comp: ClientDetailComponent;
    let fixture: ComponentFixture<ClientDetailComponent>;
    let dataUtils: JhiDataUtils;
    const route = ({ data: of({ client: new Client(123) }) } as any) as ActivatedRoute;

    beforeEach(() => {
      TestBed.configureTestingModule({
        imports: [SimpleCrudTestModule],
        declarations: [ClientDetailComponent],
        providers: [{ provide: ActivatedRoute, useValue: route }],
      })
        .overrideTemplate(ClientDetailComponent, '')
        .compileComponents();
      fixture = TestBed.createComponent(ClientDetailComponent);
      comp = fixture.componentInstance;
      dataUtils = fixture.debugElement.injector.get(JhiDataUtils);
    });

    describe('OnInit', () => {
      it('Should load client on init', () => {
        // WHEN
        comp.ngOnInit();

        // THEN
        expect(comp.client).toEqual(jasmine.objectContaining({ id: 123 }));
      });
    });

    describe('byteSize', () => {
      it('Should call byteSize from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'byteSize');
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.byteSize(fakeBase64);

        // THEN
        expect(dataUtils.byteSize).toBeCalledWith(fakeBase64);
      });
    });

    describe('openFile', () => {
      it('Should call openFile from JhiDataUtils', () => {
        // GIVEN
        spyOn(dataUtils, 'openFile');
        const fakeContentType = 'fake content type';
        const fakeBase64 = 'fake base64';

        // WHEN
        comp.openFile(fakeContentType, fakeBase64);

        // THEN
        expect(dataUtils.openFile).toBeCalledWith(fakeContentType, fakeBase64);
      });
    });
  });
});
