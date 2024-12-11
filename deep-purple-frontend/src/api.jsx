import axios from "axios";

const API_BASE_URL = "http://localhost:8080/emotion"; // Adjust as per your backend setup

const API_MOD_URL = "http://localhost:8080/models";

const API_ADMIN_URL = "http://localhost:8080/admin";

const API_USER_URL = "http://localhost:8080/users";

// Create a new category
export const createCategory = async (modelId, name) => {
    return axios.post("http://localhost:8080/emotion/category", null, {
      params: {
        modelId,
        name,
      },
    });
  };

  // Get categories by model ID
  export const getCategoriesByModel = async (modelId) => {
    return axios.get("http://localhost:8080/emotion/category", {
      params: { modelId },
    });
  };

  // Update category
  export const updateCategory = async (id, name) => {
    return axios.put(`http://localhost:8080/emotion/category/${id}`, null, {
      params: { name },
    });
  };

  // Delete category
  export const deleteCategory = async (id) => {
    return axios.delete(`http://localhost:8080/emotion/category/${id}`);
  };

export const createWordAssociation = (word, emotionCategoryId) =>
  axios.post(`${API_BASE_URL}/word-association`, null, {
    params: { word, emotionCategoryId },
  });

export const deleteWordAssociation = (id) =>
  axios.delete(`${API_BASE_URL}/word-association/${id}`);

export const getAllModels = () => axios.get(API_MOD_URL);
export const createModel = (name) => axios.post(API_MOD_URL, null, { params: { name } });
export const deleteModel = (id) => axios.delete(`${API_MOD_URL}/${id}`);

// user register
export const registerUser = async (username: string, password: string) => {
    try {
        const response = await axios.post(`${API_URL}/register`, { username, password });
        return response.data;
    } catch (error) {
        console.error("Error registering", error.response?.data || error.message);
        throw error;
    }
}

// admin create user
export const createUser = async (username: string, password: string, role: string) => {
    try {
        const response = await axios.post(`${API_ADMIN_URL}/create`, { username, password, role });
        return response.data;
    } catch (error) {
        console.error("Error creating user", error.response?.data || error.message);
        throw error;
    }
}

// admin create user
export const createUser = async (username: string, password: string, role: string) => {
    try {
        const response = await axios.post(`${API_ADMIN_URL}/create`, { username, password, role });
        return response.data;
    } catch (error) {
        console.error("Error creating user", error.response?.data || error.message);
        throw error;
    }
}

// admin get all users
export const getAllUsers = async () => {
    try {
        const response = await axios.get(`${API_ADMIN_URL}`);
        return response.data;
    } catch (error) {
        console.error("Error fetching users", error.response?.data || error.message);
        throw error;
    }
}

// admin update user
export const updateUser = async (id: number, updatedUser: { username: string, password: string, role: string }) => {
    try {
        const response = await axios.put(`${API_URL}/${id}`, updatedUser);
        return response.data;
    } catch (error) {
        console.error("Error updating user", error.response?.data || error.message);
        throw error;
    }
}

// admin delete user
export const deleteUser = async (id: number) => {
    try {
        const response = await axios.delete(`${API_URL}/${id}`);
        return response.data;
    } catch (error) {
        console.error("Error deleting user", error.response?.data || error.message);
        throw error;
    }
}