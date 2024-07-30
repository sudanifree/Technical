لنكتب برنامجًا لمراقبة الفيروسات والبرمجيات الخبيثة والأبواب الخلفية في الوقت الفعلي لـ27 كلية تقنية باستخدام الذكاء الاصطناعي، والبرمجيات الحرة مفتوحة المصدر، وDjango، وPython، وMySQL، وHTML، مع التعامل مع عناوين IP، والمنافذ، وبائعي DHCP.

### 1. إعداد البيئة

تأكد من أنك قمت بتنصيب Django وMySQL وحزم الذكاء الاصطناعي المطلوبة (مثل `scikit-learn` أو `tensorflow`):

```bash
pip install django mysqlclient scikit-learn
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
       virus_detected = models.BooleanField(default=False)
       malware_detected = models.BooleanField(default=False)
       backdoor_detected = models.BooleanField(default=False)
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
       # أضف باقي الكليات هنا (لإجمالي 27 كلية)
   ]

   for college_data in colleges_data:
       College.objects.create(**college_data)
   ```

### 5. استخدام الذكاء الاصطناعي للكشف عن البرمجيات الضارة

هنا سنستخدم `scikit-learn` كمثال لتدريب نموذج على اكتشاف البرمجيات الضارة. 

1. **إضافة ملف تدريب النموذج:**

   ```python
   # monitoring/ai_model.py
   import numpy as np
   from sklearn.ensemble import RandomForestClassifier
   import joblib

   def train_model():
       # البيانات التمثيلية للتدريب (هذا مجرد مثال)
       X = np.array([[0, 1, 0], [1, 0, 1], [0, 0, 0], [1, 1, 1]])
       y = np.array([0, 1, 0, 1])  # 0 يعني نظيف، 1 يعني ضار

       model = RandomForestClassifier()
       model.fit(X, y)

       # حفظ النموذج
       joblib.dump(model, 'monitoring/malware_detection_model.pkl')

   def predict(features):
       model = joblib.load('monitoring/malware_detection_model.pkl')
       return model.predict([features])
   ```

2. **تدريب النموذج:**

   ```python
   # قم بتشغيل هذا الجزء من الشيفرة مرة واحدة لتدريب النموذج
   from monitoring.ai_model import train_model
   train_model()
   ```

### 6. إنشاء واجهات المستخدم (Views) وواجهات العرض (Templates)

1. **تحديث ملف `views.py` في تطبيق `monitoring`:**

   ```python
   from django.shortcuts import render
   from .models import SystemLog
   from .ai_model import predict

   def index(request):
       logs = SystemLog.objects.all().order_by('-timestamp')
       return render(request, 'monitoring/index.html', {'logs': logs})

   def add_log(request):
       if request.method == 'POST':
           college_id = request.POST['college_id']
           port = request.POST['port']
           dhcp_vendor = request.POST['dhcp_vendor']
           log_message = request.POST['log_message']
           
           # مثال على المميزات التي سيتم استخدامها في التنبؤ
           features = [int(request.POST['feature1']), int(request.POST['feature2']), int(request.POST['feature3'])]
           prediction = predict(features)
           
           virus_detected = prediction[0] == 1

           log = SystemLog(
               college_id=college_id,
               port=port,
               dhcp_vendor=dhcp_vendor,
               log_message=log_message,
               virus_detected=virus_detected
           )
           log.save()

       return render(request, 'monitoring/add_log.html')
   ```

2. **إنشاء قالب HTML لإضافة السجلات في `templates/monitoring/add_log.html`:**

   ```html
   <!DOCTYPE html>
   <html lang="ar">
   <head>
       <meta charset="UTF-8">
       <meta name="viewport" content="width=device-width, initial-scale=1.0">
       <title>إضافة سجل</title>
   </head>
   <body>
       <h1>إضافة سجل جديد</h1>
       <form method="post">
           {% csrf_token %}
           <label for="college_id">الكلية:</label>
           <input type="text" id="college_id" name="college_id"><br><br>
           <label for="port">المنفذ:</label>
           <input type="text" id="port" name="port"><br><br>
           <label for="dhcp_vendor">بائع DHCP:</label>
           <input type="text" id="dhcp_vendor" name="dhcp_vendor"><br><br>
           <label for="log_message">رسالة السجل:</label>
           <input type="text" id="log_message" name="log_message"><br><br>
           <label for="feature1">ميزة 1:</label>
           <input type="text" id="feature1" name="feature1"><br><br>
           <label for="feature2">ميزة 2:</label>
           <input type="text" id="feature2" name="feature2"><br><br>
           <label for="feature3">ميزة 3:</label>
           <input type="text" id="feature3" name="feature3"><br><br>
           <button type="submit">إضافة</button>
       </form>
   </body>
   </html>
   ```

3. **تحديث قالب HTML للعرض في `templates/monitoring/index.html`:**

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
                   <th>فيروس مكتشف</th>
                   <th>برمجية خبيثة مكتشفة</th>
                   <th>باب خلفي مكتشف</th>
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
                   <td>{{ log.virus_detected }}</td>
                   <td>{{ log.malware_detected }}</td>
                   <td>{{ log.backdoor_detected }}</td>
                   <td>{{ log.timestamp }}</td>
               </tr>
               {% endfor %}
           </tbody>
       </table>
   </body>
   </html>
   ```

### 7. إعداد عناوين URL

1. **تحديث ملف `urls.py` في تطبيق `monitoring`:**

   ```python
   from django.urls import path
   from
