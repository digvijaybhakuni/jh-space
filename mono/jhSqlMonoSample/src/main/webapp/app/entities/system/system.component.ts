import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs/Rx';
import { JhiEventManager, JhiParseLinks, JhiPaginationUtil, JhiAlertService } from 'ng-jhipster';

import { System } from './appSystem.model';
import { SystemService } from './appSystem.service';
import { ITEMS_PER_PAGE, Principal, ResponseWrapper } from '../../shared';
import { PaginationConfig } from '../../blocks/config/uib-pagination.config';

@Component({
    selector: 'jhi-appSystem',
    templateUrl: './appSystem.component.html'
})
export class SystemComponent implements OnInit, OnDestroy {
systems: System[];
    currentAccount: any;
    eventSubscriber: Subscription;

    constructor(
        private systemService: SystemService,
        private alertService: JhiAlertService,
        private eventManager: JhiEventManager,
        private principal: Principal
    ) {
    }

    loadAll() {
        this.systemService.query().subscribe(
            (res: ResponseWrapper) => {
                this.systems = res.json;
            },
            (res: ResponseWrapper) => this.onError(res.json)
        );
    }
    ngOnInit() {
        this.loadAll();
        this.principal.identity().then((account) => {
            this.currentAccount = account;
        });
        this.registerChangeInSystems();
    }

    ngOnDestroy() {
        this.eventManager.destroy(this.eventSubscriber);
    }

    trackId(index: number, item: System) {
        return item.id;
    }
    registerChangeInSystems() {
        this.eventSubscriber = this.eventManager.subscribe('systemListModification', (response) => this.loadAll());
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }
}
