import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { Site } from './site.model';
import { SitePopupService } from './site-popup.service';
import { SiteService } from './site.service';
import { System, SystemService } from '../appSystem';
import { ResponseWrapper } from '../../shared';

@Component({
    selector: 'jhi-site-dialog',
    templateUrl: './site-dialog.component.html'
})
export class SiteDialogComponent implements OnInit {

    site: Site;
    isSaving: boolean;

    systems: System[];

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private siteService: SiteService,
        private systemService: SystemService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
        this.systemService.query()
            .subscribe((res: ResponseWrapper) => { this.systems = res.json; }, (res: ResponseWrapper) => this.onError(res.json));
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.site.id !== undefined) {
            this.subscribeToSaveResponse(
                this.siteService.update(this.site));
        } else {
            this.subscribeToSaveResponse(
                this.siteService.create(this.site));
        }
    }

    private subscribeToSaveResponse(result: Observable<Site>) {
        result.subscribe((res: Site) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError());
    }

    private onSaveSuccess(result: Site) {
        this.eventManager.broadcast({ name: 'siteListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.alertService.error(error.message, null, null);
    }

    trackSystemById(index: number, item: System) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-site-popup',
    template: ''
})
export class SitePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private sitePopupService: SitePopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.sitePopupService
                    .open(SiteDialogComponent as Component, params['id']);
            } else {
                this.sitePopupService
                    .open(SiteDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
