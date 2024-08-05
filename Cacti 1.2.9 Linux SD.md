لتثبيت Cacti 1.2.9 على نظام Linux باستخدام ملف tar.gz المقدم، اتبع الخطوات التالية. سأستخدم توزيعة Ubuntu كمثال، لكن الخطوات ستكون مشابهة في معظم توزيعات Linux.

### 1. **تحديث النظام**

تأكد من تحديث النظام لديك:

```sh
sudo apt update
sudo apt upgrade
```

### 2. **تثبيت المتطلبات الأساسية**

تثبيت Apache، MySQL، وPHP مع المكونات الضرورية لـ Cacti:

```sh
sudo apt install apache2 mysql-server php php-mysql php-xml php-gd php-ldap php-snmp php-json php-zip rrdtool snmp snmpd snmp-mibs-downloader
```

### 3. **تنزيل Cacti 1.2.9**

قم بتنزيل ملف tar.gz من الرابط:

```sh
wget https://files.cacti.net/cacti/test/linux/cacti-1.2.9.tar.gz
```

### 4. **استخراج الملفات**

قم باستخراج الملفات من الأرشيف:

```sh
tar -zxvf cacti-1.2.9.tar.gz
```

### 5. **نسخ ملفات Cacti إلى مجلد الويب**

انسخ الملفات إلى مجلد الويب الخاص بـ Apache:

```sh
sudo cp -R cacti-1.2.9 /var/www/html/cacti
```

### 6. **تكوين قاعدة البيانات**

- **تسجيل الدخول إلى MySQL:**

  ```sh
  sudo mysql -u root -p
  ```

- **إنشاء قاعدة بيانات واسم مستخدم لـ Cacti:**

  ```sql
  CREATE DATABASE cacti;
  CREATE USER 'cactiuser'@'localhost' IDENTIFIED BY 'your_password';
  GRANT ALL PRIVILEGES ON cacti.* TO 'cactiuser'@'localhost';
  FLUSH PRIVILEGES;
  EXIT;
  ```

- **استيراد البيانات الأولية:**

  ```sh
  sudo mysql -u cactiuser -p cacti < /var/www/html/cacti/cacti.sql
  ```

### 7. **تكوين PHP**

افتح ملف تكوين PHP وتأكد من إعداد `date.timezone` و `cgi.fix_pathinfo`:

```sh
sudo nano /etc/php/7.4/apache2/php.ini
```

ابحث عن السطور التالية واضبطها:

```ini
date.timezone = Your/Timezone
cgi.fix_pathinfo=0
```

### 8. **تكوين Apache**

- **إعداد Apache لخدمة Cacti:**

  قم بإنشاء ملف تكوين لموقع Cacti:

  ```sh
  sudo nano /etc/apache2/sites-available/cacti.conf
  ```

  أضف التكوين التالي:

  ```apache
  <VirtualHost *:80>
      ServerAdmin webmaster@localhost
      DocumentRoot /var/www/html/cacti
      ServerName your_domain_or_IP

      <Directory /var/www/html/cacti>
          Options Indexes FollowSymLinks
          AllowOverride All
          Require all granted
      </Directory>

      ErrorLog ${APACHE_LOG_DIR}/error.log
      CustomLog ${APACHE_LOG_DIR}/access.log combined
  </VirtualHost>
  ```

  ثم قم بتمكين الموقع وإعادة تشغيل Apache:

  ```sh
  sudo a2ensite cacti.conf
  sudo systemctl reload apache2
  ```

### 9. **إعداد Cacti**

- **إعداد أذونات الكتابة:**

  ```sh
  sudo chown -R www-data:www-data /var/www/html/cacti
  sudo chmod -R 775 /var/www/html/cacti/rra /var/www/html/cacti/log
  ```

- **إعداد cron jobs:**

  لإعداد المهام المجدولة لـ Cacti، أضف الأمر إلى crontab:

  ```sh
  sudo crontab -e
  ```

  ثم أضف السطر التالي:

  ```sh
  */5 * * * * www-data php /var/www/html/cacti/poller.php > /dev/null 2>&1
  ```

- **الانتقال إلى واجهة الويب:**

  افتح المتصفح وانتقل إلى `http://your_domain_or_IP/cacti` واتبع تعليمات الإعداد. خلال الإعداد، أدخل تفاصيل قاعدة البيانات التي قمت بإعدادها مسبقاً.

### 10. **التحقق من التثبيت**

بعد إتمام التثبيت والتكوين، يمكنك الوصول إلى لوحة التحكم الخاصة بـ Cacti عبر المتصفح وإضافة الأجهزة وتكوين المراقبة كما هو مطلوب.

باتباع هذه الخطوات، ستكون قد قمت بتثبيت Cacti 1.2.9 وتكوينه على نظام Linux بنجاح.
