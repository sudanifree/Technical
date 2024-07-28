لإنشاء نظام إلكتروني يعرض معلومات دقة الشاشة ويحدد ما إذا كانت الشاشة في وضع أفقي (Landscape) أو عمودي (Portrait) باستخدام الذكاء الاصطناعي والبرمجيات الحرة ومفتوحة المصدر، مع استخدام PHP وMySQL وHTML، يمكنك اتباع الخطوات التالية:

### متطلبات المشروع
- PHP
- MySQL
- HTML
- JavaScript
- مكتبة TensorFlow.js للذكاء الاصطناعي

### خطوات التنفيذ

#### 1. إعداد قاعدة البيانات
قم بإنشاء قاعدة بيانات MySQL لتخزين معلومات دقة الشاشة ووضع الشاشة.

```sql
CREATE DATABASE screen_info;

USE screen_info;

CREATE TABLE screen_data (
    id INT AUTO_INCREMENT PRIMARY KEY,
    width INT NOT NULL,
    height INT NOT NULL,
    orientation VARCHAR(10) NOT NULL,
    user_agent VARCHAR(255) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

#### 2. إعداد بيئة التطوير
تأكد من تثبيت XAMPP أو LAMP لتشغيل Apache و MySQL على جهازك المحلي.

#### 3. إنشاء صفحة HTML لجمع بيانات الشاشة
قم بإنشاء صفحة HTML تحتوي على JavaScript لاكتشاف دقة الشاشة وتحديد وضع الشاشة (أفقي أو عمودي) وإرسال البيانات إلى الخادم.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Screen Info</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <h1>Screen Information</h1>
    <p id="info"></p>

    <script>
        $(document).ready(function() {
            var width = screen.width;
            var height = screen.height;
            var orientation = width > height ? 'Landscape' : 'Portrait';
            var userAgent = navigator.userAgent;
            $('#info').text('Width: ' + width + ', Height: ' + height + ', Orientation: ' + orientation);

            $.post('save_screen_info.php', {
                width: width,
                height: height,
                orientation: orientation,
                user_agent: userAgent
            }, function(response) {
                console.log('Data saved: ' + response);
            });
        });
    </script>
</body>
</html>
```

#### 4. إنشاء ملف PHP لحفظ البيانات في قاعدة البيانات
قم بإنشاء ملف PHP يسمى `save_screen_info.php` لتلقي البيانات من صفحة HTML وحفظها في قاعدة البيانات.

```php
<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "screen_info";

$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$width = $_POST['width'];
$height = $_POST['height'];
$orientation = $_POST['orientation'];
$user_agent = $_POST['user_agent'];

$sql = "INSERT INTO screen_data (width, height, orientation, user_agent) VALUES ('$width', '$height', '$orientation', '$user_agent')";

if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

$conn->close();
?>
```

#### 5. استخدام الذكاء الاصطناعي لتحليل البيانات (اختياري)
يمكنك استخدام مكتبة TensorFlow.js لتحليل بيانات الشاشة وعرض توصيات أو تقارير بناءً على التحليلات.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Screen Info AI Analysis</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/@tensorflow/tfjs"></script>
</head>
<body>
    <h1>Screen Information</h1>
    <p id="info"></p>
    <p id="analysis"></p>

    <script>
        $(document).ready(function() {
            var width = screen.width;
            var height = screen.height;
            var orientation = width > height ? 'Landscape' : 'Portrait';
            var userAgent = navigator.userAgent;
            $('#info').text('Width: ' + width + ', Height: ' + height + ', Orientation: ' + orientation);

            $.post('save_screen_info.php', {
                width: width,
                height: height,
                orientation: orientation,
                user_agent: userAgent
            }, function(response) {
                console.log('Data saved: ' + response);
            });

            // AI Analysis Example
            const model = tf.sequential();
            model.add(tf.layers.dense({units: 1, inputShape: [1]}));
            model.compile({loss: 'meanSquaredError', optimizer: 'sgd'});

            const xs = tf.tensor2d([width], [1, 1]);
            const ys = tf.tensor2d([height], [1, 1]);

            model.fit(xs, ys, {epochs: 10}).then(() => {
                let prediction = model.predict(tf.tensor2d([width], [1, 1]));
                prediction.print();
                prediction.array().then(array => {
                    $('#analysis').text('Predicted Height: ' + array[0][0]);
                });
            });
        });
    </script>
</body>
</html>
```

### الخطوات النهائية
1. **اختبار النظام**: تأكد من أن النظام يعمل بشكل صحيح عن طريق فتح صفحة HTML في متصفحك.
2. **تحسين الأمان**: قم بتحسين الأمان عن طريق استخدام تقنيات مثل إعدادات CORS، والتحقق من المدخلات لمنع الهجمات مثل SQL Injection.
3. **نشر النظام**: يمكنك نشر النظام على خادم ويب إذا كنت ترغب في الوصول إليه من أجهزة متعددة.

بهذه الطريقة، يمكنك بناء نظام إلكتروني لعرض معلومات دقة الشاشة وتحديد وضع الشاشة (أفقي أو عمودي) باستخدام الذكاء الاصطناعي والبرمجيات الحرة ومفتوحة المصدر، مع استخدام PHP، MySQL، وHTML.
