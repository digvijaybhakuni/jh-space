import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes, CanActivate } from '@angular/router';

import { UserRouteAccessService } from '../../shared';
import { JhiPaginationUtil } from 'ng-jhipster';

import { SystemComponent } from './appSystem.component';
import { SystemDetailComponent } from './appSystem-detail.component';
import { SystemPopupComponent } from './appSystem-dialog.component';
import { SystemDeletePopupComponent } from './appSystem-delete-dialog.component';

export const systemRoute: Routes = [
    {
        path: 'appSystem',
        component: SystemComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Systems'
        },
        canActivate: [UserRouteAccessService]
    }, {
        path: 'appSystem/:id',
        component: SystemDetailComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Systems'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const systemPopupRoute: Routes = [
    {
        path: 'appSystem-new',
        component: SystemPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Systems'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'appSystem/:id/edit',
        component: SystemPopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Systems'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    },
    {
        path: 'appSystem/:id/delete',
        component: SystemDeletePopupComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'Systems'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
