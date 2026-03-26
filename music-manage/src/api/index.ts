import {deletes, get, getBaseURL, post, put} from './request'

const HttpManager = {
    // 获取图片信息
    attachImageUrl: (url) => `${getBaseURL()}/${url}`,
    // =======================> 认证 API
    // JWT 登录
    login: ({ username, password, userType }) => post(`auth/login`, { username, password, userType }),
    // JWT 登出
    logout: () => post(`auth/logout`),

    // =======================> 管理员 API 完成
    // 返回所有用户
    getAllUser: () => get(`user`),
    // 返回指定ID的用户
    getUserOfId: (id) => get(`user/detail?id=${id}`),
    // 删除用户
    deleteUser: (id) => get(`user/delete?id=${id}`),
    // =======================> 收藏列表 API 完成
    // 返回的指定用户ID收藏列表
    getCollectionOfUser: (userId) => get(`collection/detail?userId=${userId}`),
    // 删除收藏的歌曲
    deleteCollection: (userId, songId) => deletes(`collection/delete?userId=${userId}&&songId=${songId}`),

    // =======================> 评论列表 API 完成
    // 获得指定歌曲ID的评论列表
    getCommentOfSongId: (songId) => get(`comment/song/detail?songId=${songId}`),
    // 获得指定歌单ID的评论列表
    getCommentOfSongListId: (songListId) => get(`comment/songList/detail?songListId=${songListId}`),
    // 删除评论
    deleteComment: (id) => get(`comment/delete?id=${id}`),

    // =======================> 歌手 API 完成
    // 返回所有歌手
    getAllSinger: () => get(`singer`),
    // 添加歌手
    setSinger: ({name, sex, birth, location, introduction}) => post(`singer/add`, {
        name,
        sex,
        birth,
        location,
        introduction
    }),
    // 更新歌手信息
    updateSingerMsg: ({id, name, sex, birth, location, introduction}) => post(`singer/update`, {
        id,
        name,
        sex,
        birth,
        location,
        introduction
    }),
    // 删除歌手
    deleteSinger: (id) => deletes(`singer/delete?id=${id}`),

    // =======================> 歌曲 API  完成
    // 返回所有歌曲
    getAllSong: () => get(`song`),
    // 返回指定歌手ID的歌曲
    getSongOfSingerId: (id) => get(`song/singer/detail?singerId=${id}`),
    // 返回的指定用户ID收藏列表
    getSongOfId: (id) => get(`song/detail?id=${id}`),
    // 返回指定歌手名的歌曲
    getSongOfSingerName: (id) => get(`song/singerName/detail?name=${id}`),
    // 更新歌曲信息
    updateSongMsg: ({id, singerId, name, introduction, lyric}) => post(`song/update`, {
        id,
        singerId,
        name,
        introduction,
        lyric
    }),
    updateSongUrl: (id) => `${getBaseURL()}/song/url/update?id=${id}`,
    updateSongImg: (id) => `${getBaseURL()}/song/img/update?id=${id}`,
    updateSongLrc: (id) => `${getBaseURL()}/song/lrc/update?id=${id}`,
    // 删除歌曲
    deleteSong: (id) => deletes(`song/delete?id=${id}`),

    // =======================> 歌单 API 完成
    // 添加歌单t
    setSongList: ({title, introduction, style}) => post(`songList/add`, {title, introduction, style}),
    // 获取全部歌单
    getSongList: () => get(`songList`),
    // 更新歌单信息
    updateSongListMsg: ({id, title, introduction, style}) => post(`songList/update`, {id, title, introduction, style}),
    // 删除歌单
    deleteSongList: (id) => get(`songList/delete?id=${id}`),

    // =======================> 歌单歌曲 API 完成
    // 给歌单添加歌曲
    setListSong: ({songId,songListId}) => post(`listSong/add`, {songId,songListId}),
    // 返回歌单里指定歌单ID的歌曲
    getListSongOfSongId: (songListId) => get(`listSong/detail?songListId=${songListId}`),
    // 删除歌单里的歌曲
    deleteListSong: (songId) => get(`listSong/delete?songId=${songId}`)

}

const SystemManager = {
    attachImageUrl: (url: string) => `${getBaseURL()}/${url}`,

    // =======================> 用户管理 API
    getAllUsers: () => get('system/user/list'),
    getUserById: (id: number) => get(`system/user/detail?id=${id}`),
    deleteUser: (id: number) => get(`system/user/delete?id=${id}`),
    addUser: (data: any) => post('system/user/add', data),
    updateUser: (data: any) => post('system/user/update', data),

    // =======================> 角色管理 API
    getAllRoles: () => get('system/role/list'),
    getRoleById: (id: number) => get(`system/role/detail?id=${id}`),
    addRole: (data: any) => post('system/role/add', data),
    updateRole: (data: any) => put(`system/role/update`, data),
    deleteRole: (id: number) => deletes(`system/role/delete?id=${id}`),

    // =======================> 权限管理 API
    getAllPermissions: () => get('system/permission/list'),
    getPermissionById: (id: number) => get(`system/permission/detail?id=${id}`),
    addPermission: (data: any) => post('system/permission/add', data),
    updatePermission: (data: any) => put(`system/permission/update`, data),
    deletePermission: (id: number) => deletes(`system/permission/delete?id=${id}`),
    getPermissionTree: () => get('system/permission/tree'),

    // =======================> 组织管理 API
    getAllOrganizations: () => get('system/organization/list'),
    getOrganizationById: (id: number) => get(`system/organization/detail?id=${id}`),
    addOrganization: (data: any) => post('system/organization/add', data),
    updateOrganization: (data: any) => put(`system/organization/update`, data),
    deleteOrganization: (id: number) => deletes(`system/organization/delete?id=${id}`),
    getOrganizationTree: () => get('system/organization/tree'),

    // =======================> 用户角色管理 API
    getUserRoles: (userId: number) => get(`system/userRole/list?userId=${userId}&userType=admin`),
    assignRoles: (data: any) => post('system/userRole/assign', data),
    removeRoleFromUser: (userId: number, roleId: number) => deletes(`system/userRole/remove?userId=${userId}&roleId=${roleId}`),

    // =======================> 角色权限管理 API
    getRolePermissions: (roleId: number) => get(`system/permission/listByRole?roleId=${roleId}`),
    assignPermissions: (data: any) => post('system/permission/assign', data),
}

export {HttpManager, SystemManager}