const express = require('express');
const cors = require('cors');
const path = require('path');
const crypto = require('crypto');
require('dotenv').config({ path: __dirname + '/.env' });
const Razorpay = require('razorpay');

const razorpay = new Razorpay({
  key_id: process.env.RAZORPAY_KEY_ID,
  key_secret: process.env.RAZORPAY_KEY_SECRET,
});

const app = express();
app.use(cors());
app.use(express.json());
app.use('/images', express.static(path.join(__dirname, 'public/images')));

// In-memory orders storage
let orders = [];
// Track Razorpay orders for server-side verification
const razorpayOrders = {};

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
