لإنشاء مجلد بأسماء مستمدة من قيم العرض (`width`)، الارتفاع (`height`) والوضع (`orientation`) مثل `174231l.html` أو `812123p.html`، سنقوم بتعديل الكود في عرض `save_screen_info` لإنشاء المجلدات بالأسماء المطلوبة.

### الخطوات:

1. **تعديل عرض `save_screen_info`**:
   - سنضيف وظيفة لإنشاء المجلد بأسماء مستمدة من قيم العرض، الارتفاع، والوضع.

### كود `views.py` المعدل:

```python
# screen_info/views.py

import os
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

        # Create folder based on width, height, and orientation
        folder_name = f"{width}{height}{orientation[0].lower()}"
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

### الشرح:

- **تكوين اسم المجلد**:
  - نستخدم `width` و`height` و`orientation` لتكوين اسم المجلد.
  - `orientation[0].lower()` يأخذ الحرف الأول من `orientation` ويجعله حرفًا صغيرًا (`l` لـ `Landscape` و`p` لـ `Portrait`).

- **إنشاء المجلد**:
  - `os.path.join('user_folders', folder_name)` يقوم بدمج اسم المجلد مع المسار الجذر للمجلدات (`user_folders`).
  - `os.makedirs(folder_path)` يقوم بإنشاء المجلد إذا لم يكن موجودًا.

### إعدادات إضافية:

- **إعداد مجلد `user_folders`**:
  - تأكد من أن المجلد `user_folders` موجود ويمكن للـ Python الكتابة فيه. يمكنك إنشاء المجلد يدويًا أو باستخدام الأمر التالي في الجهاز:

    ```bash
    mkdir user_folders
    chmod 777 user_folders
    ```

### تشغيل الخادم واختبار النظام:

شغل خادم التطوير الخاص بـ Django:

```bash
python manage.py runserver
```

ثم افتح متصفحك وانتقل إلى `http://localhost:8000/` لاختبار النظام والتأكد من أنه يعمل بشكل صحيح وأن المجلدات تُنشأ بالأسماء المطلوبة.
