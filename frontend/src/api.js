const API_URL = "http://localhost:8080/api";

async function request(path, options = {}) {
  const response = await fetch(`${API_URL}${path}`, {
    headers: { "Content-Type": "application/json", ...(options.headers || {}) },
    ...options
  });

  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || "Erro ao processar requisicao");
  }

  const contentType = response.headers.get("content-type") || "";
  if (contentType.includes("application/json")) {
    return response.json();
  }

  return response.blob();
}

export const api = {
  getDashboard: () => request("/dashboard"),
  getProducts: () => request("/products"),
  getSales: () => request("/sales"),
  deleteProduct: (id) => request(`/products/${id}`, { method: "DELETE" }),
  saveProduct: (payload, id) =>
    request(id ? `/products/${id}` : "/products", {
      method: id ? "PUT" : "POST",
      body: JSON.stringify(payload)
    }),
  createSale: (payload) =>
    request("/sales", {
      method: "POST",
      body: JSON.stringify(payload)
    }),
  downloadExcel: () => request("/reports/excel", { headers: {} }),
  downloadPdf: () => request("/reports/pdf", { headers: {} })
};
