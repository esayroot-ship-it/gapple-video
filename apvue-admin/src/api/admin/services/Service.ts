/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { announcement } from '../models/announcement';
import type { banner } from '../models/banner.ts';
import type { category } from '../models/category';
import type { episode } from '../models/episode';
import type { hot } from '../models/hot';
import type { R } from '../models/R';
import type { series } from '../models/series';
import type { user } from '../models/user';
import type { video } from '../models/video';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class Service {
    /**
     * 更新视频信息
     * @param video
     * @returns R OK
     * @throws ApiError
     */
    public static update(
        video: video,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/video/update',
            body: video,
            mediaType: 'application/json',
        });
    }
    /**
     * 新增视频(含文件上传)
     * @param title
     * @param description
     * @param categoryId
     * @param status
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static add(
        title: string,
        description: string,
        categoryId: number,
        status: number,
        requestBody?: {
            videofile: Blob;
            coverfile: Blob;
        },
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/video/add',
            formData: {
                'title': title,
                'description': description,
                'categoryId': categoryId,
                'status': status,
                'videofile': requestBody?.videofile,
                'coverfile': requestBody?.coverfile,
            },
            mediaType: 'multipart/form-data',
        });
    }
    /**
     * 更新用户(含头像)
     * @param id
     * @param username
     * @param password
     * @param nickname
     * @param isadmin
     * @param status
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static updateUser(
        id: number,
        username?: string,
        password?: string,
        nickname?: string,
        isadmin?: number,
        status?: number,
        requestBody?: {
            file?: Blob;
        },
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/update',
            query: {
                'id': id,
                'username': username,
                'password': password,
                'nickname': nickname,
                'isadmin': isadmin,
                'status': status,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 保存用户(新增或更新)
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static save(
        requestBody: user,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/save',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 解封用户
     * @param username
     * @returns R OK
     * @throws ApiError
     */
    public static rebanUser(
        username: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/reban',
            query: {
                'username': username,
            },
        });
    }
    /**
     * 删除用户
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static deleteUser(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/delete',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 根据用户名删除用户
     * @param username
     * @returns R OK
     * @throws ApiError
     */
    public static deleteUserByUsername(
        username: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/deleteByName',
            query: {
                'username': username,
            },
        });
    }
    /**
     * 创建用户(含头像)
     * @param username
     * @param password
     * @param nickname
     * @param isadmin
     * @param status
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static createUser(
        username: string,
        password: string,
        nickname?: string,
        isadmin?: number,
        status: number = 1,
        requestBody?: {
            file?: Blob;
        },
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/create',
            query: {
                'username': username,
                'password': password,
                'nickname': nickname,
                'isadmin': isadmin,
                'status': status,
            },
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 封禁用户
     * @param username
     * @returns R OK
     * @throws ApiError
     */
    public static banUser(
        username: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/user/ban',
            query: {
                'username': username,
            },
        });
    }
    /**
     * 更新剧集
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static updateSeries(
        requestBody: series,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/series/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 删除剧集
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static deleteSeries(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/series/delete',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 创建剧集
     * @param title
     * @param description
     * @param status
     * @param ecount
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static createSeries(
        title: string,
        description: string,
        status: string,
        category_id: string,
        ecount: string,
        requestBody?: {
            file: Blob;
        },
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/series/create',
            formData: {
                'title': title,
                'description': description,
                'status': status,
                'category_id': category_id,
                'ecount': ecount,
                'file': requestBody?.file,
            },
            mediaType: 'multipart/form-data',
        });
    }
    /**
     * 删除热搜
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static deleteHot(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/hot/delete',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 添加热搜
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static addHot(
        requestBody: hot,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/hot/add',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 更新反馈状态
     * @param id
     * @param status
     * @returns R OK
     * @throws ApiError
     */
    public static updateStatus(
        id: number,
        status: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/feedback/updateStatus',
            query: {
                'id': id,
                'status': status,
            },
        });
    }
    /**
     * 删除分集
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static deleteEpisode(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/episode/delete',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 创建分集
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static createEpisode(
        requestBody: episode,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/episode/create',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 更新分类
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static updateCategory(
        requestBody: category,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/category/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 删除分类
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static deleteCategory(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/category/delete',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 创建分类
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static createCategory(
        requestBody: category,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/category/create',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 更新轮播图
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static updateBanner(
        requestBody: banner,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/banner/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 更新轮播图状态
     * @param id
     * @param status
     * @returns R OK
     * @throws ApiError
     */
    public static updateStatus1(
        id: number,
        status: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/banner/updateStatus',
            query: {
                'id': id,
                'status': status,
            },
        });
    }
    /**
     * 删除轮播图
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static deleteBanner(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/banner/delete',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 创建轮播图
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static createBanner(
        requestBody: banner,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/banner/create',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 更新公告
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static updateAnnouncement(
        requestBody: announcement,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/announcement/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 更新公告状态
     * @param id
     * @param status
     * @returns R OK
     * @throws ApiError
     */
    public static updateStatus2(
        id: number,
        status: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/announcement/updateStatus',
            query: {
                'id': id,
                'status': status,
            },
        });
    }
    /**
     * 删除公告
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static deleteAnnouncement(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/announcement/delete',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 创建公告
     * @param requestBody
     * @returns R OK
     * @throws ApiError
     */
    public static createAnnouncement(
        requestBody: announcement,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/announcement/create',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * 删除管理员
     * @param username
     * @returns R OK
     * @throws ApiError
     */
    public static delete(
        username: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/admin/delete',
            query: {
                'username': username,
            },
        });
    }
    /**
     * 创建管理员
     * @param username
     * @param password
     * @returns R OK
     * @throws ApiError
     */
    public static create(
        username: string,
        password: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/admin/create',
            query: {
                'username': username,
                'password': password,
            },
        });
    }
    /**
     * 根据ID获取视频
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static getById(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/video/{id}',
            path: {
                'id': id,
            },
        });
    }
    /**
     * 获取所有视频
     * @returns R OK
     * @throws ApiError
     */
    public static getAll(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/video/all',
        });
    }
    /**
     * 根据用户名获取用户
     * @param username
     * @returns R OK
     * @throws ApiError
     */
    public static getUserByUsername(
        username: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/getByUsername',
            query: {
                'username': username,
            },
        });
    }
    /**
     * 根据用户名获取用户(登录校验)
     * @param username
     * @param password
     * @returns R OK
     * @throws ApiError
     */
    public static login(
        username: string,
        password: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/getByName',
            query: {
                'username': username,
                'password': password,
            },
        });
    }
    /**
     * 获取所有用户
     * @returns R OK
     * @throws ApiError
     */
    public static getallUser(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/user/all',
        });
    }
    /**
     * 获取剧集详情
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static getSeriesById(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/series/get',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 获取所有剧集
     * @returns R OK
     * @throws ApiError
     */
    public static getAllSeries(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/series/all',
        });
    }
    /**
     * 获取所有热搜
     * @returns R OK
     * @throws ApiError
     */
    public static getAllHot(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/hot/all',
        });
    }
    /**
     * 获取所有反馈
     * @returns R OK
     * @throws ApiError
     */
    public static getAllFeedback(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/feedback/all',
        });
    }

    /**
     * 删除反馈
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static deleteFeedback(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/feedback/delete',
            query: {
                'id': id,
            },
        });
    }

    /**
     * 获取剧集的所有分集
     * @param seriesId
     * @returns R OK
     * @throws ApiError
     */
    public static getEpisodesBySeriesId(
        seriesId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/episode/list',
            query: {
                'seriesId': seriesId,
            },
        });
    }
    /**
     * 获取分集详情
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static getEpisodeById(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/episode/get',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 获取分类详情
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static getCategory(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/category/get',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 获取所有分类
     * @returns R OK
     * @throws ApiError
     */
    public static getAllCategory(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/category/all',
        });
    }
    /**
     * 获取轮播图详情
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static getBanner(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/banner/get',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 获取所有轮播图
     * @returns R OK
     * @throws ApiError
     */
    public static getAllBanner(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/banner/all',
        });
    }
    /**
     * 获取公告详情
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static getAnnouncement(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/announcement/get',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 获取所有公告
     * @returns R OK
     * @throws ApiError
     */
    public static getAllAnnouncement(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/announcement/all',
        });
    }
    /**
     * 管理员登录
     * @param username
     * @param password
     * @returns R OK
     * @throws ApiError
     */
    public static login1(
        username: string,
        password: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/admin/login',
            query: {
                'username': username,
                'password': password,
            },
        });
    }
    /**
     * 获取所有管理员
     * @returns R OK
     * @throws ApiError
     */
    public static all(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/admin/all',
        });
    }
    /**
     * 删除视频
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static delete1(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/video/delete/{id}',
            path: {
                'id': id,
            },
        });
    }
}
