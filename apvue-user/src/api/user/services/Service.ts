/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { R } from '../models/R';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class Service {
    /**
     * 取消订阅
     * @param userId
     * @param seriesId
     * @returns R OK
     * @throws ApiError
     */
    public static deleteSubscribe(
        userId: number,
        seriesId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/subsrcibe/delete',
            query: {
                'userId': userId,
                'seriesId': seriesId,
            },
        });
    }
    /**
     * 订阅剧集
     * @param userId
     * @param seriesId
     * @returns R OK
     * @throws ApiError
     */
    public static addSubscribe(
        userId: number,
        seriesId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/subsrcibe/add',
            query: {
                'userId': userId,
                'seriesId': seriesId,
            },
        });
    }
    /**
     * 设置昵称
     * @param nickname
     * @param username
     * @returns R OK
     * @throws ApiError
     */
    public static setNickname(
        nickname: string,
        username: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/setNickname',
            query: {
                'nickname': nickname,
                'username': username,
            },
        });
    }

    /**
     * 设置头像
     * @param userid
     * @param file
     * @returns R OK
     * @throws ApiError
     */
    public static setuserimg(
        id: number,
        file: Blob,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/setuserimg',
            formData: {
                'id': id,
                'file': file,
            },
            mediaType: 'multipart/form-data',
        });
    }
    /**
     * 用户注册
     * @param username
     * @param password
     * @returns R OK
     * @throws ApiError
     */
    public static register(
        username: string,
        password: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/register',
            query: {
                'username': username,
                'password': password,
            },
        });
    }

    /**获取用户信息
     * @param userId
     * @returns R OK
     * @throws ApiError
     */
    public static getById (
        userId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/getById',
            query: {
                'userId': userId,
            },
        });
    }
    /**
     * 提交评分
     * @param userId
     * @param videoId
     * @param score
     * @returns R OK
     * @throws ApiError
     */
    public static addRating(
        userId: number,
        videoId: number,
        score: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/rating/add',
            query: {
                'userId': userId,
                'videoId': videoId,
                'score': score,
            },
        });
    }
    /**
     * 用户登录
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
            method: 'POST',
            url: '/u/login',
            query: {
                'username': username,
                'password': password,
            },
        });
    }
    /**
     * 添加观看历史
     * @param userId
     * @param videoId
     * @returns R OK
     * @throws ApiError
     */
    public static addHistory(
        userId: number,
        videoId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/history/add',
            query: {
                'userId': userId,
                'videoId': videoId,
            },
        });
    }
    /**
     * 提交反馈
     * @param userId
     * @param content
     * @param type
     * @param status
     * @returns R OK
     * @throws ApiError
     */
    public static addFeedback(
        userId: number,
        content: string,
        type: string,
        status: number = 0,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/feedback/add',
            query: {
                'userId': userId,
                'content': content,
                'type': type,
                'status': status,
            },
        });
    }
    /**
     * 删除收藏
     * @param favoriteId
     * @returns R OK
     * @throws ApiError
     */
    public static deleteFavorite(
        favoriteId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/favorite/delete',
            query: {
                'favoriteId': favoriteId,
            },
        });
    }
    /**
     * 添加收藏
     * @param userId
     * @param videoId
     * @returns R OK
     * @throws ApiError
     */
    public static addFavorite(
        userId: number,
        videoId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/favorite/add',
            query: {
                'userId': userId,
                'videoId': videoId,
            },
        });
    }
    /**
     * 给评论点赞
     * @param userId
     * @param commentId
     * @returns R OK
     * @throws ApiError
     */
    public static likeComment(
        userId: number,
        commentId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/commentlike/add',
            query: {
                'userId': userId,
                'commentId': commentId,
            },
        });
    }
    /**
     * 发表评论
     * @param userId
     * @param videoId
     * @param content
     * @returns R OK
     * @throws ApiError
     */
    public static addComment(
        userId: number,
        videoId: number,
        content: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/u/comment/add',
            query: {
                'userId': userId,
                'videoId': videoId,
                'content': content,
            },
        });
    }
    /**
     * 搜索视频
     * @param title
     * @returns R OK
     * @throws ApiError
     */
    public static getVideoByTitle(
        title: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/video/search',
            query: {
                'title': title,
            },
        });
    }
    /**
     * 根据剧集获取视频
     * @param seriesId
     * @returns R OK
     * @throws ApiError
     */
    public static getVideoBySeries(
        seriesId: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/video/getBySeries',
            query: {
                'seriesId': seriesId,
            },
        });
    }
    /**
     * 获取视频详情
     * @param id
     * @returns R OK
     * @throws ApiError
     */
    public static getVideoById(
        id: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/video/getById',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 根据分类获取视频
     * @param categoryId
     * @returns R OK
     * @throws ApiError
     */
    public static getVideosByCategory(
        categoryId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/video/getByCategory',
            query: {
                'categoryId': categoryId,
            },
        });
    }
    /**
     * 获取标签和话题
     * @returns R OK
     * @throws ApiError
     */
    public static gettagAndTopic(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/tag/list',
        });
    }
    /**
     * 获取订阅列表
     * @param userId
     * @returns R OK
     * @throws ApiError
     */
    public static getSubscribes(
        userId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/subsrcibe/list',
            query: {
                'userId': userId,
            },
        });
    }
    /**
     * 判断是否订阅
     * @param userId
     * @param seriesId
     * @returns R OK
     * @throws ApiError
     */
    public static getIfSubscribed(
        userId: number,
        seriesId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/subsrcibe/isSubscribed',
            query: {
                'userId': userId,
                'seriesId': seriesId,
            },
        });
    }
    /**
     * 搜索剧集
     * @param title
     * @returns R OK
     * @throws ApiError
     */
    public static getSeriesByTitle(
        title: string,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/series/search',
            query: {
                'title': title,
            },
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
            url: '/u/series/getById',
            query: {
                'id': id,
            },
        });
    }
    /**
     * 根据分类获取剧集
     * @param categoryId
     * @returns R OK
     * @throws ApiError
     */
    public static getSeriesByCategory(
        categoryId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/series/getByCategory',
            query: {
                'categoryId': categoryId,
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
            url: '/u/series/all',
        });
    }
    /**
     * 获取用户对视频的评分
     * @param userId
     * @param videoId
     * @returns R OK
     * @throws ApiError
     */
    public static getUserRating(
        userId: number,
        videoId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/rating/user',
            query: {
                'userId': userId,
                'videoId': videoId,
            },
        });
    }
    /**
     * 获取视频所有评分
     * @param videoId
     * @returns R OK
     * @throws ApiError
     */
    public static getRatingByVideoId(
        videoId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/rating/list',
            query: {
                'videoId': videoId,
            },
        });
    }
    /**
     * 获取热搜榜单
     * @returns R OK
     * @throws ApiError
     */
    public static getHotList(): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/hot/getHotList',
        });
    }
    /**
     * 获取用户观看历史
     * @param userId
     * @returns R OK
     * @throws ApiError
     */
    public static getHistorysByUserId(
        userId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/history/list',
            query: {
                'userId': userId,
            },
        });
    }
    /**
     * 获取用户反馈列表
     * @param userId
     * @returns R OK
     * @throws ApiError
     */
    public static getFeedbacksByUserId(
        userId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/feedback/list',
            query: {
                'userId': userId,
            },
        });
    }
    /**
     * 获取用户收藏列表
     * @param userId
     * @returns R OK
     * @throws ApiError
     */
    public static getFavoritesByUserId(
        userId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/favorite/list',
            query: {
                'userId': userId,
            },
        });
    }
    /**
     * 根据剧集获取分集列表
     * @param seriesId
     * @returns R OK
     * @throws ApiError
     */
    public static getEpisodesBySeriesId(
        seriesId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/episode/list',
            query: {
                'seriesId': seriesId,
            },
        });
    }
    /**
     * 根据视频获取剧集
     * @param videoId
     * @returns R OK
     * @throws ApiError
     */
    public static getSeriesByVideoId(
        videoId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/episode/getSeries',
            query: {
                'videoId': videoId,
            },
        });
    }
    /**
     * 获取评论点赞数
     * @param commentId
     * @returns R OK
     * @throws ApiError
     */
    public static getLikeCount(
        commentId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/commentlike/count',
            query: {
                'commentId': commentId,
            },
        });
    }
    /**
     * 获取视频评论
     * @param videoId
     * @returns R OK
     * @throws ApiError
     */
    public static getComments(
        videoId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/u/comment/list',
            query: {
                'videoId': videoId,
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
            url: '/u/category/all',
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
            url: '/u/banner/all',
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
            url: '/u/announcement/all',
        });
    }
    /**
     * 删除评分
     * @param userId
     * @param videoId
     * @returns R OK
     * @throws ApiError
     */
    public static deleteRating(
        userId: number,
        videoId: number,
    ): CancelablePromise<R> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/u/rating/delete',
            query: {
                'userId': userId,
                'videoId': videoId,
            },
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
            method: 'DELETE',
            url: '/u/feedback/delete',
            query: {
                'id': id,
            },
        });
    }
}
