const fs = require('fs');
const path = require('path');

const products = JSON.parse(fs.readFileSync(path.join(__dirname, '..', 'products.json'), 'utf-8')).products;
const imageMap = JSON.parse(fs.readFileSync(path.join(__dirname, '..', 'image-map.json'), 'utf-8'));

function toRupees(paise) {
  return '₹' + (paise / 100).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

const cats = [...new Set(products.map(p => p.category))];

function skuTable(skus) {
  return `<table class="sku-table">
    <tr><th>Weight</th><th>Price</th><th>Stock</th></tr>
    ${skus.filter(s => s.isAvailable !== false).map(s => `
      <tr>
        <td>${s.weight}</td>
        <td class="price">${toRupees(s.price)}</td>
        <td>${s.stock > 20 ? 'In Stock' : 'Limited'}</td>
      </tr>`).join('')}
  </table>`;
}

const productRows = products.map((p, i) => {
  const img = p.images?.[0] ? (imageMap[p.images[0]] || '') : '';
  return `<div class="product">
    <div class="num">${i + 1}</div>
    ${img ? `<img src="${img}" alt="${p.name}">` : ''}
    <div class="info">
      <div class="name-ta">${p.nameTamil || p.name}</div>
      <div class="name-en">${p.name}</div>
      <span class="cat">${p.category}</span>
      ${p.shortDesc ? `<div class="desc">${p.shortDesc}</div>` : ''}
      <div class="price-range">${toRupees(Math.min(...p.skus.filter(s => s.isAvailable !== false).map(s => s.price)))} – ${toRupees(Math.max(...p.skus.filter(s => s.isAvailable !== false).map(s => s.price)))}</div>
      ${skuTable(p.skus)}
    </div>
  </div>`;
}).join('');

const html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Rameswaram Dry Fish — Product Catalog</title>
  <style>
    @page { margin: 15mm; }
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; color: #222; padding: 40px; max-width: 1000px; margin: 0 auto; }
    h1 { font-size: 28px; margin-bottom: 4px; }
    .subtitle { color: #666; font-size: 14px; margin-bottom: 30px; }
    .summary { background: #f5f7fa; padding: 16px 20px; border-radius: 8px; margin-bottom: 30px; font-size: 14px; }
    .summary strong { color: #0a1628; }
    .product { display: flex; gap: 20px; padding: 20px 0; border-bottom: 1px solid #eee; page-break-inside: avoid; }
    .product .num { font-size: 12px; color: #999; min-width: 28px; }
    .product img { width: 140px; height: 140px; object-fit: cover; border-radius: 8px; background: #f5f7fa; flex-shrink: 0; }
    .info { flex: 1; }
    .name-ta { font-size: 20px; font-weight: 700; }
    .name-en { font-size: 14px; color: #888; margin-bottom: 6px; }
    .cat { display: inline-block; background: #e3f2fd; color: #1565c0; font-size: 11px; font-weight: 600; padding: 2px 10px; border-radius: 12px; margin-bottom: 8px; }
    .desc { font-size: 13px; color: #666; margin-bottom: 8px; line-height: 1.4; }
    .price-range { font-size: 18px; font-weight: 700; color: #e65100; margin-bottom: 10px; }
    .sku-table { width: 100%; border-collapse: collapse; font-size: 13px; }
    .sku-table th { text-align: left; padding: 6px 8px; background: #f5f7fa; border-bottom: 2px solid #ddd; font-weight: 600; color: #555; }
    .sku-table td { padding: 6px 8px; border-bottom: 1px solid #eee; }
    .sku-table .price { font-weight: 700; color: #e65100; }
    @media print {
      body { padding: 0; }
      .product { break-inside: avoid; }
    }
  </style>
</head>
<body>
  <h1>Rameswaram Dry Fish</h1>
  <div class="subtitle">Product Catalog — ${new Date().toLocaleDateString('en-IN', { year: 'numeric', month: 'long', day: 'numeric' })}</div>
  <div class="summary">
    <strong>${products.length} products</strong> across ${cats.length} categories: ${cats.join(', ')}.<br>
    All prices in Indian Rupees (INR). Payment processed via Razorpay.
  </div>
  ${productRows}
</body>
</html>`;

const outPath = path.join(__dirname, '..', 'product-catalog.html');
fs.writeFileSync(outPath, html);
console.log('Product catalog written to:', outPath);
console.log('Open in browser → File → Print → Save as PDF or take screenshots');
