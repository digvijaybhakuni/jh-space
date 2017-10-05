import { BaseEntity } from './../../shared';

export class Post implements BaseEntity {
    constructor(
        public id?: number,
        public title?: string,
        public content?: any,
        public tags?: any,
    ) {
    }
}
