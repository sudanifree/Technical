لإنشاء نظام مراقبة الشبكة في الوقت الحقيقي باستخدام SNMP (v1, v2, v3, v4) لمراقبة عنوان IP، DHCP، المنفذ، BIOS، GPU، CPU، اللوحة الأم، لوحة المفاتيح، والفأرة، مع استخدام البرمجيات الحرة والمفتوحة المصدر 100٪ مثل Python وDjango وMySQL وHTML، يمكنك اتباع الخطوات التالية:

### 1. **إعداد البيئة**

#### **تثبيت البرمجيات الأساسية:**
1. **تثبيت Python:**
   - تأكد من أنك تستخدم أحدث إصدار من Python. يمكنك تنزيله من [الموقع الرسمي لـ Python](https://www.python.org/downloads/).

2. **تثبيت Django:**
   - يمكنك تثبيت Django باستخدام pip:
     ```bash
     pip install django
     ```

3. **تثبيت MySQL:**
   - قم بتثبيت MySQL من [الموقع الرسمي لـ MySQL](https://dev.mysql.com/downloads/mysql/).

4. **تثبيت مكتبة `mysqlclient`:**
   - لتوصيل Django بـ MySQL، تحتاج إلى مكتبة `mysqlclient`:
     ```bash
     pip install mysqlclient
     ```

5. **تثبيت مكتبة `pysnmp`:**
   - هذه المكتبة تستخدم لاستعلام SNMP:
     ```bash
     pip install pysnmp
     ```

### 2. **إنشاء مشروع Django**

#### **إنشاء مشروع Django:**
```bash
django-admin startproject network_monitoring_project
cd network_monitoring_project
```

#### **إنشاء تطبيق Django:**
```bash
python manage.py startapp monitoring
```

### 3. **إعداد قاعدة البيانات**

#### **تكوين إعدادات قاعدة البيانات في Django:**

في ملف `network_monitoring_project/settings.py`، قم بتعديل إعدادات قاعدة البيانات لتتصل بـ MySQL:
```python
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'network_monitoring_db',
        'USER': 'root',
        'PASSWORD': 'your_password',
        'HOST': 'localhost',
        'PORT': '3306',
    }
}
```

#### **إنشاء الجداول:**

أنشئ نموذجًا لقاعدة البيانات في ملف `monitoring/models.py`:
```python
from django.db import models

class Device(models.Model):
    ip_address = models.GenericIPAddressField()
    dhcp = models.BooleanField()
    port = models.IntegerField()
    bios = models.CharField(max_length=255)
    gpu = models.CharField(max_length=255)
    cpu = models.CharField(max_length=255)
    motherboard = models.CharField(max_length=255)
    keyboard = models.CharField(max_length=255)
    mouse = models.CharField(max_length=255)
    timestamp = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.ip_address} - {self.cpu}"
```

ثم قم بترحيل الجداول إلى قاعدة البيانات:
```bash
python manage.py makemigrations
python manage.py migrate
```

### 4. **جمع البيانات باستخدام SNMP**

#### **إنشاء سكربت لجمع البيانات (snmp_collector.py):**
```python
from pysnmp.hlapi import *
from monitoring.models import Device
import django
import os

os.environ.setdefault('DJANGO_SETTINGS_MODULE', 'network_monitoring_project.settings')
django.setup()

def snmp_get(ip, oid, community='public'):
    iterator = getCmd(
        SnmpEngine(),
        CommunityData(community),
        UdpTransportTarget((ip, 161)),
        ContextData(),
        ObjectType(ObjectIdentity(oid))
    )

    error_indication, error_status, error_index, var_binds = next(iterator)

    if error_indication:
        print(error_indication)
    elif error_status:
        print(f'{error_status.prettyPrint()} at {error_index and var_binds[int(error_index) - 1][0] or "?"}')
    else:
        for var_bind in var_binds:
            return var_bind.prettyPrint().split('=')[1].strip()

def collect_data():
    ip = '192.168.1.1'  # قم بتحديث عنوان IP للجهاز المراد مراقبته
    community = 'public'  # قم بتحديث المجتمع إذا لزم الأمر

    dhcp = bool(snmp_get(ip, '1.3.6.1.2.1.4.34.1.3.1.4', community))  # OID لـ DHCP
    port = int(snmp_get(ip, '1.3.6.1.2.1.2.2.1.1', community))  # OID للمنفذ
    bios = snmp_get(ip, '1.3.6.1.2.1.1.5.0', community)  # OID لـ BIOS
    gpu = snmp_get(ip, '1.3.6.1.2.1.25.3.2.1.3.0', community)  # OID لـ GPU
    cpu = snmp_get(ip, '1.3.6.1.2.1.25.3.3.1.2.0', community)  # OID لـ CPU
    motherboard = snmp_get(ip, '1.3.6.1.2.1.25.3.2.1.3.0', community)  # OID للوحة الأم
    keyboard = snmp_get(ip, '1.3.6.1.2.1.25.3.2.1.3.0', community)  # OID للوحة المفاتيح
    mouse = snmp_get(ip, '1.3.6.1.2.1.25.3.2.1.3.0', community)  # OID للفأرة

    Device.objects.create(
        ip_address=ip,
        dhcp=dhcp,
        port=port,
        bios=bios,
        gpu=gpu,
        cpu=cpu,
        motherboard=motherboard,
        keyboard=keyboard,
        mouse=mouse
    )

if __name__ == "__main__":
    collect_data()
```

### 5. **إنشاء واجهة المستخدم**

#### **إعداد الملفات الثابتة:**

إنشاء مجلد `static` و`templates` داخل مجلد التطبيق `monitoring`:

- **`static/`**: لتخزين ملفات CSS وJavaScript.
- **`templates/`**: لتخزين ملفات HTML.

#### **إعداد HTML وCSS:**

- **HTML (templates/index.html):**
  ```html
  <!DOCTYPE html>
  <html lang="en">
  <head>
      <meta charset="UTF-8">
      <meta name="viewport" content="width=device-width, initial-scale=1.0">
      <title>Network Monitoring System</title>
      <link rel="stylesheet" href="{% static 'styles.css' %}">
      <script src="{% static 'script.js' %}" defer></script>
  </head>
  <body>
      <div class="container">
          <h1 class="header">Network Monitoring</h1>
          <div id="data-container"></div>
      </div>
  </body>
  </html>
  ```

- **CSS (static/styles.css):**
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

- **JavaScript (static/script.js):**
  ```javascript
  function fetchData() {
      fetch('/fetch_data/')
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

### 6. **إعداد العرض (Views) والمسارات (URLs)**

#### **إعداد العرض (views.py):**

في ملف `monitoring/views.py`، أضف العرض لجلب البيانات:
```python
from django.http import JsonResponse
from .models import Device

def fetch_data(request):
    data = list(Device.objects.all().values('ip_address', 'dhcp', 'port', 'bios', 'gpu',
