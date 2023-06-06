const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function (app) {
    app.use("/api/v1/bwic", createProxyMiddleware({
        target: process.env.REACT_APP_BWIC_API,
        changeOrigin: true
    }))
    app.use("/api/v1/bid", createProxyMiddleware({
        target: process.env.REACT_APP_BID_API,
        changeOrigin: true
    }))
    app.use("/api/v1/account", createProxyMiddleware({
        target: process.env.REACT_APP_ACCOUNT_API,
        changeOrigin: true
    }))
}