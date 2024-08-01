لبناء نظام مراقبة الشبكة في الوقت الحقيقي باستخدام SNMP (v1, v2, v3, v4) لمراقبة عنوان IP، DHCP، المنفذ، BIOS، GPU، CPU، اللوحة الأم، لوحة المفاتيح، والفأرة، مع استخدام البرمجيات الحرة والمفتوحة المصدر مثل PHP وMySQL وHTML، يمكن اتباع الخطوات التالية:

### 1. **إعداد البيئة**

#### **تثبيت البرمجيات الأساسية:**
1. **تثبيت PHP وMySQL وApache:**
   - يمكنك تثبيت هذه البرمجيات باستخدام حزمة مثل XAMPP أو LAMP.

2. **تثبيت مكتبة `php-snmp`:**
   - على نظام Linux، يمكنك تثبيتها باستخدام:
     ```bash
     sudo apt-get install php-snmp
     ```

### 2. **إعداد قاعدة البيانات**

#### **إنشاء قاعدة البيانات والجداول:**

في MySQL، قم بإنشاء قاعدة البيانات والجداول:
```sql
CREATE DATABASE network_monitoring_db;

USE network_monitoring_db;

CREATE TABLE devices (
    id INT AUTO_INCREMENT PRIMARY KEY,
    ip_address VARCHAR(45),
    dhcp BOOLEAN,
    port INT,
    bios VARCHAR(255),
    gpu VARCHAR(255),
    cpu VARCHAR(255),
    motherboard VARCHAR(255),
    keyboard VARCHAR(255),
    mouse VARCHAR(255),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 3. **جمع البيانات باستخدام SNMP**

#### **إنشاء سكربت لجمع البيانات (collect_data.php):**

```php
<?php
$ip = '192.168.1.1'; // قم بتحديث عنوان IP للجهاز المراد مراقبته
$community = 'public'; // قم بتحديث المجتمع إذا لزم الأمر

function snmp_get_value($ip, $oid, $community) {
    $session = new SNMP(SNMP::VERSION_2C, $ip, $community);
    $result = $session->get($oid);
    $session->close();
    return $result;
}

$dhcp = snmp_get_value($ip, '1.3.6.1.2.1.4.34.1.3.1.4', $community);
$port = snmp_get_value($ip, '1.3.6.1.2.1.2.2.1.1', $community);
$bios = snmp_get_value($ip, '1.3.6.1.2.1.1.5.0', $community);
$gpu = snmp_get_value($ip, '1.3.6.1.2.1.25.3.2.1.3.0', $community);
$cpu = snmp_get_value($ip, '1.3.6.1.2.1.25.3.3.1.2.0', $community);
$motherboard = snmp_get_value($ip, '1.3.6.1.2.1.25.3.2.1.3.0', $community);
$keyboard = snmp_get_value($ip, '1.3.6.1.2.1.25.3.2.1.3.0', $community);
$mouse = snmp_get_value($ip, '1.3.6.1.2.1.25.3.2.1.3.0', $community);

$mysqli = new mysqli("localhost", "root", "your_password", "network_monitoring_db");

if ($mysqli->connect_error) {
    die("Connection failed: " . $mysqli->connect_error);
}

$stmt = $mysqli->prepare("INSERT INTO devices (ip_address, dhcp, port, bios, gpu, cpu, motherboard, keyboard, mouse) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
$stmt->bind_param("sisssssss", $ip, $dhcp, $port, $bios, $gpu, $cpu, $motherboard, $keyboard, $mouse);
$stmt->execute();
$stmt->close();
$mysqli->close();
?>
```

### 4. **إنشاء واجهة المستخدم**

#### **إعداد HTML وCSS:**

- **HTML (index.html):**
  ```html
  <!DOCTYPE html>
  <html lang="en">
  <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Network Monitoring System</title>
      <link rel="stylesheet" href="styles.css">
  </head>
  <body>
      <div class="container">
          <h1 class="header">Network Monitoring</h1>
          <div id="data-container"></div>
      </div>
      <script src="script.js"></script>
  </body>
  </html>
  ```

- **CSS (styles.css):**
  ```css
  @media (max-width: 600px) {
      .container {
          padding: 10px;
      }
      .header {
          font-size: 18px;
      }
  }

  @media (min-width: 601px) and (max-width: 1024px) {
      .container {
          padding: 20px;
      }
      .header {
          font-size: 24px;
      }
  }

  @media (min-width: 1025px) {
      .container {
          padding: 30px;
      }
      .header {
          font-size: 30px;
      }
  }
  ```

- **JavaScript (script.js):**
  ```javascript
  function fetchData() {
      fetch('fetch_data.php')
          .then(response => response.json())
          .then(data => {
              const container = document.getElementById('data-container');
              container.innerHTML = data.map(item => `
                  <div>
                      <p>IP Address: ${item.ip_address}</p>
                      <p>DHCP: ${item.dhcp}</p>
                      <p>Port: ${item.port}</p>
                      <p>BIOS: ${item.bios}</p>
                      <p>GPU: ${item.gpu}</p>
                      <p>CPU: ${item.cpu}</p>
                      <p>Motherboard: ${item.motherboard}</p>
                      <p>Keyboard: ${item.keyboard}</p>
                      <p>Mouse: ${item.mouse}</p>
                      <p>Timestamp: ${item.timestamp}</p>
                  </div>
              `).join('');
          });
  }

  setInterval(fetchData, 5000); // Refresh data every 5 seconds
  ```

#### **إنشاء سكربت جلب البيانات (fetch_data.php):**

```php
<?php
$mysqli = new mysqli("localhost", "root", "your_password", "network_monitoring_db");

if ($mysqli->connect_error) {
    die("Connection failed: " . $mysqli->connect_error);
}

$result = $mysqli->query("SELECT * FROM devices ORDER BY timestamp DESC");

$data = array();
while($row = $result->fetch_assoc()) {
    $data[] = $row;
}

$mysqli->close();

header('Content-Type: application/json');
echo json_encode($data);
?>
```

### 5. **تشغيل المشروع:**

1. **تأكد من إعداد البيئة بشكل صحيح، وتشغيل خادم الويب (Apache) وخادم قاعدة البيانات (MySQL).**
2. **ضع الملفات (index.html, styles.css, script.js, collect_data.php, fetch_data.php) في مجلد مشروع الويب.**
3. **قم بتشغيل سكربت جمع البيانات (collect_data.php) بشكل دوري (يمكنك استخدام `cron job` على Linux أو `Task Scheduler` على Windows).**

### ملخص:

1. **إعداد البيئة وتثبيت البرمجيات الأساسية.**
2. **إنشاء قاعدة البيانات والجداول في MySQL.**
3. **جمع البيانات باستخدام SNMP وتخزينها في قاعدة البيانات.**
4. **إنشاء واجهة المستخدم باستخدام HTML وCSS وJavaScript.**
5. **إعداد سكربت جلب البيانات وعرضها في الوقت الحقيقي.**

بهذا يمكنك أن تبني نظامًا لمراقبة الشبكة في الوقت الحقيقي باستخدام تقنيات PHP وMySQL وHTML. النظام سيستفيد من SNMP لجمع بيانات الشبكة وسيعرض هذه البيانات في واجهة ويب متجاوبة.
