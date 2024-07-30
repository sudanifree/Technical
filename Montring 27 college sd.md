لإنشاء برنامج لمراقبة النظام في الوقت الفعلي لـ27 كلية تقنية باستخدام Django وPython وMySQL وHTML وعنوان IP والمنفذ وبائع DHCP، إليك الخطوات التفصيلية:

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

   class College(models.Model):
       name = models.CharField(max_length=255)
       ip_address = models.GenericIPAddressField()

       def __str__(self):
           return self.name

   class SystemLog(models.Model):
       college = models.ForeignKey(College, on_delete=models.CASCADE)
       port = models.PositiveIntegerField()
       dhcp_vendor = models.CharField(max_length=255)
       log_message = models.TextField()
       timestamp = models.DateTimeField(auto_now_add=True)

       def __str__(self):
           return f"{self.college.name} - {self.timestamp}"
   ```

2. **تنفيذ التعديلات على قاعدة البيانات:**

   ```bash
   python manage.py makemigrations
   python manage.py migrate
   ```

3. **إضافة بيانات الكليات إلى قاعدة البيانات:**

   يمكنك استخدام Django admin لإضافة بيانات الكليات، أو إضافة البيانات يدوياً عبر سكريبت Python. هنا مثال على كيفية إضافة الكليات باستخدام سكريبت Python:

   ```python
   from monitoring.models import College

   colleges_data = [
       {"name": "كلية النوبة التقنية", "ip_address": "11.11.22.11"},
       {"name": "كلية دلقو التقنية", "ip_address": "22.22.11.22"},
       # أضف باقي الكليات هنا
   ]

   for college_data in colleges_data:
       College.objects.create(**college_data)
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
                   <th>الكلية</th>
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
                   <td>{{ log.college.name }}</td>
                   <td>{{ log.college.ip_address }}</td>
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

### ملاحظة إضافية:
تأكد من إضافة بقية الكليات في البيانات الأولية، كما يمكنك تطوير النظام بشكل أكبر لإضافة وظائف جديدة حسب الحاجة.
