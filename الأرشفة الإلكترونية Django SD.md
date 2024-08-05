لإنشاء نظام أرشفة إلكترونية باستخدام Django وPython وMySQL وHTML مع التركيز على الحماية القوية، سنقوم بتوضيح خطوات إنشاء هذا النظام مع تقديم أكواد أساسية. سنقوم بتغطية:

1. **إعداد بيئة Django**.
2. **إنشاء نموذج قاعدة بيانات**.
3. **بناء واجهة المستخدم**.
4. **إضافة الذكاء الاصطناعي**.
5. **تأمين النظام**.

### 1. إعداد بيئة Django

**أ. تثبيت Django و MySQL**

```bash
pip install django mysqlclient
```

**ب. إنشاء مشروع Django**

```bash
django-admin startproject archive_system
cd archive_system
```

**ج. إنشاء تطبيق داخل المشروع**

```bash
python manage.py startapp documents
```

### 2. إعداد قاعدة البيانات

**أ. إعداد قاعدة بيانات MySQL**

أنشئ قاعدة بيانات جديدة في MySQL:

```sql
CREATE DATABASE archive_db;
```

**ب. تعديل إعدادات Django**

**`archive_system/settings.py`**: تكوين إعدادات قاعدة البيانات.

```python
DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'archive_db',
        'USER': 'your_mysql_user',
        'PASSWORD': 'your_mysql_password',
        'HOST': 'localhost',
        'PORT': '3306',
    }
}
```

### 3. إنشاء نموذج قاعدة بيانات

**أ. إعداد نموذج الوثائق**

**`documents/models.py`**: تعريف نموذج الوثائق.

```python
from django.db import models

class Document(models.Model):
    title = models.CharField(max_length=255)
    file = models.FileField(upload_to='documents/')
    uploaded_at = models.DateTimeField(auto_now_add=True)

    def __str__(self):
        return self.title
```

**ب. إنشاء الجداول في قاعدة البيانات**

```bash
python manage.py makemigrations
python manage.py migrate
```

### 4. بناء واجهة المستخدم

**أ. إعداد نماذج الإدخال**

**`documents/forms.py`**: إعداد نموذج رفع الوثائق.

```python
from django import forms
from .models import Document

class DocumentForm(forms.ModelForm):
    class Meta:
        model = Document
        fields = ['title', 'file']
```

**ب. إعداد العروض**

**`documents/views.py`**: إعداد العروض لعرض وتحميل الوثائق.

```python
from django.shortcuts import render, redirect
from .forms import DocumentForm
from .models import Document

def upload_document(request):
    if request.method == 'POST':
        form = DocumentForm(request.POST, request.FILES)
        if form.is_valid():
            form.save()
            return redirect('document_list')
    else:
        form = DocumentForm()
    return render(request, 'upload.html', {'form': form})

def document_list(request):
    documents = Document.objects.all()
    return render(request, 'document_list.html', {'documents': documents})
```

**ج. إعداد ملفات القوالب**

**`documents/templates/upload.html`**: نموذج تحميل الوثائق.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Upload Document</title>
</head>
<body>
    <h1>Upload Document</h1>
    <form method="post" enctype="multipart/form-data">
        {% csrf_token %}
        {{ form.as_p }}
        <button type="submit">Upload</button>
    </form>
</body>
</html>
```

**`documents/templates/document_list.html`**: قائمة الوثائق.

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document List</title>
</head>
<body>
    <h1>Document List</h1>
    <ul>
        {% for document in documents %}
            <li>
                <a href="{{ document.file.url }}">{{ document.title }}</a>
            </li>
        {% endfor %}
    </ul>
</body>
</html>
```

**د. إعداد عنوان URL**

**`documents/urls.py`**: إعداد URL للتطبيق.

```python
from django.urls import path
from . import views

urlpatterns = [
    path('upload/', views.upload_document, name='upload_document'),
    path('documents/', views.document_list, name='document_list'),
]
```

**`archive_system/urls.py`**: تضمين URLs التطبيق.

```python
from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('', include('documents.urls')),
]
```

### 5. دمج الذكاء الاصطناعي

**أ. إعداد واجهة Python لتحليل النصوص**

**`text_analysis.py`**: إعداد خادم Flask لتحليل النصوص باستخدام مكتبة spaCy.

```python
from flask import Flask, request, jsonify
import spacy

app = Flask(__name__)
nlp = spacy.load("en_core_web_sm")

@app.route('/analyze', methods=['POST'])
def analyze():
    data = request.json
    text = data.get('text')
    doc = nlp(text)
    entities = [(ent.text, ent.label_) for ent in doc.ents]
    return jsonify({"entities": entities})

if __name__ == "__main__":
    app.run(debug=True)
```

**ب. استدعاء API من Django**

**`documents/views.py`**: استدعاء واجهة API لتحليل النصوص.

```python
import requests
from django.conf import settings

def analyze_text(text):
    response = requests.post('http://localhost:5000/analyze', json={'text': text})
    return response.json()

def document_list(request):
    documents = Document.objects.all()
    analysis_results = [analyze_text(doc.file.read().decode('utf-8')) for doc in documents]
    return render(request, 'document_list.html', {'documents': documents, 'analysis_results': analysis_results})
```

### 6. تأمين النظام

**أ. حماية من هجمات SQL Injection و XSS**

**تأكد من استخدام Django’s ORM**: يعتبر Django’s ORM آمنًا ضد SQL Injection. تأكد من تعقيم المدخلات في واجهة المستخدم.

**ب. استخدام HTTPS**

**تكوين HTTPS**: تأكد من تشغيل Django باستخدام شهادة SSL/TLS على الخادم.

**ج. حماية من CSRF**

**تأكد من وجود `{% csrf_token %}` في جميع النماذج.**

### تشغيل النظام

**أ. بدء خادم Django**

```bash
python manage.py runserver
```

**ب. بدء خادم Flask (لتحليل النصوص)**

```bash
python text_analysis.py
```

### خاتمة

لقد قمنا بإنشاء نظام أرشفة إلكترونية باستخدام Django، Python، MySQL، وHTML مع دمج الذكاء الاصطناعي. يتضمن النظام رفع الوثائق، عرض الوثائق، تحليل النصوص باستخدام AI، وتأكيد الأمان عبر تدابير مختلفة. يمكنك توسيع النظام وإضافة ميزات إضافية حسب احتياجاتك.
