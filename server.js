const http = require('http');
const fs = require('fs');
const path = require('path');

const JAR_FILE = path.join(__dirname, 'Azr.Client-1.0.4-KeyM.jar');
const JAR_NAME = 'Azr.Client-1.0.4-KeyM.jar';

const html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Azr Client - Timer Mod</title>
  <style>
    body { font-family: sans-serif; max-width: 600px; margin: 80px auto; padding: 0 20px; color: #222; background: #f5f5f5; }
    .card { background: #fff; border-radius: 12px; padding: 32px; box-shadow: 0 2px 16px rgba(0,0,0,0.10); }
    h1 { color: #1a1a2e; margin-top: 0; }
    .badge { display: inline-block; background: #22c55e; color: #fff; border-radius: 6px; padding: 2px 10px; font-size: 13px; margin-left: 8px; vertical-align: middle; }
    a.btn { display: inline-block; margin-top: 24px; background: #2563eb; color: #fff; text-decoration: none; padding: 12px 28px; border-radius: 8px; font-size: 16px; font-weight: bold; transition: background 0.2s; }
    a.btn:hover { background: #1d4ed8; }
    ul { line-height: 2.2; color: #444; }
    code { background: #f0f0f0; padding: 2px 6px; border-radius: 4px; font-size: 14px; }
  </style>
</head>
<body>
  <div class="card">
    <h1>Azr Client <span class="badge">v1.0.4</span></h1>
    <p><strong>ClickGUI tuşu M olarak değiştirildi.</strong></p>
    <ul>
      <li><strong>Custom Main Menu</strong> → Oyun açılırken standart menü yerine gelir</li>
      <li>Gökkuşağı efektli butonlar (singleplayer, multiplayer, options, quit, alts)</li>
      <li>Sol üstte GitHub changelog paneli (canlı commit mesajları)</li>
      <li>Büyük saat göstergesi (HH:mm)</li>
      <li><strong>Kill Aura</strong> + <strong>Aim Assist</strong> + <strong>Timer</strong> + <strong>Save</strong> (önceki sürümden)</li>
      <li>Minecraft: <code>1.21.4 (Fabric)</code></li>
    </ul>
    <a class="btn" href="/download">⬇ İndir — ${JAR_NAME}</a>
  </div>
</body>
</html>`;

const server = http.createServer((req, res) => {
  if (req.url === '/download') {
    if (!fs.existsSync(JAR_FILE)) {
      res.writeHead(404);
      res.end('Dosya bulunamadi.');
      return;
    }
    const stat = fs.statSync(JAR_FILE);
    res.writeHead(200, {
      'Content-Type': 'application/java-archive',
      'Content-Disposition': `attachment; filename="${JAR_NAME}"`,
      'Content-Length': stat.size
    });
    fs.createReadStream(JAR_FILE).pipe(res);
    return;
  }

  res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' });
  res.end(html);
});

server.listen(5000, '0.0.0.0', () => {
  console.log('Server running on http://0.0.0.0:5000');
});
