لتطوير نظام إلكتروني للعدادات الذكية يقيس الكمية المطلوبة من الكهرباء للـ 27 ولاية في السودان تلقائيًا باستخدام الذكاء الاصطناعي، البرمجيات الحرة ومفتوحة المصدر بنسبة 100٪، Django، Python، MySQL، HTML، واجهة عربية باستخدام Unicode، يمكن اتباع الخطوات التالية:

### المتطلبات:
- **Django**: إطار عمل ويب لبناء تطبيق الويب.
- **Python**: لغة البرمجة الأساسية للنظام.
- **MySQL**: قاعدة البيانات لتخزين البيانات.
- **HTML/CSS/JavaScript**: لتطوير واجهة المستخدم.
- **TensorFlow أو Scikit-learn**: لتحليل البيانات والتنبؤ بالاستهلاك.
- **Geolocation API**: للحصول على الموقع الجغرافي.

### الخطوات التفصيلية:

#### 1. إعداد البيئة:
- تثبيت Django وMySQL:
    ```bash
    pip install django mysqlclient
    ```

- إعداد مشروع Django:
    ```bash
    django-admin startproject smart_meter
    cd smart_meter
    django-admin startapp meter
    ```

#### 2. إعداد قاعدة البيانات:
- إعداد قاعدة البيانات في MySQL:
    ```sql
    CREATE DATABASE smart_meter_db;

    CREATE TABLE states (
        id INT AUTO_INCREMENT PRIMARY KEY,
        state_name VARCHAR(255) UNIQUE
    );

    CREATE TABLE electrical_devices (
        id INT AUTO_INCREMENT PRIMARY KEY,
        device_name VARCHAR(255),
        power_rating FLOAT,
        expected_usage_hours FLOAT,
        state_id INT,
        FOREIGN KEY (state_id) REFERENCES states(id)
    );

    CREATE TABLE energy_consumption (
        id INT AUTO_INCREMENT PRIMARY KEY,
        timestamp DATETIME,
        device_id INT,
        power_consumed FLOAT,
        state_id INT,
        FOREIGN KEY (device_id) REFERENCES electrical_devices(id),
        FOREIGN KEY (state_id) REFERENCES states(id)
    );
    ```

- إعداد الاتصال بقاعدة البيانات في `settings.py`:
    ```python
    DATABASES = {
        'default': {
            'ENGINE': 'django.db.backends.mysql',
            'NAME': 'smart_meter_db',
            'USER': 'your_mysql_user',
            'PASSWORD': 'your_mysql_password',
            'HOST': 'localhost',
            'PORT': '3306',
        }
    }
    ```

#### 3. إنشاء النماذج (Models) في Django:
- إعداد النماذج في `meter/models.py`:
    ```python
    from django.db import models

    class State(models.Model):
        state_name = models.CharField(max_length=255, unique=True)

        def __str__(self):
            return self.state_name

    class ElectricalDevice(models.Model):
        device_name = models.CharField(max_length=255)
        power_rating = models.FloatField()
        expected_usage_hours = models.FloatField()
        state = models.ForeignKey(State, on_delete=models.CASCADE)

        def __str__(self):
            return self.device_name

    class EnergyConsumption(models.Model):
        timestamp = models.DateTimeField(auto_now_add=True)
        device = models.ForeignKey(ElectricalDevice, on_delete=models.CASCADE)
        power_consumed = models.FloatField()
        state = models.ForeignKey(State, on_delete=models.CASCADE)

        def __str__(self):
            return f"{self.device.device_name} - {self.power_consumed} kWh"
    ```

- إنشاء وتطبيق الهجرات:
    ```bash
    python manage.py makemigrations meter
    python manage.py migrate
    ```

#### 4. إنشاء واجهة المستخدم (HTML/CSS/JavaScript):
- إعداد قالب HTML لعرض البيانات بواجهة عربية في `templates/index.html`:
    ```html
    <!DOCTYPE html>
    <html lang="ar">
    <head>
        <meta charset="UTF-8">
        <title>لوحة تحكم العدادات الذكية</title>
        <style>
            body {
                direction: rtl;
                font-family: 'Arial', sans-serif;
            }
            table {
                width: 100%;
                border-collapse: collapse;
            }
            table, th, td {
                border: 1px solid black;
            }
            th, td {
                padding: 15px;
                text-align: center;
            }
            th {
                background-color: #f2f2f2;
            }
        </style>
    </head>
    <body>
        <h1>لوحة تحكم العدادات الذكية</h1>
        <table>
            <tr>
                <th>اسم الولاية</th>
                <th>اسم الجهاز</th>
                <th>التصنيف الكهربائي (kW)</th>
                <th>ساعات التشغيل المتوقعة</th>
            </tr>
            {% for device in devices %}
            <tr>
                <td>{{ device.state.state_name }}</td>
                <td>{{ device.device_name }}</td>
                <td>{{ device.power_rating }}</td>
                <td>{{ device.expected_usage_hours }}</td>
            </tr>
            {% endfor %}
        </table>
    </body>
    </html>
    ```

- إعداد العرض في `meter/views.py`:
    ```python
    from django.shortcuts import render
    from .models import ElectricalDevice

    def index(request):
        devices = ElectricalDevice.objects.select_related('state').all()
        return render(request, 'index.html', {'devices': devices})
    ```

- إعداد URL في `meter/urls.py`:
    ```python
    from django.urls import path
    from . import views

    urlpatterns = [
        path('', views.index, name='index'),
    ]
    ```

- إضافة URL التطبيق إلى `smart_meter/urls.py`:
    ```python
    from django.contrib import admin
    from django.urls import include, path

    urlpatterns = [
        path('admin/', admin.site.urls),
        path('', include('meter.urls')),
    ]
    ```

#### 5. تطوير خوارزمية الذكاء الاصطناعي:
- استخدام مكتبة Scikit-learn أو TensorFlow لتحليل البيانات والتنبؤ بالاستهلاك.
- مثال على نموذج بسيط باستخدام Scikit-learn في `meter/ai.py`:
    ```python
    import numpy as np
    from sklearn.linear_model import LinearRegression
    from .models import EnergyConsumption

    def predict_consumption(state_id, device_id, hours):
        data = EnergyConsumption.objects.filter(state_id=state_id, device_id=device_id)
        X = np.array([d.timestamp.timestamp() for d in data]).reshape(-1, 1)
        y = np.array([d.power_consumed for d in data])

        model = LinearRegression()
        model.fit(X, y)

        prediction = model.predict(np.array([hours]).reshape(-1, 1))
        return prediction[0]
    ```

#### 6. تشغيل المشروع:
- تشغيل الخادم المحلي:
    ```bash
    python manage.py runserver
    ```

بهذا النظام، يمكنك مراقبة استهلاك الكهرباء تلقائيًا بناءً على عدد الأدوات الكهربائية المتصلة في كل ولاية، وتحليل البيانات لتحسين كفاءة استخدام الطاقة بواجهة عربية.
