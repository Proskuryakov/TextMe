module.exports = [
  {
    context: '/api',
    target: 'http://localhost:8080',
    secure: false,
    changeOrigin: true
  },
  {
    context: '/ws',
    target: 'http://localhost:8080',
    secure: false,
    changeOrigin: true
  }
];
