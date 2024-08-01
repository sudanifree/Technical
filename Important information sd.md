لبناء نظام إلكتروني يقوم تلقائيًا بجمع البيانات حول الأجهزة باستخدام الذكاء الاصطناعي والبرمجيات الحرة والمفتوحة المصدر، وباستخدام PHP و MySQL و HTML، يمكن اتباع الخطوات التالية:

1. **إعداد البيئة**:
   - تثبيت خادم الويب (مثل Apache) وقاعدة البيانات (مثل MySQL).
   - تثبيت PHP.

2. **قاعدة البيانات**:
   - إنشاء قاعدة بيانات تحتوي على جدول لتخزين المعلومات المتعلقة بالأجهزة. يمكن أن يكون الجدول بالشكل التالي:

     ```sql
     CREATE TABLE devices (
         id INT AUTO_INCREMENT PRIMARY KEY,
         ip_address VARCHAR(15) NOT NULL,
         os VARCHAR(50),
         chipset VARCHAR(50),
         cpu VARCHAR(50),
         gpu VARCHAR(50),
         battery_type VARCHAR(50),
         port INT,
         dhcp BOOLEAN
     );
     ```

3. **جمع البيانات**:
   - كتابة سكربتات PHP لجمع البيانات من الأجهزة الموجودة على الشبكة باستخدام الذكاء الاصطناعي و DHCP لتحديد العناوين.

     مثال لسكربت لجمع المعلومات من جهاز:
     ```php
     <?php
     function getDeviceInfo($ip) {
         // هنا نستخدم أدوات مثل nmap لجمع المعلومات عن الأجهزة
         // يمكن أيضاً استخدام مكتبات معينة في PHP لجمع هذه المعلومات
         // مثلاً استخدام exec لتنفيذ أوامر النظام والحصول على النتائج
         $os = exec("nmap -O $ip | grep 'OS detection'");
         $cpu = exec("lshw -class cpu | grep product");
         $gpu = exec("lshw -class display | grep product");
         $battery = exec("upower -i /org/freedesktop/UPower/devices/battery_BAT0 | grep 'model'");
         
         return [
             'ip_address' => $ip,
             'os' => $os,
             'chipset' => '', // جمع معلومات الشرائح يعتمد على نظام التشغيل والأدوات المتاحة
             'cpu' => $cpu,
             'gpu' => $gpu,
             'battery_type' => $battery,
             'port' => '', // يمكن جمع معلومات المنافذ من خلال أدوات مثل nmap
             'dhcp' => true // أو false بناءً على النتيجة
         ];
     }
     ```

4. **تخزين البيانات**:
   - تخزين المعلومات المجمعة في قاعدة البيانات.

     مثال لتخزين البيانات في قاعدة البيانات:
     ```php
     function storeDeviceInfo($deviceInfo) {
         $conn = new mysqli("hostname", "username", "password", "database");
         if ($conn->connect_error) {
             die("Connection failed: " . $conn->connect_error);
         }

         $stmt = $conn->prepare("INSERT INTO devices (ip_address, os, chipset, cpu, gpu, battery_type, port, dhcp) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
         $stmt->bind_param("sssssssi",
             $deviceInfo['ip_address'],
             $deviceInfo['os'],
             $deviceInfo['chipset'],
             $deviceInfo['cpu'],
             $deviceInfo['gpu'],
             $deviceInfo['battery_type'],
             $deviceInfo['port'],
             $deviceInfo['dhcp']
         );

         $stmt->execute();
         $stmt->close();
         $conn->close();
     }
     ```

5. **واجهة المستخدم**:
   - إنشاء واجهة HTML لعرض المعلومات المجمعة.

     مثال لصفحة HTML لعرض المعلومات:
     ```html
     <!DOCTYPE html>
     <html>
     <head>
         <title>Device Information</title>
     </head>
     <body>
         <h1>Device Information</h1>
         <table border="1">
             <tr>
                 <th>IP Address</th>
                 <th>OS</th>
                 <th>Chipset</th>
                 <th>CPU</th>
                 <th>GPU</th>
                 <th>Battery Type</th>
                 <th>Port</th>
                 <th>DHCP</th>
             </tr>
             <?php
             $conn = new mysqli("hostname", "username", "password", "database");
             $result = $conn->query("SELECT * FROM devices");
             while($row = $result->fetch_assoc()) {
                 echo "<tr>
                     <td>{$row['ip_address']}</td>
                     <td>{$row['os']}</td>
                     <td>{$row['chipset']}</td>
                     <td>{$row['cpu']}</td>
                     <td>{$row['gpu']}</td>
                     <td>{$row['battery_type']}</td>
                     <td>{$row['port']}</td>
                     <td>" . ($row['dhcp'] ? 'Yes' : 'No') . "</td>
                 </tr>";
             }
             $conn->close();
             ?>
         </table>
     </body>
     </html>
     ```

6. **التنفيذ التلقائي**:
   - يمكن استخدام مهام مجدولة (cron jobs) لتشغيل سكربت جمع البيانات بشكل دوري.

بذلك، تكون قد أنشأت نظامًا لجمع وتخزين وعرض معلومات الأجهزة باستخدام البرمجيات الحرة والمفتوحة المصدر.
