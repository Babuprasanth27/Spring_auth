// import axios from "axios";

// const api = axios.create({
//     baseURL: "http://localhost:8081",
//     withCredentials: true //important for cookies
// });


// //Attach Access Tokens

// api.interceptors.request.use((config) => {
//     const token = localStorage.getItem("accessToken");

//     if(token){
//         config.headers.Authorization = `Bearer ${token}`;
//     }
//     return config;
// });

// //Auto refresh interceptor
// let isRefreshing = false;
// let refershSubscribers = [];
// //Notify all waiting requests
// const onRefreshed = (token) => {
//     refershSubscribers.forEach((callback) => callback(token));
//     refershSubscribers = [];
// };

// //Queue requests while refreshing
// const addSubscriber = (callback) => {
//     refershSubscribers.push(callback);

// };
// api.interceptors.response.use(
//     (response) => response,
//     async (error) =>{
//         const originalRequest = error.config;

//         //If access token expired
//         if(!error.response){
//             return Promise.reject({ message: "Network Error" });
//         }
//         if(error.response.status == 401){

//             if(originalRequest.url.includes("/auth/refresh")){
//                 localStorage.removeItem("accessToken");
//                 window.location.href = "/login";
//                 return Promise.reject(error);
//             }
//            if(isRefreshing){
//             return new Promise((resolve) => {
//                 addSubscriber((token) => {
//                     originalRequest.headers.Authorization = `Bearer ${token}`;
//                     resolve(api(originalRequest));
//                 });
//             });
//            }
//            originalRequest._retry = true;
//            isRefreshing = true;
//             try{
//                 console.log("Refreshing token...");

//                 const res = await api.post("/auth/refresh");
//                 const newAccessToken = res.data.accessToken;

//                 localStorage.setItem("accessToken", newAccessToken);

//                 //retry original request
//                 console.log("New token received");
//                 onRefreshed(newAccessToken);
//                 isRefreshing = false;

//                 originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
//                 return api(originalRequest);
//             }
//             catch(err){
//                 isRefreshing = false;
//                 const errorcode = err.response?.data?.error;

//                 if(errorcode === "REFRESH_TOKEN_EXPIRED"){
//                     alert("Session expired.Please login again.");
//                 }
//                 //logout
//                 localStorage.removeItem("accessToken");
//                 window.location.href = "/login";

//                 return Promise.reject(err);
//             }
//         }
//         return Promise.reject(error);
//     }
// );

// export default api;



import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8081",
  withCredentials: true // 🔥 VERY IMPORTANT for cookies
});

// Attach access token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");

  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }

  return config;
});


api.interceptors.response.use(
  (response) => response,
  async (error) => {

    const originalRequest = error.config;

    // If access token expired
    if (error.response?.status === 401 && !originalRequest._retry) {

      originalRequest._retry = true;

      try {
        const res = await api.post("/auth/refresh");

        const newAccessToken = res.data.accessToken;

        localStorage.setItem("accessToken", newAccessToken);

        // retry original request
        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return api(originalRequest);

      } catch (err) {

        const errorCode = err.response?.data?.error;

        if (errorCode === "REFRESH_TOKEN_EXPIRED") {
          alert("Session expired. Please login again.");
        }

        // logout
        localStorage.removeItem("accessToken");
        window.location.href = "/login";
      }
    }

    return Promise.reject(error);
  }
);

export default api;