To create a network device discovery system using SNMP, PySNMP, Django, Python, MySQL, HTML, and interactive and beautiful charts, follow these steps:

### 1. **Set Up Your Django Project**

**Create a Django project:**

```bash
django-admin startproject device_discovery
cd device_discovery
```

**Create a Django app:**

```bash
python manage.py startapp devices
```

### 2. **Install Required Packages**

**Install PySNMP and MySQL Connector:**

```bash
pip install pysnmp mysql-connector-python
```

**Install Django Chart Libraries (e.g., Plotly or Chart.js):**

```bash
pip install plotly
```

### 3. **Configure Your Django App**

**Update `settings.py`:**

Add your `devices` app to `INSTALLED_APPS` and configure your database settings:

```python
INSTALLED_APPS = [
    ...
    'devices',
]

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.mysql',
        'NAME': 'your_database_name',
        'USER': 'your_database_user',
        'PASSWORD': 'your_database_password',
        'HOST': 'localhost',
        'PORT': '3306',
    }
}
```

**Create a model for storing device information in `devices/models.py`:**

```python
from django.db import models

class Device(models.Model):
    oid = models.CharField(max_length=255)
    value = models.TextField()

    def __str__(self):
        return f"{self.oid}: {self.value}"
```

**Run migrations to create the database schema:**

```bash
python manage.py makemigrations
python manage.py migrate
```

### 4. **Develop the SNMP Discovery Script**

**Create a management command in Django to run the SNMP discovery.**

Create a file `devices/management/commands/discover_devices.py`:

```python
from django.core.management.base import BaseCommand
from pysnmp.hlapi import *
from devices.models import Device

class Command(BaseCommand):
    help = 'Discover devices using SNMP'

    def handle(self, *args, **kwargs):
        target_ip = '192.168.1.1'  # Replace with your network IP
        community_string = 'public'
        iterator = nextCmd(
            SnmpEngine(),
            CommunityData(community_string),
            UdpTransportTarget((target_ip, 161)),
            ContextData(),
            ObjectType(ObjectIdentity('1.3.6.1.2.1.1.1.0')),  # Example OID for system description
            lexicographicMode=False
        )

        for (errorIndication, errorStatus, errorIndex, varBinds) in iterator:
            if errorIndication:
                self.stdout.write(self.style.ERROR(f'Error: {errorIndication}'))
                return
            elif errorStatus:
                self.stdout.write(self.style.ERROR(f'Error: {errorStatus.prettyPrint()} at {errorIndex}'))
                return
            else:
                for varBind in varBinds:
                    Device.objects.update_or_create(
                        oid=varBind[0].prettyPrint(),
                        defaults={'value': varBind[1].prettyPrint()}
                    )
        self.stdout.write(self.style.SUCCESS('Devices discovered successfully'))
```

Run the command:

```bash
python manage.py discover_devices
```

### 5. **Create Views and Templates**

**Create a view to display the data in `devices/views.py`:**

```python
from django.shortcuts import render
from .models import Device

def device_list(request):
    devices = Device.objects.all()
    return render(request, 'devices/device_list.html', {'devices': devices})
```

**Configure the URL for the view in `devices/urls.py`:**

```python
from django.urls import path
from . import views

urlpatterns = [
    path('', views.device_list, name='device_list'),
]
```

**Include this URL configuration in your main `urls.py`:**

```python
from django.contrib import admin
from django.urls import path, include

urlpatterns = [
    path('admin/', admin.site.urls),
    path('devices/', include('devices.urls')),
]
```

**Create a template to display the data and charts in `devices/templates/devices/device_list.html`:**

```html
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Network Devices</title>
    <script src="https://cdn.plot.ly/plotly-latest.min.js"></script>
</head>
<body>
    <h1>Network Devices</h1>
    <table border="1">
        <tr>
            <th>OID</th>
            <th>Value</th>
        </tr>
        {% for device in devices %}
        <tr>
            <td>{{ device.oid }}</td>
            <td>{{ device.value }}</td>
        </tr>
        {% endfor %}
    </table>

    <!-- Interactive Chart -->
    <div id="chart"></div>
    <script>
        var devices = {{ devices|json_script:"devices-data" }};
        var oids = devices.map(device => device.oid);
        var values = devices.map(device => device.value);

        var trace1 = {
            x: oids,
            y: values,
            type: 'bar'
        };

        var data = [trace1];

        Plotly.newPlot('chart', data);
    </script>
</body>
</html>
```

### Summary

1. **Set up Django and configure the database.**
2. **Create a model to store SNMP discovery results.**
3. **Develop a management command for SNMP discovery.**
4. **Create views and templates to display the results with interactive charts.**

This setup will enable you to discover network devices using SNMP, store the results in a MySQL database, and visualize them with interactive charts using Django. Adjust the SNMP parameters, database settings, and chart configuration as needed for your specific requirements.
