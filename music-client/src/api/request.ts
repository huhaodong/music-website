import axios, { AxiosInstance, AxiosError, AxiosRequestConfig, AxiosResponse } from 'axios';
import { getToken, getRefreshToken, setAuthData, clearAuthData } from '@/utils/auth';
import router from '@/router';

function createRequest(): any {
  const service: AxiosInstance = axios.create({
    baseURL: 'http://localhost:8888',
    timeout: 30000,
  });

  // 请求拦截器
  service.interceptors.request.use(
    (config: AxiosRequestConfig) => {
      const token = getToken();
      if (token && config.headers) {
        config.headers['Authorization'] = `Bearer ${token}`;
      }
      return config;
    },
    (error: AxiosError) => {
      console.error('Request error:', error);
      return Promise.reject(error);
    }
  );

  // 响应拦截器
  service.interceptors.response.use(
    async (response: AxiosResponse) => {
      return response.data;
    },
    async (error: AxiosError) => {
      const originalRequest = error.config as AxiosRequestConfig & { _retry?: boolean };
      const hasToken = !!getToken();

      if (error.response?.status === 401 && hasToken && !originalRequest._retry) {
        originalRequest._retry = true;

        try {
          const refreshToken = getRefreshToken();
          if (refreshToken) {
            const response = await axios.post('http://localhost:8888/auth/refresh', `"${refreshToken}"`, {
              headers: { 'Content-Type': 'application/json' }
            });

            const { accessToken, refreshToken: newRefreshToken } = response.data;
            setAuthData(accessToken, newRefreshToken);

            if (originalRequest.headers) {
              originalRequest.headers['Authorization'] = `Bearer ${accessToken}`;
            }
            return service(originalRequest);
          }
        } catch (refreshError) {
          clearAuthData();
          router.replace({ path: '/login' });
          return Promise.reject(refreshError);
        }
      }

      if (error.response?.status === 401 && hasToken) {
        clearAuthData();
        router.replace({ path: '/login' });
      }

      return Promise.reject(error);
    }
  );

  return service;
}

export default createRequest();