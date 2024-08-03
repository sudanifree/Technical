لضمان دعم **Unicode** للغات متعددة مثل **العربية** و**الإنجليزية**، وتنظيم الطباعة مع دعم الاتجاهات الثنائية (bidirectional) عبر **CSS**، وإعداد **PHP** و**MySQL** و**HTML** بشكل صحيح، يمكن اتباع الخطوات التالية:

### 1. **إعداد UTF-8 في MySQL**

تأكد من أن قاعدة البيانات والجداول تستخدم **UTF-8**:
```sql
CREATE DATABASE my_database CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE TABLE my_table (
    id INT PRIMARY KEY AUTO_INCREMENT,
    text_column VARCHAR(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci
);
```

### 2. **إعداد UTF-8 في PHP**

تأكد من تعيين الترميز عند الاتصال بقاعدة البيانات:
```php
$dsn = 'mysql:host=localhost;dbname=my_database;charset=utf8mb4';
$pdo = new PDO($dsn, 'user', 'password');
```

تعيين رأس المحتوى في PHP:
```php
header('Content-Type: text/html; charset=utf-8');
```

### 3. **إعداد UTF-8 في HTML**

حدد الترميز في `<head>` من مستند HTML:
```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Bidirectional Unicode Example</title>
    <link rel="stylesheet" href="print.css" media="print">
</head>
<body>
    <h1>مرحبا بالعالم! Hello World!</h1>
    <p>هذا مثال على النصوص المطبوعة باستخدام Unicode. This is an example of printed text using Unicode.</p>
</body>
</html>
```

### 4. **إضافة طباعة CSS (Print CSS) مع دعم الاتجاهات الثنائية**

أنشئ ملف CSS للطباعة لدعم النصوص ذات الاتجاهات الثنائية (من اليمين لليسار ومن اليسار لليمين):

**ملف `print.css`**:
```css
@media print {
    body {
        font-family: Arial, sans-serif;
        direction: ltr; /* الاتجاه الافتراضي للطباعة بالإنجليزية */
        text-align: left; /* المحاذاة لليسار للطباعة بالإنجليزية */
    }

    /* تخصيص النصوص العربية */
    body[lang="ar"] {
        direction: rtl; /* من اليمين لليسار للطباعة بالعربية */
        text-align: right; /* المحاذاة لليمين للطباعة بالعربية */
    }

    /* تحسين النصوص متعددة اللغات */
    .bidi {
        direction: ltr; /* الافتراضي للنصوص من اليسار لليمين */
        unicode-bidi: embed; /* لدعم النصوص من اتجاهات مختلفة في نفس الفقرة */
    }
}
```

### 5. **اختبار الطباعة على أجهزة مختلفة**

#### **Huawei Y5 2019**
- استخدم متصفح **Chrome** أو **Firefox** لاختبار الطباعة. تحقق من أن النصوص تظهر بشكل صحيح، لا سيما النصوص ذات الاتجاهات الثنائية.

#### **iPhone X**
- استخدم متصفح **Safari** على **iPhone X** لاختبار الطباعة. تأكد من أن الطباعة تتناسب مع النصوص باللغة العربية والإنجليزية.

#### **itel 2160**
- نظرًا لأن **itel 2160** هو هاتف بسيط بشاشة صغيرة، تأكد من أن الطباعة تتوافق مع الشاشات الصغيرة عبر اختبار الطباعة باستخدام متصفح يدعمه الهاتف.

### ملخص:

- **MySQL**: استخدم **utf8mb4** لتخزين النصوص متعددة اللغات.
- **PHP**: تعيين الترميز إلى **UTF-8** عند الاتصال بقاعدة البيانات وإرسال رأس المحتوى.
- **HTML**: تحديد الترميز باستخدام `<meta charset="UTF-8">` وربط **CSS** للطباعة.
- **CSS للطباعة**: تخصيص الأنماط للطباعة باستخدام `direction` و`unicode-bidi` لدعم النصوص ذات الاتجاهات الثنائية.

باتباع هذه الخطوات، ستتمكن من التعامل مع النصوص بالعربية والإنجليزية بكفاءة وضمان عرضها بشكل صحيح عند الطباعة عبر أجهزة متعددة.
