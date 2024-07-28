لتحليل البيانات وعرضها باستخدام الذكاء الاصطناعي، يمكننا استخدام مكتبة مثل `pandas` لتحليل البيانات و`matplotlib` أو `seaborn` لعرض البيانات بصريًا. يمكننا أيضًا استخدام مكتبة `scikit-learn` لتطبيق بعض خوارزميات التعلم الآلي إذا لزم الأمر.

### الخطوات التفصيلية:

1. **تثبيت المكتبات المطلوبة**:

   قم بتثبيت المكتبات التالية:

   ```bash
   pip install pandas matplotlib seaborn scikit-learn
   ```

2. **إضافة عرض البيانات إلى Django**:

   سنضيف عرض البيانات بشكل بصري في صفحة HTML باستخدام `matplotlib` و`seaborn`.

3. **إعدادات Django لعرض البيانات**:

   - قم بتعديل ملف `views.py` لإضافة وظيفة لتحليل البيانات وإنشاء الرسوم البيانية.

   ```python
   # screen_info/views.py

   import os
   import pandas as pd
   import matplotlib.pyplot as plt
   import seaborn as sns
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

           # Create folder based on user agent
           folder_name = ''.join(e for e in user_agent if e.isalnum())
           folder_path = os.path.join('user_folders', folder_name)

           if not os.path.exists(folder_path):
               os.makedirs(folder_path)

           screen_data = ScreenData(width=width, height=height, orientation=orientation, user_agent=user_agent)
           screen_data.save()
           return JsonResponse({'status': 'success', 'folder_path': folder_path})

   def analyze_data(request):
       # Load data from the database
       data = ScreenData.objects.all().values()
       df = pd.DataFrame(data)

       # Create visualizations
       plt.figure(figsize=(10, 6))
       sns.countplot(data=df, x='orientation')
       plt.title('Orientation Count')
       plt.savefig('static/orientation_count.png')

       plt.figure(figsize=(10, 6))
       sns.histplot(data=df, x='width', bins=30, kde=True)
       plt.title('Width Distribution')
       plt.savefig('static/width_distribution.png')

       plt.figure(figsize=(10, 6))
       sns.histplot(data=df, x='height', bins=30, kde=True)
       plt.title('Height Distribution')
       plt.savefig('static/height_distribution.png')

       return render(request, 'analyze.html', {
           'orientation_count': 'static/orientation_count.png',
           'width_distribution': 'static/width_distribution.png',
           'height_distribution': 'static/height_distribution.png',
       })
   ```

   - قم بإنشاء ملف `analyze.html` لعرض الرسوم البيانية.

   ```html
   <!-- screen_info/templates/analyze.html -->

   <!DOCTYPE html>
   <html lang="en">
   <head>
       <meta charset="UTF-8">
       <meta name="viewport" content="width=device-width, initial-scale=1.0">
       <title>Analyze Data</title>
   </head>
   <body>
       <h1>Data Analysis</h1>
       <h2>Orientation Count</h2>
       <img src="{{ orientation_count }}" alt="Orientation Count">

       <h2>Width Distribution</h2>
       <img src="{{ width_distribution }}" alt="Width Distribution">

       <h2>Height Distribution</h2>
       <img src="{{ height_distribution }}" alt="Height Distribution">
   </body>
   </html>
   ```

   - قم بإضافة مسار URL جديد في ملف `urls.py` داخل تطبيق `screen_info`.

   ```python
   # screen_info/urls.py

   from django.urls import path
   from .views import index, save_screen_info, analyze_data

   urlpatterns = [
       path('', index, name='index'),
       path('save_screen_info/', save_screen_info, name='save_screen_info'),
       path('analyze/', analyze_data, name='analyze_data'),
   ]
   ```

4. **تشغيل الخادم واختبار النظام**:

   شغل خادم التطوير الخاص بـ Django:

   ```bash
   python manage.py runserver
   ```

   ثم افتح متصفحك وانتقل إلى `http://localhost:8000/analyze/` لاختبار عرض البيانات والتحليلات.

### شرح الخطوات:

- **تحليل البيانات**: قمنا بتحميل البيانات من قاعدة البيانات واستخدام مكتبة `pandas` لتحليلها. قمنا بإنشاء الرسوم البيانية باستخدام `seaborn` وحفظناها في مجلد `static`.

- **عرض البيانات**: عرضنا الرسوم البيانية في صفحة HTML باستخدام علامة `<img>`.

بهذه الطريقة، يمكنك تحليل البيانات وعرضها بشكل بصري باستخدام الذكاء الاصطناعي والبرمجيات الحرة ومفتوحة المصدر 100٪، بما في ذلك Django وPython وHTML وMySQL.
