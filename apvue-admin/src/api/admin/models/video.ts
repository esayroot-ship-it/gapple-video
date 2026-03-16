/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 视频
 */
export type video = {
    id?: number;
    topicIds?: Array<number>;
    tagIds?: Array<number>;
    title?: string;
    description?: string;
    coverUrl?: string;
    playUrl?: string;
    categoryId?: number;
    status?: number;
    durationSeconds?: number;
    playCount?: number;
    avgRating?: number;
    ratingCount?: number;
    commentCount?: number;
    publishTime?: string;
    createdBy?: number;
    createdAt?: string;
    updatedAt?: string;
    videofile?: Blob;
    coverfile?: Blob;
};

