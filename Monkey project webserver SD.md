لتثبيت خادم الويب Monkey على نظام Linux من GitHub، اتبع هذه الخطوات:

### 1. **تحديث النظام**

تأكد من تحديث قائمة الحزم لديك:

```sh
sudo apt update
sudo apt upgrade
```

### 2. **تثبيت المتطلبات الأساسية**

تثبيت الأدوات والمكتبات اللازمة لبناء البرامج:

```sh
sudo apt install build-essential autoconf libtool pkg-config
```

### 3. **تنزيل Monkey**

قم بتنزيل آخر نسخة من Monkey من GitHub:

```sh
git clone https://github.com/monkey/monkey.git
cd monkey
```

### 4. **بناء وتثبيت Monkey**

اتبع الخطوات التالية لبناء وتثبيت Monkey:

```sh
./autogen.sh
./configure
make
sudo make install
```

### 5. **تكوين Monkey**

بعد التثبيت، يمكنك تكوين Monkey. عادةً ما يتم وضع ملف التكوين في `/usr/local/etc/monkey`، ولكن قد تحتاج إلى التحقق من مسار التثبيت الدقيق بناءً على إصدارك.

قم بإنشاء ملف تكوين أساسي:

```sh
sudo nano /usr/local/etc/monkey/monkey.conf
```

أضف التكوين الأساسي:

```ini
server {
    listen 80
    server_name localhost
    root /var/www/html
    index index.html
}
```

### 6. **إنشاء دليل الموقع وتعيين الأذونات**

تأكد من وجود دليل الموقع وتعيين الأذونات المناسبة:

```sh
sudo mkdir -p /var/www/html
sudo chown -R www-data:www-data /var/www/html
```

### 7. **إضافة ملف HTML تجريبي**

قم بإضافة ملف `index.html` إلى دليل الموقع:

```sh
echo "<h1>Hello, Monkey!</h1>" | sudo tee /var/www/html/index.html
```

### 8. **بدء خدمة Monkey**

تشغيل خادم Monkey باستخدام ملف التكوين:

```sh
sudo monkey -c /usr/local/etc/monkey/monkey.conf
```

إذا كنت تريد تشغيل Monkey كخدمة نظامية، يمكنك إنشاء وحدة خدمة systemd:

- قم بإنشاء ملف وحدة خدمة systemd:

  ```sh
  sudo nano /etc/systemd/system/monkey.service
  ```

- أضف المحتوى التالي:

  ```ini
  [Unit]
  Description=Monkey Web Server
  After=network.target

  [Service]
  ExecStart=/usr/local/sbin/monkey -c /usr/local/etc/monkey/monkey.conf
  Restart=on-failure

  [Install]
  WantedBy=multi-user.target
  ```

- ثم، قم بتمكين وبدء الخدمة:

  ```sh
  sudo systemctl enable monkey
  sudo systemctl start monkey
  ```

### 9. **التحقق من التثبيت**

افتح المتصفح وانتقل إلى عنوان IP الخاص بالخادم أو اسم النطاق الذي قمت بتكوينه في Monkey. يجب أن ترى صفحة الترحيب التي أضفتها في `index.html`.

باتباع هذه الخطوات، ستكون قد قمت بتثبيت وتكوين خادم Monkey على نظام Linux بنجاح.
