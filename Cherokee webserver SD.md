لتثبيت خادم الويب Cherokee على نظام Linux، يمكنك اتباع الخطوات التالية. سنستخدم توزيعة Ubuntu كمثال، لكن يمكن تعديل الخطوات لتناسب توزيعات أخرى.

### الخطوات:

1. **تحديث النظام:**

   تأكد من تحديث قائمة الحزم لديك:

   ```sh
   sudo apt update
   sudo apt upgrade
   ```

2. **تثبيت المتطلبات الأساسية:**

   تثبيت الأدوات اللازمة لبناء البرامج من المصدر:

   ```sh
   sudo apt install build-essential autoconf libtool
   ```

3. **تنزيل Cherokee:**

   يمكنك تنزيل أحدث نسخة من Cherokee من الموقع الرسمي أو عبر Git:

   - **من الموقع الرسمي:**
     قم بزيارة [موقع Cherokee الرسمي](https://cherokee-project.com/download) واختر النسخة التي تريد تنزيلها، ثم استخدم `wget` لتنزيلها:

     ```sh
     wget https://github.com/cherokee/cherokee/releases/download/1.2.104/cherokee-1.2.104.tar.gz
     ```

   - **من GitHub:**
     قم بتنزيلها من مستودع GitHub الخاص بـ Cherokee:

     ```sh
     git clone https://github.com/cherokee/cherokee.git
     cd cherokee
     ```

4. **فك ضغط الملف (إذا قمت بتحميله من الموقع الرسمي):**

   بعد تنزيل الملف، قم بفك ضغطه:

   ```sh
   tar xvf cherokee-1.2.104.tar.gz
   cd cherokee-1.2.104
   ```

5. **بناء وتثبيت Cherokee:**

   اتبع الخطوات التالية لبناء وتثبيت Cherokee:

   ```sh
   ./configure
   make
   sudo make install
   ```

6. **تكوين Cherokee:**

   بعد التثبيت، يمكنك تكوين Cherokee. قم بإنشاء ملف تكوين أو استخدم الواجهة الرسومية لتكوين الخادم.

   **مثال على تكوين بسيط:**

   قم بإنشاء ملف تكوين:

   ```sh
   sudo nano /usr/local/etc/cherokee/cherokee.conf
   ```

   أضف التكوين الأساسي:

   ```ini
   server "default" {
       listen "0.0.0.0" port 80
       document-root "/var/www/html"
   }
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
   echo "<h1>Hello, Cherokee!</h1>" | sudo tee /var/www/html/index.html
   ```

9. **بدء خدمة Cherokee:**

   قم بتشغيل خدمة Cherokee:

   ```sh
   sudo cherokee -f /usr/local/etc/cherokee/cherokee.conf
   ```

   أو إذا كنت ترغب في استخدام Cherokee كخدمة نظامية:

   - قم بإنشاء ملف وحدة خدمة systemd:

     ```sh
     sudo nano /etc/systemd/system/cherokee.service
     ```

   - أضف المحتوى التالي:

     ```ini
     [Unit]
     Description=Cherokee Web Server
     After=network.target

     [Service]
     ExecStart=/usr/local/sbin/cherokee -f /usr/local/etc/cherokee/cherokee.conf
     Restart=on-failure

     [Install]
     WantedBy=multi-user.target
     ```

   - ثم، قم بتمكين وبدء الخدمة:

     ```sh
     sudo systemctl enable cherokee
     sudo systemctl start cherokee
     ```

10. **التحقق من التثبيت:**

    افتح المتصفح وانتقل إلى عنوان IP الخاص بالخادم أو اسم النطاق الذي قمت بتكوينه في Cherokee. يجب أن ترى صفحة الترحيب التي أضفتها في `index.html`.

باتباع هذه الخطوات، ستكون قد قمت بتثبيت وتكوين خادم Cherokee على نظام Linux بنجاح.
