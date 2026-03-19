import { useEffect, useState } from "react";
import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis
} from "recharts";
import { api } from "./api";

const emptyProduct = { name: "", category: "", stock: 0, price: 0 };

function currency(value) {
  return new Intl.NumberFormat("pt-BR", {
    style: "currency",
    currency: "BRL"
  }).format(Number(value || 0));
}

function downloadFile(blob, filename) {
  const url = window.URL.createObjectURL(blob);
  const anchor = document.createElement("a");
  anchor.href = url;
  anchor.download = filename;
  anchor.click();
  window.URL.revokeObjectURL(url);
}

function Card({ title, value }) {
  return (
    <article className="stat-card">
      <span>{title}</span>
      <strong>{value}</strong>
    </article>
  );
}

export default function App() {
  const [dashboard, setDashboard] = useState(null);
  const [products, setProducts] = useState([]);
  const [sales, setSales] = useState([]);
  const [form, setForm] = useState(emptyProduct);
  const [cart, setCart] = useState([]);
  const [error, setError] = useState("");

  async function loadData() {
    try {
      setError("");
      const [dashboardData, productsData, salesData] = await Promise.all([
        api.getDashboard(),
        api.getProducts(),
        api.getSales()
      ]);
      setDashboard(dashboardData);
      setProducts(productsData);
      setSales(salesData);
    } catch {
      setError("Nao foi possivel carregar os dados. Inicie o backend Spring Boot.");
    }
  }

  useEffect(() => {
    loadData();
  }, []);

  async function handleProductSubmit(event) {
    event.preventDefault();
    await api.saveProduct({
      ...form,
      stock: Number(form.stock),
      price: Number(form.price)
    });
    setForm(emptyProduct);
    loadData();
  }

  function addToCart(productId) {
    setCart((current) => {
      const existing = current.find((item) => item.productId === productId);
      if (existing) {
        return current.map((item) =>
          item.productId === productId ? { ...item, quantity: item.quantity + 1 } : item
        );
      }
      return [...current, { productId, quantity: 1 }];
    });
  }

  async function finishSale() {
    if (!cart.length) {
      return;
    }
    await api.createSale({ items: cart });
    setCart([]);
    loadData();
  }

  async function removeProduct(productId) {
    await api.deleteProduct(productId);
    loadData();
  }

  async function handleDownload(type) {
    const blob = type === "excel" ? await api.downloadExcel() : await api.downloadPdf();
    downloadFile(blob, type === "excel" ? "relatorio-sgv.xlsx" : "relatorio-sgv.pdf");
  }

  return (
    <div className="page-shell">
      <header className="hero">
        <div>
          <span className="eyebrow">Sistema de Gestao de Vendas</span>
          <h1>Controle seu PDV com dashboard, estoque, vendas e relatorios em um unico painel.</h1>
        </div>
        <div className="hero-actions">
          <button onClick={() => handleDownload("excel")}>Exportar Excel</button>
          <button className="secondary" onClick={() => handleDownload("pdf")}>Exportar PDF</button>
        </div>
      </header>

      {error ? <div className="error-banner">{error}</div> : null}

      <section className="stats-grid">
        <Card title="Produtos" value={dashboard?.totalProducts ?? 0} />
        <Card title="Vendas" value={dashboard?.totalSales ?? 0} />
        <Card title="Estoque" value={dashboard?.totalStock ?? 0} />
        <Card title="Receita" value={currency(dashboard?.totalRevenue)} />
      </section>

      <section className="content-grid">
        <div className="panel wide">
          <div className="panel-header">
            <h2>Receita por dia</h2>
          </div>
          <div className="chart-wrap">
            <ResponsiveContainer width="100%" height={260}>
              <AreaChart data={dashboard?.salesByDay || []}>
                <defs>
                  <linearGradient id="salesGradient" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#ff7a18" stopOpacity={0.8} />
                    <stop offset="95%" stopColor="#ff7a18" stopOpacity={0.05} />
                  </linearGradient>
                </defs>
                <CartesianGrid strokeDasharray="3 3" stroke="#2f3d4f" />
                <XAxis dataKey="date" stroke="#a9bed1" />
                <YAxis stroke="#a9bed1" />
                <Tooltip />
                <Area type="monotone" dataKey="value" stroke="#ff7a18" fill="url(#salesGradient)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        <div className="panel">
          <div className="panel-header">
            <h2>Estoque Baixo</h2>
          </div>
          <div className="list">
            {(dashboard?.lowStockProducts || []).map((product) => (
              <div className="list-item" key={product.id}>
                <strong>{product.name}</strong>
                <span>{product.stock} unidades</span>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="content-grid">
        <form className="panel" onSubmit={handleProductSubmit}>
          <div className="panel-header">
            <h2>Cadastrar Produto</h2>
          </div>
          <input
            placeholder="Nome do produto"
            value={form.name}
            onChange={(event) => setForm({ ...form, name: event.target.value })}
            required
          />
          <input
            placeholder="Categoria"
            value={form.category}
            onChange={(event) => setForm({ ...form, category: event.target.value })}
          />
          <input
            type="number"
            min="0"
            placeholder="Estoque"
            value={form.stock}
            onChange={(event) => setForm({ ...form, stock: event.target.value })}
          />
          <input
            type="number"
            min="0"
            step="0.01"
            placeholder="Preco"
            value={form.price}
            onChange={(event) => setForm({ ...form, price: event.target.value })}
          />
          <button type="submit">Salvar produto</button>
        </form>

        <div className="panel">
          <div className="panel-header">
            <h2>Registro de Vendas</h2>
          </div>
          <div className="list">
            {products.map((product) => (
              <div className="list-item" key={product.id}>
                <div>
                  <strong>{product.name}</strong>
                  <span>{currency(product.price)} • estoque {product.stock}</span>
                </div>
                <button onClick={() => addToCart(product.id)}>Adicionar</button>
              </div>
            ))}
          </div>
          <div className="sale-footer">
            <span>Itens no carrinho: {cart.reduce((sum, item) => sum + item.quantity, 0)}</span>
            <button onClick={finishSale}>Finalizar venda</button>
          </div>
        </div>
      </section>

      <section className="content-grid">
        <div className="panel wide">
          <div className="panel-header">
            <h2>Produtos cadastrados</h2>
          </div>
          <table>
            <thead>
              <tr>
                <th>Produto</th>
                <th>Categoria</th>
                <th>Estoque</th>
                <th>Preco</th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {products.map((product) => (
                <tr key={product.id}>
                  <td>{product.name}</td>
                  <td>{product.category}</td>
                  <td>{product.stock}</td>
                  <td>{currency(product.price)}</td>
                  <td>
                    <button className="secondary table-button" onClick={() => removeProduct(product.id)}>
                      Excluir
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        <div className="panel">
          <div className="panel-header">
            <h2>Ultimas vendas</h2>
          </div>
          <div className="list">
            {sales.slice(-6).reverse().map((sale) => (
              <div className="list-item" key={sale.id}>
                <strong>Venda #{sale.id}</strong>
                <span>{currency(sale.total)}</span>
              </div>
            ))}
          </div>
        </div>
      </section>
    </div>
  );
}
