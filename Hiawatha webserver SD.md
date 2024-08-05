لتثبيت خادم Hiawatha على نظام Linux، يمكنك اتباع الخطوات التالية. سنستخدم الأوامر لتنفيذ التثبيت على توزيعة Ubuntu كمثال:

### الخطوات:

1. **تحديث النظام:**

   قبل البدء في تثبيت Hiawatha، من الجيد التأكد من تحديث النظام لديك:

   ```sh
   sudo apt update
   sudo apt upgrade
   ```

2. **تثبيت المتطلبات الأساسية:**

   تثبيت الأدوات الضرورية لتجميع البرمجيات:

   ```sh
   sudo apt install build-essential cmake
   ```

3. **تنزيل Hiawatha:**

   قم بتنزيل أحدث نسخة من Hiawatha من الموقع الرسمي:

   ```sh
   wget https://www.hiawatha-webserver.org/files/hiawatha-11.3.tar.gz
   ```

4. **فك ضغط الملف:**

   بعد تنزيل الملف، قم بفك ضغطه:

   ```sh
   tar xvf hiawatha-11.3.tar.gz
   cd hiawatha-11.3
   ```

5. **تجميع وتثبيت Hiawatha:**

   قم بإنشاء دليل البناء وتجميع Hiawatha:

   ```sh
   mkdir build
   cd build
   cmake ..
   make
   sudo make install
   ```

6. **تكوين Hiawatha:**

   بعد التثبيت، تحتاج إلى تكوين Hiawatha. يمكن العثور على ملفات التكوين في `/usr/local/etc/hiawatha`:

   ```sh
   sudo nano /usr/local/etc/hiawatha/hiawatha.conf
   ```

   قم بتعديل ملف التكوين حسب احتياجاتك. مثلاً، يمكنك إعداد الموقع الافتراضي بهذه الطريقة:

   ```conf
   Binding {
       Port = 80
       Interface = 0.0.0.0
   }

   Hostname = example.com

   WebsiteRoot = /var/www/html
   StartFile = index.html
   AccessLogfile = /var/log/hiawatha/access.log
   ErrorLogfile = /var/log/hiawatha/error.log
   ```

7. **إنشاء دليل الموقع وتعيين الأذونات:**

   تأكد من وجود دليل الموقع وتعيين الأذونات المناسبة:

   ```sh
   sudo mkdir -p /var/www/html
   sudo chown -R www-data:www-data /var/www/html
   ```

8. **إضافة ملف HTML تجريبي:**

   لإنشاء صفحة ويب تجريبية، أضف ملف `index.html` إلى دليل الموقع:

   ```sh
   echo "<h1>Hello, Hiawatha!</h1>" | sudo tee /var/www/html/index.html
   ```

9. **بدء خدمة Hiawatha:**

   يمكنك بدء خدمة Hiawatha يدويًا:

   ```sh
   sudo hiawatha
   ```

   أو يمكنك إنشاء وحدة خدمة لـ systemd لبدء الخدمة تلقائيًا:

   ```sh
   sudo nano /etc/systemd/system/hiawatha.service
   ```

   أضف المحتوى التالي:

   ```ini
   [Unit]
   Description=Hiawatha Web Server
   After=network.target

   [Service]
   ExecStart=/usr/local/sbin/hiawatha
   ExecReload=/bin/kill -HUP $MAINPID
   KillMode=process
   Restart=on-failure

   [Install]
   WantedBy=multi-user.target
   ```

   ثم، تمكين وبدء الخدمة:

   ```sh
   sudo systemctl enable hiawatha
   sudo systemctl start hiawatha
   ```

10. **التحقق من التثبيت:**

    افتح المتصفح وانتقل إلى عنوان IP الخاص بالخادم أو اسم النطاق الذي قمت بتكوينه في Hiawatha. يجب أن ترى صفحة الترحيب التي أضفتها في `index.html`.

باتباع هذه الخطوات، ستكون قد قمت بتثبيت وتكوين خادم Hiawatha على نظام Linux بنجاح.
