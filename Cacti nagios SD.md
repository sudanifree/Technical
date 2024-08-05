### مراقبة أداء الشبكات باستخدام Cacti وNagios: دليل شامل

#### مقدمة
في عصر التكنولوجيا المتقدمة، أصبحت مراقبة أداء الشبكات أمرًا حيويًا لضمان استمرارية الخدمات والكفاءة التشغيلية. يوفر كل من Cacti وNagios أدوات قوية لمراقبة وتحليل أداء الشبكات والبنية التحتية لتكنولوجيا المعلومات. في هذا الدليل، سنتناول كيفية إعداد واستخدام Cacti وNagios لمراقبة الشبكات بشكل شامل.

#### 1. **ما هو Cacti؟**

**أ. مقدمة:**
Cacti هو نظام مفتوح المصدر يستخدم لتخزين البيانات الرسومية لمراقبة أداء الشبكات والأجهزة. يعتمد على RRDTool لإنشاء الرسوم البيانية ويمكنه تتبع استخدام النطاق الترددي، وسرعة المعالجة، والذاكرة، وغيرها من المقاييس.

**ب. الميزات:**
- دعم SNMP لجمع البيانات من الأجهزة المختلفة.
- إنشاء رسوم بيانية مخصصة.
- لوحة تحكم سهلة الاستخدام.

#### 2. **ما هو Nagios؟**

**أ. مقدمة:**
Nagios هو نظام مفتوح المصدر لمراقبة الشبكات والبنية التحتية لتكنولوجيا المعلومات. يمكنه مراقبة حالة الخوادم، والخدمات، والأجهزة، وتوفير تنبيهات في حالة حدوث أي مشكلة.

**ب. الميزات:**
- مراقبة الشبكات والخوادم.
- تنبيهات فورية عبر البريد الإلكتروني والرسائل النصية.
- تقارير مفصلة وتحليلات الأداء.

#### 3. **إعداد البيئة لتثبيت Cacti وNagios**

**أ. متطلبات النظام:**
- خادم يعمل بنظام تشغيل Linux (مثل Ubuntu Server).
- خادم ويب (Apache).
- قاعدة بيانات (MySQL/MariaDB).
- PHP.

**ب. تحديث النظام:**
تأكد من أن النظام محدث بأحدث الحزم الأمنية.
```sh
sudo apt update && sudo apt upgrade -y
```

#### 4. **تثبيت وإعداد Cacti**

**أ. تثبيت المتطلبات الأساسية:**
```sh
sudo apt install apache2 mysql-server php php-mysql libapache2-mod-php php-xml php-mbstring php-gd php-snmp rrdtool snmp snmpd -y
```

**ب. إعداد قاعدة البيانات:**
- قم بإنشاء قاعدة بيانات جديدة لـ Cacti.
```sh
sudo mysql -u root -p
CREATE DATABASE cacti;
GRANT ALL PRIVILEGES ON cacti.* TO 'cactiuser'@'localhost' IDENTIFIED BY 'password';
FLUSH PRIVILEGES;
EXIT;
```

**ج. تثبيت Cacti:**
- قم بتحميل وتثبيت Cacti.
```sh
wget https://www.cacti.net/downloads/cacti-latest.tar.gz
tar -zxvf cacti-latest.tar.gz
sudo mv cacti-*/ /var/www/html/cacti
sudo chown -R www-data:www-data /var/www/html/cacti/
```

**د. إعداد Cacti:**
- قم بتحرير ملف التكوين للإشارة إلى قاعدة البيانات.
```sh
sudo nano /var/www/html/cacti/include/config.php
# أضف معلومات قاعدة البيانات
```

**هـ. إعداد Cron Job:**
- قم بإعداد مهمة مجدولة لتحديث البيانات.
```sh
sudo crontab -e
# أضف المهمة التالية
*/5 * * * * www-data php /var/www/html/cacti/poller.php > /dev/null 2>&1
```

#### 5. **تثبيت وإعداد Nagios**

**أ. تثبيت المتطلبات الأساسية:**
```sh
sudo apt install -y autoconf gcc libc6 libmcrypt-dev make libssl-dev wget apache2 php libapache2-mod-php php-gd libgd-dev
```

**ب. إنشاء مستخدم Nagios:**
```sh
sudo useradd nagios
sudo passwd nagios
sudo groupadd nagcmd
sudo usermod -a -G nagcmd nagios
sudo usermod -a -G nagcmd www-data
```

**ج. تحميل وتثبيت Nagios:**
- قم بتحميل أحدث إصدار من Nagios وتثبيته.
```sh
cd /tmp
wget https://assets.nagios.com/downloads/nagioscore/releases/nagios-4.4.6.tar.gz
tar -zxvf nagios-4.4.6.tar.gz
cd nagios-4.4.6/
sudo ./configure --with-command-group=nagcmd
sudo make all
sudo make install
sudo make install-commandmode
sudo make install-init
sudo make install-config
sudo make install-webconf
```

**د. إعداد Apache:**
- قم بإنشاء مستخدم لتسجيل الدخول إلى واجهة الويب.
```sh
sudo htpasswd -c /usr/local/nagios/etc/htpasswd.users nagiosadmin
```
- أعد تشغيل Apache لتطبيق التغييرات.
```sh
sudo systemctl restart apache2
```

**هـ. تثبيت وإعداد Nagios Plugins:**
- قم بتحميل وتثبيت الإضافات اللازمة لـ Nagios.
```sh
cd /tmp
wget https://nagios-plugins.org/download/nagios-plugins-2.3.3.tar.gz
tar -zxvf nagios-plugins-2.3.3.tar.gz
cd nagios-plugins-2.3.3/
sudo ./configure --with-nagios-user=nagios --with-nagios-group=nagios
sudo make
sudo make install
```

**و. إعداد خدمة Nagios:**
- قم بتمكين خدمة Nagios وتشغيلها.
```sh
sudo systemctl enable nagios
sudo systemctl start nagios
```

#### 6. **مراقبة الشبكة باستخدام Cacti وNagios**

**أ. إعداد Cacti لمراقبة الأجهزة:**
- أضف الأجهزة والمضيفين من خلال واجهة الويب الخاصة بـ Cacti.
- حدد القوالب والمخططات المناسبة لكل جهاز.

**ب. إعداد Nagios لمراقبة الأجهزة:**
- قم بتعديل ملفات التكوين في `/usr/local/nagios/etc/objects/` لإضافة المضيفين والخدمات المراد مراقبتها.
```sh
sudo nano /usr/local/nagios/etc/objects/localhost.cfg
# أضف تعريفات المضيفين والخدمات
```
- تحقق من صحة إعدادات Nagios باستخدام الأمر التالي:
```sh
sudo /usr/local/nagios/bin/nagios -v /usr/local/nagios/etc/nagios.cfg
```
- أعد تشغيل خدمة Nagios لتطبيق التغييرات.
```sh
sudo systemctl restart nagios
```

### خاتمة
باستخدام Cacti وNagios، يمكن للمؤسسات مراقبة أداء شبكاتها بشكل فعال وشامل. من خلال إعداد Cacti لجمع البيانات الرسومية وتحليلها، واستخدام Nagios لمراقبة الحالة وإصدار التنبيهات، يمكن تحقيق إدارة أفضل للشبكات وضمان استمرارية العمليات. هذا الدليل يوفر الخطوات الأساسية للبدء، ويمكن التوسع في استخدام الميزات المتقدمة لتحقيق مراقبة أكثر تفصيلاً وكفاءة.
