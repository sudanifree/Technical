### كيفية بناء شبكة آمنة باستخدام Ubuntu Server وNagios

#### مقدمة
بناء شبكة آمنة باستخدام Ubuntu Server وأداة المراقبة Nagios يضمن مراقبة مستمرة وأمانًا عاليًا للنظام. Nagios هو نظام مفتوح المصدر لمراقبة الشبكات والخوادم، ويعتبر من الأدوات القوية في مجال إدارة الأنظمة. في هذا المقال، سنوضح الخطوات اللازمة لتثبيت وإعداد Ubuntu Server وNagios لبناء شبكة آمنة.

#### 1. **تثبيت وإعداد Ubuntu Server**

**أ. تحميل Ubuntu Server:**
- قم بتحميل صورة Ubuntu Server من الموقع الرسمي [Ubuntu](https://ubuntu.com/download/server).

**ب. تثبيت Ubuntu Server:**
- قم بتهيئة وسائط التثبيت (USB أو قرص DVD) وتثبيت النظام على الجهاز المخصص ليكون الخادم.
- اتبع التعليمات على الشاشة لإكمال عملية التثبيت، وحدد "Minimal Installation" لتقليل عدد الحزم المثبتة وتسهيل الإدارة والأمان.

**ج. تحديث النظام:**
- بعد التثبيت، قم بتحديث النظام للتأكد من أنك تستخدم أحدث البرامج وحزم الأمان.
```sh
sudo apt update && sudo apt upgrade -y
```

#### 2. **إعداد أمان الشبكة**

**أ. إعداد جدار الحماية (UFW):**
- UFW هو أداة سهلة الاستخدام لإدارة جدار الحماية في Ubuntu. قم بتمكينه وإعداد القواعد الأساسية.
```sh
sudo ufw allow OpenSSH
sudo ufw enable
sudo ufw status
```

**ب. إعداد SSH:**
- تأكد من أن اتصال SSH آمن عن طريق تعطيل تسجيل الدخول باستخدام كلمة المرور وتفعيل استخدام مفاتيح SSH.
```sh
sudo nano /etc/ssh/sshd_config
# قم بتغيير الإعدادات التالية:
PasswordAuthentication no
PermitRootLogin no
```
- أعد تشغيل خدمة SSH لتطبيق التغييرات.
```sh
sudo systemctl restart ssh
```

#### 3. **تثبيت وإعداد Nagios**

**أ. تثبيت المتطلبات الأساسية:**
- Nagios يتطلب خادم ويب (Apache)، PHP، وقاعدة بيانات (MySQL/MariaDB).
```sh
sudo apt install apache2 libapache2-mod-php php php-gd libgd-dev unzip
sudo apt install build-essential libgd-dev openssl libssl-dev
sudo apt install xinetd
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
- قم بتحميل Nagios Core من الموقع الرسمي وتثبيته.
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

#### 4. **مراقبة الشبكة باستخدام Nagios**

**أ. إعداد Nagios لمراقبة الأجهزة:**
- أضف الأجهزة التي ترغب في مراقبتها عن طريق تعديل ملفات التكوين في `/usr/local/nagios/etc/objects/`.
```sh
sudo nano /usr/local/nagios/etc/objects/localhost.cfg
```
- أضف تعريفات الخدمات والأجهزة وفقًا لاحتياجاتك.

**ب. التحقق من إعدادات Nagios:**
- تحقق من صحة إعدادات Nagios باستخدام الأمر التالي:
```sh
sudo /usr/local/nagios/bin/nagios -v /usr/local/nagios/etc/nagios.cfg
```

**ج. إعداد التنبيهات:**
- قم بإعداد التنبيهات في Nagios لإعلامك عند حدوث أي مشكلة أو تجاوز عتبات معينة.

### خاتمة
بناء شبكة آمنة باستخدام Ubuntu Server وNagios يتطلب إعدادات دقيقة وخطوات محسوبة لضمان الأمان والمراقبة الفعالة. من خلال اتباع الخطوات المذكورة أعلاه، يمكنك إنشاء بيئة شبكية آمنة وموثوقة تمكنك من مراقبة وتحليل الأداء بشكل مستمر. هذا الإعداد يوفر أساسًا قويًا يمكن البناء عليه لتلبية احتياجات الأمان والمراقبة المتقدمة في شبكتك.
