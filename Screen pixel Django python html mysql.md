لإنشاء نظام إلكتروني يعرض معلومات دقة الشاشة ويحدد ما إذا كانت الشاشة في وضع أفقي (Landscape) أو عمودي (Portrait) باستخدام البرمجيات الحرة ومفتوحة المصدر، يمكننا استخدام Django (وهو إطار عمل ويب في Python) وHTML وMySQL. فيما يلي خطوات إعداد هذا النظام:

### متطلبات المشروع
- Python
- Django
- MySQL
- مكتبة Django-MySQL
- مكتبة requests

### الخطوات التفصيلية:

#### 1. إعداد بيئة التطوير

قم بتثبيت Python وpip إذا لم تكن مثبتة بالفعل. ثم، أنشئ بيئة افتراضية:

```bash
python -m venv myenv
source myenv/bin/activate  # على نظام ويندوز استخدم: myenv\Scripts\activate
```

#### 2. تثبيت Django والمكتبات المطلوبة

```bash
pip install django django-mysql mysqlclient
```

#### 3. إنشاء مشروع Django وتطبيق

```bash
django-admin startproject screen_info_project
cd screen_info_project
django-admin startapp screen_info
```

#### 4. إعداد قاعدة البيانات

تعديل ملف `settings.py` لاستخدام MySQL:

```python
# settings.py

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'screen_info',
        'USER': 'root',
        'PASSWORD': 'yourpassword',
        'HOST': 'localhost',
        'PORT': '3306',
    }
}
```

#### 5. إنشاء نموذج البيانات

قم بتعديل ملف `models.py` داخل تطبيق `screen_info` لإنشاء النموذج:

```python
# screen_info/models.py

from django.db import models

class ScreenData(models.Model):
    width = models.IntegerField()
    height = models.IntegerField()
    orientation = models.CharField(max_length=10)
    user_agent = models.CharField(max_length=255)
    timestamp = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return f"{self.width}x{self.height} - {self.orientation}"
```

ثم قم بتشغيل أوامر إنشاء وترحيل قاعدة البيانات:

```bash
python manage.py makemigrations
python manage.py migrate
```

#### 6. إعداد صفحة HTML لجمع بيانات الشاشة

قم بإنشاء ملف HTML في المجلد `templates` داخل تطبيق `screen_info`:

```html
<!-- screen_info/templates/index.html -->

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

            $.post('/save_screen_info/', {
                width: width,
                height: height,
                orientation: orientation,
                user_agent: userAgent,
                csrfmiddlewaretoken: '{{ csrf_token }}'
            }, function(response) {
                console.log('Data saved: ' + response);
            });
        });
    </script>
</body>
</html>
```

#### 7. إعداد طرق العرض (Views) 

قم بتعديل ملف `views.py` داخل تطبيق `screen_info`:

```python
# screen_info/views.py

from django.shortcuts import render
from django.http import JsonResponse
from .models import ScreenData

def index(request):
    return render(request, 'index.html')

def save_screen_info(request):
    if request.method == 'POST':
        width = request.POST['width']
        height = request.POST['height']
        orientation = request.POST['orientation']
        user_agent = request.POST['user_agent']

        screen_data = ScreenData(width=width, height=height, orientation=orientation, user_agent=user_agent)
        screen_data.save()
        return JsonResponse({'status': 'success'})
```

#### 8. إعداد ملف URL 

قم بتعديل ملف `urls.py` داخل تطبيق `screen_info`:

```python
# screen_info/urls.py

from django.urls import path
from .views import index, save_screen_info

urlpatterns = [
    path('', index, name='index'),
    path('save_screen_info/', save_screen_info, name='save_screen_info'),
]
```

ثم قم بتعديل ملف `urls.py` الخاص بالمشروع الرئيسي لإدراج تطبيق `screen_info`:

```python
# screen_info_project/urls.py

from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', include('screen_info.urls')),
]
```

#### 9. إعداد المجلد لإنشاء المجلدات الفريدة

قم بتعديل طرق العرض (Views) لإضافة وظيفة إنشاء المجلدات:

```python
# screen_info/views.py

import os

def save_screen_info(request):
    if request.method == 'POST':
        width = request.POST['width']
        height = request.POST['height']
        orientation = request.POST['orientation']
        user_agent = request.POST['user_agent']

        # Create folder based on user agent
        folder_name = ''.join(e for e in user_agent if e.isalnum())
        folder_path = os.path.join('user_folders', folder_name)

        if not os.path.exists(folder_path):
            os.makedirs(folder_path)

        screen_data = ScreenData(width=width, height=height, orientation=orientation, user_agent=user_agent)
        screen_data.save()
        return JsonResponse({'status': 'success', 'folder_path': folder_path})
```

#### 10. تشغيل الخادم واختبار النظام

شغل خادم التطوير الخاص بـ Django:

```bash
python manage.py runserver
```

ثم افتح متصفحك وانتقل إلى `http://localhost:8000/` لاختبار النظام والتأكد من أنه يعمل بشكل صحيح.

بهذا الشكل، يمكنك إنشاء نظام إلكتروني يعرض معلومات دقة الشاشة ويحدد ما إذا كانت الشاشة في وضع أفقي أو عمودي، بالإضافة إلى إنشاء مجلد فريد لكل مستخدم باستخدام البرمجيات الحرة ومفتوحة المصدر 100٪، بما في ذلك Django وPython وHTML وMySQL.
