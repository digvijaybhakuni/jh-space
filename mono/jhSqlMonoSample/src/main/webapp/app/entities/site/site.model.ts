import { BaseEntity } from './../../shared';

export class Site implements BaseEntity {
    constructor(
        public id?: number,
        public name?: string,
        public description?: string,
        public creationDateTime?: any,
        public systemId?: number,
    ) {
    }
}
