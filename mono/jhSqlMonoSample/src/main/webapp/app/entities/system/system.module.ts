import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { RouterModule } from '@angular/router';

import { JhSqlMonoSampleSharedModule } from '../../shared';
import {
    SystemService,
    SystemPopupService,
    SystemComponent,
    SystemDetailComponent,
    SystemDialogComponent,
    SystemPopupComponent,
    SystemDeletePopupComponent,
    SystemDeleteDialogComponent,
    systemRoute,
    systemPopupRoute,
} from './';

const ENTITY_STATES = [
    ...systemRoute,
    ...systemPopupRoute,
];

@NgModule({
    imports: [
        JhSqlMonoSampleSharedModule,
        RouterModule.forRoot(ENTITY_STATES, { useHash: true })
    ],
    declarations: [
        SystemComponent,
        SystemDetailComponent,
        SystemDialogComponent,
        SystemDeleteDialogComponent,
        SystemPopupComponent,
        SystemDeletePopupComponent,
    ],
    entryComponents: [
        SystemComponent,
        SystemDialogComponent,
        SystemPopupComponent,
        SystemDeleteDialogComponent,
        SystemDeletePopupComponent,
    ],
    providers: [
        SystemService,
        SystemPopupService,
    ],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class JhSqlMonoSampleSystemModule {}
