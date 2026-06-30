const express = require('express');
const cors = require('cors');
const path = require('path');
const crypto = require('crypto');
const fs = require('fs');
const cloudinary = require('cloudinary').v2;
const multer = require('multer');
require('dotenv').config({ path: __dirname + '/.env' });
const Razorpay = require('razorpay');

const razorpay = new Razorpay({
  key_id: process.env.RAZORPAY_KEY_ID,
  key_secret: process.env.RAZORPAY_KEY_SECRET,
});

const app = express();
app.use(cors());
app.use(express.json());
// Cloudinary config
cloudinary.config({
  cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
  api_key: process.env.CLOUDINARY_API_KEY,
  api_secret: process.env.CLOUDINARY_API_SECRET,
});

// Multer config for file uploads
const upload = multer({ dest: '/tmp/uploads/' });

// Product catalog for website
let products = [];
let imageMap = {};

function loadJSON(filename) {
  const paths = [
    path.join(__dirname, filename),
    path.join(process.cwd(), filename),
    path.join(__dirname, '..', filename),
    '/' + filename,
  ];
  for (const p of paths) {
    try {
      return JSON.parse(fs.readFileSync(p, 'utf-8'));
    } catch (e) { /* try next */ }
  }
  console.error('Could not load', filename, '(tried', paths.length, 'paths)');
  return null;
}

const prodsData = loadJSON('products.json');
if (prodsData) products = prodsData.products || [];
else products = [];

const imgData = loadJSON('image-map.json');
if (imgData) imageMap = imgData;

function toRupees(paise) {
  return '₹' + (paise / 100).toLocaleString('en-IN', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

app.use('/images', express.static(path.join(__dirname, 'public/images')));

// Upload endpoint — receives image, uploads to Cloudinary, returns URL
app.post('/api/v1/upload', upload.single('image'), async (req, res) => {
  try {
    if (!req.file) {
      return res.status(400).json({ success: false, message: 'No image file provided' });
    }
    const result = await cloudinary.uploader.upload(req.file.path, {
      folder: 'rameswaram-dry-fish',
      use_filename: true,
      unique_filename: false,
    });
    fs.unlink(req.file.path, () => {});
    res.json({ success: true, data: { url: result.secure_url, publicId: result.public_id } });
  } catch (error) {
    console.error('Upload error:', error.message);
    res.status(500).json({ success: false, message: 'Upload failed: ' + error.message });
  }
});

// In-memory orders storage
let orders = [];
// Track Razorpay orders for server-side verification
const razorpayOrders = {};

const BUSINESS = {
  name: 'Rameswaram Dry Fish',
  email: 'godofwarvetri999@gmail.com',
  phone: '+91 00000 00000',
  address: 'Rameswaram, Tamil Nadu, India',
};

function layout(title, body) {
  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>${title} — ${BUSINESS.name}</title>
  <meta name="description" content="Premium quality dry fish from Rameswaram, Tamil Nadu">
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; line-height: 1.6; color: #333; }
    .container { max-width: 960px; margin: 0 auto; padding: 0 20px; }
    header { background: #0a1628; color: #fff; padding: 20px 0; }
    header h1 { font-size: 24px; }
    nav { margin-top: 8px; }
    nav a { color: #90caf9; text-decoration: none; margin-right: 16px; font-size: 14px; }
    nav a:hover { text-decoration: underline; }
    .prod-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); gap: 20px; }
    .prod-card { background: #fff; border-radius: 12px; overflow: hidden; box-shadow: 0 2px 8px rgba(0,0,0,0.08); border: 1px solid #eee; transition: transform 0.15s, box-shadow 0.15s; }
    .prod-card:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(0,0,0,0.12); }
    .prod-card img { width: 100%; height: 180px; object-fit: cover; background: #f5f7fa; }
    .prod-card .body { padding: 16px; }
    .prod-card .name-ta { font-size: 18px; font-weight: 700; margin-bottom: 2px; }
    .prod-card .name-en { font-size: 13px; color: #888; margin-bottom: 8px; }
    .prod-card .cat { display: inline-block; background: #e3f2fd; color: #1565c0; font-size: 11px; font-weight: 600; padding: 2px 10px; border-radius: 12px; margin-bottom: 10px; }
    .prod-card .desc { font-size: 13px; color: #666; margin-bottom: 12px; line-height: 1.4; }
    .prod-card .skus { border-top: 1px solid #eee; padding-top: 10px; }
    .prod-card .sku-row { display: flex; justify-content: space-between; font-size: 14px; padding: 4px 0; }
    .prod-card .sku-row .weight { color: #555; }
    .prod-card .sku-row .price { font-weight: 700; color: #e65100; }
    .prod-card .sku-row .mrp { text-decoration: line-through; color: #999; font-size: 12px; margin-right: 6px; }
    .hero { background: linear-gradient(135deg, #0a1628, #1a3a5c); color: #fff; padding: 60px 0; text-align: center; }
    .hero h2 { font-size: 36px; margin-bottom: 16px; }
    .hero p { font-size: 18px; color: #b0bec5; max-width: 600px; margin: 0 auto; }
    .section { padding: 48px 0; }
    .section h3 { font-size: 24px; margin-bottom: 24px; color: #0a1628; }
    .section p { margin-bottom: 12px; color: #555; }
    .grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(200px, 1fr)); gap: 16px; }
    .card { background: #f5f7fa; padding: 20px; border-radius: 8px; text-align: center; }
    .card h4 { font-size: 16px; margin-bottom: 8px; }
    .card p { font-size: 13px; color: #777; }
    footer { background: #0a1628; color: #888; padding: 24px 0; text-align: center; font-size: 13px; }
    footer a { color: #90caf9; text-decoration: none; }
    .contact { margin-top: 8px; }
    .contact span { margin: 0 8px; }
    @media (max-width: 600px) { .hero h2 { font-size: 26px; } }
  </style>
</head>
<body>
  <header>
    <div class="container">
      <h1>${BUSINESS.name}</h1>
      <nav>
        <a href="/">Home</a>
        <a href="/products">Products</a>
        <a href="/privacy">Privacy Policy</a>
        <a href="/terms">Terms of Service</a>
      </nav>
    </div>
  </header>
  ${body}
  <footer>
    <div class="container">
      <p>&copy; ${new Date().getFullYear()} ${BUSINESS.name}. All rights reserved.</p>
      <p class="contact">${BUSINESS.email} <span>|</span> ${BUSINESS.phone}</p>
      <p>${BUSINESS.address}</p>
    </div>
  </footer>
</body>
</html>`;
}

app.get('/', (req, res) => {
  res.send(layout('Home', `
  <section class="hero">
    <div class="container">
      <h2>Premium Quality Dry Fish</h2>
      <p>Authentic Rameswaram dry fish — naturally sun-dried and delivered fresh to your doorstep across India.</p>
    </div>
  </section>
  <section class="section">
    <div class="container">
      <h3>Our Products</h3>
      <div class="grid">
        <div class="card"><h4>Fish</h4><p>Nethili, Koduva, Vanjiram, Kola, Ayirai & more</p></div>
        <div class="card"><h4>Prawns</h4><p>Kaintha Iraal — cleaned & sun-dried</p></div>
        <div class="card"><h4>Squid</h4><p>Kanava / Squid — premium quality</p></div>
        <div class="card"><h4>Crab</h4><p>Nandu — naturally dried</p></div>
        <div class="card"><h4>Lobster</h4><p>Fresh dried lobster from Gulf of Mannar</p></div>
        <div class="card"><h4>Combos</h4><p>Value combo packs for families</p></div>
      </div>
      <div style="text-align:center;margin-top:20px">
        <a href="/products" style="display:inline-block;background:#1565c0;color:#fff;padding:12px 32px;border-radius:8px;text-decoration:none;font-weight:600;font-size:16px">View All Products with Prices</a>
      </div>
    </div>
  </section>
  <section class="section" style="background:#f5f7fa;">
    <div class="container">
      <h3>About Us</h3>
      <p>We source the freshest catch from the Gulf of Mannar, Rameswaram — one of India's richest fishing grounds. Our traditional sun-drying process preserves natural flavor without any preservatives.</p>
      <p>Every order is carefully packed and delivered through our app for a seamless shopping experience.</p>
    </div>
  </section>
  <section class="section">
    <div class="container">
      <h3>Contact Us</h3>
      <p>Email: <a href="mailto:${BUSINESS.email}">${BUSINESS.email}</a></p>
      <p>Phone: ${BUSINESS.phone}</p>
      <p>Location: ${BUSINESS.address}</p>
    </div>
  </section>`));
});

app.get('/products', (req, res) => {
  const cat = req.query.category;
  let filtered = cat ? products.filter(p => p.category.toLowerCase() === cat.toLowerCase()) : products;

  const cats = [...new Set(products.map(p => p.category))];

  const cards = filtered.map(p => {
    const img = p.images?.[0] ? (imageMap[p.images[0]] || '') : '';
    const skuRows = p.skus.filter(s => s.isAvailable !== false).map(s => {
      const hasDiscount = s.mrp && s.mrp > s.price;
      return `<div class="sku-row">
        <span class="weight">${s.weight}</span>
        <span class="price">${hasDiscount ? `<span class="mrp">${toRupees(s.mrp)}</span>` : ''}${toRupees(s.price)}</span>
      </div>`;
    }).join('');

    return `<div class="prod-card">
      <img src="${img}" alt="${p.name}" loading="lazy" onerror="this.style.display='none'">
      <div class="body">
        <div class="name-ta">${p.nameTamil || p.name}</div>
        <div class="name-en">${p.name}</div>
        <span class="cat">${p.category}</span>
        ${p.shortDesc ? `<div class="desc">${p.shortDesc}</div>` : ''}
        <div class="skus">${skuRows}</div>
      </div>
    </div>`;
  }).join('');

  const catLinks = cats.map(c =>
    `<a href="/products?category=${encodeURIComponent(c)}" style="display:inline-block;background:#e3f2fd;color:#1565c0;padding:6px 16px;border-radius:20px;font-size:13px;font-weight:600;text-decoration:none;margin:4px">${c}</a>`
  ).join('');

  res.send(layout('Products', `
  <section class="section">
    <div class="container">
      <h3>Our Products ${cat ? `— ${cat}` : ''}</h3>
      <div style="margin-bottom:24px;text-align:center">
        <a href="/products" style="display:inline-block;background:${cat ? '#e0e0e0' : '#1565c0'};color:${cat ? '#333' : '#fff'};padding:6px 16px;border-radius:20px;font-size:13px;font-weight:600;text-decoration:none;margin:4px">All</a>
        ${catLinks}
      </div>
      <p style="text-align:center;color:#888;margin-bottom:24px">${filtered.length} product${filtered.length !== 1 ? 's' : ''} available</p>
      <div class="prod-grid">${cards}</div>
    </div>
  </section>`));
});

app.get('/privacy', (req, res) => {
  res.send(layout('Privacy Policy', `
  <section class="section">
    <div class="container">
      <h3>Privacy Policy</h3>
      <p><strong>Last updated:</strong> ${new Date().toLocaleDateString('en-IN')}</p>
      <p>${BUSINESS.name} respects your privacy. This policy explains how we collect, use, and protect your information.</p>
      <h4>Information We Collect</h4>
      <p>Name, email address, phone number, shipping address, and payment details necessary for processing orders.</p>
      <h4>How We Use Your Information</h4>
      <p>To process and deliver your orders, communicate order updates, and improve our service.</p>
      <h4>Data Security</h4>
      <p>All payment transactions are processed securely through Razorpay. We do not store your payment card details.</p>
      <h4>Contact</h4>
      <p>For privacy concerns, email us at <a href="mailto:${BUSINESS.email}">${BUSINESS.email}</a>.</p>
    </div>
  </section>`));
});

app.get('/terms', (req, res) => {
  res.send(layout('Terms of Service', `
  <section class="section">
    <div class="container">
      <h3>Terms of Service</h3>
      <p><strong>Last updated:</strong> ${new Date().toLocaleDateString('en-IN')}</p>
      <h4>Orders & Payment</h4>
      <p>All prices are in Indian Rupees (INR). Payment is collected at the time of ordering via Razorpay.</p>
      <h4>Shipping & Delivery</h4>
      <p>Orders are processed within 24 hours and shipped across India. Delivery timelines vary by location.</p>
      <h4>Returns & Refunds</h4>
      <p>Contact us within 24 hours of delivery for any quality issues. Refunds are processed within 5-7 business days.</p>
      <h4>Contact</h4>
      <p>For support, email <a href="mailto:${BUSINESS.email}">${BUSINESS.email}</a>.</p>
    </div>
  </section>`));
});

app.post('/api/v1/auth/google', (req, res) => {
  const { token } = req.body;
  if (!token) return res.status(400).json({ success: false, message: 'Token required' });
  res.json({ success: true, data: { id: 'user-1', name: 'Test User', email: 'test@gmail.com', image: null, role: 'USER' } });
});

app.get('/api/v1/orders', (req, res) => {
  const userId = req.query.userId;
  let filteredOrders = userId ? orders.filter(order => order.userId === userId) : orders;
  // Only return paid/completed orders to prevent leaking unverified orders
  filteredOrders = filteredOrders.filter(order => order.paymentStatus === 'PAID');
  res.json({ success: true, data: filteredOrders });
});

app.get('/api/v1/orders/:id', (req, res) => {
  const userId = req.query.userId;
  const order = orders.find(item => item.id === req.params.id);
  if (!order) return res.status(404).json({ success: false, message: 'Order not found' });
  if (userId && order.userId !== userId) {
    return res.status(404).json({ success: false, message: 'Order not found' });
  }
  res.json({ success: true, data: order });
});

// ----- Order endpoints -----
let orderIdCounter = 0;

app.post('/api/v1/orders', (req, res) => {
  const body = req.body;
  if (!body) return res.status(400).json({ success: false, message: 'Body required' });
  if (!body.userId) return res.status(400).json({ success: false, message: 'userId required' });
  
  orderIdCounter++;

  // Use address from request if provided, otherwise build from addressId
  const rawAddr = body.address || {};
  const address = {
    id: body.addressId || rawAddr.id || '',
    name: rawAddr.name || '',
    phone: rawAddr.phone || '',
    street: rawAddr.street || '',
    city: rawAddr.city || '',
    state: rawAddr.state || '',
    pincode: rawAddr.pincode || '',
  };

  const order = {
    id: `order_${orderIdCounter}`,
    userId: body.userId,
    orderNumber: `RDF${String(orderIdCounter).padStart(4, '0')}`,
    items: body.items || [],
    status: 'PENDING',
    timeline: [
      { status: 'PENDING', timestamp: new Date().toISOString(), description: 'Order placed successfully' }
    ],
    shippingAddress: address,
    paymentMethod: body.paymentMethod || 'RAZORPAY',
    paymentStatus: 'PENDING',
    subtotal: body.subtotal || 0,
    deliveryCharge: body.deliveryCharge || 0,
    discount: body.discount || 0,
    total: body.total || 0,
    couponApplied: null,
    notes: null,
    createdAt: new Date().toISOString(),
    updatedAt: new Date().toISOString(),
    estimatedDelivery: null
  };
  orders.push(order);
  res.json({ success: true, data: order });
});

// ----- Razorpay endpoints -----

app.post('/api/v1/razorpay/create-order', async (req, res) => {
  try {
    const { amount } = req.body;
    if (!amount || amount < 100) {
      return res.status(400).json({ success: false, message: 'Amount must be at least 100 paise' });
    }

    // Real Razorpay API call
    const options = {
      amount: Math.round(amount),
      currency: 'INR',
      receipt: 'receipt_' + Date.now(),
    };
    const order = await razorpay.orders.create(options);
    // Store for server-side verification
    razorpayOrders[order.id] = {
      amount: order.amount,
      userId: 'anonymous',
      createdAt: Date.now(),
    };
    res.json({
      success: true,
      data: {
        orderId: order.id,
        amount: order.amount,
        currency: order.currency,
        keyId: process.env.RAZORPAY_KEY_ID,
      }
    });
  } catch (error) {
    console.error('Razorpay create-order error:', error);
    res.status(500).json({ success: false, message: 'Failed to create payment order' });
  }
});

app.post('/api/v1/razorpay/verify-payment', async (req, res) => {
  try {
    const { razorpay_order_id, razorpay_payment_id } = req.body;

    if (!razorpay_order_id) {
      return res.status(400).json({ success: false, message: 'Missing razorpay_order_id' });
    }

    let payment;

    if (razorpay_payment_id) {
      // Normal flow: verify a specific payment
      payment = await razorpay.payments.fetch(razorpay_payment_id);

      if (payment.order_id !== razorpay_order_id) {
        console.error('verify-payment: order_id mismatch');
        return res.status(400).json({ success: false, message: 'Order ID mismatch' });
      }
    } else {
      // Recovery flow: no payment_id — check order status directly
      // The order's 'paid' status is more reliable and available sooner than
      // individual payment details via fetchPayments (which can be slow/immediately inconsistent)
      const orderDetails = await razorpay.orders.fetch(razorpay_order_id);
      if (orderDetails.status === 'paid' && orderDetails.amount_paid > 0) {
        // Try to get the payment_id for record-keeping, but don't fail if unavailable
        try {
          const payments = await razorpay.orders.fetchPayments(razorpay_order_id);
          const captured = payments.items.find(p => p.status === 'captured');
          payment = captured || { id: '', status: 'captured', amount: orderDetails.amount_paid, order_id: razorpay_order_id };
        } catch (_) {
          payment = { id: '', status: 'captured', amount: orderDetails.amount_paid, order_id: razorpay_order_id };
        }
      } else {
        console.error('verify-payment: order not paid, status=' + orderDetails.status + ' amount_paid=' + orderDetails.amount_paid);
        return res.status(400).json({ success: false, message: 'No payment found for this order' });
      }
    }

    // Verify payment is captured
    if (payment.status !== 'captured') {
      console.error('verify-payment: payment not captured, status=' + payment.status);
      return res.status(400).json({ success: false, message: 'Payment not captured' });
    }

    // Verify payment amount matches the original Razorpay order (prevent amount tampering)
    const originalOrder = razorpayOrders[razorpay_order_id];
    if (originalOrder && payment.amount < originalOrder.amount) {
      console.error('verify-payment: amount mismatch: payment=' + payment.amount + ' expected=' + originalOrder.amount);
      return res.status(400).json({ success: false, message: 'Payment amount mismatch' });
    }

    return res.json({ success: true, message: 'Payment verified successfully', data: payment.id });
  } catch (error) {
    const detail = error.error?.description || error.message || 'Unknown error';
    console.error('Razorpay verify-payment error:', detail);
    res.status(500).json({ success: false, message: 'Verification failed: ' + detail });
  }
});

// ----- Razorpay Webhook -----
app.post('/api/v1/razorpay/webhook', async (req, res) => {
  try {
    const webhookSecret = process.env.RAZORPAY_WEBHOOK_SECRET;
    const signature = req.headers['x-razorpay-signature'];

    if (webhookSecret && signature) {
      const expectedSig = crypto.createHmac('sha256', webhookSecret)
        .update(JSON.stringify(req.body))
        .digest('hex');
      if (signature !== expectedSig) {
        console.error('webhook: invalid signature');
        return res.status(400).json({ success: false, message: 'Invalid signature' });
      }
    }

    const event = req.body.event;
    const payment = req.body.payload?.payment?.entity;

    if (event === 'payment.captured' && payment) {
      const orderId = payment.order_id;
      const existingOrder = orders.find(o => o.paymentOrderId === orderId);
      if (existingOrder && existingOrder.paymentStatus !== 'PAID') {
        existingOrder.paymentStatus = 'PAID';
        existingOrder.paymentId = payment.id;
        existingOrder.updatedAt = new Date().toISOString();
        console.log('webhook: order ' + existingOrder.id + ' marked PAID via webhook');
      }
    }

    res.json({ success: true });
  } catch (error) {
    console.error('Webhook error:', error.message);
    res.status(500).json({ success: false, message: 'Webhook error' });
  }
});

const PORT = process.env.PORT || 4000;
app.listen(PORT, '0.0.0.0', () => console.log(`Backend running on port ${PORT}`));
