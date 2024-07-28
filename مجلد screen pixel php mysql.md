لإنشاء نظام يستخدم PHP وMySQL لإنشاء مجلدات بأسماء مستمدة من قيم العرض (`width`)، الارتفاع (`height`)، والوضع (`orientation`)، يمكن اتباع الخطوات التالية:

### الخطوات:

1. **إعداد قاعدة البيانات:**

    أولاً، تأكد من أن لديك قاعدة بيانات MySQL جاهزة. يمكنك استخدام الأمر التالي لإنشاء قاعدة بيانات:

    ```sql
    CREATE DATABASE screen_info;
    ```

    ثم قم بإنشاء جدول لتخزين بيانات الشاشة:

    ```sql
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

2. **إعداد ملف PHP لجمع البيانات:**

    قم بإنشاء ملف PHP لجمع بيانات الشاشة وحفظها في قاعدة البيانات وإنشاء المجلدات:

    ```php
    <?php
    // الاتصال بقاعدة البيانات
    $servername = "localhost";
    $username = "root";
    $password = "yourpassword";
    $dbname = "screen_info";

    // إنشاء الاتصال
    $conn = new mysqli($servername, $username, $password, $dbname);

    // التحقق من الاتصال
    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }

    // التحقق من وجود البيانات في طلب POST
    if ($_SERVER["REQUEST_METHOD"] == "POST") {
        $width = intval($_POST['width']);
        $height = intval($_POST['height']);
        $orientation = $_POST['orientation'];
        $user_agent = $_POST['user_agent'];

        // إنشاء اسم المجلد
        $folder_name = $width . $height . strtolower($orientation[0]) . ".html";
        $folder_path = "user_folders/" . $folder_name;

        // إنشاء المجلد إذا لم يكن موجودًا
        if (!file_exists($folder_path)) {
            mkdir($folder_path, 0777, true);
        }

        // إدراج البيانات في قاعدة البيانات
        $stmt = $conn->prepare("INSERT INTO screen_data (width, height, orientation, user_agent) VALUES (?, ?, ?, ?)");
        $stmt->bind_param("iiss", $width, $height, $orientation, $user_agent);

        if ($stmt->execute()) {
            echo "Data saved and folder created: " . $folder_path;
        } else {
            echo "Error: " . $stmt->error;
        }

        $stmt->close();
    }

    $conn->close();
    ?>
    ```

3. **إنشاء صفحة HTML لجمع بيانات الشاشة:**

    قم بإنشاء صفحة HTML مع جافا سكريبت لجمع بيانات الشاشة وإرسالها إلى ملف PHP:

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

4. **إنشاء مجلد `user_folders`**:

    تأكد من وجود مجلد `user_folders` في نفس مسار ملف PHP ويمكنك إنشاء المجلد باستخدام الأمر التالي في الجهاز:

    ```bash
    mkdir user_folders
    chmod 777 user_folders
    ```

### تشغيل النظام:

- ضع ملفات PHP وHTML في مسار الويب الخاص بك (مثل `htdocs` أو `www` على حسب خادم الويب الذي تستخدمه).
- افتح متصفحك وانتقل إلى ملف HTML لاختبار النظام (`http://localhost/path_to_file.html`).

### الشرح:

- **جمع البيانات**: تستخدم جافا سكريبت لجمع بيانات الشاشة (العرض، الارتفاع، والوضع) وإرسالها إلى ملف PHP باستخدام AJAX.
- **حفظ البيانات**: يقوم ملف PHP بحفظ البيانات في قاعدة البيانات وإنشاء مجلد بأسماء مستمدة من العرض، الارتفاع، والوضع.

بهذه الطريقة، يمكنك إنشاء نظام يقوم بجمع بيانات الشاشة وحفظها في قاعدة البيانات وإنشاء مجلدات بأسماء مستمدة من قيم العرض، الارتفاع، والوضع باستخدام PHP وMySQL. 
