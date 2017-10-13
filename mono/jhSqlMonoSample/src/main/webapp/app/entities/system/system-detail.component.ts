import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager } from 'ng-jhipster';

import { System } from './appSystem.model';
import { SystemService } from './appSystem.service';

@Component({
    selector: 'jhi-appSystem-detail',
    templateUrl: './appSystem-detail.component.html'
})
export class SystemDetailComponent implements OnInit, OnDestroy {

    appSystem: System;
    private subscription: Subscription;
    private eventSubscriber: Subscription;

    constructor(
        private eventManager: JhiEventManager,
        private systemService: SystemService,
        private route: ActivatedRoute
    ) {
    }

    ngOnInit() {
        this.subscription = this.route.params.subscribe((params) => {
            this.load(params['id']);
        });
        this.registerChangeInSystems();
    }

    load(id) {
        this.systemService.find(id).subscribe((appSystem) => {
            this.appSystem = appSystem;
        });
    }
    previousState() {
        window.history.back();
    }

    ngOnDestroy() {
        this.subscription.unsubscribe();
        this.eventManager.destroy(this.eventSubscriber);
    }

    registerChangeInSystems() {
        this.eventSubscriber = this.eventManager.subscribe(
            'systemListModification',
            (response) => this.load(this.appSystem.id)
        );
    }
}
