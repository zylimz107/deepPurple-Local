import axios from "axios";

const API_URL = "http://deeppurpleapp-env.eba-im9nsvbi.ap-southeast-1.elasticbeanstalk.com/api/communications";
const API_BASE_URL = "http://deeppurpleapp-env.eba-im9nsvbi.ap-southeast-1.elasticbeanstalk.com/emotion";
const API_MOD_URL = "http://deeppurpleapp-env.eba-im9nsvbi.ap-southeast-1.elasticbeanstalk.com/models";
const API_ADMIN_URL = "http://deeppurpleapp-env.eba-im9nsvbi.ap-southeast-1.elasticbeanstalk.com/admin";
const API_USER_URL = "http://deeppurpleapp-env.eba-im9nsvbi.ap-southeast-1.elasticbeanstalk.com/users";

// Category Functions
export const createCategory = (modelId, name) =>
  axios.post(`${API_BASE_URL}/category`, null, { params: { modelId, name } });

export const getCategoriesByModel = (modelId) =>
  axios.get(`${API_BASE_URL}/category`, { params: { modelId } });

export const updateCategory = (id, name) =>
  axios.put(`${API_BASE_URL}/category/${id}`, null, { params: { name } });

export const deleteCategory = (id) =>
  axios.delete(`${API_BASE_URL}/category/${id}`);

// Model Functions
export const getAllModels = () => axios.get(API_MOD_URL);

export const createModel = (name) =>
  axios.post(API_MOD_URL, null, { params: { name } });

export const deleteModel = (id) =>
  axios.delete(`${API_MOD_URL}/${id}`);

// User Authentication Functions
export const loginUser = (username, password) =>
  axios.post(`${API_USER_URL}/login`, { username, password });

export const registerUser = (username, password) =>
  axios.post(`${API_USER_URL}/register`, { username, password });

// Admin User Management Functions
export const createUser = (username, password, role) =>
  axios.post(`${API_ADMIN_URL}/create`, { username, password, role });

export const getAllUsers = () => axios.get(API_ADMIN_URL);

export const getUserByID = (id) => axios.get(`${API_ADMIN_URL}/${id}`)

export const updateUser = (id, updatedUser) =>
  axios.put(`${API_ADMIN_URL}/${id}`, updatedUser);

export const deleteUser = (id) =>
  axios.delete(`${API_ADMIN_URL}/${id}`);

export const getAssociationsForModel = async (modelId) => {
  return await axios.get(`${API_BASE_URL}/word-associations/${modelId}`);
};
export const getAllEmotionCategories = () => axios.get(`${API_BASE_URL}/emotion-categories`);
export const createAssociation = (word, emotionCategoryId) =>
  axios.post(`${API_BASE_URL}/word-association`, null, {
    params: { word, emotionCategoryId },
  });
export const deleteAssociation = (id) => axios.delete(`${API_BASE_URL}/word-association/${id}`);
