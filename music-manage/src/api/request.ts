import axios from 'axios'
import router from '../router'
import { getToken } from '../utils'

const BASE_URL = process.env.NODE_HOST

axios.defaults.timeout = 5000 // 超时时间设置
axios.defaults.withCredentials = true // true允许跨域
axios.defaults.baseURL = BASE_URL
// Content-Type 响应头 - JSON格式
axios.defaults.headers.post['Content-Type'] = 'application/json;charset=UTF-8'
axios.defaults.headers.get['Content-Type'] = 'application/json;charset=UTF-8'

// 请求拦截器 - 自动添加 JWT token
axios.interceptors.request.use(
  config => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

// 响应拦截器
// 【注意】此拦截器已将 axios 原始 response 中的 .data 取出，返回的是后端 R 对象
// 即：{ code, success, message, type, data }
// 因此下方的封装函数中直接 resolve(response)，不再做二次 .data 提取
axios.interceptors.response.use(
  response => {
    if (response.status === 200) {
      return Promise.resolve(response.data)  // → 返回后端 R 对象
    } else {
      return Promise.reject(response.data)
    }
  },
  // 服务器状态码不是2开头的的情况
  error => {
    if (error.response && error.response.status) {
      switch (error.response.status) {
        // 401: 未登录
        case 401:
          router.replace({
            path: "/",
            query: {},
          });
          break;
        case 403:
          setTimeout(() => {
            router.replace({
              path: "/",
              query: {},
            });
          }, 1000);
          break;

        // 404请求不存在
        case 404:
          break;
      }
      return Promise.reject(error.response.data);
    } else if (error.request) {
      return Promise.reject(error.message || '网络错误');
    } else {
      return Promise.reject(error);
    }
  }
)

export function getBaseURL() {
  return BASE_URL;
}

/**
 * 封装get方法
 * 返回完整 R 对象：{ code, success, message, type, data }
 */
export function get(url, params?: object) {
  return new Promise((resolve, reject) => {
    axios.get(url, params).then(
      response => resolve(response),  // response 已是 R 对象（由拦截器解包）
      error => reject(error)
    )
  });
}

/**
 * 封装post请求
 * 返回完整 R 对象：{ code, success, message, type, data }
 */
export function post(url, data = {}, config = {}) {
  return new Promise((resolve, reject) => {
    axios.post(url, data, config).then(
      response => resolve(response),  // response 已是 R 对象（由拦截器解包）
      error => reject(error)
    );
  });
}

/**
 * 封装delete请求
 * 返回完整 R 对象：{ code, success, message, type, data }
 */
export function deletes(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.delete(url, data).then(
      response => resolve(response),
      error => reject(error)
    );
  });
}

/**
 * 封装put请求
 * 返回完整 R 对象：{ code, success, message, type, data }
 */
export function put(url, data = {}) {
  return new Promise((resolve, reject) => {
    axios.put(url, data).then(
      response => resolve(response),
      error => reject(error)
    );
  });
}
