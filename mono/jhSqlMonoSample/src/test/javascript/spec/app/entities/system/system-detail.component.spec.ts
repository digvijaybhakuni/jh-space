/* tslint:disable max-line-length */
import { ComponentFixture, TestBed, async, inject } from '@angular/core/testing';
import { OnInit } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs/Rx';
import { JhiDateUtils, JhiDataUtils, JhiEventManager } from 'ng-jhipster';
import { JhSqlMonoSampleTestModule } from '../../../test.module';
import { MockActivatedRoute } from '../../../helpers/mock-route.service';
import { SystemDetailComponent } from '../../../../../../main/webapp/app/entities/appSystem/appSystem-detail.component';
import { SystemService } from '../../../../../../main/webapp/app/entities/appSystem/appSystem.service';
import { System } from '../../../../../../main/webapp/app/entities/appSystem/appSystem.model';

describe('Component Tests', () => {

    describe('System Management Detail Component', () => {
        let comp: SystemDetailComponent;
        let fixture: ComponentFixture<SystemDetailComponent>;
        let service: SystemService;

        beforeEach(async(() => {
            TestBed.configureTestingModule({
                imports: [JhSqlMonoSampleTestModule],
                declarations: [SystemDetailComponent],
                providers: [
                    JhiDateUtils,
                    JhiDataUtils,
                    DatePipe,
                    {
                        provide: ActivatedRoute,
                        useValue: new MockActivatedRoute({id: 123})
                    },
                    SystemService,
                    JhiEventManager
                ]
            }).overrideTemplate(SystemDetailComponent, '')
            .compileComponents();
        }));

        beforeEach(() => {
            fixture = TestBed.createComponent(SystemDetailComponent);
            comp = fixture.componentInstance;
            service = fixture.debugElement.injector.get(SystemService);
        });

        describe('OnInit', () => {
            it('Should call load all on init', () => {
            // GIVEN

            spyOn(service, 'find').and.returnValue(Observable.of(new System(10)));

            // WHEN
            comp.ngOnInit();

            // THEN
            expect(service.find).toHaveBeenCalledWith(123);
            expect(comp.appSystem).toEqual(jasmine.objectContaining({id: 10}));
            });
        });
    });

});
