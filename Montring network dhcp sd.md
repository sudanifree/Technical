إليك مثال عملي على كيفية كتابة برنامج لمراقبة النظام في الوقت الفعلي باستخدام Django وPython وMySQL وHTML، مع التعامل مع عنوان IP والمنفذ وبائع DHCP، مكتوب باللغة العربية الفصحى.

### 1. إعداد البيئة
تأكد من أنك قمت بتنصيب Django وMySQL:

```bash
pip install django mysqlclient
```

### 2. إعداد مشروع Django

1. **إنشاء مشروع Django:**
   ```bash
   django-admin startproject monitoring_system
   ```

2. **إنشاء تطبيق داخل المشروع:**
   ```bash
   cd monitoring_system
   python manage.py startapp monitoring
   ```

### 3. إعداد قاعدة البيانات (MySQL)

1. **تحديث إعدادات قاعدة البيانات في `settings.py`:**

   ```python
   DATABASES = {
       'default': {
           'ENGINE': 'django.db.backends.mysql',
           'NAME': 'monitoring_db',
           'USER': 'root',
           'PASSWORD': 'your_password',
           'HOST': 'localhost',
           'PORT': '3306',
       }
   }
   ```

### 4. إنشاء نماذج البيانات (Models)

1. **تحديث ملف `models.py` في تطبيق `monitoring`:**

   ```python
   from django.db import models

   class SystemLog(models.Model):
       ip_address = models.GenericIPAddressField()
       port = models.PositiveIntegerField()
       dhcp_vendor = models.CharField(max_length=255)
       log_message = models.TextField()
       timestamp = models.DateTimeField(auto_now_add=True)

       def __str__(self):
           return f"{self.ip_address} - {self.timestamp}"
   ```

2. **تنفيذ التعديلات على قاعدة البيانات:**

   ```bash
   python manage.py makemigrations
   python manage.py migrate
   ```

### 5. إنشاء واجهات المستخدم (Views) وواجهات العرض (Templates)

1. **تحديث ملف `views.py` في تطبيق `monitoring`:**

   ```python
   from django.shortcuts import render
   from .models import SystemLog

   def index(request):
       logs = SystemLog.objects.all().order_by('-timestamp')
       return render(request, 'monitoring/index.html', {'logs': logs})
   ```

2. **إنشاء قالب HTML في `templates/monitoring/index.html`:**

   ```html
   <!DOCTYPE html>
   <html lang="ar">
   <head>
       <meta charset="UTF-8">
       <meta name="viewport" content="width=device-width, initial-scale=1.0">
       <title>نظام المراقبة</title>
   </head>
   <body>
       <h1>سجل النظام</h1>
       <table border="1">
           <thead>
               <tr>
                   <th>عنوان IP</th>
                   <th>المنفذ</th>
                   <th>بائع DHCP</th>
                   <th>رسالة السجل</th>
                   <th>الوقت</th>
               </tr>
           </thead>
           <tbody>
               {% for log in logs %}
               <tr>
                   <td>{{ log.ip_address }}</td>
                   <td>{{ log.port }}</td>
                   <td>{{ log.dhcp_vendor }}</td>
                   <td>{{ log.log_message }}</td>
                   <td>{{ log.timestamp }}</td>
               </tr>
               {% endfor %}
           </tbody>
       </table>
   </body>
   </html>
   ```

### 6. إعداد عناوين URL

1. **تحديث ملف `urls.py` في تطبيق `monitoring`:**

   ```python
   from django.urls import path
   from . import views

   urlpatterns = [
       path('', views.index, name='index'),
   ]
   ```

2. **تحديث ملف `urls.py` في المشروع الرئيسي:**

   ```python
   from django.contrib import admin
   from django.urls import include, path

   urlpatterns = [
       path('admin/', admin.site.urls),
       path('', include('monitoring.urls')),
   ]
   ```

### 7. تشغيل الخادم

شغل الخادم المحلي لتجربة التطبيق:

```bash
python manage.py runserver
```

يمكنك الآن زيارة `http://127.0.0.1:8000/` لرؤية واجهة المستخدم الخاصة بنظام المراقبة في الوقت الفعلي.
