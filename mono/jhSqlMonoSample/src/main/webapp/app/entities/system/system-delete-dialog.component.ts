import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { JhiEventManager } from 'ng-jhipster';

import { System } from './appSystem.model';
import { SystemPopupService } from './appSystem-popup.service';
import { SystemService } from './appSystem.service';

@Component({
    selector: 'jhi-appSystem-delete-dialog',
    templateUrl: './appSystem-delete-dialog.component.html'
})
export class SystemDeleteDialogComponent {

    appSystem: System;

    constructor(
        private systemService: SystemService,
        public activeModal: NgbActiveModal,
        private eventManager: JhiEventManager
    ) {
    }

    clear() {
        this.activeModal.dismiss('cancel');
    }

    confirmDelete(id: number) {
        this.systemService.delete(id).subscribe((response) => {
            this.eventManager.broadcast({
                name: 'systemListModification',
                content: 'Deleted an appSystem'
            });
            this.activeModal.dismiss(true);
        });
    }
}

@Component({
    selector: 'jhi-appSystem-delete-popup',
    template: ''
})
export class SystemDeletePopupComponent implements OnInit, OnDestroy {

    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private systemPopupService: SystemPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            this.systemPopupService
                .open(SystemDeleteDialogComponent as Component, params['id']);
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
