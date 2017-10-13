import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { Observable } from 'rxjs/Rx';
import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager, JhiAlertService } from 'ng-jhipster';

import { System } from './appSystem.model';
import { SystemPopupService } from './appSystem-popup.service';
import { SystemService } from './appSystem.service';

@Component({
    selector: 'jhi-appSystem-dialog',
    templateUrl: './appSystem-dialog.component.html'
})
export class SystemDialogComponent implements OnInit {

    appSystem: System;
    isSaving: boolean;

    constructor(
        public activeModal: NgbActiveModal,
        private alertService: JhiAlertService,
        private systemService: SystemService,
        private eventManager: JhiEventManager
    ) {
    }

    ngOnInit() {
        this.isSaving = false;
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.appSystem.id !== undefined) {
            this.subscribeToSaveResponse(
                this.systemService.update(this.appSystem));
        } else {
            this.subscribeToSaveResponse(
                this.systemService.create(this.appSystem));
        }
    }

    private subscribeToSaveResponse(result: Observable<System>) {
        result.subscribe((res: System) =>
            this.onSaveSuccess(res), (res: Response) => this.onSaveError());
    }

    private onSaveSuccess(result: System) {
        this.eventManager.broadcast({ name: 'systemListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError() {
        this.isSaving = false;
    }

    private onError(error: any) {
        this.alertService.error(error.message, null, null);
    }
}

@Component({
    selector: 'jhi-appSystem-popup',
    template: ''
})
export class SystemPopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private systemPopupService: SystemPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.systemPopupService
                    .open(SystemDialogComponent as Component, params['id']);
            } else {
                this.systemPopupService
                    .open(SystemDialogComponent as Component);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
