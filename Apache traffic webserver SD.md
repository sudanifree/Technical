لتثبيت خادم الويب Apache Traffic Server على نظام Linux، يمكنك اتباع الخطوات التالية. سأستخدم توزيعة Ubuntu كمثال، لكن يمكن تعديل الخطوات لتناسب توزيعات أخرى.

### 1. **تحديث النظام**

تأكد من تحديث قائمة الحزم لديك:

```sh
sudo apt update
sudo apt upgrade
```

### 2. **تثبيت المتطلبات الأساسية**

تثبيت الأدوات والمكتبات اللازمة لبناء Apache Traffic Server:

```sh
sudo apt install build-essential autoconf libtool pkg-config
```

### 3. **تنزيل Apache Traffic Server**

يمكنك تنزيل أحدث نسخة من Apache Traffic Server من الموقع الرسمي أو من GitHub:

- **من الموقع الرسمي:**

  قم بزيارة [موقع Apache Traffic Server](https://trafficserver.apache.org/downloads) واختر النسخة التي تريد تنزيلها، ثم استخدم `wget` لتنزيلها. على سبيل المثال:

  ```sh
  wget https://downloads.apache.org/trafficserver/trafficserver-9.0.0.tar.bz2
  ```

- **من GitHub:**

  قم بتنزيلها من مستودع GitHub:

  ```sh
  git clone https://github.com/apache/trafficserver.git
  cd trafficserver
  ```

### 4. **فك ضغط الملف (إذا قمت بتحميله من الموقع الرسمي)**

إذا قمت بتحميل الملف من الموقع الرسمي، قم بفك ضغطه:

```sh
tar xjf trafficserver-9.0.0.tar.bz2
cd trafficserver-9.0.0
```

### 5. **بناء وتثبيت Apache Traffic Server**

اتبع الخطوات التالية لبناء وتثبيت Apache Traffic Server:

```sh
./configure
make
sudo make install
```

### 6. **تكوين Apache Traffic Server**

بعد التثبيت، قم بتكوين Apache Traffic Server. عادةً ما يتم وضع ملفات التكوين في `/usr/local/etc/trafficserver`. يمكنك نسخ ملفات التكوين الافتراضية وتعديلها حسب الحاجة.

نسخ ملفات التكوين الافتراضية:

```sh
sudo cp /usr/local/etc/trafficserver/records.config.default /usr/local/etc/trafficserver/records.config
sudo cp /usr/local/etc/trafficserver/stop.config.default /usr/local/etc/trafficserver/stop.config
```

تعديل ملف التكوين الأساسي:

```sh
sudo nano /usr/local/etc/trafficserver/records.config
```

قم بتعديل الإعدادات وفقاً لاحتياجاتك.

### 7. **بدء Apache Traffic Server**

تشغيل Apache Traffic Server باستخدام الأمر:

```sh
sudo traffic_manager start
```

يمكنك أيضاً استخدام أوامر إضافية مثل `stop` و `restart` لإدارة الخدمة:

```sh
sudo traffic_manager stop
sudo traffic_manager restart
```

### 8. **التحقق من التثبيت**

للتحقق من أن Apache Traffic Server يعمل بشكل صحيح، افتح متصفح الويب وانتقل إلى عنوان IP الخاص بالخادم. إذا كان الخادم يعمل، يجب أن تكون قادراً على رؤية واجهة الويب أو تنفيذ طلبات عبر الخادم.

باتباع هذه الخطوات، ستكون قد قمت بتثبيت وتكوين Apache Traffic Server على نظام Linux بنجاح.
