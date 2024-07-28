لإعداد نظام تسجيل المواطنين باستخدام Django وMySQL وعرض الفئة العمرية في الوقت الحقيقي باستخدام الذكاء الاصطناعي، اتبع الخطوات التالية:

### 1. إعداد مشروع Django

#### إنشاء مشروع جديد:

```bash
django-admin startproject citizen_registration
cd citizen_registration
```

#### إنشاء تطبيق جديد:

```bash
python manage.py startapp citizens
```

### 2. إعداد MySQL كقاعدة بيانات

#### إعداد ملف `settings.py`:

تحديث إعدادات قاعدة البيانات في ملف `settings.py` لاستخدام MySQL:

```python
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'citizens_db',
        'USER': 'root',
        'PASSWORD': 'your_password',
        'HOST': 'localhost',
        'PORT': '3306',
    }
}
```

### 3. إنشاء نماذج البيانات

#### تحديث ملف `models.py`:

```python
from django.db import models

class Citizen(models.Model):
    unique_id = models.CharField(max_length=20, unique=True)
    first_name = models.CharField(max_length=100)
    second_name = models.CharField(max_length=100)
    third_name = models.CharField(max_length=100)
    fourth_name = models.CharField(max_length=100)
    age = models.IntegerField()
    gender = models.CharField(max_length=10, choices=[('Male', 'ذكر'), ('Female', 'أنثى')])
    is_married = models.CharField(max_length=10, choices=[('Yes', 'نعم'), ('No', 'لا')])
    father_name = models.CharField(max_length=100)
    mother_name = models.CharField(max_length=100)
    locality = models.CharField(max_length=100)
    administrative_unit = models.CharField(max_length=100)
    village = models.CharField(max_length=100)
    is_war_refugee = models.CharField(max_length=10, choices=[('Yes', 'نعم'), ('No', 'لا')])

    def __str__(self):
        return self.unique_id
```

### 4. إعداد نماذج الإدخال

#### إنشاء نموذج في ملف `forms.py`:

```python
from django import forms
from .models import Citizen

class CitizenForm(forms.ModelForm):
    class Meta:
        model = Citizen
        fields = '__all__'
```

### 5. إعداد وجهات النظر (Views)

#### تحديث ملف `views.py`:

```python
from django.shortcuts import render, redirect
from .forms import CitizenForm
from django.http import JsonResponse
import json

def register_citizen(request):
    if request.method == 'POST':
        form = CitizenForm(request.POST)
        if form.is_valid():
            form.save()
            return redirect('citizen_list')
    else:
        form = CitizenForm()
    return render(request, 'register.html', {'form': form})

def citizen_list(request):
    citizens = Citizen.objects.all()
    return render(request, 'citizen_list.html', {'citizens': citizens})

def predict_age_category(request):
    if request.method == 'POST':
        data = json.loads(request.body)
        age = data['age']
        category = get_age_category(age)
        return JsonResponse({'category': category})

def get_age_category(age):
    if age < 2:
        return "رضيع"
    elif 2 <= age < 13:
        return "طفل"
    elif 13 <= age <= 30:
        return "شباب"
    elif 31 <= age <= 60:
        return "بالغ"
    else:
        return "كبار"
```

### 6. إعداد مسارات URL

#### تحديث ملف `urls.py`:

```python
from django.contrib import admin
from django.urls import path
from citizens import views

urlpatterns = [
    path('admin/', admin.site.urls),
    path('register/', views.register_citizen, name='register_citizen'),
    path('citizens/', views.citizen_list, name='citizen_list'),
    path('predict/', views.predict_age_category, name='predict_age_category'),
]
```

### 7. إعداد قوالب HTML

#### إنشاء قالب `register.html`:

```html
<!DOCTYPE html>
<html lang="ar">
<head>
    <meta charset="UTF-8">
    <title>تسجيل المواطنين</title>
    <script>
        function predictAgeCategory(age) {
            fetch('/predict/', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ age: age })
            })
            .then(response => response.json())
            .then(data => {
                document.getElementById('age_category').innerText = 'الفئة العمرية: ' + data.category;
            })
            .catch(error => console.error('Error:', error));
        }

        function updateAgeCategory() {
            const age = document.getElementById('id_age').value;
            predictAgeCategory(age);
        }
    </script>
</head>
<body>
    <h2>نموذج تسجيل المواطن</h2>
    <form method="post">
        {% csrf_token %}
        {{ form.as_p }}
        <div id="age_category"></div><br>
        <button type="submit">تسجيل</button>
    </form>
    <br>
    <a href="{% url 'citizen_list' %}">عرض البيانات</a>
</body>
</html>
```

#### إنشاء قالب `citizen_list.html`:

```html
<!DOCTYPE html>
<html lang="ar">
<head>
    <meta charset="UTF-8">
    <title>عرض بيانات المواطنين</title>
</head>
<body>
    <h2>قائمة المواطنين</h2>
    <table border="1">
        <tr>
            <th>الرقم المميز</th>
            <th>الاسم الأول</th>
            <th>الاسم الثاني</th>
            <th>الاسم الثالث</th>
            <th>الاسم الرابع</th>
            <th>العمر</th>
            <th>الجنس</th>
            <th>متزوج</th>
            <th>اسم الأب</th>
            <th>اسم الأم</th>
            <th>المحلية</th>
            <th>الوحدة الإدارية</th>
            <th>القرية</th>
            <th>فار من الحرب</th>
        </tr>
        {% for citizen in citizens %}
        <tr>
            <td>{{ citizen.unique_id }}</td>
            <td>{{ citizen.first_name }}</td>
            <td>{{ citizen.second_name }}</td>
            <td>{{ citizen.third_name }}</td>
            <td>{{ citizen.fourth_name }}</td>
            <td>{{ citizen.age }}</td>
            <td>{{ citizen.gender }}</td>
            <td>{{ citizen.is_married }}</td>
            <td>{{ citizen.father_name }}</td>
            <td>{{ citizen.mother_name }}</td>
            <td>{{ citizen.locality }}</td>
            <td>{{ citizen.administrative_unit }}</td>
            <td>{{ citizen.village }}</td>
            <td>{{ citizen.is_war_refugee }}</td>
        </tr>
        {% endfor %}
    </table>
    <br>
    <a href="{% url 'register_citizen' %}">تسجيل مواطن جديد</a>
</body>
</html>
```

### 8. تشغيل التطبيق

#### ترحيل قاعدة البيانات:

```bash
python manage.py makemigrations
python manage.py migrate
```

#### تشغيل خادم Django:

```bash
python manage.py runserver
```

بهذا الشكل، يمكنك تسجيل المواطنين وعرض بياناتهم في الوقت الحقيقي باستخدام Django وMySQL، مع القدرة على تصنيف الفئة العمرية باستخدام الذكاء الاصطناعي.
