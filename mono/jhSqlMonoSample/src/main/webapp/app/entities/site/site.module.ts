import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { JhSqlMonoSampleSharedModule } from '../../shared';
import {
    SiteService,
    SitePopupService,
    SiteComponent,
    SiteDetailComponent,
    SiteDialogComponent,
    SitePopupComponent,
    SiteDeletePopupComponent,
    SiteDeleteDialogComponent,
    siteRoute,
    sitePopupRoute,
} from './';

const ENTITY_STATES = [
    ...siteRoute,
    ...sitePopupRoute,
];

@NgModule({
    imports: [
        JhSqlMonoSampleSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        SiteComponent,
        SiteDetailComponent,
        SiteDialogComponent,
        SiteDeleteDialogComponent,
        SitePopupComponent,
        SiteDeletePopupComponent,
    ],
    entryComponents: [
        SiteComponent,
        SiteDialogComponent,
        SitePopupComponent,
        SiteDeleteDialogComponent,
        SiteDeletePopupComponent,
    ],
    providers: [
        SiteService,
        SitePopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JhSqlMonoSampleSiteModule {}
